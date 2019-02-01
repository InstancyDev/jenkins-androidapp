package com.instancy.instancylearning.synchtasks;


import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonParser;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.utils.Utilities;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.instancy.instancylearning.utils.Utilities.convertStreamToString;

public class WebAPIClient {
    private HttpURLConnection httpURLConnection;
    URL url;
    String requestURL;
    private InputStream inputStream;
    private Context ctx;
    private PreferencesManager preferencesManager;

    public WebAPIClient() {

    }

    public WebAPIClient(Context context) {
        this.ctx = context;
        PreferencesManager.initializeInstance(context);
        preferencesManager = PreferencesManager.getInstance();
    }


    public String getSiteAPIDetails(String siteurl, boolean isMainSite) {
        String requestURL = siteurl + "/PublicModules/SiteAPIDetails.aspx";
        Log.d("TAG", "getSiteAPIDetails: " + requestURL);
        String strAPIURL = "";
        inputStream = null;
        httpURLConnection = null;

        try {
            URL url = new URL(requestURL);


            httpURLConnection = (HttpURLConnection) url.openConnection();

            int statusCode = httpURLConnection.getResponseCode();

            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                String result = Utilities.convertStreamToString(inputStream);
                if (result.indexOf("<!DOCTYPE html>") > -1) {
                    strAPIURL = result.split("<!DOCTYPE html>")[0]
                            .toString().trim();

                    if (!Utilities.isValidString(strAPIURL)) {
                        strAPIURL = "";
                    } else {
                        Log.d("webapiurl", strAPIURL);
                        if (isMainSite) {
                            preferencesManager.setStringValue(strAPIURL, StaticValues.KEY_WEBAPIURL);
                        } else {
                            preferencesManager.setStringValue(strAPIURL, StaticValues.SUB_KEY_WEBAPIURL);
                        }

                    }
                }
            } else {
                strAPIURL = "";
            }
        } catch (Exception e) {
            Log.d("In GetSiteAPIDetails",
                    "unable to get entity from response" + e.getMessage());
            strAPIURL = "";
        }


