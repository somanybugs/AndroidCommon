package lhg.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import lhg.common.utils.EmptyActivityLifecycleCallbacks;
import lhg.common.servicemanager.AppServiceManager;
import lhg.common.servicemanager.ServiceManager;
import lhg.common.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhg on 15/9/29.
 */
public class BaseApplication extends Application implements ServiceManager {

    final List<Activity> activities = new ArrayList<>();
    protected static BaseApplication sInstance = null;
    protected boolean isFoceground = false;
    protected AppServiceManager appServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        CrashHandler.getInstance().init(this);
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        appServiceManager = new AppServiceManager(this);
    }

    @Override
    public void onTerminate() {
        sInstance = null;
        super.onTerminate();
    }

    public Activity getTopActivity() {
        if (activities.isEmpty()) {
            return null;
        }
        return activities.get(activities.size()-1);
    }

    public static BaseApplication instance() {
        return  sInstance;
    }

    public void closeAllActivity() {
        closeAllActivityBut(null);
    }

    public void closeAllActivityButTop() {
        if (!activities.isEmpty()) {
            closeAllActivityBut(activities.get(activities.size()-1));
        }
    }


    public void closeAllActivityBut(Activity activity) {
        List<Activity> nList = new ArrayList<>(activities);
        for (Activity a : nList) {
            if (a != activity) {
                a.finish();
            }
        }
    }

    ActivityLifecycleCallbacks activityLifecycleCallbacks = new EmptyActivityLifecycleCallbacks() {

        int startCount = 0;
        int createCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            createCount++;
        }

        @Override
        public void onActivityStarted(Activity activity) {
            startCount++;
            activities.add(activity);
            if (startCount == 1) {
                onGotoForeground(activity);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            startCount--;
            if (startCount <= 0) {
                onGotoBackground(activity);
            }
            activities.remove(activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            createCount--;
        }
    };

    protected void onGotoForeground(Activity activity) {
        Log.i(Utils.getApplicationName(this), "进入前台");
        isFoceground = true;
    }

    protected void onGotoBackground(Activity activity) {
        Log.i(Utils.getApplicationName(this), "进入后台");
        isFoceground = false;
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        return appServiceManager.getService(clazz);
    }
}
