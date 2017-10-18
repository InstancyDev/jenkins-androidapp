package com.instancy.instancylearning.helper;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class VollyService {

    IResult mResultCallback = null;
    Context mContext;
    AppController appController;
    AppUserModel appUserModel;
    private String TAG = VollyService.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;


    public VollyService() {
    }

    public VollyService(IResult resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
        appController = AppController.getInstance();
        appUserModel = AppUserModel.getInstance();
    }

    public void postDataVolley(final String requestType, String url, JSONObject sendObj) {
        Log.d(TAG, "getJsonObjResponseVolley: " + url);

        try {
//            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObj = new JsonObjectRequest(url, sendObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            });

            VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObj);

        } catch (Exception e) {

        }
    }

    public void getJsonObjResponseVolley(final String requestType, String url, final String authHeaders) {
        try {

            Log.d(TAG, "getJsonObjResponseVolley: " + url);
            final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                    Log.d("logr  response =", "response " + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    String base64EncodedCredentials = Base64.encodeToString(String.format(authHeaders).getBytes(), Base64.NO_WRAP);
                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
                    return headers;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        volleyError = error;
//                        Log.d("logr  error =", "Status code "+volleyError.networkResponse.statusCode);

                    }
                    return volleyError;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjReq);

        } catch (Exception e) {

        }
    }

    public void getJsonObjResponseVolley(final String requestType, String url, final String authHeaders, final MyLearningModel learningModel) {
        try {

            Log.d(TAG, "getJsonObjResponseVolley: " + url);
            final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (mResultCallback != null)
                        mResultCallback.notifySuccessLearningModel(requestType, response, learningModel);
                    Log.d("logr  response =", "response " + response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    String base64EncodedCredentials = Base64.encodeToString(String.format(authHeaders).getBytes(), Base64.NO_WRAP);
                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
                    return headers;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        volleyError = error;
//                        Log.d("logr  error =", "Status code "+volleyError.networkResponse.statusCode);

                    }
                    return volleyError;
                }
            };
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjReq);

        } catch (Exception e) {

        }
    }

    public void getStringResponseVolley(final String requestType, String url, final String authHeaders) {
        Log.d(TAG, "getStringResponseVolley: " + url);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String _response) {

                            Log.d("logr  _response =", _response);
                            if (mResultCallback != null)
                                mResultCallback.notifySuccess(requestType, _response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // Error handling
//                    Log.d("logr  error =", error.getMessage());

                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    String base64EncodedCredentials = Base64.encodeToString(authHeaders.getBytes(), Base64.NO_WRAP);

                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
                    return headers;
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                        VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                        volleyError = error;
                        Log.d("logr  error =", "Status code " + volleyError.networkResponse.statusCode);

                    }
                    return volleyError;
                }
            };
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);

        } catch (Exception e) {

        }
    }

}
