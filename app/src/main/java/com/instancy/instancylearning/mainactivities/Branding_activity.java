package com.instancy.instancylearning.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.SlideAdapters;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Upendranath on 5/11/2017.
 */

public class Branding_activity extends Activity {

    ArrayList<String> imagesArray;
    public static final String TAG = Branding_activity.class
            .getSimpleName();

    private static ViewPager mPager;
    private static int currentPage = 0;
    UiSettingsModel uiSettingsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branding_activity);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        imagesArray = new ArrayList<String>();
        if (extras != null) {

            imagesArray = extras.getStringArrayList("slideimages");
        }
        uiSettingsModel = UiSettingsModel.getInstance();
        assert imagesArray != null;
        if (imagesArray.size() > 0) {
            initView();
        } else {
            Toast.makeText(this, getString(R.string.alert_headtext_no_sliding_images), Toast.LENGTH_SHORT).show();
        }

    }
    public void initView() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlideAdapters(Branding_activity.this, imagesArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
    }

    @OnClick(R.id.signin_brand)
    public void submit() {

        Intent intent = new Intent(this, Login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
