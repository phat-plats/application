package com.phat_plats.scanitfortheplanet.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phat_plats.scanitfortheplanet.R;
import com.phat_plats.scanitfortheplanet.fragments.ProductInfoFragment;

import java.util.List;
import java.util.Random;

public class HazardRecyclerAdapter extends RecyclerView.Adapter<HazardRecyclerAdapter.CustomViewHolder> {
    private final ProductInfoFragment context;
    private List<String> feedItemList;

    public HazardRecyclerAdapter(ProductInfoFragment context, List<String> feedItemList) {
        this.feedItemList = feedItemList;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.harmful_list_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        String feedItem = feedItemList.get(i);

        //Setting text view title
        customViewHolder.textView.setText(feedItem);
        int rand = (new Random()).nextInt(9)+1;
        customViewHolder.percentage.setText("-" + rand + "%");
        context.subtractPercentage(rand);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected TextView percentage;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.rd_header_text);
            this.percentage = (TextView)view.findViewById(R.id.percent_text);
        }
    }
}
