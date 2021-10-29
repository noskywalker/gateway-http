package com.baidu.fbu.mtp.common;

import org.junit.Before;
import org.junit.Test;

public class SleepyTaskTest {
    
    private SleepyTask task;
    
    @Before
    public void setUp() {
        task = new SleepyTask() {
            
            @Override
            protected void runTask() {
                System.out.println("runTask...");
            }
        };
    }
    
    @Test
    public void testRun() {
        task.run();
    }
    
    @Test
    public void testWakeup() {
        task.wakeup();
    }
}
