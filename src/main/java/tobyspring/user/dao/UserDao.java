package tobyspring.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import tobyspring.user.dao.jdbcstrategy.AddStatement;
import tobyspring.user.dao.jdbcstrategy.DeleteAllStatement;
import tobyspring.user.dao.jdbcstrategy.StatementStrategy;
import tobyspring.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {

    private JdbcContext jdbcContext;
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.jdbcContext = new JdbcContext();
        this.jdbcContext.setDataSource(dataSource);
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException {
        jdbcContext.executeAdd("INSERT INTO USERS VALUES(?, ?, ?)", user);
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if(user == null)
            throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() throws SQLException {
        jdbcContext.executeDeleteAll("DELETE FROM USERS");
    }

    public int getCount() throws SQLException {
        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM USERS");
            ResultSet rs = ps.executeQuery();) {

            rs.next();
            int count = rs.getInt(1);

            return count;
        }
    }
}

