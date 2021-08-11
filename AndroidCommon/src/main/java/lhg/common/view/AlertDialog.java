package lhg.common.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lhg.common.R;

import androidx.annotation.NonNull;

public class AlertDialog extends android.app.AlertDialog {

    private Button btn_cancel, btn_ok;
    private OnClickListener okListener, cancelListener;
    private TextView tv_title;

    public AlertDialog(@NonNull Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.pcm_dialog_alert, null);
        tv_title = view.findViewById(R.id.tvTitle);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> onClickCancel());
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> onClickOk());

        setView(view);
        setCancelable(true);
    }

    public void setBtnOk(String title) {
        btn_ok.setText(title);
    }

    public void setBtnCancel(String title) {
        btn_cancel.setText(title);
    }

    @Override
    public void setTitle(CharSequence title) {
        tv_title.setText(title);
    }


    public void setOnOKListener(OnClickListener okListener) {
        this.okListener = okListener;
    }

    public void setOnCancelListener(OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    private void onClickOk() {
        if (okListener != null) {
            okListener.onClick();
        }
        dismiss();
    }

    protected void onClickCancel() {
        if (cancelListener != null) {
            cancelListener.onClick();
        }
        dismiss();
    }

    public interface OnClickListener {
        void onClick();
    }

}
