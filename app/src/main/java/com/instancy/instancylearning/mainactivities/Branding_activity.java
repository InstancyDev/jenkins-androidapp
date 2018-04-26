package com.instancy.instancylearning.mainactivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.SlideAdapters;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.ArrayList;

import butterknife.BindView;
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


    @BindView(R.id.signin_brand)
    Button btnLogin;

//    @BindView(R.id.imglogo)
//    ImageView imagelogo;

    @BindView(R.id.signup_brand)
    Button btnSignup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branding_activity_new);
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

        if (getResources().getString(R.string.app_name).equalsIgnoreCase("CLE Academy")) {

            cleLogoMethod();
            mPager.setVisibility(View.GONE);
            btnSignup.setVisibility(View.VISIBLE);
            btnSignup.setBackgroundColor(getResources().getColor(R.color.cle_drakbrown_color));
            btnLogin.setBackgroundColor(getResources().getColor(R.color.cle_drakbrown_color));
        }

        btnLogin.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        btnLogin.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        View someView = findViewById(R.id.brandinglayout);
        someView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppLoginBGColor()));

    }


    public void cleLogoMethod() {

        ImageView imageview = new ImageView(Branding_activity.this);
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.relative_layout);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        // Add image path from drawable folder.
        imageview.setImageResource(R.drawable.cle_logo);
        imageview.setLayoutParams(params);
        relativelayout.addView(imageview);

    }

    public void initView() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlideAdapters(Branding_activity.this, imagesArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        indicator.setVisibility(View.GONE);
    }

    @OnClick(R.id.signin_brand)
    public void loginMethod() {

        Intent intent = new Intent(this, Login_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.signup_brand)
    public void newUserMethod() {

        Intent intentSignup = new Intent(this, SignUp_Activity.class);
        startActivity(intentSignup);
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
