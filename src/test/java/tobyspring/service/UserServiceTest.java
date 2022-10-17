package tobyspring.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import tobyspring.config.DaoFactory;
import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;
import tobyspring.exception.TestUserServiceException;
import tobyspring.service.policy.UserLevelUpgradePolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static tobyspring.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static tobyspring.service.UserService.MIN_RECCOMEND_FOR_GOLD;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = DaoFactory.class)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private MailSender mailSender;

    private List<User> users;

    @BeforeAll
    public void setUp(){
        users = Arrays.asList(
                new User("1", "1", "1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "111"),
                new User("2", "2", "2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "222"),
                new User("3", "3", "3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "333"),
                new User("4", "4", "4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "444"),
                new User("5", "5", "5", Level.GOLD, 100, Integer.MAX_VALUE, "555")
        );
    }

    @Test
    void bean() {
        assertThat(userService).isNotNull();
    }

    static class MockMailSender implements MailSender {

        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... mailMessage) throws MailException {

        }
    }

    @Test
    @DirtiesContext
    void upgradeLevels() throws Exception {
        userDao.deleteAll();
        users.stream().forEach(user -> userDao.add(user));

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    private void checkLevelUpgraded(User user, boolean upgraded){
        User userUpdate = userDao.get(user.getId());
        if(upgraded){
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        }
        else{
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
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

    static class TestUserService extends UserService{
        private String id;
        private TestUserService(String id){
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    @Test
    void upgradeAllOrNothing() throws Exception{
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
        testUserService.setTransactionManager(transactionManager);
        testUserService.setMailSender(mailSender);

        userDao.deleteAll();
        users.stream().forEach(user -> userDao.add(user));

        try{
            testUserService.upgradeLevels();
//            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e){

        }

        checkLevelUpgraded(users.get(1), false);
    }
}