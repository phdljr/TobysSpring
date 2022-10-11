package tobyspring.user.dao;

import tobyspring.user.domain.User;

public class UserDao {
    public void add(User user) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }
}
