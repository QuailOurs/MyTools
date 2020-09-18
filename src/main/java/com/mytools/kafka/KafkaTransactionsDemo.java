package com.mytools.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaTransactionsDemo {

    static final Logger LOG = LoggerFactory.getLogger(KafkaTransactionsDemo.class);

    static  Properties _producerConfigProps = new Properties();

    static Properties _consumerConfigProps = new Properties();

    static Map<TopicPartition, OffsetAndMetadata> _offsets = new HashMap<>();

    public static void updateOffset(TopicPartition topicPartition,OffsetAndMetadata offsetAndMetadata){
        OffsetAndMetadata meta = _offsets.get(topicPartition);
        if(meta != null){
            if(offsetAndMetadata.offset() > meta.offset()){
                _offsets.put(topicPartition,offsetAndMetadata);
            }
        }else {
            _offsets.put(topicPartition,offsetAndMetadata);
        }
    }

    static {
        _producerConfigProps.put("bootstrap.servers", KafkaProperties.BOOTSTRAP_ERVERS);//required
        _producerConfigProps.put("client.id", KafkaProperties.CLIENT_ID_PRODUCER);
        _producerConfigProps.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");//required
        _producerConfigProps.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");//required
        _producerConfigProps.put("batch.size","16384");//default 16384 | 16K
        _producerConfigProps.put("acks", "-1"); //default 1
        _producerConfigProps.put("buffer.memory", "33554432");// 32M
        _producerConfigProps.put("metadata.max.age.ms", "300000");// default 300000 | 5min
        _producerConfigProps.put("max.block.ms", "60000");// default 60000
        _producerConfigProps.put("request.timeout.ms", "30000");// default 30000
        _producerConfigProps.put("transactional.id", KafkaProperties.TRANSACTION_ID);

        _consumerConfigProps.put("bootstrap.servers", KafkaProperties.BOOTSTRAP_ERVERS); //required
        _consumerConfigProps.put("group.id", KafkaProperties.GROUP_ID);
        _consumerConfigProps.put("client.id", KafkaProperties.CLIENT_ID_CONSUMER);
        _consumerConfigProps.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        _consumerConfigProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        _consumerConfigProps.put("max.poll.interval.ms", "300000");//default 300000
        _consumerConfigProps.put("max.poll.records", "500");//default 500
        _consumerConfigProps.put("enable.auto.commit", "false");
        _consumerConfigProps.put("fetch.max.bytes", "4194304");
    }

    public static void main(String args[]) {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(_consumerConfigProps);
        // for test
        consumer.assign(Arrays.asList(new TopicPartition(KafkaProperties.TOPIC_NAME,0)));
        // Note that the ‘transactional.id’ configuration _must_ be specified in the
        // producer config in order to use transactions.
        KafkaProducer<String, String> producer = new KafkaProducer<>(_producerConfigProps);

        // We need to initialize transactions once per producer instance. To use transactions,
        // it is assumed that the application id is specified in the config with the key
        // transactional.id.
        //
        // This method will recover or abort transactions initiated by previous instances of a
        // producer with the same app id. Any other transactional messages will report an error
        // if initialization was not performed.
        //
        // The response indicates success or failure. Some failures are irrecoverable and will
        // require a new producer  instance. See the documentation for TransactionMetadata for a
        // list of error codes.

        producer.initTransactions();
        int times = 3;
        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(KafkaProperties.CONSUMER_POLL_TIMEOUT);
            if (!records.isEmpty()) {

                // Start a new transaction. This will begin the process of batching the consumed
                // records as well
                // as an records produced as a result of processing the input records.
                //
                // We need to check the response to make sure that this producer is able to initiate
                // a new transaction.
                producer.beginTransaction();

                // Process the input records and send them to the output topic(s).
                List<ProducerRecord<String, String>> outputRecords = processRecords(records);
                for (ProducerRecord<String, String> outputRecord : outputRecords) {
                    producer.send(outputRecord);
                }

                // To ensure that the consumed and produced messages are batched, we need to commit
                // the offsets through the producer and not the consumer.
                //
                // If this returns an error, we should abort the transaction.
                try {
                    producer.sendOffsetsToTransaction(_offsets, KafkaProperties.GROUP_ID);
                    if(-- times == 0)
                        throw new IllegalStateException();
                    // Now that we have consumed, processed, and produced a batch of messages, let's
                    // commit the results.
                    // If this does not report success, then the transaction will be rolled back.
                    producer.commitTransaction();
                }catch (Exception e){
                    producer.abortTransaction();
                    LOG.warn("exception occurred,希望重启后从"
                            + (_offsets.get(new TopicPartition(KafkaProperties.TOPIC_NAME,0) ).offset() - records.count() )
                            + "开始消费");
                    consumer.close();
                    producer.close();
                    System.exit(17);
                }
            }
        }
    }

    public static List<ProducerRecord<String, String>> processRecords(ConsumerRecords<String, String> records){
        List<ProducerRecord<String, String>> res = new ArrayList<>();
        for (ConsumerRecord<String,String> record : records) {
            info(record);
            res.add(new ProducerRecord<String, String>(KafkaProperties.RES_TOPIC_NAME,record.offset() + ""));
            updateOffset(new TopicPartition(record.topic(),record.partition()),new OffsetAndMetadata(record.offset()));
        }
        return res;
    }
    public static void info(ConsumerRecord<String,String> record) {
        LOG.info("topic:{},partition:{},offset:{},key:{},value:{}",
                record.topic(),record.partition(),record.offset(),record.key(),record.value());
    }
}
