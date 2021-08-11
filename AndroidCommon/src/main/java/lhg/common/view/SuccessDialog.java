package lhg.common.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import lhg.common.R;
import lhg.common.utils.DimenUtils;

import androidx.annotation.NonNull;

/**
 * Created by lhg on 2017/6/16.
 */

public class SuccessDialog extends AlertDialog {

    TextView textView;
    CharSequence title;

    public SuccessDialog(@NonNull Context context, String title) {
        super(context);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.pcm_dialog_success, null);
        textView = view.findViewById(R.id.tvTitle);
        textView.setText(title);
        //设置一个布局
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        int size = (int) (DimenUtils.screenSizeInPixel(getContext()).x * 0.65);
        getWindow().setLayout(size, size);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        if (textView != null) {
            textView.setText(title);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper());

    public static SuccessDialog show(Context context, String title, OnDismissListener dismissListener) {
        if (context instanceof Activity) {
            if (((Activity) context).isDestroyed()) {
                return null;
            }
        }
        SuccessDialog dialog = new SuccessDialog(context, title);
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(() -> dismiss(), 3000);
    }
}
