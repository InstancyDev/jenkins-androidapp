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

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.getDrawableForStars;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class RatingRadioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RatingRadioActivity.class.getSimpleName();

    RadioGroup radioGroup;

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ContentFilterByModel contentFilterByModel;

    int isFromMylearning = 0;

    Button btnApply, btnReset;
    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;
    IResult resultCallback = null;

    List<SortModel> sortModelList;


    //    https://github.com/shineM/TreeView

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_radio_activity);
        appUserModel = AppUserModel.getInstance();

        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        svProgressHUD = new SVProgressHUD(this);

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
        sortModelList = new ArrayList<>();

        radioGroup = (RadioGroup) findViewById(R.id.radioGrp);
        radioGroup.setVisibility(View.VISIBLE);

        sortModelList = generateFilterByModelList();
        dynamicRadioBtn(sortModelList);
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

    public void finishTheActivity(boolean isApplied) {

        if (isApplied) {
            getSelectedCategories();
        } else {
            resetAllSortings();
        }

        if (contentFilterByModel != null) {
            Intent intent = getIntent();

            intent.putExtra("contentFilterByModel", (Serializable) contentFilterByModel);
            intent.putExtra("ISFROM", 3);
            intent.putExtra("FILTER", true);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    public void resetAllSortings() {
        if (sortModelList != null && sortModelList.size() > 0) {

            contentFilterByModel.selectedSkillsCatIdString = "";
            contentFilterByModel.categorySelectedID = -1;
        }
    }


    public void getSelectedCategories() {

        for (int i = 0; i < sortModelList.size(); i++) {

            if (sortModelList.get(i).isSelected) {

                contentFilterByModel.selectedSkillsCatIdString = sortModelList.get(i).optionText;
                contentFilterByModel.categorySelectedID = sortModelList.get(i).categoryID;

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");

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


    public List<SortModel> generateFilterByModelList() {
        List<SortModel> sortModelList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            SortModel sortModel = new SortModel();

            sortModel.optionText = i + 1.5 + " and Up";
            sortModel.optionValue = i + 1.5 + "";
            sortModel.categoryID = i;

            try {
                sortModel.ratingValue = Integer.valueOf(sortModel.optionValue);
            } catch (NumberFormatException exp) {
                exp.printStackTrace();
            }
            sortModelList.add(sortModel);
        }


        return sortModelList;
    }

    public void dynamicRadioBtn(final List<SortModel> sortModelList) {

        if (sortModelList != null && sortModelList.size() > 0) {
            for (int i = 0; i < sortModelList.size(); i++) {
                RadioButton rbn = new RadioButton(this);
                rbn.setId(sortModelList.get(i).categoryID);
                rbn.setText(sortModelList.get(i).optionText);
                rbn.setTextSize(14.0f);
                rbn.setPadding(4, 18, 4, 18);
                rbn.setCompoundDrawablesWithIntrinsicBounds(getDrawableForStars((float) 1.5, this), null, null, null);

                rbn.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
                if (contentFilterByModel.categorySelectedID == sortModelList.get(i).categoryID) {
                    rbn.setChecked(true);
                }
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

}
