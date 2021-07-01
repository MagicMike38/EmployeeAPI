package com.mycompany.services;

import com.mycompany.model.Employee;
import com.mycompany.serializers.EmployeeDeserializer;
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

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmployeeKafkaService implements AbstractKakfaService<Employee>{

    private final EmployeeService employeeService;
    private final Properties producerProps;
    private final Properties consumerProps;

    public EmployeeKafkaService(String producerConfig, String consumerConfig) throws IOException {
        FileReader producerConfigFile = new FileReader("src/main/resources/"+producerConfig);
        FileReader consumerConfigFile = new FileReader("src/main/resources/"+consumerConfig);

        producerProps = new Properties();
        consumerProps = new Properties();

        producerProps.load(producerConfigFile);
        consumerProps.load(consumerConfigFile);

        employeeService = new EmployeeService();
    }

    public boolean publish(Employee employee) {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProps.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        props.put(ProducerConfig.ACKS_CONFIG, producerProps.getProperty("ACKS_CONFIG"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProps.get("BATCH_SIZE_CONFIG"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerProps.get("BUFFER_MEMORY_CONFIG"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());

        Producer<Integer, Employee> producer = new KafkaProducer<>(props);

        producer.send(new ProducerRecord<>(producerProps.getProperty("TOPIC_NAME"), employee.getId(), employee));
        producer.close();

        return true;
    }

    public boolean consume() {

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProps.getProperty("BOOTSTRAP_SERVERS_CONFIG"));
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerProps.getProperty("AUTO_OFFSET_RESET_CONFIG"));
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerProps.getProperty("GROUP_ID_CONFIG"));

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class.getName());

        KafkaConsumer<Integer, Employee> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(consumerProps.getProperty("TOPIC_NAME")));

        ConsumerRecords<Integer, Employee> records = consumer.poll(Duration.ofMillis(100));
        System.out.println("Records: "+records);
        for (ConsumerRecord<Integer, Employee> record : records) {
            try{
            employeeService.createEmployee(record.value());}
            catch (Exception ex){
                System.out.println("Exception occurred" + ex.getMessage());
            }
        }
        consumer.close();

        return true;
    }
}
