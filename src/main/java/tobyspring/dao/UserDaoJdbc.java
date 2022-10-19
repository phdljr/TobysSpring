package tobyspring.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import tobyspring.domain.Level;
import tobyspring.domain.User;

import javax.sql.DataSource;
import java.util.List;

public class UserDaoJdbc  implements UserDao{

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setLevel(Level.valueOf(rs.getInt("level")));
        user.setLogin(rs.getInt("login"));
        user.setRecommend(rs.getInt("recommend"));
        user.setEmail(rs.getString("email"));
        return user;
    };

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        jdbcTemplate.update("INSERT INTO USERS VALUES(?, ?, ?, ?, ?, ?, ?)"
                , user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail());
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", userMapper, id);
    }

    public List<User> getAll(){
        return jdbcTemplate.query("SELECT * FROM USERS", userMapper);
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM USERS");
    }

    public Integer getCount() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
        return count;
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("UPDATE USERS SET name=?, password=?, level=?, login=?, recommend=?, email=? where id=?"
                , user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
    }
}

