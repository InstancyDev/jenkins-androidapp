package com.instancy.instancylearning.mylearning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ReviewContentModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 * http://androidcocktail.blogspot.in/2014/03/android-spannablestring-example.html
 */

public class WriteReviewAcitiviy extends AppCompatActivity {

    Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = WriteReviewAcitiviy.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;

    DatabaseHandler db;
    ResultListner resultListner = null;

    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;

    boolean isEditReview = false;

    float finalRating = 0;

    JSONObject editObj = null;

    MyLearningModel learningModel;

    ReviewContentModel reviewContentModel;

    @Nullable
    @BindView(R.id.txtcancel)
    TextView txtCancel;

    @Nullable
    @BindView(R.id.txtsave)
    TextView txtSave;


    @Nullable
    @BindView(R.id.edit_description)
    EditText editDescription;

    @BindView(R.id.detailRatingBar)
    RatingBar ratingBar;

    @Nullable
    @BindView(R.id.bottomlayout)
    LinearLayout bottomLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writeareview);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        initVolleyCallback();
        vollyService = new VollyService(resultCallback, context);

        reviewContentModel = new ReviewContentModel();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            learningModel = (MyLearningModel) bundle.getSerializable("myLearningDetalData");
            isEditReview = bundle.getBoolean("isEditReview");

            if (isEditReview) {

                try {
                    editObj = new JSONObject(getIntent().getStringExtra("editObj"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                learningModel.getCourseName() + "</font>"));


        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        try {
            initilizeHeaderView();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initilizeHeaderView() throws JSONException {
        float ratingValue = 0;

        try {
            ratingValue = Float.parseFloat(learningModel.getRatingId());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            ratingValue = 0;
        }

        ratingBar.setRating(ratingValue);
        finalRating = ratingValue;

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser) {
//                    int ratingVa = Math.round(rating);
                    finalRating = rating;
                }
            }
        });

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);
        bottomLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        if (isEditReview) {
            editDescription.getText().toString().trim();
//        "EditRating" -> "{"UserName":null,"RatingID":5,"Title":null,"Description":"From Android Native App","ReviewDate":"0001-01-01T00:00:00","RatingUserID":1,"picture":null,"RatingSiteID":null,"intApprovalStatus":null,"ErrorMessage":""}"

            if (editObj.has("Description")) {

                reviewContentModel.description = editObj.getString("Description");

            }

            editDescription.setText(reviewContentModel.description);
        }

    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

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
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tracklistmenu, menu);
        MenuItem itemInfo = menu.findItem(R.id.tracklist_help);
        Drawable myIcon = getResources().getDrawable(R.drawable.help);
        itemInfo.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
        itemInfo.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static Drawable setTintDrawable(Drawable drawable, @ColorInt int color) {
        drawable.clearColorFilter();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.invalidateSelf();
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.txtsave, R.id.txtcancel})
    public void actionsBottomBtns(View view) {

        switch (view.getId()) {
            case R.id.txtsave:
                try {
                    validateNewRating();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txtcancel:
                finish();
                break;

        }

    }

    public void validateNewRating() throws JSONException {

        String descriptionStr = editDescription.getText().toString().trim();
        String dateString = getCurrentDateTime("yyyy-MM-dd HH:mm:ss");

        if (descriptionStr.length() < 10) {
            Toast.makeText(WriteReviewAcitiviy.this, "Enter description", Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();

            parameters.put("ContentID", learningModel.getContentID());
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("Title", "");
            parameters.put("Description", descriptionStr);
            parameters.put("ReviewDate", dateString);
            parameters.put("Rating", finalRating);

            final String parameterString = parameters.toString();

            String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/AddRatings";

            final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    svProgressHUD.dismiss();
                    Log.d(TAG, "onResponse: " + s);
//                initilizeRatingsListView();
                    if (s != null) {
                        closeForum(true);
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

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWREVIEW", refresh);
        intent.putExtra("myLearningDetalData", learningModel);
        setResult(RESULT_OK, intent);
        finish();
    }
}

