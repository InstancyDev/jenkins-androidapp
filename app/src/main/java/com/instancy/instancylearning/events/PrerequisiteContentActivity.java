package com.instancy.instancylearning.events;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.Gson;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningDetailActivity1;
import com.instancy.instancylearning.mylearning.MyLearningScheduleChildModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CATALOG_CODE;
import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormat;
import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormatCreatedOn;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;
import static com.instancy.instancylearning.utils.Utilities.toCSVString;


public class PrerequisiteContentActivity extends AppCompatActivity implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match

    public LinkedHashMap<String, List<PrerequisiteModel>> prerequisiteExpandableList;

    private PrerequisiteExpandableAdapter prerequisiteExpandableAdapter;

    private ExpandableListView schedule_expandablelistview;

    UiSettingsModel uiSettingsModel;
    MyLearningModel myLearningModel;
    PreferencesManager preferencesManager;
    AppUserModel appUserModel;
    VollyService vollyService;
    IResult resultCallback = null;
    String TAG = PrerequisiteContentActivity.class.getSimpleName();
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    SideMenusModel sideMenusModel;
    TextView noDataLabel;

    Button btnAdd, btnCancal;
    private int position;
    private View v;
    private ImageButton btnselected;

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, PrerequisiteContentActivity.this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prerequisiteactivity);
        preferencesManager = PreferencesManager.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(this);
        if (getIntent().getExtras() != null) {
            myLearningModel = (MyLearningModel) getIntent().getExtras().getSerializable("myLearningDetalData");

            sideMenusModel = (SideMenusModel) getIntent().getExtras().getSerializable("sideMenusModel");
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'> " + getLocalizationValue(JsonLocalekeys.prerequis_headerlabel_headertitle) + " </font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);
        schedule_expandablelistview = findViewById(R.id.schedule_expandablelistview);
        prerequisiteExpandableList = new LinkedHashMap<>();
        prerequisiteExpandableAdapter = new PrerequisiteExpandableAdapter(PrerequisiteContentActivity.this, prerequisiteExpandableList, myLearningModel, schedule_expandablelistview);
        schedule_expandablelistview.setAdapter(prerequisiteExpandableAdapter);
        noDataLabel = findViewById(R.id.noDataLabel);
        noDataLabel.setText("");
        // schedule_expandablelistview.setEmptyView(noDataLabel);

        schedule_expandablelistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                final PrerequisiteModel prerequisiteModel = (PrerequisiteModel) prerequisiteExpandableAdapter.getChild(groupPosition, childPosition);

                if (view.getId() == R.id.btn_contextmenu) {

                    ImageButton contextMenu = view.findViewById(R.id.btn_contextmenu);

                    Log.d(TAG, "btn_contextmenu: if " + prerequisiteModel.Title);
                    MyLearningModel learningModel = convertPrereqToMyLearningModel(prerequisiteModel);
                    catalogContextMenuMethod(view, contextMenu, learningModel);

                } else if (view.getId() == R.id.chxBoxPreq) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxBoxPreq);
                    if (checkBox.isChecked()) {
                        prerequisiteModel.isItemChecked = true;
                    } else {
                        prerequisiteModel.isItemChecked = false;
                    }
                    updateCheckList(prerequisiteModel, groupPosition, childPosition);
                }

                return false;
            }
        });
        applyUiColor();
        getPrerequisiteData(false);
    }

    public void applyUiColor() {

        btnAdd = (Button) findViewById(R.id.btnAddtoMy);
        btnAdd.setOnClickListener(this);

        btnCancal = (Button) findViewById(R.id.btnCancal);
        btnCancal.setOnClickListener(this);

        btnAdd.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnAdd.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnCancal.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnCancal.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));

        btnAdd.setText(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_addtomylearningoption));
        btnCancal.setText(getLocalizationValue(JsonLocalekeys.details_button_cancelbutton));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.atn_direct_enable:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getPrerequisiteData(boolean isRefresh) {

        if (!isRefresh)

            svProgressHUD.showWithStatus(getLocalizationValue(JsonLocalekeys.commoncomponent_label_loaderlabel));

        if (isNetworkConnectionAvailable(PrerequisiteContentActivity.this, -1)) {

            String urlString = appUserModel.getWebAPIUrl() + "/AssociatedContent/GetAssociatedContent?ContentID=" + myLearningModel.getContentID() + "&ComponentID=" + sideMenusModel.getComponentId() + "&ComponentInstanceID=" + sideMenusModel.getRepositoryId() + "&SiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&Instancedata=&Locale=" + preferencesManager.getLocalizationStringValue(preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));

            vollyService.getJsonObjResponseVolley("GetAssociatedContent", urlString, appUserModel.getAuthHeaders());
        } else {

        }
    }

    void initVolleyCallback() {
        resultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject jsonObject) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + jsonObject);

                if (requestType.equalsIgnoreCase("GetAssociatedContent")) {
                    try {
                        if (jsonObject != null) {
                            if (jsonObject.has("CourseList")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("CourseList");
                                List<PrerequisiteModel> prerequisiteModelArrayList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                                    Gson gson = new Gson();
                                    PrerequisiteModel prerequisiteModel = gson.fromJson(jsonObject1.toString(), PrerequisiteModel.class);
                                    prerequisiteModelArrayList.add(prerequisiteModel);
                                }
                                Log.d(TAG, "notifySuccess: " + prerequisiteModelArrayList.size());

                                if (prerequisiteModelArrayList != null && prerequisiteModelArrayList.size() > 0) {
                                    prerequisiteExpandableList = getPrerequisiteContent(true, prerequisiteModelArrayList);
                                }

                                prerequisiteExpandableAdapter.refreshList(prerequisiteExpandableList);

                                if (prerequisiteExpandableAdapter.getGroupCount() == 0) {
                                    noDataLabel.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel));
                                }

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

                if (requestType.equalsIgnoreCase("GetContentDetails")) {

                    if (response != null) {


                    }

                }

                svProgressHUD.dismiss();
            }
        };
    }


    public void finishTheActivity(MyLearningScheduleChildModel myLearningScheduleChildModel) {

        if (myLearningModel != null) {
            Intent intent = getIntent();
            intent.putExtra("SHEDULED", true);
            intent.putExtra("myLearningDetalData", (Serializable) myLearningModel);
            intent.putExtra("EventInstanceId", myLearningScheduleChildModel.getContentID());
            setResult(RESULT_OK, intent);
            finish();
        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    public LinkedHashMap<String, List<PrerequisiteModel>> getPrerequisiteContent(boolean isForWholeContent, List<PrerequisiteModel> prerequisiteModelArrayList) {

        LinkedHashMap<String, List<PrerequisiteModel>> expandableListDetail = new LinkedHashMap<String, List<PrerequisiteModel>>();

        if (isForWholeContent) {
            expandableListDetail.put(myLearningModel.getCourseName(), convertToPrerequisiteModelList());
        } else {
            expandableListDetail.put(myLearningModel.getCourseName(), getOnlyParentList(prerequisiteModelArrayList));
        }

        if (prerequisiteModelArrayList.size() > 0) {
            ArrayList<PrerequisiteModel> prerequisiteModelArrayListRecommended = new ArrayList<>();
            ArrayList<PrerequisiteModel> prerequisiteModelArrayListRequired = new ArrayList<>();

            ArrayList<PrerequisiteModel> prerequisiteModelArrayListCompletion = new ArrayList<>();

            for (int i = 0; i < prerequisiteModelArrayList.size(); i++) {
                PrerequisiteModel prerequisiteModel = prerequisiteModelArrayList.get(i);

                if (prerequisiteModel.Prerequisites.equalsIgnoreCase("1")) {
                    prerequisiteModel.prereqGrouplabel = getLocalizationValue(JsonLocalekeys.prerequistes_recommendedsubtitle_recommendedsubtitlelbl);
                    prerequisiteModelArrayListRecommended.add(prerequisiteModel);

                } else if (prerequisiteModel.Prerequisites.equalsIgnoreCase("2")) {
                    prerequisiteModel.prereqGrouplabel = getLocalizationValue(JsonLocalekeys.prerequistes_requiredsubtitle_requiredsubtitlelbl);
                    prerequisiteModelArrayListRequired.add(prerequisiteModel);

                } else if (prerequisiteModel.Prerequisites.equalsIgnoreCase("3")) {
                    prerequisiteModel.prereqGrouplabel = getLocalizationValue(JsonLocalekeys.prerequistes_completionsubtitle_completionsubtitlelbl);
                    prerequisiteModelArrayListCompletion.add(prerequisiteModel);
                }

            }

            if (prerequisiteModelArrayListRecommended.size() > 0) {
                expandableListDetail.put(getLocalizationValue(JsonLocalekeys.prerequistes_recommendedtitle_recommendedtitlelbl), prerequisiteModelArrayListRecommended);
            }

            if (prerequisiteModelArrayListRequired.size() > 0) {
                expandableListDetail.put(getLocalizationValue(JsonLocalekeys.prerequistes_requiredtitle_requiredtitlelbl), prerequisiteModelArrayListRequired);
            }

            if (prerequisiteModelArrayListCompletion.size() > 0) {

                expandableListDetail.put(getLocalizationValue(JsonLocalekeys.prerequistes_completiontitle_completiontitlelbl), prerequisiteModelArrayListCompletion);
            }
        }

        return expandableListDetail;
    }

    public ArrayList<PrerequisiteModel> convertToPrerequisiteModelList() {

        ArrayList<PrerequisiteModel> prerequisiteModelArrayList = new ArrayList<>();

        PrerequisiteModel prerequisiteModel = new PrerequisiteModel();

        prerequisiteModel.ContentID = myLearningModel.getContentID();

        prerequisiteModel.ThumbnailIconPath = "/Content/SiteFiles/ContentTypeIcons/" + myLearningModel.getContentTypeImagePath();

        String createdOn = convertToEventDisplayDateFormatCreatedOn(myLearningModel.getCreatedDate(), "yyyy-MM-dd'T'HH:mm:ss");

        prerequisiteModel.CreatedOn = createdOn;

        prerequisiteModel.AuthorDisplayName = myLearningModel.getAuthor();

        prerequisiteModel.ScoID = myLearningModel.getScoId();

        prerequisiteModel.ThumbnailImagePath = myLearningModel.getImageData();

        prerequisiteModel.Tags = "";

        prerequisiteModel.Title = myLearningModel.getCourseName();

        prerequisiteModel.ShortDescription = myLearningModel.getShortDes();

        prerequisiteModel.ContentTypeId = myLearningModel.getObjecttypeId();

        prerequisiteModel.Currency = myLearningModel.getCurrency();

        prerequisiteModel.ContentType = myLearningModel.getMediaName();

        prerequisiteModel.TimeZone = myLearningModel.getTimeZone();

        prerequisiteModel.SalePrice = myLearningModel.getPrice();

        prerequisiteModel.bit5 = false;

        prerequisiteModel.Prerequisites = "";

        prerequisiteModel.IsLearnerContent = false;

        prerequisiteModel.Ischecked = false;

        prerequisiteModel.Usertimezone = "";

        prerequisiteModel.EventselectedinstanceID = "";


        String fromDate = convertToEventDisplayDateFormat(myLearningModel.getEventstartTime(), "yyyy-MM-dd hh:mm:ss");
        String toDate = convertToEventDisplayDateFormat(myLearningModel.getEventendTime(), "yyyy-MM-dd hh:mm:ss");


        prerequisiteModel.EventStartDateTime = fromDate;

        prerequisiteModel.EventEndDateTime = toDate;

        prerequisiteModel.IsRequiredCompletionCompleted = "";

        prerequisiteModel.EventScheduleType = "" + myLearningModel.getEventScheduleType();

        prerequisiteModel.DetailsLink = "";

        prerequisiteModel.Prerequisites = "0";

        prerequisiteModel.NoInstanceAvailable = "";

        // prerequisiteModel.prereqGrouplabel = myLearningModel.getMediaName();

        prerequisiteModel.isItemChecked = true;

        prerequisiteModel.isAddedToMylearning = myLearningModel.getAddedToMylearning();

        prerequisiteModel.viewType = myLearningModel.getViewType();

        prerequisiteModelArrayList.add(prerequisiteModel);

        return prerequisiteModelArrayList;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnAddtoMy:
                completeAddtoMyLearningValidation();
                break;
            case R.id.btnCancal:
                finish();
                break;
        }
    }

    public void catalogContextMenuMethod(final View v, ImageButton btnselected, final MyLearningModel myLearningModel) {
        this.v = v;
        this.btnselected = btnselected;

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.catalog_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);//view  ctx_view
        menu.getItem(2).setVisible(false);//buy    ctx_buy
        menu.getItem(4).setVisible(false);//delete  ctx_delete
        menu.getItem(5).setVisible(false);//download ctx_download
        menu.getItem(6).setVisible(false);//addwishlist  ctx_addtowishlist
        menu.getItem(7).setVisible(false);//rmwishlist  ctx_removefromwishlist


        menu.getItem(1).setVisible(false);//add   ctx_add
        menu.getItem(3).setVisible(true);//detail ctx_detail

        menu.getItem(3).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_detailsoption));//view  ctx_view
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.catalog_actionsheet_addtomylearningoption));//add   ctx_add

