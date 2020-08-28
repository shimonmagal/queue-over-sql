public enum Operations {
    CREATE("CREATE TABLE IF NOT EXISTS $QUEUE");

    private final String sqlTemplate;

    private Operations(String sqlTeamplate)
    {
        this.sqlTemplate = sqlTeamplate;
    }

    String bindQueueName(String queueName)
    {
        return sqlTemplate.replace("$QUEUE", queueName);
    }
}
