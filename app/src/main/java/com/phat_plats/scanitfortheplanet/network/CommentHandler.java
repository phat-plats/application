package com.phat_plats.scanitfortheplanet.network;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.network.model.Comment;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CommentHandler {

    public static void getComments(String upc, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("upc", upc);

        HttpUtil.get("posts/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("POST LIST CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONArray array = serverResp.getJSONArray("posts");
                    ArrayList<Comment> list = new ArrayList();
                    for (int i = 0; i < array.length(); i++) {
                        int id = array.getJSONObject(i).getInt("id");
                        int score = array.getJSONObject(i).getInt("score");
                        String poster = array.getJSONObject(i).getString("poster");
                        String content = array.getJSONObject(i).getString("contents");
                        list.add(new Comment(id, score, poster, content));
                    }
                    callback.run(serverResp.getBoolean("success"), list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void writeComment(String upc, String poster, String contents, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("upc", upc);
        rp.add("poster", poster);
        rp.add("contents", contents);

        HttpUtil.post("post/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("POST CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    int id = serverResp.getInt("id");
                    int score = serverResp.getInt("score");
                    String poster = serverResp.getString("poster");
                    String content = serverResp.getString("contents");
                    callback.run(serverResp.getBoolean("success"), new Comment(id, score, poster, content));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void vote(int id, boolean up, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("id", Integer.toString(id));
        rp.add("up", Boolean.toString(up));

        HttpUtil.post("vote/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("VOTE CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    callback.run(serverResp.getBoolean("success"), serverResp.get("score"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
