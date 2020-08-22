public class QueueOverSql {
    private final String jdbcUrl;
    private final long messageTimeoutMillis;
    private final long backgroundThreadInterval;

    public QueueOverSql(String jdbcUrl, long messageTimeoutMillis, long backgroundThreadInterval)
    {
        this.jdbcUrl = jdbcUrl;
        this.messageTimeoutMillis = messageTimeoutMillis;
        this.backgroundThreadInterval = backgroundThreadInterval;
    }
}
