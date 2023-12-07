package com.bigking.springcloud.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

class IdleConnectionMonitorThread extends Thread {

    private static final Log LOG = LogFactory.getLog(IdleConnectionMonitorThread.class);
    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        super();
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            LOG.error("IdleConnectionMonitorThread run exception", ex);
            Thread.currentThread().interrupt();
        }
    }
}
