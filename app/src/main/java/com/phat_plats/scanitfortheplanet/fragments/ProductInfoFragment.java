package com.phat_plats.scanitfortheplanet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phat_plats.scanitfortheplanet.R;
import com.phat_plats.scanitfortheplanet.network.model.Product;
import com.phat_plats.scanitfortheplanet.views.HazardRecyclerAdapter;

import java.util.ArrayList;


public class ProductInfoFragment extends Fragment {

    private ArrayList<String> hazard_list;
    private int recycleNum;
    private int percentage = 100;

    public static ProductInfoFragment newInstance() {
        ProductInfoFragment fragment = new ProductInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        percentage = 100;
        Product data = (Product) getArguments().getSerializable("product");
        hazard_list = data.harmfulStuff;
        recycleNum = data.recyling;
    }

    public boolean canBeRecycled(int recycleNum) {
        //plastics
        if(recycleNum >= 1 && recycleNum <= 7)
            return true;
        //paper
        if(recycleNum >= 20 && recycleNum <= 21)
            return true;
        //metal
        if(recycleNum == 40 || recycleNum == 41)
            return true;
        //glass
        if(recycleNum >= 70 && recycleNum <= 72)
            return true;
        //composite
        return recycleNum == 84;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);

        TextView recycle_text = (TextView)view.findViewById(R.id.recyclable);
        if(canBeRecycled(recycleNum)) {
            view.findViewById(R.id.crossout).setVisibility(View.GONE);
            recycle_text.setText("Recyclable");
            recycle_text.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            percentage -= 50;
        }

        if(!hazard_list.isEmpty()) {
            RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.harmful_list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

            HazardRecyclerAdapter adapter = new HazardRecyclerAdapter(this, hazard_list);
            mRecyclerView.setAdapter(adapter);
        } else {
            view.findViewById(R.id.no_harmful).setVisibility(View.VISIBLE);
        }


        TextView percentText = (TextView)view.findViewById(R.id.percentage);
        if(percentage < 65)
            percentText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        if(percentage < 0)
            percentage = 0;
        percentText.setText(percentage + "%");
        
        return view;
    }

    public void subtractPercentage(int percent) {
        percentage -=percent;
        if(getView() != null) {
            TextView percentText = (TextView)getView().findViewById(R.id.percentage);
            if(percentage < 65)
                percentText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            if(percentage < 0)
                percentage = 0;
            percentText.setText(percentage + "%");
        }
    }
}
