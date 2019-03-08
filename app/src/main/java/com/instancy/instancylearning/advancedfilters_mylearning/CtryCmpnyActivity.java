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
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class CtryCmpnyActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = CtryCmpnyActivity.class.getSimpleName();

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ContentFilterByModel contentFilterByModel;

    List<CtryCmpnyModel> ctryCmpnyModelList;

    int isForLocations = 0;

    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;
    IResult resultCallback = null;

    CtryCmpnyAdapter ctryCmpnyAdapter;

    @BindView(R.id.lstContentFilterBy)
    ListView listView;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.btnReset)
    Button btnReset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_contentbyactivity);
        appUserModel = AppUserModel.getInstance();
        ButterKnife.bind(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        svProgressHUD = new SVProgressHUD(this);
        initVolleyCallback();
        vollyService = new VollyService(resultCallback, this);

        isForLocations = getIntent().getIntExtra("isFrom", 0);

        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        contentFilterByModel = (ContentFilterByModel) getIntent().getExtras().getSerializable("contentFilterByModel");
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
        ctryCmpnyModelList = new ArrayList<>();
        ctryCmpnyAdapter = new CtryCmpnyAdapter(this, ctryCmpnyModelList, true);
        listView.setAdapter(ctryCmpnyAdapter);
        listView.setOnItemClickListener(this);

        getPeoplePrimaryFilterData();
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
        btnApply.setOnClickListener(this);
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
                finishTheActivity();
                break;
            case R.id.btnReset:
                finish();
                break;
        }
    }

    public void finishTheActivity() {

        getSelectedCategoriesValues();

        if (contentFilterByModel != null) {

            Intent intent = getIntent();
            intent.putExtra("allFilterModel", (Serializable) contentFilterByModel);
            intent.putExtra("ISFROM", 3);
            intent.putExtra("FILTER", true);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    public void getPeoplePrimaryFilterData() {

        if (isNetworkConnectionAvailable(this, -1)) {

            vollyService.getStringResponseVolley("getPeoplePrimaryFilterData", appUserModel.getWebAPIUrl() + "PeopleListing/GetPeoplePrimaryFilterData?LocalID=en-us", appUserModel.getAuthHeaders());

        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

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
                if (requestType.equalsIgnoreCase("getPeoplePrimaryFilterData")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);

                        try {
                            ctryCmpnyModelList = generateModelList(response);
                            ctryCmpnyAdapter.refreshList(ctryCmpnyModelList);
                            updateSelectedArrayList(contentFilterByModel.selectedSkillIdsArry);
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

    public List<CtryCmpnyModel> generateModelList(String responseStr) throws JSONException {
        List<CtryCmpnyModel> ctryCmpnyModelArrayList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(responseStr);

        if (isForLocations == 2) {

            JSONArray jsonTableAry = jsonObject.getJSONArray("org");

            for (int i = 0; i < jsonTableAry.length(); i++) {
                CtryCmpnyModel cmpnyModel = new CtryCmpnyModel();
                JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);
                cmpnyModel.categoryId = i;
                cmpnyModel.categoryName = jsonColumnObj.optString("Organization");
                cmpnyModel.isSelected = false;
                ctryCmpnyModelArrayList.add(cmpnyModel);
            }
        } else {
            JSONArray jsonTableAry = jsonObject.getJSONArray("country");

            for (int i = 0; i < jsonTableAry.length(); i++) {
                CtryCmpnyModel cmpnyModel = new CtryCmpnyModel();
                JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

                cmpnyModel.categoryId = jsonColumnObj.optInt("ChoiceID");
                cmpnyModel.categoryName = jsonColumnObj.optString("ChoiceValue");
                cmpnyModel.isSelected = false;

                ctryCmpnyModelArrayList.add(cmpnyModel);
            }
        }


        return ctryCmpnyModelArrayList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (view.getId()) {
            case R.id.chxCategoryDisplayName:
                refreshCheckedItem(view, i);
                break;
        }


    }

    public void refreshCheckedItem(View view, int i) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxCategoryDisplayName);
        if (checkBox.isChecked()) {
            ctryCmpnyModelList.get(i).isSelected = true;
        } else {
            ctryCmpnyModelList.get(i).isSelected = false;
        }
        ctryCmpnyAdapter.refreshList(ctryCmpnyModelList);
    }


    public void getSelectedCategoriesValues() {

        if (ctryCmpnyModelList != null && ctryCmpnyModelList.size() > 0) {

            contentFilterByModel.selectedSkillIdsArry = new ArrayList<>();
            contentFilterByModel.selectedSkillNamesArry = new ArrayList<>();

            for (int i = 0; i < ctryCmpnyModelList.size(); i++) {

                if (ctryCmpnyModelList.get(i).isSelected) {

                    contentFilterByModel.selectedSkillIdsArry.add("" + ctryCmpnyModelList.get(i).categoryId);
                    contentFilterByModel.selectedSkillNamesArry.add("" + ctryCmpnyModelList.get(i).categoryName);

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



    public void updateSelectedArrayList(List<String> selectedValues) {

        if (selectedValues != null && selectedValues.size() > 0) {

            for (int i = 0; i < selectedValues.size(); i++) {

                for (int j = 0; j < ctryCmpnyModelList.size(); j++) {

                    if (selectedValues.get(i).equalsIgnoreCase("" + ctryCmpnyModelList.get(j).categoryId)) {

                        ctryCmpnyModelList.get(j).isSelected = true;

                    }

                }

            }
            ctryCmpnyAdapter.refreshList(ctryCmpnyModelList);
        }
    }

}
