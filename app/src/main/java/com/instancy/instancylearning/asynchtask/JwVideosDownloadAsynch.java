package com.instancy.instancylearning.asynchtask;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.instancy.instancylearning.interfaces.JwFileDownloadInterface;
import com.instancy.instancylearning.interfaces.SiteConfigInterface;

import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.copyFile;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class JwVideosDownloadAsynch extends AsyncTask<String, Integer, Void> {

    Context context;
    List<String> jwFileArray = new ArrayList<>();
    //  JwFileDownloadInterface jwFileDownloadInterface;
    List<String> jwFileLocalPathsArray = new ArrayList<>();

    public JwVideosDownloadAsynch(Context context, List<String> jwFileArray, List<String> jwFileLocalPathsArray) {
        this.context = context;
        this.jwFileArray = jwFileArray;
        this.jwFileLocalPathsArray = jwFileLocalPathsArray;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("JWD", "onProgressUpdate: ");
    }

    @Override
    protected Void doInBackground(String... strings) {

        if (jwFileArray != null) {
            for (int i = 0; i < jwFileArray.size(); i++) {
                //   jwFileDownloadInterface.progressUpdateIn(i);
                Log.d("JWD", "doInBackground: " + jwFileArray.get(i));
                Log.d("JWD", "doInBackground: " + jwFileLocalPathsArray.get(i));

                String jwUrlStr = jwFileArray.get(i);

//                String fileName = jwUrlStr.substring(jwUrlStr.lastIndexOf("/") + 1, jwUrlStr.indexOf("."));
                String fileName = jwUrlStr.substring(jwUrlStr.lastIndexOf('/') + 1, jwUrlStr.length());

                String jwLocalPath = jwFileLocalPathsArray.get(i);

                int index = jwLocalPath.lastIndexOf('/');
                String offlinePathWithoutStartPage = jwLocalPath.substring(0, index) + "";

                String offlinePath = offlinePathWithoutStartPage + "/jwvideos";

                try {
                    boolean success = (new File(offlinePath)).mkdirs();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }

                final String finalDownloadedFilePath = "file://" + offlinePath + "/" + fileName;

                File file = new File(finalDownloadedFilePath);

                Uri destinationUri = Uri.parse(finalDownloadedFilePath);

                Uri uri = Uri.parse(jwUrlStr); // Path where you want to download file.

//                Uri destinationUri = FileProvider.getUriForFile(
//                        context,
//                        context.getApplicationContext()
//                                .getPackageName() + ".provider", file);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
                request.setVisibleInDownloadsUi(true);

                try {
                    request.setDestinationUri(destinationUri);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
//                request.setDestinationInExternalPublicDir(file.getAbsolutePath(),"");  // Storage directory path
                ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
                Log.d("JWD", "doInBackground: " + i);
            }
            Log.d("JWD", "doInBackground: ");
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("JWD", "onPreExecute: ");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //jwFileDownloadInterface.postExecuteIn();
        Log.d("JWD", "onPostExecute: ");
    }


}
