package com.baidu.fbu.mtp.common;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created on 18:50 12/18/2015.
 *
 * @author skywalker
 */
public class Timing {

    public static long timing(Stopwatch sw) {
        return sw.elapsed(TimeUnit.MILLISECONDS);
    }

}
