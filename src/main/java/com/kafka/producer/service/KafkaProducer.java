package com.kafka.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Component
public class KafkaProducer {
	
	public static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
	
//	@Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplateString;
	
	@Autowired
    private KafkaTemplate<String, Object> kafkaTemplateObject;

    private static final String TOPIC1 = "will-test1";
    
    private static final String TOPIC2 = "will-test2";

    public String send() {
    	
    	StringBuilder response = new StringBuilder();
    	
    	String stringMsg = "String Message";
    	log.info("sending String Message ='{}' to topic='{}'", stringMsg, TOPIC1);
    	kafkaTemplateString.send(TOPIC1, stringMsg);
    	ListenableFuture<SendResult<String, String>> stringResponse = kafkaTemplateString.send(TOPIC1, stringMsg);
    	
    	stringResponse.addCallback(new ListenableFutureCallback<>() {
	        @Override
	        public void onSuccess(SendResult<String, String> result) {
	        	response.append("Sent event=[" + stringMsg +
	                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
	        	log.info(response.toString());
	        	
	        }
	        @Override
	        public void onFailure(Throwable ex) {
	        	response.append("Unable to send message=["
	                    + stringMsg + "] due to : " + ex.getMessage());
	        	log.error(response.toString());
	        }
	    });
    	
    	String sb = "String Object";
    	log.info("sending Object Message ='{}' to topic='{}'", sb, TOPIC2);
    	
    	ListenableFuture<SendResult<String, Object>> objectResponse = kafkaTemplateObject.send(TOPIC2, sb);
    	
    	objectResponse.addCallback(new ListenableFutureCallback<>() {
	        @Override
	        public void onSuccess(SendResult<String, Object> result) {
	        	response.append("Sent event=[" + sb +
	                    "] with offset=[" + result.getRecordMetadata().offset() + "]");
	        	log.info(response.toString());
	        }
	        @Override
	        public void onFailure(Throwable ex) {
	        	response.append("Unable to send message=["
	                    + sb + "] due to : " + ex.getMessage());
	        	log.error(response.toString());
	        }
	    });

        return response.toString();
    }
    
//    public void sendDefault() {
//    	
//    	log.info("sending Default String New Message");
//    	kafkaTemplate.send(TOPIC1, "Default String");
//    }

}