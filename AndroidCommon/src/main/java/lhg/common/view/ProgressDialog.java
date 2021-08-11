package lhg.common.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import lhg.common.R;
import lhg.common.utils.DimenUtils;

import androidx.appcompat.app.AppCompatDialog;

/**
 * Created by lhg on 2017/6/14.
 */

public class ProgressDialog extends AppCompatDialog {

    TextView textView;
    CharSequence title;

    public ProgressDialog( Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.pcm_dialog_progress, null);
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
}
