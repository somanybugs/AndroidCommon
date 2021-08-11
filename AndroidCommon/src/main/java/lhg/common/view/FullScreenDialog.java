package lhg.common.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import lhg.common.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

public class FullScreenDialog extends AppCompatDialog {

    View layout;
    WrapOnDismissListener wrapOnDismissListener = new WrapOnDismissListener();

    private FullScreenDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public FullScreenDialog(Context context, int layoutId) {
        this(context, View.inflate(context, layoutId, null));
    }
    public FullScreenDialog(Context context, View view) {
        this(context);
        this.layout = view;
        super.setOnDismissListener(wrapOnDismissListener);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        wrapOnDismissListener.setListener(listener);
    }

    protected void onDismiss() {

    }

    public <T extends View> T findViewById(int id) {
        return layout.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置一个布局
        setContentView(layout);
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    static class WrapOnDismissListener implements OnDismissListener {

        OnDismissListener wrap;

        public void setListener(OnDismissListener wrap) {
            this.wrap = wrap;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((FullScreenDialog)dialog).onDismiss();
            if (wrap != null) {
                wrap.onDismiss(dialog);
            }
        }
    }

}
