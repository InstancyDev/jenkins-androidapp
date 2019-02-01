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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 9/28/2017 Working on Instancy-Playground-Android.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserInfoActivity.class.getSimpleName();

    AppUserModel appUserModel;

    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    ContentFilterByModel contentFilterByModel;

    int isFromMylearning = 0;

    SideMenusModel sideMenusModel;

    @BindView(R.id.txtFname)
    TextView txtFname;

    @BindView(R.id.txtLname)
    TextView txtLname;

    @BindView(R.id.editLastName)
    TextView editLastName;

    @BindView(R.id.editFirstName)
    TextView editFirstName;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.btnReset)
    Button btnReset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adv_userinfo_activiy);
        appUserModel = AppUserModel.getInstance();
        ButterKnife.bind(this);
        uiSettingsModel = UiSettingsModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();

        isFromMylearning = getIntent().getIntExtra("isFrom", 0);
        sideMenusModel = (SideMenusModel) getIntent().getSerializableExtra("sideMenusModel");
        contentFilterByModel = (ContentFilterByModel) getIntent().getExtras().getSerializable("contentFilterByModel");
        // Action Bar Color And Tint
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        getSupportActionBar().setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + contentFilterByModel.categoryDisplayName + "</font>"));

        applyUiColor();

        if (contentFilterByModel != null) {
            editFirstName.setText(contentFilterByModel.categorySelectedFname);
            editLastName.setText(contentFilterByModel.categorySelectedLname);

        }
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
        btnApply.setText(getLocalizationValue(JsonLocalekeys.advancefilter_button_applybutton));
        btnReset.setText(getLocalizationValue(JsonLocalekeys.advancefilter_button_resetbutton));
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
            resettSelectedCategories();
        }

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

    public void getSelectedCategories() {

        String fName = editFirstName.getText().toString();

        String lName = editLastName.getText().toString();

        if (fName.length() > 0 || lName.length() > 0) {
            contentFilterByModel.categorySelectedFname = fName;
            contentFilterByModel.categorySelectedLname = lName;
        } else {

        }
    }

    public void resettSelectedCategories() {
        contentFilterByModel.categorySelectedFname = "";
        contentFilterByModel.categorySelectedLname = "";
        editLastName.setText("");
        editFirstName.setText("");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult inneractivity:");

    }
}
