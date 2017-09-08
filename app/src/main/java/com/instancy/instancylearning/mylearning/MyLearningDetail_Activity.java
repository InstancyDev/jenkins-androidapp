package com.instancy.instancylearning.mylearning;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.mainactivities.NativeSettings;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 6/27/2017 Working on InstancyLearning.
 */

public class MyLearningDetail_Activity extends AppCompatActivity {
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    @Bind(R.id.txt_title_name)
    TextView txtTitle;

    @Bind(R.id.btntxt_download_detail)
    TextView btnDownload;

    @Bind(R.id.txtDescription)
    TextView txtDescription;

    @Bind(R.id.txtLongDesc)
    TextView txtLongDisx;

    @Bind(R.id.imagethumb)
    ImageView imgThumb;

    @Bind(R.id.txt_coursename)
    TextView txtCourseName;

    @Bind(R.id.rat_detail_ratingbar)
    RatingBar ratingBar;

    @Bind(R.id.txt_course_progress)
    TextView txtCourseStatus;

    @Bind(R.id.course_progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.txt_author)
    TextView txtAuthor;

    @Bind(R.id.txt_site_name)
    TextView txtSiteName;

    @Bind(R.id.view_btn_txt)
    TextView txtBtnView;

    @Bind(R.id.report_btn_txt)
    TextView txtBtnReport;

    @Bind(R.id.view_fa_icon)
    TextView txtFontReport;

    @Bind(R.id.report_fa_icon)
    TextView txtFontView;

    @Nullable
    @Bind(R.id.circle_progress)
    CircleProgressBar circleProgressBar;

    @Nullable
    @Bind(R.id.consolidateline)
    View consolidateLine;

    @Nullable
    @Bind(R.id.viewLayout)
    RelativeLayout bottomViewLayout;

    @Nullable
    @Bind(R.id.bottom_button_layout)
    LinearLayout bottomLayout;


    @Nullable
    @Bind(R.id.detail_layout)
    RelativeLayout relativeLayout;


    PreferencesManager preferencesManager;
    String TAG = NativeSettings.class.getSimpleName();
    MyLearningModel myLearningModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    VollyService vollyService;
    IResult resultCallback = null;
    float ratingValue = 0;
    ResultListner resultListner = null;
    WebAPIClient webAPIClient;
    boolean isFileExist = false;
    AppController appController;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylearning_detail_activity);
        ButterKnife.bind(this);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        myLearningModel = (MyLearningModel) getIntent().getSerializableExtra("myLearningDetalData");
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        webAPIClient = new WebAPIClient(this);
        db = new DatabaseHandler(this);
        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        appController = AppController.getInstance();
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.btntxt_download_detail), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.report_fa_icon), iconFont);
        FontManager.markAsIconContainer(findViewById(R.id.view_fa_icon), iconFont);
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        bottomViewLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        txtBtnReport.setVisibility(View.GONE);
        if (myLearningModel != null) {
            txtTitle.setText(myLearningModel.getCourseName());
            txtCourseName.setText(myLearningModel.getMediaName());
            txtAuthor.setText("By " + myLearningModel.getAuthor());
            txtSiteName.setText(myLearningModel.getSiteName());
            txtLongDisx.setText(myLearningModel.getShortDes());
            // apply colors
            txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bottom_button_layout);

            linearLayout.setBackground(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));


            if (myLearningModel.getSiteName().equalsIgnoreCase("")) {
                consolidateLine.setVisibility(View.GONE);

            } else {
                consolidateLine.setVisibility(View.VISIBLE);
            }

