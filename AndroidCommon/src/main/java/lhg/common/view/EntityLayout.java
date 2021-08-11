package lhg.common.view;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import lhg.common.utils.DimenUtils;
import lhg.common.utils.EntityUI;
import lhg.common.utils.Utils;
import lhg.common.utils.ViewUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLayout extends LinearLayout {

    Map<String, EntityUI.FieldView> fieldViewMap = new HashMap<>();

    public EntityLayout(Context context) {
        this(context, null);
    }

    public EntityLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EntityLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initDetail(Object entity) {
        List<View> views = createViews(entity, false);
        int pad4 = DimenUtils.dip2px(getContext(), 4);
        int minHeight = DimenUtils.dip2px(getContext(), 30);
        for (View v : views) {
            v.setPadding(v.getPaddingLeft(), pad4, v.getPaddingRight(), pad4);
            v.setMinimumHeight(minHeight);
            v.setOnClickListener(null);
            v.setClickable(false);
        }
        removeAllViews();
        ViewUtils.addItemViewsToLinearLayout(this, views, false, true, 0);
    }

    public void initEdit(Object entity) {
        List<View> views = createViews(entity, true);
        removeAllViews();
        ViewUtils.addItemViewsToLinearLayout(this, views, false, true, 0);
    }

    public View findView(String fieldName) {
        return fieldViewMap.get(fieldName).view;
    }
    public EntityUI.FieldView findFieldView(String fieldName) {
        return fieldViewMap.get(fieldName);
    }

    private List<View> createViews(Object entity, boolean editable) {
        List<View> views = new ArrayList<>();
        Field[] fields = Utils.getAllFields(entity.getClass(), true);
        List<Pair<Field, Property>> propertyList = new ArrayList<>();
        for (Field f : fields) {
            Property property = f.getAnnotation(Property.class);
            if (property == null) {
                continue;
            }
            propertyList.add(new Pair<>(f, property));
        }

        Collections.sort(propertyList, (o1, o2) -> o1.second.sort()-o2.second.sort());

        for (Pair<Field, Property> pair : propertyList) {
            Field f = pair.first;
            Property property = pair.second;
            try {
                EntityUI.FieldView fieldView = property.viewClazz().newInstance();
                ValueWrapper valueWrapper = property.wrapperClazz().newInstance();
                String name = property.name();
                if (TextUtils.isEmpty(name)) {
                    name = f.getName();
                }
                fieldView.valueWrapper = valueWrapper;
                fieldView.editable = editable && property.editable();
                fieldView.field = f;
                fieldView.entity = entity;
                fieldView.name = name + ": ";
                if (fieldView instanceof EntityUI.DateView) {
                    ParamDate paramDate = f.getAnnotation(ParamDate.class);
                    ((EntityUI.DateView)fieldView).bdate = paramDate.bdate();
                    ((EntityUI.DateView)fieldView).btime = paramDate.btime();
                    ((EntityUI.DateView)fieldView).inFormat = paramDate.format();
                } else if (fieldView instanceof EntityUI.ChoiceView) {
                    ParamChoice paramChoice = f.getAnnotation(ParamChoice.class);
                    ((EntityUI.ChoiceView)fieldView).names = paramChoice.names();
                    ((EntityUI.ChoiceView)fieldView).values = paramChoice.values();
                }
                fieldViewMap.put(f.getName(), fieldView);
                View view = fieldView.createView(getContext());
//                view.setTag(f.getName());
                view.setClickable(fieldView.editable);
                views.add(view);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return views;
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ParamChoice {
        String[] names() default {};
        String[] values() default {};
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ParamDate {
        boolean bdate() default true;
        boolean btime() default true;
        String format() default "yyyy-MM-dd HH:mm:ss";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Property {
        int sort() default 0;
        String name() default "";
        String hint() default "";
        boolean editable() default true; //是否可编辑
        Class<? extends EntityUI.FieldView> viewClazz();
        Class<? extends ValueWrapper> wrapperClazz() default DefaultValueWrapper.class;
    }

    public interface ValueWrapper {
        String get(Field field, Object entity) throws IllegalAccessException;
        void set(Field field, Object entity, String value) throws IllegalAccessException;
    }

    public static class DefaultValueWrapper implements ValueWrapper {

        public String get(Field field, Object entity) throws IllegalAccessException {
            Object val = field.get(entity);
            return (val == null ? null : String.valueOf(val));
        }

        public void set(Field field, Object entity, String value) throws IllegalAccessException {
            Class type = field.getType();
            if (CharSequence.class.isAssignableFrom(type)) {
                field.set(entity, value);
            }
            else if (type == int.class || type == Integer.class) {
                field.setInt(entity, Integer.parseInt(value));
            }
            else if (type == long.class || type == Long.class) {
                field.setLong(entity, Long.parseLong(value));
            }
            else if (type == short.class || type == Short.class) {
                field.setShort(entity, Short.parseShort(value));
            }
            else if (type == byte.class || type == Byte.class) {
                field.setByte(entity, Byte.parseByte(value));
            }
            else if (type == char.class || type == Character.class) {
                field.setChar(entity, value.charAt(0));
            }
            else if (type == float.class || type == Float.class) {
                field.setFloat(entity, Float.parseFloat(value));
            }
            else if (type == double.class || type == Double.class) {
                field.setDouble(entity, Double.parseDouble(value));
            }
            else if (type == boolean.class || type == Boolean.class) {
                field.setBoolean(entity, Boolean.parseBoolean(value));
            }
            else {
                throw new RuntimeException("不支持这种格式 " + type.getName());
            }
        }
    }

}
