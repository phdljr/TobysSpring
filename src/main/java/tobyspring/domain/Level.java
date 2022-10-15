package tobyspring.domain;

public enum Level {
    // Level enum에선 세 가지의 오브젝트만 존재한다.
    // BASIC, SILVER, GOLD는 Level enum의 오브젝트이다.
    // 다음 레벨이 무엇인지에 대한 정보도 존재한다.
    GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);

    private final int value;
    private final Level next;

    // DB에 저장할 값을 넣어줄 생성자
    Level(int value, Level next) {
        this.value = value;
        this.next = next;
    }

    // 값을 가져오는 메소드
    public int intValue(){
        return value;
    }

    public Level nextLevel() {
        return next;
    }

    // 값으로부터 Level 타입 오브젝트를 가져오도록 만든 스태틱 메소드
    public static Level valueOf(int value){
        switch (value){
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
