# queue-over-sql
This projects implement a priority queue (like SQS, RabbitMQ and others) over sql.
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
