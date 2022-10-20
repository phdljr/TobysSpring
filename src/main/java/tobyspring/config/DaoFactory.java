package tobyspring.config;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import tobyspring.dao.UserDaoJdbc;
import tobyspring.service.*;
import tobyspring.service.mail.DummyMailSender;
import tobyspring.service.policy.UserLevelUpgradeNormalPolicy;
import tobyspring.service.policy.UserLevelUpgradePolicy;
import tobyspring.service.proxy.TransactionAdvice;

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

    @Bean
    public ProxyFactoryBean userService(){
        ProxyFactoryBean factoryBean = new ProxyFactoryBean();
        factoryBean.setTarget(userServiceImpl());
        factoryBean.setInterceptorNames("transactionAdvisor");
        return factoryBean;
    }

    @Bean
    public TransactionAdvice transactionAdvice(){
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }

    @Bean
    public NameMatchMethodPointcut transactionPointcut(){
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor(){
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(transactionPointcut());
        advisor.setAdvice(transactionAdvice());
        return advisor;
    }
}
