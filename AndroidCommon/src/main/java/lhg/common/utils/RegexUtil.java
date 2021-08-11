package lhg.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 刘浩歌 on 2015/7/20.
 */
public class RegexUtil {
    private static Map<String, Pattern> patterns ;

    public static Pattern pattern(String text) {
        if (patterns == null) {
            patterns = new HashMap<>();
        }
        Pattern pattern = patterns.get(text);
        if (pattern == null) {
            pattern = Pattern.compile(text);
            patterns.put(text, pattern);
        }
        return pattern;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     */
    public static boolean isEmail(String text) {
        if (text == null || text.trim().length() == 0)
            return false;
        return pattern("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*").matcher(text).matches();
    }

    /**
     * 判断是不是一个合法的手机号码
     */
    public static boolean isPhone(String text) {
        if (text == null || text.trim().length() == 0)
            return false;
        return pattern("^1\\d{10}$").matcher(text).matches();
    }

}
