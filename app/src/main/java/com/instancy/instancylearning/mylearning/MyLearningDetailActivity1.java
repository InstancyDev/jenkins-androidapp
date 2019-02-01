package com.instancy.instancylearning.mylearning;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.gson.Gson;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.CmiSynchTask;
import com.instancy.instancylearning.asynchtask.SetCourseCompleteSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.NativeSettings;
import com.instancy.instancylearning.mainactivities.SignUp_Activity;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MembershipModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ReviewRatingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.EndlessScrollListener;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.wifisharing.WiFiDirectNewActivity;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.StaticValues.REVIEW_REFRESH;
import static com.instancy.instancylearning.utils.Utilities.convertDateToDayFormat;
import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormat;

import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.getButtonDrawable;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isCourseEndDateCompleted;
import static com.instancy.instancylearning.utils.Utilities.isMemberyExpry;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;

/**
 * Created by Upendranath on 6/27/2017 Working on InstancyLearning.
 */

public class MyLearningDetailActivity1 extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor billingProcessor;
    private int MY_SOCKET_TIMEOUT_MS = 5000;

    TextView txtTitle;

    TextView btnDownload;

    TextView txtDescription;

    TextView txtLongDisx;

    ImageView imgThumb;

    TextView txtCourseName;

    RatingBar ratingBar, overallRatingbar;

    TextView txtCourseStatus;

    ProgressBar progressBar;

    TextView txtAuthor;

    TextView txtSiteName;

    CircleProgressBar circleProgressBar;

    View consolidateLine;

    RelativeLayout downloadlayout;

    RelativeLayout relativeLayout;

    FloatingActionButton fabbtnthumb;
    // added for events

    TextView txtEvntIcon;

    TextView txtAthrIcon;

    TextView txtLocationIcon;

    TextView txtEventFromTo;

    TextView txtTimeZone;

    TextView txtEventLocation;

    LinearLayout eventLayout, locationLayout, authorLayout;

    ImageView imgPlayBtn;

    TextView txtPrice;

    Button btnEditReview;

    @BindView(R.id.event_bottom)
    LinearLayout btnsLayout;

    @BindView(R.id.textSecond)
    TextView buttonSecond;

    @BindView(R.id.textFirst)
    TextView buttonFirst;

    @BindView(R.id.icon_first)
    ImageView iconFirst;

    @BindView(R.id.icon_second)
    ImageView iconSecond;

    @Nullable
    @BindView(R.id.relativeone)
    RelativeLayout relativeOne;

    @Nullable
    @BindView(R.id.relativesecond)
    RelativeLayout relativeSecond;

    RelativeLayout ratingsLayout;

    @Nullable
    @BindView(R.id.ratingslistview)
    ListView ratinsgListview;

    @Nullable
    @BindView(R.id.whiteline)
    View whiteLine;

    TextView txtTableofContentText, txtTableofContentTitle, txtWhatYouLearnText, txtWhatYouLearnTitle;

    TextView txtOverallRating, ratedOutOfTxt, txtAvg, txtRating;

    View header;

    String refreshOrNo = "norefresh";

    boolean refreshReview = false;

    RatingsAdapter ratingsAdapter;

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
    boolean isFromCatalog = false;
    String typeFrom = "";
    boolean refreshCatalogContent = false;
    UiSettingsModel uiSettingsModel;
    MembershipModel membershipModel = null;
    public SetCompleteListner setCompleteListner;
    CmiSynchTask cmiSynchTask;
    int skippedRows = 0;
    List<ReviewRatingModel> reviewRatingModelList = null;
    boolean isEditReview = false, isReportEnabled = true;
    JSONObject editObj = null;

    private DatePickerDialog datePickerDialog;

    String dueDate = "";

    boolean isEventSheduled = false;

    boolean isEventSheduledDone = false;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, MyLearningDetailActivity1.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylearning_detail_activity_new);
        ButterKnife.bind(this);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        membershipModel = new MembershipModel();
        db = new DatabaseHandler(this);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            myLearningModel = (MyLearningModel) bundle.getSerializable("myLearningDetalData");
            isFromCatalog = bundle.getBoolean("IFROMCATALOG");
            typeFrom = bundle.getString("typeFrom", "");
            isEventSheduledDone = bundle.getBoolean("SHEDULE", false);
        }
        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZKOgrgA0BACsUqzZ49Xqj1SEWSx/VNSQ7e/WkUdbn7Bm2uVDYagESPHd7xD6cIUZz9GDKczG/fkoShHZdMCzWKiq07BzWnxdSaWa4rRMr+uylYAYYvV5I/R3dSIAOCbbcQ1EKUp5D7c2ltUpGZmHStDcOMhyiQgxcxZKTec6YiJ17X64Ci4adb9X/ensgOSduwQwkgyTiHjklCbwyxYSblZ4oD8WE/Ko9003VrD/FRNTAnKd5ahh2TbaISmEkwed/TK4ehosqYP8pZNZkx/bMsZ2tMYJF0lBUl5i9NS+gjVbPX4r013Pjrnz9vFq2HUvt7p26pxpjkBTtkwVgnkXQIDAQAB";

//        if (!isFromCatalog){
        billingProcessor = new BillingProcessor(this, apiKey, this);
//        }

//        boolean isCompleted=getEventCompletedUTC(myLearningModel.getEventstartUtcTime());

        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        webAPIClient = new WebAPIClient(this);

        isReportEnabled = db.isPrivilegeExistsFor(StaticValues.REPORTPREVILAGEID);
        membershipModel = db.fetchMembership(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        appController = AppController.getInstance();

        initilizeHeaderView();
        typeLayout(isFromCatalog, uiSettingsModel, false);

        if (myLearningModel != null) {
            txtTitle.setText(myLearningModel.getCourseName());

            txtCourseName.setText(myLearningModel.getMediaName());

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                int avaliableSeats = 0;
                try {
                    avaliableSeats = Integer.parseInt(myLearningModel.getAviliableSeats());
                } catch (NumberFormatException nf) {
                    avaliableSeats = 0;
                    nf.printStackTrace();
                }
                if (avaliableSeats > 0) {
                    txtCourseName.setText(myLearningModel.getMediaName() + " |  " + getLocalizationValue(JsonLocalekeys.commoncomponent_label_availableseats) + myLearningModel.getAviliableSeats());

                } else if (avaliableSeats <= 0) {
                    if (myLearningModel.getEnrollmentlimit() == myLearningModel.getNoofusersenrolled() && myLearningModel.getWaitlistlimit() == 0 || (myLearningModel.getWaitlistlimit() != -1 && myLearningModel.getWaitlistlimit() == myLearningModel.getWaitlistenrolls())) {

                        txtCourseName.setText(myLearningModel.getMediaName() + " | (" + getLocalizationValue(JsonLocalekeys.commoncomponent_label_enrollmentclosed) + ")");

                    } else if (myLearningModel.getWaitlistlimit() != -1 && myLearningModel.getWaitlistlimit() != myLearningModel.getWaitlistenrolls()) {


                        int waitlistSeatsLeftout = myLearningModel.getWaitlistlimit() - myLearningModel.getWaitlistenrolls();

                        if (waitlistSeatsLeftout > 0) {
                            txtCourseName.setText(myLearningModel.getMediaName() + " |  " + getLocalizationValue(JsonLocalekeys.commoncomponent_label_full) + "  |  " + getLocalizationValue(JsonLocalekeys.commoncomponent_label_wairlistseats) + waitlistSeatsLeftout);
                        }
                    }
                }
                txtAuthor.setText(myLearningModel.getPresenter() + " ");
                String fromDate = convertToEventDisplayDateFormat(myLearningModel.getEventstartTime(), "yyyy-MM-dd hh:mm:ss");
                String toDate = convertToEventDisplayDateFormat(myLearningModel.getEventendTime(), "yyyy-MM-dd hh:mm:ss");

                txtEventFromTo.setText(fromDate + "  to  " + toDate);
                txtEventLocation.setText(myLearningModel.getLocationName());
                txtTimeZone.setText(myLearningModel.getTimeZone());

                eventLayout.setVisibility(View.VISIBLE);
                txtAthrIcon.setVisibility(View.VISIBLE);

                if (myLearningModel.getTypeofevent() == 2 || myLearningModel.getLocationName().length() == 0) {
                    locationLayout.setVisibility(View.GONE);
                }

            } else {
                eventLayout.setVisibility(View.GONE);
                txtAthrIcon.setVisibility(View.GONE);
                txtAuthor.setText(myLearningModel.getAuthor() + " ");
            }

            if (myLearningModel.getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {

                eventLayout.setVisibility(View.GONE);
                authorLayout.setVisibility(View.GONE);
            }


            txtDescription.setText(getLocalizationValue(JsonLocalekeys.details_label_descriptionlabel));

            txtSiteName.setText(myLearningModel.getSiteName());
            txtLongDisx.setText(myLearningModel.getShortDes());
            // apply colors
            txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtOverallRating.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            ratedOutOfTxt.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtRating.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtAvg.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


            txtEventFromTo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtEventLocation.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtTimeZone.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            txtAthrIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtLocationIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtEvntIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            txtTableofContentTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtWhatYouLearnTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtTableofContentText.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            txtWhatYouLearnText.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            if (myLearningModel.getSiteName().equalsIgnoreCase("")) {
                consolidateLine.setVisibility(View.INVISIBLE);

            } else {
                consolidateLine.setVisibility(View.VISIBLE);
            }

            txtLongDisx.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            ratingValue = 0;

            try {
                ratingValue = Float.parseFloat(myLearningModel.getRatingId());
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                ratingValue = 0;
            }
            ratingBar.setRating(ratingValue);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);

//            Drawable progress = ratingBar.getProgressDrawable();
//            DrawableCompat.setTint(progress, Color.parseColor(uiSettingsModel.getAppTextColor()));


            LayerDrawable overallRatingbarProgressDrawable = (LayerDrawable) overallRatingbar.getProgressDrawable();
            overallRatingbarProgressDrawable.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);

            Drawable progresss = overallRatingbar.getProgressDrawable();
            DrawableCompat.setTint(progresss, Color.parseColor(uiSettingsModel.getAppTextColor()));


            if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
                ratingBar.setVisibility(View.INVISIBLE);
            }

            if (TextUtils.isEmpty(myLearningModel.getLongDes())) {
                txtLongDisx.setVisibility(View.GONE);
                txtDescription.setVisibility(View.GONE);
            } else {
                txtLongDisx.setVisibility(View.VISIBLE);
                txtDescription.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(myLearningModel.getShortDes())) {
                txtDescription.setVisibility(View.VISIBLE);
                txtLongDisx.setVisibility(View.VISIBLE);
                txtLongDisx.setText(myLearningModel.getShortDes());
            } else {
                txtDescription.setVisibility(View.GONE);
            }

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                btnDownload.setVisibility(View.GONE);
                circleProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);

            } else {

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.getObjecttypeId().equalsIgnoreCase("688") || myLearningModel.getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {
                    btnDownload.setVisibility(View.GONE);
                    circleProgressBar.setVisibility(View.GONE);
                } else {

                    if (uiSettingsModel.getMyLearningContentDownloadType().equalsIgnoreCase("0")) {
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
//                        if (!appController.isAlreadyViewd()) {
//                            ViewTooltip
//                                    .on(btnDownload)
//                                    .autoHide(true, 5000)
//                                    .corner(30)
//                                    .position(ViewTooltip.Position.LEFT)
//                                    .text(getLocalizationValue(JsonLocalekeys.mylearning_label_clicktodownloadlabel)).clickToHide(true)
//                                    .show();
//                        }

                        }

//                    btnDownload.setVisibility(View.GONE);
                        circleProgressBar.setVisibility(View.GONE);
                    }

                }
                // Hidden fro Tableofcontent
                progressBar.setVisibility(View.GONE);
                txtCourseStatus.setVisibility(View.GONE);

            }

            if (isFromCatalog) {

                progressBar.setVisibility(View.GONE);
                txtCourseStatus.setVisibility(View.GONE);
                ratingBar.setIsIndicator(true);
            }

            String imgUrl = myLearningModel.getImageData();

