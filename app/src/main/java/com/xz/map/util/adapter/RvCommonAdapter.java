package com.xz.map.util.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;

import com.xz.map.util.adapter.util.RvItemViewDelegate;
import com.xz.map.util.adapter.util.RvViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * update by xz on 17/7/25.
 */
public abstract class RvCommonAdapter<T> extends RvMultiItemTypeAdapter<T> {

    /**
     * 不携带初始数据
     * @param layoutId Layout布局
     */
    public RvCommonAdapter(final Context context, @LayoutRes final int layoutId) {
        super(context, new ArrayList<T>());

        addItemViewDelegate(new RvItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(RvViewHolder holder, T t, int position) {
                RvCommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    /**
     * 携带初始数据
     * @param layoutId Layout布局
     * @param datas 数据
     */
    public RvCommonAdapter(final Context context, @LayoutRes final int layoutId, List<T> datas) {
        super(context, datas);

        addItemViewDelegate(new RvItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(RvViewHolder holder, T t, int position) {
                RvCommonAdapter.this.convert(holder, t, position);
            }
        });
    }


    protected abstract void convert(RvViewHolder holder, T t, int position);


}
