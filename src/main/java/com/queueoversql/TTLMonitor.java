package com.queueoversql;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TTLMonitor
{
    private final long ttlTimeout;
    private final QueueOverSql qos;
    private final ScheduledExecutorService executor;

    TTLMonitor(long ttlTimeout, QueueOverSql qos)
    {
        this.ttlTimeout = ttlTimeout;
        this.qos = qos;

        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    void init()
    {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                qos.ttl();
            }
        }, 0l, ttlTimeout, TimeUnit.MILLISECONDS);
    }

    void desroty()
    {
        executor.shutdownNow();
    }
}
