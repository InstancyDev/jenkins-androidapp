package com.instancy.instancylearning.advancedfilters_mylearning;

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
import android.widget.ListView;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;


/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class AllFiltersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = AllFiltersActivity.class.getSimpleName();
    ListView listView;
    List<ContentFilterByModel> contentFilterByModelList;
    List<AllFilterModel> allFilterModelList;
    AppUserModel appUserModel;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    int isFromMylearning = 0;
    AllFilterAdapter allFilterAdapter;
    Button btnApply, btnReset;
    SideMenusModel sideMenusModel;
    String contentFilterType = "";

    HashMap<String, String> responMap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_contentbyactivity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();

        preferencesManager = PreferencesManager.getInstance();

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + getLocalizationValue(JsonLocalekeys.filter_lbl_filtertitlelabel) + "</font>"));

        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        contentFilterByModelList = (List<ContentFilterByModel>) getIntent().getExtras().getSerializable("contentFilterByModelList");

        allFilterModelList = (List<AllFilterModel>) getIntent().getExtras().getSerializable("allFilterModelList");

        responMap = (HashMap<String, String>) getIntent().getExtras().getSerializable("responMap");

        contentFilterType = getIntent().getStringExtra("contentFilterType");

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
        allFilterAdapter = new AllFilterAdapter(this, allFilterModelList);
        listView.setAdapter(allFilterAdapter);
        listView.setOnItemClickListener(this);
        updateUiForSelectedFilters();
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
                finishTheActivity(true);
                break;
            case R.id.btnReset:
                finishTheActivity(false);
                break;
        }
    }

    public void finishTheActivity(boolean isApplied) {
        ApplyFilterModel applyFilterModel = new ApplyFilterModel();
        if (isApplied) {
            applyFilterModel = generateApplyFilterModel();

        } else {
            applyFilterModel = new ApplyFilterModel();
            contentFilterByModelList = new ArrayList<>();
        }

        if (allFilterModelList != null) {
            Intent intent = getIntent();
            intent.putExtra("APPLY", true);
            intent.putExtra("applyFilterModel", (Serializable) applyFilterModel);
            intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
            setResult(RESULT_OK, intent);
            finish();
        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("FILTER", false);
                int ISFROM = data.getIntExtra("ISFROM", 0);
                if (ISFROM == 0) {

                    contentFilterByModelList = (List<ContentFilterByModel>) data.getExtras().getSerializable("contentFilterByModelList");

                    updateUiForSelectedFilters();
                }

                if (ISFROM == 3) {
                    AllFilterModel allFilterModel = (AllFilterModel) data.getExtras().getSerializable("allFilterModel");
                    Log.d(TAG, "selectedCategories: " + allFilterModel.categorySelectedData);

                    if (allFilterModelList != null && allFilterModelList.size() > 0) {

                        for (int i = 0; i < allFilterModelList.size(); i++) {

                            if (allFilterModel.categoryID == allFilterModelList.get(i).categoryID) {

                                allFilterModelList.get(i).categorySelectedData = allFilterModel.categorySelectedData;
                                allFilterModelList.get(i).categorySelectedID = allFilterModel.categorySelectedID;
                                allFilterModelList.get(i).categorySelectedDataDisplay = allFilterModel.categorySelectedDataDisplay;
                                allFilterAdapter.refreshList(allFilterModelList);
                            }
                        }
                    }

                }
            }
        }
    }

    public void updateUiForSelectedFilters() {

        if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {
            String nameSelectedCats = "";
            Log.d(TAG, "onActivityResult: " + contentFilterByModelList.size());
            for (int i = 0; i < contentFilterByModelList.size(); i++) {

                if (contentFilterByModelList.get(i).selectedChildSkillIdsArry != null && contentFilterByModelList.get(i).selectedChildSkillIdsArry.size() > 0 || contentFilterByModelList.get(i).selectedSkillIdsArry != null && contentFilterByModelList.get(i).selectedSkillIdsArry.size() > 0) {

                    if (nameSelectedCats.length() > 0) {
                        nameSelectedCats = nameSelectedCats.concat(", " + contentFilterByModelList.get(i).categoryDisplayName);
                    } else {
                        nameSelectedCats = "" + contentFilterByModelList.get(i).categoryDisplayName;
                    }

                }

                if (contentFilterByModelList.get(i).categorySelectedID != -1 && contentFilterByModelList.get(i).categorySelectedID > 0) {

                    if (nameSelectedCats.length() > 0) {
                        nameSelectedCats = nameSelectedCats.concat(", " + contentFilterByModelList.get(i).categoryDisplayName);
                    } else {
                        nameSelectedCats = "" + contentFilterByModelList.get(i).categoryDisplayName;
                    }
                }
            }

            if (allFilterModelList != null && allFilterModelList.size() > 0) {
                allFilterModelList.get(0).categorySelectedData = nameSelectedCats;
                allFilterAdapter.refreshList(allFilterModelList);
            }

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        switch (allFilterModelList.get(i).categoryID) {

            case 1:
                Intent intent = new Intent(this, ContentFilterByActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("allFilterModel", (Serializable) allFilterModelList.get(i));
                intent.putExtra("isFrom", 0);
                intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
                startActivityForResult(intent, FILTER_CLOSE_CODE);
                break;
            case 3:
                Intent intents = new Intent(this, SortActivity.class);
                intents.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intents.putExtra("allFilterModel", (Serializable) allFilterModelList.get(i));
                intents.putExtra("isFrom", 3);
                intents.putExtra("contentFilterType", contentFilterType);
                intents.putExtra("responMap", (Serializable) responMap);
                startActivityForResult(intents, FILTER_CLOSE_CODE);
                break;
            case 2:
                Intent intentG = new Intent(this, SortActivity.class);
                intentG.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intentG.putExtra("allFilterModel", (Serializable) allFilterModelList.get(i));
                intentG.putExtra("responMap", (Serializable) responMap);
                intentG.putExtra("contentFilterType", contentFilterType);
                intentG.putExtra("isFrom", 3);
                startActivityForResult(intentG, FILTER_CLOSE_CODE);
                break;
        }
    }

    public ApplyFilterModel generateApplyFilterModel() {

        ApplyFilterModel applyFilterModel = new ApplyFilterModel();

        for (int i = 0; i < contentFilterByModelList.size(); i++) {
            applyFilterModel.filterApplied = true;
            switch (contentFilterByModelList.get(i).categoryID) {
                case "cat":
                    applyFilterModel.categories = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "skills":
                    applyFilterModel.skillCats = generateSelectedCategoriesCatsAndSkills(contentFilterByModelList.get(i), 1);
                    applyFilterModel.skills = generateSelectedCategoriesCatsAndSkills(contentFilterByModelList.get(i), 2);
                    break;
                case "bytype":
                    applyFilterModel.objectTypes = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "jobroles":
                    applyFilterModel.jobRoles = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "tag":
                    applyFilterModel.solutions = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "inst":
                    applyFilterModel.instructors = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "rate":
                    applyFilterModel.ratings = generateSelectedCategories(contentFilterByModelList.get(i));
                    break;
                case "eventdates": //IDS
                    applyFilterModel.firstName = contentFilterByModelList.get(i).categorySelectedStartDate.length() > 0 ? contentFilterByModelList.get(i).categorySelectedStartDate : contentFilterByModelList.get(i).selectedSkillsCatIdString;
                    applyFilterModel.lastName = contentFilterByModelList.get(i).categorySelectedEndDate;
                    break;
            }

            if (allFilterModelList.size() > 0) {

                for (int k = 0; k < allFilterModelList.size(); k++) {

                    if (allFilterModelList.get(k).categoryID == 3) {
                        applyFilterModel.sortBy = allFilterModelList.get(k).categorySelectedData;
                        applyFilterModel.sortByDisplay = allFilterModelList.get(k).categorySelectedDataDisplay;

                    }

                    if (allFilterModelList.get(k).categoryID == 2) {
                        applyFilterModel.groupBy = allFilterModelList.get(k).categorySelectedData;

                    }
                    applyFilterModel.selectedId = allFilterModelList.get(k).categorySelectedID;
                }
                applyFilterModel.filterApplied = true;
            }

        }

        return applyFilterModel;
    }

    public String generateSelectedCategoriesCatsAndSkills(ContentFilterByModel contentFilterByModel, int typeFilter) {
        String generatedStr = "";


        if (typeFilter == 1) {

            if (contentFilterByModel.selectedSkillIdsArry == null || contentFilterByModel.selectedSkillIdsArry.size() == 0)
                return generatedStr;

            for (int i = 0; i < contentFilterByModel.selectedSkillIdsArry.size(); i++) {
                if (generatedStr.length() > 0) {
                    generatedStr = generatedStr.concat("," + contentFilterByModel.selectedSkillIdsArry.get(i));
                } else {
                    generatedStr = "" + contentFilterByModel.selectedSkillIdsArry.get(i);
                }
            }
        }


        if (typeFilter == 2) {

            if (contentFilterByModel.selectedChildSkillIdsArry == null || contentFilterByModel.selectedChildSkillIdsArry.size() == 0)
                return generatedStr;

            generatedStr = "";
            for (int i = 0; i < contentFilterByModel.selectedChildSkillIdsArry.size(); i++) {
                if (generatedStr.length() > 0) {
                    generatedStr = generatedStr.concat("," + contentFilterByModel.selectedChildSkillIdsArry.get(i));
                } else {
                    generatedStr = "" + contentFilterByModel.selectedChildSkillIdsArry.get(i);
                }

            }
        }
        return generatedStr;
    }


    public String generateSelectedCategories(ContentFilterByModel contentFilterByModel) {
        String generatedStr = "";

        if (contentFilterByModel.selectedSkillIdsArry == null || contentFilterByModel.selectedSkillIdsArry.size() == 0)
            return "";

        for (int i = 0; i < contentFilterByModel.selectedSkillIdsArry.size(); i++) {
            if (generatedStr.length() > 0) {
                generatedStr = generatedStr.concat("," + contentFilterByModel.selectedSkillIdsArry.get(i));
            } else {
                generatedStr = "" + contentFilterByModel.selectedSkillIdsArry.get(i);
            }

        }

        if (contentFilterByModel.selectedChildSkillIdsArry == null || contentFilterByModel.selectedChildSkillIdsArry.size() == 0)
            return generatedStr;

        for (int i = 0; i < contentFilterByModel.selectedChildSkillIdsArry.size(); i++) {
            if (generatedStr.length() > 0) {
                generatedStr = generatedStr.concat("," + contentFilterByModel.selectedChildSkillIdsArry.get(i));
            } else {
                generatedStr = "" + contentFilterByModel.selectedChildSkillIdsArry.get(i);
            }

        }

        return generatedStr;
    }


//    public void getSelectedCategories() {
//        String selectedSkillsIds = "";
//        String selectedSkillsNames = "";
//        List<ContentValues> selectedCategories = new ArrayList<ContentValues>();
//
//        if (filtersParents != null && filtersParents.size() > 0) {
//
//            for (int i = 0; i < filtersParents.size(); i++) {
//
//                if (filtersParents.get(i).isSelected) {
//
//                    if (selectedSkillsIds.length() > 0) {
//                        selectedSkillsIds = selectedSkillsIds.concat("," + filtersParents.get(i).categoryID);
//                    } else {
//                        selectedSkillsIds = "" + filtersParents.get(i).categoryID;
//                    }
//
//                    if (selectedSkillsNames.length() > 0) {
//                        selectedSkillsNames = selectedSkillsNames.concat("," + filtersParents.get(i).categoryName);
//                    } else {
//                        selectedSkillsNames = "" + filtersParents.get(i).categoryName;
//                    }
//
//                }
//            }
//
//        }
//
//        if (contentFilterByModel.selectedSkillsCatIdString != null && contentFilterByModel.selectedSkillsCatIdString.length() > 0) {
//            contentFilterByModel.selectedSkillsCatIdString = contentFilterByModel.selectedSkillsCatIdString.concat("," + selectedSkillsIds);
//            contentFilterByModel.selectedSkillsNameString = contentFilterByModel.selectedSkillsNameString.concat("," + selectedSkillsNames);
//        } else {
//            contentFilterByModel.selectedSkillsCatIdString = selectedSkillsIds;
//            contentFilterByModel.selectedSkillsNameString = selectedSkillsNames;
//        }
//
//
//    }


}
