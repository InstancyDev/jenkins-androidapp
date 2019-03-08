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
import static com.instancy.instancylearning.utils.StaticValues.INNER_FILTER_CLOSE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class SelectedActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = SelectedActivity.class.getSimpleName();
    ListView listView;
    List<FilterByModel> filterByModelList;
    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    int isFromMylearning = 0;

    FilterByAdapter filterByAdapter;

    Button btnApply, btnReset;

    ContentFilterByModel contentFilterByModel;

    List<String> previousSelectedArrayList = new ArrayList<>();

    //    https://github.com/shineM/TreeView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_contentbyactivity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        filterByModelList = new ArrayList<>();
        filterByModelList = (List<FilterByModel>) getIntent().getExtras().getSerializable("filterByModelList");
        contentFilterByModel = (ContentFilterByModel) getIntent().getExtras().getSerializable("contentFilterByModel");

        String titleHeader = getIntent().getStringExtra("TITLEHEADER");

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));

        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + titleHeader + "</font>"));

        applyUiColor();

        try {

            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.lstContentFilterBy);
        filterByAdapter = new FilterByAdapter(this, filterByModelList, contentFilterByModel);
        listView.setAdapter(filterByAdapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(this);

        updateSelectedArrayList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Log.d("DEBUG", "onOptionsItemSelected: ");
                finish();
//                finishTheActivity();
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

    public void updateSelectedArrayList() {

        List<String> selectedValuesAry = contentFilterByModel.selectedChildSkillIdsArry;

        if (selectedValuesAry != null && selectedValuesAry.size() > 0) {
//            previousSelectedArrayList = selectedValuesAry;
            for (int i = 0; i < selectedValuesAry.size(); i++) {

                for (int j = 0; j < filterByModelList.size(); j++) {

                    if (selectedValuesAry.get(i).equalsIgnoreCase("" + filterByModelList.get(j).categoryID)) {
                        filterByModelList.get(j).isSelected = true;
                        previousSelectedArrayList.add(selectedValuesAry.get(i));

                    } else {

                    }
                }

            }
            filterByAdapter.refreshList(filterByModelList);

        }

    }

    public void resetArrayList() {

        contentFilterByModel.selectedChildSkillIdsArry = addAllValues(resetBtnForList());

    }


    public List<String> resetBtnForList() {

        List<String> resetTheValues = new ArrayList<>();

        resetTheValues = addAllValues(contentFilterByModel.selectedChildSkillIdsArry);

        if (contentFilterByModel.selectedChildSkillIdsArry != null && contentFilterByModel.selectedChildSkillIdsArry.size() > 0) {

            if (previousSelectedArrayList != null && previousSelectedArrayList.size() > 0) {

                for (int i = 0; i < previousSelectedArrayList.size(); i++) {

                    for (int k = 0; k < contentFilterByModel.selectedChildSkillIdsArry.size(); k++) {

                        if (previousSelectedArrayList.get(i).equalsIgnoreCase(contentFilterByModel.selectedChildSkillIdsArry.get(k))) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");

    }

    public void getSelectedCategoriesValues() {

        if (filterByModelList != null && filterByModelList.size() > 0) {
            contentFilterByModel.selectedChildSkillIdsArry = new ArrayList<>();
            contentFilterByModel.selectedChildSkillNamesArry = new ArrayList<>();
            for (int i = 0; i < filterByModelList.size(); i++) {

                if (filterByModelList.get(i).isSelected) {

                    contentFilterByModel.selectedChildSkillIdsArry.add("" + filterByModelList.get(i).categoryID);
                    contentFilterByModel.selectedChildSkillNamesArry.add("" + filterByModelList.get(i).categoryName);
                }
            }

            contentFilterByModel.selectedChildSkillIdsArry = removeAllDuplicates(contentFilterByModel.selectedChildSkillIdsArry);

            contentFilterByModel.selectedChildSkillNamesArry = removeAllDuplicates(contentFilterByModel.selectedChildSkillNamesArry);

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


    public void getSelectedCategories() {
        String selectedSkillsIds = "";
        String selectedSkillsNames = "";

        if (filterByModelList != null && filterByModelList.size() > 0) {

            for (int i = 0; i < filterByModelList.size(); i++) {

                if (filterByModelList.get(i).isSelected) {

                    if (selectedSkillsIds.length() > 0) {
                        selectedSkillsIds = selectedSkillsIds.concat("," + filterByModelList.get(i).categoryID);
                    } else {
                        selectedSkillsIds =  filterByModelList.get(i).categoryID;
                    }

                    if (selectedSkillsNames.length() > 0) {
                        selectedSkillsNames = selectedSkillsNames.concat("," + filterByModelList.get(i).categoryName);
                    } else {
                        selectedSkillsNames =  filterByModelList.get(i).categoryName;
                    }

                }
            }

        }

        contentFilterByModel.selectedSkillsCatIdString = selectedSkillsIds;
        contentFilterByModel.selectedSkillsNameString = selectedSkillsNames;
    }


    public String generateCategoryIds(List<ContentValues> breadcrumbItemsList) {
        String selectedSkills = "";

        if (breadcrumbItemsList != null && breadcrumbItemsList.size() > 0) {

            for (int s = 0; s < breadcrumbItemsList.size(); s++) {
                if (selectedSkills.length() > 0) {
                    selectedSkills = selectedSkills.concat("," + breadcrumbItemsList.get(s).getAsString("categoryid"));
                } else {
                    selectedSkills = breadcrumbItemsList.get(s).getAsString("categoryid");
                }

                Log.d(TAG, "generateSkills: " + selectedSkills);

            }

        } else {
            selectedSkills = "";
        }

        return selectedSkills;
    }


    public void finishTheActivity(boolean isApply) {

//        getSelectedCategories();

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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (view.getId()) {
            case R.id.chxCategoryDisplayName:
                refreshCheckedItem(view, i);
                break;
            case R.id.txtCategoryDisplayName:

                break;
        }

    }

    public void refreshCheckedItem(View view, int i) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxCategoryDisplayName);
        if (checkBox.isChecked()) {
            filterByModelList.get(i).isSelected = true;
        } else {
            filterByModelList.get(i).isSelected = false;
        }
        filterByAdapter.refreshList(filterByModelList);

    }

}
