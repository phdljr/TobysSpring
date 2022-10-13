package tobyspring.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionDataSource implements ConnectionMaker{

    private int counter = 0;
    private DataSource realConnectionMaker;

    public CountingConnectionDataSource(DataSource realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws SQLException {
        counter++;
        return realConnectionMaker.getConnection();
    }
}
