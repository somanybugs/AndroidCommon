package lhg.common.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import lhg.common.R;
import lhg.common.view.EntityLayout;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;

public class EntityUI {

    public static abstract class FieldView {
        public EntityLayout.ValueWrapper valueWrapper;
        public String name;
        public View view;
        public Object entity;
        public Field field;
        public boolean editable;

        public View createView(Context context) {
            this.view = onCreateView(context);
            return view;
        }

        public abstract View onCreateView(Context context);
    }

    public static class AutoInputViewH extends InputView {
        AutoCompleteTextView autoCompleteTextView;
        @Override
        public View onCreateView(Context context) {
            View view = _2input(context, R.layout.pcm_entity_edit_item_2h_auto, name, entity, field, valueWrapper, editable);
            autoCompleteTextView = view.findViewById(android.R.id.text2);
            initAutoComplete(autoCompleteTextView);
            return view;
        }

        String key() {
            return entity.getClass().getSimpleName() + "-" + field.getName();
        }

        public void saveHistory() {
            SharedPreferences sp = view.getContext().getSharedPreferences("entity_auto_list", Context.MODE_PRIVATE);
            Set<String> set = sp.getStringSet(key(), new HashSet<>());
            String text = autoCompleteTextView.getText().toString();
            if (TextUtils.isEmpty(text) || set.contains(text)) {
                return;
            }
            Set<String> set2 = new HashSet<>(set);
            set2.add(text);
            sp.edit().putStringSet(key(), set2).commit();
        }

