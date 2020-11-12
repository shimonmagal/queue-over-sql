package com.queueoversql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class QueueOverSql {
    private static final Logger logger = LoggerFactory.getLogger(QueueOverSql.class);

    private final String jdbcUrl;
    private final long messageTimeoutMillis;
    private final long ttlTimeoutMillis;
    private final String uniqueConsumerId;
    private final TTLMonitor ttlMonitor;

    private final ConcurrentMap<String, Boolean> allQueues = new ConcurrentHashMap<>();

    private long consumerRound;

    public QueueOverSql(String jdbcUrl, long messageTimeout, TimeUnit messageTimeoutUnit,
                        long ttlTimeout, TimeUnit backgroundThreadIntervalUnit) {
        this.jdbcUrl = jdbcUrl;
        this.messageTimeoutMillis = TimeUnit.MILLISECONDS.convert(messageTimeout, messageTimeoutUnit);
        this.ttlTimeoutMillis = TimeUnit.MILLISECONDS.convert(ttlTimeout, backgroundThreadIntervalUnit);

        this.uniqueConsumerId = UUID.randomUUID().toString();
        this.consumerRound = 0l;

        this.ttlMonitor = new TTLMonitor(ttlTimeout, this);
    }

    public boolean createQueue(String queueName) {
        logger.debug("Creating queue {}", queueName);
        boolean result = executeWithParams(Operations.CREATE.bindQueueName(queueName));

        for (String field : Operations.fieldsForIndexing) {
            String sql = Operations.INDEX.bindQueueAndFieldName(queueName, field);

            boolean queueCreated = executeWithParams(sql);

            if (queueCreated)
            {
                allQueues.put(queueName, true);
            }

            result &= queueCreated;
        }

        return result;
    }

    public Long publishTask(String queueName, String message) {
        long messageId = UUID.randomUUID().getLeastSignificantBits();
        logger.debug("Publishing new task with id {} to queue {}", messageId, queueName);

        boolean result = executeWithParams(Operations.PUBLISH.bindQueueName(queueName), messageId, message, System.currentTimeMillis());

        if (!result) {
            return null;
        }

        return messageId;
    }

    public boolean deleteTask(String queueName, long messageId)
    {
        logger.debug("Deleting task with id {} from queue {}", messageId, queueName);

        return executeWithParams(Operations.DELETE.bindQueueName(queueName), messageId);
    }

    public List<Task> consume(String queueName, int count)
    {
        logger.debug("Consuming {} tasks from queue {}", count, queueName);

        String markBeforeConsumeSql = Operations.MARK_BEFORE_CONSUME.bindQueueName(queueName);
        consumerRound++;

        long now = System.currentTimeMillis();
        long lastMessageTimeout = now - messageTimeoutMillis;
        long lastTTL = now - ttlTimeoutMillis;

        if (!executeWithParams(markBeforeConsumeSql, uniqueConsumerId, consumerRound, now, lastMessageTimeout,
                lastTTL, count))
        {
            return null;
        }

        String consumeSql = Operations.CONSUME.bindQueueName(queueName);

        List<Task> tasks = new LinkedList<>();

        if (!executeWithParams(consumeSql, tasks, uniqueConsumerId, consumerRound))
        {
            return null;
        }

        logger.debug("Consumed {} tasks from queue {}", tasks.size(), queueName);

        return tasks;
    }

    public void ttl()
    {
        for (String queueName: allQueues.keySet())
        {
            String updateTTLSql = Operations.UPDATE_TTL.bindQueueName(queueName);

            long now = System.currentTimeMillis();

            executeWithParams(updateTTLSql, now, uniqueConsumerId);
        }
    }

    private boolean executeWithParams(String sql, Object... params)
    {
        return executeWithParams(sql, new LinkedList<>(), params);
    }

    private boolean executeWithParams(String sql, List<Task> tasks, Object... params)
    {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);)
        {
            try (PreparedStatement ps = connection.prepareStatement(sql))
            {
                for (int i = 0 ; i < params.length; i++)
                {
                    ps.setObject(i + 1, params[i]);
                }

                if (ps.execute())
                {
                    try (ResultSet rs = ps.getResultSet())
                    {
                        while (rs.next())
                        {
                            long messageId = rs.getLong(Operations.ID_FIELD);
                            String messageBody = rs.getString(Operations.MESSAGE_FIELD);

                            tasks.add(new Task(messageId, messageBody));
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL operation failed", e);
            return false;
        }

        return true;
    }
}
