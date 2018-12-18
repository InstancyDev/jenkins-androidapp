package com.instancy.instancylearning.askexpertenached;

import android.util.Base64;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    public BasicAuthInterceptor(String basicAuth) {
        String base64EncodedCredentials = Base64.encodeToString(String.format(basicAuth).getBytes(), Base64.NO_WRAP);

        this.credentials = "Basic " + base64EncodedCredentials;
    }

    public BasicAuthInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }

}
