package tobyspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tobyspring.learningtest.factorybean.MessageFactoryBean;

@Configuration
public class FactoryBeanConfiguration {
    @Bean
    public MessageFactoryBean message(){
        MessageFactoryBean mb = new MessageFactoryBean();
        mb.setText("Factory Bean");
        return mb;
    }
}
