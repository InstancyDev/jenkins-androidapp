package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourms.DiscussionCommentsActivity;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UpvotersModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKRESPONSES;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class AskExpertsAnswersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    final Context context = this;
    SVProgressHUD svProgressHUD;
    String TAG = AskExpertsAnswersActivity.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    DatabaseHandler db;
    List<AskExpertAnswerModel> askExpertAnswerModelList = null;
    ResultListner resultListner = null;
    AskExpertQuestionModel askExpertQuestionModel;
    PreferencesManager preferencesManager;
    RelativeLayout relativeLayout;
    AppController appController;
    UiSettingsModel uiSettingsModel;
    AskExpertAnswerAdapter askExpertAnswerAdapter;

    ListView lv_languages;
    BottomSheetDialog bottomSheetDialog;

    boolean refreshAnswers = false;

    UpvotersAdapter upvotersAdapter;

    List<UpvotersModel> upvotersModelList;


    @Nullable
    @BindView(R.id.askexpertslistview)
    ListView askExpertsListView;

    View header;
    View footor;

    @Nullable
    @BindView(R.id.nodata_label)
    TextView noDataLabel;

    @Nullable
    @BindView(R.id.topicheader)
    RelativeLayout topicheader;

    @Nullable
    @BindView(R.id.fab_comment_button)
    FloatingActionButton floatingActionButton;

    @Nullable
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.askexpertsanswersactivity);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_relative);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appController = AppController.getInstance();
        db = new DatabaseHandler(this);
        ButterKnife.bind(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        uiSettingsModel = db.getAppSettingsFromLocal(appUserModel.getSiteURL(), appUserModel.getSiteIDValue());
        relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        svProgressHUD = new SVProgressHUD(context);

        topicheader.setVisibility(View.GONE);

        upvotersAdapter = new UpvotersAdapter(this, upvotersModelList);

        header = (View) getLayoutInflater().inflate(R.layout.askexpertcell_en, null);
        footor = (View) getLayoutInflater().inflate(R.layout.no_data_layout, null);

        vollyService = new VollyService(resultCallback, context);
        askExpertQuestionModel = (AskExpertQuestionModel) getIntent().getSerializableExtra("AskExpertQuestionModel");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" +
                "Answers" + "</font>"));

        askExpertAnswerAdapter = new AskExpertAnswerAdapter(this, BIND_ABOVE_CLIENT, askExpertAnswerModelList);
        askExpertsListView.setAdapter(askExpertAnswerAdapter);
        askExpertsListView.setOnItemClickListener(this);
