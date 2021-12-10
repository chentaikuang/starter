import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaochen.starter.test.entity.User;
import com.xiaochen.starter.test.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ServiceTest extends TestBase {

    @Autowired
    private UserService userService;

    @Test
    public void test() {
//        User user = userService.findUserById(String.valueOf(1));
//        log.info(JSONObject.toJSONString(user));

//        User user2 = new User();
//        user2.setUsername(RandomStringUtils.randomAlphanumeric(2));
//        user2.setPassword(RandomStringUtils.random(2));
//        user2.setId(RandomStringUtils.randomNumeric(2));
//        user2 = userService.findByUsername(user2);
//        log.info(JSONObject.toJSONString(user2));

        User user3 = new User();
        user3.setId("2");
        user3.setUsername("shard1");
        user3 = userService.mergeInfo(user3);
        log.info(JSONObject.toJSONString(user3));
    }

    @Resource    // 自动注入，spring boot会帮我们实例化一个对象
    private JdbcTemplate jdbcTemplate;   // 一个通过JDBC连接数据库的工具类，可以通过这个工具类对数据库进行增删改查

    @Test
    public void jdbcTemplate() {
        String sql = "select * from user where id = 1";
        List<User> userList = jdbcTemplate.query(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getString("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        });

        System.out.println("查询成功");
        for (User s : userList) {
            System.out.println(s);
        }
    }

    @Test
    public void page() {
        PageHelper.startPage(2, 1);
        List<User> result = userService.findAll();
        CommonPage commonPage = new CommonPage(result);
        log.info(commonPage.toString());
    }

    @Data
    private class CommonPage<T> {
        private Integer pageNum;
        private Integer pageSize;
        private Integer totalPage;
        private Integer total;
        private List<T> list;

        public CommonPage(List<User> result) {
            PageInfo page = new PageInfo<>(result);
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.total = Integer.valueOf(String.valueOf(page.getTotal()));
            this.totalPage = page.getPages();
            this.list = page.getList();
            this.list.forEach(user -> log.info(user.toString()));
        }
    }
}
