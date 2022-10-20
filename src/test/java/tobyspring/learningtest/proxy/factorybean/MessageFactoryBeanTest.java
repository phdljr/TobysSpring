package tobyspring.learningtest.proxy.factorybean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import tobyspring.config.FactoryBeanConfiguration;
import tobyspring.learningtest.factorybean.Message;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = FactoryBeanConfiguration.class)
class MessageFactoryBeanTest {
    @Autowired
    ApplicationContext context;

    @Test
    void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        // 팩토리 빈 자체를 들고오려면 이름의 맨 앞에 &를 붙이면 된다.
        // Object message = context.getBean("&message");

        // 팩토리 빈의 getObject로 들고온 객체의 타입은 Message이다.
        assertThat(message).isInstanceOf(Message.class);
        assertThat(((Message)message).getText()).isEqualTo("Factory Bean");
    }
}