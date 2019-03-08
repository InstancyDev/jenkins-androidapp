package com.instancy.instancylearning.mainactivities;

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
import android.view.MenuItem;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.ScrollBar;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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

        if (bundle != null) {
            coursePdf = bundle.getString("PDF_URL");
            courseName = bundle.getString("PDF_FILENAME");
            isOnline = bundle.getString("ISONLINE");
            myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        }

        pdfView = (PDFView) findViewById(R.id.pdfView);
        ScrollBar scrollBar = (ScrollBar) findViewById(R.id.scrollBar);
        pdfView.setScrollBar(scrollBar);
        pdfView.setHorizontalScrollBarEnabled(false);
        TextView pdfTitleText = (TextView) findViewById(R.id.pdf_course_title);
        pdfTitleText.setText(courseName);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
