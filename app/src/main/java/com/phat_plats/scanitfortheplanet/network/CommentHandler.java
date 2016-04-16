package com.phat_plats.scanitfortheplanet.network;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
                    callback.run(serverResp.get("success") == true, serverResp.get("posts"));
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
                    callback.run(serverResp.get("success") == true, serverResp.get("posts"));
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
                    callback.run(serverResp.get("success") == true, serverResp.get("score"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
