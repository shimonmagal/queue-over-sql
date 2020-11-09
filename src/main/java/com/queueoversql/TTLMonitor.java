package com.queueoversql;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TTLMonitor
{
    private final long ttlTimeout;
    private final QueueOverSql qos;

    TTLMonitor(long ttlTimeout, QueueOverSql qos)
    {
        this.ttlTimeout = ttlTimeout;
        this.qos = qos;
    }

    void init()
    {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                qos.ttl();
            }
        }, 0l, ttlTimeout, TimeUnit.MILLISECONDS);
    }
}
