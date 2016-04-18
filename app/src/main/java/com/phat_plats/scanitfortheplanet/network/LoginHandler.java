package com.phat_plats.scanitfortheplanet.network;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.phat_plats.scanitfortheplanet.R;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.network.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginHandler {

    public static String currentUser = null;

    public static void showLoginDialog(Context context, final Callback callback) {
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.login_dialog, null);
        final EditText username = (EditText)dialogView.findViewById(R.id.username);
        final EditText password = (EditText)dialogView.findViewById(R.id.password);

        final TextView errorText = (TextView) dialogView.findViewById(R.id.error_text);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Please Login/Register")
                .setPositiveButton("Login", null) //Set to null. We override the onclick
                .setNeutralButton("Register", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface di) {
                Button login = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String user = username.getText().toString();
                        String pass = password.getText().toString();
                        if(!"".equals(user) && !"".equals(pass)) {
                            Log.d("LOGIN CLICK", "logging in...");
                            verifyCredentials(user, pass, new Callback() {
                                @Override
                                public void run(boolean success, Object result) {
                                    if(success) {
                                        currentUser = user;
                                        dialog.dismiss();
                                        callback.run(true, currentUser);
                                    } else {
                                        errorText.setText("Invalid login, please try again");
                                        errorText.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        } else {
                            errorText.setText("Please fill all fields");
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
                });

                Button register = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String user = username.getText().toString();
                        String pass = password.getText().toString();
                        Log.d("REGISTER CLICK", "registering...");
                        if(!"".equals(user) && !"".equals(pass)) {
                            registerUser(user, pass, new Callback() {
                                @Override
                                public void run(boolean success, Object result) {
                                    if(success) {
                                        currentUser = user;
                                        dialog.dismiss();
                                        callback.run(true, currentUser);
                                    } else {
                                        errorText.setText("Try a different username");
                                        errorText.setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                        } else {
                            errorText.setText("Please fill all fields");
                            errorText.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(currentUser == null)
                    callback.run(false, null);
            }
        });
        dialog.show();
    }

    public static void verifyCredentials(final String username, String password, final Callback callback) {
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        HttpUtil.post("auth/", rp, new JsonHttpResponseHandler() {
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