//        askExpertsListView.setEmptyView(findViewById(R.id.nodata_label));
//        askExpertsListView.addHeaderView(header);
        askExpertsListView.addFooterView(footor);
        askExpertAnswerModelList = new ArrayList<AskExpertAnswerModel>();
        askExpertsListView.addHeaderView(header, null, false);

        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        initilizeHeaderView(header);
        injectFromDbtoModel();
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(this).inflate(R.layout.iconcomment, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(this, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        floatingActionButton.setImageDrawable(d);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openCommentView();
                openCommentAndAnswerActivity(false);

            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        if (appUserModel.getUserIDValue().equalsIgnoreCase(askExpertQuestionModel.postedUserId)) {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    public void openCommentAndAnswerActivity(boolean isFrom) {

        Intent intentDetail = new Intent(context, AskExpertsAskAnsCmtActivity.class);
        intentDetail.putExtra("AskExpertQuestionModel", askExpertQuestionModel);
        intentDetail.putExtra("ISCOMMENT", false);
        startActivity(intentDetail);

    }


    public void openCommentsActivity(boolean isFrom) {

        Intent intentDetail = new Intent(context, AskExpertsCommentsActivity.class);
        intentDetail.putExtra("AskExpertQuestionModel", askExpertQuestionModel);
        startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);

    }


    public void initilizeHeaderView(View view) {

//        @Nullable
//        @BindView(R.id.card_view)
//        CardView card_view;

        ImageButton btnContextMenu = (ImageButton) view.findViewById(R.id.btn_contextmenu);
        TextView txtAllActivites = (TextView) view.findViewById(R.id.txt_all_activites);
        TextView txtNoAnswers = (TextView) view.findViewById(R.id.txtno_answers);
        TextView txtQuestion = (TextView) view.findViewById(R.id.txt_question);

        ImageView imagethumb = (ImageView) view.findViewById(R.id.imagethumb);

        CustomFlowLayout tagsSkills = (CustomFlowLayout) view.findViewById(R.id.cflBreadcrumb);


        btnContextMenu.setVisibility(View.INVISIBLE);
        txtQuestion.setText(askExpertQuestionModel.userQuestion);
        txtAllActivites.setText("Asked by: " + askExpertQuestionModel.username + " ");

        txtAllActivites.setText("Asked by: " + askExpertQuestionModel.username + "   |   " + "Asked on: " + askExpertQuestionModel.postedDate + "   |   " + "Last active: " + askExpertQuestionModel.postedDate);

        txtNoAnswers.setText(askExpertQuestionModel.answers + " Answer(s)");
        txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtAllActivites.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

//        imagethumb.setVisibility(View.GONE);
//        card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));


        List<ContentValues> breadcrumbItemsList = null;

        breadcrumbItemsList = new ArrayList<ContentValues>();
        breadcrumbItemsList = generateTagsList(10);

        generateBreadcrumb(breadcrumbItemsList, tagsSkills, this);

    }


    public void injectFromDbtoModel() {
        askExpertAnswerModelList = db.fetchAskExpertAnswersModelList("" + askExpertQuestionModel.questionID);
        if (askExpertAnswerModelList != null) {
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
            noDataLabel.setVisibility(View.INVISIBLE);
            footor.setVisibility(View.GONE);
        } else {
            askExpertAnswerModelList = new ArrayList<AskExpertAnswerModel>();
            askExpertAnswerAdapter.refreshList(askExpertAnswerModelList);
            noDataLabel.setVisibility(View.INVISIBLE);
            footor.setVisibility(View.VISIBLE);
        }

//        txtNoAnswers.setText(askExpertAnswerModelList.size() + " Answer(s)");
    }

    @Override
    public void onBackPressed() {
        closeForum(refreshAnswers);
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
                closeForum(refreshAnswers);
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
    public void onRefresh() {
        if (isNetworkConnectionAvailable(context, -1)) {
//            refreshMyLearning(true);

            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult first:");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWFORUM", false);
                if (refresh) {
//                    refreshMyLearning(false);
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
//                attachFragment(askExpertAnswerModelList.get(position));
//                PopupMenu popup = new PopupMenu(this, v);
                break;
            case R.id.btn_contextmenu:
                View v = askExpertsListView.getChildAt(position - askExpertsListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, askExpertAnswerModelList.get(position));
                break;
            case R.id.txtTotalUpvoters:
                showUpvoters();
                break;
            case R.id.txtComment:
                openCommentAndAnswerActivity(false);
                break;
            case R.id.txtComments:
                openCommentsActivity(false);
            default:

        }

    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final AskExpertAnswerModel askExpertAnswerModel) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);

        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.askexpertmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);//delete

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false).setTitle("Confirmation").setMessage("Are you sure you want to permanently delete the answer :")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    deleteAnswerFromServer(askExpertAnswerModel);
                                }
                            }).setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    dialogBox.cancel();
                                }
                            });

                    AlertDialog alertDialogAndroid = alertDialog.create();
                    alertDialogAndroid.show();
                }
                return true;
            }
        });
        popup.show();//showing popup menu

    }

    public void deleteAnswerFromServer(final AskExpertAnswerModel answerModel) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteAskResponse?ResponseID=" + answerModel.responseid;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, " Success! \nAnswer has been successfully deleted ", Toast.LENGTH_SHORT).show();
                    refreshAnswers = true;
                    deleteAnswerFromLocalDB(answerModel);
                } else {

                    Toast.makeText(context, "Answer cannot be deleted . Contact site admin.", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();
                svProgressHUD.dismiss();
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void deleteAnswerFromLocalDB(AskExpertAnswerModel askExpertAnswerModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_ASKRESPONSES + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND responseid  ='" + askExpertAnswerModel.responseid + "'";
            db.executeQuery(strDelete);

            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }


    public void insertSingleAsktheExpertAnswerDataIntoSqLite(AskExpertAnswerModel askExpertAnswerModel, int answerCount) {

        String insertStr = "INSERT INTO ASKRESPONSES (questionid, responseid, response, respondeduserid, respondedusername, respondeddate, responsedate, siteid ) VALUES (" +
                "" + askExpertAnswerModel.questionID +
                "," + askExpertAnswerModel.responseid +
                ",'" + askExpertAnswerModel.response +
                "','" + askExpertAnswerModel.respondeduserid +
                "','" + askExpertAnswerModel.respondedusername +
                "','" + askExpertAnswerModel.respondeddate +
                "','" + askExpertAnswerModel.responsedate +
                "'," + askExpertAnswerModel.siteID +
                ")";

        try {
            db.executeQuery(insertStr);

            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void closeForum(boolean refresh) {
        Intent intent = getIntent();
        intent.putExtra("NEWQS", refreshAnswers);
        setResult(RESULT_OK, intent);
        finish();
    }


    public List<ContentValues> generateTagsList(int position) {
        List<ContentValues> tagsList = new ArrayList<>();

        for (int i = 1; i < position; i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", i);
            cvBreadcrumbItem.put("categoryname", "Skill " + i);
            tagsList.add(cvBreadcrumbItem);

        }


        return tagsList;
    }


    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems, CustomFlowLayout category_breadcrumb, final Context context) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        category_breadcrumb.removeAllViews();
        int breadcrumbCount = dicBreadcrumbItems.size();
        View.OnClickListener onBreadcrumbItemCLick = null;
        onBreadcrumbItemCLick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String categoryId = tv.getTag(R.id.CATALOG_CATEGORY_ID_TAG)
                        .toString();
                int categoryLevel = Integer.valueOf(tv.getTag(
                        R.id.CATALOG_CATEGORY_LEVEL_TAG).toString());

                String categoryName = tv.getText().toString();


            }
        };

        for (int i = 0; i < breadcrumbCount; i++) {
            if (i == 0) {
                isFirstCategory = true;
            } else {
                isFirstCategory = false;
            }

            TextView textView = new TextView(context);
            TextView arrowView = new TextView(context);

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(arrowView, iconFont);

            arrowView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><medium><b>"
                    + context.getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

            arrowView.setTextSize(12);

            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            arrowView.setVisibility(View.GONE);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

//            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><big><b>"
//                    + categoryName + "</b></small>  </font>"));

            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><small>"
                    + categoryName + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                category_breadcrumb.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            category_breadcrumb.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    private void showUpvoters() {
        View view = getLayoutInflater().inflate(R.layout.askexpertsupvotersbottomsheet, null);
        ListView lv_languages = (ListView) view.findViewById(R.id.lv_languages);

        lv_languages.setAdapter(upvotersAdapter);
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }


}

