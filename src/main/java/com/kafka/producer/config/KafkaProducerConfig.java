package com.kafka.producer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {
	
	@Value("${spring.kafka.producer1.bootstrap-servers}")
    private String producer1BootstrapServers;
	
	@Value("${spring.kafka.producer1.key-serializer}")
    private String producer1KeySerializer;
	
	@Value("${spring.kafka.producer1.value-serializer}")
    private String producer1ValueSerializer;
	
	@Value("${spring.kafka.producer1.retries}")
    private String producer1Retries;
	
	@Value("${spring.kafka.producer1.max.request.size}")
    private String producer1MaxReqSize;
	
	@Value("${spring.kafka.producer2.bootstrap-servers}")
    private String producer2BootstrapServers;
	
	@Value("${spring.kafka.producer2.key-serializer}")
    private String producer2KeySerializer;
	
	@Value("${spring.kafka.producer2.value-serializer}")
    private String producer2ValueSerializer;
	
	@Value("${spring.kafka.producer2.retries}")
    private String producer2Retries;
	
	@Value("${spring.kafka.producer2.max.request.size}")
    private String producer2MaxReqSize;

	@Bean
    public ProducerFactory<String, String> producerFactory1() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer1BootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer1KeySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer1ValueSerializer);
        config.put(ProducerConfig.RETRIES_CONFIG, producer1Retries);

        return new DefaultKafkaProducerFactory<String, String>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate1() {
        return new KafkaTemplate<String, String>(producerFactory1());
    }
    
	@Autowired
    private KafkaProperties kafkaProperties;
	
	@Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer2BootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer2KeySerializer);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer2ValueSerializer);
        config.put(ProducerConfig.RETRIES_CONFIG, producer2Retries);

        return new DefaultKafkaProducerFactory<String, Object>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<String, Object>(producerFactory());
    }    
}
