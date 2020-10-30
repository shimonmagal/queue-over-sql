package com.queueoversql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QueueOverSql {
    private static final Logger logger = LoggerFactory.getLogger(QueueOverSql.class);

    private final String jdbcUrl;
    private final long messageTimeoutMillis;
    private final long backgroundThreadInterval;
    private final String uniqueInstanceIdentifier;
    private final long consumerRound;

    public QueueOverSql(String jdbcUrl, long messageTimeout, TimeUnit messageTimeoutUnit,
                        long backgroundThreadInterval, TimeUnit backgroundThreadIntervalUnit) {
        this.jdbcUrl = jdbcUrl;
        this.messageTimeoutMillis = TimeUnit.MILLISECONDS.convert(messageTimeout, messageTimeoutUnit);
        this.backgroundThreadInterval = TimeUnit.MILLISECONDS.convert(backgroundThreadInterval, backgroundThreadIntervalUnit);

        this.uniqueInstanceIdentifier = UUID.randomUUID().toString();
        this.consumerRound = 0l;
    }

    public boolean createQueue(String queueName) {
        logger.debug("Creating queue {}", queueName);
        boolean result = executeWithParams(Operations.CREATE.bindQueueName(queueName));

        for (String field : Operations.fieldsForIndexing) {
            String sql = Operations.INDEX.bindQueueAndFieldName(queueName, field);

            result &= executeWithParams(sql);
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

    public boolean deleteTask(String queueName, long messageId) {
        logger.debug("Deleting task with id {} from queue {}", messageId, queueName);

        return executeWithParams(Operations.DELETE.bindQueueName(queueName), messageId);
    }

    public List<Task> consume(String queueName, int count) {
        return null;
    }

    private boolean executeWithParams(String sql, Object... params)
    {
        executeWithParams(sql, new LinkedList<>(), params);
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
                            // tasks
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
