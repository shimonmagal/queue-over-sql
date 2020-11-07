package com.queueoversql.tests;

import com.queueoversql.QueueOverSql;
import com.queueoversql.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.util.List;
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
            Assertions.fail();
        }

        Long id = qos.publishTask(queueName, "{task: handleEverything}");

        if (id == null)
        {
            Assertions.fail();
        }

        result = qos.deleteTask(queueName, id);

        Assertions.assertTrue(result);
    }

    @Test
    public void testCreateInsertConsume()
    {
        final String queueName = "testQueue";

        QueueOverSql qos = new QueueOverSql(jdbcUrl, 30, TimeUnit.MINUTES, 20, TimeUnit.SECONDS);

        boolean result = qos.createQueue(queueName);

        if (!result)
        {
            Assertions.fail();
        }

        for (int i = 0 ; i < 10; i++)
        {
            Long id = qos.publishTask(queueName, "{task: handleEverything}");

            if (id == null)
            {
                Assertions.fail();
            }
        }

        List<Task> tasks = qos.consume(queueName, 3);

        Assertions.assertEquals(3, tasks.size());

        tasks = qos.consume(queueName, 25);

        Assertions.assertEquals(7, tasks.size());
    }

    @Test
    public void testCreateInsertConsumeAndDelete()
    {
        final String queueName = "testQueue";

        QueueOverSql qos = new QueueOverSql(jdbcUrl, 30, TimeUnit.MINUTES, 20, TimeUnit.SECONDS);

        boolean result = qos.createQueue(queueName);

        if (!result)
        {
            Assertions.fail();
        }

        for (int i = 0 ; i < 10; i++)
        {
            Long id = qos.publishTask(queueName, "{task: handleEverything}");

            if (id == null)
            {
                Assertions.fail();
            }
        }

        List<Task> tasks = qos.consume(queueName, 3);

        Assertions.assertEquals(3, tasks.size());

        List<Task> moreTasks = qos.consume(queueName, 25);

        Assertions.assertEquals(7, tasks.size());

        for (Task task : tasks)
        {
            if (!qos.deleteTask(queueName, task.id))
            {
                Assertions.fail();
            }
        }
    }
}
