package tobyspring.domain;

public class User {
    String id;
    String name;
    String password;
    Level level;
    int login;
    int recommend;
    String email;

    // 자바빈의 규약을 따르는 클래스에는 기본 생성자가 필수다.
    public User() {
    }

    public User(String id, String name, String password, Level level, int login, int recommend, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.level = level;
        this.login = login;
        this.recommend = recommend;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(int login) {
        this.login = login;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // User 클래스는 DAO와 Service 계층에서도 많이 쓰이기 때문에
    // 내부 정보가 변경되는 것은 UserService보다 User가 스스로 다루는 게 적절하다.
    // 또한, user 객체 스스로 예외상황에 대한 검증 기능을 갖고 있는 편이 안전하다.
    public void upgradeLevel(){
        Level nextLevel = level.nextLevel();
        if(nextLevel == null){
            throw new IllegalStateException(this.level +"은 업그레이드가 불가능합니다.");
        }
        else{
            this.level = nextLevel;
        }
    }
}
