package com.mytools.kafka.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProducerInterceptorDemo  implements ProducerInterceptor<Integer,String>{

    private static final Logger LOG = LoggerFactory.getLogger(ProducerInterceptorDemo.class);

    public ProducerRecord<Integer, String> onSend(ProducerRecord<Integer, String> record) {
        //过滤掉key为奇数的消息
        if (record.key() % 2 == 0){
            return record;
        }
        return null;
    }

    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if(metadata != null && "test".equals(metadata.topic()) && metadata.partition() == 0){
            // 对于正常返回，Topic为‘test’且分区为0的消息的返回信息进行打印
            LOG.info(metadata.toString());
        }
    }

    public void close() {

    }

    public void configure(Map<String, ?> configs) {
        //初始化
    }
}
