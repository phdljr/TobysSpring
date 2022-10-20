package tobyspring.learningtest.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    // 오브젝트를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서
    // 대신 DI를 받을 수 있게 한다.
    // 이는 오브젝트 생성 중에 사용한다.
    public void setText(String text) {
        this.text = text;
    }

    // getObject() 메소드가 돌려주는 오브젝트가 항상 같은 싱글톤 오브젝트인지 알려준다.
    // 이 팩토리 빈은 매번 요청할 때마다 새로운 오브젝트를 만들므로 false
    // 만들어진 빈 오브젝트는 싱글톤으로 스프링이 관리해줄 수 있다.
    @Override
    public boolean isSingleton() {
        return false;
    }

    // 빈 오브젝트를 생성해서 돌려준다.
    // 실제 빈으로 사용될 오브젝트를 직접 생성한다.
    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(text);
    }

    // 생성되는 오브젝트의 타입을 알려준다.
    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }
}
