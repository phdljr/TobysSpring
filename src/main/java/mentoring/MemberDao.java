package mentoring;

import mentoring.connection.ConnectionMaker;
import tobyspring.domain.User;

import java.sql.*;


// CRUD
public class MemberDao {

    private ConnectionMaker connectionMaker;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public int upgradeDelete(Long id) throws SQLException, ClassNotFoundException {
        Connection c = connectionMaker.makeConnection();

        PreparedStatement ps = c.prepareStatement("DELETE FROM MEMBER WHERE ID = ?");
        ps.setLong(1, id);

        int result = ps.executeUpdate();

        return result;
    }

    public Member get(Long id) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
        PreparedStatement ps = c.prepareStatement("SELECT * FROM MEMBER WHERE ID = ?");
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();

        String name = rs.getString(1);
        String password = rs.getString(2);

        Member result = new Member(id, name, password);

        return result;
    }

    public int add(Member member) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");

        PreparedStatement ps = c.prepareStatement("INSERT INTO MEMBER VALUES (?, ?, ?)");
        ps.setLong(1, member.getId());
        ps.setString(2, member.getName());
        ps.setString(3, member.getPassword());

        int result = ps.executeUpdate();

        return result;
    }

    public int update(Member member) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");

        PreparedStatement ps = c.prepareStatement("UPDATE MEMBER SET NAME = ?, PASSWORD = ? WHERE ID = ?");
        ps.setString(1, member.getName());
        ps.setString(2, member.getPassword());
        ps.setLong(3, member.getId());

        int result = ps.executeUpdate();

        return result;
    }

    public int delete(Long id) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection c = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");

        PreparedStatement ps = c.prepareStatement("DELETE FROM MEMBER WHERE ID = ?");
        ps.setLong(1, id);

        int result = ps.executeUpdate();

        return result;
    }
}
