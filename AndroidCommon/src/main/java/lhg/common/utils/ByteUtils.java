package lhg.common.utils;

public class ByteUtils {

    public static boolean begin(byte[] source, byte[] target) {
        if (source == null || source.length == 0
                || target == null || target.length == 0
                || target.length > source.length) {
            return false;
        }
        for (int i = 0; i < target.length; i++) {
            if (source[i] != target[i]) {
                return false;
            }
        }
        return true;
    }

//    public static boolean contains(byte[] source, byte[] target) {
//        if (source == null || source.length == 0
//                || target == null || target.length == 0
//                || target.length > source.length) {
//            return false;
//        }
//        for (int i = 0; i < source.length; i++) {
//            if (source[i] != target[i]) {
//                return false;
//            }
//        }
//        return true;
//    }

    public static boolean equals(byte[] src, int srcOffset, byte[] des, int desOffset, int len) {
        if (src == null || src.length == 0
                || des == null || des.length == 0) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (src[i+srcOffset] != des[i+desOffset]) {
                return false;
            }
        }
        return true;
    }

    public static boolean equals(byte[] src, byte[] des) {
        return equals(src, 0, des, 0, src.length);
    }

}
