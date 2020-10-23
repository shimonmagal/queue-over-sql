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

    PUBLISH("INSERT INTO $QUEUE (id, message, publish_time) values(?,?,?)"),
    DELETE("DELETE FROM $QUEUE where id=?"),
    MARK_BEFORE_CONSUME("UPDATE $QUEUE SET consumer_id=?, consumer_round=?, "),
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
