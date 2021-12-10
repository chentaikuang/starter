import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class MainTest {
    public static void main(String[] args) {
//        uuid();

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
