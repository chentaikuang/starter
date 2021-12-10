import com.xiaochen.starter.test.TestApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest(classes = TestApplication.class)
public class TestBase {

    @Before
    public void before() {
        log.info("before test");
    }
}
