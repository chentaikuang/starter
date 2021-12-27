import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class MainTest {
    public static void main(String[] args) {
//        uuid();

//        parseLong();

        appendNo();
    }

    private static void appendNo() {
        while (new Random().nextInt(20) > 5) {
            StringBuilder orderNo = new StringBuilder();
            orderNo.append("PO");
            long curTimeMillis = System.currentTimeMillis();
            System.out.println("curTimeMillis -> " + curTimeMillis);
            orderNo.append(curTimeMillis);
            String userIdString = "00000" + RandomStringUtils.randomNumeric(new Random().nextInt(20));
            orderNo.append(userIdString.substring(userIdString.length() - 4));
            String randomNumber = "00" + new Random().nextInt(100);
            orderNo.append(randomNumber.substring(randomNumber.length() - 2));
            System.out.println("No -> " + orderNo.toString() + "\n");
        }
    }

    private static void parseLong() {
        int nm = 0;
        while (nm++ <= 10) {
            System.out.println(nm + "|" + Long.parseLong(nm + ""));
        }
    }

    private static void uuid() {
        String uuid = UUID.randomUUID().toString();
        //uuid -> c285edbb-e9e0-4092-bb7b-1178e8671e16,length -> 36,8-4-4-4-12
        log.warn("uuid -> {},length -> {}", uuid, uuid.length());

        Arrays.stream(uuid.split("-")).forEach(str -> {
            log.warn("{} -> {}", str, str.length());
//            c285edbb -> 8
//            e9e0 -> 4
//            4092 -> 4
//            bb7b -> 4
//            1178e8671e16 -> 12
        });
    }
}