//            ViewTooltip
//                    .on(imgThumb)
//                    .autoHide(true, 5000)
//                    .corner(30)
//                    .position(ViewTooltip.Position.BOTTOM)
//                    .text("Click on image to view")
//                    .show();

            Picasso.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(imgThumb);
            String thumbUrl = "";
            if (myLearningModel.getContentTypeImagePath().contains("/Content/SiteFiles/ContentTypeIcons/")) {
                thumbUrl = myLearningModel.getSiteURL() + myLearningModel.getContentTypeImagePath();
            } else {
                thumbUrl = myLearningModel.getSiteURL() + "/Content/SiteFiles/ContentTypeIcons/" + myLearningModel.getContentTypeImagePath();
            }

//            String thumbUrl = myLearningModel.getSiteURL() + "/Content/SiteFiles/ContentTypeIcons/" + myLearningModel.getContentTypeImagePath();

            Picasso.with(this).
                    load(thumbUrl).
                    into(fabbtnthumb);
            fabbtnthumb.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

            statusUpdate(myLearningModel.getStatusActual());
        } else {
            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.unable_to_fetch), Toast.LENGTH_SHORT).show();

        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'> " + getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_detailsoption) + " </font>"));
//        getSupportActionBar().setCustomView(R.layout.drawermenu_item);
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        if (reviewRatingModelList == null) {
            reviewRatingModelList = new ArrayList<>();
        }

        ratingsAdapter = new RatingsAdapter(this, BIND_ABOVE_CLIENT, reviewRatingModelList);
        ratinsgListview.setAdapter(ratingsAdapter);
        ratinsgListview.addHeaderView(header, null, false);

        int ratingRequired = 0;
        try {
            ratingRequired = Integer.parseInt(uiSettingsModel.getMinimimRatingRequiredToShowRating());
        } catch (NumberFormatException exce) {
            ratingRequired = 0;
        }


        if (myLearningModel.getTotalratings() >= uiSettingsModel.getNumberOfRatingsRequiredToShowRating() && ratingValue >= ratingRequired) {
            ratingsLayout.setVisibility(View.VISIBLE);
            try {
                getUserRatingsOfTheContent(0, false, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ratinsgListview.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

            initilizeRatingsListView();

        } else {

            ratingsLayout.setVisibility(View.GONE);
            try {
                getUserRatingsOfTheContent(0, false, true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    if (isNetworkConnectionAvailable(MyLearningDetailActivity1.this, -1)) {
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
                                                    MyLearningDetailActivity1.this,
                                                    getLocalizationValue(JsonLocalekeys.rating_update_success),
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                            myLearningModel.setRatingId(rating);
                                            refreshCatalogContent = true;
//                                        notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(
                                                    MyLearningDetailActivity1.this,
                                                    getLocalizationValue(JsonLocalekeys.rating_update_fail),
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
                                            MyLearningDetailActivity1.this,
                                            getLocalizationValue(JsonLocalekeys.rating_update_fail),
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
                            VolleySingleton.getInstance(MyLearningDetailActivity1.this).addToRequestQueue(jsonObjReq);

                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (isNetworkConnectionAvailable(MyLearningDetailActivity1.this, -1)) {
            GetContentDetails(false, myLearningModel.getContentID());
        }

    }

    public void initilizeHeaderView() {

        header = (View) getLayoutInflater().inflate(R.layout.detail_activity_header, null);
        txtTitle = (TextView) header.findViewById(R.id.txt_title_name);
        btnDownload = (TextView) header.findViewById(R.id.btntxt_download_detail);

        txtDescription = (TextView) header.findViewById(R.id.txtDescription);
        txtLongDisx = (TextView) header.findViewById(R.id.txtLongDesc);

        txtTableofContentTitle = (TextView) header.findViewById(R.id.txtTableofContentTitle);
        txtTableofContentText = (TextView) header.findViewById(R.id.txtTableofContentText);
        txtWhatYouLearnTitle = (TextView) header.findViewById(R.id.txtWhatYouLearnTitle);
        txtWhatYouLearnText = (TextView) header.findViewById(R.id.txtWhatYouLearnText);

        fabbtnthumb = (FloatingActionButton) header.findViewById(R.id.fabbtnthumb);

        imgThumb = (ImageView) header.findViewById(R.id.imagethumb);

        imgPlayBtn = (ImageView) header.findViewById(R.id.imgPlay);

        imgPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    myLearningModel.getThumbnailVideoPath();

                String videoLinkUrl = myLearningModel.getThumbnailVideoPath();

                if (!videoLinkUrl.contains("http")) {
                    videoLinkUrl = myLearningModel.getSiteURL() + videoLinkUrl;
                }

                Intent intentSocial = new Intent(MyLearningDetailActivity1.this, SocialWebLoginsActivity.class);
                intentSocial.putExtra("ATTACHMENT", true);
                intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, videoLinkUrl);
                intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, myLearningModel.getCourseName());
                startActivity(intentSocial);
            }
        });

        txtCourseName = (TextView) header.findViewById(R.id.txt_coursename);

        txtCourseStatus = (TextView) header.findViewById(R.id.txt_course_progress);

        ratingBar = (RatingBar) header.findViewById(R.id.rat_detail_ratingbar);

        overallRatingbar = (RatingBar) header.findViewById(R.id.overallratingbar);

        progressBar = (ProgressBar) header.findViewById(R.id.course_progress_bar);

        txtAuthor = (TextView) header.findViewById(R.id.txt_author);

        txtSiteName = (TextView) header.findViewById(R.id.txt_site_name);

        circleProgressBar = (CircleProgressBar) header.findViewById(R.id.circle_progress);

        consolidateLine = (View) header.findViewById(R.id.consolidateline);

        downloadlayout = (RelativeLayout) header.findViewById(R.id.downloadlayout);

        relativeLayout = (RelativeLayout) header.findViewById(R.id.detail_layout);

        txtPrice = (TextView) header.findViewById(R.id.btn_price);

        txtOverallRating = (TextView) header.findViewById(R.id.txt_overallrating);

        ratedOutOfTxt = (TextView) header.findViewById(R.id.ratedoutofTxt);

        txtAvg = (TextView) header.findViewById(R.id.txtAvg);

        txtRating = (TextView) header.findViewById(R.id.txtRating);

        btnEditReview = (Button) header.findViewById(R.id.btnReview);

        ratingsLayout = (RelativeLayout) header.findViewById(R.id.overall_ratingslayout);

        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));


        // event labels
        txtEvntIcon = (TextView) header.findViewById(R.id.txteventicon);

        txtAthrIcon = (TextView) header.findViewById(R.id.txtathricon);

        txtLocationIcon = (TextView) header.findViewById(R.id.txtlocationicon);

        txtEventFromTo = (TextView) header.findViewById(R.id.txt_eventfromtotime);

        txtTimeZone = (TextView) header.findViewById(R.id.txt_timezone);

        txtEventLocation = (TextView) header.findViewById(R.id.txt_eventlocation);

        eventLayout = (LinearLayout) header.findViewById(R.id.eventlayout);

        locationLayout = (LinearLayout) header.findViewById(R.id.locationlayout);

        authorLayout = (LinearLayout) header.findViewById(R.id.author_site_layout);


        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        FontManager.markAsIconContainer(header.findViewById(R.id.btntxt_download_detail), iconFont);

        FontManager.markAsIconContainer(header.findViewById(R.id.txteventicon), iconFont);
        FontManager.markAsIconContainer(header.findViewById(R.id.txtathricon), iconFont);
        FontManager.markAsIconContainer(header.findViewById(R.id.txtlocationicon), iconFont);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTheCourse(myLearningModel, view);
            }
        });

//        btnEditReview.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//        btnEditReview.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        btnEditReview.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnEditReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWriteReview();
            }
        });

//        Commented Beta
        if (isFromCatalog) {
            btnEditReview.setVisibility(View.GONE);
        }
    }

    public void typeLayout(boolean isCatalog, UiSettingsModel uiSettingsModel, boolean isLoadingCompleted) {

        btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        relativeSecond.setVisibility(View.GONE);
        whiteLine.setVisibility(View.GONE);
        buttonFirst.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        buttonSecond.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        if (isCatalog) {

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

                buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_plus, this, uiSettingsModel.getAppButtonTextColor());
                Drawable reportImg = getButtonDrawable(R.string.fa_icon_filter, this, uiSettingsModel.getAppButtonTextColor());

                if (myLearningModel.getAddedToMylearning() == 1) {

                    if (myLearningModel.getIsListView().equalsIgnoreCase("true") && !myLearningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
                        if (isReportEnabled) {
                            relativeSecond.setVisibility(View.VISIBLE);
                            whiteLine.setVisibility(View.VISIBLE);
                            Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppButtonTextColor());
                            iconSecond.setBackground(relatedContent);
                            buttonSecond.setText(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_reportoption));
                            buttonSecond.setTag(5);
                        }

                        Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppButtonTextColor());
                        iconFirst.setBackground(viewIcon);
                        buttonFirst.setText(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewoption));
                        buttonFirst.setTag(1);
                        txtPrice.setVisibility(View.GONE);
                        txtPrice.setText("");
                        btnDownload.setVisibility(View.VISIBLE);

                    }
