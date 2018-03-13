package com.instancy.instancylearning.mylearning;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;

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
import com.instancy.instancylearning.mainactivities.NativeSettings;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MembershipModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.wifisharing.WiFiDirectActivity;
import com.instancy.instancylearning.wifisharing.WiFiDirectNewActivity;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.MYLEARNING_FRAGMENT_OPENED_FIRSTTIME;
import static com.instancy.instancylearning.utils.Utilities.getButtonDrawable;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isMemberyExpry;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 6/27/2017 Working on InstancyLearning.
 */

public class MyLearningDetail_Activity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor billingProcessor;
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    @BindView(R.id.txt_title_name)
    TextView txtTitle;

    @BindView(R.id.btntxt_download_detail)
    TextView btnDownload;

    @BindView(R.id.txtDescription)
    TextView txtDescription;

    @BindView(R.id.txtLongDesc)
    TextView txtLongDisx;

    @BindView(R.id.imagethumb)
    ImageView imgThumb;

    @BindView(R.id.txt_coursename)
    TextView txtCourseName;

    @BindView(R.id.rat_detail_ratingbar)
    RatingBar ratingBar;

    @BindView(R.id.txt_course_progress)
    TextView txtCourseStatus;

    @BindView(R.id.course_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.txt_author)
    TextView txtAuthor;

    @BindView(R.id.txt_site_name)
    TextView txtSiteName;

    @Nullable
    @BindView(R.id.circle_progress)
    CircleProgressBar circleProgressBar;

    @Nullable
    @BindView(R.id.consolidateline)
    View consolidateLine;

    @BindView(R.id.downloadlayout)
    RelativeLayout downloadlayout;

    @Nullable
    @BindView(R.id.detail_layout)
    RelativeLayout relativeLayout;

    @BindView(R.id.btn_price)
    TextView txtPrice;

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
    boolean refreshCatalogContent = false;
    UiSettingsModel uiSettingsModel;
    MembershipModel membershipModel = null;
    public SetCompleteListner setCompleteListner;
    CmiSynchTask cmiSynchTask;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylearning_detail_activity);
        ButterKnife.bind(this);
        PreferencesManager.initializeInstance(this);
        preferencesManager = PreferencesManager.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        membershipModel = new MembershipModel();
        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {

            myLearningModel = (MyLearningModel) bundle.getSerializable("myLearningDetalData");
            isFromCatalog = bundle.getBoolean("IFROMCATALOG");

        }
        String apiKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZKOgrgA0BACsUqzZ49Xqj1SEWSx/VNSQ7e/WkUdbn7Bm2uVDYagESPHd7xD6cIUZz9GDKczG/fkoShHZdMCzWKiq07BzWnxdSaWa4rRMr+uylYAYYvV5I/R3dSIAOCbbcQ1EKUp5D7c2ltUpGZmHStDcOMhyiQgxcxZKTec6YiJ17X64Ci4adb9X/ensgOSduwQwkgyTiHjklCbwyxYSblZ4oD8WE/Ko9003VrD/FRNTAnKd5ahh2TbaISmEkwed/TK4ehosqYP8pZNZkx/bMsZ2tMYJF0lBUl5i9NS+gjVbPX4r013Pjrnz9vFq2HUvt7p26pxpjkBTtkwVgnkXQIDAQAB";

//        if (!isFromCatalog){
        billingProcessor = new BillingProcessor(this, apiKey, this);
