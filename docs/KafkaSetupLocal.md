Steps to setup Kafka locally:

Setup Zookeeper
`.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties`

Setup Kafka
`.\bin\windows\kafka-server-start.bat .\config\server.properties`

Setup Kafka Producer
`.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic TestTopic`

Setup Kafka Consumer
`.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic TestTopic --from-beginning`