//                    else if (myLearningModel.getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
//                        Drawable calendar = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppButtonTextColor());
//                        iconFirst.setBackground(calendar);
//                        buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_tab_scheduletitlelabel));
//                        buttonFirst.setTag(7);
//                    }
                    else {
                        if (!returnEventCompleted(myLearningModel.getEventendUtcTime())) {
                            Drawable calendar = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppButtonTextColor());
                            iconFirst.setBackground(calendar);
                            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_addtocalendaroption));
                            buttonFirst.setTag(4);
                        } else {
                            btnsLayout.setVisibility(View.GONE);
                        }

                    }

                } else {
                    if (myLearningModel.getViewType().equalsIgnoreCase("1") || myLearningModel.getViewType().equalsIgnoreCase("2")) {
                        if (!returnEventCompleted(myLearningModel.getEventstartUtcTime())) {
                            iconFirst.setBackground(calendarImg);

                            int avaliableSeats = 0;
                            try {
                                avaliableSeats = Integer.parseInt(myLearningModel.getAviliableSeats());
                            } catch (NumberFormatException nf) {
                                avaliableSeats = 0;
                                nf.printStackTrace();
                            }

                            if (avaliableSeats > 0) {

                                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_enrollbutton));
                                buttonFirst.setTag(6);

                            } else if (avaliableSeats <= 0 && myLearningModel.getWaitlistlimit() != 0 && myLearningModel.getWaitlistlimit() != myLearningModel.getWaitlistenrolls()) {
                                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_enrollbutton));
                                buttonFirst.setTag(6);

                            } else {
                                btnsLayout.setVisibility(View.GONE);
                            }


                        } else {

                            if (uiSettingsModel.isAllowExpiredEventsSubscription()) {
                                iconFirst.setBackground(calendarImg);
                                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_enrollbutton));
                                buttonFirst.setTag(6);
                            } else {
                                btnsLayout.setVisibility(View.GONE);

                            }

                        }

                    } else if (myLearningModel.getViewType().equalsIgnoreCase("3")) {

                        if (!returnEventCompleted(myLearningModel.getEventendUtcTime())) {
                            Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppButtonTextColor());
                            if (uiSettingsModel.isAllowExpiredEventsSubscription()) {

                                iconFirst.setBackground(cartIcon);
                                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_buybutton));
                                buttonFirst.setTag(3);
                            }
                        }
//                        else if (myLearningModel.getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
//                            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_tab_scheduletitlelabel));
//                            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_addtocalendaroption));
//                            buttonFirst.setTag(7);
//                        }
                        else {
                            btnsLayout.setVisibility(View.GONE);
                        }
// Uncomment this if required
//                        Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppButtonTextColor());
//                        iconFirst.setBackground(cartIcon);
//                        buttonFirst.setText("Buy");
//                        buttonFirst.setTag(3);

                    }
                }

            } else {
                buttonFirst.setTag(2);
                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_addtomylearningbutton));
                Drawable addPlusIcon = getButtonDrawable(R.string.fa_icon_plus_circle, this, uiSettingsModel.getAppButtonTextColor());
                iconFirst.setBackground(addPlusIcon);
                if (myLearningModel.getViewType().equalsIgnoreCase("3")) {
//                    Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppHeaderTextColor());
//                    iconFirst.setBackground(cartIcon);
//                    buttonFirst.setText("Buy");

                    boolean isMemberExpired = isMemberyExpry(membershipModel.expirydate);
                    if (!isMemberExpired) {
                        if (membershipModel.membershiplevel >= myLearningModel.getMemberShipLevel()) {
                            Drawable cartIcon = getButtonDrawable(R.string.fa_icon_plus, this, uiSettingsModel.getAppHeaderTextColor());
                            iconFirst.setBackground(cartIcon);
                            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_addtomylearningbutton));
                            buttonFirst.setTag(2);
                        } else {
                            Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppHeaderTextColor());
                            iconFirst.setBackground(cartIcon);
                            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_buybutton));
                            buttonFirst.setTag(3);
                        }
                    } else {
                        Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppButtonTextColor());
                        iconFirst.setBackground(cartIcon);
                        buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_buybutton));
                        buttonFirst.setTag(3);
                    }
                    txtPrice.setText("$" + myLearningModel.getPrice());
                    txtPrice.setVisibility(View.VISIBLE);
                    btnDownload.setVisibility(View.GONE);
                } else {
                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
                        btnDownload.setVisibility(View.GONE);
                    } else {
                        btnDownload.setVisibility(View.VISIBLE);
                    }
                    txtPrice.setVisibility(View.GONE);
                    txtPrice.setText("");
                }
                if (myLearningModel.getAddedToMylearning() == 1) {
                    buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_viewbutton));
                    buttonFirst.setTag(1);
                    txtPrice.setVisibility(View.GONE);
                    txtPrice.setText("");
                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppButtonTextColor());

                    iconFirst.setBackground(viewIcon);
                }
            }
        } else {
            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

                if (!returnEventCompleted(myLearningModel.getEventendUtcTime())) {
                    buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                    buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                    btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                    Drawable calendarImg = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppButtonTextColor());
                    iconFirst.setBackground(calendarImg);
                    buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_addtocalendarbutton));
                    buttonFirst.setTag(4);
                }

                if (myLearningModel.getIsListView().equalsIgnoreCase("true") && !myLearningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
                    if (isReportEnabled) {
                        relativeSecond.setVisibility(View.VISIBLE);
                        whiteLine.setVisibility(View.VISIBLE);
                        Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppButtonTextColor());
                        iconSecond.setBackground(relatedContent);
                        buttonSecond.setText(getLocalizationValue(JsonLocalekeys.details_button_reportbutton));
                        buttonSecond.setTag(5);
                    }
                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppButtonTextColor());
                    iconFirst.setBackground(viewIcon);
                    buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_viewbutton));
                    buttonFirst.setTag(1);
                    txtPrice.setVisibility(View.GONE);
                    txtPrice.setText("");
                    btnDownload.setVisibility(View.VISIBLE);
                } else if (myLearningModel.getEventScheduleType() == 1) {
                    if (uiSettingsModel.isEnableMultipleInstancesforEvent()) {
                        buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_tab_scheduletitlelabel));
                        buttonFirst.setTag(7);
                        if (typeFrom.equalsIgnoreCase("tab") && isLoadingCompleted) {
//                            buttonFirst.performClick();
                            Intent intent = new Intent(MyLearningDetailActivity1.this, MyLearningSchedulelActivity.class);
                            intent.putExtra("myLearningDetalData", myLearningModel);
                            startActivityForResult(intent, DETAIL_CLOSE_CODE);
                        }
                    }
                } else {
                    if (returnEventCompleted(myLearningModel.getEventstartTime())) {
                        btnsLayout.setVisibility(View.GONE);
                    }
                }
            } else {

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.getObjecttypeId().equalsIgnoreCase("20") | myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {

                    if (!myLearningModel.getStatusActual().toLowerCase().contains("completed")) {
                        buttonSecond.setText(getLocalizationValue(JsonLocalekeys.details_button_setcompletebutton));
                        buttonSecond.setTag(6);
                        relativeSecond.setVisibility(View.VISIBLE);
                        whiteLine.setVisibility(View.VISIBLE);
                        Drawable relatedContent = getButtonDrawable(R.string.fa_icon_check, this, uiSettingsModel.getAppButtonTextColor());
                        iconSecond.setBackground(relatedContent);
                    }

                } else {
// uncomment after reports completed
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27") || myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
                        relativeSecond.setVisibility(View.GONE);

                    } else {
                        if (isReportEnabled) {
                            relativeSecond.setVisibility(View.VISIBLE);
                            whiteLine.setVisibility(View.VISIBLE);
                            Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppButtonTextColor());
                            iconSecond.setBackground(relatedContent);
                            buttonSecond.setText(getLocalizationValue(JsonLocalekeys.details_button_reportbutton));
                            buttonSecond.setTag(5);
                        }

                    }
                }

                Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppButtonTextColor());
                iconFirst.setBackground(viewIcon);
                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_viewbutton));
                buttonFirst.setTag(1);
                txtPrice.setVisibility(View.GONE);
                txtPrice.setText("");
                btnDownload.setVisibility(View.VISIBLE);

            }

        }

        setCompleteListner = new SetCompleteListner() {
            @Override
            public void completedStatus() {

                relativeSecond.setVisibility(View.GONE);
                whiteLine.setVisibility(View.GONE);
                statusUpdate("Completed");

            }
        };
    }

    public void updateEnrolledEvent() {

        if (myLearningModel.getIsListView().equalsIgnoreCase("true") && !myLearningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
            Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppButtonTextColor());
            iconFirst.setBackground(viewIcon);
            buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_viewbutton));
            buttonFirst.setTag(1);
            txtPrice.setVisibility(View.GONE);
            txtPrice.setText("");

        } else {

            if (!returnEventCompleted(myLearningModel.getEventendUtcTime())) {
                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppButtonTextColor());
                iconFirst.setBackground(calendarImg);
                buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_addtocalendarbutton));
                buttonFirst.setTag(4);
            } else {
                btnsLayout.setVisibility(View.GONE);
            }

        }

        db.updateEventAddedToMyLearningInEventCatalog(myLearningModel, 1);
        refreshCatalogContent = true;
        MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void statusUpdate(String courseStatus) {

        String displayStatus = "";
        if (courseStatus.equalsIgnoreCase("Completed") || (courseStatus.toLowerCase().contains("passed") || courseStatus.toLowerCase().contains("failed"))) {

            String progressPercent = "100";
            String statusValue = courseStatus;
            if (courseStatus.equalsIgnoreCase("Completed")) {
//                statusValue = "Completed";
                statusValue = " " + getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel);

            } else if (courseStatus.equalsIgnoreCase("failed")) {

//                statusValue = "Completed(failed)";
                statusValue = getLocalizationValue(JsonLocalekeys.status_completed_failed);
            } else if (courseStatus.equalsIgnoreCase("passed")) {

//                statusValue = "Completed(passed)";
                statusValue = getLocalizationValue(JsonLocalekeys.status_completed_passed);
            }

            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusCompleted)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