//        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);

        if (myLearningModel.getAddedToMylearning() == 1) {

            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(true);

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                Integer relatedCount = Integer.parseInt(myLearningModel.getRelatedContentCount());
                if (relatedCount > 0 && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
                    menu.getItem(0).setVisible(true);
                } else {
                    menu.getItem(0).setVisible(false);
                }
            }

        } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70") && uiSettingsModel.isEnableMultipleInstancesforEvent() && myLearningModel.getEventScheduleType() == 1) {
            menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.events_actionsheet_enrolloption));//add   ctx_add
            menu.getItem(1).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ctx_add:
                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70") && uiSettingsModel.isEnableMultipleInstancesforEvent() && myLearningModel.getEventScheduleType() == 1) {

                            // multi instance
                        }
                        break;
                    case R.id.ctx_detail:
//                        Intent intentDetail = new Intent(PrerequisiteContentActivity.this, MyLearningDetailActivity1.class);
//                        intentDetail.putExtra("myLearningDetalData", myLearningModel);
//                        intentDetail.putExtra("sideMenusModel", sideMenusModel);
//                        intentDetail.putExtra("IFROMCATALOG", true);
//                        intentDetail.putExtra("ISICONENABLED", true);
//                        startActivityForResult(intentDetail, DETAIL_CATALOG_CODE);
//                        getMobileCatalogObjectsData(myLearningModel);
                        GetContentDetails(myLearningModel.getContentID(), myLearningModel);
                        break;

                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }


    public MyLearningModel convertPrereqToMyLearningModel(PrerequisiteModel prerequisiteModel) {


        MyLearningModel myLearningModel = new MyLearningModel();

        myLearningModel.setContentID(prerequisiteModel.ContentID);

        myLearningModel.setContentTypeImagePath(prerequisiteModel.ThumbnailIconPath);

        myLearningModel.setCreatedDate(prerequisiteModel.CreatedOn);

        myLearningModel.setAuthor(prerequisiteModel.AuthorDisplayName);

        myLearningModel.setScoId(prerequisiteModel.ScoID);

        if (prerequisiteModel.ThumbnailImagePath.contains("http"))
            myLearningModel.setImageData(prerequisiteModel.ThumbnailImagePath);
        else
            myLearningModel.setImageData(appUserModel.getSiteURL() + prerequisiteModel.ThumbnailImagePath);
        myLearningModel.setCourseName(prerequisiteModel.Title);

        myLearningModel.setShortDes(prerequisiteModel.ShortDescription);

        myLearningModel.setObjecttypeId(prerequisiteModel.ContentTypeId);

        myLearningModel.setCurrency(prerequisiteModel.Currency);

        myLearningModel.setMediaName(prerequisiteModel.ContentType);

        myLearningModel.setTimeZone(prerequisiteModel.TimeZone);

        myLearningModel.setPrice(prerequisiteModel.SalePrice);

        myLearningModel.isFromPrereq = true;

        if (prerequisiteModel.Ischecked && prerequisiteModel.IsLearnerContent) {
            myLearningModel.setAddedToMylearning(1);
        } else {
            myLearningModel.setAddedToMylearning(prerequisiteModel.isAddedToMylearning);
        }

        myLearningModel.setUserID(appUserModel.getUserIDValue());

        myLearningModel.setSiteID(appUserModel.getSiteIDValue());

        myLearningModel.setSiteURL(appUserModel.getSiteURL());


        myLearningModel.setViewType(prerequisiteModel.viewType);


//        prerequisiteModel.bit5 = false;
//
//        prerequisiteModel.Prerequisites = "";
//
//        prerequisiteModel.IsLearnerContent = false;
//
//        prerequisiteModel.Ischecked = false;
//
//        prerequisiteModel.Usertimezone = "";
//
//        prerequisiteModel.EventselectedinstanceID = "";
//
//        prerequisiteModel.DetailsLink = "";
//
//        prerequisiteModel.NoInstanceAvailable = "";
//
//        prerequisiteModel.isItemChecked = true;

        myLearningModel.setEventstartTime(prerequisiteModel.EventStartDateTime);

        myLearningModel.setEventendTime(prerequisiteModel.EventEndDateTime);

        prerequisiteModel.IsRequiredCompletionCompleted = "";

        myLearningModel.setEventScheduleType(Integer.parseInt(prerequisiteModel.EventScheduleType));


        // myLearningModel.setMediaName(prerequisiteModel.prereqGrouplabel);

        return myLearningModel;
    }


