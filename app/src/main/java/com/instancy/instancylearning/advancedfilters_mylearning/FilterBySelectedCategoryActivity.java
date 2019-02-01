package com.instancy.instancylearning.advancedfilters_mylearning;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class FilterBySelectedCategoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = FilterBySelectedCategoryActivity.class.getSimpleName();
    ListView listView;
    List<FilterByModel> filterByModelList;
    List<FilterByModel> filtersChilds;
    List<FilterByModel> filtersParents;

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    ContentFilterByModel contentFilterByModel;

    AllFilterModel allFilterModel;

    int isFromMylearning = 0;

    FilterByAdapter filterByAdapter;

    Button btnApply, btnReset;
    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;
    IResult resultCallback = null;

    TextView noDataLabel;

    List<String> previousSelectedArrayList = new ArrayList<>();

    //    https://github.com/shineM/TreeView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_contentbyactivity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);

        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        contentFilterByModel = (ContentFilterByModel) getIntent().getExtras().getSerializable("contentFilterByModel");
        allFilterModel = (AllFilterModel) getIntent().getExtras().getSerializable("allFilterModel");
        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + contentFilterByModel.categoryDisplayName + "</font>"));

        applyUiColor();
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        noDataLabel = (TextView) findViewById(R.id.nodata_label);
        filterByModelList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.lstContentFilterBy);
        filterByAdapter = new FilterByAdapter(this, filterByModelList, contentFilterByModel);
        listView.setAdapter(filterByAdapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(this);
        filterByCategorySelected();
    }

    public void filterByCategorySelected() {

        if (isNetworkConnectionAvailable(this, -1)) {

            String urlStr = "SiteID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&ComponentID=" + sideMenusModel.getComponentId() + "&Type=" + contentFilterByModel.categoryID + "&ShowAllItems=&FilterContentType=&FilterMediaType=&EventType=&SprateEvents=false&IsCompetencypath=false&Locale=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name));

            vollyService.getStringResponseVolley("FILTER", appUserModel.getWebAPIUrl() + "/catalog/GetCategoriesTree?" + urlStr, appUserModel.getAuthHeaders());

        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

        }
    }

    public void updateSelectedArrayList(List<String> selectedValuesAry) {

        if (selectedValuesAry != null && selectedValuesAry.size() > 0) {

            for (int i = 0; i < selectedValuesAry.size(); i++) {

                for (int j = 0; j < filtersParents.size(); j++) {

                    if (selectedValuesAry.get(i).equalsIgnoreCase("" + filtersParents.get(j).categoryID)) {

                        filtersParents.get(j).isSelected = true;
                        previousSelectedArrayList.add(selectedValuesAry.get(i));

                    }

                }

            }
            filterByAdapter.refreshList(filtersParents);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void applyUiColor() {

        btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(this);

        btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        btnReset.setBackground(getButtonDrawable());

        btnReset.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        btnApply.setText(getLocalizationValue(JsonLocalekeys.advancefilter_button_applybutton));
        btnReset.setText(getLocalizationValue(JsonLocalekeys.advancefilter_button_resetbutton));
    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, this);
    }

    public ShapeDrawable getButtonDrawable() {

        ShapeDrawable sd = new ShapeDrawable();

        // Specify the shape of ShapeDrawable
        sd.setShape(new RectShape());

        // Specify the border color of shape
        sd.getPaint().setColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        // Set the border width
        sd.getPaint().setStrokeWidth(10f);

        // Specify the style is a Stroke
        sd.getPaint().setStyle(Paint.Style.STROKE);

        // Finally, add the drawable background to TextView

        return sd;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnApply:
                finishTheActivity(true);
                break;
            case R.id.btnReset:
                finishTheActivity(false);
                break;
        }
    }

    public void finishTheActivity(boolean isApply) {

        if (isApply) {
            getSelectedCategoriesValues();
        } else {
            resetArrayList();
        }

        if (contentFilterByModel != null) {
            Intent intent = getIntent();
            intent.putExtra("contentFilterByModel", (Serializable) contentFilterByModel);
            intent.putExtra("FILTER", true);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    public void getSelectedCategoriesValues() {


        if (filtersParents != null && filtersParents.size() > 0) {
            contentFilterByModel.selectedSkillIdsArry = new ArrayList<>();
            contentFilterByModel.selectedSkillNamesArry = new ArrayList<>();
            for (int i = 0; i < filtersParents.size(); i++) {

                if (filtersParents.get(i).isSelected) {

                    contentFilterByModel.selectedSkillIdsArry.add("" + filtersParents.get(i).categoryID);
                    contentFilterByModel.selectedSkillNamesArry.add("" + filtersParents.get(i).categoryName);

                }
            }

            contentFilterByModel.selectedSkillIdsArry = removeAllDuplicates(contentFilterByModel.selectedSkillIdsArry);

            contentFilterByModel.selectedSkillNamesArry = removeAllDuplicates(contentFilterByModel.selectedSkillNamesArry);

        }
    }

    public List<String> removeAllDuplicates(List<String> values) {

        List<String> noDuplicates = new ArrayList<String>();

        if (values == null || values.size() == 0)
            return noDuplicates;

        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(values);
        values.clear();
        values.addAll(hashSet);

        return values;
    }


    public void resetArrayList() {

        contentFilterByModel.selectedSkillIdsArry = addAllValues(resetBtnForList());

    }

    public List<String> resetBtnForList() {

        List<String> resetTheValues = new ArrayList<>();

        resetTheValues = addAllValues(contentFilterByModel.selectedSkillIdsArry);

        if (contentFilterByModel.selectedSkillIdsArry != null && contentFilterByModel.selectedSkillIdsArry.size() > 0) {

            if (previousSelectedArrayList != null && previousSelectedArrayList.size() > 0) {

                for (int i = 0; i < previousSelectedArrayList.size(); i++) {

                    for (int k = 0; k < contentFilterByModel.selectedSkillIdsArry.size(); k++) {

                        if (previousSelectedArrayList.get(i).equalsIgnoreCase(contentFilterByModel.selectedSkillIdsArry.get(k))) {

                            resetTheValues.remove(previousSelectedArrayList.get(i));
                        }

                    }
                }

            }

        }

        return resetTheValues;
    }

    public List<String> addAllValues(List<String> allContentt) {
        List<String> selectedValues = new ArrayList<>();
        if (allContentt != null && allContentt.size() > 0) {

            for (int i = 0; i < allContentt.size(); i++) {

                selectedValues.add(i, allContentt.get(i));
            }
        }
        return selectedValues;
    }


    public void getSelectedCategories() {
        String selectedSkillsIds = "";
        String selectedSkillsNames = "";


        if (filtersParents != null && filtersParents.size() > 0) {

            for (int i = 0; i < filtersParents.size(); i++) {

                if (filtersParents.get(i).isSelected) {

                    if (selectedSkillsIds.length() > 0) {
                        selectedSkillsIds = selectedSkillsIds.concat("," + filtersParents.get(i).categoryID);
                    } else {
                        selectedSkillsIds = "" + filtersParents.get(i).categoryID;
                    }

                    if (selectedSkillsNames.length() > 0) {
                        selectedSkillsNames = selectedSkillsNames.concat("," + filtersParents.get(i).categoryName);
                    } else {
                        selectedSkillsNames = "" + filtersParents.get(i).categoryName;
                    }

                }
            }

        }

        if (contentFilterByModel.selectedSkillsCatIdString != null && contentFilterByModel.selectedSkillsCatIdString.length() > 0) {
            contentFilterByModel.selectedSkillsCatIdString = contentFilterByModel.selectedSkillsCatIdString.concat("," + selectedSkillsIds);
            contentFilterByModel.selectedSkillsNameString = contentFilterByModel.selectedSkillsNameString.concat("," + selectedSkillsNames);
        } else {
            contentFilterByModel.selectedSkillsCatIdString = selectedSkillsIds;
            contentFilterByModel.selectedSkillsNameString = selectedSkillsNames;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("FILTER", false);
                if (refresh) {

                    contentFilterByModel = (ContentFilterByModel) data.getExtras().getSerializable("contentFilterByModel");

                }
            }
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
                if (requestType.equalsIgnoreCase("FILTER")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            filtersParents = generateFilterByModelList(response);
                            filterByAdapter.refreshList(filtersParents);
                            if (filtersParents != null && filtersParents.size() > 0) {
                                updateSelectedArrayList(contentFilterByModel.selectedSkillIdsArry);
                            } else {
                                noDataLabel.setText(getResources().getString(R.string.no_data));
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

                svProgressHUD.dismiss();
            }
        };
    }

    public List<FilterByModel> generateFilterByModelList(String responseStr) throws JSONException {
        List<FilterByModel> filterByList = new ArrayList<>();
        JSONArray jsonTableAry = new JSONArray(responseStr);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            FilterByModel filterByModel = new FilterByModel();
            JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

            filterByModel.categoryName = jsonColumnObj.optString("CategoryName");
            filterByModel.categoryID = jsonColumnObj.optString("CategoryID");
            filterByModel.parentID = jsonColumnObj.optString("ParentID","0");
            filterByModel.categoryIcon = jsonColumnObj.optString("CategoryIcon");

            if (filterByModel.parentID.equalsIgnoreCase("0")) {
                filterByList.add(filterByModel);
            }
            filterByModelList.add(filterByModel);
        }

        if (filterByList.size() > 0) {
            for (int i = 0; i < filterByList.size(); i++) {

                filterByList.get(i).isContainsChild = checkItContainsChilds(filterByList.get(i));

            }
        }

        return filterByList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FilterByModel filterByModel = (FilterByModel) adapterView.getItemAtPosition(i);
        String titleHeader = filterByModel.categoryName;
        switch (view.getId()) {
            case R.id.chxCategoryDisplayName:
                refreshCheckedItem(view, i);
                break;
            case R.id.txtCategoryDisplayName:
                if (contentFilterByModel.goInside) {
                    filtersChilds = generateInnerChildCategories(filterByModel);
                    if (filtersChilds != null && filtersChilds.size() > 0) {
                        Intent intent = new Intent(this, SelectedActivity.class);
                        intent.putExtra("isFrom", 0);
                        intent.putExtra("TITLEHEADER", titleHeader);
                        intent.putExtra("filterByModelList", (Serializable) filtersChilds);
                        intent.putExtra("contentFilterByModel", (Serializable) contentFilterByModel);
                        startActivityForResult(intent, FILTER_CLOSE_CODE);
                    }
                }
                break;
        }

    }

    public void refreshCheckedItem(View view, int i) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxCategoryDisplayName);
        if (checkBox.isChecked()) {
            filtersParents.get(i).isSelected = true;
        } else {
            filtersParents.get(i).isSelected = false;
        }
        filterByAdapter.refreshList(filtersParents);
    }


    public List<FilterByModel> generateInnerChildCategories(FilterByModel filterByModel) {
        List<FilterByModel> filterByList = new ArrayList<>();

        for (int i = 0; i < filterByModelList.size(); i++) {

            if (filterByModel.parentID.equalsIgnoreCase("0") && filterByModelList.get(i).parentID.equalsIgnoreCase(filterByModel.categoryID)) {
                filterByList.add(filterByModelList.get(i));
            }

        }

        if (filterByList.size() > 0) {

            for (int i = 0; i < filterByList.size(); i++) {
                List<FilterByModel> filterInnerByList = new ArrayList<>();

                for (int k = 0; k < filterByModelList.size(); k++) {

                    if (filterByModelList.get(k).parentID.equalsIgnoreCase(filterByList.get(i).categoryID)) {
                        filterInnerByList.add(filterByModelList.get(k));
                    }

                }

                if (filterInnerByList.size() > 0) {

                    filterByList.addAll(filterInnerByList);
                }

            }

        }

        return filterByList;
    }

    public boolean checkItContainsChilds(FilterByModel filterByModel) {
        List<FilterByModel> filterByList = new ArrayList<>();

        boolean isHaveChilds = false;

        for (int i = 0; i < filterByModelList.size(); i++) {

            if (filterByModel.parentID.equalsIgnoreCase("0") && filterByModelList.get(i).parentID.equalsIgnoreCase(filterByModel.categoryID)) {
//                filterByList.add(filterByModelList.get(i));
                isHaveChilds = true;
            }

        }

        if (filterByList.size() > 0) {

            for (int i = 0; i < filterByList.size(); i++) {
                List<FilterByModel> filterInnerByList = new ArrayList<>();

                for (int k = 0; k < filterByModelList.size(); k++) {

                    if (filterByModelList.get(k).parentID.equalsIgnoreCase(filterByList.get(i).categoryID)) {
                        // filterInnerByList.add(filterByModelList.get(k));
                        isHaveChilds = true;
                    }

                }

                if (filterInnerByList.size() > 0) {
                    filterByList.addAll(filterInnerByList);
                }

            }

        }

        return isHaveChilds;
    }


}
