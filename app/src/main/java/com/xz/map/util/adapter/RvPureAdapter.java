package com.xz.map.util.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xz.map.util.adapter.util.RvViewHolder;


/**
 * 一个简单封装了，Rl点击事件的类，
 * Created by xz on 2016/8/15 0015.
 * <p>
 * 纯净版的adapter(没有head，footer，多layout控制 数据控制，只有点击事件)
 */
public abstract class RvPureAdapter extends RecyclerView.Adapter<RvViewHolder> {

    protected OnItemClickListener mOnItemClickListener;//点击事件
    protected OnItemLongClickListener mOnItemLongClickListener;//长按事件

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RvViewHolder viewHolder = RvViewHolder.createViewHolder(parent.getContext(), parent, getItemLayout(viewType));

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemLongClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
        return viewHolder;
    }


    @LayoutRes
    public abstract int getItemLayout(int viewType);

    /**
     * 点击接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
    }


    /**
     * 长按接口
     */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    /**
     * 点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 长按事件
     *
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }


}
