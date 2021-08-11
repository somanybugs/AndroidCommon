package lhg.common.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import lhg.common.R;
import lhg.common.SimplePreference;
import lhg.common.view.InputDialog;

import androidx.appcompat.app.AlertDialog;

public class SettingUI {

    public static View section(Context context) {
        View view = new View(context);
        view.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dip2px(context, 20))
        );
        view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }


    public static View section(Context context, String title) {
        TextView textView = (TextView) View.inflate(context, R.layout.pcm_simple_section, null);
        textView.setText(title);
        return textView;
    }

    public static View switch1(Context context, String title, SimplePreference.Entity_Boolean entity) {
        View view = View.inflate(context, R.layout.pcm_simple_list_item_switch, null);
        ((TextView)view.findViewById(android.R.id.text1)).setText(title);
        Switch switch1 = view.findViewById(R.id.switch1);
        switch1.setChecked(entity.get(false));
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> entity.set(isChecked));
        return view;
    }
    public static View switch1(Context context, String title, boolean checked, CompoundButton.OnCheckedChangeListener l) {
        View view = View.inflate(context, R.layout.pcm_simple_list_item_switch, null);
        ((TextView)view.findViewById(android.R.id.text1)).setText(title);
        Switch switch1 = view.findViewById(R.id.switch1);
        switch1.setChecked(checked);
        switch1.setOnCheckedChangeListener(l);
        return view;
    }

    public static View click1(Context context, String title, View.OnClickListener onClickListener) {
        TextView textView = (TextView) View.inflate(context, R.layout.pcm_simple_list_item_1, null);
        textView.setText(title);
        textView.setOnClickListener(onClickListener);
        return textView;
    }


    public static View click2V(Context context, String title, String value, View.OnClickListener onClickListener) {
        return _2click(context, R.layout.pcm_simple_list_item_2v, title, value, onClickListener);
    }

    public static View click2H(Context context, String title, String value, View.OnClickListener onClickListener) {
        return _2click(context, R.layout.pcm_simple_list_item_2h, title, value, onClickListener);
    }

    public static View input2V(Context context, String title, SimplePreference.Entity entity, boolean notNull) {
        return _2input(context, R.layout.pcm_simple_list_item_2v, title, entity, notNull, true, null);
    }

    public static View input2H(Context context, String title, SimplePreference.Entity entity, boolean notNull) {
        return _2input(context, R.layout.pcm_simple_list_item_2h, title, entity, notNull, true, null);
    }

    public static View input2H(Context context, String title, SimplePreference.Entity entity, boolean notNull, boolean showValue) {
        return _2input(context, R.layout.pcm_simple_list_item_2h, title, entity, notNull, showValue, null);
    }

    public static View input2H(Context context, String title, SimplePreference.Entity entity, boolean notNull, boolean showValue, ClickHook clickHook) {
        return _2input(context, R.layout.pcm_simple_list_item_2h, title, entity, notNull, showValue, clickHook);
    }

    public static View choice2V(Context context, String title, SimplePreference.Entity entity, String[]values, String[]names) {
        return _2choice(context, R.layout.pcm_simple_list_item_2v, title, entity, values, names);
    }
    public static View choice2H(Context context, String title, SimplePreference.Entity entity, String[]values, String[]names) {
        return _2choice(context, R.layout.pcm_simple_list_item_2h, title, entity, values, names);
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    private static View _2input(Context context, int layout, String title, SimplePreference.Entity entity, boolean notNull, boolean showValue, ClickHook clickHook) {
        View view = View.inflate(context, layout, null);
        TextView text1 = view.findViewById(android.R.id.text1);
        TextView text2 = view.findViewById(android.R.id.text2);
        text1.setText(title);
        if (showValue) {
            text2.setText(entity.getRaw());
        }
        view.setOnClickListener(v -> {
            if (clickHook != null && clickHook.handle(v)) {
                return;
            }
            InputDialog inputDialog = new InputDialog(context);
            inputDialog.setTitle(title);
            if (showValue) {
                inputDialog.getEditText().setText(entity.getRaw());
            }

            inputDialog.setOnInputListener(new InputDialog.OnInputListener() {
                @Override
                public void onCancel(InputDialog dialog) {
                    inputDialog.dismiss();
                }

                @Override
                public void onInput(InputDialog dialog, String text) {
                    if (notNull && TextUtils.isEmpty(text)) {
                        Toast.makeText(context, "不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    inputDialog.dismiss();
                    entity.setRaw(text);
                    if (showValue) {
                        text2.setText(text);
                    }
                }
            });
            inputDialog.show();
            int width = Utils.screenSizeInPixel(inputDialog.getContext()).x;
            WindowManager.LayoutParams lp = inputDialog.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = (int) (width * 0.9);//宽高可设置具体大小
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            inputDialog.getWindow().setAttributes(lp);
        });
        return view;
    }

    private static View _2click(Context context, int layout, String title, String value, View.OnClickListener onClickListener) {
        View view = View.inflate(context, layout, null);
        ((TextView)view.findViewById(android.R.id.text1)).setText(title);
        ((TextView)view.findViewById(android.R.id.text2)).setText(value);
        view.setOnClickListener(onClickListener);
        if (onClickListener == null) {
            view.setClickable(false);
        }
        return view;
    }

    private static View _2choice(Context context, int layout, String title, SimplePreference.Entity entity, String[]values, String[]names) {
        View view = View.inflate(context, layout, null);
        TextView text1 = view.findViewById(android.R.id.text1);
        TextView text2 = view.findViewById(android.R.id.text2);
        text1.setText(title);
        String[] finalNames = (names == null ? values : names);
        String row = entity.getRaw();
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(row)) {
                text2.setText(finalNames[i]);
                break;
            }
        }
        view.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context).setItems(finalNames, (d, which) -> {
                entity.setRaw(values[which]);
                text2.setText(finalNames[which]);
            }).show();
            int width = Utils.screenSizeInPixel(dialog.getContext()).x;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = (int) (width * 0.9);//宽高可设置具体大小
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        });
        return view;
    }

    public interface ClickHook {
        boolean handle(View view);
    }

}
