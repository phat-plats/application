package com.phat_plats.scanitfortheplanet.network;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginHandler {

    public static void verifyCredentials(String username, String password, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        HttpUtil.get("auth/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("CREDENTIALS CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    callback.run(serverResp.get("success") == true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void registerUser(String username, String password, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        HttpUtil.post("register/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("REGISTER CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    callback.run(serverResp.get("success") == true, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

// POST : /post/
// params
// upc, poster, contents
// success

// GET: /posts/
// param
// upc
// success, posts

// POST: /vote/
// param
// up (bool)
// success, score