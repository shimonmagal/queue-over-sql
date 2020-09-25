public enum Operations {
    CREATE("CREATE TABLE IF NOT EXISTS $QUEUE"),
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
