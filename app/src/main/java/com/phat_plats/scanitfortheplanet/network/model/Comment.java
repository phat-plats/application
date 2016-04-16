package com.phat_plats.scanitfortheplanet.network.model;

import com.phat_plats.scanitfortheplanet.network.CommentHandler;
import com.phat_plats.scanitfortheplanet.network.util.Callback;

import java.util.ArrayList;

/**
 * Created by Gareth on 4/16/16.
 */
public class Comment {
    private int id;
    private int score;
    public String poster;
    public String contents;
    private ArrayList<Activity> commentActivity;

    public Comment(int id, int score, String poster, String contents) {
        this.id = id;
        this.score = score;
        this.poster = poster;
        this.contents = contents;
        commentActivity = new ArrayList<>();
    }

    public void vote(int userId, boolean isUpVote) {
        boolean inc = false;
        if((hasVoted(userId) == 1 && isUpVote) || (hasVoted(userId) == 0 && !isUpVote))
            this.score--;
        else if ((hasVoted(userId) == -1 && !isUpVote) || (hasVoted(userId) == 0 && isUpVote)) {
            this.score++;
            inc = true;
        }
        setVote(userId, (isUpVote ? 1 : -1));
        CommentHandler.vote(this.id, inc, new Callback() {
            @Override
            public void run(boolean success, Object result) {
                if(success)
                    score = (int)result;
            }
        });
    }

    private int hasVoted(int userId) {
        for (Activity a : commentActivity) {
            if(a.userId == userId)
                return a.action;
        }
        return 0;
    }

    private void setVote(int userId, int vote) {
        for (Activity a : commentActivity) {
            if(a.userId == userId)
                a.action = vote;
        }
    }

    private class Activity {
        public int userId;
        public int action;

        public Activity(int userId, int action) {
            this.userId = userId;
            this.action = action;
        }
    }
}
