package com.queueoversql;

import java.util.concurrent.Executors;

public class TTLMonitor
{
    private final long ttlTimeout;

    TTLMonitor(long ttlTimeout)
    {
        this.ttlTimeout = ttlTimeout;
    }

    void init()
    {
        Executors.newScheduledThreadPool(1);
    }
}
