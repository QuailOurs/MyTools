/*************************************************
 * @File name: 恶趣味恶趣味次.java
 * @Author: QuailOurs(yifei.wu)
 * @Version: 1.0
 * @Date: 2018-12-27 11:12:86
 * @Description:
 *************************************************/
package com.bj58.sec.test.demo.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

/**
 * @File 恶趣味恶趣味次.java
 * @Author QuailOurs(yifei.wu)
 * @Date 2018-12-27 11:12:86
 * @Description
 */
public class KafkaOrderConsumer implements Runnable {

	protected static final Logger logger = LoggerFactory.getLogger(KafkaOrderConsumer.class);

	private KafkaStream stream;
	private int threadNumber;

	public KafkaOrderConsumer(KafkaStream stream, int threadNumber) {
		this.stream = stream;
		this.threadNumber = threadNumber;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			// 消费队列内容
			final MessageAndMetadata<byte[], byte[]> messageAndMetadata = it.next();
			try {
				final byte[] messageBytes = (byte[]) messageAndMetadata.message();
				if (messageBytes != null && messageBytes.length > 0) {
					String content = new String(messageBytes);
					logger.info("message:'" + content + "'");
					/**
					 * @// TODO: 2018/3/20 0020 消费入库
					 */
				}
			} catch (Exception e) {
				logger.error("kafka back order consumer error", e);
			}

		}
		logger.info("Shutting down Thread: " + threadNumber);
	}
}
