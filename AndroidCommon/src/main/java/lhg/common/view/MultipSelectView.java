package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import lhg.common.R;
import lhg.common.utils.Utils;
import lhg.common.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 刘浩歌 on 2015/7/13.
 */
public class MultipSelectView extends TagsLayout {
    OnChoiceClickListener myChoiceClickListener;
    int maxSelectCount = Integer.MAX_VALUE;
    boolean allowMultip = true;
    private OnClickListener onChoiceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectChild(v, !v.isSelected(), true);
        }
    };

    public MultipSelectView(Context context) {
        super(context);
        init(null, 0);
    }

    public MultipSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MultipSelectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setHorizontalSpacing(Utils.dip2px(getContext(), 3));
        setVerticalSpacing(Utils.dip2px(getContext(), 3));
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    public void setAllowMultip(boolean allowMultip) {
        this.allowMultip = allowMultip;
    }

    public <T> void setDataList(List<String> names, List<T> tags) {
        removeAllViews();
        for (int i = 0, size = names.size(); i < size; i++) {
            TextView textView = (TextView) View.inflate(getContext(), R.layout.pcm_textview_choice, null);
            textView.setText(names.get(i));
            if (tags != null && tags.size() > 0) {
                textView.setTag(tags.get(i));
            }
            textView.setOnClickListener(onChoiceClickListener);
            addView(textView);
        }
    }
    public <T> void setDataList(String[] names, T[] tags) {
        setDataList(Arrays.asList(names), Arrays.asList(tags));
    }

    public void selectOne(String name) {
        List<String> list = new ArrayList<>();
        list.add(name);
        selectSome(list);
    }

    public void selectSome(List<String> names) {
        for (int i = 0, count = getChildCount(); i < count; i++) {
            TextView child = (TextView) getChildAt(i);
            child.setSelected(false);
            if (names != null && names.size() > 0) {
                String text = (String) child.getText();
                selectChild(child, names.contains(text), false);
            }
        }
    }

    void selectChild(View v, boolean select, boolean notifyListener) {
        int selectedCount = 0;
        if (select) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child.isSelected() && child != v)
                    selectedCount++;
            }
            if (selectedCount >= maxSelectCount) {
                ToastUtil.show(getContext(), "最多只能选择" + maxSelectCount + "个");
                return;
            }
        }

        v.setSelected(select);
        if (notifyListener && myChoiceClickListener != null) {
            myChoiceClickListener.onClick(select, v.getTag());
        }
        if (v.isSelected() && !allowMultip) {
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child != v)
                    child.setSelected(false);
            }
        }
    }

    public <T> List<T> getSelectTags() {
        List<T> list = new ArrayList<>();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.isSelected()) {
                list.add((T) child.getTag());
            }
        }
        return list;
    }

    public <T> T getSelectTag() {
        List<T> list = new ArrayList<>();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.isSelected()) {
                return (T) child.getTag();
            }
        }
        return null;
    }

    public void setOnChoiceClickListener(OnChoiceClickListener onChoiceClickListener) {
        this.myChoiceClickListener = onChoiceClickListener;
    }

    public void unselectAll() {
        for (int i = 0, count = getChildCount(); i < count; i++) {
            getChildAt(i).setSelected(false);
        }
    }

    public boolean isTagSelected(Object tag) {
        List list = getSelectTags();
        return list != null ? list.contains(tag) : false;
    }

    public List<String> getSelectNames() {
        List<String> list = new ArrayList<>();
        for (int i = 0, count = getChildCount(); i < count; i++) {
            TextView child = (TextView) getChildAt(i);
            if (child.isSelected()) {
                list.add(child.getText().toString());
            }
        }
        return list;
    }

    public interface OnChoiceClickListener {
        void onClick(boolean selected, Object choice);
    }

}
