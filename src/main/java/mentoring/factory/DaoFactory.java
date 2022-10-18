package mentoring.factory;

import mentoring.MemberDao;
import mentoring.connection.ConnectionMaker;
import mentoring.connection.TestConnectionMaker;

public class DaoFactory {
    public MemberDao memberDao(){
        MemberDao memberDao = new MemberDao();
        memberDao.setConnectionMaker(connectionMaker());
        return memberDao;
    }

    public ConnectionMaker connectionMaker(){
        ConnectionMaker connectionMaker = new TestConnectionMaker();
        return connectionMaker;
    }
}
