import com.xiaochen.starter.test.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisTest extends TestBase {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void zset() {
        String zsetKey = "zset_test";
        redisUtil.clear(zsetKey);
        int num = 10;
        while (0 < num--) {
            redisUtil.zsetAdd(zsetKey, RandomStringUtils.randomNumeric(2), new Random().nextInt(100));
        }
        log.info("zset size -> {}", redisUtil.zsetSize(zsetKey));
        Set zset = redisUtil.range(zsetKey, 0, -1);
        log.info("zset -> {}", zset);

        Set<ZSetOperations.TypedTuple> withScores = redisUtil.rangeWithScores(zsetKey, 0, -1);
        withScores.forEach(score -> {
            log.info("rangeWithScores -> {}:{}", score.getScore(), score.getValue());
        });

        log.info("rangeByScore -> {}", redisUtil.rangeByScore(zsetKey, 60, 100));
    }

    @Test
    public void multiSet() {
        final String multiSetKey = "test:multiset:";
        Map map = new HashMap<>();
        int nm = 2;
        while (nm-- > 0) {
            map.put(multiSetKey + nm, nm);
        }

        Set keys = redisTemplate.keys(multiSetKey + "*");
        log.info("keys -> {}", keys);
        redisTemplate.delete(keys);

//        redisTemplate.opsForValue().multiSet(map);

        map.forEach((k, v) -> {
            map.put(k, v.toString() + 2);
        });
        map.put(multiSetKey + RandomStringUtils.randomNumeric(2), RandomStringUtils.randomNumeric(2));

        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();

        Boolean flag = redisTemplate.opsForValue().multiSetIfAbsent(map);
        log.info("flag -> {},map -> {}", flag, map);
        //flag -> false,map -> {multiSet:0=02, multiSet:1=12, multiSet:32=23}
        // 只要存在key则都Set不成功

        expireTime(map);

        redisTemplate.exec();
    }

    private void expireTime(Map map) {
        map.forEach((k, v) -> {
            redisTemplate.expire(k, 10, TimeUnit.MINUTES);
        });
    }
}
