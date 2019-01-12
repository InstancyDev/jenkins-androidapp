package com.instancy.instancylearning.advancedfilters_mylearning;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class DateSelectionRadioActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DateSelectionRadioActivity.class.getSimpleName();

    RadioGroup radioGroup;

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ContentFilterByModel contentFilterByModel;

    int isFromMylearning = 0;

    Button btnApply, btnReset, btnStartDate, btnEndDate;

    SideMenusModel sideMenusModel;

    SVProgressHUD svProgressHUD;

    VollyService vollyService;
    IResult resultCallback = null;

    List<SortModel> sortModelList;

    LinearLayout linearDateLayout;
    //    https://github.com/shineM/TreeView

    private DatePickerDialog datePickerDialog;

    private Date startDate, endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_date_activity);
        appUserModel = AppUserModel.getInstance();
        startDate = new Date();
        endDate = new Date();
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
        linearDateLayout = (LinearLayout) findViewById(R.id.linearDateLayout);

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


        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnStartDate.setOnClickListener(this);

        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnEndDate.setOnClickListener(this);

        btnStartDate.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        btnEndDate.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnApply:
                finishTheActivity(true);
                break;
            case R.id.btnReset:
                finishTheActivity(false);
                break;
            case R.id.btnStartDate:
                selectTheDate(true);
                break;
            case R.id.btnEndDate:
                selectTheDate(false);
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
            if (contentFilterByModel.categorySelectedID == 7) {
                if (contentFilterByModel.categorySelectedStartDate.length() == 0) {
                    Toast.makeText(this, "Select Start Date", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (startDate.after(endDate)) {
                Toast.makeText(this, "Start date not after End Date", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra("contentFilterByModel", (Serializable) contentFilterByModel);
            intent.putExtra("ISFROM", 3);
            intent.putExtra("FILTER", true);
            setResult(RESULT_OK, intent);
            finish();

        } else {

            Toast.makeText(this, " select atleast one category", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectTheDate(final boolean isStartClicked) {

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (isStartClicked) {
                    btnStartDate.setText(dateFormatter.format(newDate.getTime()));
                    startDate = newDate.getTime();
                } else {
                    btnEndDate.setText(dateFormatter.format(newDate.getTime()));
                    endDate = newDate.getTime();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

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

                if (sortModelList.get(i).categoryID == 7) {
                    java.text.SimpleDateFormat Dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    contentFilterByModel.categorySelectedStartDate = Dateformat.format(startDate);
                    contentFilterByModel.categorySelectedEndDate = Dateformat.format(endDate);
                } else {
//                    contentFilterByModel.categorySelectedStartDate = "";
//                    contentFilterByModel.categorySelectedEndDate = "";
                }

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
                    if (sortModelList.get(k).categoryID == 7) {
                        linearDateLayout.setVisibility(View.VISIBLE);
                    } else {
                        linearDateLayout.setVisibility(View.GONE);
                    }
                } else {
                    sortModelList.get(k).isSelected = false;
                }

            }

        }

    }


    public List<SortModel> generateFilterByModelList() {
        List<SortModel> sortModelList = new ArrayList<>();

//        let mainFilterType = [
//        "Today", "Tomorrow", "This Week", "Next Week", "This Month", "Choose Date"]
//
//
//        {"id":'today',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateToday')},
//        {"id":'tomorrow',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateTomorrow')},
//        {"id":'thisweek',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateThisWeek')},
//        {"id":'nextweek',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateNextWeek')},
//        {"id":'thismonth',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateThisMonth')},
//        {"id":'nextmonth',"itemName":this.Commonutilites.GetLocalizationString('Filter_EventDateNextMonth')},

        SortModel sortModelToday = new SortModel();
        sortModelToday.optionText = "Today";
        sortModelToday.optionValue = "today";
        sortModelToday.categoryID = 1;
        sortModelList.add(sortModelToday);

        SortModel sortModelTomorrow = new SortModel();
        sortModelTomorrow.optionText = "Tomorrow";
        sortModelTomorrow.optionValue = "tomorrow";
        sortModelTomorrow.categoryID = 2;
        sortModelList.add(sortModelTomorrow);

        SortModel sortModelThisWeek = new SortModel();
        sortModelThisWeek.optionText = "This Week";
        sortModelThisWeek.optionValue = "thisweek";
        sortModelThisWeek.categoryID = 3;
        sortModelList.add(sortModelThisWeek);


        SortModel sortModelNextweek = new SortModel();
        sortModelNextweek.optionText = "Next Week";
        sortModelNextweek.optionValue = "nextweek";
        sortModelNextweek.categoryID = 4;
        sortModelList.add(sortModelNextweek);


        SortModel sortModelThismonth = new SortModel();
        sortModelThismonth.optionText = "This Month";
        sortModelThismonth.optionValue = "thismonth";
        sortModelThismonth.categoryID = 5;
        sortModelList.add(sortModelThismonth);

        SortModel sortModelNextmonth = new SortModel();
        sortModelNextmonth.optionText = "This Month";
        sortModelNextmonth.optionValue = "nextmonth";
        sortModelNextmonth.categoryID = 6;
        sortModelList.add(sortModelNextmonth);


        SortModel sortModelChooseDate = new SortModel();
        sortModelChooseDate.optionText = "Choose Date";
        sortModelChooseDate.optionValue = "choosedate";
        sortModelChooseDate.categoryID = 7;
        sortModelList.add(sortModelChooseDate);


        return sortModelList;
    }

    public void dynamicRadioBtn(final List<SortModel> sortModelList) {

        if (sortModelList != null && sortModelList.size() > 0) {
            for (int i = 0; i < sortModelList.size(); i++) {
                RadioButton rbn = new RadioButton(this);
                rbn.setId(sortModelList.get(i).categoryID);
                rbn.setText(sortModelList.get(i).optionText);
                rbn.setTextSize(16.0f);
                rbn.setPadding(4, 18, 4, 18);

                rbn.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
                if (contentFilterByModel.categorySelectedID == sortModelList.get(i).categoryID) {
                    rbn.setChecked(true);

                    if (contentFilterByModel.categorySelectedID == 7) {
                        linearDateLayout.setVisibility(View.VISIBLE);
                    } else {
                        linearDateLayout.setVisibility(View.GONE);
                    }

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
