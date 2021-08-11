package lhg.common.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * Company:
 * Project:
 * Author: liuhaoge
 * Date: 2020/11/29 9:28
 * Note:
 */
public abstract class RecyclerClickAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private WeakReference<RecyclerView> recyclerViewWeakReference;
    protected OnItemClickListener<T, VH> onItemClickListener;
    protected OnItemLongClickListener<T, VH> onItemLongClickListener;

    public  void setOnItemClickListener(OnItemClickListener<T, VH> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T, VH> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private HolderClickListsner onHolderClickListener = new HolderClickListsner(this);

    private static class HolderClickListsner implements View.OnClickListener, View.OnLongClickListener {
        WeakReference<RecyclerClickAdapter> adapterWeakReference;

        public HolderClickListsner(RecyclerClickAdapter adapter) {
            adapterWeakReference = new WeakReference<>(adapter);
        }

        private RecyclerView.ViewHolder getHolder(View v) {
            RecyclerClickAdapter adapter = adapterWeakReference.get();
            if (adapter == null || adapter.onItemClickListener == null || adapter.recyclerViewWeakReference == null) {
                return null;
            }
            RecyclerView recyclerView = (RecyclerView) adapter.recyclerViewWeakReference.get();
            if (recyclerView == null) {
                return null;
            }
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
            return holder;
        }

        @Override
        public void onClick(View v) {
            RecyclerClickAdapter adapter = adapterWeakReference.get();
            if (adapter == null || adapter.recyclerViewWeakReference == null) {
                return;
            }
            RecyclerView recyclerView = (RecyclerView) adapter.recyclerViewWeakReference.get();
            if (recyclerView == null) {
                return;
            }
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
            adapter.onItemClick(holder.getBindingAdapterPosition(), holder);
        }

        @Override
        public boolean onLongClick(View v) {
            RecyclerClickAdapter adapter = adapterWeakReference.get();
            if (adapter == null || adapter.recyclerViewWeakReference == null) {
                return false;
            }
            RecyclerView recyclerView = (RecyclerView) adapter.recyclerViewWeakReference.get();
            if (recyclerView == null) {
                return false;
            }
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
            return adapter.onItemLongClick(holder.getBindingAdapterPosition(), holder);
        }
    }

    protected void onItemClick(int position, VH holder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, getItem(position), holder);
        }
    }
    protected boolean onItemLongClick(int position, VH holder) {
        if (onItemLongClickListener != null) {
            return onItemLongClickListener.onItemLongClick(this, getItem(position), holder);
        }
        return false;
    }

    public abstract T getItem(int postion);

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerViewWeakReference = new WeakReference<>(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerViewWeakReference = null;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setOnClickListener(onHolderClickListener);
        holder.itemView.setOnLongClickListener(onHolderClickListener);
    }

    public interface OnItemClickListener<T, VH extends RecyclerView.ViewHolder> {
        void onItemClick(RecyclerClickAdapter<T, VH> adapter, T data, VH holder);
    }
    public interface OnItemLongClickListener<T, VH extends RecyclerView.ViewHolder> {
        boolean onItemLongClick(RecyclerClickAdapter<T, VH> adapter, T data, VH holder);
    }

}
