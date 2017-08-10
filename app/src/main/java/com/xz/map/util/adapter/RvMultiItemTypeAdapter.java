package com.xz.map.util.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xz.map.util.adapter.util.RvItemViewDelegate;
import com.xz.map.util.adapter.util.RvItemViewDelegateManager;
import com.xz.map.util.adapter.util.RvViewHolder;
import com.xz.map.util.adapter.util.RvWrapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhy on 16/4/9.
 * update by xz on17/7/25
 * <p>
 * 此Adapter 拥有 添加Header，Footer，点击，长按，实现不同layout功能;
 */
public class RvMultiItemTypeAdapter<T> extends RvDataAdapter<T> {
    protected Context mContext;
    protected RvItemViewDelegateManager mItemViewDelegateManager;


    /**
     * 直接使用集合
     *
     * @param context
     * @param datas
     */
    public RvMultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        if (datas == null)
            datas = new ArrayList<>();
        mDatas = datas;
        mItemViewDelegateManager = new RvItemViewDelegateManager();
    }


    /**
     * 根据position来是实现不同layout
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - mDatas.size());
        } else {
            position = position - getHeadersCount();
            //判断是否有不同View的操作，如果有，则返回
            if (!useItemViewDelegateManager())
                return super.getItemViewType(position);
            return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
        }
    }


    /**
     * 创建ViewHolder
     */
    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Header 和 Hooter 创建
        if (mHeaderViews.get(viewType) != null) {
            RvViewHolder holder = RvViewHolder.createViewHolder(parent.getContext(), mHeaderViews.get(viewType));
            return holder;

        } else if (mFootViews.get(viewType) != null) {
            RvViewHolder holder = RvViewHolder.createViewHolder(parent.getContext(), mFootViews.get(viewType));
            return holder;
        } else {
            //一般layout 创建
            RvItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
            int layoutId = itemViewDelegate.getItemViewLayoutId();
            RvViewHolder holder = RvViewHolder.createViewHolder(mContext, parent, layoutId);
            onViewHolderCreated(holder, holder.getConvertView());
            setListener(parent, holder, viewType);
            return holder;
        }
    }

    /**
     * 创建一个item的view
     */
    protected void onViewHolderCreated(RvViewHolder holder, View itemView) {

    }


    public void convert(RvViewHolder holder, T t) {
        //这里需要减去头部的值，
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition() - getHeadersCount());
    }

    /**
     * 判断当前viewtype是否是header 和 footer
     *
     * @param viewType
     * @return
     */
    protected boolean isEnabled(int viewType) {
        if (viewType == BASE_ITEM_TYPE_HEADER || viewType == BASE_ITEM_TYPE_FOOTER)
            return false;
        else
            return true;
    }


    private void setListener(final ViewGroup parent, final RvViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition() - getHeadersCount();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    int position = viewHolder.getAdapterPosition() - getHeadersCount();
                    return mOnItemLongClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        //如果当前下标没有对应Header，Footer,position则应该减去header的数量
        position = position - getHeadersCount();
        convert(holder, mDatas.get(position));
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RvWrapperUtils.onAttachedToRecyclerView(recyclerView, new RvWrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                } else if (mFootViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }

    /**
     * 当Item进入这个页面的时候调用
     */
    @Override
    public void onViewAttachedToWindow(RvViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            RvWrapperUtils.setFullSpan(holder);
        }
    }


    /**
     * 添加一个item
     *
     * @param itemViewDelegate item视图
     */
    public RvMultiItemTypeAdapter addItemViewDelegate(RvItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    /**
     * 添加一个item
     *
     * @param viewType         view类型
     * @param itemViewDelegate item视图
     */
    public RvMultiItemTypeAdapter addItemViewDelegate(int viewType, RvItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    /**
     * 判断是否有 特殊ItemLayout
     */
    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }


    /**
     * 判断当前item下标是否小于Header数量
     * 如果小于，则可以对header处理
     *
     * @param position 当前需要显示的item下标
     */
    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    /**
     * 判断是否当前下标是否大于等于 HeaderView数量和mDatas数量
     * 如果大于等于，则可以显示footerView
     *
     * @param position 当前需要显示的item下标
     */
    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + mDatas.size();
    }

}
