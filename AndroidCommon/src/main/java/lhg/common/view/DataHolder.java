package lhg.common.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DataHolder<T> extends RecyclerView.ViewHolder {

    public T data, oldData;
    private ViewClickListener viewClickListener = new ViewClickListener();

    public DataHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(viewClickListener);
    }

    static class ViewClickListener implements View.OnClickListener {
        OnClickListsner onClickListsner;
        Object data;

        public void setOnClickListsner(OnClickListsner onClickListsner) {
            this.onClickListsner = onClickListsner;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            if (onClickListsner != null) {
                onClickListsner.onClick(data);
            }
        }
    }


    public DataHolder<T> setOnClickListener(OnClickListsner onClickListsner) {
        viewClickListener.setOnClickListsner(onClickListsner);
        return this;
    }

    public void bindTo(T data) {
        oldData = this.data;
        this.data = data;
        viewClickListener.setData(data);
        updateViews(oldData, data);
    }

    protected abstract void updateViews(T oldData, T data);

    public interface OnClickListsner<T> {
        void onClick(T data);
    }
}
