package com.hhxy.zw.adressbook;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;



public interface StickyHeaderAdapter<T extends RecyclerView.ViewHolder> {
    long getHeaderId(int position);

    T onCreateHeaderViewHolder(ViewGroup parent);

    void onBindHeaderViewHolder(T viewholder, int position);
}
