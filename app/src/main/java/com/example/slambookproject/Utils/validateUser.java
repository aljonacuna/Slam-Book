package com.example.slambookproject.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.slambookproject.GUI.Login;
import com.example.slambookproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class validateUser {
    static urlClass localip = new urlClass();

    public static interface checkUser {
        void onCallback(boolean isValid) throws JSONException;
    }

    public static void ValidateUser(final Context context, String uname, final String passTxt,final String sqTxt,
            final String ansTxt, final String tag, final checkUser myCallback) {
        String url = localip.local_host + "users/validate_account.php?user=" + uname;
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        final JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int x = 0; x < response.length(); x++) {
                            try {
                                JSONObject obj = response.getJSONObject(x);
                                final String code = obj.getString("code");
                                if (tag.equals("register")) {
                                    try {
                                        if (code.equals("001")) {
                                            myCallback.onCallback(false);
                                        } else {
                                            myCallback.onCallback(true);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (tag.equals("login")) {
                                    if (code.equals("001")) {
                                        try {
                                            String pass = obj.getString("password");
                                            if (passTxt.equals(pass)) {
                                                myCallback.onCallback(true);
                                            } else {
                                                myCallback.onCallback(false);
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(context, "Catch E: " + e.toString(), Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }else{
                                        myCallback.onCallback(false);
                                    }
                                } else if (tag.equals("forgot")) {
                                    if (code.equals("001")) {
                                        try {
                                            String sq = obj.getString("sq");
                                            String ans = obj.getString("ans");
                                            String pass = obj.getString("password");
                                            if (sqTxt.equals(sq) && ansTxt.equals(ans)){
                                                myCallback.onCallback(true);
                                                final Dialog dialog = new Dialog(context);
                                                dialog.setContentView(R.layout.show_password);
                                                dialog.setCancelable(false);
                                                dialog.show();
                                                TextView textView = dialog.findViewById(R.id.show_pass);
                                                Button button = dialog.findViewById(R.id.bcktologin);
                                                textView.setText("Your password is: "+pass);
                                                button.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                        ((Activity)context).finish();
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }else{
                                                myCallback.onCallback(false);
                                            }
                                        } catch (JSONException e) {

                                        }
                                    }else{
                                        myCallback.onCallback(false);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Volley Error" + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        objectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 3000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(objectRequest);
    }
}
