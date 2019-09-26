/*************************************************
 * @File: KafkaProducerSingleton.java
 * @author QuailOurs(yifei.wu)
 * @Version: 1.0
 * @since 2019-01-15 20:01:84
 * @Description:
 *************************************************/
package com.tools.kafka;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * @File KafkaProducerSingleton.java
 * @author QuailOurs(yifei.wu)
 * @since 2019-01-15 20:01:87
 * @Description
 */
@Slf4j
public class KafkaProducerSingleton {

	private static class LazyHandler {
		private static final KafkaProducerSingleton instance = new KafkaProducerSingleton();
	}

	public static final KafkaProducerSingleton getInstance() {
		return LazyHandler.instance;
	}

	private KafkaProducerSingleton() {}

	private static final String BOOTSTRAP_ERVERS = "10.135.9.4:9092,10.135.9.5:9092,10.135.9.6:9092,10.135.9.7:9092,10.135.9.8:9092";
	private static final String TOPIC_NAME = "hdp_teu_op_skymirror";
	private static final String CLIENT_ID_PRODUCER = "hdp_teu_op-hdp_teu_op_skymirror-bRe09";

	private static Properties props = new Properties();
	private static KafkaProducer<String, String> producer;

	static {
		props.put("bootstrap.servers", BOOTSTRAP_ERVERS);// required
		props.put("client.id", CLIENT_ID_PRODUCER);
		props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");// required
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");// required
		props.put("batch.size", "16384");// default 16384 | 16K
		props.put("acks", "all");// default 1
		props.put("buffer.memory", "33554432");// 32M
		props.put("metadata.max.age.ms", "300000");// default 300000 | 5min
		props.put("max.block.ms", "60000");// default 60000
		props.put("request.timeout.ms", "30000");// default 30000

		producer = new KafkaProducer<String, String>(props);
	}

	public void produce(String message, boolean isAsync) {
		produce0(message, TOPIC_NAME, isAsync);
	}

	private void produce0(String message, String topic, boolean isAsync) {
		try {
			long startTime = System.currentTimeMillis();
			if (isAsync) { // 异步
				producer.send(new ProducerRecord<String, String>(topic, message), new AckCallBack(startTime, message));
			} else {
				producer.send(new ProducerRecord<String, String>(topic, message)).get();
			}
		} catch (Exception e) {
			producer.close();
			log.error("produce: ", e);
		}
	}

	@SuppressWarnings("unused")
	private class AckCallBack implements Callback {

		private final long _startTime;
		private final String _message;

		public AckCallBack(long startTime, String message) {
			this._startTime = startTime;
			this._message = message;
		}

		/**
		 * 收到Kafka服务端发来的Ack确认消息后, 会调用此函数
		 *
		 * @param metadata
		 *            生产者发送消息的元数据, 如果发送过程出现异常, 此参数为null
		 * @param e
		 *            发送过程出现的异常, 如果发送成功此参数为空
		 */
		@Override
		public void onCompletion(RecordMetadata metadata, Exception e) {
			if (metadata != null) {
//				ConstantLogger.COMMON.info(String.format("写入成功 partition:{},messgage:{},elapsedTimeMs:{}".replace("{}", "%s"), metadata.partition(), _message, elapsedTime));
			} else {
				log.error("写入失败", e);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		String message = new JSONObject().toJSONString();
		KafkaProducerSingleton.getInstance().produce(message, true);
	}
}
