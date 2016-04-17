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

    public static String currentUser;

    public static void verifyCredentials(final String username, String password, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        HttpUtil.get("auth/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("CREDENTIALS CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    if(serverResp.getBoolean("success"))
                        currentUser = username;
                    callback.run(serverResp.getBoolean("success"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void registerUser(final String username, String password, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        HttpUtil.post("register/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("REGISTER CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    if(serverResp.getBoolean("success"))
                        currentUser = username;
                    callback.run(serverResp.getBoolean("success"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}