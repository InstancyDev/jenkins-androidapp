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

import com.instancy.instancylearning.models.AppUserModel;

import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import com.instancy.instancylearning.utils.PreferencesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.FILTER_CLOSE_CODE;


/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class ContentFilterByActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = ContentFilterByActivity.class.getSimpleName();
    ListView listView;
    List<ContentFilterByModel> contentFilterByModelList;
    AppUserModel appUserModel;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    int isFromMylearning = 0;
    ContentFilterAdapter contentFilterAdapter;
    Button btnApply, btnReset;
    SideMenusModel sideMenusModel;
    AllFilterModel allFilterModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_contentbyactivity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();

        preferencesManager = PreferencesManager.getInstance();


        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        contentFilterByModelList = (List<ContentFilterByModel>) getIntent().getExtras().getSerializable("contentFilterByModelList");

        allFilterModel = (AllFilterModel) getIntent().getExtras().getSerializable("allFilterModel");


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

        listView = (ListView) findViewById(R.id.lstContentFilterBy);
        contentFilterAdapter = new ContentFilterAdapter(this, contentFilterByModelList);
        listView.setAdapter(contentFilterAdapter);
        listView.setOnItemClickListener(this);
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

    public void resetArrayList() {


        if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {
            for (int i = 0; i < contentFilterByModelList.size(); i++) {

                contentFilterByModelList.get(i).selectedChildSkillIdsArry = new ArrayList<>();
                contentFilterByModelList.get(i).selectedSkillIdsArry = new ArrayList<>();
                contentFilterByModelList.get(i).selectedSkillsCatIdString = "";

            }
        }

    }

    public void finishTheActivity(boolean isApply) {

        if (isApply) {

        } else {
            resetArrayList();
        }

        if (contentFilterByModelList != null) {
            Intent intent = getIntent();
            intent.putExtra("contentFilterByModelList", (Serializable) contentFilterByModelList);
            intent.putExtra("FILTER", true);
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
                if (refresh) {

                    ContentFilterByModel contentFilterByModel = (ContentFilterByModel) data.getExtras().getSerializable("contentFilterByModel");

                    Log.d(TAG, "selectedCategories: " + contentFilterByModel.selectedSkillsNameString);

                    if (contentFilterByModelList != null && contentFilterByModelList.size() > 0) {

                        for (int i = 0; i < contentFilterByModelList.size(); i++) {

                            if (contentFilterByModel.categoryID.equalsIgnoreCase(contentFilterByModelList.get(i).categoryID)) {

                                contentFilterByModelList.get(i).categorySelectedID = contentFilterByModel.categorySelectedID;
                                contentFilterByModelList.get(i).selectedSkillIdsArry = contentFilterByModel.selectedSkillIdsArry;
                                contentFilterByModelList.get(i).selectedSkillNamesArry = contentFilterByModel.selectedSkillNamesArry;
                                contentFilterByModelList.get(i).categorySelectedStartDate = contentFilterByModel.categorySelectedStartDate;
                                contentFilterByModelList.get(i).categorySelectedEndDate = contentFilterByModel.categorySelectedEndDate;

                                if (contentFilterByModel.selectedChildSkillIdsArry != null && contentFilterByModel.selectedChildSkillIdsArry.size() > 0) {

                                    contentFilterByModelList.get(i).selectedChildSkillIdsArry = contentFilterByModel.selectedChildSkillIdsArry;
                                    contentFilterByModelList.get(i).selectedChildSkillIdsArry = contentFilterByModel.selectedChildSkillIdsArry;

                                }

                                contentFilterAdapter.refreshList(contentFilterByModelList);
                            }

                        }

                    }

                }

            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        switch (contentFilterByModelList.get(i).categoryID) {

            case "cat":
            case "skills":
            case "bytype":
            case "jobroles":
                Intent intent = new Intent(this, FilterBySelectedCategoryActivity.class);
                intent.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intent.putExtra("isFrom", 0);
                intent.putExtra("contentFilterByModel", (Serializable) contentFilterByModelList.get(i));
                startActivityForResult(intent, FILTER_CLOSE_CODE);
                break;
            case "inst":
                Intent intents = new Intent(this, InstructorActivity.class);
                intents.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intents.putExtra("isFrom", 0);
                intents.putExtra("contentFilterByModel", (Serializable) contentFilterByModelList.get(i));
                startActivityForResult(intents, FILTER_CLOSE_CODE);
                break;
            case "rate":
                Intent intentR = new Intent(this, RatingRadioActivity.class);
                intentR.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intentR.putExtra("isFrom", 0);
                intentR.putExtra("contentFilterByModel", (Serializable) contentFilterByModelList.get(i));
                startActivityForResult(intentR, FILTER_CLOSE_CODE);
                break;
            case "priceRange":
            case "duration":
                Intent intentP = new Intent(this, DurationPriceActivity.class);
                intentP.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intentP.putExtra("isFrom", 0);
                intentP.putExtra("contentFilterByModel", (Serializable) contentFilterByModelList.get(i));
                startActivityForResult(intentP, FILTER_CLOSE_CODE);
                break;
            case "eventdates":
                Intent intentD = new Intent(this, DateSelectionRadioActivity.class);
                intentD.putExtra("sideMenusModel", (Serializable) sideMenusModel);
                intentD.putExtra("isFrom", 0);
                intentD.putExtra("contentFilterByModel", (Serializable) contentFilterByModelList.get(i));
                startActivityForResult(intentD, FILTER_CLOSE_CODE);
                break;

        }
    }
}
