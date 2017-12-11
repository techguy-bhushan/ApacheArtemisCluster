package com.artemispoc.artemispoc;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Topic;

import java.util.HashMap;
import java.util.Map;
/*
* @author Bhushan Uniyal
* */
@Configuration
public class ArtemisConfig {

    @Value("${sample}")
    String topicDestination;

    @Bean("amqTransportConfiguration")
    public TransportConfiguration amqTransportConfiguration() {
        return new TransportConfiguration(NettyConnectorFactory.class.getName(), getParams("61616"));
    }


    @Bean("connectionFactory")
    public ConnectionFactory activeMQJMSConnectionFactory(@Qualifier("amqTransportConfiguration") TransportConfiguration transportConfiguration) throws JMSException {
        ActiveMQJMSConnectionFactory activeMQJMSConnectionFactory =
                new ActiveMQJMSConnectionFactory( false, transportConfiguration);
        activeMQJMSConnectionFactory.setPassword("admin");
        activeMQJMSConnectionFactory.setUser("admin");
        return activeMQJMSConnectionFactory;
    }


    // We create a JMS listenerContainer1 which is a connection to  artemis server 1
    @Bean
    public MessageListenerContainer listenerContainer1(@Qualifier("connectionFactory") ConnectionFactory connectionFactory, Consumer consumer, SimpleMessageConverter messageConverter, @Qualifier("topic") Topic topic) {
        DefaultMessageListenerContainer defaultMessageListenerContainer =
                new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestination(topic);
        defaultMessageListenerContainer.setMessageListener(consumer);
        defaultMessageListenerContainer.setSessionAcknowledgeMode(1);
        defaultMessageListenerContainer.setMessageConverter(messageConverter);
        return defaultMessageListenerContainer;
    }

    // a JMS listenerContainer which is a connection to artimes server 2
    @Bean
    public MessageListenerContainer listenerContainer2(@Qualifier("connectionFactory")ConnectionFactory connectionFactory, Consumer consumer, SimpleMessageConverter messageConverter, @Qualifier("topic") Topic topic) {
        DefaultMessageListenerContainer defaultMessageListenerContainer =
                new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestination(topic);
        defaultMessageListenerContainer.setMessageListener(consumer);
        defaultMessageListenerContainer.setSessionAcknowledgeMode(1);
        defaultMessageListenerContainer.setMessageConverter(messageConverter);
        return defaultMessageListenerContainer;

    }

    @Bean("topic")
    public Topic topic() {
        Topic topic = ActiveMQJMSClient.createTopic(topicDestination);
        return topic;
    }

    @Bean("connection")
    public Connection connection(ConnectionFactory connectionFactory) {
        try {
        Connection connection = connectionFactory.createConnection();
        return connection;
        } catch (JMSException e) {
            throw new RuntimeException();
        }
    }

     static Map<String, Object> getParams(String port) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("host", "localhost");
        params.put("port", port);
        return params;
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }
}