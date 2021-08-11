package lhg.common.servicemanager;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2021/1/14 21:06
 * Note:
 */
public abstract class ModuleServiceManager implements ServiceManager {
    private final Map<String, ServiceHolder> serviceHolders = new HashMap();
    private Application app;
    private WeakReference<AppServiceManager> appServiceManager;

    public void init(Application app, AppServiceManager appServiceManager) {
        this.app = app;
        this.appServiceManager = new WeakReference<>(appServiceManager);
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        ServiceHolder holder = serviceHolders.get(clazz.getName());
        if (holder != null) {
            return (T) holder.getService(app, appServiceManager.get());
        }
        return null;
    }

    protected final void bindServiceBuilder(Class serviceClazz, boolean single, ServiceBuilder builder) {
        serviceHolders.put(serviceClazz.getName(), new ServiceHolder(single, builder));
    }

    protected abstract void onBindServiceBuilders();

    private static class ServiceHolder {
        private boolean single;
        private ServiceBuilder builder;
        private Object service;

        public ServiceHolder(boolean single, ServiceBuilder builder) {
            this.single = single;
            this.builder = builder;
        }

        Object getService(Context context, ServiceManager appServiceManager) {
            if (single) {
                if (service == null) {
                    synchronized (this) {
                        if (service == null) {
                            service = builder.create(context, appServiceManager);
                        }
                    }
                }
                return service;
            } else {
                return builder.create(context, appServiceManager);
            }
        }
    }

    public interface ServiceBuilder {
        <T> T create(Context context, ServiceManager appServiceManager);
    }
}
