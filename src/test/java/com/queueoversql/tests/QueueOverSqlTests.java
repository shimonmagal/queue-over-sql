package com.queueoversql.tests;

import com.queueoversql.QueueOverSql;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.concurrent.TimeUnit;

public class QueueOverSqlTests {
    private final static String jdbcUrl = "jdbc:h2:mem:queueOverSqlTest;DB_CLOSE_DELAY=-1";

    @Test
    public void testCreateQueue()
    {
        QueueOverSql qos = new QueueOverSql(jdbcUrl, 30, TimeUnit.MINUTES, 20, TimeUnit.SECONDS);

        boolean result = qos.createQueue("testQueue");

        Assertions.assertTrue(result);
    }

    @Test
    public void testCreateAndInsertAndDelete()
    {
        final String queueName = "testQueue";

        QueueOverSql qos = new QueueOverSql(jdbcUrl, 30, TimeUnit.MINUTES, 20, TimeUnit.SECONDS);

        boolean result = qos.createQueue(queueName);

        if (!result)
        {
            Assertions.assertTrue(false);
        }

        Long id = qos.publishTask(queueName, "{task: handleEverything}");

        if (id == null)
        {
            Assertions.assertTrue(false);
        }

        result = qos.deleteTask(queueName, id);

        Assertions.assertTrue(result);
    }

}
