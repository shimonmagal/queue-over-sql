package com.queueoversql;

public enum Operations {
    CREATE("CREATE TABLE IF NOT EXISTS $QUEUE(" +
            "id long NOT NULL," +
            "message VARCHAR(10000)," +
            "consumer_id VARCHAR(128)," +
            "consumer_round long," +
            "publish_time long," +
            "consume_time long," +
            "ttl long," +
            "PRIMARY KEY (id))"),
    INDEX("CREATE INDEX IF NOT EXISTS $FIELD_NAME_idx ON $QUEUE($FIELD_NAME)"),
    PUBLISH("INSERT INTO $QUEUE (id, message, publish_time) values(?,?,?)"),
    DELETE("DELETE FROM $QUEUE where id=?"),
    MARK_BEFORE_CONSUME("UPDATE $QUEUE SET consumer_id=?, consumer_round=? WHERE id=(" +
            "select id from (" +
            "   select id from (" +
            "       select * from ORDER BY publish_time DESC" +
            "   )" +
            "   WHERE consume_time is NULL OR consume_time < ? OR ttl < ?)" +
            "limit ?)"),
    CONSUME("SELECT FROM $QUEUE WHERE consumer_id=? AND consumer_round=?"),
    UNASSIGN_TIMEDOUT("");

    private final String sqlTemplate;

    public static String[] fieldsForIndexing = {"id", "consumer_id", "consumer_round", "consume_time", "publish_time", "ttl"};
    public static final String ID_FIELD = "id";
    public static final String MESSAGE_FIELD = "message";

    Operations(String sqlTeamplate)
    {
        this.sqlTemplate = sqlTeamplate;
    }

    String bindQueueName(String queueName)
    {
        return sqlTemplate.replace("$QUEUE", queueName);
    }

    String bindQueueAndFieldName(String queueName, String fieldName)
    {
        String sqlTemplateWithQueueName = bindQueueName(queueName);

        return sqlTemplateWithQueueName.replace("$FIELD_NAME", fieldName);
    }
}
