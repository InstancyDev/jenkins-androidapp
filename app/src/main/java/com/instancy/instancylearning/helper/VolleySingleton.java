package com.instancy.instancylearning.helper;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instancy.instancylearning.interfaces.StringResultListner;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class VolleySingleton {

    private static final String TAG = VolleySingleton.class.getSimpleName();
    private static VolleySingleton instance;
    private static RequestQueue requestQueue;

    public VolleySingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }


    //this is so you don't need to pass context each time
    public static synchronized VolleySingleton getInstance() {
        if (null == instance) {
            throw new IllegalStateException(VolleySingleton.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public static void stringRequests(final String defaultSiteUrl, final StringResultListner<String> listeners) {

        StringRequest req = new StringRequest(defaultSiteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//             VolleyLog.v("Response:%n %s", response);
                listeners.getResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                listeners.getError(error.getMessage());
            }

        });

        requestQueue.add(req);

    }
}