//            txtBtnReport.setBackground(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            txtBtnView.setBackground(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//
//            txtFontView.setBackground(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
//            txtFontReport.setBackground(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

            txtLongDisx.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            ratingValue = 0;

            try {
                ratingValue = Float.parseFloat(myLearningModel.getRatingId());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                ratingValue = 0;
            }
            ratingBar.setRating(ratingValue);
            if (myLearningModel.getLongDes().isEmpty()) {
                txtLongDisx.setVisibility(View.GONE);
                txtDescription.setVisibility(View.GONE);
            } else {
                txtLongDisx.setVisibility(View.VISIBLE);
                txtDescription.setVisibility(View.VISIBLE);
            }
            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                btnDownload.setVisibility(View.GONE);
                circleProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                txtCourseStatus.setVisibility(View.GONE);
            } else {

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {
                    btnDownload.setVisibility(View.GONE);
                    circleProgressBar.setVisibility(View.GONE);
                } else {
                    File myFile = new File(myLearningModel.getOfflinepath());

                    if (myFile.exists()) {
                        btnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
                        btnDownload.setEnabled(false);
                        isFileExist = true;
                    } else {

                        btnDownload.setTextColor(getResources().getColor(R.color.colorBlack));
                        btnDownload.setEnabled(true);
                        isFileExist = false;
                        if (!appController.isAlreadyViewd()) {
                            ViewTooltip
                                    .on(btnDownload)
                                    .autoHide(true, 5000)
                                    .corner(30)
                                    .position(ViewTooltip.Position.LEFT)
                                    .text("Click to download the content").clickToHide(true)
                                    .show();
                        }

                    }

                    btnDownload.setVisibility(View.VISIBLE);
                    circleProgressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.VISIBLE);
                txtCourseStatus.setVisibility(View.VISIBLE);

            }
            String imgUrl = myLearningModel.getImageData();

