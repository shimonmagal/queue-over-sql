package com.queueoversql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QueueOverSql {
    private static final Logger logger = LoggerFactory.getLogger(QueueOverSql.class);

    private final String jdbcUrl;
    private final long messageTimeoutMillis;
    private final long backgroundThreadInterval;
    private final String uniqueInstanceIdentifier;

    public QueueOverSql(String jdbcUrl, long messageTimeout, TimeUnit messageTimeoutUnit,
                        long backgroundThreadInterval, TimeUnit backgroundThreadIntervalUnit)
    {
        this.jdbcUrl = jdbcUrl;
        this.messageTimeoutMillis = TimeUnit.MILLISECONDS.convert(messageTimeout, messageTimeoutUnit);
        this.backgroundThreadInterval = TimeUnit.MILLISECONDS.convert(backgroundThreadInterval, backgroundThreadIntervalUnit);

        this.uniqueInstanceIdentifier = UUID.randomUUID().toString();
    }

    public boolean createQueue(String queueName)
    {
        return executeWithParams(Operations.CREATE.bindQueueName(queueName));
    }

    public Long publishTask(String queueName, String message)
    {
        long messageId = UUID.randomUUID().getLeastSignificantBits();

        boolean result = executeWithParams(Operations.PUBLISH.bindQueueName(queueName), messageId, message);

        if (!result)
        {
            return null;
        }

        return messageId;
    }

    public boolean deleteTask(String queueName, long messageId)
    {
        executeWithParams(Operations.DELETE.bindQueueName(queueName));
        //return success
        return false;
    }

    private boolean executeWithParams(String sql, Object... params)
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

                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL operation", e);
            return false;
        }

        return true;
    }
}
