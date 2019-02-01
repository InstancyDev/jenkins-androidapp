package com.instancy.instancylearning.filter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.NativeSetttingsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class Filter_Inner_activity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = Filter_Inner_activity.class.getSimpleName();
    ListView listView;
    Filter_Inner_Adapter filter_inner_adapter;
    List<NativeSetttingsModel.FilterInnerModel> filterInnerModelList;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    AppController appcontroller;
    Button btnApply;
    String filterName = "",
            categoryId = "",
            categoryName = "";
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,Filter_Inner_activity.this);

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_inner_activity);
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(this);
        db = new DatabaseHandler(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        appcontroller = AppController.getInstance();
        preferencesManager = PreferencesManager.getInstance();
        filterInnerModelList = new ArrayList<NativeSetttingsModel.FilterInnerModel>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            NativeSetttingsModel.FilterModel filterModel = (NativeSetttingsModel.FilterModel) bundle.getSerializable("filtermodel");
            filterName = bundle.getString("filtername");
            assert filterModel != null;
            filterInnerModelList = filterModel.filterInnerModels;
        }

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>"+getLocalizationValue(JsonLocalekeys.advancefilter_header_filtertitlelabel)+"</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            applyUiColor(uiSettingsModel);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.filter_inner_list);

        filter_inner_adapter = new Filter_Inner_Adapter(this, BIND_ABOVE_CLIENT, filterInnerModelList);
        listView.setAdapter(filter_inner_adapter);
        listView.setOnItemClickListener(this);
    }

    public void applyUiColor(UiSettingsModel uiSettingsModel) {

        btnApply = (Button) findViewById(R.id.btninnerapply);
        btnApply.setOnClickListener(this);

        btnApply.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        filterInnerModelList.get(position).isSelected = true;
        filter_inner_adapter = new Filter_Inner_Adapter(this, BIND_ABOVE_CLIENT, filterInnerModelList);
        listView.setAdapter(filter_inner_adapter);
        categoryId = filterInnerModelList.get(position).id;
        categoryName = filterInnerModelList.get(position).name;
    }

    @Override
    public void onClick(View v) {
        insertBundleValues();
    }

    public void insertBundleValues() {

        Intent intent = getIntent();
        intent.putExtra("filtername", filterName);
        intent.putExtra("categoryid", categoryId);
        intent.putExtra("groupname", categoryName);
        setResult(RESULT_OK, intent);
        finish();
    }

}
