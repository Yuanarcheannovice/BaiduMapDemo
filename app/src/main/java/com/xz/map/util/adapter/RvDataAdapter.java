package com.xz.map.util.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xz.map.util.adapter.util.RvViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xz on 2017/7/26 0026.
 * <p>
 * 此adapter，着重于 对于数据处理
 */
public abstract class RvDataAdapter<T> extends RecyclerView.Adapter<RvViewHolder> {

    protected List<T> mDatas;//一般数据
    protected SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();//headerView数量
    protected SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();//footerView数量
    protected static final int BASE_ITEM_TYPE_HEADER = 100000;//定义header的key的起始位置
    protected static final int BASE_ITEM_TYPE_FOOTER = 200000;//定义footer的Key的起始位置
    protected OnItemClickListener mOnItemClickListener;//点击事件
    protected OnItemLongClickListener mOnItemLongClickListener;//长按事件


    public List<T> getDatas() {
        if (mDatas == null)
            mDatas = new ArrayList<>();
        return mDatas;
    }

    /**
     * 数据替换 ，带刷新
     *
     * @param datas     数据
     * @param isRefresh 是否刷新
     */
    public void setData(List<T> datas, boolean isRefresh) {
        if (datas == null)
            datas = new ArrayList<>();
        this.mDatas = datas;
        if (isRefresh)
            notifyDataSetChanged();
    }

    /**
     * 一般使用 不带刷新
     */
    public void setDatas(List<T> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        mDatas = datas;
    }

    /**
     * 数据集合增加数据 带刷新
     *
     * @param data      数据
     * @param isRefresh 是否刷新
     */
    public void addDatas(List<T> data, boolean isRefresh) {
        if (this.mDatas == null) {
            this.mDatas = new ArrayList<>();
            this.mDatas.addAll(data);
            if (isRefresh)
                notifyDataSetChanged();
        } else {
            this.mDatas.addAll(data);
            //因为Header也是占有刷新的 下标，所以需要加上
            notifyItemRangeInserted(mDatas.size() + mHeaderViews.size() - data.size(), data.size());
        }
    }



    /**
     * 移除data
     *
     * @param data 对象
     * @return 是否移除成功
     */
    public boolean removeData(T data) {
        return this.mDatas != null && this.mDatas.remove(data);
    }

    /**
     * 移除data
     *
     * @param index 下标
     * @return
     */
    public boolean removeData(int index) {
        if (mDatas != null && index >= 0) {
            if (index < mDatas.size()) {
                mDatas.remove(index);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 移除所有数据
     */
    public void removeDataAll() {
        if (mDatas != null)
            mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 得到单个item
     *
     * @param position item的下标
     * @return 集合中的某个对象
     */
    public T getItem(int position) {
        if (mDatas != null && !mDatas.isEmpty()) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mDatas != null)
            return getHeadersCount() + getFootersCount() + mDatas.size();
        else
            return 0;
    }

    /**
     * @return header数量
     */
    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    /**
     * @return footer的数量
     */
    public int getFootersCount() {
        return mFootViews.size();
    }

    /**
     * 添加HeaderView
     */
    public void addHeaderView(View view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    /**
     * 添加FooterView
     */
    public void addFootView(View view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, view);
    }

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
