package com.mytools.kafka.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Consumer08Demo {

    static final Logger log = LoggerFactory.getLogger(Consumer08Demo.class);

    final Properties props = new Properties();

    static String topic = null;

    static {
    	Map<Integer, String> topicMap = new HashMap<>();
    	topicMap.put(1, "hdp_teu_ops_ajk_wos_broker"); // 安居客 wos
    	topicMap.put(2, "hdp_teu_ops_bash_log_db"); // db bash log
    	topicMap.put(3, "hdp_teu_ops_bash_log"); // bash log
    	topicMap.put(4, "hdp_ubu_base_uc_passport"); // 58passport log
    	topicMap.put(5, "hdp_teu_ops_bash_log"); // bash log
    	topicMap.put(6, "hdp_teu_ops_nginx_log"); // nginx log
    	topicMap.put(7, "hdp_teu_spat_im_access_logdispatch"); // im log
    	topicMap.put(8, "hdp_ubu_xxzl_usermgrlog"); // usermgr log
    	topicMap.put(9, "hdp_ubu_xxzl_hunter_strategy_adv"); // hunter_strategy log

        topic = topicMap.get(4);
    }

    final String _name;

    public static void main(String[] args) throws Exception {
        Consumer08Demo consumerDemo = new Consumer08Demo("consumer-demo");
        consumerDemo.consume();
    }

    public void consume(){

    	ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));

    	Thread thread = new Thread(() -> {
            if (consumer != null) {
                try {
                    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
                    topicCountMap.put(topic, 1); // 一次从主题中获取一个数据

                    Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
                    KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// 获取每次接收到的这个数据
                    ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

                    while (iterator.hasNext()) {
                        String message = new String(iterator.next().message());
                        info(message);
                    }
                    messageStreams.clear();

                } catch (Exception e) {
                    if (consumer != null) {
                        try {
                            consumer.shutdown();
                        } catch (Throwable e1) {
                            log.error("Turn off Kafka consumer error! " + e);
                        }
                    }
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        }, "Topic-Distribute-" + topic + "-");
        thread.start();
    }

    public Consumer08Demo(String name) {
        this._name = name;
        //common config
        props.put("zookeeper.connect", "10.126.99.105:2181,10.126.99.196:2181,10.126.81.208:2181,10.126.100.144:2181,10.126.81.215:2181/58_kafka_cluster");//声明zk
        props.put("group.id", "usermgr");// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
//        props.put("enable.auto.commit", "false");
        props.put("fetch.message.max.bytes", "10485760");//默认1M 设置成10M
    }

    public void info(String msg) {
        log.info("msg:{}", msg);
    }

}
