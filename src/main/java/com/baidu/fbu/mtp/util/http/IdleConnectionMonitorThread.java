package com.baidu.fbu.mtp.util.http;

import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 15:56 12/01/2015.
 *
 * @author skywalker
 */
public class IdleConnectionMonitorThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread() {
        super();
    }

    public IdleConnectionMonitorThread(final HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (! shutdown) {
                synchronized (this) {
                    wait(5000);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections that have been idle longer than 30 sec
                    // connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException e) {
            logger.error("connection monitor interrupted", e);
        }
    }

    public void shutdown() {
        logger.info("Shutdown IdleConnectionMonitorThread...");
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public void setConnMgr(final HttpClientConnectionManager connMgr) {
        this.connMgr = connMgr;
    }
}
