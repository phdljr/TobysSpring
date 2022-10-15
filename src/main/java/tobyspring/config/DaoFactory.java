package tobyspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import tobyspring.dao.UserDaoJdbc;
import tobyspring.service.policy.UserLevelUpgradeNormalPolicy;
import tobyspring.service.policy.UserLevelUpgradePolicy;
import tobyspring.service.UserService;

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
    public UserService userService(){
        UserService userService = new UserService();
        userService.setUserDao(userDao());
        userService.setUserLevelUpgradePolicy(userLevelUpgradePolicy());
        return userService;
    }

    @Bean
    public UserLevelUpgradePolicy userLevelUpgradePolicy(){
        UserLevelUpgradeNormalPolicy userLevelUpgradePolicy = new UserLevelUpgradeNormalPolicy();
        userLevelUpgradePolicy.setUserDao(userDao());
        return userLevelUpgradePolicy;
    }
}
