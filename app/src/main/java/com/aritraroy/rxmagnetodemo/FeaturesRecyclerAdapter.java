package com.aritraroy.rxmagnetodemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by aritraroy on 18/02/17.
 */

public class FeaturesRecyclerAdapter extends RecyclerView.Adapter<FeaturesRecyclerAdapter.FeaturesViewHolder> {


    @Override
    public FeaturesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(FeaturesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class FeaturesViewHolder extends RecyclerView.ViewHolder {

        public FeaturesViewHolder(View itemView) {
            super(itemView);
        }
    }
}
