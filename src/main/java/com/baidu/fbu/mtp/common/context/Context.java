package com.baidu.fbu.mtp.common.context;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * Created on 07/14/2015.
 *
 * @author skywalker
 */
public class Context {
    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    private static final ThreadLocal<Context> CURRENT_CONTEXT = new ThreadLocal<>();

    /**
     * className => instance
     */
    private final Map<String, Object> cache = Maps.newHashMap();

    // Suppresses default constructor, ensuring non-instantiability.
    private Context() {
    }

    /**
     * @return 当前线程绑定的实例，如果尚未创建, 创建之
     */
    public static Context getCurrent() {
        return CURRENT_CONTEXT.get();
        // Context context = CURRENT_CONTEXT.get();
        // if (context == null) {
        //    CURRENT_CONTEXT.set(context = new Context());
        // }
        // return context;
    }

    public static Context getCurrent(boolean createIfAbsent) {
        Context context = CURRENT_CONTEXT.get();
        if (context == null && createIfAbsent) {
            CURRENT_CONTEXT.set(context = new Context());
        }
        return context;
    }

    public static <T> T getFromCurrent(Class<T> type) {
        return getFromCurrent(type, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromCurrent(Class<T> type, boolean required) {
        return (T) getFromCurrent(type.getName(), required);
    }

    public static Object getFromCurrent(String className) {
        return getFromCurrent(className, true);
    }

    public static Object getFromCurrent(String className, boolean required) {
        Context context = getCurrent();
        Object object = context != null ? context.get(className) : null;
        if (object == null) {
            if (required) {
                throw new RuntimeException("no '" + className + "' in current context");
            }
            return null;
        }
        return object;
    }

    public static void removeCurrent() {
        Context context = CURRENT_CONTEXT.get();
        if (context != null) {
            context.clear();
            CURRENT_CONTEXT.remove();
        }
    }

    public Object get(String className) {
        return cache.get(className);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        Object object = get(type.getName());
        return object == null ? null : (T) object;
    }

    public Object set(String className, Object object) {
        return cache.put(className, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T set(Class<T> type, T object) {
        return (T) set(type.getName(), object);
    }

    public Object remove(String className) {
        Object removed = cache.remove(className);
        if (removed != null && removed instanceof Closeable) {
            try {
                ((Closeable) removed).close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(Class<T> type) {
        Object removed = remove(type.getName());
        return removed == null ? null : (T) removed;
    }

    public void clear() {
        for (Map.Entry<String, Object> entry : cache.entrySet()) {
            if (entry.getValue() instanceof Closeable) {
                try {
                    ((Closeable) entry.getValue()).close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        cache.clear();
    }

    public void close() {
        clear();
        CURRENT_CONTEXT.set(null);
    }
}
