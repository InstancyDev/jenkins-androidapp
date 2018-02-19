package com.instancy.instancylearning.wifisharing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.UiSettingsModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Upendranath on 2/12/2018.
 */

public class WiFiDirectNewActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {


    public String TAG = WiFiDirectNewActivity.class.getSimpleName();

    @BindView(R.id.segmentedswitch)
    SegmentedGroup segmentedSwitch;

    UiSettingsModel uiSettingsModel;

    @BindView(R.id.sendBtn)
    RadioButton sendBtn;

    @BindView(R.id.recBtn)
    RadioButton receiveBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifisharenewactivity);
        uiSettingsModel = UiSettingsModel.getInstance();
        ButterKnife.bind(this);


        // RadioBtns Bottom
        sendBtn.setChecked(true);
        sendBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        receiveBtn.setTextColor(getResources().getColor(R.color.colorWhite));
        sendBtn.setTypeface(null, Typeface.BOLD);
        segmentedSwitch.setOnCheckedChangeListener(this);


        //ActionBar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Share Content</font>"));
//        getSupportActionBar().setCustomView(R.layout.drawermenu_item);
        try {
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int isChecked) {

        switch (isChecked) {
            case R.id.sendBtn:

                break;
            case R.id.recBtn:

                break;

            default:
                // Nothing to do
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
