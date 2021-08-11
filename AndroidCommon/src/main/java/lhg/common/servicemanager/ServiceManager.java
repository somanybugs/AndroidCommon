package lhg.common.servicemanager;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/1/15 9:58
 * Note:
 */
public interface ServiceManager {
    <T> T getService(Class<T> clazz);
}
