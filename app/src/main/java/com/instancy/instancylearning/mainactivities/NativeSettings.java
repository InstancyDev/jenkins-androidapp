package com.instancy.instancylearning.mainactivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.adapters.NativeSettingsAdapter;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.interfaces.StringResultListner;
import com.instancy.instancylearning.models.NativeSetttingsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.instancy.instancylearning.utils.Utilities.formatURL;

/**
 * Created by Upendranath on 5/29/2017.
 */

public class NativeSettings extends AppCompatActivity {

    NativeSettingsAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    PreferencesManager preferencesManager;
    Boolean isLogin;
    final Context context = this;
    String TAG = NativeSettings.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            isLogin = bundle.getBoolean(StaticValues.KEY_ISLOGIN);
        }
        PreferencesManager.initializeInstance(context);
        preferencesManager = PreferencesManager.getInstance();
        preferencesManager.setStringValue(getResources().getString(R.string.app_default_url), StaticValues.KEY_SITEURL);

        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>Settings</font>"));
        try {
            final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        } catch (RuntimeException ex) {

            ex.printStackTrace();
        }
        // Add a few teams to display.
        expandableListDetail = NativeSetttingsModel.getData(isLogin);
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListView = (ExpandableListView) findViewById(R.id.settings_list);
        // Construct our adapter, using our own layout and myTeams
        expandableListAdapter = new NativeSettingsAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        if (isLogin) {
            expandableListView.expandGroup(0);
            expandableListView.expandGroup(1);
        } else {
            expandableListView.expandGroup(0);
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childPosition == 0 && !isLogin) {

                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Do you want to reset the site URL!")
                            .setConfirmText("Yes,reset it!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    preferencesManager.setStringValue(StaticValues.KEY_SITEURL, getString(R.string.app_default_url));
                                    sDialog.dismissWithAnimation();
                                    Toast.makeText(context, "Default url set", Toast.LENGTH_LONG).show();
                                }
                            }).setCancelText("Cancel").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();

                        }
                    })
                            .show();
                } else {


                    resetUrlEditDialog();
                }

                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {

                return true;
            }
        });

    }

    public void resetUrlEditDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.reseturldialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.reseturledit);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setTitle("RESET SITE URL")
                .setPositiveButton("SAVE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
//                            result.setText(userInput.getText());
                                String newUrl = userInput.getText().toString().trim();
                                newUrl = formatURL(newUrl);

                                Toast.makeText(context, "" + newUrl, Toast.LENGTH_SHORT).show();

                                VolleySingleton.stringRequests(newUrl + "/PublicModules/SiteAPIDetails.aspx", new StringResultListner<String>() {
                                    @Override
                                    public void getResult(String result) {
                                        if (!result.isEmpty()) {
                                            //do what you need with the result...
                                            LogUtils.d(TAG, "" + result);
                                        }
                                    }
                                });
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        if (isLogin) {


        }

        finish();
        super.onBackPressed();
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
}