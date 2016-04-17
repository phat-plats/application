package com.phat_plats.scanitfortheplanet.network.model;

import android.util.Log;

import com.phat_plats.scanitfortheplanet.network.CommentHandler;
import com.phat_plats.scanitfortheplanet.network.util.Callback;

import java.util.ArrayList;

/**
 * Created by Gareth on 4/16/16.
 */
public class Comment {
    public int id;
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

    public int vote(String userId, boolean isUpVote) {
        int hasVoted = hasVoted(userId);
        int action = 0;
        int increment = 0;

        if((hasVoted == 1 && isUpVote) || (hasVoted == 0 && !isUpVote)) {
            if (hasVoted == 0)
                action = -1;
            this.score--;
            increment = -1;
        } else if ((hasVoted == -1 && !isUpVote) || (hasVoted == 0 && isUpVote)) {
            if(hasVoted == 0)
                action = 1;
            this.score++;
            increment = 1;
        } else if(hasVoted == -1 && isUpVote) {
            action = 1;
            this.score += 2;
            increment = 2;
        } else if(hasVoted == 1 && !isUpVote) {
            action = -1;
            this.score -= 2;
            increment = -2;
        }
        setVote(userId, action);
        CommentHandler.vote(this.id, increment, new Callback() {
            @Override
            public void run(boolean success, Object result) {
                if(success)
                    score = (int)result;
            }
        });
        return action;
    }

    public int getScore() {
        return score;
    }

    private int hasVoted(String userId) {
        Log.d("Comment Activity", commentActivity.toString());
        for (Activity a : commentActivity) {
            if(a.userId.equals(userId))
                return a.action;
        }
        return 0;
    }

    private void setVote(String userId, int vote) {
        boolean found = false;
        for (Activity a : commentActivity) {
            if(a.userId.equals(userId)) {
                a.action = vote;
                found = true;
            }
        }
        if(!found) {
            commentActivity.add(new Activity(userId, vote));
        }
    }

    private class Activity {
        public String userId;
        public int action;

        public Activity(String userId, int action) {
            this.userId = userId;
            this.action = action;
        }
    }
}