//            ViewTooltip
//                    .on(imgThumb)
//                    .autoHide(true, 5000)
//                    .corner(30)
//                    .position(ViewTooltip.Position.BOTTOM)
//                    .text("Click on image to view")
//                    .show();


            Glide.with(this).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.cellimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgThumb);
            statusUpdate(myLearningModel.getStatus());
        } else {
            Toast.makeText(this, "Unable to fetch", Toast.LENGTH_SHORT).show();

        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Details</font>"));
//        getSupportActionBar().setCustomView(R.layout.drawermenu_item);
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        final float oldRating = ratingValue;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
                    int ratingInt = Math.round(rating);
                    svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
                    String paramsString = appUserModel.getWebAPIUrl() +
                            ApiConstants.UPDATERATINGURL + "UserID=" + appUserModel.getUserIDValue() +
                            "&ContentID=" + myLearningModel.getContentID()
                            + "&Title=" +
                            "&Description=From%20Android%20Native%20App" +
                            "&RatingID=" + ratingInt;
                    if (isNetworkConnectionAvailable(MyLearningDetail_Activity.this, -1)) {
                        try {

                            Log.d(TAG, "getJsonObjResponseVolley: " + paramsString);
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, paramsString, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.d("logr  response =", "response " + response.get("table1"));
                                        JSONArray jsonArray = response.getJSONArray("table1");
                                        String status = jsonArray.getJSONObject(0).get("status").toString();
                                        String rating = jsonArray.getJSONObject(0).get("rating").toString();
                                        if (status.contains("Success")) {
                                            db.updateContentRatingToLocalDB(myLearningModel, rating);
                                            Toast.makeText(
                                                    MyLearningDetail_Activity.this,
                                                    MyLearningDetail_Activity.this.getString(R.string.rating_update_success),
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            myLearningModel.setRatingId(rating);
//                                        notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(
                                                    MyLearningDetail_Activity.this,
                                                    MyLearningDetail_Activity.this.getString(R.string.rating_update_fail),
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            ratingBar.setRating(oldRating);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ratingBar.setRating(oldRating);
                                    }
                                    svProgressHUD.dismiss();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    ratingBar.setRating(oldRating);
                                    svProgressHUD.dismiss();
                                    Toast.makeText(
                                            MyLearningDetail_Activity.this,
                                            MyLearningDetail_Activity.this.getString(R.string.rating_update_fail),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    final Map<String, String> headers = new HashMap<>();
                                    String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
                                    return headers;
                                }
                            };
//                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                                0,
//                                -1,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            VolleySingleton.getInstance(MyLearningDetail_Activity.this).addToRequestQueue(jsonObjReq);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(MyLearningDetail_Activity.this, "No internet", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void statusUpdate(String courseStatus) {

        String displayStatus = "";
        if (courseStatus.equalsIgnoreCase("Completed") || (courseStatus.toLowerCase().contains("passed") || courseStatus.toLowerCase().contains("failed"))) {


            String progressPercent = "100";
            String statusValue = myLearningModel.getStatus();
            if (myLearningModel.getStatus().equalsIgnoreCase("Completed")) {
                statusValue = "Completed";

            } else if (myLearningModel.getStatus().equalsIgnoreCase("failed")) {

                statusValue = "Completed(failed)";
            } else if (myLearningModel.getStatus().equalsIgnoreCase("passed")) {

                statusValue = "Completed(passed)";

            }

            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusCompleted)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
//                courseStatus = trackChildList.getStatus() + " (" + trackChildList.getProgress();
            courseStatus = statusValue + " (" + progressPercent;

        } else if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {

//                holder.progressBar.setBackgroundColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusNotStarted)));
            progressBar.setProgress(0);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusNotStarted));
            courseStatus = myLearningModel.getStatus() + "  (0";

        } else {
            String statusValue = "In Progress";

            if (myLearningModel.getStatus().toLowerCase().equalsIgnoreCase("incomplete") || myLearningModel.getStatus().equalsIgnoreCase("") || myLearningModel.getStatus().toLowerCase().equalsIgnoreCase("in complete")) {
                statusValue = "In Progress";
            }


            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusInProgress)));
            progressBar.setProgress(Integer.parseInt(myLearningModel.getProgress()));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusInProgress));
            courseStatus = statusValue + " (" + myLearningModel.getProgress();

        }
        txtCourseStatus.setText(courseStatus + "%)");
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("UPDATESTATUS")) {

                    if (response != null) {

                        if (resultListner != null)
                            resultListner.statusUpdateFromServer(true, response);

                    } else {

                    }

                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);

                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }

    @Override
    public void onBackPressed() {

        Intent intent = getIntent();
        intent.putExtra("refresh", "refresh");
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
                intent.putExtra("refresh", "refresh");
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({R.id.view_btn_txt, R.id.report_btn_txt, R.id.btntxt_download_detail, R.id.viewLayout})
    public void actionsforDetail(View view) {
        switch (view.getId()) {
            case R.id.view_btn_txt:

//                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
//                    Intent intentDetail = new Intent(view.getContext(), TrackList_Activity.class);
//                    intentDetail.putExtra("myLearningDetalData", myLearningModel);
//                    view.getContext().startActivity(intentDetail);
//
//                } else {
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetail_Activity.this);
//                }

                break;

            case R.id.viewLayout:
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetail_Activity.this);
                break;
            case R.id.report_btn_txt:
                Toast.makeText(this, "here selected", Toast.LENGTH_SHORT).show();
                Intent intentReports = new Intent(MyLearningDetail_Activity.this, Reports_Activity.class);
                intentReports.putExtra("myLearningDetalData", myLearningModel);
                startActivity(intentReports);
                break;
            case R.id.btntxt_download_detail:
                downloadTheCourse(myLearningModel, view);
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COURSE_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                Log.d(TAG, "onActivityResult if getCourseName :" + myLearningModel.getCourseName());

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                    if (!isFileExist) {
                        getStatusFromServer(myLearningModel);
                    }
                } else {

                    if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                        int i = -1;
                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                        if (i == 1) {
//                            Toast.makeText(MyLearningDetail_Activity.this, "Status updated!", Toast.LENGTH_SHORT).show();
                            statusUpdate(getResources().getString(R.string.metadata_status_progress));
                        } else {
//                            Toast.makeText(MyLearningDetail_Activity.this, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            }
        }
    }

    public void getStatusFromServer(final MyLearningModel myLearningModel) {
//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        String paramsString = "userId="
                + myLearningModel.getUserID()
                + "&scoId="
                + myLearningModel.getScoId();
        vollyService.getJsonObjResponseVolley("UPDATESTATUS", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentStatus?" + paramsString, appUserModel.getAuthHeaders());

        resultListner = new ResultListner() {
            @Override
            public void statusUpdateFromServer(boolean serverUpdated, JSONObject result) {
                int i = -1;

                Log.d(TAG, "statusUpdateFromServer JSONObject :" + result);

                JSONArray jsonArray = null;
                try {
                    if (result.has("contentstatus")) {
                        jsonArray = result.getJSONArray("contentstatus");

                    }

                    if (jsonArray.length() > 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String status = jsonObject.get("status").toString();
                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatus(myLearningModel, status, progress);
                        if (i == 1) {

//                            Toast.makeText(MyLearningDetail_Activity.this, "Status updated!", Toast.LENGTH_SHORT).show();

                            myLearningModel.setStatus(status);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                statusUpdate(status);
                            }

                        } else {

//                            Toast.makeText(MyLearningDetail_Activity.this, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void downloadTheCourse(final MyLearningModel learningModel, final View view) {

        boolean isZipFile = false;

        final String[] downloadSourcePath = {null};


        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath[0] = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                isZipFile = false;
                break;
            case "11":
            case "14":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getStartPage();
                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID()
                        + ".zip";
                isZipFile = true;
                break;
        }

        final boolean finalisZipFile = isZipFile;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode = 0;
                //code to do the HTTP request
                if (finalisZipFile) {

                    statusCode = webAPIClient.checkFileFoundOrNot(downloadSourcePath[0], appUserModel.getAuthHeaders());

                    if (statusCode != 200) {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/downloadfiles/"
                                + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel);

                    } else {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                                + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], view, learningModel);

                    }
                } else {

                    downloadThin(downloadSourcePath[0], view, learningModel);
                }
//                int statusCode = vollyService.checkResponseCode(downloadSourcePath[0]);

            }
        });
        thread.start();

    }

    public void downloadThin(String downloadStruri, View view, final MyLearningModel learningModel) {

        downloadStruri = downloadStruri.replace(" ", "%20");
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        Uri downloadUri = Uri.parse(downloadStruri);
        String extensionStr = "";
        switch (learningModel.getObjecttypeId()) {
            case "52":
            case "11":
            case "14":

                String[] startPage = null;
                if (learningModel.getStartPage().contains("/")) {
                    startPage = learningModel.getStartPage().split("/");
                    extensionStr = startPage[1];
                } else {
                    extensionStr = learningModel.getStartPage();
                }
                break;
            case "8":
            case "9":
            case "10":
                extensionStr = learningModel.getContentID() + ".zip";
                break;
            default:
                extensionStr = learningModel.getContentID() + ".zip";
                break;
        }

        String localizationFolder = "";
        String[] startPage = null;
        if (learningModel.getStartPage().contains("/")) {
            startPage = learningModel.getStartPage().split("/");
            localizationFolder = "/" + startPage[0];
        } else {
            localizationFolder = "";
        }
        String downloadDestFolderPath = "";
        if (extensionStr.contains(".zip")) {

            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();

        } else {
            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
        }

        boolean success = (new File(downloadDestFolderPath)).mkdirs();

        final String finalDownloadedFilePath = downloadDestFolderPath + "/" + extensionStr;

        final Uri destinationUri = Uri.parse(finalDownloadedFilePath);
        final String finalDownloadDestFolderPath = downloadDestFolderPath;
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d(TAG, "onDownloadComplete: ");

                        if (finalDownloadedFilePath.contains(".zip")) {
                            String zipFile = finalDownloadedFilePath;
                            String unzipLocation = finalDownloadDestFolderPath;
                            UnZip d = new UnZip(zipFile,
                                    unzipLocation);
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }

                        if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            callMetaDataService(learningModel);
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        Log.d(TAG, "onProgress: " + progress);
                        updateStatus(progress);
                    }

                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMetaDataService(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

    private void updateStatus(int Status) {
        // Update ProgressBar
        // Update Text to ColStatus

        circleProgressBar.setVisibility(View.VISIBLE);
        btnDownload.setVisibility(View.GONE);
        circleProgressBar.setProgress(Status);
        // Enabled Button View
        if (Status >= 100) {
            btnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
            btnDownload.setVisibility(View.VISIBLE);
            circleProgressBar.setVisibility(View.GONE);
            btnDownload.setEnabled(false);


        }


    }

}
