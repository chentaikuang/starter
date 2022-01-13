import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class TinyUrl {
    public static final char[] array =
            {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd',
                    'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
                    'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D',
                    'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M'
            };

    public static Map<Character, Integer> charValueMap = new HashMap<Character, Integer>();

    //初始化map
    static {
        for (int i = 0; i < array.length; i++) charValueMap.put(array[i], i);
    }

    /**
     * 把数字转换成相对应的进制,目前支持(2-62)进制
     *
     * @param number
     * @param decimal
     * @return
     */
    public static String numberConvertToDecimal(long number, int decimal) {
        StringBuilder builder = new StringBuilder();
        while (number != 0) {
            builder.append(array[(int) (number - (number / decimal) * decimal)]);
            number /= decimal;
        }
        return builder.reverse().toString();
    }

    /**
     * 把进制字符串转换成相应的数字
     *
     * @param decimalStr
     * @param decimal
     * @return
     */
    public static long decimalConvertToNumber(String decimalStr, int decimal) {
        long sum = 0;
        long multiple = 1;
        char[] chars = decimalStr.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) {
            char c = chars[i];
            sum += charValueMap.get(c) * multiple;
            multiple *= decimal;
        }
        return sum;
    }

    /**
     * 1、短链需要保证唯一，所以需要对应的生成一个唯一的ID号（数字）
     * 2、转换ID号为62进制
     * 3、转换后的ID对应数组字符（数字10+大小写字母52）
     * 4、组合成为唯一字符串：短链
     * 5、将该短链和长链绑定映射关系，完成长链转换短链
     *
     * @param args
     */
    public static void main(String[] args) {
        IntStream.range(1, 10).forEach(nm -> {
            long number = Long.MAX_VALUE - nm;
            String tinUrl = numberConvertToDecimal(number, 62);
            long toNumber = decimalConvertToNumber(tinUrl, 62);
            System.out.println(nm + ".number->" + number + ",tinUrl->" + tinUrl + ",toNumber->" + toNumber + "\n");
        });
    }
}
