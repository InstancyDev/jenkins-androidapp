package com.instancy.instancylearning.mainactivities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;
//import com.thin.downloadmanager.DownloadManager;
//import com.thin.downloadmanager.DownloadRequest;
//import com.thin.downloadmanager.DownloadStatusListenerV1;
//import com.thin.downloadmanager.ThinDownloadManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringMethod;


public class PdfViewer_Activity extends AppCompatActivity implements OnPageChangeListener {

    PreferencesManager preferencesManager;
    String TAG = PdfViewer_Activity.class.getSimpleName();

    PDFView pdfView;
    String coursePdf = "";
    SVProgressHUD svProgressHUD;
    String isOnline;
    Integer pageNumber = 0;
    Uri uri;
    MyLearningModel myLearningModel;

    UiSettingsModel uiSettingsModel;

    boolean isCertificate = false;

    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        svProgressHUD = new SVProgressHUD(this);
        Bundle bundle = getIntent().getExtras();
        String courseName = "";
        uiSettingsModel = UiSettingsModel.getInstance();
        if (bundle != null) {
            coursePdf = bundle.getString("PDF_URL");
            courseName = bundle.getString("PDF_FILENAME");
            isOnline = bundle.getString("ISONLINE");
            myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");


            isCertificate = bundle.getBoolean("ISCERTIFICATE", false);

            if (!coursePdf.contains("https"))
                coursePdf = coursePdf.replace("http", "https");

        }

        pdfView = (PDFView) findViewById(R.id.pdfView);
        ScrollBar scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
        pdfView.setScrollBar(scrollBar);
        pdfView.setHorizontalScrollBarEnabled(false);
        pdfView.setVerticalScrollBarEnabled(false);
        TextView pdfTitleText = (TextView) findViewById(R.id.pdf_course_title);
//        pdfTitleText.setText(courseName);

        pdfTitleText.setText(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                courseName + "</font>"));

        if (isOnline.equalsIgnoreCase("YES")) {
            new RetriveStreamFromAsynchTask().execute(coursePdf);
        } else {
            Uri myUri = Uri.parse(coursePdf);
            pdfView.fromUri(myUri)
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .load();
        }
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'>" + courseName + "</font>"));
//        getSupportActionBar().setSubtitle(courseName);
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    private class RetriveStreamFromAsynchTask extends AsyncTask<String, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        }

        @Override
        protected InputStream doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

//            svProgressHUD.dismiss();
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            if (inputStream != null) {

                pdfView.fromStream(inputStream).showPageWithAnimation(true).load();
            }
            svProgressHUD.dismiss();
        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = getIntent();
        intent.putExtra("myLearningDetalData", myLearningModel);
        setResult(RESULT_OK, intent);

        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = getIntent();
                intent.putExtra("myLearningDetalData", myLearningModel);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.ctx_certificateaction:
                downloadCertificate(coursePdf, myLearningModel);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.certificatedownloadmenu, menu);
        MenuItem item_certificate = menu.findItem(R.id.ctx_certificateaction);

        if (isCertificate)
            item_certificate.setVisible(true);
        else
            item_certificate.setVisible(false);

        if (item_certificate != null) {
            Drawable filterDrawable = getDrawableFromStringMethod(R.string.fa_icon_download, this, uiSettingsModel.getAppHeaderTextColor());
            item_certificate.setIcon(filterDrawable);
        }

        return true;
    }

//    public void downloadCertificate(String downloadStruri, final MyLearningModel learningModel) {
//
//
//        ThinDownloadManager downloadManager = new ThinDownloadManager();
//        Uri downloadUri = Uri.parse(downloadStruri);
//
//
////        String localizationFolder = "";
////        String[] startPage = null;
////        if (learningModel.getStartPage().contains("/")) {
////            startPage = learningModel.getStartPage().split("/");
////            localizationFolder = "/" + startPage[0];
////        } else {
////            localizationFolder = "";
////        }
//
//
//        File dir = new File("//sdcard//Download//");
//
//        File file = new File(dir, learningModel.getCourseName() + ".pdf");
//
//        final Uri destinationUri = Uri.parse(file.getAbsolutePath());
//
//
//        Log.d(TAG, "downloadThin: " + downloadUri);
//        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
//                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
//                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH).setDeleteDestinationFileOnFailure(false)
//                .setStatusListener(new DownloadStatusListenerV1() {
//                    @Override
//                    public void onDownloadComplete(DownloadRequest downloadRequest) {
//
//                        Toast.makeText(PdfViewer_Activity.this, getLocalizationValue(JsonLocalekeys.certificatedownloadedalert_alerttitile) + " " + destinationUri.getPath(), Toast.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
//
//                        Toast.makeText(PdfViewer_Activity.this, getLocalizationValue(JsonLocalekeys.certificatedownloadedfailedalert_alerttitile), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
////
//                    }
//
//
//                });
//        int downloadId = downloadManager.add(downloadRequest);
//    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, this);
    }

    public void downloadThroughDownloadManage(String downloadStruri, final MyLearningModel learningModel) {


    }

    private void downloadCertificate(String downloadStruri, final MyLearningModel learningModel) {

        File dir = new File("//sdcard//Download//");

        File file = new File(dir, learningModel.getCourseName() + ".pdf");

        final Uri destinationUri = Uri.parse(downloadStruri);

        Uri uri = Uri.parse(downloadStruri); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(learningModel.getCourseName()); // Title for notification.
        request.setVisibleInDownloadsUi(true);
//        request.setDestinationInExternalPublicDir(file.getAbsolutePath(), uri.getLastPathSegment());  // Storage directory path
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
    }
}
