package com.phat_plats.scanitfortheplanet.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phat_plats.scanitfortheplanet.R;
import com.phat_plats.scanitfortheplanet.network.CommentHandler;
import com.phat_plats.scanitfortheplanet.network.model.Comment;
import com.phat_plats.scanitfortheplanet.network.model.Product;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.views.CommentRecyclerAdapter;

import java.util.ArrayList;

public class CommentsFragment extends Fragment {

    private ArrayList<Comment> comments;
    private String upc;

    public CommentsFragment() {
        // Required empty public constructor
    }
    public static CommentsFragment newInstance() {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Product data = (Product) getArguments().getSerializable("product");
        upc = data.upc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_comments, container, false);

        CommentHandler.getComments(upc, new Callback() {
            @Override
            public void run(boolean success, Object result) {
                if(success) {
                    comments = (ArrayList<Comment>)result;
                    if(!comments.isEmpty()) {
                        RecyclerView mRecyclerView = (RecyclerView)view.findViewById(R.id.comment_list);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(CommentsFragment.this.getContext()));

                        CommentRecyclerAdapter adapter = new CommentRecyclerAdapter(CommentsFragment.this, comments);
                        mRecyclerView.setAdapter(adapter);
                    } else {
                        view.findViewById(R.id.comment_text).setVisibility(View.VISIBLE);
                    }
                }
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
        return view;
    }
}
