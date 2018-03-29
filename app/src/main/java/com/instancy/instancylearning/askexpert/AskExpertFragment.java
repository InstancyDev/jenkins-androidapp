package com.instancy.instancylearning.askexpert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

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
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;

import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.ResultListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;

import com.instancy.instancylearning.models.AskExpertSkillsModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.databaseutils.DatabaseHandler.TBL_ASKQUESTIONS;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.FORUM_CREATE_NEW_FORUM;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


/**
 * Created by Upendranath on 5/19/2017.
 */

@TargetApi(Build.VERSION_CODES.N)
public class AskExpertFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, View.OnClickListener {

    String TAG = AskExpertFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    @BindView(R.id.swipemylearning)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.discussionfourmlist)
    ListView askexpertListView;

    @BindView(R.id.askexpertMenu)
    FloatingActionMenu askexpertMenu;

    @BindView(R.id.fabFilter)
    FloatingActionButton fabFilter;

    @BindView(R.id.fabAsk)
    FloatingActionButton fabAskeQuestion;

    AskExpertAdapter askExpertAdapter;
    List<AskExpertQuestionModel> askExpertQuestionModelList = null;

    PreferencesManager preferencesManager;
    Context context;
    Toolbar toolbar;
    Menu search_menu;
    MenuItem item_search;
    SideMenusModel sideMenusModel = null;
    ResultListner resultListner = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;


    boolean isFromNotification = false;

    String contentIDFromNotification = "";

    List<AskExpertSkillsModel> askExpertSkillsModelList = null;

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());

    public AskExpertFragment() {


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        initVolleyCallback();
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        vollyService = new VollyService(resultCallback, context);

        sideMenusModel = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            sideMenusModel = (SideMenusModel) bundle.getSerializable("sidemenumodel");

            isFromNotification = bundle.getBoolean("ISFROMNOTIFICATIONS");

            if (isFromNotification) {

                contentIDFromNotification = bundle.getString("CONTENTID");
            }


        }
    }

    public void refreshCatalog(Boolean isRefreshed) {
        if (!isRefreshed) {
            svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));
        }

        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GetAskQandA?userid=" + appUserModel.getUserIDValue() + "&SiteID=" + appUserModel.getSiteIDValue() + "&QuestionID=0&QuestionTypeID=1&SkillID=-1&RecordCount=0&SearchText=";

        vollyService.getJsonObjResponseVolley("ASKQS", parmStringUrl, appUserModel.getAuthHeaders());

    }

    public void getSkillsCalatalogFrom() {


        String parmStringUrl = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserQuestionSkills?SiteID=" + appUserModel.getSiteIDValue() + "&Type=selected";

        vollyService.getJsonObjResponseVolley("ASKQSCAT", parmStringUrl, appUserModel.getAuthHeaders());

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("ASKQS")) {
                    if (response != null) {
                        try {
                            db.injectAsktheExpertQuestionDataTable(response);
                            db.injectAsktheExpertAnswersDataTable(response);
                            injectFromDbtoModel();
                            getSkillsCalatalogFrom();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                if (requestType.equalsIgnoreCase("ASKQSCAT")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: ASKQSCAT  " + response);
                        try {
                            db.injectAsktheExpertCategoryDataTable(response);
                            askExpertSkillsModelList = db.fetchAskExpertSkillsModelList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {

                svProgressHUD.dismiss();
            }
        };
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discussionfourm_fragment, container, false);

        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setOnRefreshListener(this);

        askExpertAdapter = new AskExpertAdapter(getActivity(), BIND_ABOVE_CLIENT, askExpertQuestionModelList);
        askexpertListView.setAdapter(askExpertAdapter);
        askexpertListView.setOnItemClickListener(this);
        askexpertListView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        askExpertQuestionModelList = new ArrayList<AskExpertQuestionModel>();
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(false);
        } else {
            injectFromDbtoModel();
        }

        initilizeView();
        fabActionMenusInitilization();


        return rootView;
    }

    public void injectFromDbtoModel() {
        askExpertQuestionModelList = db.fetchAskExpertModelList("");
        if (askExpertQuestionModelList != null) {
            askExpertAdapter.refreshList(askExpertQuestionModelList);
        } else {
            askExpertQuestionModelList = new ArrayList<AskExpertQuestionModel>();
            askExpertAdapter.refreshList(askExpertQuestionModelList);
        }

        if (askExpertQuestionModelList.size() > 5) {
            if (item_search != null)
                item_search.setVisible(true);
        } else {
            if (item_search != null)
                item_search.setVisible(false);
        }

        triggerActionForFirstItem();
    }

    public void triggerActionForFirstItem() {

        if (isFromNotification) {
            int selectedPostion = getPositionForNotification(contentIDFromNotification);
            askexpertListView.setSelection(selectedPostion);

            if (askExpertQuestionModelList != null) {

                try {
                    attachFragment(askExpertQuestionModelList.get(selectedPostion));
                    isFromNotification = false;
                } catch (IndexOutOfBoundsException ex) {
//                        Toast.makeText(context, "No Content Avaliable", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, "No Content Avaliable", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public int getPositionForNotification(String contentID) {
        int position = 0;
        int contentIntID = 0;
        try {
            contentIntID = Integer.parseInt(contentID);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

        for (int k = 0; k < askExpertQuestionModelList.size(); k++) {
            if (askExpertQuestionModelList.get(k).questionID == contentIntID) {
                position = k;
                break;
            }

        }

        return position;
    }


    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setHasOptionsMenu(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + sideMenusModel.getDisplayName() + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    public void fabActionMenusInitilization() {
        askexpertMenu.setVisibility(View.VISIBLE);
        askexpertMenu.setClosedOnTouchOutside(true);
        askexpertMenu.setMenuButtonColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        fabAskeQuestion.setOnClickListener(this);
        fabFilter.setOnClickListener(this);

        fabAskeQuestion.setImageDrawable(getDrawableFromString(R.string.fa_icon_question));
        fabFilter.setImageDrawable(getDrawableFromString(R.string.fa_icon_filter));

        fabAskeQuestion.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        fabFilter.setColorNormal(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

        fabAskeQuestion.setLabelText(getResources().getString(R.string.askaquestion));
        fabFilter.setLabelText(getResources().getString(R.string.filter));

    }

    public Drawable getDrawableFromString(int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));

        return d;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);
        item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);

        itemInfo.setVisible(false);
        item_filter.setVisible(false);

        if (item_search != null) {
            Drawable myIcon = getResources().getDrawable(R.drawable.search);
            item_search.setIcon(setTintDrawable(myIcon, Color.parseColor(uiSettingsModel.getMenuHeaderTextColor())));
//            tintMenuIcon(getActivity(), item_search, R.color.colorWhite);
            item_search.setTitle("Search");
            final SearchView searchView = (SearchView) item_search.getActionView();
//            searchView.setBackgroundColor(Color.WHITE);
            EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
            txtSearch.setHint("Search..");
            txtSearch.setHintTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));
            txtSearch.setTextColor(Color.parseColor(uiSettingsModel.getMenuHeaderTextColor()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    askExpertAdapter.filter(newText.toLowerCase(Locale.getDefault()));

                    return true;
                }

            });

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mylearning_search:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    circleReveal(R.id.toolbar, 1, true, true);
                else
                    toolbar.setVisibility(View.VISIBLE);
                item_search.expandActionView();
                break;
            case R.id.mylearning_info_help:

                break;
            case R.id.mylearning_filter:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshCatalog(true);
            MenuItemCompat.collapseActionView(item_search);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (view.getId()) {
            case R.id.card_view:
                attachFragment(askExpertQuestionModelList.get(position));
                break;
            case R.id.btn_contextmenu:
                View v = askexpertListView.getChildAt(position - askexpertListView.getFirstVisiblePosition());
                ImageButton txtBtnDownload = (ImageButton) v.findViewById(R.id.btn_contextmenu);
                catalogContextMenuMethod(position, view, txtBtnDownload, askExpertQuestionModelList.get(position));
                break;
            default:

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = getActivity().findViewById(viewID);
        int width = myView.getWidth();
        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);
        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);
        anim.setDuration((long) 400);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });
        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }

    public void catalogContextMenuMethod(final int position, final View v, ImageButton btnselected, final AskExpertQuestionModel askExpertQuestionModel) {

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
                    alertDialog.setCancelable(false).setTitle("Confirmation").setMessage("Are you sure you want to permanently delete the question :")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialogBox, int id) {
                                    // ToDo get user input here
                                    deleteQuestionFromServer(askExpertQuestionModel);
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

    public void deleteQuestionFromServer(final AskExpertQuestionModel questionModel) {

//        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/DeleteAskQuestion?QuestionID=" + questionModel.questionID;

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);

                if (s.contains("success")) {

                    Toast.makeText(context, " Success! \nQuestion has been successfully deleted from server. ", Toast.LENGTH_SHORT).show();

                    deleteQuestionFromLocalDB(questionModel);
                } else {

                    Toast.makeText(context, "Question cannot be deleted to server. Contact site admin.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FORUM_CREATE_NEW_FORUM && resultCode == RESULT_OK && data != null) {

            if (data != null) {
                boolean refresh = data.getBooleanExtra("NEWQS", false);
                if (refresh) {

                    if (isNetworkConnectionAvailable(getContext(), -1)) {
                        refreshCatalog(true);
                    } else {
                        injectFromDbtoModel();
                    }


                }
            }
        }
    }

    public void attachFragment(AskExpertQuestionModel askExpertQuestionModel) {

        List<AskExpertAnswerModel> askExpertAnswerModelList = new ArrayList<AskExpertAnswerModel>();

        Intent intentDetail = new Intent(context, AskExpertsAnswersActivity.class);
        intentDetail.putExtra("AskExpertQuestionModel", askExpertQuestionModel);

        startActivity(intentDetail);

    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    public void deleteQuestionFromLocalDB(AskExpertQuestionModel askExpertQuestionModel) {

        try {
            String strDelete = "DELETE FROM " + TBL_ASKQUESTIONS + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND questionid ='" + askExpertQuestionModel.questionID + "' AND userid  ='" + askExpertQuestionModel.userID + "'";
            db.executeQuery(strDelete);
            injectFromDbtoModel();

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fabFilter:
                filterPopUp();
                break;
            case R.id.fabAsk:
                Intent intentDetail = new Intent(context, AskQuestionActivity.class);
                startActivityForResult(intentDetail, FORUM_CREATE_NEW_FORUM);
                break;

        }
    }


    public void filterPopUp() {

        if (askExpertSkillsModelList != null) {
            final String[] strSplitvalues = new String[askExpertSkillsModelList.size()];

            for (int s = 0; s < askExpertSkillsModelList.size(); s++) {
                strSplitvalues[s] = askExpertSkillsModelList.get(s).shortSkillName;

            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            //set the title for alert dialog
            builder.setTitle("Choose a skill in order to filter the above questions");

            //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
            builder.setItems(strSplitvalues, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {
                    //setting the button text to the selected itenm from the list

//                    Toast.makeText(context, " Selected" + strSplitvalues[item], Toast.LENGTH_SHORT).show();

                }
            });

            //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
            builder.setCancelable(false)
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //When clicked on CANCEL button the dalog will be dismissed
                            dialog.dismiss();


                        }
                    });

            //Creating alert dialog
            AlertDialog alert = builder.create();

            //Showingalert dialog
            alert.show();


        } else {
            Toast.makeText(context, " Filters not configured ", Toast.LENGTH_SHORT).show();
        }

    }


}