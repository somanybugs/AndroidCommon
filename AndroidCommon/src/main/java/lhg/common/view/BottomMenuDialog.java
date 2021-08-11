package lhg.common.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDialog;

import lhg.common.utils.Utils;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2020/11/4 13:04
 * Note:
 */
public class BottomMenuDialog extends AppCompatDialog {

    private DialogListView listView = null;
    private BaseAdapter listAdapter = null;
    private DialogInterface.OnClickListener listener = null;

    public BottomMenuDialog(Context context) {
        super(context);
    }

    public void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        listView = new DialogListView(getContext());
        listView.setBackgroundColor(Color.WHITE);
        listView.maxHeight = (int) (Utils.screenSizeInPixel(getContext()).y * 0.8);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(listView);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        updateView();
    }

    public BottomMenuDialog setItems(String[] items, DialogInterface.OnClickListener listener) {
        return setItems(new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, items), listener);
    }
    public BottomMenuDialog setItems(BaseAdapter adapter, DialogInterface.OnClickListener listener) {
        this.listAdapter = adapter;
        this.listener = listener;
        updateView();
        return this;
    }

    private void updateView() {
        if (listView != null && listAdapter != null) {
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dismiss();
                    if (listener != null) {
                        listener.onClick(BottomMenuDialog.this, position);
                    }
                }
            });
        }
    }

    static class DialogListView extends ListView {
        int maxHeight = 0;

        public DialogListView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST));

        }
    }
}
