package com.mytools.kafka.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mytools.kafka.KafkaProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * 注意：
 * 1.KafkaConsumer不是线程安全，一个线程只能独享一个KafkaConsumer
 * 2.千万不要频繁创建KafkaConsumer
 * 3.KafkaConsumer使用完后需要调用close方法关闭
 */
public class Consumer10Demo {

    static final Logger LOG = LoggerFactory.getLogger(Consumer10Demo.class);


    final Properties _props = new Properties();

    final String _name;
    //not thread-safe.
    private KafkaConsumer _consumer;

    public static void main(String[] args) throws Exception {
        Consumer10Demo consumerDemo = new Consumer10Demo("consumer-demo");
        consumerDemo.consume();
    }

    public void testNewConsume() {

        try{
            // 关闭offset自动提交
            _props.put("enable.auto.commit", "false");
            _consumer = new KafkaConsumer(_props);
            TopicPartition topicPartition = new TopicPartition(KafkaProperties.TOPIC_NAME,1);
            // 手动分配分区，可以指定消费某个分区，方便测试
            _consumer.assign(Arrays.asList(topicPartition));
            // 自动分配分区，线上推荐使用这种方式
            /*
            _consumer.subscribe(Arrays.asList(topics), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    LOG.info("onPartitionsRevoked is invoked");
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    LOG.info("onPartitionsAssigned is invoked");
                }
            }); */
            // 上次消费到的位置
            long lastPosition = _consumer.position(topicPartition);
            LOG.info("[topic: {},partition: {}] current offset is {}",KafkaProperties.TOPIC_NAME,topicPartition.partition(),lastPosition);
            // 强制从某个offset开始消费，上次消费到的位置是lastPosition，这里从lastPosition-10开始消费
            if(lastPosition > 10)
                _consumer.seek(topicPartition,lastPosition - 10);
            //_consumer.seek(topicPartition,25345199	);
            // 强制从最后一条消息开始消费
            //_consumer.seekToEnd(Arrays.asList(topicPartition));
            // 强制从第一条消息开始消费
            // _consumer.seekToBeginning(Arrays.asList(topicPartition));
            try {
                while (true) {
                    ConsumerRecords<String,String> records = _consumer.poll(200);
                    for (ConsumerRecord<String,String> record : records) {
                        info(record);
                    }
                    if(!records.isEmpty()){
                        _consumer.commitSync();
                        break;
                    }
                }
            }finally {
                _consumer.close();
            }
        } catch (Exception e){
            LOG.error("unexpected error",e);
        }finally {
            _consumer.close();
        }
    }

	public void consumeThread() {

		KafkaConsumer  consumer = new KafkaConsumer(_props);
        /* 消费者订阅的topic, 可同时订阅多个 */
        consumer.subscribe(Arrays.asList(KafkaProperties.TOPIC_NAME));

        Thread thread = new Thread(() -> {
            if (consumer != null) {
                try {

                    while (true) {
                        try{
                            ConsumerRecords<String, String> records = consumer.poll(100);
                            for (ConsumerRecord<String, String> record : records){
                                String message = record.value();
                                LOG.info(String.format("partition = %d, offset = %d, value = %s \n", record.partition(), record.offset(), record.value()));
                            }
                        }catch (Exception e){
                        	LOG.error(e.getMessage(),e);
                        }
                    }

                } catch (Exception e) {
                    if (consumer != null) {
                        try {
                            consumer.close();
                        } catch (Throwable e1) {
                        	LOG.error("Turn off Kafka consumer error! " + e);
                        }
                    }
                    LOG.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }, "Topic-Distribute-" + KafkaProperties.TOPIC_NAME + "-");
        thread.start();

	}


    public void consume(){

        try{
            _consumer = new KafkaConsumer(_props);
            _consumer.subscribe(Arrays.asList(KafkaProperties.TOPIC_NAME), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    LOG.info("onPartitionsRevoked is invoked");
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    LOG.info("onPartitionsAssigned is invoked");
                }
            });
            try {
                while (true) {
                    ConsumerRecords<String,String> records = _consumer.poll(200);
                    for (ConsumerRecord<String,String> record : records) {
                        info(record);
                    }
                }
            }finally {
                _consumer.close();
            }
        } catch (Exception e){
            LOG.error("unexpected error",e);
        }finally {
            _consumer.close();
        }
    }

    public Consumer10Demo(String name) {
        this._name = name;
        //common config
        _props.put("bootstrap.servers", KafkaProperties.BOOTSTRAP_ERVERS); //required
        _props.put("group.id", KafkaProperties.GROUP_ID);
        _props.put("client.id", KafkaProperties.CLIENT_ID_CONSUMER);
        _props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        _props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        _props.put("max.poll.interval.ms", "300000");//default 300000
        _props.put("max.poll.records", "500");//default 500
        _props.put("enable.auto.commit", "true");
        _props.put("fetch.max.bytes", "4194304");
    }

    /**
     * Manual partition assignment does not use group coordination, so consumer failures will not
     * cause assigned partitions to be rebalanced.
     */
    public void consumeWitManualPartitionAssignment(){

        try{
            _consumer = new KafkaConsumer(_props);
            TopicPartition topicPartition = new TopicPartition("benchmark",0);
            _consumer.assign(Arrays.asList(topicPartition));
            try {
                while (true) {
                    ConsumerRecords<String,String> records = _consumer.poll(100);
                    for (ConsumerRecord<String,String> record : records) {
                        info(record);
                    }
                }
            }finally {
                _consumer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            _consumer.close();
        }

    }

    /**
     * Look up the offsets for the given partitions by timestamp
     * @param timestamp
     */
    public void offsetsForTimes(long timestamp,TopicPartition topicPartition){

        _consumer = new KafkaConsumer(_props);
        Map<TopicPartition, Long> offsetsToSearch = new HashMap<>();
        offsetsToSearch.put(topicPartition,timestamp);
        Map<TopicPartition, OffsetAndTimestamp> offsets =_consumer.offsetsForTimes(offsetsToSearch);
        LOG.info("------ offsetsForTimes on partition {} ------",topicPartition.partition());
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> offset:
                offsets.entrySet()) {
            if(offset.getValue() == null)
                LOG.info("no offfset found on topic {} partition {} on timestamp {}",
                        offset.getKey().topic(),offset.getKey().partition(),timestamp);
            else
                LOG.info("topic:{},partition:{},offset:{},timestatmp:{}", offset.getKey().topic(),
                        offset.getKey().partition(), offset.getValue().offset(),offset.getValue().timestamp());
        }
    }

    public void info(ConsumerRecord<String,String> record) {
//        LOG.info("conusmer:{},topic:{},partition:{},offset:{},key:{},value:{}",
//                this._name,record.topic(),record.partition(),record.offset(),record.key(),record.value());

//        System.out.println(record.value());

    }

}
