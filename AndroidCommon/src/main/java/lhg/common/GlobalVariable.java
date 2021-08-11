package lhg.common;

import java.util.HashMap;

public class GlobalVariable {

    HashMap<String, Object> map = new HashMap();
    static GlobalVariable sInstance;

    private GlobalVariable() {

    }

    public static GlobalVariable instance() {
        if (sInstance == null) {
            synchronized (GlobalVariable.class) {
                if (sInstance == null) {
                    sInstance = new GlobalVariable();
                }
            }
        }
        return sInstance;
    }

    public static synchronized <T> T get(String key) {
        return (T) instance().map.get(key);
    }

    public static synchronized void put(String key, Object val) {
        instance().map.put(key, val);
    }

    public static synchronized void remove(String key) {
        instance().map.remove(key);
    }
}
