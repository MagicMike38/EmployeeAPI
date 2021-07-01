package com.mycompany.services;

import com.mycompany.dao.Employee;
import com.mycompany.serializers.EmployeeSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmployeeKafkaService {

    public EmployeeKafkaService() {

    }

    public boolean publish(Employee employee) {

        System.out.println("Publishing...");
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());

        Producer<Integer, Employee> producer = new KafkaProducer<>(props);

        producer.send(new ProducerRecord<>("TestTopic", employee.getId(), employee));
        System.out.println("Publishing done...");
        producer.close();

        return true;
    }

    public boolean consume(Employee employee) {
        String bootstrapServers = "localhost:9092";
        String topic = "TestTopic";
        System.out.println("Consuming...");
        String grp_id = "app";
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,grp_id);

        KafkaConsumer<Integer, Employee> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));

        ConsumerRecords<Integer, Employee> records = consumer.poll(Duration.ofMillis(100));
        System.out.println("Records: "+records);
        for (ConsumerRecord<Integer, Employee> record : records) {
            System.out.println("Key: " + record.key() + ", Value:" + record.value());
            System.out.println("Partition:" + record.partition() + ",Offset:" + record.offset());
        }
        System.out.println("Consuming done...");
        return true;
    }
}
