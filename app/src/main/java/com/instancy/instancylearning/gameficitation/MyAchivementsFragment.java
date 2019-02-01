package com.instancy.instancylearning.gameficitation;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.Ach_UserBadges;
import com.instancy.instancylearning.models.Ach_UserLevel;
import com.instancy.instancylearning.models.Ach_UserOverAllData;
import com.instancy.instancylearning.models.Ach_UserPoints;
import com.instancy.instancylearning.models.AchievementModel;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GamificationModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.OtherGameModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.BIND_ABOVE_CLIENT;
import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;


/**
 * Created by Upendranath on 5/19/2017.
 */

public class MyAchivementsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = MyAchivementsFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    @Nullable
    @BindView(R.id.swipeachivments)
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseHandler db;

    @BindView(R.id.achivmentexpandlist)
    ExpandableListView achExpandList;

    @BindView(R.id.otherGamesListView)
    ListView otherGamesListView;

    OtherGamesAdapter otherGamesAdapter;

    List<OtherGameModel> otherGameModelList = null;

    @Nullable
    @BindView(R.id.spnrGame)
    Spinner spnrGame;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    @Nullable
    @BindView(R.id.card_view)
    CardView cardView;

    @Nullable
    @BindView(R.id.achivmentheader)
    RelativeLayout achiveheader;

    @Nullable
    @BindView(R.id.otherGamesHeader)
    RelativeLayout otherGamesHeader;

    @Nullable
    @BindView(R.id.txtCredits)
    TextView txtCredits;

    @Nullable
    @BindView(R.id.txtCertificate)
    TextView txtCertificate;

    @Nullable
    @BindView(R.id.lbCredits)
    TextView lbCredits;

    @Nullable
    @BindView(R.id.lbCertificate)
    TextView lbCertificate;

    @Nullable
    @BindView(R.id.imageachived)
    ImageView imageAchived;

    @Nullable
    @BindView(R.id.txt_name)
    TextView txtName;

    @Nullable
    @BindView(R.id.txtpointsawarded)
    TextView txtPointsawarded;

    @Nullable
    @BindView(R.id.txtPoints)
    TextView txtPoints;

    @Nullable
    @BindView(R.id.txtLevel)
    TextView txtLevel;

    @Nullable
    @BindView(R.id.txtBadges)
    TextView txtBadges;

    @Nullable
    @BindView(R.id.groupname)
    TextView groupName;

    String gameId = "1";

    @Nullable
    @BindView(R.id.achivement_progress_bar)
    ProgressBar progressBar;


    AchiviExpandAdapter achiviExpandAdapter;

    List<Ach_UserPoints> achUserPointsList = null;
    List<Ach_UserBadges> achUserBadgesList = null;
    List<Ach_UserLevel> achUserLevelList = null;
    Ach_UserOverAllData achUserOverAllData = null;
    AchievementModel achievementModel = null;

    List<GamificationModel> gamificationModelList = new ArrayList<>();

    PreferencesManager preferencesManager;
    Context context;
    SideMenusModel sideMenusModel = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    public MyAchivementsFragment() {


    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, getActivity());
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
        }

    }

    public void refreshGameList(Boolean isRefreshed) {
        if (!isRefreshed) {
//            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));
        }
//        String urlStr = appUserModel.getWebAPIUrl() + "/Mobilelms/GetGameList";
        String urlStr = appUserModel.getWebAPIUrl() + "/LeaderBoard/GetGameList";

        JSONObject parameters = new JSONObject();

        //mandatory

        try {

            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("LeaderByGroup", "");
            parameters.put("GameID", -1);
            parameters.put("fromAchievement", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();


        vollyService.getStringResponseFromPostMethod(parameterString, "GAMESLIST", urlStr);

    }

    public void getAchivmentsByGameIDForOther(String gameID) {


        String urlStr = appUserModel.getWebAPIUrl() + "MyAchievementr/MyCreditCertificate?UserID=" + appUserModel.getUserIDValue();

        vollyService.getJsonObjResponseVolley("ACHIVBOARDOTHER", urlStr, appUserModel.getAuthHeaders());

    }

    public void getAchivmentsByGameID(String gameID) {


        String urlStr = appUserModel.getWebAPIUrl() + "/UserAchievement/GetUserAchievementData";

        JSONObject parameters = new JSONObject();

//,,,jsonobject = Optional(["GameID": 1, "UserID": "1", "ComponentID": 293, "SiteID": "374", "Locale": preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)), "ComponentInsID": 4202])

        try {
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("GameID", gameID);
            parameters.put("UserID", appUserModel.getUserIDValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();
        Log.d(TAG, "getAchivmentsByGameID: " + parameters);

        vollyService.getStringResponseFromPostMethod(parameterString, "ACHIVBOARD", urlStr);

    }


    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);

                if (requestType.equalsIgnoreCase("ACHIVBOARDOTHER")) {

                    if (response != null && response.length() > 0) {

                        Log.d(TAG, requestType + " : " + response);
                        try {
                            otherGamesListView.setVisibility(View.VISIBLE);
                            achExpandList.setVisibility(View.GONE);
                            achiveheader.setVisibility(View.GONE);
                            otherGamesHeader.setVisibility(View.VISIBLE);
                            otherGamesAdapter.refreshList(generateOtherModellist(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.there_are_no_games_available_at_this_time));
            }

            @Override
            public void notifySuccess(String requestType, String response) {

                if (requestType.equalsIgnoreCase("GAMESLIST")) {
                    Log.d(TAG, requestType + " : " + response);
                    if (response != null && response.length() > 0) {


                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() > 0) {
                                db.injectGameslist(jsonArray);
                                JSONObject object = jsonArray.getJSONObject(0);
                                gameId = object.getString("GameID");
//                                getAchivmentsByGameID(gameId);
                                if (gameId.equalsIgnoreCase("-1"))
                                    getAchivmentsByGameIDForOther(gameId);
                                else
                                    getAchivmentsByGameID(gameId);

                                updateGameSpinner();
                            } else {
                                svProgressHUD.dismiss();

                                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.there_are_no_games_available_at_this_time));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }

                if (requestType.equalsIgnoreCase("ACHIVBOARD")) {

                    if (response != null && response.length() > 0) {

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            db.injectAchivmentData(jsonObj);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    Log.d(TAG, requestType + " : " + response);


                }

                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();

            }

            @Override
            public void notifySuccessLearningModel(String requestType, JSONObject response, MyLearningModel myLearningModel) {
                svProgressHUD.dismiss();
            }
        };
    }

    public List<OtherGameModel> generateOtherModellist(JSONObject response) throws JSONException {
        List<OtherGameModel> otherGameModellocList = new ArrayList<>();
        otherGameModelList = new ArrayList<>();

        JSONArray jsonArray = response.getJSONArray("Table");
        int certificateCount = 0;
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject columnObj = jsonArray.getJSONObject(j);

                OtherGameModel otherGameModel = new OtherGameModel();
                otherGameModel.contentID = columnObj.optString("ContentID");
                otherGameModel.name = columnObj.optString("Name");
                otherGameModel.decimal2 = columnObj.optDouble("Decimal2",0);
                otherGameModel.certificateID = columnObj.optString("CertificateID");
                otherGameModel.coreLessonStatus = columnObj.optString("CoreLessonStatus");
                otherGameModel.scoreRaw = columnObj.optString("ScoreRaw");
                otherGameModel.certificatePercentage = columnObj.optString("CertificatePercentage");
                otherGameModel.certificatePage = columnObj.optString("CertificatePage");
                otherGameModel.certifyviewlink = columnObj.optString("certifyviewlink");
                otherGameModel.certifycountwebapilevel = columnObj.optInt("certifycountwebapilevel");
                otherGameModellocList.add(otherGameModel);
                otherGameModelList.add(otherGameModel);
                certificateCount = certificateCount + otherGameModel.certifycountwebapilevel;
            }

        }

        JSONArray jsonArray1 = response.getJSONArray("Table1");

        JSONArray jsonArray2 = response.getJSONArray("Table2");

        String creditScore = "0", certificates = "0";
//[{"creditcount":58,"certificatecount":3}]
        if (jsonArray1 != null && jsonArray1.length() > 0) {
            JSONObject jsonObject = jsonArray1.getJSONObject(0);

            certificates = jsonObject.optString("certificatecount");
        }

        if (jsonArray2 != null && jsonArray2.length() > 0) {
            JSONObject jsonObject = jsonArray2.getJSONObject(0);
            creditScore = jsonObject.optString("creditsum");

        }

//        updateOtherGameBoard(creditScore, certificates); commented for not matching web level certificate count
        updateOtherGameBoard(creditScore, "" + certificateCount);

        return otherGameModellocList;
    }

    public void updateGameSpinner() {

        final ArrayList<String> gamesList = db.fetchGames();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, gamesList);
        spnrGame.setAdapter(spinnerAdapter);
        spnrGame.setSelection(0, true);
        spnrGame.setVisibility(View.VISIBLE);
        View v = spnrGame.getSelectedView();
        ((TextView) v).setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        spnrGame.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppTextColor())));

        spnrGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spnrPosition,
                                       long id) {

                Log.d(TAG, "onItemSelected: gamesList " + gamesList.get(spnrPosition));

                gameId = db.getGamesID(gamesList.get(spnrPosition));

                if (isNetworkConnectionAvailable(getContext(), -1)) {
                    if (gameId.equalsIgnoreCase("-1"))
                        getAchivmentsByGameIDForOther(gameId);
                    else
                        getAchivmentsByGameID(gameId);
                } else {
                    injectFromDbtoModel();
//                    Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.achivment_fragment, container, false);

        ButterKnife.bind(this, rootView);

        progressBar.setScaleY(2f);

        swipeRefreshLayout.setOnRefreshListener(this);

        achievementModel = new AchievementModel();
        achUserOverAllData = new Ach_UserOverAllData();
        achUserBadgesList = new ArrayList<>();
        achUserLevelList = new ArrayList<>();
        achUserPointsList = new ArrayList<>();

        achiviExpandAdapter = new AchiviExpandAdapter(getActivity(), achUserBadgesList, achUserLevelList, gamificationModelList, achUserPointsList);

        achExpandList.setAdapter(achiviExpandAdapter);
        achExpandList.setEmptyView(rootView.findViewById(R.id.nodata_label));

        achExpandList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Doing nothing
                return true;
            }
        });

        swipeRefreshLayout.setEnabled(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshGameList(false);
        } else {
//            injectFromDbtoModel();
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.there_are_no_games_available_at_this_time));
        }

        initilizeView();

        initilizeOtherGameView();

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconforum, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        return rootView;
    }

    public void injectFromDbtoModel() {
        achiveheader.setVisibility(View.VISIBLE);
        achievementModel = new AchievementModel();
        achUserOverAllData = new Ach_UserOverAllData();
        achUserBadgesList = new ArrayList<>();
        achUserLevelList = new ArrayList<>();
        achUserPointsList = new ArrayList<>();

        achievementModel = db.fetchAchivmentData();

        achUserOverAllData = db.fetchUserAllOverData(appUserModel.getSiteIDValue(), appUserModel.getUserIDValue(), gameId);

        gamificationModelList = new ArrayList<>();

        updateLeaderBoard(achUserOverAllData);

        if (achievementModel.userBadges.equalsIgnoreCase("true")) {
            achUserBadgesList = db.fetchUserAchivBadges(gameId);
        }

        if (achievementModel.userLevel.equalsIgnoreCase("true")) {
            achUserPointsList = db.fetchUserAchivPoints(gameId);
        }

        if (achievementModel.userPoints.equalsIgnoreCase("true")) {
            achUserLevelList = db.fetchUserAchivLevels(gameId);
        }

        if (achUserPointsList.size() > 0) {
            GamificationModel gamificationModel = new GamificationModel();
            gamificationModel.groupId = "1";
            gamificationModel.groupname = "Points";
            gamificationModelList.add(gamificationModel);
        }

        if (achUserLevelList.size() > 0) {
            GamificationModel gamificationModel = new GamificationModel();
            gamificationModel.groupId = "2";
            gamificationModel.groupname = "Level";
            gamificationModelList.add(gamificationModel);
        }

        if (achUserBadgesList.size() > 0) {

            GamificationModel gamificationModel = new GamificationModel();
            gamificationModel.groupId = "3";
            gamificationModel.groupname = "Badges";
            gamificationModelList.add(gamificationModel);
        }

        achiviExpandAdapter.refreshList(achUserBadgesList, achUserLevelList, gamificationModelList, achUserPointsList);

        if (gamificationModelList != null && gamificationModelList.size() > 0) {
            for (int i = 0; i < gamificationModelList.size(); i++)
                achExpandList.expandGroup(i);
        }

        if (gameId.equalsIgnoreCase("-1")) {
            otherGamesListView.setVisibility(View.VISIBLE);
            achExpandList.setVisibility(View.GONE);
            achiveheader.setVisibility(View.GONE);
            otherGamesHeader.setVisibility(View.VISIBLE);
        } else {
            otherGamesListView.setVisibility(View.GONE);
            achExpandList.setVisibility(View.VISIBLE);
            achiveheader.setVisibility(View.VISIBLE);
            otherGamesHeader.setVisibility(View.GONE);
        }
    }

    public void updateOtherGameBoard(String credits, String certificates) {
        txtCredits.setText(credits);
        txtCertificate.setText(certificates);
    }

    public void updateLeaderBoard(Ach_UserOverAllData achUserOverAllData1) {

        txtName.setText(achUserOverAllData1.userDisplayName);
        txtBadges.setText("" + achUserOverAllData1.badges);
        txtPoints.setText("" + achUserOverAllData1.overAllPoints);
        txtLevel.setText(achUserOverAllData1.userLevel);
        String imgUrl = appUserModel.getSiteURL() + achUserOverAllData1.userProfilePath;
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.user_placeholder).into(imageAchived);

        if (achUserOverAllData1.neededPoints > 0 && isValidString(achUserOverAllData1.neededLevel)) {
            txtPointsawarded.setText(achUserOverAllData1.neededPoints + " " + getLocalizationValue(JsonLocalekeys.filter_label_points) + " " + achUserOverAllData1.neededLevel + " " + getLocalizationValue(JsonLocalekeys.filter_label_level));

        } else {
            txtPointsawarded.setText("");
        }

        //   Math.round(((this.UserAchieveDashBoardData.OverAllPoints) / (this.UserAchieveDashBoardData.OverAllPoints + this.UserAchieveDashBoardData.NeededPoints)) * 100)


        int progressValue = (achUserOverAllData1.overAllPoints / (achUserOverAllData1.overAllPoints + achUserOverAllData1.neededPoints)) * 100;

        progressBar.setProgress(progressValue);

        //color update
        txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtPointsawarded.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtBadges.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtLevel.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtPoints.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        cardView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
    }

    public void initilizeOtherGameView() {

        otherGamesAdapter = new OtherGamesAdapter(context, BIND_ABOVE_CLIENT, otherGameModelList);
        otherGamesListView.setAdapter(otherGamesAdapter);
        otherGamesListView.setOnItemClickListener(this);
        otherGamesListView.setVisibility(View.GONE);

        lbCertificate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        lbCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtCertificate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        groupName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        groupName.setText(getLocalizationValue(JsonLocalekeys.gamemifaction_othergames_header));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mylearning_menu, menu);

        MenuItem item_search = menu.findItem(R.id.mylearning_search);
        MenuItem item_filter = menu.findItem(R.id.mylearning_filter);
        MenuItem itemInfo = menu.findItem(R.id.mylearning_info_help);

        itemInfo.setVisible(false);
        item_filter.setVisible(false);
        item_search.setVisible(false);

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

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshGameList(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: in Mylearning fragment");

    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intentSocial = new Intent(context, SocialWebLoginsActivity.class);
        switch (view.getId()) {
            case R.id.txtCertificate:
                OtherGameModel otherGameModel = otherGameModelList.get(i);
                Log.d(TAG, "onItemClick: " + otherGameModel);
                String urlForCertificate = appUserModel.getSiteURL() + "content/sitefiles/" + appUserModel.getSiteIDValue() + "/UserCertificates/" + appUserModel.getUserIDValue() + "/" + otherGameModelList.get(i).certificateID + "/Certificate.pdf";

                if (isValidString(otherGameModelList.get(i).certificateID)) {
//                    intentSocial.putExtra("ATTACHMENT", true);
//                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, urlForCertificate);
//                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, otherGameModelList.get(i).certificatePage);
//                    startActivity(intentSocial);
                    MyLearningModel myLearningModel = new MyLearningModel();
                    Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                    pdfIntent.putExtra("PDF_URL", urlForCertificate);
                    pdfIntent.putExtra("ISONLINE", "YES");
                    pdfIntent.putExtra("myLearningDetalData", myLearningModel);
                    pdfIntent.putExtra("PDF_FILENAME", otherGameModelList.get(i).certificatePage);
                    startActivity(pdfIntent);
                }

                Log.d(TAG, "onItemClick: " + urlForCertificate);
                break;
        }
    }
}