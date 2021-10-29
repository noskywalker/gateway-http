package com.baidu.fbu.mtp.common;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 00:13 08/24/2015.
 *
 * @author skywalker
 */
public abstract class SleepyTask implements Runnable {

    private final AtomicBoolean should = new AtomicBoolean();
    private final AtomicBoolean running = new AtomicBoolean(false);

    protected final Executor exector;

    public SleepyTask() {
        this(new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
    }

    public SleepyTask(Executor exector) {
        this.exector = exector;
    }

    @Override
    public final void run() {
        try {
            while (should.compareAndSet(true, false)) {
                try {
                    runTask();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            running.set(false);
        }
    }

    public boolean wakeup() {
        should.set(true);
        if (running.compareAndSet(false, true)) {
            exector.execute(this);
            return true;
        }
        return false;
    }

    protected abstract void runTask();
}

