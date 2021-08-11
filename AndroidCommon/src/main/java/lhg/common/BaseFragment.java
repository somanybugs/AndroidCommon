package lhg.common;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.View;

import lhg.common.utils.ToastUtil;

import java.util.Stack;

/**
 * Created by 刘浩歌 on 2015/7/15.
 */
public class BaseFragment extends Fragment implements OnBackPressedCallback{

    Stack<Fragment> fragments = new Stack<>();

    protected Handler uiHandler;
    protected ProgressDialog mProgressDialog;
    boolean isUserVisibleReal = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHandler = new Handler();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void runOnUiThread(final Runnable run) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            run.run();
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null || isDetached()) {
                       return;
                    }
                    run.run();
                }
            });
        }
    }

    public boolean isRealVisible() {
        return isAdded() && isResumed() && getUserVisibleHint() && !isHidden() && !isDetached();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRealVisible()) {
            postUserVisible(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        postUserVisible(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            postUserVisible(false);
        } else if (isRealVisible()) {
            postUserVisible(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            postUserVisible(false);
        } else if (isRealVisible()) {
            postUserVisible(true);
        }
    }

    private void postUserVisible(boolean isVisible) {
        if (isUserVisibleReal && !isVisible) {
            onUserInVisible();
        }
        if (!isUserVisibleReal && isVisible) {
            onUserVisible();
        }
        isUserVisibleReal = isVisible;
    }

    protected void onUserInVisible() {

    }

    protected void onUserVisible() {

    }

    public <T extends View> T findViewById(int id) {
        return getView().findViewById(id);
    }


    public void showProgressDialog(int textId) {
        showProgressDialog(getString(textId));
    }

    public void showProgressDialog(String text) {
        showProgressDialog(text, null);
    }

    public void showProgressDialog(final String text, final DialogInterface.OnCancelListener cancelListener) {
        if (getActivity() == null) {
            return;
        }

        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setCanceledOnTouchOutside(false);
            }
            mProgressDialog.setMessage(text);
            mProgressDialog.setCancelable(cancelListener != null);
            mProgressDialog.setOnCancelListener(cancelListener);
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    showProgressDialog(text, cancelListener);
                }
            });
        }
    }

    public void hideProgressDialog() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } else {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    hideProgressDialog();
                }
            });
        }
    }


    public void showAlert(String title, String message) {
        if (getActivity() == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            runOnUiThread(() -> showAlert(title, message));
        }
    }


    public void onDestroy() {
        hideProgressDialog();
        super.onDestroy();
    }

    public void showToast(int textId) {
        showToast(getString(textId));
    }

    public void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(getActivity(), text);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
