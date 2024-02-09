# Client-Server 
Client-Server software architecture using java

<b>Compile</b><br>
`javac -d build/classes src/pubsub/*.java`

<b>Run</b><br>
<b>Server</b> --> `java -cp build/classes pubsub.PubSub` or `java -cp build/classes pubsub.PubSub <port> <backlog> <serverip>` <br>
eg: java -cp build/classes pubsub.PubSub 8080 0 localhost

<b>Client</b> --> `java -cp build/classes pubsub.Client <serverip> <port> <username>` <br>
eg: java -cp build/classes pubsub.Client localhost 8080 John
