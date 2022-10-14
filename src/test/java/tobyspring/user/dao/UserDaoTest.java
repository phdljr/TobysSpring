package tobyspring.user.dao;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobyspring.user.domain.User;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

// 자동으로 만들어줄 애플리케이션 컨텍스트의 설정 파일 위치 지정
@ContextConfiguration(classes = {DaoFactory.class})
// JUnit 프레임워크의 테스트 실행 방법을 확장할 때 사용
// SpringExtension이라는 JUnit용 테스트 컨텍스트 프레임워크 확장 클래스를 지정함
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoTest {

    @Autowired
    private ApplicationContext context;

    private UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    // 모든 테스트 메소드가 실행되기 전에 한 번 호출됨
    @BeforeAll
    public void setUp() {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        this.dao = this.context.getBean("userDao", UserDao.class);
//        System.out.println("before all");

        user1 = new User("1", "1", "1");
        user2 = new User("2", "2", "2");
        user3 = new User("3", "3", "3");
    }

    @Test
    public void addAndGet() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        User user = new User();
        user.setId("111");
        user.setName("hhh");
        user.setPassword("1234");

        dao.add(user);
        assertThat(dao.getCount()).isEqualTo(1);

        User user2 = dao.get("111");

        assertThat(user.getName()).isEqualTo(user2.getName());
        assertThat(user.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    public void count() throws SQLException {


        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);
    }

    @Test
    public void getUserFailure() {
        assertThatThrownBy(() -> {
            dao.get("asdasd");
        }).isInstanceOf(EmptyResultDataAccessException.class);
    }
}
