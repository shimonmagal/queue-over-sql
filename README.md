# queue-over-sql
This projects implement a persistent priority queue (or a worker queue) (like SQS, RabbitMQ and others) over sql.
Why? There are some cases where you can't install a queue, and all you have is an old fashioned sql server of some kind.

The api supports these operations:

CREATE(queue_name) - will create a new queue

PUBLISH(queue_name, message) - puts a new message in the given queue

CONSUME(queue_name, count) - gets {count} messages from queue, in FIFO order

DELETE(queue_name, messageId) - deletes message (this should usually be called after consumer finished handling task)

via the Java api you can also set a timeout, which means that if message wasn't handled in X seconds, it will be reappear again in the queue, allowing others to consume it

The solution supports multiple servers. If any of the servers dies, the messages that were being processes by that server, will reappear in the queue as well

Note: It is advised to create all the queues in advaced. Since each queue creation translates into a CREATE TABLE operation, and that might fail in some cases (when the db is unavailable), and we don't try to recreate the queue if it failed, upon PUBLISH - so it is best to initialize the queue service when you server starts, and if that fails, to terminate the server so that you don't begin to use QueueOverSql before successful initializtion.
That being said, you don't need to worry about failure if a table/queue already exists since we use a CREATE TABLE IF NOT EXISTS.

We support timed out messages - which means that if a message is being handled for too long time, it will be unassigned and made available in the queue again. The timeout is controllable from the ctor.

We support also timeout for a QueueOverSql instance that died an can no longer do keep-alive, to have all of its messages restored and made available back in the queue. This timeout is also controllable from the ctor by a different parameter.

TODO:
Support hikary or maybe generic DataSources being passed
Adjust to support other DBs rather than just h2 (should be really easy if anyone wants to adjust to other dbs)

## How it works?
For every queue is choose to create, there is a table formed.
The table has 7 columns - id, messageBody, consumer_id, consumer_round, consume_time, publish_time, ttl

id is just random and the messageBody is a string that is the task you wanted queue but just seralized into a string.
When task is pubslished we give it its publish_time as now.

When consume is invoked, it tries to fetch all the messages that either haven't been consumed or that timed out.
Timed out couldn't mean either their consumer died (ttl) or that the consumer is alive but a long time has passed.
Either way, we guarantee to always consume the oldest messages first.
Lastly, how do we consume messages? We need to both mark and select. Since most SQL dbs don't support both update and select, we mark all the messages we want to consume.
We give each such message the current consumer_round (which is an incremental number starting from 0) - so that we select exactly what we marked now and not get confused with previous markings done by this consumer in the past.

Lastly, the code does in fact support multiple servers or multi-threading.