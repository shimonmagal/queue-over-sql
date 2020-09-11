public enum Operations {
    CREATE("CREATE TABLE IF NOT EXISTS $QUEUE"),
    PUBLISH("INSERT INTO $QUEUE values(?,?)"),
    CONSUME("");

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
