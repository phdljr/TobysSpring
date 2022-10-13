package tobyspring.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tobyspring.user.domain.User;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

public class UserDaoTest {
    @Test
    public void addAndGet() throws SQLException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("111");
        user.setName("hhh");
        user.setPassword("1234");

        dao.add(user);

        User user2 = dao.get("111");

        System.out.println(user.getId() + user2.getId());

        assertThat(user).isEqualTo(user2);
    }
}
