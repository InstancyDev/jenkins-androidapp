package com.instancy.instancylearning.advancedfilters_mylearning;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.Button;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.List;


import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class SortActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SortActivity.class.getSimpleName();

    RadioGroup radioGroup;

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;

    UiSettingsModel uiSettingsModel;

    AllFilterModel allFilterModel;

    int isFromMylearning = 0;

    Button btnApply, btnReset;

    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;

    IResult resultCallback = null;

    List<SortModel> sortModelList;

    RelativeLayout relativeLayout;

    HashMap<String, String> responMap = null;

    String contentFilterType = "";

    //    https://github.com/shineM/TreeView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_radioactivity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);

        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        allFilterModel = (AllFilterModel) getIntent().getExtras().getSerializable("allFilterModel");
        responMap = (HashMap<String, String>) getIntent().getExtras().getSerializable("responMap");

        contentFilterType = getIntent().getStringExtra("contentFilterType");

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + allFilterModel.categoryName + "</font>"));

        applyUiColor();

        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        sortModelList = new ArrayList<>();

        radioGroup = (RadioGroup) findViewById(R.id.radioGrp);
        radioGroup.setVisibility(View.VISIBLE);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeSeekbar);

        relativeLayout.setVisibility(View.GONE);

        if (allFilterModel.isGroup) {
            sortModelList = generateGroupBy();
            dynamicRadioBtn(sortModelList);
        } else {
            getSortList();
        }

    }

    public void getSortList() {

        if (isNetworkConnectionAvailable(this, -1)) {

            String urlStr = "OrgUnitID=" + appUserModel.getSiteIDValue() + "&UserID=" + appUserModel.getUserIDValue() + "&ComponentID=" + sideMenusModel.getComponentId() + "&LocaleID=" + preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name)) + "";

            vollyService.getStringResponseVolley("SORT", appUserModel.getWebAPIUrl() + "/catalog/GetComponentSortOptions?" + urlStr, appUserModel.getAuthHeaders());

        } else {

            Toast.makeText(this, JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.network_alerttitle_nointernet, this), Toast.LENGTH_SHORT).show();

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

        btnApply.setText(getLocalizationValue(JsonLocalekeys.filter_btn_applybutton));
        btnReset.setText(getLocalizationValue(JsonLocalekeys.filter_btn_resetbutton));
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
                finishTheActivity(false);
                break;
            case R.id.btnReset:
                finishTheActivity(true);
                break;
        }
    }

    public void finishTheActivity(boolean isReseted) {

        if (isReseted) {
            resetAllSortings();
        } else {
            getSelectedCategories();
        }
        if (allFilterModel != null) {
            Intent intent = getIntent();
            intent.putExtra("allFilterModel", (Serializable) allFilterModel);
            intent.putExtra("ISFROM", 3);
            intent.putExtra("FILTER", false);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.select_at_least_one_category, this), Toast.LENGTH_SHORT).show();
        }

    }

    public void getSelectedCategories() {

        for (int i = 0; i < sortModelList.size(); i++) {

            if (sortModelList.get(i).isSelected) {
                allFilterModel.categorySelectedData = sortModelList.get(i).optionIdValue;
                allFilterModel.categorySelectedDataDisplay = sortModelList.get(i).optionDisplayText;
                allFilterModel.categorySelectedID = sortModelList.get(i).categoryID;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");

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
                if (requestType.equalsIgnoreCase("SORT")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            sortModelList = generateFilterByModelList(response);
                            dynamicRadioBtn(sortModelList);
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

    public void dynamicRadioBtn(final List<SortModel> sortModelList) {

        if (sortModelList != null && sortModelList.size() > 0) {
            for (int i = 0; i < sortModelList.size(); i++) {
                RadioButton rbn = new RadioButton(this);
                rbn.setId(sortModelList.get(i).categoryID);
                rbn.setText(sortModelList.get(i).optionDisplayText);
                rbn.setTextSize(20.0f);
                rbn.setPadding(4, 20, 4, 20);
                rbn.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
                if (allFilterModel.categorySelectedData.equalsIgnoreCase(sortModelList.get(i).optionIdValue)) {
                    rbn.setChecked(true);
                }
//                if (allFilterModel.categorySelectedData == sortModelList.get(i).categoryID) {
//                    rbn.setChecked(true);
//                }
//
                radioGroup.addView(rbn);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        if (radioGroup.getChildAt(i).getId() == checkedId) {
                            updateSelectedModel(i);
                            break;
                        }
                    }
                }
            });
        }

    }

    public void updateSelectedModel(int i) {

        if (sortModelList != null && sortModelList.size() > 0) {

            for (int k = 0; k < sortModelList.size(); k++) {

                if (i == k) {
                    sortModelList.get(k).isSelected = true;
                } else {
                    sortModelList.get(k).isSelected = false;
                }

            }

        }

    }


    public void resetAllSortings() {
        if (sortModelList != null && sortModelList.size() > 0) {

            allFilterModel.categorySelectedData = "";
            allFilterModel.categorySelectedID = 0;
        }
    }

    public List<SortModel> generateFilterByModelList(String responseStr) throws JSONException {
        List<SortModel> sortModelList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(responseStr);
        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");
        List<String> filterCategoriesArray = getArrayListFromString(contentFilterType);
        for (int i = 0; i < jsonTableAry.length(); i++) {
            SortModel sortModel = new SortModel();
            JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

            sortModel.optionDisplayText = jsonColumnObj.optString("OptionText");
            sortModel.optionIdValue = jsonColumnObj.optString("OptionValue");
            sortModel.componentID = jsonColumnObj.optInt("ComponentID");
            sortModel.localID = jsonColumnObj.optString("LocalID");
            sortModel.categoryID = jsonColumnObj.optInt("ID");
            //   DateAssigned desc

            if (jsonColumnObj.optString("EnableColumn").equalsIgnoreCase("ecommerce")) {
                if (filterCategoriesArray != null && filterCategoriesArray.size() > 0) {
                    for (int k = 0; k < filterCategoriesArray.size(); k++) {
                        switch (filterCategoriesArray.get(k)) {
                            case "ecommerceprice":
                                sortModelList.add(sortModel);
                                break;
//                            case "rating":
//                                sortModelList.add(sortModel);
//                                break;

                        }
                    }
                }
//                if (responMap != null && responMap.containsKey("EnableEcommerce")) {
//                    String showrRatings = responMap.get("EnableEcommerce");
//                    if (showrRatings.contains("true")) {
//                        if (uiSettingsModel.isEnableEcommerce()) {
//                            sortModelList.add(sortModel);
//                        }
//                    }
//                }

            } else if (jsonColumnObj.optString("EnableColumn").equalsIgnoreCase("rating")) {

                if (filterCategoriesArray != null && filterCategoriesArray.size() > 0) {
                    for (int k = 0; k < filterCategoriesArray.size(); k++) {
                        switch (filterCategoriesArray.get(k)) {
                            case "rating":
                                sortModelList.add(sortModel);
                                break;
                        }
                    }
                }
//                if (responMap != null && responMap.containsKey("ShowrRatings")) {
//                    String showrRatings = responMap.get("ShowrRatings");
//                    if (showrRatings.contains("true")) {
//                        sortModelList.add(sortModel);
//                    }
//                }
            } else {

                sortModelList.add(sortModel);
            }

        }
        return sortModelList;
    }

    public List<SortModel> generateGroupBy() {

        List<SortModel> sortModelList = new ArrayList<>();

        for (int i = 0; i < allFilterModel.groupArrayList.size(); i++) {

            SortModel sortModel = new SortModel();
            sortModel.optionIdValue = allFilterModel.groupArrayList.get(i);
            switch (allFilterModel.groupArrayList.get(i)) {
                case "duedates":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_duedates);
                    break;
                case "Job":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_jobroles_header);
                    break;
                case "Skills":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_byskills);
                    break;
                case "ContentTypes":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_contenttype);
                    break;
                case "Categories":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_caegoriestitlelabel);
                    break;
                case "progress":
                    sortModel.optionDisplayText = getLocalizationValue(JsonLocalekeys.filter_lbl_progresstitlelabel);
                    break;

            }

            sortModel.categoryID = i;

            sortModelList.add(sortModel);
        }

        return sortModelList;
    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }

}
