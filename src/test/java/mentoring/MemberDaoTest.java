package mentoring;

import mentoring.connection.NConnectionMaker;
import mentoring.connection.TestConnectionMaker;
import mentoring.factory.DaoFactory;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;

class MemberDaoTest {
    @Test
    void getMember() throws SQLException, ClassNotFoundException {

        // Given(조건)
        MemberDao dao = new MemberDao();
        dao.setConnectionMaker(new TestConnectionMaker());

        Member member = new Member(1L, "test", "test");
        dao.add(member);

        // When(상황)
        Member getMember = dao.get(1L);

        // Then(결과)
        assertThat(member.getName()).isEqualTo(getMember.getName());
    }

    @Test
    void addMember() throws SQLException, ClassNotFoundException {
        // Given(조건)
        MemberDao dao = new MemberDao();
        Member member = new Member(1L, "test", "test");

        // When(상황)
        int result = dao.add(member);

        // Then(결과)
        assertThat(result).isEqualTo(1);
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMember() {
    }
}