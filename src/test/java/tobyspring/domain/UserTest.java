package tobyspring.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    User user;

    @BeforeEach
    public void setUp(){
        user = new User();
    }

    @Test
    void upgradeLevel() {
        Level[] levels = Level.values();
        for(Level level: levels){
            if(level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    void cannotUpgradeLevel() {
        assertThatThrownBy(() -> {
            Level[] levels = Level.values();
            for(Level level: levels) {
                if (level.nextLevel() != null) continue;
                user.setLevel(level);
            }
        }).isInstanceOf(IllegalStateException.class);
    }
}