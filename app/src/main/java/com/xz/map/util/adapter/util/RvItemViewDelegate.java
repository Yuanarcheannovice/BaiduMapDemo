package com.xz.map.util.adapter.util;


/**
 * Created by zhy on 16/6/22.
 * 项目委托
 */
public interface RvItemViewDelegate<T>
{
    //获取项目视图布局ID
    int getItemViewLayoutId();

    //是用于视图类型的
    boolean isForViewType(T item, int position);

    //转换
    void convert(RvViewHolder holder, T t, int position);

}
