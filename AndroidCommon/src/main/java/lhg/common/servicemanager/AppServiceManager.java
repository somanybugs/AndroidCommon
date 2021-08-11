package lhg.common.servicemanager;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/1/15 9:58
 * Note:
 */
public class AppServiceManager implements ServiceManager {

    private final Application app;
    private List<ModuleServiceManager> moduleManagers = new ArrayList<>();

    public AppServiceManager(Application app) {
        this.app = app;
    }

    public void addModules(Class<? extends ModuleServiceManager>...clazzs) {
        if (clazzs != null) {
            for (Class cls : clazzs) {
                try {
                    ModuleServiceManager m = (ModuleServiceManager) cls.newInstance();
                    m.init(app, this);
                    m.onBindServiceBuilders();
                    moduleManagers.add(m);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    @Override
    public <T> T getService(Class<T> clazz) {
        for (ModuleServiceManager m : moduleManagers) {
            Object service = m.getService(clazz);
            if (service != null) {
                return (T) service;
            }
        }
        return null;
    }
}