        return strAPIURL;
    }

    /**
     * To get the Authentication string of a specific web API URL.
     *
     * @author Venu
     */
    public void getAPIAuthDetails(String siteUrl, String strAPIURL, boolean isMainSite) {

        String requestURL = strAPIURL + "/MobileLMS/GetAPIAuthDetails"
                + "?AppURL=" + siteUrl;
        Log.d("AUTH", "getAPIAuthDetails: " + requestURL);
        inputStream = null;
        httpURLConnection = null;

        try {
            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            int statusCode = httpURLConnection.getResponseCode();


            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                String result = Utilities.convertStreamToString(inputStream);
                result = result.substring(1, result.lastIndexOf(","));
                result = result.replaceAll(",", ":");
                Log.d("Auth details " + isMainSite, result);

                if (isMainSite) {
                    preferencesManager.setStringValue(result, StaticValues.KEY_AUTHENTICATION);
                } else {
                    preferencesManager.setStringValue(result, StaticValues.SUB_KEY_AUTHENTICATION);
                }

            } else {
                Log.e("getAuthDetails",
                        "No response from site.\nUsing default API key.");
            }

        } catch (Exception e) {
            Log.e("getAuthDetails", e.getMessage());
        }
    }

    public void getAPIAuthDetailsForDigi(String siteUrl, String strAPIURL, boolean isMainSite) {

        String requestURL = "https://masterapi.instancy.com//api//Authentication/GetAPIAuth?AppURL=" + siteUrl;


        Log.d("AUTH", "getAPIAuthDetails: " + requestURL);
        inputStream = null;
        httpURLConnection = null;

        try {
            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            int statusCode = httpURLConnection.getResponseCode();


            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                String result = Utilities.convertStreamToString(inputStream);

                JSONObject jsonObject = new JSONObject(result);

                String webAuth = jsonObject.getString("basicAuth");

                Log.d("Auth details " + isMainSite, result);

                if (isMainSite) {
                    preferencesManager.setStringValue(webAuth, StaticValues.KEY_AUTHENTICATION);
                } else {
                    preferencesManager.setStringValue(webAuth, StaticValues.SUB_KEY_AUTHENTICATION);
                }

            } else {
                Log.e("getAuthDetails",
                        "No response from site.\nUsing default API key.");
            }

        } catch (Exception e) {
            Log.e("getAuthDetails", e.getMessage());
        }
    }

    public String getSiteAPIDetailsForDigi(String siteUrl, boolean isMainSite,Context context) {
        String requestURL =context.getResources().getString(R.string.app_default_auth_url)+ "Authentication/GetAPIAuth?AppURL=" + siteUrl;
//        String requestURL = "http://angularbasicapi.instancysoft.com/api/Authentication/GetAPIAuth?AppURL=" + siteUrl;

        String webApiUrl = "";
        Log.d("AUTH", "getAPIAuthDetails: " + requestURL);
        inputStream = null;
        httpURLConnection = null;

        try {
            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();

            int statusCode = httpURLConnection.getResponseCode();


            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                String result = Utilities.convertStreamToString(inputStream);

                JSONObject jsonObject = new JSONObject(result);

                webApiUrl = jsonObject.getString("WebAPIUrl");

                Log.d("Auth details " + isMainSite, result);

                Log.d("webapiurl", webApiUrl);
                if (isMainSite) {
                    preferencesManager.setStringValue(webApiUrl, StaticValues.KEY_WEBAPIURL);
                } else {
                    preferencesManager.setStringValue(webApiUrl, StaticValues.SUB_KEY_WEBAPIURL);
                }


            } else {
                Log.e("getAuthDetails",
                        "No response from site.\nUsing default API key.");
            }

        } catch (Exception e) {
            Log.e("getAuthDetails", e.getMessage());
        }

        return webApiUrl;
    }


    public InputStream callWebAPIMethod(String webAPIURL,
                                        String controllerName, String actionName, String authentication,
                                        String paramsString) {
        inputStream = null;
        httpURLConnection = null;

        requestURL = webAPIURL + "/" + controllerName + "/" + actionName
                + "?" + paramsString;

        requestURL = requestURL.replace(" ", "%20");
        requestURL = requestURL.replace(">", "%3E");
        try {

            Log.d("TAG", "HERE " + requestURL);

            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            String base64EncodedCredentials = Base64.encodeToString(
                    authentication.getBytes(), Base64.NO_WRAP);
            httpURLConnection.setRequestProperty("Authorization", "Basic "
                    + base64EncodedCredentials);
            int statusCode = httpURLConnection.getResponseCode();


            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());


            }
        } catch (Exception ex) {
            ex.printStackTrace();
            inputStream = null;
        }

        return inputStream;
    }

    public int checkFileFoundOrNot(String siteurl, String authHeaders) {
        siteurl = siteurl.replaceAll(" ", "%20");
        final int[] statusCode = new int[1];
        try {

            final String finalSiteurl = siteurl;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(finalSiteurl)
                    .head()
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            statusCode[0] = response.code();

        } catch (Exception e) {
            Log.d("In GetSiteAPIDetails",
                    "unable to get entity from response" + e.getMessage());
            statusCode[0] = 0;
        }

        return statusCode[0];
    }


    public InputStream synchronousPostMethod(String requestURL, String authentication,
                                             String postData) {
        inputStream = null;
        httpURLConnection = null;

//        requestURL = requestURL.replace(" ", "%20");

        String encodedPostData = "";
        String tempData = "\"" + postData + "\"";
        try {

            encodedPostData = URLEncoder.encode(tempData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            Log.d("TAG", "SYNC URL " + requestURL);
            Log.d("TAG", "HERE " + encodedPostData);

            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            String base64EncodedCredentials = Base64.encodeToString(
                    authentication.getBytes(), Base64.NO_WRAP);
            httpURLConnection.setRequestProperty("Authorization", "Basic "
                    + base64EncodedCredentials);

            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Content-type", "application/json");


            httpURLConnection.setReadTimeout(15000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(tempData);

            writer.flush();
            writer.close();
            os.close();

            int statusCode = httpURLConnection.getResponseCode();


            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());


            }
        } catch (Exception ex) {
            ex.printStackTrace();
            inputStream = null;
        }

        return inputStream;
    }

    public String getInputStreamForSearchResults(String requestURL, String authentication) {
        inputStream = null;
        httpURLConnection = null;
        requestURL = requestURL.replace(" ", "%20");

        try {

            Log.d("TAG", "HERE " + requestURL);

            URL url = new URL(requestURL);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            String base64EncodedCredentials = Base64.encodeToString(
                    authentication.getBytes(), Base64.NO_WRAP);
            httpURLConnection.setRequestProperty("Authorization", "Basic "
                    + base64EncodedCredentials);
            int statusCode = httpURLConnection.getResponseCode();

            if (statusCode == 200) {

                inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            inputStream = null;
        }


        return convertStreamToString(inputStream);
    }


}
