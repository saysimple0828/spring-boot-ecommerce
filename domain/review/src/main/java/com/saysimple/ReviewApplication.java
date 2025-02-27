package com.saysimple;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
public class ReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewApplication.class, args);
    }

    @Bean
//    @LoadBalanced
    public RestTemplate getRestTemplate() {
        int TIMEOUT = 5000;

        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(TIMEOUT))
                .setReadTimeout(Duration.ofMillis(TIMEOUT))
                .build();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
//    @Bean
//    public FeignErrorDecoder getFeignErrorDecoder() {
//        return new FeignErrorDecoder();
//    }

//    @Bean
//    MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public ConnectionFactory rabbitConnectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost", 5672);
//        connectionFactory.setUsername("guest_user");
//        connectionFactory.setPassword("guest_user");
//        // .. 이러저런 설정
//        // publisher confirm ON
//        connectionFactory.setPublisherConfirms(true);
//        return connectionFactory;
//    }
//
//    @Bean
//    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
//                                  MessageConverter messageConverter) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter);
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if(ack) {
//                System.out.println("ACK");
//            } else {
//                System.out.println("NACK: " + cause);
//            }
//        });
//        return rabbitTemplate;
//    }
}
