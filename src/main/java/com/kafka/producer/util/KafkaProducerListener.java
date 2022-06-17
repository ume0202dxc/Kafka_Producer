package com.kafka.producer.util;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;

public class KafkaProducerListener implements ProducerListener<Object, Object> {
	
	public static final String KAFKA_UID = "Kafka_UID";

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerListener.class);

    private final String sourceTemplate;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public KafkaProducerListener(final String sourceTemplate, final KafkaTemplate<Object, Object> kafkaTemplate) {
        this.sourceTemplate = sourceTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onSuccess(final ProducerRecord<Object, Object> producerRecord, final RecordMetadata recordMetadata) {
        ThreadContext.put(KAFKA_UID,
            new String(producerRecord.headers().lastHeader(KAFKA_UID).value(), StandardCharsets.UTF_8));
        log.info(sourceTemplate
                + " Sucessfully written message to Client topic: {}, offset: {}, partition: {}, message size (bytes):{}",
            producerRecord.topic(),
            recordMetadata.offset(), recordMetadata.partition(), recordMetadata.serializedValueSize());
        ThreadContext.remove(KAFKA_UID);
    }

    @Override
    public void onError(final ProducerRecord<Object, Object> producerRecord, final Exception exception) {
        ThreadContext.put(KAFKA_UID,
            new String(producerRecord.headers().lastHeader(KAFKA_UID).value(), StandardCharsets.UTF_8));
        log.error(sourceTemplate + " Failed to send following message to EH:" + producerRecord.value());
        final Header header = producerRecord.headers().lastHeader("RetryCount");
        boolean send = false;
        if (header == null) {
            producerRecord.headers().add("RetryCount", ByteBuffer.allocate(Integer.BYTES).putInt(1).array());
            send = true;
        } else {
            int retryCount = ByteBuffer.wrap(header.value()).getInt();
            if (retryCount < 3) {
                producerRecord.headers().add("RetryCount", ByteBuffer.allocate(Integer.BYTES).putInt(++retryCount).array());
                send = true;
            } else {
                send = false;
                log.error(sourceTemplate + " Reached maximum retries for {} caused by {} ",
                    producerRecord.value(), exception.getMessage());
            }
        }
        if (send) {
            log.error(sourceTemplate + " Retrying to send message to Client topic " + producerRecord.topic());
            kafkaTemplate.send(producerRecord);
        }
        ThreadContext.remove(KAFKA_UID);
    }
}