//        }

        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        webAPIClient = new WebAPIClient(this);
        db = new DatabaseHandler(this);
        membershipModel = db.fetchMembership(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue());

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        appController = AppController.getInstance();

        typeLayout2(isFromCatalog, uiSettingsModel);

        if (myLearningModel != null) {
            txtTitle.setText(myLearningModel.getCourseName());
            txtCourseName.setText(myLearningModel.getMediaName());

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

                txtAuthor.setText(myLearningModel.getPresenter() + " ");
            } else {

                txtAuthor.setText(myLearningModel.getAuthor() + " ");
            }

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

            if (getResources().getString(R.string.app_name).equalsIgnoreCase(getResources().getString(R.string.crop_life))) {
                ratingBar.setVisibility(View.INVISIBLE);
            }

            if (myLearningModel.getLongDes().isEmpty()) {

                txtDescription.setVisibility(View.GONE);
            } else {
                txtLongDisx.setVisibility(View.VISIBLE);

            }

            if (myLearningModel.getShortDes().isEmpty()) {
                txtDescription.setVisibility(View.GONE);
            } else {

                txtDescription.setVisibility(View.VISIBLE);
            }

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                btnDownload.setVisibility(View.GONE);
                circleProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);

            } else {

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.getObjecttypeId().equalsIgnoreCase("688") || myLearningModel.getObjecttypeId().equalsIgnoreCase("688") || uiSettingsModel.getContentDownloadType().equalsIgnoreCase("0")) {
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
//                                    .text("Click to download the content").clickToHide(true)
//                                    .show();
//                        }

                    }

//                    btnDownload.setVisibility(View.GONE);
                    circleProgressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.VISIBLE);
                txtCourseStatus.setVisibility(View.VISIBLE);

            }

            if (isFromCatalog) {

                progressBar.setVisibility(View.GONE);
                txtCourseStatus.setVisibility(View.GONE);
            }

            String imgUrl = myLearningModel.getImageData();

//            ViewTooltip
//                    .on(imgThumb)
//                    .autoHide(true, 5000)
//                    .corner(30)
//                    .position(ViewTooltip.Position.BOTTOM)
//                    .text("Click on image to view")
//                    .show();


