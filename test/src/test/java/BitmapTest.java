import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chentaikuang
 * @date 2022/5/16
 * @desc
 */
@Slf4j
public class BitmapTest extends TestBase {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void bitmap() {
        int num = 10;
        AtomicInteger uid = new AtomicInteger(10000);
        LocalDate date = LocalDate.now();
        String dateText = date.format(DateTimeFormatter.ISO_DATE);
        Boolean bit = false;
        while (num-- > 0) {
            bit = RandomUtils.nextBoolean();
            redisTemplate.opsForValue().setBit(dateText, uid.get(), bit);
            log.info("uid->{},sBit->{},gBit->{}", uid.get(), bit, redisTemplate.opsForValue().getBit(dateText, uid.get()));
        }

    }

}
