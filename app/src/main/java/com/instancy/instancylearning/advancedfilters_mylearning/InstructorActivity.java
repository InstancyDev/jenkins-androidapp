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
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class InstructorActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = InstructorActivity.class.getSimpleName();
    ListView listView;
    List<InstructorModel> instructorModelList;
    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    ContentFilterByModel contentFilterByModel;

    int isFromMylearning = 0;

    InstructorAdapter instructorAdapter;

    Button btnApply, btnReset;
    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;
    IResult resultCallback = null;


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

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + contentFilterByModel.categoryName + "</font>"));
        applyUiColor();
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

        instructorModelList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.lstContentFilterBy);
        instructorAdapter = new InstructorAdapter(this, instructorModelList, true);
        listView.setAdapter(instructorAdapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(this);
        getIntructorList();

    }

    public void getIntructorList() {

        if (isNetworkConnectionAvailable(this, -1)) {


            String urlStr = "instSiteID=" + appUserModel.getSiteIDValue() + "&instUserID=" + appUserModel.getUserIDValue();

            vollyService.getStringResponseVolley("INSTRCT", appUserModel.getWebAPIUrl() + "/catalog/GetInstructorListForFilter?" + urlStr, appUserModel.getAuthHeaders());

        } else {

            Toast.makeText(this, getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();

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
            contentFilterByModel.selectedSkillIdsArry = new ArrayList<>();
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

        if (instructorModelList != null && instructorModelList.size() > 0) {
            contentFilterByModel.selectedSkillIdsArry = new ArrayList<>();
            contentFilterByModel.selectedSkillNamesArry = new ArrayList<>();
            for (int i = 0; i < instructorModelList.size(); i++) {

                if (instructorModelList.get(i).isSelected) {

                    contentFilterByModel.selectedSkillIdsArry.add("" + instructorModelList.get(i).userId);
                    contentFilterByModel.selectedSkillNamesArry.add("" + instructorModelList.get(i).userName);

                }
            }
        }
    }


    public void updateSelectedArrayList(List<String> selectedValues) {

        if (selectedValues != null && selectedValues.size() > 0) {

            for (int i = 0; i < selectedValues.size(); i++) {

                for (int j = 0; j < instructorModelList.size(); j++) {

                    if (selectedValues.get(i).equalsIgnoreCase("" + instructorModelList.get(j).userId)) {

                        instructorModelList.get(j).isSelected = true;

                    }

                }

            }
            instructorAdapter.refreshList(instructorModelList);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");
        if (requestCode == FILTER_CLOSE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                boolean refresh = data.getBooleanExtra("INSTRCT", false);
                if (refresh) {

                    contentFilterByModel = (ContentFilterByModel) data.getExtras().getSerializable("contentFilterByModel");
                    Log.d(TAG, "selectedCategories: " + contentFilterByModel.selectedSkillsNameString);


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
                if (requestType.equalsIgnoreCase("INSTRCT")) {
                    if (response != null) {
                        Log.d(TAG, "notifySuccess: " + response);
                        try {
                            instructorModelList = generateFilterByModelList(response);
                            instructorAdapter.refreshList(instructorModelList);
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

    public List<InstructorModel> generateFilterByModelList(String responseStr) throws JSONException {
        List<InstructorModel> instructorModels = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(responseStr);
        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        for (int i = 0; i < jsonTableAry.length(); i++) {
            InstructorModel instructorModel = new InstructorModel();
            JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

            instructorModel.userId = jsonColumnObj.optInt("UserID");
            instructorModel.userName = jsonColumnObj.optString("UserName");

            instructorModels.add(instructorModel);
        }

        return instructorModels;
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
            instructorModelList.get(i).isSelected = true;
        } else {
            instructorModelList.get(i).isSelected = false;
        }
        instructorAdapter.refreshList(instructorModelList);

    }

}
