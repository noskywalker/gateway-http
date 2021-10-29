package com.baidu.fbu.mtp.common.retry;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Retry builder can build Retryer with retry policy and condition.
 * <pre>
 * Retryer retryer = new RetryerBuilder()
 * 	.retryTimes(3)
 * 	.retryInterval(10, TimeUnit.SECONDS)
 * 	.retryIfThrow(expectedException())
 * 	.retryIfReturn(Predicates.alwaysFalse())
 * 	.build();
 * </pre>
 *
 * <pre>
 * try {
 *     Boolean isSuccess = retryer.callWithRetry(new Callable<Boolean>() {
 *         public Boolean call() throws Exception {
 *             return process(param);
 *         }
 *     });
 * } catch (RetryException e) {
 *     e.printStackTrace();
 * } catch (ExecutionException e) {
 *     e.printStackTrace();
 * }
 * <pre/>
 *
 * Retry condition:
 * <pre>
 * private static Predicate<Exception> expectedException() {
 * 	return new Predicate<Exception>() {
 * 		public boolean apply(Exception exception) {
 * 			if (exception instanceof TimeoutException) {
 * 				return exception.getMessage().startsWith("Retryable");
 * 			}
 *		}
 * 	}
 * }
 * </pre>
 *
 * This will retry whenever the result of the Callable is false,
 * or an expectedException is thrown from the call() method.
 * It will stop after attempting to retry 3 times whose interval is 10 seconds
 * and throw a RetryException that contains information about the last failed attempt.
 * If any other Exception pops out of the call() method
 * it's wrapped and rethrown in an ExecutionException.
 */
public class RetryerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(RetryerBuilder.class);

    private static final Duration DEFAULT_INTERVAL = new Duration(0, TimeUnit.SECONDS);
    private static final int DEFAULT_RETRY_TIMES = 3;

    // default Predicate is false
    private Predicate<Exception> throwCondition = Predicates.alwaysFalse();
    private Predicate<Object> returnCondition = Predicates.alwaysFalse();

    private Duration interval = DEFAULT_INTERVAL;
    private int times = DEFAULT_RETRY_TIMES;

    public Retryer build() {
        return new RetryerImpl(this);
    }

    public RetryerBuilder retryTimes(int times) {
        checkArgument(times >=0, "times '" + times + "'");
        this.times = times;
        return this;
    }

    public RetryerBuilder retryInterval(int duration, TimeUnit timeUnit) {
        checkArgument(duration >= 0, "duration '" + duration + "'");
        checkNotNull(timeUnit, "timeUnit");
        this.interval = new Duration(duration, timeUnit);
        return this;
    }

    /**
     * You should call methods related throwing exception only ONCE.
     * Or it will override the previous calling, that is only the last call is effective.
     * @param throwCondition predicate for throw condition
     * @return retry builder
     */
    public RetryerBuilder retryIfThrow(Predicate<Exception> throwCondition) {
        checkNotNull(throwCondition, "throwCondition");
        this.throwCondition = throwCondition;
        return this;
    }

    public RetryerBuilder retryIfException() {
        this.throwCondition = new Predicate<Exception>() {
            @Override
            public boolean apply(Exception e) {
                return true;
            }
        };
        return this;
    }

    public RetryerBuilder retryIfRuntimeException() {
        this.throwCondition = new Predicate<Exception>() {
            @Override
            public boolean apply(Exception e) {
                return e instanceof RuntimeException;
            }
        };
        return this;
    }

    public <T> RetryerBuilder retryIfExceptionOfType(final Class<T> type) {
        checkNotNull(type, "throwCondition");
        this.throwCondition = new Predicate<Exception>() {
            @Override
            public boolean apply(Exception e) {
                return e.getClass() == type;
            }
        };
        return this;
    }

    /**
     * If you want to care more than one specified exceptions, please use this method.
     * @param types exception types you want to care
     * @return retry builder
     */
    public RetryerBuilder retryIfExceptionOfType(final Class<?>[] types) {
        checkArgument(types != null && types.length >= 0, "throwCondition");
        this.throwCondition = new Predicate<Exception>() {
            @Override
            public boolean apply(Exception e) {
                for (Class<?> type : types){
                    if (e.getClass() == type) {
                        return true;
                    }
                }
                return false;
            }
        };
        return this;
    }

    /**
     * You should call methods related return values only ONCE.
     * Or it will override the previous calling, that is only the last call is effective.
     * @param returnCondition predicate for return values
     * @return retry builder
     */
    public RetryerBuilder retryIfReturn(Predicate<Object> returnCondition) {
        checkNotNull(throwCondition, "returnCondition");
        this.returnCondition = returnCondition;
        return this;
    }

    private static final class RetryerImpl implements Retryer {
        private final Duration interval;
        private final Predicate<Exception> throwCondition;
        private final Predicate<Object> returnCondition;
        private final int times;

        /**
         * Prohibits instantiation.
         */
        private RetryerImpl(RetryerBuilder retryerBuilder) {
            this.times = retryerBuilder.times;
            this.interval = retryerBuilder.interval;
            this.throwCondition = retryerBuilder.throwCondition;
            this.returnCondition = retryerBuilder.returnCondition;
        }

        @Override
        public final <T> T callWithRetry(Callable<T> task) throws Exception {
            return new RetryableTask<T>(task).call();
        }

        @Override
        public final <T> T newProxy(final T target, Class<T> interfaceType) {
            checkNotNull(target, "target");
            checkNotNull(interfaceType, "interfaceType");
            return newProxy(interfaceType, new RetryableInvocationHandler(target));
        }

        private static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
            Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[]{interfaceType}, handler);
            return interfaceType.cast(object);
        }

        private final class RetryableTask<T> implements Callable<T> {
            private final Callable<T> callable;
            private int left;

            public RetryableTask(Callable<T> callable) {
                this.callable = callable;
                this.left = times;
            }

            @Override
            public T call() throws Exception {
                try {
                    T returnObject = callable.call();
                    if (!exhausted() && returnCondition.apply(returnObject)) {
                        return retry();
                    }
                    return returnObject;
                } catch (Exception exception) {
                    if (!exhausted() && throwCondition.apply(exception)) {
                        return retry();
                    }
                    if (exhausted()) {
                        throw new RetryException(times, exception);
                    }
                    throw new ExecutionException(exception);
                }
            }

            private T retry() throws Exception {
                logger.info("now retry, retry time(s) left: {}", left);
                interval.sleep();
                left--;
                return call();
            }

            private boolean exhausted() {
                return left == 0;
            }
        }

        private final class RetryableInvocationHandler implements InvocationHandler {
            private final Object target;

            public RetryableInvocationHandler(Object target) {
                this.target = target;
            }

            @Override
            public Object invoke(final Object obj, final Method method, final Object[] args) throws Throwable {
                Callable<Object> task = new Callable<Object>() {
                    public Object call() throws Exception {
                        try {
                            return method.invoke(target, args);
                        } catch (InvocationTargetException e) {
                            throw  Throwables.propagate(e);
                        }
                    }
                };
                return callWithRetry(task);
            }
        }
    }

    private final static class Duration {
        private final long duration;
        private final TimeUnit timeUnit;

        private Duration(long duration, TimeUnit timeUnit) {
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        private void sleep() throws InterruptedException {
            if (duration > 0) {
                timeUnit.sleep(duration);
            }
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(duration)
                    .append(" ")
                    .append(timeUnit)
                    .toString();
        }
    }
}
