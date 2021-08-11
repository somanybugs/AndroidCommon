package lhg.common.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import lhg.common.R;
import lhg.common.utils.Utils;

public class InputDialog extends AlertDialog {

    private EditText editText;
    private OnInputListener onInputListener;

    public InputDialog(Context context) {
        super(context);
        View view = View.inflate(getContext(), R.layout.pcm_dialog_input, null);
        editText = view.findViewById(R.id.editText);
        setView(view);
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), (dialog, which) -> {
            if (editText.length() == 0) {
                onClickCancel();
            } else {
                onClickOk();
            }
        });
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), (dialog, which) -> onClickCancel());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void show() {
        super.show();
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Utils.showKeyboard(editText);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        editText.requestFocus();
    }

    public InputDialog setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
        return this;
    }

    public EditText getEditText() {
        return editText;
    }

    private void onClickOk() {
        if (onInputListener != null) {
            onInputListener.onInput(this, editText.getText().toString());
        }
    }

    protected void onClickCancel() {
        if (onInputListener != null) {
            onInputListener.onCancel(this);
        }
    }

    public interface OnInputListener {
        void onCancel(InputDialog dialog);
        void onInput(InputDialog dialog, String text);
    }

    public static class SimpleOnInputListener implements OnInputListener {
        public void onCancel(InputDialog dialog) {

        }
        public void onInput(InputDialog dialog, String text) {

        }
    }

}
