package lhg.common.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import lhg.common.R;
import lhg.common.utils.Utils;
import lhg.common.view.LoadingDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by 刘浩歌 okz@outlook.com on 2015/2/28.
 */
public class BaseActivity extends AppCompatActivity {
    protected Handler			mUiHandler;
    protected boolean			mIsDestroyedApi8	= false;
    protected ProgressDialog mProgressDialog;
    int permissionRequestCode = 900;
    SparseArray<PermissionListener> permissionListeners = new SparseArray<>();
    protected final Map<String, Object> mDataCache = new HashMap<>();

    LoadingDialog loadingDialog;

    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (loadingDialog == null) {
            return;
        }
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public Map<String, Object> getDataCache() {
        return mDataCache;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHandler = new Handler();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    @Override
    protected void onDestroy() {
        hideProgressDialog();
        hideLoadingDialog();
        mDataCache.clear();
//        if (mErrorDialog != null) {
//            mErrorDialog.dismiss();
//        }
//        if (mSuccessDialog != null) {
//            mSuccessDialog.dismiss();
//        }
        mIsDestroyedApi8 = true;
        super.onDestroy();
    }

    protected void removeAllFragments() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();
        if (Utils.listSize(list) > 0) {
            FragmentTransaction transition = fm.beginTransaction();
            for (Fragment f : list) {
                transition.remove(f);
            }
            transition.commitAllowingStateLoss();
        }
    }

    //判断缺少权限
    public boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED;
    }

    //PackageManager.PERMISSION_DENIED
    public void requestPermissions(PermissionListener permissionListener, String... permissions) {
        permissionListeners.put(permissionRequestCode, permissionListener);
        ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        permissionRequestCode++;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionListener l = permissionListeners.get(requestCode);
        if (l != null) {
            permissionListeners.remove(requestCode);
            l.onGranted(permissions, grantResults);
        }
    }

    public interface PermissionListener {
        void onGranted(String[] permissions, int[] grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPrevArrowOnActionBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowHomeEnabled(true);
    }
    public void hidePrevArrowOnActionBar() {
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//		getActionBar().setDisplayShowHomeEnabled(false);
    }

    public boolean isDestroyedApi8() {
        return mIsDestroyedApi8 || isFinishing();
    }

    public void post2ui(Runnable callback) {
        post2ui(callback, 0);
    }

    public void post2ui(final Runnable callback, long delay) {
        if (callback == null) {
            return;
        }
        Runnable uiRun = () -> {
            if (isDestroyed() || isDestroyedApi8()) {
                return;
            }
            callback.run();
        };
        if (delay <= 0) {
            mUiHandler.post(uiRun);
        } else {
            mUiHandler.postDelayed(uiRun, delay);
        }
    }

    public BaseActivity getActivity() {
        return this;
    }

    public void showToast(int textId) {
        showToast(getString(textId));
    }

    public void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show());
    }

    public void showProgressDialog(int textId) {
        showProgressDialog(getString(textId));
    }

    public void showProgressDialog(String text) {
        showProgressDialog(text, null);
    }

    public void showProgressDialog(final String text, final OnCancelListener cancelListener) {
        if (isDestroyedApi8()) {
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
            runOnUiThread(() -> showProgressDialog(text, cancelListener));
        }
    }

    public void hideProgressDialog() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } else {
            runOnUiThread(() -> hideProgressDialog());
        }
    }


    public void showAlert(String title, String message) {
        if (isDestroyedApi8()) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            ;
        } else {
            runOnUiThread(() -> showAlert(title, message));
        }
    }


    /////////////////
    private Menu optionsMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(optionsMenu = menu);
    }

    public boolean collapseActionView() {
        if (optionsMenu != null) {
            for (int i = 0, size = optionsMenu.size(); i < size; i++) {
                MenuItem item = optionsMenu.getItem(i);
                if (item.isActionViewExpanded()) {
                    item.getActionView().clearFocus();
                    item.collapseActionView();
                    return true;
                }
            }
        }
        return false;
    }

}