//    public void updateCheckList(PrerequisiteModel prerequisiteModel, int groupPosition, int childPosition) {
//
//
//        Set<String> newKeySets = prerequisiteExpandableList.keySet();
//
//        List<String> listKeySets = new ArrayList();
//        listKeySets.addAll(newKeySets);
//
//        Log.d(TAG, "getAllDataFromHashMap: " + newKeySets);
//        List<PrerequisiteModel> getprerquList = new ArrayList<>();
//        for (int k = 0; k < listKeySets.size(); k++) {
//
//            if (groupPosition == k) {
//
//                getprerquList = prerequisiteExpandableList.get(listKeySets.get(k));
//
//            }
//        }
//        if (getprerquList != null && getprerquList.size() > 0) {
//
//            getprerquList.get(childPosition).isItemChecked = prerequisiteModel.isItemChecked;
//
//            prerequisiteExpandableList = getPrerequisiteContent(getprerquList);
//            if (prerequisiteModel.isItemChecked) {
//                checkEventValidation(prerequisiteModel);
//            }
//        }
//
//        prerequisiteExpandableAdapter.refreshList(prerequisiteExpandableList);
//
//        Log.d(TAG, "updateCheckList: " + prerequisiteModel.isItemChecked);
//
//    }


    public void updateCheckList(PrerequisiteModel prerequisiteModel, int groupPosition, int childPosition) {

        List<PrerequisiteModel> prerequisiteModelList = getAllDataFromHashMap(true);

        if (prerequisiteModelList != null && prerequisiteModelList.size() > 0) {

            for (int k = 0; k < prerequisiteModelList.size(); k++) {

                if (prerequisiteModelList.get(k).ContentID.equalsIgnoreCase(prerequisiteModel.ContentID)) {
                    prerequisiteModelList.get(k).isItemChecked = prerequisiteModel.isItemChecked;
                }
            }
            prerequisiteExpandableList = getPrerequisiteContent(false, prerequisiteModelList);
            if (prerequisiteModel.isItemChecked) {
                boolean isExpired = checkEventValidation(prerequisiteModel);
                if (isExpired)
                    return;
            }

        }

        prerequisiteExpandableAdapter.refreshList(prerequisiteExpandableList);

        if (prerequisiteExpandableAdapter.getGroupCount() == 0) {
            noDataLabel.setText(getLocalizationValue(JsonLocalekeys.commoncomponent_label_nodatalabel));
        }

        Log.d(TAG, "updateCheckList: " + prerequisiteModel.isItemChecked);

    }


    public void completeAddtoMyLearningValidation() {

        List<PrerequisiteModel> prerequisiteModelList = getAllDataFromHashMap(false);

//
        if (prerequisiteModelList != null && prerequisiteModelList.size() > 0) {

            boolean parentChecked = isParentChecked(prerequisiteModelList);

            if (parentChecked) {
                boolean validationRequired = checkValidation(prerequisiteModelList, true);
                boolean validationCompletion = checkValidation(prerequisiteModelList, false);
                if (!validationRequired) {
                    Toast.makeText(this, getLocalizationValue(JsonLocalekeys.prerequistesalerttitle1_alerttitle1), Toast.LENGTH_SHORT).show();
                } else if (!validationCompletion) {
                    Toast.makeText(this, getLocalizationValue(JsonLocalekeys.prerequistesalerttitle_alerttitle), Toast.LENGTH_SHORT).show();
                } else {
                    List<String> contentArrays = getallSelectedContents(prerequisiteModelList);
                    boolean isManuallyChecked = isManuallyChecked(prerequisiteModelList);
                    if (contentArrays.size() > 0 && isManuallyChecked) {
// call api from here
                        try {
                            callAPiForAddtoMylearning(toCSVString(contentArrays));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        Toast.makeText(this, getLocalizationValue(JsonLocalekeys.prerequistesalerttitle3_alerttitle3), Toast.LENGTH_SHORT).show();
                    }

                }

            } else {
                List<String> contentArrays = getallSelectedContents(prerequisiteModelList);

                boolean isManuallyChecked = isManuallyChecked(prerequisiteModelList);

                if (contentArrays.size() > 0 && isManuallyChecked) {
// call api from here
                    try {
                        callAPiForAddtoMylearning(toCSVString(contentArrays));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(this, getLocalizationValue(JsonLocalekeys.prerequistesalerttitle3_alerttitle3), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    public boolean checkValidation(List<PrerequisiteModel> prerequisiteModelList, boolean isType) {

        boolean isValidated = false;

        for (int i = 0; i < prerequisiteModelList.size(); i++) {

            PrerequisiteModel prerequisiteModel = prerequisiteModelList.get(i);

            if (isType) {
                if (prerequisiteModel.Prerequisites.equalsIgnoreCase("2")) {
//required
                    if (prerequisiteModel.isItemChecked) {
                        isValidated = true;
                    } else {
                        isValidated = false;
                        break;
                    }
                }

            } else {

                if (prerequisiteModel.Prerequisites.equalsIgnoreCase("3")) {
//completion
                    if (prerequisiteModel.isItemChecked) {
                        isValidated = true;
                    } else {
                        isValidated = false;
                        break;
                    }

                }

            }


        }

        return isValidated;
    }

    public boolean isParentChecked(List<PrerequisiteModel> prerequisiteModelList) {

        boolean parentChecked = false;

        for (int i = 0; i < prerequisiteModelList.size(); i++) {

            PrerequisiteModel prerequisiteModel = prerequisiteModelList.get(i);
            if (myLearningModel.getScoId().equalsIgnoreCase(prerequisiteModel.ScoID)) {
                parentChecked = prerequisiteModel.isItemChecked;
            }

        }

        return parentChecked;
    }


    public List<PrerequisiteModel> getAllDataFromHashMap(boolean isForWholeContent) {

        List<PrerequisiteModel> getPrerequsiteList = new ArrayList<>();


        if (prerequisiteExpandableList != null && prerequisiteExpandableList.size() > 0) {

            Set<String> newKeySets = prerequisiteExpandableList.keySet();

            Log.d(TAG, "getAllDataFromHashMap: " + newKeySets);

            for (String key : newKeySets) {
                System.out.println(key);

                List<PrerequisiteModel> localPreqList = prerequisiteExpandableList.get(key);

                getPrerequsiteList.addAll(localPreqList);
            }

        }

        if (isForWholeContent)
            return getPrerequsiteList;

        if (getPrerequsiteList != null && getPrerequsiteList.size() > 0) {

            for (int i = 0; i < getPrerequsiteList.size(); i++) {
                if (getPrerequsiteList.get(i).Ischecked && getPrerequsiteList.get(i).IsLearnerContent) {
                    getPrerequsiteList.get(i).isItemChecked = true;
                }
            }

        }
        return getPrerequsiteList;
    }


    public List<String> getallSelectedContents(List<PrerequisiteModel> prerequisiteModelList) {

        List<String> contentArrays = new ArrayList<>();

        if (prerequisiteModelList != null && prerequisiteModelList.size() > 0) {

            for (int i = 0; i < prerequisiteModelList.size(); i++) {
                if (prerequisiteModelList.get(i).isItemChecked) {
                    contentArrays.add(prerequisiteModelList.get(i).ContentID);
                    boolean isExpired = checkEventValidation(prerequisiteModelList.get(i));
                    if (isExpired) {
                        contentArrays = new ArrayList<>();
                        return contentArrays;
                    }

                }
            }

        }
        return contentArrays;
    }


    public boolean isManuallyChecked(List<PrerequisiteModel> prerequisiteModelList) {

        boolean isAlreadyChecked = false;

        if (prerequisiteModelList != null && prerequisiteModelList.size() > 0) {

            for (int i = 0; i < prerequisiteModelList.size(); i++) {
                if (prerequisiteModelList.get(i).isItemChecked) {
                    if (prerequisiteModelList.get(i).Ischecked && prerequisiteModelList.get(i).IsLearnerContent) {
                        isAlreadyChecked = false;
                    } else {
                        isAlreadyChecked = true;
                        break;
                    }
                }
            }

        }
        return isAlreadyChecked;
    }


    public void callAPiForAddtoMylearning(String contentIDs) throws JSONException {

        if (contentIDs.length() < 1) {
            Toast.makeText(PrerequisiteContentActivity.this, getLocalizationValue(JsonLocalekeys.discussionforum_label_newtopicdescriptionlabel), Toast.LENGTH_SHORT).show();
        } else {

            JSONObject parameters = new JSONObject();

            parameters.put("SelectedContent", contentIDs);
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("OrgUnitID", appUserModel.getSiteIDValue());
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("AdditionalParams", "");
            parameters.put("AddLearnerPreRequisiteContent", "");
            parameters.put("AddMultiinstanceswithprice", "");
            parameters.put("AddWaitlistContentIDs", "");

            String parameterString = parameters.toString();
            Log.d(TAG, "validateNewForumCreation: " + parameterString);

            if (isNetworkConnectionAvailable(this, -1)) {
                sendNewAddMylearningDataToServer(parameterString);
            } else {
                Toast.makeText(PrerequisiteContentActivity.this, "" + getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void sendNewAddMylearningDataToServer(final String postData) {

        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

        final String urlString = appUserModel.getWebAPIUrl() + "/catalog/AssociatedAddtoMyLearning";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + responseStr);

                if (isValidString(responseStr) && responseStr.contains("true")) {

                    finish();

                } else {

                    Toast.makeText(PrerequisiteContentActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(PrerequisiteContentActivity.this, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();
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

        RequestQueue rQueue = Volley.newRequestQueue(PrerequisiteContentActivity.this);
        rQueue.add(request);
        request.setRetryPolicy(new

                DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public boolean checkEventValidation(PrerequisiteModel prerequisiteModel) {
        boolean isExpired = false;

        if (prerequisiteModel.ContentTypeId.equalsIgnoreCase("70") && !prerequisiteModel.IsLearnerContent && prerequisiteModel.NoInstanceAvailable.equalsIgnoreCase("true")) {

            Toast.makeText(PrerequisiteContentActivity.this, getLocalizationValue(JsonLocalekeys.prerequistesalerttitle4_alerttitle4), Toast.LENGTH_LONG).show();
            isExpired = true;
        }
        return isExpired;
    }


    public List<PrerequisiteModel> getOnlyParentList(List<PrerequisiteModel> prerequisiteModelArrayList) {

        List<PrerequisiteModel> prerequisiteModelArrayListLocal = new ArrayList<>();


        for (int k = 0; k < prerequisiteModelArrayList.size(); k++) {

            if (prerequisiteModelArrayList.get(k).ContentID.equalsIgnoreCase(myLearningModel.getContentID())) {
                prerequisiteModelArrayListLocal.add(prerequisiteModelArrayList.get(k));
                break;
            }
        }
        return prerequisiteModelArrayListLocal;
    }


    public void getMobileCatalogObjectsData(MyLearningModel myLearningModellocal) {

        svProgressHUD.showWithStatus(getResources().getString(R.string.loadingtxt));

        String urlStr = appUserModel.getWebAPIUrl() + "MobileLMS/MobileCatalogObjectsData";

        JSONObject parameters = new JSONObject();

        try {

            parameters.put("pageIndex", "1");
            parameters.put("pageSize", "10");
            parameters.put("SearchText", "");
            parameters.put("ContentID", myLearningModellocal.getContentID());
            parameters.put("sortBy", "");
            parameters.put("ComponentID", sideMenusModel.getComponentId());
            parameters.put("ComponentInsID", sideMenusModel.getRepositoryId());
            parameters.put("AdditionalParams", "");
            parameters.put("SelectedTab", "");
            parameters.put("AddtionalFilter", "");
            parameters.put("LocationFilter", "");
            parameters.put("UserID", appUserModel.getUserIDValue());
            parameters.put("SiteID", appUserModel.getSiteIDValue());
            parameters.put("OrgUnitID", appUserModel.getSiteIDValue());
            parameters.put("Locale", preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)));
            parameters.put("groupBy", "");
            parameters.put("categories", "");
            parameters.put("objecttypes", "");
            parameters.put("skillcats", "");
            parameters.put("skills", "");
            parameters.put("jobroles", "");
            parameters.put("solutions", "");
            parameters.put("keywords", "");
            parameters.put("ratings", "");
            parameters.put("pricerange", "");
            parameters.put("eventdate", "");
            parameters.put("certification", "");
            parameters.put("duration", "");
            parameters.put("instructors", "");
            parameters.put("iswishlistcontent", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethod(parameterString, "CATALOGDATA", urlStr);
    }

    public MyLearningModel generateMylearningModel(JSONObject jsonObject) throws
            JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("table2");
        // for deleting records in table for respective table

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel myLearningModel = new MyLearningModel();


            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("orgunitid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("orgunitid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }


            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (isValidString(authorName)) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }


            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }

            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


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
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatusActual(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }

                // shortdes
                if (jsonMyLearningColumnObj.has("shortdescription")) {


                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                    myLearningModel.setShortDes(result.toString());

                }

                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

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
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

                }
                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

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


                // isExpiry
                myLearningModel.setIsExpiry("false");

                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // display
                myLearningModel.setDisplayName(appUserModel.getDisplayName());
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // password
                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = this.getExternalFilesDir(null)
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
                if (jsonMyLearningColumnObj.has("presenter")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("saleprice")) {

                    myLearningModel.setPrice(jsonMyLearningColumnObj.get("saleprice").toString());

                }

                //googleproductid
                if (jsonMyLearningColumnObj.has("googleproductid")) {

                    myLearningModel.setGoogleProductID(jsonMyLearningColumnObj.get("googleproductid").toString());

                }

                //componentid
                if (jsonMyLearningColumnObj.has("componentid")) {

                    myLearningModel.setComponentId(jsonMyLearningColumnObj.get("componentid").toString());

                }

                //currency
                if (jsonMyLearningColumnObj.has("currency")) {

                    myLearningModel.setCurrency(jsonMyLearningColumnObj.get("currency").toString());

                }

                //viewtype
                if (jsonMyLearningColumnObj.has("viewtype")) {

                    myLearningModel.setViewType(jsonMyLearningColumnObj.get("viewtype").toString());

                }
                //isaddedtomylearning
                if (jsonMyLearningColumnObj.has("isaddedtomylearning")) {

                    myLearningModel.setAddedToMylearning(Integer.parseInt(jsonMyLearningColumnObj.get("isaddedtomylearning").toString()));

                }


                //membershipname
                if (jsonMyLearningColumnObj.has("membershipname")) {

                    myLearningModel.setMembershipname(jsonMyLearningColumnObj.get("membershipname").toString());

                }
                //membershiplevel
                if (jsonMyLearningColumnObj.has("membershiplevel")) {

                    String memberShip = jsonMyLearningColumnObj.getString("membershiplevel");
                    int memberInt = 1;
                    if (isValidString(memberShip)) {
                        memberInt = Integer.parseInt(memberShip);
                    } else {
                        memberInt = 1;
                    }
                    myLearningModel.setMemberShipLevel(memberInt);

                }

                //folderpath
                if (jsonMyLearningColumnObj.has("folderpath")) {

                    myLearningModel.setFolderPath(jsonMyLearningColumnObj.get("folderpath").toString());

                }

                myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.optString("iconpath", ""));

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
            }

        }

        return myLearningModel;
    }

    public void GetContentDetails(String eventInstanceIdOrEventContentID, MyLearningModel tempModel) {
        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);

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
            parameters.put("HideAdd: ", "false");
            parameters.put("objectTypeID", "-1");
            parameters.put("scoID", "");
            parameters.put("SubscribeERC", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String parameterString = parameters.toString();

        vollyService.getStringResponseFromPostMethodWithMylearningModel(parameterString, "GetContentDetails", urlStr, tempModel);

    }

//    public static MyLearningModel getDetailMylearningModel(String response, AppUserModel appUserModel, MyLearningModel tempModel) {
//
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = new JSONObject(response);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        MyLearningModel myLearningModel = new MyLearningModel();
//        myLearningModel.setUserID(appUserModel.getUserIDValue());
//        myLearningModel.setUserName(appUserModel.getUserName());
//        myLearningModel.setSiteID(appUserModel.getSiteIDValue());
//        myLearningModel.setSiteURL(appUserModel.getSiteURL());
//
//        myLearningModel.setSiteName(appUserModel.getSiteName());
//        myLearningModel.setContentID(tempModel.getContentID());
//        myLearningModel.setObjectId(jsonObject.optString("", ""));
//        myLearningModel.setCourseName(jsonObject.optString("", ""));
//        myLearningModel.setAuthor(jsonObject.optString("", ""));
//        myLearningModel.setPresenter(jsonObject.optString("", ""));
//        myLearningModel.setShortDes(jsonObject.optString("", ""));
//        myLearningModel.setLongDes(jsonObject.optString("", ""));
//        myLearningModel.setImageData(jsonObject.optString("", ""));
//        myLearningModel.setMediaName(jsonObject.optString("", ""));
//        myLearningModel.setCreatedDate(jsonObject.optString("", ""));
//        myLearningModel.setStartPage(jsonObject.optString("", ""));
//        myLearningModel.setAviliableSeats(jsonObject.optString("", ""));
//        myLearningModel.setObjecttypeId(jsonObject.optString("", ""));
//        myLearningModel.setLocationName(jsonObject.optString("", ""));
//        myLearningModel.setScoId(jsonObject.optString("", ""));
//        myLearningModel.setParticipantUrl(jsonObject.optString("", ""));
//        myLearningModel.setStatusActual(jsonObject.optString("", ""));
//        myLearningModel.setPassword(appUserModel.getPassword());
//        myLearningModel.setDisplayName(jsonObject.optString("", ""));
//        myLearningModel.setIsListView("false");
//        myLearningModel.setIsDownloaded("false");
//        myLearningModel.setCourseAttempts("0");
//        myLearningModel.setAddedToMylearning(jsonObject.optString("", ""));
//        myLearningModel.setEventContentid(jsonObject.optString("", ""));
//        myLearningModel.setRelatedContentCount(jsonObject.optString("", ""));
//        myLearningModel.setDurationEndDate(jsonObject.optString("", ""));
//        myLearningModel.setRatingId(jsonObject.optString("", ""));
//        myLearningModel.setIsExpiry("false");
//        myLearningModel.setMediatypeId(jsonObject.optString("", ""));
//        myLearningModel.setDateAssigned(jsonObject.optString("", ""));
//        myLearningModel.setKeywords(jsonObject.optString("", ""));
//        myLearningModel.setDownloadURL(jsonObject.optString("", ""));
//        myLearningModel.setOfflinepath("");
//        myLearningModel.setPresenter(jsonObject.optString("", ""));
//        myLearningModel.setEventAddedToCalender(false);
//        myLearningModel.setTimeZone(jsonObject.optString("", ""));
//        myLearningModel.setJoinurl("");
//        myLearningModel.setTypeofevent(jsonObject.optString("", ""));
//        myLearningModel.setViewType("" + jsonObject.optString("", ""));
//        myLearningModel.setProgress("inprogress");
//
//        myLearningModel.setMemberShipLevel(jsonObject.optString("", ""));
//
//        myLearningModel.setMembershipname(jsonObject.optString("", ""));
//
//        myLearningModel.setFolderPath(jsonObject.optString("", ""));
//
//        myLearningModel.setJwvideokey(jsonObject.optString("", "");
//
//        myLearningModel.setCloudmediaplayerkey(jsonObject.optString("", ""));
//
//        myLearningModel.setPublishedDate(formatDate(globalSearchResultModelNew.publisheddate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
//
//
//        myLearningModel.setEventstartTime(jsonObject.optString("", ""));
//        myLearningModel.setEventendTime(jsonObject.optString("", ""));
//
//        myLearningModel.setEventstartUtcTime(jsonObject.optString("", ""));
//
//        myLearningModel.setEventendUtcTime(jsonObject.optString("", ""));
//
//        myLearningModel.setBadCancellationEnabled(jsonObject.optString("", ""));
//
//        return myLearningModel;
//    }

}