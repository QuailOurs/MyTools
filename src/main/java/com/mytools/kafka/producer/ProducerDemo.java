package com.bj58.sec.test.demo.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bj58.sec.test.demo.KafkaProperties;
/**
* 注意：
* 1.KafkaProducer是线程安全，也可以一个线程独享一个KafkaProducer
* 2.千万不要频繁创建KafkaProducer，尤其不要每处理一条消息创建一个KafkaProducer
* 3.KafkaProducer使用完后需要调用close方法关闭  
*/
public class ProducerDemo {

    static final Logger LOG = LoggerFactory.getLogger(ProducerDemo.class);


    final Properties _props = new Properties();
    // thread safe
    KafkaProducer _producer;

    int _messageNo = 0;

    public static void main(String[] args) throws Exception {
        final boolean isAsync = args.length == 0 || args[0].trim().equalsIgnoreCase("async");
        final ProducerDemo producerDemo = new ProducerDemo();
        producerDemo.produce(isAsync,KafkaProperties.RES_TOPIC_NAME);
    }

    ProducerDemo() {
        _props.put("bootstrap.servers", KafkaProperties.BOOTSTRAP_ERVERS);//required
        _props.put("client.id",KafkaProperties.CLIENT_ID_PRODUCER);
        _props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");//required
        _props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");//required
        _props.put("batch.size","16384");//default 16384 | 16K
        _props.put("acks", "all"); //default 1
        _props.put("buffer.memory", "33554432");// 32M
        _props.put("metadata.max.age.ms", "300000");// default 300000 | 5min
        _props.put("max.block.ms", "60000");// default 60000
        _props.put("request.timeout.ms", "30000");// default 30000
    }

    public void produce(boolean isAsync,String topic) {
        try {
            _producer = new KafkaProducer(_props);
            String messageStr = "";
            while (true){
                messageStr = "message_" + _messageNo++;
                long startTime = System.currentTimeMillis();
                if(isAsync){//异步
                    _producer.send(new ProducerRecord(topic,messageStr),
                            new DemoCallBack(startTime,_messageNo,messageStr));
                }else{//
                    RecordMetadata rm = (RecordMetadata)_producer.send(new ProducerRecord(topic, _messageNo, messageStr)).get();
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    LOG.info("key:{},topic:{},partition:{},offset:{} send success,elapsedTimeMs:{}",
                            this._messageNo,rm.topic(),rm.partition(),rm.offset(),elapsedTime);
                }
            }
        }catch (Exception e){
            LOG.error("",e);
        }finally {
            _producer.close();
        }
    }

    public void produceWithIdempotence(String topic){
        //If set, the　retries　config will default to Integer.MAX_VALUE and the acks config will default to all
        _props.put("enable.idempotence","true");//开启幂等性
        String messageStr = "";
        try {
            _producer = new KafkaProducer(_props);
            while (true) {
                long startTime = System.currentTimeMillis();
                messageStr = "message_" + _messageNo++;
                _producer.send(new ProducerRecord(topic,_messageNo, messageStr),new DemoCallBack(startTime,_messageNo,messageStr));
            }
        }catch (Exception e){
            LOG.error("",e);
        }finally {
            _producer.close();
        }
    }

    public void produceWithTransaction(String topic) {
        //If set, the　retries　config will default to Integer.MAX_VALUE and the acks config will default to all
        _props.put("transactional.id","hdp_teu_dpd_test_transactional");
        _producer = new KafkaProducer(_props);
        _producer.initTransactions();
        String messageStr = "";
        try {
            while (true){
                long startTime = System.currentTimeMillis();
                messageStr = "message_" + _messageNo++;
                try {
                    _producer.beginTransaction();
                    for (int i = 0; i < 100; i++) {
                        _producer.send(new ProducerRecord(topic, _messageNo, messageStr),new DemoCallBack(startTime,_messageNo,messageStr));
                    }
                    _producer.commitTransaction();
                } catch (ProducerFencedException e){
                    // We can't recover from these exceptions, so our only option is to close the producer and exit.
                    _producer.close();
                    LOG.error("",e);
                }catch (OutOfOrderSequenceException e){
                    // We can't recover from these exceptions, so our only option is to close the producer and exit.
                    _producer.close();
                    LOG.error("",e);
                }catch (Exception e){
                    // For all other exceptions, just abort the transaction and try again.
                    _producer.abortTransaction();
                    LOG.warn("",e);
                }
            }
        }catch (Exception e){
            LOG.error("",e);
        }finally {
            _producer.close();
        }
    }

   static class DemoCallBack implements Callback {

        private final  long _startTime;

        private final int _key;

        private final String _message;


        public DemoCallBack(long startTime, int key, String message) {
            this._startTime = startTime;
            this._key = key;
            this._message = message;
        }

       /**
        * 收到Kafka服务端发来的Ack确认消息后，会调用此函数
        * @param metadata 生产者发送消息的元数据，如果发送过程出现异常，此参数为null
        * @param e 发送过程出现的异常，如果发送成功此参数为空
        */
        public void onCompletion(RecordMetadata metadata, Exception e) {
            long elapsedTime = System.currentTimeMillis() - _startTime;
            if(metadata != null){
                LOG.info("partition:{},key:{},messgage:{},elapsedTimeMs:{}", metadata.partition(),_key,_message,elapsedTime);
            }else {
                LOG.error("",e);
            }
        }
    }


}
