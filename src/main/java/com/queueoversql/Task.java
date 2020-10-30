package com.queueoversql;

public class Task
{
    public final long id;
    public final String messageBody;

    Task(long id, String messageBody)
    {
        this.id = id;
        this.messageBody = messageBody;
    }
}
