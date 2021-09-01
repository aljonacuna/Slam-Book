package com.example.slambookproject.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class getEntryimg {
    public static interface getImagecallback {
        void onCallback(Bitmap bitmap);
    }

    public static void imgentry(Context context, int id, final getImagecallback myCallback) {
        String url = urlClass.local_host + "entry/entry_img.php?id=" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.start();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int x = 0; x < response.length(); x++) {
                    try {
                        JSONObject object = response.getJSONObject(x);
                        String code = object.getString("code");
                        if (code.equals("001")) {
                            byte[] toimgbyte = Base64.decode(object.getString("image"), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(toimgbyte, 0, toimgbyte.length);
                            myCallback.onCallback(bitmap);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LogError",error.toString());
            }
        });
        jsonArrayRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 3000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 3000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
