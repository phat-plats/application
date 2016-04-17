package com.phat_plats.scanitfortheplanet.network;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.network.model.Comment;
import com.phat_plats.scanitfortheplanet.network.model.Product;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;
import com.phat_plats.scanitfortheplanet.search.model.QueryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProductHandler {

    public static void getProduct(String upc, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("upc", upc);

        HttpUtil.get("lookup/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("PRODUCT LOOKUP CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    JSONObject product = serverResp.getJSONObject("product");
                    if (serverResp.getBoolean("success") && statusCode == 200) {
                        String upc = product.getString("upc");
                        String name = product.getString("name");
                        String imageUrl = product.getString("imageUrl");
                        int recycle = product.getInt("recycle");
                        JSONArray array = product.getJSONArray("harmful_ingredients");
                        ArrayList<String> list = new ArrayList();
                        for (int i = 0; i < array.length();list.add(array.getString(i++)));
                        callback.run(true, new Product(upc, name, recycle, imageUrl, list));
                    } else
                        callback.run(false, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void doSearch(String query, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("term", query);

        HttpUtil.get("search/", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("PRODUCT SEARCH CALLBACK", statusCode + " headers: " + headers.toString());
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                    if (serverResp.getBoolean("success")) {
                        JSONArray array = serverResp.getJSONArray("items");
                        ArrayList<QueryItem> list = new ArrayList();
                        for (int i = 0; i < array.length(); i++) {
                            String upc = array.getJSONObject(i).getString("upc");
                            String name = array.getJSONObject(i).getString("name");
                            Log.d("PRODUCT SEARCH CALLBACK", "Search Result: " + upc + " | " + name);
                            list.add(new QueryItem(name, upc));
                        }
                        callback.run(true, list);
                    } else
                        callback.run(false, null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
