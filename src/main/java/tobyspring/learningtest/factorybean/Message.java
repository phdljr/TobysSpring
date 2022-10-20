package tobyspring.learningtest.factorybean;

public class Message {
    String text;

    // 외부에서 생성자 사용 못함
    private Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    // 스태틱 팩토리 메소드를 제공
    public static Message newMessage(String text){
        return new Message(text);
    }
}