//            Glide.with(this).load(imgUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .placeholder(R.drawable.cellimage)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgThumb);

            Picasso.with(this).load(imgUrl).placeholder(R.drawable.cellimage).into(imgThumb);

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

        try {
            getUserRatingsOfTheContent();
        } catch (JSONException e) {
            e.printStackTrace();
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
                                            refreshCatalogContent = true;
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

//    public void typeLayout(boolean isCatalog, UiSettingsModel uiSettingsModel) {
//
//        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
//        btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//        relativeSecond.setVisibility(View.GONE);
//        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
//
//        FontManager.markAsIconContainer(findViewById(R.id.btntxt_download_detail), iconFont);
//        if (isCatalog) {
//
//            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
//
//                buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_plus, this, uiSettingsModel.getAppHeaderTextColor());
//
//
//                if (myLearningModel.getAddedToMylearning() == 1) {
//                    buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                    btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                    iconFirst.setBackground(calendarImg);
//                    buttonFirst.setText(getResources().getString(R.string.btn_txt_add_to_calendar));
//
//                } else {
//
//                    iconFirst.setBackground(calendarImg);
//
//                    buttonFirst.setText(getResources().getString(R.string.btn_txt_enroll));
//                }
//
//
//            } else {
//
//                buttonFirst.setText("Add");
//                Drawable addPlusIcon = getButtonDrawable(R.string.fa_icon_plus_circle, this, uiSettingsModel.getAppHeaderTextColor());
//                iconFirst.setBackground(addPlusIcon);
//                if (myLearningModel.getViewType().equalsIgnoreCase("3")) {
//                    Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppHeaderTextColor());
//                    iconFirst.setBackground(cartIcon);
//                    buttonFirst.setText("Buy");
//                    txtPrice.setText("$" + myLearningModel.getPrice());
//
//                    txtPrice.setVisibility(View.VISIBLE);
//                    btnDownload.setVisibility(View.GONE);
//                } else {
//                    if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
//                        btnDownload.setVisibility(View.GONE);
//                    } else {
//                        btnDownload.setVisibility(View.VISIBLE);
//                    }
//                    txtPrice.setVisibility(View.GONE);
//                    txtPrice.setText("");
//                }
//                if (myLearningModel.getAddedToMylearning() == 1) {
//                    buttonFirst.setText("View");
//                    txtPrice.setVisibility(View.GONE);
//                    txtPrice.setText("");
//                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppHeaderTextColor());
//                    iconFirst.setBackground(viewIcon);
//
//                }
//            }
//        } else {
//            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
//                buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppHeaderTextColor());
//                iconFirst.setBackground(calendarImg);
//                buttonFirst.setText(getResources().getString(R.string.btn_txt_add_to_calendar));
//
//                if (myLearningModel.getIsListView().equalsIgnoreCase("true") && !myLearningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
//                    relativeSecond.setVisibility(View.VISIBLE);
//                    Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppHeaderTextColor());
//                    iconSecond.setBackground(relatedContent);
//                    buttonSecond.setText(getResources().getString(R.string.btn_txt_report));
//                }
//
//            } else {
//
//                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") && !myLearningModel.getStatus().toLowerCase().contains("completed")) {
//                    relativeSecond.setVisibility(View.VISIBLE);
//                    buttonSecond.setText(getResources().getString(R.string.btn_txt_setcomplete));
//                    Drawable relatedContent = getButtonDrawable(R.string.fa_icon_check, this, uiSettingsModel.getAppHeaderTextColor());
//                    iconSecond.setBackground(relatedContent);
//                }
//
//                Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppHeaderTextColor());
//                iconFirst.setBackground(viewIcon);
//                buttonFirst.setText("View");
//                txtPrice.setVisibility(View.GONE);
//                txtPrice.setText("");
//                btnDownload.setVisibility(View.VISIBLE);
//            }
//        }
//
//        setCompleteListner = new SetCompleteListner() {
//            @Override
//            public void completedStatus() {
//
//                relativeSecond.setVisibility(View.GONE);
//                statusUpdate("Completed");
//
//            }
//        };
//    }


    public void typeLayout2(boolean isCatalog, UiSettingsModel uiSettingsModel) {

        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        relativeSecond.setVisibility(View.GONE);
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        FontManager.markAsIconContainer(findViewById(R.id.btntxt_download_detail), iconFont);
        if (isCatalog) {

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

                buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_plus, this, uiSettingsModel.getAppHeaderTextColor());
                Drawable reportImg = getButtonDrawable(R.string.fa_icon_filter, this, uiSettingsModel.getAppHeaderTextColor());

                if (myLearningModel.getAddedToMylearning() == 1) {
                    buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                    btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                    iconFirst.setBackground(calendarImg);
                    buttonFirst.setText(getResources().getString(R.string.btn_txt_add_to_calendar));

                } else {

                    iconFirst.setBackground(calendarImg);

                    buttonFirst.setText(getResources().getString(R.string.btn_txt_enroll));
                }


            } else {

                buttonFirst.setText("Add");
                Drawable addPlusIcon = getButtonDrawable(R.string.fa_icon_plus_circle, this, uiSettingsModel.getAppHeaderTextColor());
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
                            buttonFirst.setText("Add");

                        } else {

                            Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppHeaderTextColor());
                            iconFirst.setBackground(cartIcon);
                            buttonFirst.setText("Buy");

                        }
                    } else {

                        Drawable cartIcon = getButtonDrawable(R.string.fa_icon_cart_plus, this, uiSettingsModel.getAppHeaderTextColor());
                        iconFirst.setBackground(cartIcon);
                        buttonFirst.setText("Buy");
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
                    buttonFirst.setText("View");
                    txtPrice.setVisibility(View.GONE);
                    txtPrice.setText("");
                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppHeaderTextColor());
                    iconFirst.setBackground(viewIcon);

                }
            }
        } else {
            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                buttonFirst.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                buttonSecond.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                btnsLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
                Drawable calendarImg = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppHeaderTextColor());
                iconFirst.setBackground(calendarImg);
                buttonFirst.setText(getResources().getString(R.string.btn_txt_add_to_calendar));

                if (myLearningModel.getIsListView().equalsIgnoreCase("true") && !myLearningModel.getRelatedContentCount().equalsIgnoreCase("0")) {
                    relativeSecond.setVisibility(View.VISIBLE);
                    Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppHeaderTextColor());
                    iconSecond.setBackground(relatedContent);
                    buttonSecond.setText(getResources().getString(R.string.btn_txt_report));
                }

            } else {

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.getObjecttypeId().equalsIgnoreCase("20") | myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("52") || myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {

                    if (!myLearningModel.getStatus().toLowerCase().contains("completed")) {
                        buttonSecond.setText(getResources().getString(R.string.btn_txt_setcomplete));
                        relativeSecond.setVisibility(View.VISIBLE);
                        Drawable relatedContent = getButtonDrawable(R.string.fa_icon_check, this, uiSettingsModel.getAppHeaderTextColor());
                        iconSecond.setBackground(relatedContent);
                    }

                } else {
// uncomment after reports completed
                    relativeSecond.setVisibility(View.VISIBLE);
                    Drawable relatedContent = getButtonDrawable(R.string.fa_icon_bar_chart, this, uiSettingsModel.getAppHeaderTextColor());
                    iconSecond.setBackground(relatedContent);

                    buttonSecond.setText("Report");

                }

                Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, this, uiSettingsModel.getAppHeaderTextColor());
                iconFirst.setBackground(viewIcon);
                buttonFirst.setText("View");
                txtPrice.setVisibility(View.GONE);
                txtPrice.setText("");
                btnDownload.setVisibility(View.VISIBLE);
            }

        }

        setCompleteListner = new SetCompleteListner() {
            @Override
            public void completedStatus() {

                relativeSecond.setVisibility(View.GONE);
                statusUpdate("Completed");

            }
        };
    }


    public void updateEnrolledEvent() {
        Drawable calendarImg = getButtonDrawable(R.string.fa_icon_calendar, this, uiSettingsModel.getAppHeaderTextColor());
        iconFirst.setBackground(calendarImg);
        buttonFirst.setText(getResources().getString(R.string.btn_txt_add_to_calendar));
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
                statusValue = "Completed";

            } else if (courseStatus.equalsIgnoreCase("failed")) {

                statusValue = "Completed(failed)";
            } else if (courseStatus.equalsIgnoreCase("passed")) {

                statusValue = "Completed(passed)";

            }

            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusCompleted)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusCompleted));
