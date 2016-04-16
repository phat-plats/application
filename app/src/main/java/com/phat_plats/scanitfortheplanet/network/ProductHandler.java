package com.phat_plats.scanitfortheplanet.network;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.network.model.Product;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProductHandler {

    public static void getProduct(String upc, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("upc", upc);

        HttpUtil.get("lookup/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("PRODUCT SEARCH CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    if (serverResp.getBoolean("success")) {
                        String upc = serverResp.getString("upc");
                        String name = serverResp.getString("name");
                        int recycle = serverResp.getInt("recycle");
                        JSONArray array = serverResp.getJSONArray("harmful_ingredients");
                        ArrayList<String> list = new ArrayList();
                        for (int i = 0; i < array.length();list.add(array.getString(i++)));
                        callback.run(true, new Product(upc, name, recycle, list));
                    } else
                        callback.run(false, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
