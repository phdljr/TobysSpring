package exception;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestFileInputStream extends FileInputStream {
    public TestFileInputStream(String name) throws FileNotFoundException {
        super(name);
    }

    @Override
    public void close() throws IOException {
        super.close();
        System.out.println("close");
    }
}
