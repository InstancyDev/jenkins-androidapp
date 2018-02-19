package com.instancy.instancylearning.helper;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Upendranath on 1/30/2018.
 */

public class MultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final String base64EncodedCredentials;
    private final String mMimeType;
    private final byte[] mMultipartBody;

    public MultipartRequest(String url, String base64EncodedCredentials, String mimeType, byte[] multipartBody, Response.Listener<NetworkResponse> listener, Response.ErrorListener
            errorListener) {
        super(Method.POST, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.base64EncodedCredentials = base64EncodedCredentials;
        this.mMimeType = mimeType;
        this.mMultipartBody = multipartBody;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + base64EncodedCredentials);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return mMimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return mMultipartBody;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

}
