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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.LeaderboardList;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.CompetencyCatSkillActivity;
import com.instancy.instancylearning.mycompetency.CompetencyJobRoleAdapter;
import com.instancy.instancylearning.mycompetency.CompetencyJobRoles;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

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


/**
 * Created by Upendranath on 5/19/2017.
 */

public class LeaderboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    String TAG = LeaderboardFragment.class.getSimpleName();
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    SVProgressHUD svProgressHUD;

    String gameId = "1";

    @BindView(R.id.swipeachivments)
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseHandler db;
    @BindView(R.id.leaderboardlist)
    ListView leaderBoardlistView;

    @BindView(R.id.nodata_label)
    TextView nodata_Label;

    @BindView(R.id.leadertheader)
    RelativeLayout leadertHeader;

    @Nullable
    @BindView(R.id.spnrGame)
    Spinner spnrGame;

    LeaderBoardAdapter leaderBoardAdapter;
    List<LeaderboardList> leaderboardListList = null;
    PreferencesManager preferencesManager;
    Context context;
    SideMenusModel sideMenusModel = null;
    AppController appcontroller;
    UiSettingsModel uiSettingsModel;

    public LeaderboardFragment() {
    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,getActivity());
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

        JSONObject parameters = new JSONObject();

        String urlStr = appUserModel.getWebAPIUrl() + "/LeaderBoard/GetGameList";

        try {
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("UserID", appUserModel.getUserIDValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();


        vollyService.getStringResponseFromPostMethod(parameterString, "GAMESLIST", urlStr);

    }

    public void getLeaderBoardOnGameID(String gameID) {

        String urlStr = appUserModel.getWebAPIUrl() + "/LeaderBoard/GetLeaderboardData";

        JSONObject parameters = new JSONObject();

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

        vollyService.getStringResponseFromPostMethod(parameterString, "LEADERBOARD", urlStr);
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                svProgressHUD.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work!");
                swipeRefreshLayout.setRefreshing(false);
                svProgressHUD.dismiss();
                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nogamesavailablelabel));
            }

            @Override
            public void notifySuccess(String requestType, String response) {
                Log.d(TAG, "Volley String post" + response);
                svProgressHUD.dismiss();
                if (requestType.equalsIgnoreCase("GAMESLIST")) {
                    Log.d(TAG, requestType + " : " + response);
//                    [{"GameID":1,"GameName":"Ergonomics Challenge"}]


                    if (response != null && response.length() > 0) {


                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            if (jsonArray.length() > 0) {
                                db.injectGameslist(jsonArray);
                                JSONObject object = jsonArray.getJSONObject(0);
                                gameId = object.getString("GameID");
                                getLeaderBoardOnGameID(gameId);
                                updateGameSpinner();
                            } else {
                                svProgressHUD.dismiss();
                                nodata_Label.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nogamesavailablelabel));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        nodata_Label.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nogamesavailablelabel));
                    }

                }

                if (requestType.equalsIgnoreCase("LEADERBOARD")) {

                    Log.d(TAG, requestType + " : " + response);

                    if (response != null && response.length() > 0) {

                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            db.injectLeaderboardList(jsonObj, gameId);
                            injectFromDbtoModel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
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

    public void updateGameSpinner() {
        spnrGame.setVisibility(View.VISIBLE);
        leadertHeader.setVisibility(View.VISIBLE);
        final ArrayList<String> gamesList = db.fetchGames();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, gamesList);
        spnrGame.setAdapter(spinnerAdapter);
        spnrGame.setSelection(0, true);
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
                    getLeaderBoardOnGameID(gameId);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leader_fragment, container, false);

        ButterKnife.bind(this, rootView);


        swipeRefreshLayout.setOnRefreshListener(this);
        leaderboardListList = new ArrayList<LeaderboardList>();
        leaderBoardAdapter = new LeaderBoardAdapter(getActivity(), BIND_ABOVE_CLIENT, leaderboardListList);
        leaderBoardlistView.setAdapter(leaderBoardAdapter);
        leaderBoardlistView.setOnItemClickListener(this);
        leaderBoardlistView.setEmptyView(rootView.findViewById(R.id.nodata_label));

        if (isNetworkConnectionAvailable(getContext(), -1)) {
            refreshGameList(false);
        } else {
            injectFromDbtoModel();
        }

        initilizeView();

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconforum, null);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.homeicon), iconFont);
        Drawable d = new BitmapDrawable(getResources(), createBitmapFromView(context, customNav));
        d.setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppHeaderTextColor())));

        return rootView;
    }

    public void injectFromDbtoModel() {

        leaderboardListList = db.fetchLeaderBoardList(gameId);

        if (leaderboardListList != null && leaderboardListList.size() > 0) {
            leaderBoardAdapter.refreshList(leaderboardListList);

        } else {
            leaderboardListList = new ArrayList<LeaderboardList>();
            leaderBoardAdapter.refreshList(leaderboardListList);
            nodata_Label.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nogamesavailablelabel));
        }
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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

}