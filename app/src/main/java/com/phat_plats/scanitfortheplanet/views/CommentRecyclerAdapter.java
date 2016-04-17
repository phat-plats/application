package com.phat_plats.scanitfortheplanet.views;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.phat_plats.scanitfortheplanet.R;
import com.phat_plats.scanitfortheplanet.fragments.CommentsFragment;
import com.phat_plats.scanitfortheplanet.network.LoginHandler;
import com.phat_plats.scanitfortheplanet.network.model.Comment;
import com.phat_plats.scanitfortheplanet.network.util.Callback;

import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.CustomViewHolder> {
    private final CommentsFragment context;
    private List<Comment> feedItemList;

    public CommentRecyclerAdapter(CommentsFragment context, List<Comment> feedItemList) {
        this.feedItemList = feedItemList;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_view, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final Comment comment = feedItemList.get(i);

        //Setting text view title
        customViewHolder.poster.setText(comment.getScore() + " | " + comment.poster);
        customViewHolder.content.setText(comment.contents);
        customViewHolder.down_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginHandler.currentUser == null) {
                    LoginHandler.showLoginDialog(context.getContext(), new Callback() {
                        @Override
                        public void run(boolean success, Object result) {
                            if(success)
                                customViewHolder.setButtonState(comment.vote(LoginHandler.currentUser, false));
                        }
                    });
                } else {
                    customViewHolder.setButtonState(comment.vote(LoginHandler.currentUser, false));
                }
                customViewHolder.poster.setText(comment.getScore() + " | " + comment.poster);
                Log.d("VOTE BUTTON CLICK", "downvote clicked");
            }
        });
        customViewHolder.up_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginHandler.currentUser == null) {
                    LoginHandler.showLoginDialog(context.getContext(), new Callback() {
                        @Override
                        public void run(boolean success, Object result) {
                            customViewHolder.setButtonState(comment.vote(LoginHandler.currentUser, true));
                        }
                    });
                } else {
                    customViewHolder.setButtonState(comment.vote(LoginHandler.currentUser, true));
                }
                customViewHolder.poster.setText(comment.getScore() + " | " + comment.poster);
                Log.d("VOTE BUTTON CLICK", "upvote clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView poster;
        protected TextView content;
        protected ImageButton up_arrow;
        protected ImageButton down_arrow;

        public CustomViewHolder(View view) {
            super(view);
            this.poster = (TextView) view.findViewById(R.id.comment_header);
            this.content = (TextView)view.findViewById(R.id.comment_content);
            this.down_arrow = (ImageButton)view.findViewById(R.id.comment_arrow_down);
            this.up_arrow = (ImageButton)view.findViewById(R.id.comment_arrow_up);
        }

        public void setButtonState(int value) {
            if(value == 1) {
                up_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_up_active));
                down_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_down));
            } else if (value == -1) {
                up_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_up));
                down_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_down_active));
            } else {
                up_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_up));
                down_arrow.setImageDrawable(context.getContext().getDrawable(R.drawable.ic_action_arrow_down));
            }
        }
    }
}