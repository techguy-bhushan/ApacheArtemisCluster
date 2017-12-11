package com.artemispoc.artemispoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;
/*
* @author Bhushan Uniyal
* */
@Component
public class Producer {

    @Autowired
    @Qualifier("connection")
    private Connection connection;

    @Autowired
    private Destination destination;

    public static AtomicInteger produceCount = new AtomicInteger(0);

    Logger log = LoggerFactory.getLogger(Consumer.class);


    public void send(String msg) {
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage(msg);
            producer.send(message);
            log.info("Sending message {} to destination, totalCount {}", msg, produceCount.addAndGet(1));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
           /* if (session != null || producer != null) {
                try {
                    if (producer != null)
                    session.close();

                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }*/
        }

    }

}
