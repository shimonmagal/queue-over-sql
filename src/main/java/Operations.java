public enum Operations {
    CREATE("CREATE TABLE IF NOT EXISTS $QUEUE(" +
            "id long NOT NULL," +
            "message VARCHAR(10000)," +
            "consumer_id VARCHAR(128)," +
            "publish_time long," +
            "consume_time long," +
            "ttl long)"),
    PUBLISH("INSERT INTO $QUEUE values(?,?)"),
    DELETE("DELETE FROM $QUEUE where messageId=?"),
    CONSUME(""),
    UNASSIGN_TIMEDOUT("");

    private final String sqlTemplate;

    Operations(String sqlTeamplate)
    {
        this.sqlTemplate = sqlTeamplate;
    }

    String bindQueueName(String queueName)
    {
        return sqlTemplate.replace("$QUEUE", queueName);
    }
}
