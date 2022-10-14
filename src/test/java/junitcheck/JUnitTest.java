package junitcheck;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

public class JUnitTest {

    static Set<JUnitTest> map = new HashSet<>();

    @Test
    void test1() {
        assertThat(map).doesNotContain(this);
        map.add(this);
    }

    @Test
    void test2() {
        assertThat(map).doesNotContain(this);
        map.add(this);
    }

}