//                courseStatus = trackChildList.getStatus() + " (" + trackChildList.getProgress();
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
            String statusValue = "In Progress";
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusInProgress)));
            progressBar.setProgress(Integer.parseInt(progressPercent));
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusInProgress));
            displayStatus = statusValue + " (" + progressPercent;

        } else if (courseStatus.toLowerCase().contains("pending review") || (courseStatus.toLowerCase().contains("pendingreview"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = courseStatus;
            progressBar.setProgress(100);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            displayStatus = status + "(" + 100;
        } else if (courseStatus.equalsIgnoreCase("Registered") || (courseStatus.toLowerCase().contains("registered"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGray)));
            String status = "";

            status = courseStatus;

            progressBar.setProgress(100);
            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorGray));
            courseStatus = status;
        } else if (courseStatus.toLowerCase().contains("attended") || (courseStatus.toLowerCase().contains("registered"))) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = courseStatus;


            txtCourseStatus.setTextColor(getResources().getColor(R.color.colorStatusOther));
            courseStatus = status;
        } else if (courseStatus.toLowerCase().contains("Expired")) {
            progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = courseStatus;

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
                            Toast.makeText(MyLearningDetail_Activity.this, "Purchase failed", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                    }

                }


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
        if (isFromCatalog) {
            intent.putExtra("REFRESH", refreshCatalogContent);
        } else {

            intent.putExtra("refresh", "refresh");

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

                    intent.putExtra("refresh", "refresh");

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

    @OnClick({R.id.relativeone, R.id.relativesecond, R.id.btntxt_download_detail})
    public void actionsforDetail(View view) {
        switch (view.getId()) {
            case R.id.view_btn_txt:
            case R.id.relativeone:
                if (buttonFirst.getText().toString().equalsIgnoreCase("View")) {
                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetail_Activity.this);
                } else if (buttonFirst.getText().toString().equalsIgnoreCase("Add")) {
                    addToMyLearningCheckUser(myLearningModel, false); // false for
                } else if (buttonFirst.getText().toString().equalsIgnoreCase("Buy")) {
                    addToMyLearningCheckUser(myLearningModel, true);
                } else if (buttonFirst.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_txt_add_to_calendar))) {
                    GlobalMethods.addToDeviceCalendar(myLearningModel, this);
                } else if (buttonFirst.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_txt_enroll))) {

                    addToMyLearningCheckUser(myLearningModel, false);
                }
                break;
            case R.id.btntxt_download_detail:
                downloadTheCourse(myLearningModel, view);
                break;
            case R.id.relativesecond:

                if (buttonSecond.getText().toString().equalsIgnoreCase(getResources().getString(R.string.btn_txt_setcomplete))) {
                    new SetCourseCompleteSynchTask(this, db, myLearningModel, setCompleteListner).execute();
                } else {
                    openReportsActivity();
                }
                break;
        }
    }

    public void inAppActivityCall(MyLearningModel learningModel) {

        if (!BillingProcessor.isIabServiceAvailable(this)) {

            Toast.makeText(this, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16", Toast.LENGTH_SHORT).show();
        }

        String testId = "android.test.purchased";
        String productId = learningModel.getGoogleProductID();

//        if (productId.length() != 0) {
//            billingProcessor.purchase(MyLearningDetail_Activity.this, productId);
//        }

        String originalproductid = learningModel.getGoogleProductID();

        if (originalproductid.length() != 0) {
            Intent intent = new Intent();
            intent.putExtra("learningdata", learningModel);
            billingProcessor.handleActivityResult(8099, 80, intent);
            billingProcessor.purchase(MyLearningDetail_Activity.this, originalproductid);
        } else {
            Toast.makeText(MyLearningDetail_Activity.this, "Inapp id not configured in server", Toast.LENGTH_SHORT).show();
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

                        if (isNetworkConnectionAvailable(MyLearningDetail_Activity.this, -1)) {
                            getStatusFromServer(myLearningModel);

                        }
                    } else {
                        if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            int i = -1;
                            i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                            if (i == 1) {
//                                Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();

                                statusUpdate(getResources().getString(R.string.metadata_status_progress));
                            } else {

//                                Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } else {

                    if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                        int i = -1;
                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
                        statusUpdate(getResources().getString(R.string.metadata_status_progress));
                        if (i == 1) {
//                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
//                            myLearningAdapter.notifyDataSetChanged();
//                            injectFromDbtoModel();
                        } else {

//                            Toast.makeText(context, "Unable to update the status", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        statusUpdate(myLearningModel.getStatus());
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
//                    if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
//                        int i = -1;
//                        i = db.updateContentStatus(myLearningModel, getResources().getString(R.string.metadata_status_progress), "50");
//                        if (i == 1) {
////                            Toast.makeText(MyLearningDetail_Activity.this, "Status updated!", Toast.LENGTH_SHORT).show();
//                            statusUpdate(getResources().getString(R.string.metadata_status_progress));
//                        } else {
////                            Toast.makeText(MyLearningDetail_Activity.this, "Unable to update the status", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                }
            }
        }

        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data)) {

            super.onActivityResult(requestCode, resultCode, data);
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

        if (learningModel.getAddedToMylearning() == 0 && isFromCatalog) {
            addToMyLearning(learningModel, false);
        }

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
                        this.getString(R.string.cat_add_already),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                String requestURL = appUserModel.getWebAPIUrl() + "/MobileLMS/MobileAddtoMyCatalog?"
                        + "UserID=" + myLearningDetalData.getUserID() + "&SiteURL=" + myLearningDetalData.getSiteURL()
                        + "&ContentID=" + myLearningDetalData.getContentID() + "&SiteID=" + myLearningDetalData.getSiteID();
                requestURL = requestURL.replaceAll(" ", "%20");
                Log.d(TAG, "inside catalog login : " + requestURL);
                StringRequest strReq = new StringRequest(Request.Method.GET,
                        requestURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "add to mylearning data " + response.toString());
                        if (response.equalsIgnoreCase("true")) {
                            getMobileGetMobileContentMetaData(myLearningDetalData, isJoinedCommunity);
                            myLearningModel.setAddedToMylearning(1);
                            refreshCatalogContent = true;
                        } else {
                            Toast toast = Toast.makeText(
                                    MyLearningDetail_Activity.this, "Unable to process request",
                                    Toast.LENGTH_SHORT);
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

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/LoginDetails?UserName="
                + learningModel.getUserName() + "&Password=" + learningModel.getPassword() + "&MobileSiteURL="
                + appUserModel.getSiteURL() + "&DownloadContent=&SiteID=" + appUserModel.getSiteIDValue();


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
                                    Toast.makeText(MyLearningDetail_Activity.this,
                                            "Authentication Failed. Contact site admin",
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                                if (response.contains("Pending Registration")) {

                                    Toast.makeText(MyLearningDetail_Activity.this, "Please be patient while awaiting approval. You will receive an email once your profile is approved.",
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
                                    db.updateEventStatus(learningModel, jsonObj);
                                }
                                if (isInserted) {
                                    myLearningModel.setAddedToMylearning(1);
                                    db.updateContenToCatalog(myLearningModel);
                                    buttonFirst.setText("View");
                                    Drawable viewIcon = getButtonDrawable(R.string.fa_icon_eye, MyLearningDetail_Activity.this, uiSettingsModel.getAppHeaderTextColor());

                                    iconFirst.setImageDrawable(viewIcon);
                                    refreshCatalogContent = true;

                                    if (isFromCatalog) {

                                        String succesMessage = "Content Added to My Learning";
                                        if (isJoinedCommunity) {
                                            succesMessage = "This content item has been added to My Learning page. You have successfully joined the Learning Community: " + learningModel.getSiteName();
                                        }

                                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                                            succesMessage = getResources().getString(R.string.event_add_success);
                                            db.updateEventAddedToMyLearningInEventCatalog(myLearningModel, 1);
                                            updateEnrolledEvent();

                                        }
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyLearningDetail_Activity.this);
                                        builder.setMessage(succesMessage)
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        GlobalMethods.launchCourseViewFromGlobalClass(myLearningModel, MyLearningDetail_Activity.this);
    }

    public void openReportsActivity() {

        Intent intentReports = new Intent(MyLearningDetail_Activity.this, Reports_Activity.class);
        intentReports.putExtra("myLearningDetalData", myLearningModel);
        startActivity(intentReports);

    }


    public void getUserRatingsOfTheContent() throws JSONException {

        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", myLearningModel.getContentID());
        parameters.put("Locale", "en-us");
        parameters.put("metadata", "0");
        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("CartID", "");
        parameters.put("iCMS", "0");
        parameters.put("ComponentID", "3");
        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("DetailsCompID", "107");
        parameters.put("DetailsCompInsID", "3291");
        parameters.put("ERitems", "false");
        parameters.put("SkippedRows", 0);
        parameters.put("NoofRows", 100);

        final String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserRatings";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
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
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return parameterString.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

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


}
