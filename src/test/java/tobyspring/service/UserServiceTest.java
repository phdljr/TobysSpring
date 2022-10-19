package tobyspring.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
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
import tobyspring.service.policy.UserLevelUpgradeNormalPolicy;
import tobyspring.service.policy.UserLevelUpgradePolicy;
import tobyspring.service.policy.UserService;
import tobyspring.service.policy.UserServiceTx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static tobyspring.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static tobyspring.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringJUnitConfig(classes = DaoFactory.class)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
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

    static class MockUserDao implements UserDao{

        private List<User> users;
        private List<User> updated = new ArrayList<>();

        public MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated(){
            return updated;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public List<User> getAll() {
            return users;
        }

        @Override
        public User get(String id) { throw new UnsupportedOperationException();}
        @Override
        public void deleteAll() { throw new UnsupportedOperationException();}
        @Override
        public Integer getCount() { throw new UnsupportedOperationException();}
        @Override
        public void add(User user) { throw new UnsupportedOperationException();}
    }

    @Test
    void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserLevelUpgradePolicy(new UserLevelUpgradeNormalPolicy());

        MockUserDao mockUserDao = new MockUserDao(users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "2", Level.SILVER);
        checkUserAndLevel(updated.get(1), "4", Level.GOLD);

        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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

    static class TestUserService extends UserServiceImpl{
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
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setUserService(testUserService);
        txUserService.setTransactionManager(transactionManager);

        userDao.deleteAll();
        users.stream().forEach(user -> userDao.add(user));

        try{
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e){

        }

        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    void mockUpgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        // 목 객체 생성. 생성만 해두면 이 객체는 아무런 역할도 하지 않는다.
        UserDao mockUserDao = mock(UserDao.class);
        // 리턴 값 설정(필요한 경우)
        when(mockUserDao.getAll()).thenReturn(this.users);
        // 목 객체 DI
        userServiceImpl.setUserDao(mockUserDao);

        // 목 객체 생성
        MailSender mockMailSender = mock(MailSender.class);
        // 목 객체 DI
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        // update가 두 번 실행됐는지 확인하며, 파라미터의 내용은 무시한다.
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        // update가 호출됐을 때, 파라미터로 users.get(1)을 넣어 호출한 적이 있는지 확인한다.
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        // update가 호출됐을 때, 파라미터로 users.get(3)을 넣어 호출한 적이 있는지 확인한다.
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // 파라미터로 무엇을 사용했는지 저장해주는 역할을 맡는다.
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }
}