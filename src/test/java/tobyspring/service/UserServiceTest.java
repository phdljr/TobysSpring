package tobyspring.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobyspring.config.DaoFactory;
import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    private List<User> users;

    @BeforeAll
    public void setUp(){
        users = Arrays.asList(
                new User("1", "1", "1", Level.BASIC, 49, 0),
                new User("2", "2", "2", Level.BASIC, 50, 0),
                new User("3", "3", "3", Level.SILVER, 60, 29),
                new User("4", "4", "4", Level.SILVER, 60, 30),
                new User("5", "5", "5", Level.GOLD, 100, 100)
        );
    }

    @Test
    void bean() {
        assertThat(userService).isNotNull();
    }

    @Test
    void upgradeLevels(){
        userDao.deleteAll();
        users.stream().forEach(user -> userDao.add(user));

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel){
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4); // GOLD
        User userWithoutLevel = users.get(0); // NULL
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel()); // 둘 다 GOLD 여야 함
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel()); // 둘 다 BASIC 이여야 함
    }
}