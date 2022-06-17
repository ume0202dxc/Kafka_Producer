package com.kafka.producer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import com.kafka.producer.service.KafkaProducer;


@SpringBootTest
@DirtiesContext
@EmbeddedKafka
class AppTest {

	@Autowired
    public KafkaTemplate<String, String> template;

//    @Autowired
//    private KafkaProducer producer;
//    
//    @Autowired
//    private KafkaMultiProducer multiProducer;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test 
    public void sendString() throws Exception {
    	kafkaProducer.send();
    }
    
//    @Test 
//    public void sendDefault() throws Exception {
//    	kafkaProducer.sendDefault();
//    }
    
    
	/*
	 * @Test public void produceDefaultTest() throws Exception {
	 * template.send("premiumsequence", "Sending with default template"); }
	 */
	 

	/*
	 * @Test public void produceFrameworkTest() throws Exception {
	 * 
	 * producer.sendMessage("premiumsequence",
	 * "Sending with our own simple KafkaProducer"); }
	 */
    
	/*
	 * @Test public void multiProduceFrameworkTest() throws Exception {
	 * 
	 * multiProducer.sendMessage("premiumsequence",
	 * "Sending with our own simple KafkaProducer"); }
	 */
    
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate2;
//
//    private static final String TOPIC = "will-test2";
//    
//    @Test 
//    public void sendMsg() throws Exception{
//    	
//    	kafkaTemplate2.send(TOPIC, "message");
//    	
//    }
    
    
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplateObject;
//
//    private static final String TOPIC1 = "premiumsequence";
//
//    @Test 
//    public void sendObject() {
//    	System.out.println("Message: Object");
//
//    	kafkaTemplateObject.send(TOPIC1, "Object");
//
//    }
//

}
