package learningtest.template;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobyspring.learningtest.template.Calculator;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CalcSumTest {

    private Calculator calculator;
    private String numFilepath;

    @BeforeAll
    public void setUp(){
        this.calculator = new Calculator();
        this.numFilepath = "numbers.txt";
    }

    @Test
    void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilepath)).isEqualTo(10);
    }

    @Test
    void mulOfNumbers() throws IOException {
        assertThat(calculator.calcMultiply(numFilepath)).isEqualTo(24);
    }

    @Test
    void concatenateWord() throws IOException{
        assertThat(calculator.concatenate(numFilepath)).isEqualTo("1234");
    }
}