//                courseStatus = trackChildList.getStatusActual() + " (" + trackChildList.getProgress();
            displayStatus = statusValue + " (" + progressPercent;

        } else if (courseStatus.equalsIgnoreCase("Not Started")) {
            String progressPercent = "0";

//                holder.progressBar.setBackgroundColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusNotStarted)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusNotStarted));
            displayStatus = courseStatus + "  (" + progressPercent;

        } else if (courseStatus.toLowerCase().contains("incomplete") || (courseStatus.toLowerCase().contains("inprogress")) || (courseStatus.toLowerCase().contains("in progress"))) {
            String progressPercent = "50";
            String statusValue = getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel);
            statusValue = getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel);
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusInProgress)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusInProgress));
            displayStatus = statusValue + " (" + progressPercent;

        } else if (courseStatus.toLowerCase().contains("pending review") || (courseStatus.toLowerCase().contains("pendingreview")) || (courseStatus.toLowerCase().contains("grade"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));

            String status = "Pending Review";
            status = getLocalizationValue(JsonLocalekeys.mylearning_label_pendingreviewlabel);
            progressBar.setProgress(100);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            displayStatus = status + "(" + 100;
        } else if (courseStatus.equalsIgnoreCase("Registered") || (courseStatus.toLowerCase().contains("registered"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
            String status = "";

            status = getLocalizationValue(JsonLocalekeys.status_registered);
            progressBar.setProgress(100);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
            courseStatus = status;
        } else if (courseStatus.toLowerCase().contains("attended") || (courseStatus.toLowerCase().contains("registered"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = courseStatus;
            status = getLocalizationValue(JsonLocalekeys.status_attended);

            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            courseStatus = status;
        } else if (courseStatus.toLowerCase().contains("Expired")) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));
            String status = getLocalizationValue(JsonLocalekeys.status_expired);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            courseStatus = status;
        } else {

            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
            progressBar.setProgress(0);
            String status = "";
            status = courseStatus;
            displayStatus = status + "(" + 0;

        }

        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
            txtCourseStatus.setText(courseStatus);

        } else {

            txtCourseStatus.setText(displayStatus + "%)");
        }
        myLearningModel.setStatusActual(courseStatus);
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

                if (requestType.equalsIgnoreCase("INAPP")) {

                    if (response != null) {
                        if (response.toLowerCase().contains("success")) {

                            addToMyLearning(myLearningModel, false);

                        } else {
                            Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_purchasefailed), Toast.LENGTH_SHORT).show();
                        }

                    } else {

                    }

                }
                if (requestType.equalsIgnoreCase("GetContentDetails")) {

                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (isEventSheduled) {
                                updateMylearningModel(jsonObject);
//                                return;
                            }

                            if (jsonObject.has("LongDescription")) {
                                myLearningModel.setLongDes(jsonObject.getString("LongDescription"));
                            }
                            if (jsonObject.has("EventScheduleType")) {
                                myLearningModel.setEventScheduleType(jsonObject.optInt("EventScheduleType"));
                            }
                            if (jsonObject.has("TableofContent")) {
                                myLearningModel.setTableofContent(jsonObject.getString("TableofContent"));
                            }
                            if (jsonObject.has("LearningObjectives")) {
                                myLearningModel.setLearningObjectives(jsonObject.getString("LearningObjectives"));
                            }
                            if (jsonObject.has("ThumbnailVideoPath")) {
                                myLearningModel.setThumbnailVideoPath(jsonObject.getString("ThumbnailVideoPath"));
                            }
                            if (!TextUtils.isEmpty(myLearningModel.getThumbnailVideoPath())) {
                                imgPlayBtn.setVisibility(View.VISIBLE);
                            } else {
                                imgPlayBtn.setVisibility(View.GONE);
                            }

                            if (isValidString(myLearningModel.getShortDes())) {
                                txtDescription.setVisibility(View.VISIBLE);
                                txtLongDisx.setVisibility(View.VISIBLE);
                                txtLongDisx.setText(myLearningModel.getShortDes());
                            } else {
                                txtDescription.setVisibility(View.GONE);
                            }

                            if (isValidString(myLearningModel.getLongDes())) {
                                txtDescription.setVisibility(View.VISIBLE);
                                txtLongDisx.setVisibility(View.VISIBLE);
                                txtLongDisx.setText(Html.fromHtml(myLearningModel.getLongDes()));
                            } else {
                                txtDescription.setVisibility(View.GONE);
                                txtLongDisx.setText("");
                            }

                            if (!TextUtils.isEmpty(myLearningModel.getTableofContent())) {
                                txtTableofContentTitle.setVisibility(View.VISIBLE);
                                txtTableofContentText.setVisibility(View.VISIBLE);
                                txtTableofContentText.setText(Html.fromHtml(myLearningModel.getTableofContent()));
                            }
                            if (!TextUtils.isEmpty(myLearningModel.getLearningObjectives())) {
                                txtWhatYouLearnTitle.setVisibility(View.VISIBLE);
                                txtWhatYouLearnText.setVisibility(View.VISIBLE);
                                txtWhatYouLearnText.setText(Html.fromHtml(myLearningModel.getLearningObjectives()));
                            }
                            if (myLearningModel.getEventScheduleType() == 1) {
                                if (uiSettingsModel.isEnableMultipleInstancesforEvent()) {
//                                    if (!TextUtils.isEmpty(myLearningModel.getLongDes())) {
//                                        findViewById(R.id.txtDescription).setVisibility(View.VISIBLE);
//                                        txtWhatYouLearnText.setVisibility(View.VISIBLE);
//                                        txtLongDesc.setText(Html.fromHtml(myLearningModel.getLongDes()));
//                                    }
//                                    if (!TextUtils.isEmpty(myLearningModel.getTableofContent())) {
//                                        findViewById(R.id.txtTableofContentTitle).setVisibility(View.VISIBLE);
//                                        txtWhatYouLearnText.setVisibility(View.VISIBLE);
//                                        txtTableofContentText.setText(Html.fromHtml(myLearningModel.getTableofContent()));
//                                    }
//                                    if (!TextUtils.isEmpty(myLearningModel.getLearningObjectives())) {
//                                        findViewById(R.id.txtWhatYouLearnTitle).setVisibility(View.VISIBLE);
//                                        txtWhatYouLearnText.setVisibility(View.VISIBLE);
//                                        txtWhatYouLearnText.setText(Html.fromHtml(myLearningModel.getLearningObjectives()));
//                                    }
                                }
                                typeLayout(isFromCatalog, uiSettingsModel, true); // BetaCommented
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }

                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                if (requestType.equalsIgnoreCase("MLADP")) {

                    if (response != null) {
                        try {
                            db.injectCMIDataInto(response, myLearningModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }

                svProgressHUD.dismiss();
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (isFromCatalog) {
            intent.putExtra("REFRESH", refreshCatalogContent);
        } else {

            intent.putExtra("refresh", "norefresh");

        }
        setResult(RESULT_OK, intent);

        finish();
        super.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.action_items, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = getIntent();
                if (isFromCatalog) {
                    intent.putExtra("REFRESH", refreshCatalogContent);
                } else {
                    if (refreshReview) {
                        intent.putExtra("NEWREVIEW", refreshReview);
                        intent.putExtra("myLearningDetalData", myLearningModel);
                        intent.putExtra("refresh", "norefresh");
                    } else if (isEventSheduledDone) {
                        intent.putExtra("SHEDULE", isEventSheduledDone);
                        intent.putExtra("myLearningDetalData", myLearningModel);
                        intent.putExtra("refresh", "norefresh");

                    } else {
                        intent.putExtra("refresh", refreshOrNo);
                    }
                }
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.atn_direct_enable:
                startActivity(new Intent(this, WiFiDirectNewActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.relativeone, R.id.relativesecond, R.id.btntxt_download_detail})
    public void actionsforDetail(View view) {
        switch (view.getId()) {
            case R.id.view_btn_txt:
            case R.id.relativeone:
                if ((Integer) buttonFirst.getTag() == 1) { // View
                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetailActivity1.this);
                } else if ((Integer) buttonFirst.getTag() == 2) { // ADD

                    if (uiSettingsModel.getNoOfDaysForCourseTargetDate() > 0) {
                        selectTheDueDate(myLearningModel, uiSettingsModel.getNoOfDaysForCourseTargetDate());
                    } else {
                        addToMyLearningCheckUser(myLearningModel, false); //

                    }
                } else if ((Integer) buttonFirst.getTag() == 3) {   // Buy
                    if (uiSettingsModel.isEnableIndidvidualPurchaseConfig() && uiSettingsModel.isEnableMemberShipConfig() && myLearningModel.getGoogleProductID() == null) {
                        gotoMemberShipView(myLearningModel);
                    } else {
                        addToMyLearningCheckUser(myLearningModel, true);
                    }
                } else if ((Integer) buttonFirst.getTag() == 4) {
                    GlobalMethods.addEventToDeviceCalendar(myLearningModel, this);
                } else if ((Integer) buttonFirst.getTag() == 6) {

                    if (uiSettingsModel.isAllowExpiredEventsSubscription() && returnEventCompleted(myLearningModel.getEventstartUtcTime())) {

//                            addExpiredEventToMyLearning(myLearningDetalData, position);
                        try {
                            addExpiryEvets(myLearningModel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        int avaliableSeats = 0;
                        try {
                            avaliableSeats = Integer.parseInt(myLearningModel.getAviliableSeats());
                        } catch (NumberFormatException nf) {
                            avaliableSeats = 0;
                            nf.printStackTrace();
                        }

                        if (avaliableSeats > 0) {

                            addToMyLearningCheckUser(myLearningModel, false);
                        } else if (avaliableSeats <= 0 && myLearningModel.getWaitlistlimit() != 0 && myLearningModel.getWaitlistlimit() != myLearningModel.getWaitlistenrolls()) {

                            try {
                                addToWaitList(myLearningModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            addToMyLearningCheckUser(myLearningModel, false);
                        }

                    }
                } else if ((Integer) buttonFirst.getTag() == 7) {
                    if (myLearningModel.getEventScheduleType() == 1) {
                        if (uiSettingsModel.isEnableMultipleInstancesforEvent()) {
                            Intent intent = new Intent(MyLearningDetailActivity1.this, MyLearningSchedulelActivity.class);
                            intent.putExtra("myLearningDetalData", myLearningModel);
                            startActivityForResult(intent, DETAIL_CLOSE_CODE);
                        }
                    }
                }
                break;
            case R.id.btntxt_download_detail:
                downloadTheCourse(myLearningModel, view);
                break;
            case R.id.relativesecond:
                if ((Integer) buttonSecond.getTag() == 7) {
                    if (isNetworkConnectionAvailable(MyLearningDetailActivity1.this, -1)) {
                        new SetCourseCompleteSynchTask(this, db, myLearningModel, setCompleteListner).execute();
                    }
                    refreshOrNo = "refresh";
                }
                // else if()
                else {
                    openReportsActivity();
                }
                break;
        }
    }

    public void gotoMemberShipView(MyLearningModel learningModel) {

        Intent intentSignup = new Intent(this, SignUp_Activity.class);
        intentSignup.putExtra(StaticValues.KEY_SOCIALLOGIN, appUserModel.getSiteURL() + "Join/nativeapp/true/membership/true/userid/" + learningModel.getUserID() + "/siteid/" + learningModel.getSiteID());
        intentSignup.putExtra(StaticValues.KEY_ACTIONBARTITLE, "Membership");
        startActivity(intentSignup);
//        http://mayur.instancysoft.com/Sign%20Up/profiletype/selfregistration/nativeapp/true
//        http://mayur.instancysoft.com/Join/nativeapp/true/membership/true/userid/2/siteid/374
    }


//    @OnClick({R.id.relativeone, R.id.relativesecond, R.id.btntxt_download_detail})
//    public void actionsforDetail(View view) {
//        switch (view.getId()) {
//            case R.id.view_btn_txt:
//            case R.id.relativeone:
//                if (buttonFirst.getText().toString().equalsIgnoreCase("View")) {
//                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetailActivity.this);
//                } else if (buttonFirst.getText().toString().equalsIgnoreCase("Add")) {
//                    addToMyLearningCheckUser(myLearningModel, false); // false for
//                } else if (buttonFirst.getText().toString().equalsIgnoreCase("Buy")) {
//                    addToMyLearningCheckUser(myLearningModel, true);
//                } else if (buttonFirst.getText().toString().equalsIgnoreCase(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_addtocalendaroption))) {
//                    GlobalMethods.addEventToDeviceCalendar(myLearningModel, this);
//                } else if (buttonFirst.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_txt_enroll))) {
//
//                    if (uiSettingsModel.isAllowExpiredEventsSubscription() && returnEventCompleted(myLearningModel.getEventstartUtcTime())) {
//
////                            addExpiredEventToMyLearning(myLearningDetalData, position);
//                        try {
//                            addExpiryEvets(myLearningModel);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    } else {
//
//                        int avaliableSeats = 0;
//                        try {
//                            avaliableSeats = Integer.parseInt(myLearningModel.getAviliableSeats());
//                        } catch (NumberFormatException nf) {
//                            avaliableSeats = 0;
//                            nf.printStackTrace();
//                        }
//
//                        if (avaliableSeats > 0) {
//
//                            addToMyLearningCheckUser(myLearningModel, false);
//                        } else if (avaliableSeats <= 0 && myLearningModel.getWaitlistlimit() != 0 && myLearningModel.getWaitlistlimit() != myLearningModel.getWaitlistenrolls()) {
//
//
//                            try {
//                                addToWaitList(myLearningModel);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            addToMyLearningCheckUser(myLearningModel, false);
//                        }
//
//                    }
//                }
//                break;
//            case R.id.btntxt_download_detail:
//                downloadTheCourse(myLearningModel, view);
//                break;
//            case R.id.relativesecond:
//                if (buttonSecond.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_txt_setcomplete))) {
//                    if (isNetworkConnectionAvailable(MyLearningDetailActivity.this, -1)) {
//                        new SetCourseCompleteSynchTask(this, db, myLearningModel, setCompleteListner).execute();
//                    }
//                    refreshOrNo = "refresh";
//                } else {
//                    openReportsActivity();
//                }
//                break;
//        }
//    }

    public void inAppActivityCall(MyLearningModel learningModel) {

        if (!BillingProcessor.isIabServiceAvailable(this)) {

            Toast.makeText(this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_inappserviceunavailable), Toast.LENGTH_SHORT).show();
        }

        String testId = "android.test.purchased";
        String productId = learningModel.getGoogleProductID();

//        if (productId.length() != 0) {
//            billingProcessor.purchase(MyLearningDetailActivity.this, productId);
//        }

        String originalproductid = learningModel.getGoogleProductID();

        if (originalproductid.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("learningdata", learningModel);
            billingProcessor.handleActivityResult(8099, 80, intent);
            billingProcessor.purchase(MyLearningDetailActivity1.this, originalproductid);
        } else {
            Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_inappidnotinserver), Toast.LENGTH_SHORT).show();
        }

        SkuDetails sku = billingProcessor.getPurchaseListingDetails(originalproductid);

//        Toast.makeText(this, sku != null ? sku.toString() : "Failed to load SKU details", Toast.LENGTH_SHORT).show();

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

                File myFile = new File(myLearningModel.getOfflinepath());

                if (!myFile.exists()) {

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                        if (isNetworkConnectionAvailable(MyLearningDetailActivity1.this, -1)) {
                            getStatusFromServer(myLearningModel);

                        }
                    } else {
                        if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                            if (i == 1) {
//                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();

                                statusUpdate(getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel));
                            } else {

//                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } else {

                    if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                        int i = -1;
                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                        statusUpdate(getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel));
                        if (i == 1) {
//                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
//                            myLearningAdapter.notifyDataSetChanged();
//                            injectFromDbtoModel();
                        } else {

//                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        statusUpdate(myLearningModel.getStatusActual());
                    }

                }

                if (isNetworkConnectionAvailable(this, -1) && !isFromCatalog) {
                    cmiSynchTask = new CmiSynchTask(this);
                    cmiSynchTask.execute();
                }

//                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
//
//                    if (!isFileExist) {
//                        getStatusFromServer(myLearningModel);
//                    }
//                } else {
//
//                    if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
//                        int i = -1;
//                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
//                        if (i == 1) {
////                            Toast.makeText(MyLearningDetailActivity.this, "Status updated!", Toast.LENGTH_SHORT).show();
//                            statusUpdate(getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel));
//                        } else {
////                            Toast.makeText(MyLearningDetailActivity.this, "Unable to update the status", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                }
            }
            refreshCatalogContent = true;
        }

        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);
        }


        if (requestCode == REVIEW_REFRESH && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");

                boolean refresh = data.getBooleanExtra("NEWREVIEW", false);
                if (refresh) {
                    refreshReview = true;
                    int ratingRequired = 0;
                    try {
                        ratingRequired = Integer.parseInt(uiSettingsModel.getMinimimRatingRequiredToShowRating());
                    } catch (NumberFormatException exce) {
                        ratingRequired = 0;
                    }


                    if (myLearningModel.getTotalratings() >= uiSettingsModel.getNumberOfRatingsRequiredToShowRating() && ratingValue >= ratingRequired) {
                        ratingsLayout.setVisibility(View.VISIBLE);
                        try {
                            getUserRatingsOfTheContent(0, false, false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ratinsgListview.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

                        initilizeRatingsListView();

                    } else {

                        ratingsLayout.setVisibility(View.GONE);
                    }

                }

            }
            refreshCatalogContent = true;
        }

        if (requestCode == DETAIL_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                MyLearningModel myLearningModel = (MyLearningModel) data.getSerializableExtra("myLearningDetalData");
                boolean refresh = data.getBooleanExtra("SHEDULED", false);
                if (refresh) {
                    refreshReview = true;
                    isEventSheduled = true;
                    String eventInstanceId = data.getStringExtra("EventInstanceId");
                    Log.d(TAG, "onActivityResult: " + eventInstanceId);
                    if (isNetworkConnectionAvailable(MyLearningDetailActivity1.this, -1)) {

                        GetContentDetails(false, eventInstanceId);
                    }
                }
            }
            refreshCatalogContent = true;
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
                        String status = "";
                        if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.app_esperanza))) {
                            // esperanza call
                            status = jsonObject.optString("Name").trim();
                        } else {

                            status = jsonObject.optString("status").trim();
                        }
                        String progress = "";
                        if (jsonObject.has("progress")) {
                            progress = jsonObject.get("progress").toString();
                        }
                        i = db.updateContentStatus(myLearningModel, status, progress);
                        if (i == 1) {

//                            Toast.makeText(MyLearningDetailActivity.this, "Status updated!", Toast.LENGTH_SHORT).show();

                            myLearningModel.setStatusActual(status);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                statusUpdate(status);
                            }

                        } else {

//                            Toast.makeText(MyLearningDetailActivity.this, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }


    public void GetContentDetails(Boolean isRefreshed, String eventInstanceIdOrEventContentID) {
        if (!isRefreshed) {
            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
        }

        String urlStr = appUserModel.getWebAPIUrl() + "/ContentDetails/GetContentDetails";

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("ContentID", eventInstanceIdOrEventContentID);
            parameters.put("metadata", "1");
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("intUserID", appUserModel.getUserIDValue());
            parameters.put("iCMS", false);
            parameters.put("ComponentID", myLearningModel.getComponentId());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("ERitems", "");
            parameters.put("DetailsCompID", "107");
            parameters.put("DetailsCompInsID", "3291");
            parameters.put("ComponentDetailsProperties", "");
            parameters.put("HideAdd: ", "");
            parameters.put("objectTypeID", "-1");
            parameters.put("scoID", "");
            parameters.put("SubscribeERC", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "GetContentDetails", urlStr);

    }


    public void downloadTheCourse(final MyLearningModel learningModel, final View view) {

        if (learningModel.getAddedToMylearning() == 0 && isFromCatalog) {
            addToMyLearning(learningModel, false);
        }
        btnDownload.setEnabled(false);
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
                if (learningModel.getObjecttypeId().equalsIgnoreCase("11") && learningModel.getJwvideokey().length() > 0 & learningModel.getCloudmediaplayerkey().length() > 0) {
                    //JW Standalone video content in offline mode.

                    downloadSourcePath[0] = "https://content.jwplatform.com/videos/" + learningModel.getJwvideokey() + ".mp4";

                } else {

                    downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                            + learningModel.getFolderPath() + "/" + learningModel.getStartPage();
                }

                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                        + learningModel.getFolderPath() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                        + learningModel.getFolderPath() + "/" + learningModel.getContentID()
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
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/publishfiles/"
                                + learningModel.getFolderPath() + "/" + learningModel.getContentID() + ".zip";
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
        Log.d(TAG, "downloadThin: " + downloadStruri);
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
                    + "/.Mydownloads/Contentdownloads" + "/" + learningModel.getContentID();

        } else {
            downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                    + "/.Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
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

//                        if (!learningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
//                            callMobileGetContentTrackedData(learningModel);
//                        }

                        if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                            if (!learningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);
                                callMobileGetMobileContentMetaData(learningModel);
                            } else {
                                callMobileGetMobileContentMetaData(learningModel);

                            }

                        } else {
                            if (!learningModel.getStatusActual().equalsIgnoreCase("Not Started")) {
                                callMobileGetContentTrackedData(learningModel);

                            }

                        }


                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d(TAG, "onDownloadFailed: " + +errorCode);
                        btnDownload.setEnabled(true);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                        Log.d(TAG, "onProgress: " + progress);
                        updateStatus(progress);
                    }

                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMobileGetContentTrackedData(MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        vollyService.getJsonObjResponseVolley("MLADP", appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, appUserModel.getAuthHeaders(), learningModel);

    }

    private void updateStatus(int Status) {
        // Update ProgressBar
        // Update Text to ColStatus

        btnDownload.setVisibility(View.GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        circleProgressBar.setProgress(Status);
        // Enabled Button View
        if (Status >= 100) {
            btnDownload.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
            btnDownload.setVisibility(View.VISIBLE);
            circleProgressBar.setVisibility(View.GONE);
            btnDownload.setEnabled(false);
            refreshOrNo = "refresh";
        }
    }


    public void addToMyLearningCheckUser(MyLearningModel myLearningDetalData, boolean isInApp) {

        if (isNetworkConnectionAvailable(this, -1)) {

            if (myLearningDetalData.getUserID().equalsIgnoreCase("-1")) {

                checkUserLogin(myLearningDetalData, false);

            } else {

                if (isInApp) {
                    inAppActivityCall(myLearningDetalData);
                } else {
                    addToMyLearning(myLearningDetalData, false);
                }

            }
        }
    }

    public void addToMyLearning(final MyLearningModel myLearningDetalData, final boolean isJoinedCommunity) {

        if (isNetworkConnectionAvailable(this, -1)) {
            boolean isSubscribed = db.isSubscribedContent(myLearningDetalData);
            if (isSubscribed) {
                Toast toast = Toast.makeText(
                        this,
                        getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String requestURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileAddtoMyCatalog?"
                        + "UserID=" + myLearningDetalData.getUserID() + "&SiteURL=" + myLearningDetalData.getSiteURL()
                        + "&ContentID=" + myLearningDetalData.getContentID() + "&SiteID=" + myLearningDetalData.getSiteID() + "&targetDate=" + dueDate;
                requestURL = requestURL.replaceAll(" ", "%20");
                Log.d(TAG, "inside catalog login : " + requestURL);
                dueDate = "";
                StringRequest strReq = new StringRequest(Request.Method.GET,
                        requestURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equalsIgnoreCase("true")) {
                            getMobileGetMobileContentMetaData(myLearningDetalData, isJoinedCommunity);
                            myLearningModel.setAddedToMylearning(1);
                            refreshCatalogContent = true;
                        } else {
                            Toast toast = Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());

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
                ;
                VolleySingleton.getInstance(this).addToRequestQueue(strReq);


            }

        }
    }

    public void checkUserLogin(final MyLearningModel learningModel, final boolean isInApp) {

        final String userName = preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID);

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + userName + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + learningModel.getSiteID();


        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "inside catalog login : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        svProgressHUD.dismiss();
                        if (jsonObj.has("faileduserlogin")) {

                            JSONArray userloginAry = null;
                            try {
                                userloginAry = jsonObj
                                        .getJSONArray("faileduserlogin");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (userloginAry.length() > 0) {

                                String response = null;
                                try {
                                    response = userloginAry
                                            .getJSONObject(0)
                                            .get("userstatus")
                                            .toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (response.contains("Login Failed")) {
                                    Toast.makeText(MyLearningDetailActivity1.this,
                                            getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_authenticationfailedcontactsiteadmin),
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                                if (response.contains("Pending Registration")) {

                                    Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_pleasebepatientawaitingapproval),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        } else if (jsonObj.has("successfulluserlogin")) {

                            try {
                                JSONArray loginResponseAry = jsonObj.getJSONArray("successfulluserlogin");
                                if (loginResponseAry.length() != 0) {
                                    JSONObject jsonobj = loginResponseAry.getJSONObject(0);

                                    String userIdresponse = loginResponseAry
                                            .getJSONObject(0)
                                            .get("userid").toString();
                                    if (userIdresponse.length() != 0) {

                                        if (!isInApp) {
                                            learningModel.setUserID(userIdresponse);
                                            addToMyLearning(learningModel, true);
                                        } else {

                                            inAppActivityCall(learningModel);
                                        }
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
//                        svProgressHUD.dismiss();
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

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void callMobileGetMobileContentMetaData(final MyLearningModel learningModel) {

        String paramsString = "SiteURL=" + learningModel.getSiteURL()
                + "&ContentID=" + learningModel.getContentID()
                + "&userid=" + learningModel.getUserID()
                + "&DelivoryMode=1&IsDownload=1";

        String metaDataUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetMobileContentMetaData?" + paramsString;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, metaDataUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        db.insertTrackObjects(response, learningModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);

    }

    public void getMobileGetMobileContentMetaData(final MyLearningModel learningModel, final boolean isJoinedCommunity) {

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileGetMobileContentMetaData?SiteURL="
                + learningModel.getSiteURL() + "&ContentID=" + learningModel.getContentID() + "&userid="
                + appUserModel.getUserIDValue() + "&DelivoryMode=1";

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "getMobileGetMobileContentMetaData : " + urlStr);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlStr, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObj) {

                        Log.d(TAG, "getMobileGetMobileContentMetaData response : " + jsonObj);
                        if (jsonObj.length() != 0) {
                            boolean isInserted = false;
                            try {
                                isInserted = db.saveNewlySubscribedContentMetadata(jsonObj);
                                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                                    db.updateEventStatus(learningModel, jsonObj, true);
                                }
                                if (isInserted) {
                                    myLearningModel.setAddedToMylearning(1);
                                    db.updateContenToCatalog(myLearningModel);
                                    buttonFirst.setText(getLocalizationValue(JsonLocalekeys.details_button_viewbutton));
                                    buttonFirst.setTag(1);
                                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, MyLearningDetailActivity1.this, uiSettingsModel.getAppButtonTextColor());

                                    iconFirst.setBackground(viewIcon);
                                    refreshCatalogContent = true;

                                    if (isFromCatalog) {

                                        String succesMessage = getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_thiscontentitemhasbeenaddedto) + " " + getLocalizationValue(JsonLocalekeys.mylearning_header_mylearningtitlelabel);
                                        if (isJoinedCommunity) {
                                            succesMessage = succesMessage + getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_thiscontentitemhasbeenaddedto) + getLocalizationValue(JsonLocalekeys.catalog_alertsubtitle_youhavesuccessfullyjoinedcommunity) + learningModel.getSiteName();
                                        }

                                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                                            succesMessage = getLocalizationValue(JsonLocalekeys.events_alertsubtitle_thiseventitemhasbeenaddedto) + " " + getLocalizationValue(JsonLocalekeys.mylearning_header_mylearningtitlelabel);
//                                            db.updateEventAddedToMyLearningInEventCatalog(myLearningModel, 1);
                                            updateEnrolledEvent();

                                        }
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyLearningDetailActivity1.this);
                                        builder.setMessage(succesMessage)
                                                .setCancelable(false)
                                                .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //do things
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error: ", error.getMessage());
//                        svProgressHUD.dismiss();
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

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//        Toast.makeText(this, "You Have Purchased Something ", Toast.LENGTH_SHORT).show();
        Log.v("chip", "Owned Managed Product: " + details.purchaseInfo.purchaseData);
        sendInAppDetails(details);
    }

    @Override
    public void onPurchaseHistoryRestored() {
//        Toast.makeText(this, "onPurchaseHistoryRestored", Toast.LENGTH_SHORT).show();
        for (String sku : billingProcessor.listOwnedProducts())
            Log.v("chip", "Owned Managed Product: " + sku);
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
//        Toast.makeText(this, "onBillingError", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBillingInitialized() {
//        Toast.makeText(this, "onBillingInitialized", Toast.LENGTH_SHORT).show();
    }

    public boolean sendInAppDetails(@Nullable TransactionDetails details) {
        boolean status = false;
        String orderId = "";
        String productId = "";
        String purchaseToken = "";
        String purchaseTime = "";
        try {
            assert details != null;
            orderId = details.purchaseInfo.purchaseData.orderId;
            productId = details.purchaseInfo.purchaseData.productId;
            purchaseToken = details.purchaseInfo.purchaseData.purchaseToken;

            String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
            purchaseTime = dateString.replace(" ", "%20");

        } catch (Exception jx) {
            Log.d("sendInAppDetails", jx.getMessage());

        }
        String urlStr = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileSaveInAppPurchaseDetails?_userId="
                + myLearningModel.getUserID() + "&_siteURL=" + appUserModel.getSiteURL() + "&_contentId="
                + myLearningModel.getContentID() + "&_transactionId=" + orderId + "&_receipt="
                + purchaseToken + "&_productId=" + productId
                + "&_purchaseDate=" + purchaseTime + "&_devicetype=Android";

        urlStr = urlStr.replaceAll(" ", "%20");
        Log.d(TAG, "inappwebcall : " + urlStr);

        vollyService.getStringResponseVolley("INAPP", urlStr, appUserModel.getAuthHeaders());

        return status;
    }

    public void openRelatedContent() {
        GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetailActivity1.this);
    }

    public void openReportsActivity() {

        Intent intentReports = new Intent(MyLearningDetailActivity1.this, Reports_Activity.class);
        intentReports.putExtra("myLearningDetalData", myLearningModel);
        intentReports.putExtra("typeFrom", typeFrom);
        startActivity(intentReports);

    }

    public void getUserRatingsOfTheContent(int skippedRows, final boolean isFromActivityResult, final boolean isToCheckUseRating) throws JSONException {

//        if (skippedRows == 0) {
//            svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
//        }

        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", myLearningModel.getContentID());
        parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
        parameters.put("metadata", "0");
        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("CartID", "");
        parameters.put("iCMS", "0");
        parameters.put("ComponentID", "3");
        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("DetailsCompID", "107");
        parameters.put("DetailsCompInsID", "3291");
        parameters.put("ERitems", "false");
        parameters.put("SkippedRows", skippedRows);
        parameters.put("NoofRows", 3);

        final String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserRatings";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
//                initilizeRatingsListView();
                if (s != null) {
                    try {
                        mapReviewRating(s, isFromActivityResult, isToCheckUseRating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return parameterString.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void initilizeRatingsListView() {


        ratinsgListview.setOnScrollListener(new EndlessScrollListener() {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.d(TAG, "onLoadMore: called ");
                try {
                    if (skippedRows < totalItemsCount - 3 && totalItemsCount != 0) {

                        skippedRows = skippedRows + 3;
                        getUserRatingsOfTheContent(skippedRows, false, false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void mapReviewRating(String response, boolean isFromActivityResult, boolean isToCheckUseRating) throws JSONException {

        if (!isToCheckUseRating)
            ratingsLayout.setVisibility(View.VISIBLE);

        JSONObject jsonObject = new JSONObject(response);

        int recordCount = 0;

        if (jsonObject.has("RecordCount")) {
            recordCount = jsonObject.getInt("RecordCount");
        }

        if (jsonObject.has("EditRating")) {
//            JSONObject userEditRating = jsonObject.getJSONObject("EditRating");
            if (jsonObject.isNull("EditRating")) {
                btnEditReview.setText(getLocalizationValue(JsonLocalekeys.details_button_writeareviewbutton));
                isEditReview = false;
                //updateUiForRating();
            } else {
                btnEditReview.setText(getLocalizationValue(JsonLocalekeys.details_button_edityourreviewbutton));
                isEditReview = true;
                editObj = jsonObject.getJSONObject("EditRating");
            }
        }

        if (isToCheckUseRating)
            return;

        if (jsonObject.has("EditRating")) {

//            JSONObject userEditRating = jsonObject.getJSONObject("EditRating");

            if (!jsonObject.isNull("EditRating")) {
                JSONObject ratinObj = jsonObject.getJSONObject("EditRating");

                if (ratinObj.has("RatingID")) {

                    try {
                        ratingValue = Float.parseFloat(ratinObj.getString("RatingID"));
                        ratingBar.setRating(ratingValue);
//                        myLearningModel.setRatingId("" + ratingValue);
                        db.updateContentRatingToLocalDB(myLearningModel, "" + ratingValue);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        ratingValue = 0;
                    }

                }

            }

        }


//        myLearningModel.setRatingId("" + ratingValue);


        if (jsonObject.has("UserRatingDetails")) {
            JSONArray userRatingJsonAry = jsonObject.getJSONArray("UserRatingDetails");

            if (reviewRatingModelList == null) {
                reviewRatingModelList = new ArrayList<>();
            }

            if (isFromActivityResult) {
                reviewRatingModelList = new ArrayList<>();
            }

            if (userRatingJsonAry.length() > 0) {

                for (int i = 0; i < userRatingJsonAry.length(); i++) {
                    ReviewRatingModel reviewRatingModel = new ReviewRatingModel();
                    JSONObject userObject = userRatingJsonAry.getJSONObject(i);

                    if (userObject.has("UserName")) {

                        reviewRatingModel.userName = userObject.getString("UserName");
                    }

                    if (userObject.has("RatingID")) {

                        reviewRatingModel.rating = userObject.getInt("RatingID");
                    }

                    if (userObject.has("Title")) {

                        reviewRatingModel.title = userObject.getString("Title");
                    }

                    if (userObject.has("Description")) {

                        reviewRatingModel.description = userObject.getString("Description");
                    }

                    if (userObject.has("ReviewDate")) {

                        String formattedDate = convertDateToDayFormat(userObject.getString("ReviewDate"), "yyyy-MM-dd'T'HH:mm:ss.SSS");

                        Log.d(TAG, "ReviewDate: " + formattedDate);
                        reviewRatingModel.reviewDate = formattedDate;
                    }

                    if (userObject.has("picture")) {

                        reviewRatingModel.picture = userObject.getString("picture");
                    }

                    reviewRatingModelList.add(reviewRatingModel);
                }
                ratingsAdapter.refreshList(reviewRatingModelList);
            } else {
                ratingValue = 0;
            }
            String rating = "" + ratingValue;
            if (reviewRatingModelList.size() > 0) {

                int k = 0;

                for (int i = 0; i < reviewRatingModelList.size(); i++) {
                    k = k + reviewRatingModelList.get(i).rating;

                }

                ratingValue = (float) k / recordCount;

                rating = String.format("%.1f", ratingValue);

//                ratingValue = (float)k / reviewRatingModelList.size();
//                myLearningModel.setRatingId("" + ratingValue);

            }
            txtOverallRating.setText(rating);
            overallRatingbar.setRating(ratingValue);
            String ratedStyle = "Rated " + rating + " out of 5 of " + recordCount + " ratings";//

            String replacetheString = getLocalizationValue(JsonLocalekeys.rated_out_of_odf_ratings);
            replacetheString = replacetheString.replace("%.1f", rating);
            replacetheString = replacetheString.replace("%d", "" + recordCount);


            txtRating.setText(getLocalizationValue(JsonLocalekeys.details_label_ratingsandreviewslabel));
            txtAvg.setText(getLocalizationValue(JsonLocalekeys.details_label_averageratinglabel));

            ratedOutOfTxt.setText(replacetheString);


        }


    }

    public void updateUiForRating() {

//        String ratedStyle = "Rated 0 out of 5 of  0 ratings";//
//        ratingBar.setRating(0);
//        ratedOutOfTxt.setText(ratedStyle);
        reviewRatingModelList = new ArrayList<>();
        ratingsAdapter.refreshList(reviewRatingModelList);
    }

    public void openWriteReview() {

        Intent intentReview = new Intent(MyLearningDetailActivity1.this, WriteReviewAcitiviy.class);
        intentReview.putExtra("myLearningDetalData", myLearningModel);
        intentReview.putExtra("isEditReview", isEditReview);
        if (isEditReview) {
            intentReview.putExtra("editObj", (Serializable) editObj.toString());
        }

        startActivityForResult(intentReview, REVIEW_REFRESH);

    }

    public void addExpiryEvets(MyLearningModel catalogModel) throws JSONException {

        JSONObject parameters = new JSONObject();

        //mandatory
        parameters.put("SelectedContent", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("SiteID", catalogModel.getSiteID());
        parameters.put("OrgUnitID", catalogModel.getSiteID());
        parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    MyLearningDetailActivity1.this,
                    getLocalizationValue(JsonLocalekeys.events_alertsubtitle_thiseventitemhasbeenaddedto) + " " + getLocalizationValue(JsonLocalekeys.mylearning_header_mylearningtitlelabel),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendExpiryEventData(parameterString, catalogModel);
        }
    }

    public void sendExpiryEventData(final String postData, final MyLearningModel catalogModel) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/Catalog/AddExpiredContentToMyLearning";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {

                        if (s.contains("true")) {

                            getMobileGetMobileContentMetaData(catalogModel, false);

//                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyLearningDetailActivity.this);
//                            builder.setMessage(getString(R.string.event_add_success))
//                                    .setCancelable(false)
//                                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            //do things
//                                            dialog.dismiss();
//                                            // add event to android calander
////                                            addEventToAndroidDevice(catalogModel);
//                                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);
//
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json"); Beta
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void addToWaitList(MyLearningModel catalogModel) throws JSONException {

        JSONObject parameters = new JSONObject();
        //mandatory
        parameters.put("WLContentID", catalogModel.getContentID());
        parameters.put("UserID", appUserModel.getUserIDValue());
        parameters.put("siteid", catalogModel.getSiteID());
        parameters.put("locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

        String parameterString = parameters.toString();
        boolean isSubscribed = db.isSubscribedContent(catalogModel);
        if (isSubscribed) {
            Toast toast = Toast.makeText(
                    MyLearningDetailActivity1.this,
                    getLocalizationValue(JsonLocalekeys.catalog_label_alreadyinmylearning),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            sendWaitlistEventData(parameterString, catalogModel);
        }
    }

    public void sendWaitlistEventData(final String postData, final MyLearningModel catalogModel) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/MobileLMS/EnrollWaitListEvent";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        if (jsonObj.has("IsSuccess")) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(MyLearningDetailActivity1.this);
                            builder.setMessage(jsonObj.optString("Message"))
                                    .setCancelable(false)
                                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.commoncomponent_alertbutton_okbutton), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();
                                            // add event to android calander
//                                            addEventToAndroidDevice(catalogModel);
                                            db.updateEventAddedToMyLearningInEventCatalog(catalogModel, 1);


                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            Toast toast = Toast.makeText(
                                    MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.commoncomponent_label_unabletoprocess),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MyLearningDetailActivity1.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(this);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectTheDueDate(final MyLearningModel myLearningDetalData, int dueDateTarget) {

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.DATE, dueDateTarget);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                Log.d(TAG, "onDateSet: " + dateFormatter.format(newDate.getTime()));
                dueDate = "";
                dueDate = dateFormatter.format(newDate.getTime());
                addToMyLearningCheckUser(myLearningDetalData, false);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

    public void updateMylearningModel(JSONObject jsonMyLearningColumnObj) throws JSONException {

        if (jsonMyLearningColumnObj.has("SiteName")) {

            myLearningModel.setSiteName(jsonMyLearningColumnObj.get("SiteName").toString());
        }
        // siteurl
        if (jsonMyLearningColumnObj.has("siteurl")) {

            myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

        }
        // siteid
        if (jsonMyLearningColumnObj.has("siteid")) {

            myLearningModel.setSiteID(jsonMyLearningColumnObj.get("siteid").toString());

        }
        // userid
        if (jsonMyLearningColumnObj.has("userid")) {

            myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

        }
        // coursename


        if (jsonMyLearningColumnObj.has("Title")) {

            myLearningModel.setCourseName(jsonMyLearningColumnObj.get("Title").toString());

        }

        // shortdes
        if (jsonMyLearningColumnObj.has("ShortDescription")) {


            Spanned result = fromHtml(jsonMyLearningColumnObj.get("ShortDescription").toString());

            myLearningModel.setShortDes(result.toString());

        }

        String authorName = "";
        if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
            authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

        }

        if (isValidString(authorName)) {
            myLearningModel.setAuthor(authorName);
        } else {
            // author
            if (jsonMyLearningColumnObj.has("AuthorDisplayName")) {

                myLearningModel.setAuthor(jsonMyLearningColumnObj.get("AuthorDisplayName").toString());

            }
        }

        // contentID
        if (jsonMyLearningColumnObj.has("ContentID")) {

            myLearningModel.setContentID(jsonMyLearningColumnObj.get("ContentID").toString());

        }
        // createddate
        if (jsonMyLearningColumnObj.has("createddate")) {

            myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

        }
        // displayName

        myLearningModel.setDisplayName(appUserModel.getDisplayName());
        // durationEndDate
        if (jsonMyLearningColumnObj.has("DurationEndDate")) {

            myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("DurationEndDate").toString());

            String scoreraw = jsonMyLearningColumnObj.getString("DurationEndDate");
            if (isValidString(scoreraw)) {
                myLearningModel.setDurationEndDate(scoreraw);

                myLearningModel.setIsExpiry("false");
                boolean isCompleted = false;
                isCompleted = isCourseEndDateCompleted(scoreraw);

                if (isCompleted) {
                    myLearningModel.setIsExpiry("true");
                } else {
                    myLearningModel.setIsExpiry("false");
                }

            } else {
                myLearningModel.setDurationEndDate("");
                myLearningModel.setIsExpiry("false");
            }
        }

        myLearningModel.setIsExpiry("false");
        // objectID
        if (jsonMyLearningColumnObj.has("objectid")) {

            myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

        }
        // thumbnailimagepath
        if (jsonMyLearningColumnObj.has("ThumbnailImagePath")) {

            String imageurl = jsonMyLearningColumnObj.getString("ThumbnailImagePath");


            if (isValidString(imageurl)) {

                myLearningModel.setThumbnailImagePath(imageurl);
                String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                myLearningModel.setImageData(imagePathSet);


            } else {
                if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                    String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                    if (isValidString(imageurlContentType)) {
                        String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                        myLearningModel.setImageData(imagePathSet);

                    }
                }

            }
        }
        // relatedcontentcount
        if (jsonMyLearningColumnObj.has("relatedconentcount")) {

            myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

        }
        // isDownloaded
        if (jsonMyLearningColumnObj.has("isdownloaded")) {

            myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

        }
        // courseattempts
        if (jsonMyLearningColumnObj.has("courseattempts")) {

            myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

        }
        // objecttypeid
        if (jsonMyLearningColumnObj.has("objecttypeid")) {

            myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

        }
        // scoid
        if (jsonMyLearningColumnObj.has("ScoID")) {

            myLearningModel.setScoId(jsonMyLearningColumnObj.get("ScoID").toString());

        }
        // startpage
        if (jsonMyLearningColumnObj.has("startpage")) {

            myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

        }
        // status
        if (jsonMyLearningColumnObj.has("actualstatus")) {

            String status = jsonMyLearningColumnObj.get("actualstatus").toString();
            if (isValidString(status)) {
                myLearningModel.setStatusActual(jsonMyLearningColumnObj.get("actualstatus").toString());
            } else {
                myLearningModel.setStatusActual("Not Started");
            }

        }

        if (jsonMyLearningColumnObj.has("corelessonstatus")) {

            myLearningModel.setStatusDisplay(jsonMyLearningColumnObj.get("corelessonstatus").toString());

        }

        // userName
        myLearningModel.setUserName(appUserModel.getUserName());
        // longdes
        if (jsonMyLearningColumnObj.has("LongDescription")) {

            Spanned result = fromHtml(jsonMyLearningColumnObj.get("LongDescription").toString());

//                    myLearningModel.setShortDes(result.toString());
            myLearningModel.setLongDes(result.toString());

        }
        // typeofevent
        if (jsonMyLearningColumnObj.has("typeofevent")) {

            int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

            myLearningModel.setTypeofevent(typeoFEvent);

        }

        // medianame
        if (jsonMyLearningColumnObj.has("medianame")) {
            String medianame = "";

            if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                    medianame = "Assessment(Test)";

                } else {
                    medianame = jsonMyLearningColumnObj.get("medianame").toString();
                }
            } else {
                if (myLearningModel.getTypeofevent() == 2) {
                    medianame = "Event (Online)";


                } else if (myLearningModel.getTypeofevent() == 1) {
                    medianame = "Event (Face to Face)";
                }
            }

            myLearningModel.setMediaName(medianame);

        }       // ratingid
        if (jsonMyLearningColumnObj.has("RatingID")) {

            myLearningModel.setRatingId(jsonMyLearningColumnObj.get("RatingID").toString());

        }
        // publishedDate
        if (jsonMyLearningColumnObj.has("publisheddate")) {

            myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

        }
        // eventstartdatedisplay
        if (jsonMyLearningColumnObj.has("EventStartDateTime")) {
            myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("EventStartDateTime").toString());
        }


        //  eventenddatedisplay
        if (jsonMyLearningColumnObj.has("EventEndDateTime")) {

            myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("EventEndDateTime").toString());
        }

        // eventstartdatetime UTC
        if (jsonMyLearningColumnObj.has("EventStartDateTime")) {

            myLearningModel.setEventstartUtcTime(jsonMyLearningColumnObj.get("EventStartDateTime").toString());


        }

        //  eventenddatetime UTC
        if (jsonMyLearningColumnObj.has("EventEndDateTime")) {

            myLearningModel.setEventendUtcTime(jsonMyLearningColumnObj.get("EventEndDateTime").toString());

        }
        // mediatypeid
        if (jsonMyLearningColumnObj.has("mediatypeid")) {

            myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

        }
        // dateassigned
        if (jsonMyLearningColumnObj.has("dateassigned")) {

            myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());


        }
        // keywords
        if (jsonMyLearningColumnObj.has("seokeywords")) {

            myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

        }
        // eventcontentid
        if (jsonMyLearningColumnObj.has("eventcontentid")) {

            myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

        }
        // eventAddedToCalender
        myLearningModel.setEventAddedToCalender(false);


        // locationname
        if (jsonMyLearningColumnObj.has("eventfulllocation")) {

            myLearningModel.setLocationName(jsonMyLearningColumnObj.get("eventfulllocation").toString());

        }
        // timezone
        if (jsonMyLearningColumnObj.has("TimeZone")) {

            myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("TimeZone").toString());

        }
        // participanturl
        if (jsonMyLearningColumnObj.has("participanturl")) {

            myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

        }
        // password

        myLearningModel.setPassword(appUserModel.getPassword());

        // isListView
        if (jsonMyLearningColumnObj.has("bit5")) {

            myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

        }

        // joinurl
        if (jsonMyLearningColumnObj.has("JoinURL")) {

            if (myLearningModel.getTypeofevent() == 2) {


                String joinUrl = jsonMyLearningColumnObj.get("JoinURL").toString();

                if (isValidString(joinUrl)) {
                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("JoinURL").toString());
                }

            } else if (myLearningModel.getTypeofevent() == 1) {
                myLearningModel.setJoinurl("");
            }
        }

        // offlinepath
        if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
            String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
            String startPage = jsonMyLearningColumnObj.get("startpage").toString();
            String contentid = jsonMyLearningColumnObj.get("contentid").toString();
            String downloadDestFolderPath = getExternalFilesDir(null)
                    + "/.Mydownloads/Contentdownloads" + "/" + contentid;

            String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

            myLearningModel.setOfflinepath(finalDownloadedFilePath);
        }
//

        // wresult
        if (jsonMyLearningColumnObj.has("wresult")) {

            myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

        }
        // wmessage
        if (jsonMyLearningColumnObj.has("wmessage")) {

            myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

        }

        // presenter
        if (jsonMyLearningColumnObj.has("Presentername")) {

            myLearningModel.setPresenter(jsonMyLearningColumnObj.get("Presentername").toString());

        }

        //sitename
        if (jsonMyLearningColumnObj.has("progress")) {

            myLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
            if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started")) {

            }
        }

        //membershipname
        if (jsonMyLearningColumnObj.has("membershipname")) {

            myLearningModel.setMembershipname(jsonMyLearningColumnObj.get("membershipname").toString());

        }
        //membershiplevel
        if (jsonMyLearningColumnObj.has("membershiplevel")) {

            myLearningModel.setMemberShipLevel(jsonMyLearningColumnObj.getInt("membershiplevel"));

        }

        //membershiplevel
        if (jsonMyLearningColumnObj.has("folderpath")) {

            myLearningModel.setFolderPath(jsonMyLearningColumnObj.getString("folderpath"));

        }

        if (jsonMyLearningColumnObj.has("IsArchived")) {

            myLearningModel.setArchived(jsonMyLearningColumnObj.getBoolean("IsArchived"));

        }


        //jwvideokey
        if (jsonMyLearningColumnObj.has("jwvideokey")) {

            String jwKey = jsonMyLearningColumnObj.getString("jwvideokey");

            if (isValidString(jwKey)) {
                myLearningModel.setJwvideokey(jwKey);
            } else {
                myLearningModel.setJwvideokey("");
            }

        }

        //cloudmediaplayerkey
        if (jsonMyLearningColumnObj.has("cloudmediaplayerkey")) {

            myLearningModel.setCloudmediaplayerkey(jsonMyLearningColumnObj.optString("cloudmediaplayerkey"));

            String jwKey = jsonMyLearningColumnObj.getString("cloudmediaplayerkey");

            if (isValidString(jwKey)) {
                myLearningModel.setCloudmediaplayerkey(jwKey);
            } else {
                myLearningModel.setCloudmediaplayerkey("");
            }
        }

        myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.optString("iconpath", ""));

        myLearningModel.setIsRequired(jsonMyLearningColumnObj.optInt("required ", 0));

        myLearningModel.setTotalratings(jsonMyLearningColumnObj.optInt("totalratings", 0));

        if (jsonMyLearningColumnObj.has("LongDescription")) {
            myLearningModel.setLongDes(jsonMyLearningColumnObj.getString("LongDescription"));
        }
        if (jsonMyLearningColumnObj.has("EventScheduleType")) {
            myLearningModel.setEventScheduleType(jsonMyLearningColumnObj.getInt("EventScheduleType"));
        }
        if (jsonMyLearningColumnObj.has("TableofContent")) {
            myLearningModel.setTableofContent(jsonMyLearningColumnObj.getString("TableofContent"));
        }
        if (jsonMyLearningColumnObj.has("LearningObjectives")) {
            myLearningModel.setLearningObjectives(jsonMyLearningColumnObj.getString("LearningObjectives"));
        }

        if (jsonMyLearningColumnObj.has("ThumbnailVideoPath")) {
            myLearningModel.setThumbnailVideoPath(jsonMyLearningColumnObj.getString("ThumbnailVideoPath"));
        }

        if (jsonMyLearningColumnObj.has("ThumbnailIconPath")) {
            myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.getString("ThumbnailIconPath"));
        }

        refreshTheActivity();
    }

    public void refreshTheActivity() {

        finish();
        overridePendingTransition(0, 0);
        getIntent().putExtra("myLearningDetalData", myLearningModel);
        getIntent().putExtra("SHEDULE", true);
        startActivity(getIntent());
        overridePendingTransition(0, 0);

    }
}
