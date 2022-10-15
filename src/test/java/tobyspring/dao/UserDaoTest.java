package tobyspring.dao;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobyspring.config.DaoFactory;
import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

// 자동으로 만들어줄 애플리케이션 컨텍스트의 설정 파일 위치 지정
@ContextConfiguration(classes = {DaoFactory.class})
// JUnit 프레임워크의 테스트 실행 방법을 확장할 때 사용
// SpringExtension이라는 JUnit용 테스트 컨텍스트 프레임워크 확장 클래스를 지정함
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// 스프링의 테스트 컨텍스프 프레임워크에게 해당 클래스의 테스트에서 컨테이너의 상태를 변경한다는 것을 알려준다.
// 메소드에 붙여줄 수도 있다. 근데 차라리 다른 설정 파일을 만들어서 하는게 좋지 않을까?
@DirtiesContext
public class UserDaoTest {

    @Autowired
    private UserDao dao;

    private User user1;
    private User user2;
    private User user3;

    // @BeforeAll 모든 테스트 메소드가 실행되기 전에 한 번 호출됨
    // @BeforeEach 각각의 테스트 메소드가 실행될 때마다 호출됨
    @BeforeEach
    public void setUp() {
        dao.deleteAll();
        user1 = new User("1", "1", "1", Level.BASIC, 1, 1);
        user2 = new User("2", "2", "2", Level.SILVER, 2, 2);
        user3 = new User("3", "3", "3", Level.GOLD, 3, 3);
    }

    @Test
    public void addAndGet() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        User userget1 = dao.get(user1.getId());
        checkSameUser(user1, userget1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(user2, userget2);
    }

    private void checkSameUser(User user1, User user2){
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
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

    @Test
    void getAll() throws SQLException {
        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        assertThat(dao.getAll()).hasSize(3);
    }

    @Test
    void duplicateKey() {
        assertThatThrownBy(() -> {
            dao.add(user1);
            dao.add(user1);
        }).isInstanceOf(DataAccessException.class);
    }

    @Test
    void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("test");
        user1.setPassword("spring5");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUser(user1, user1update);

        User user2update = dao.get(user2.getId());
        checkSameUser(user2, user2update);
    }
}

