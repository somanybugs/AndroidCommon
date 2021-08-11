package lhg.common.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class ContextActivityRequest {
    private V4Fragment v4Fragment;
    private AppFragment appFragment;
    private int requestCode = 0;

    public ContextActivityRequest(Context context, String tag) {
        Activity activity = findActivity(context);
        if (activity instanceof FragmentActivity) {
            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
            v4Fragment = (V4Fragment) fragmentManager.findFragmentByTag(tag);
            if (v4Fragment == null) {
                fragmentManager.beginTransaction().add(v4Fragment = new V4Fragment(), tag)
                        .commitAllowingStateLoss();
            }
            requestCode = ++v4Fragment.requestCodeCount;
        } else {
            android.app.FragmentManager fragmentManager = activity.getFragmentManager();
            appFragment = (AppFragment) fragmentManager.findFragmentByTag(tag);
            if (appFragment == null) {
                fragmentManager.beginTransaction().add(appFragment = new AppFragment(), tag)
                        .commitAllowingStateLoss();
            }
            requestCode = ++appFragment.requestCodeCount;
        }
    }

    public void startActivityForResult(Intent intent, Callback callback) {
        if (v4Fragment != null) {
            v4Fragment.callback = callback;
            v4Fragment.startActivityForResult(intent, requestCode);
        }
        if (appFragment != null) {
            appFragment.callback = callback;
            appFragment.startActivityForResult(intent, requestCode);
        }
    }


    private static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    public interface Callback {
        void onActivityResult(int resultCode, Intent data);
    }

    public static class V4Fragment extends Fragment {
        int requestCodeCount = 0;
        Callback callback;
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (callback != null) {
                callback.onActivityResult(resultCode, data);
            }
            callback = null;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                requestCodeCount = savedInstanceState.getInt("requestCodeCount");
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("requestCodeCount", requestCodeCount);
        }
    }

    public static class AppFragment extends android.app.Fragment {
        int requestCodeCount = 0;
        Callback callback;
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (callback != null) {
                callback.onActivityResult(resultCode, data);
            }
            callback = null;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            if (savedInstanceState != null) {
                requestCodeCount = savedInstanceState.getInt("requestCodeCount");
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("requestCodeCount", requestCodeCount);
        }
    }

}