        private void initAutoComplete(AutoCompleteTextView autoCompleteTextView) {
            Context context = autoCompleteTextView.getContext();
            SharedPreferences sp = context.getSharedPreferences("entity_auto_list", Context.MODE_PRIVATE);
            Set<String> set = sp.getStringSet(key(), null);
            if (set == null || set.isEmpty()) {
                return;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(context,
                    android.R.layout.simple_dropdown_item_1line, set.toArray(new String[0]));
            autoCompleteTextView.setAdapter(adapter);
            autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            });
        }
    }

    public static abstract class InputView extends FieldView {
        public String hint = null;
        protected TextView text1, text2;
    }

    public static class InputViewH extends InputView {
        @Override
        public View onCreateView(Context context) {
            if (editable) {
                return _2input(context, R.layout.pcm_entity_edit_item_2h, name, entity, field, valueWrapper, editable);
            } else {
                return _2input(context, R.layout.pcm_entity_label_item_2h, name, entity, field, valueWrapper, editable);
            }
        }
    }

    public static class InputViewV extends InputView {
        @Override
        public View onCreateView(Context context) {
            View view = _2input(context, R.layout.pcm_entity_edit_item_2v, name, entity, field, valueWrapper, editable);
            return view;
        }
    }

    public static class SwitchView extends FieldView {
        @Override
        public View onCreateView(Context context) {
            View view = _switch(context, name, entity, field);
            View switch1 = view.findViewById(R.id.switch1);
            if (switch1 != null) {
                switch1.setEnabled(editable);
            }
            return view;
        }
    }

    public static abstract class ChoiceView extends FieldView {
        public String[] values, names;

        @Override
        public View onCreateView(Context context) {
            return null;
        }

        private String[] names() {
            String[] finalNames = (names == null || names.length == 0 ? values : names);
            return finalNames;
        }

        protected View _2choice(Context context, int layout) {
            View view = View.inflate(context, layout, null);
            TextView text1 = view.findViewById(android.R.id.text1);
            TextView text2 = view.findViewById(android.R.id.text2);
            text1.setText(name);

            String row = null;
            try {
                row = valueWrapper.get(field, entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(row)) {
                    text2.setText(names()[i]);
                    break;
                }
            }
            view.setOnClickListener(v -> new AlertDialog.Builder(context).setItems(names(), (dialog, which) -> {
                try {
                    valueWrapper.set(field, entity, values[which]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                text2.setText(names()[which]);
            }).show());
            return view;
        }
    }

    public static class ChoiceViewV extends ChoiceView {
        @Override
        public View onCreateView(Context context) {
            return _2choice(context, R.layout.pcm_entity_label_item_2v);
        }
    }

    public static class ChoiceViewH extends ChoiceView {
        @Override
        public View onCreateView(Context context) {
            return _2choice(context, R.layout.pcm_entity_label_item_2h);
        }
    }

    public static class DateView extends FieldView {
        public boolean bdate, btime;
        public String inFormat;


        Date getFieldDate() {
            Date date = null;
            try {
                date = (Date) field.get(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (date == null) {
                try {
                    date = new SimpleDateFormat(inFormat).parse(String.valueOf(field.get(entity)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return date == null ? new Date() : date;
        }

        void setFieldDate(Date date) {
            TextView text2 = view.findViewById(android.R.id.text2);
            text2.setText(String.valueOf(DateFormat.format(inFormat, date)));
            try {
                if (Date.class.isAssignableFrom(field.getType())) {
                    field.set(entity, date);
                } else {
                    field.set(entity, text2.getText().toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View onCreateView(Context context) {
            Date date = getFieldDate();
            String text = null;
            if (date != null) {
                text = String.valueOf(DateFormat.format(inFormat, date));
            }

            View view = View.inflate(context, R.layout.pcm_entity_label_item_2h, null);
            TextView text1 = view.findViewById(android.R.id.text1);
            TextView text2 = view.findViewById(android.R.id.text2);
            text1.setText(name);
            text2.setText(text);
            view.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getFieldDate());
                if (bdate) {
                    showDateDialog(calendar);
                } else if (btime) {
                    showTimeDialog(calendar);
                }
            });
            return view;
        }

        void showDateDialog(Calendar calendar) {
            DatePickerDialog dialog = new DatePickerDialog(view.getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if (btime) {
                    showTimeDialog(calendar);
                } else {
                    setFieldDate(calendar.getTime());
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }

        void showTimeDialog(Calendar calendar) {
            TimePickerDialog dialog = new TimePickerDialog(view.getContext(), (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                setFieldDate(calendar.getTime());
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            dialog.show();
        }
    }

//    public static class DateTimeH extends BaseDateH {
//        public DateTimeH() {
//            super(true, true, "yyyy-MM-dd HH:mm:ss");
//        }
//    }
//    public static class TimeH extends BaseDateH {
//        public TimeH() {
//            super(false, true,  "HH:mm:ss");
//        }
//    }
//    public static class DateH extends BaseDateH {
//        public DateH() {
//            super(true, false, "yyyy-MM-dd");
//        }
//    }

    ////////////////////////////////////////////////////////////////////////////////////////
    final static List<Class> FLOAT_CLASS_LIST = Arrays.asList(double.class, float.class, Double.class, Float.class);
    final static List<Class> INT_CLASS_LIST = Arrays.asList(int.class, long.class, byte.class,
            Integer.class, Long.class, Byte.class);

    private static View _2input(Context context, int layout, String title, Object entity, Field field, EntityLayout.ValueWrapper valueWrapper, boolean editable) {
        View view = View.inflate(context, layout, null);
        TextView text1 = view.findViewById(android.R.id.text1);
        TextView text2 = view.findViewById(android.R.id.text2);
        text1.setText(title);
        try {
            text2.setText(valueWrapper.get(field, entity));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        text2.addTextChangedListener(new EditTextWatcher(field, entity, valueWrapper));

        if (!editable) {
            text2.setFocusable(false);
            text2.setEnabled(editable);
            text2.setTextColor(Color.DKGRAY);
            return view;
        }
        text2.setTextColor(0xff189EED);

        Class fieldType = field.getType();
        if (FLOAT_CLASS_LIST.contains(fieldType)) {
            text2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (INT_CLASS_LIST.contains(fieldType)) {
            text2.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        view.setOnClickListener(v -> {
            text2.requestFocus();
            if (text2 instanceof EditText) {
                ((EditText)text2).setSelection(text2.length());
            }
        });

        return view;
    }

    private static View _2label(Context context, int layout, String title, Object entity, Field field, EntityLayout.ValueWrapper valueWrapper) {
        View view = View.inflate(context, layout, null);
        TextView text1 = view.findViewById(android.R.id.text1);
        TextView text2 = view.findViewById(android.R.id.text2);
        text1.setText(title);
        try {
            text2.setText(valueWrapper.get(field, entity));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return view;
    }

    private static View _switch(Context context, String title, Object entity, Field field) {
        View view = View.inflate(context, R.layout.pcm_simple_list_item_switch, null);
        ((TextView) view.findViewById(android.R.id.text1)).setText(title);
        Switch switch1 = view.findViewById(R.id.switch1);
        try {
            switch1.setChecked(field.getBoolean(entity));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                field.setBoolean(entity, isChecked);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return view;
    }



    static class EditTextWatcher implements TextWatcher {
        Object entity;
        Field field;
        EntityLayout.ValueWrapper valueWrapper;

        public EditTextWatcher(Field field, Object entity, EntityLayout.ValueWrapper valueWrapper) {
            this.entity = entity;
            this.field = field;
            this.valueWrapper = valueWrapper;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            try {
                valueWrapper.set(field, entity, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
