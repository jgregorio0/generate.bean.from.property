package dev.jgregorio.test.generate.bean.from.property;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ConfigTest {

    @Autowired
    @Qualifier("test1")
    TestProp testProp1;

    @Test
    public void test() {
        assertThat(testProp1).isNotNull();
        assertThat(testProp1.getId()).isEqualTo("test1");
        assertThat(testProp1.getProperty()).isEqualTo("property1");
    }
}