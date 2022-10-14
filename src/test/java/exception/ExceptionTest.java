package exception;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ExceptionTest {
    @Test
    void checkExceptionInTryResourceWith() {
        try(InputStream in = new TestFileInputStream("README.md");){
            throw new IOException("강제로 예외 발생");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
