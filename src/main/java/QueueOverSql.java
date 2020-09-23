import java.util.concurrent.TimeUnit;

public class QueueOverSql {
    private final String jdbcUrl;
    private final long messageTimeoutMillis;
    private final long backgroundThreadInterval;

    public QueueOverSql(String jdbcUrl, long messageTimeout, TimeUnit messageTimeoutUnit,
                        long backgroundThreadInterval, TimeUnit backgroundThreadIntervalUnit)
    {
        this.jdbcUrl = jdbcUrl;
        this.messageTimeoutMillis = TimeUnit.MILLISECONDS.convert(messageTimeout, messageTimeoutUnit);
        this.backgroundThreadInterval = TimeUnit.MILLISECONDS.convert(backgroundThreadInterval, backgroundThreadIntervalUnit);
    }

    public void createQueue(String queueName)
    {
        execute(Operations.CREATE.bindQueueName(queueName));
    }

    public String publishTask(String queueName, String message)
    {
        execute(Operations.PUBLISH.bindQueueName(queueName));

        String id = null; // get id
        return id;
    }

    public String deleteTask(String queueName, String messageId)
    {
        execute(Operations.DELETE.bindQueueName(queueName));

        String id = null; // get id
        return id;
    }

    private void execute(String sql)
    {
        
    }
}
