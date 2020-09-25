import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QueueOverSql {
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

    public void createQueue(String queueName)
    {
        execute(Operations.CREATE.bindQueueName(queueName));
    }

    public long publishTask(String queueName, String message)
    {
        long messageId = UUID.randomUUID().getLeastSignificantBits();

        execute(Operations.PUBLISH.bindQueueName(queueName));

        return messageId;
    }

    public String deleteTask(String queueName, long messageId)
    {
        execute(Operations.DELETE.bindQueueName(queueName));
    }

    private void execute(String sql)
    {
        
    }
}
