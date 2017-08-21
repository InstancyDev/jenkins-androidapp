package com.instancy.instancylearning.asynchtask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.instancy.instancylearning.interfaces.XmlDownloadListner;
import com.instancy.instancylearning.models.MyLearningModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.instancy.instancylearning.utils.Utilities.copyFile;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class DownloadXmlAsynchTask extends AsyncTask<String, Integer, Void> {


    Context context;
    boolean isTraxkListView;
    MyLearningModel learningModel;
    String siteUrl;
   public   XmlDownloadListner xmlDownloadListner;

    public DownloadXmlAsynchTask(Context context, boolean isTraxkListView, MyLearningModel learningModel, String siteUrl) {
        this.context = context;
        this.isTraxkListView = isTraxkListView;
        this.learningModel = learningModel;
        this.siteUrl = siteUrl;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
        String downloadDestFolderPath = context.getExternalFilesDir(null)
                + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();
        String workflowXMLLocalPath = downloadDestFolderPath
                + "/content.xml";
        (new File(downloadDestFolderPath)).mkdirs();
        File f = new File(workflowXMLLocalPath);
        if (f.exists()) {
            f.delete();
        }
        String workflowXMLSourcePath;
        if (isTraxkListView) {
            workflowXMLSourcePath = siteUrl
                    + "content/sitefiles/" + learningModel.getContentID()
                    + "/content.xml";
        } else {
            workflowXMLSourcePath = siteUrl
                    + "content/publishfiles/" + learningModel.getContentID()
                    + "/EventContent.xml";
        }

        URL url = null;
        try {
            url = new URL(workflowXMLSourcePath);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("HEAD");
            conn.connect();
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                workflowXMLSourcePath = siteUrl
//                        + "content/sitefiles/" + learningModel.getContentID()
//                        + "/content.xml";
//            } else {
//                workflowXMLSourcePath = siteUrl
//                        + "content/publishfiles/"
//                        + learningModel.getContentID() + "/content.xml";
//            }

        } catch (Exception e) {
//            workflowXMLSourcePath = siteUrl + "content/sitefiles/"
//                    + learningModel.getContentID() + "/content.xml";
        }

        try {
            InputStream input = new BufferedInputStream(
                    url.openStream());
            OutputStream output = new FileOutputStream(
                    workflowXMLLocalPath);

            copyFile(input, output);
            input.close();
            input = null;
            output.flush();
            output.close();
            output = null;
        } catch (Exception e) {
            Log.e("workflowXMLcopyFile", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        xmlDownloadListner.completedXmlFileDownload();
    }
}
