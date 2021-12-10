import com.xiaochen.starter.dingtalk.service.DingtalkService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DingtalkTest extends TestBase {

    @Autowired(required = false)
    private DingtalkService dingtalkService;

    @Test
    public void send() {

//        dingtalkService.send("简单发送消息测试", "我是一条内容消息");
//        dingtalkService.send("会员注册失败告警", "[no_key]手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败,手机号保存失败", new RuntimeException());
//        dingtalkService.send("系统运行错误告警", "[has_key]CUP资源告警CUP资源告警CUP资源告警数据库资源不够.数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够",
//                new IllegalArgumentException(), "test-dingtalk");
//        dingtalkService.send("系统运行错误告警", "[has_key]CUP资源告警CUP资源告警CUP资源告警数据库资源不够.数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够数据库资源不够",
//                new RuntimeException(RandomStringUtils.randomAlphanumeric(8)), "XXX");
//        dingtalkService.send("BCP发送消息测试", "我是一条内容消息", "bcp");
    }
}
