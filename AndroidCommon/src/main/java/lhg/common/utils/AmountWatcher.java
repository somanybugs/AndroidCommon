package lhg.common.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class AmountWatcher implements TextWatcher {

    String numbers = "0123456789.";
    public String check(String text) {
        if (TextUtils.isEmpty(text)) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();

        //删除所有非数字字符
        for (char ch : text.toCharArray()) {
            if (numbers.contains(String.valueOf(ch))) {
                sb.append(ch);
            }
        }

        int dot1Index = sb.indexOf(".");
        if (dot1Index >= 0) {
            int dot2Index = sb.indexOf(".", dot1Index + 1);
            //不能超过两个dot
            if (dot2Index >= 0) {
                sb.delete(dot2Index, sb.length());
            }

            //dot后不能超过两位小数
            if (sb.length() - (dot1Index + 1) > 2) {
                sb.delete(dot1Index + 3, sb.length());
            }
        }

        //删除前面的所有0
        while (sb.length() > 0 && sb.charAt(0) == '0') {
            sb.deleteCharAt(0);
        }

        //如果最开始是dot或者长度是0,则添加0
        if (sb.length() == 0 || sb.charAt(0) == '.') {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        String amount = check(text);
        if (!amount.equals(text)) {
            s.replace(0, s.length(), amount);
        }
    }
}
