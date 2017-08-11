package com.instancy.instancylearning.asynchtask;

import android.content.Context;
import android.os.AsyncTask;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.SiteConfigInterface;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.StaticValues;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class GetSiteConfigsAsycTask extends AsyncTask<String, Integer, Void> {

    public SiteConfigInterface siteConfigInterface;
    WebAPIClient webAPIClient;
    Context context;
    DatabaseHandler db;

    public GetSiteConfigsAsycTask(Context context) {
        this.context = context;
        webAPIClient = new WebAPIClient(context);
        db = new DatabaseHandler(context);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        siteConfigInterface.preExecuteIn();
    }

    @Override
    protected Void doInBackground(String... params) {
        int i = 10;
        publishProgress(i);
        String tempWebApiUrl = webAPIClient.getSiteAPIDetails(params[0]);
        i = i + 10;
        publishProgress(i);
        webAPIClient.getAPIAuthDetails(params[0], tempWebApiUrl);
        i = i + 10;
        publishProgress(i);
        db.getSiteSettingsServer(tempWebApiUrl, params[0]);
        i = i + 10;
        publishProgress(i);
        db.getNativeMenusFromServer(tempWebApiUrl, params[0]);
        i = i + 10;
        publishProgress(i);
        db.getSiteTinCanDetails(tempWebApiUrl, params[0]);
        i = i + 10;
        publishProgress(i);
        db.downloadSplashImages(params[0]);
        i = i + 10;
        publishProgress(i);
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        uiSettingsModel = db.getAppSettingsFromLocal(params[0], "374");
        i = i + 10;
        publishProgress(i);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        siteConfigInterface.progressUpdateIn(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        siteConfigInterface.postExecuteIn("");
    }
}
