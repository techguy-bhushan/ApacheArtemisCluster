package com.artemispoc.artemispoc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@Import(ArtemispocApplication.class)
@SpringBootTest
public class ArtemisTest {

    @Autowired
    private ArtemisProducer producer;

    /*
    * We will check in this test case before send message count and then after message send count
    * */

    @Test
    public void testMqBridge() throws InterruptedException {

        // Assert count 0, because no message send to jms
        assertThat(ArtemisProducer.produceCount.get()).isEqualTo(0);
        assertThat(ArtemisConsumer.consumeCount.get()).isEqualTo(0);

        // 5 messages will send on A-MQ producer
        IntStream.range(0,50).forEach((int i) -> {
            producer.send("Test message:"+i);
        });

        Thread.sleep(2000);
        // 5 message will produce by Artemis producer
        assertThat(ArtemisProducer.produceCount.get()).isEqualTo(50);

        // 5 message will consume by Artemis consumer
        assertThat(ArtemisConsumer.consumeCount.get()).isEqualTo(50);

    }
}
