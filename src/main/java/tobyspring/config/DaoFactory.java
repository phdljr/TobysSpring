package tobyspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import tobyspring.dao.UserDaoJdbc;
import tobyspring.service.UserServiceImpl;
import tobyspring.service.mail.DummyMailSender;
import tobyspring.service.policy.UserLevelUpgradeNormalPolicy;
import tobyspring.service.policy.UserLevelUpgradePolicy;
import tobyspring.service.policy.UserService;
import tobyspring.service.policy.UserServiceTx;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDaoJdbc userDao(){
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/tobyspring");
        dataSource.setUsername("root");
        dataSource.setPassword("19980703");
        return dataSource;
    }

    @Bean
    public UserServiceTx userService(){
        UserServiceTx userService = new UserServiceTx();
        userService.setUserService(userServiceImpl());
        userService.setTransactionManager(transactionManager());
        return userService;
    }

    @Bean
    public UserServiceImpl userServiceImpl(){
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSender());
        userService.setUserLevelUpgradePolicy(new UserLevelUpgradeNormalPolicy());
        return userService;
    }

    @Bean
    public UserLevelUpgradePolicy userLevelUpgradePolicy(){
        UserLevelUpgradeNormalPolicy userLevelUpgradePolicy = new UserLevelUpgradeNormalPolicy();
        userLevelUpgradePolicy.setUserDao(userDao());
        return userLevelUpgradePolicy;
    }

    @Bean
    public PlatformTransactionManager transactionManager(){
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }

    @Bean
    public MailSender mailSender(){
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("mail.server.com");

        DummyMailSender mailSender = new DummyMailSender();
        return mailSender;
    }
}
