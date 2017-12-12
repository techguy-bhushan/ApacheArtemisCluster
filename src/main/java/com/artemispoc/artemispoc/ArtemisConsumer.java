package com.artemispoc.artemispoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;
/*
* @author Bhushan Uniyal
* */
@Component
public class ArtemisConsumer implements MessageListener{
    @Value("${sample}")
    String topic;
    Logger log = LoggerFactory.getLogger(ArtemisConsumer.class);
    public static AtomicInteger consumeCount = new AtomicInteger(0);

    @Override
    public void onMessage(Message message) {
        String  lastMessage = null;
        try {
            TextMessage textMessage = (TextMessage) message;
             lastMessage = textMessage.getText();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        log.info("Recieved Message: in Amq Consumer---> {} count : {}", lastMessage , consumeCount.addAndGet(1));
    }

}
