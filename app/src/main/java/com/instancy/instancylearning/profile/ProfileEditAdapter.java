package com.instancy.instancylearning.profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ProfileEditAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ProfileConfigsModel> profileConfigsModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = ProfileEditAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<ProfileConfigsModel> searchList;
    AppController appcontroller;


    public ProfileEditAdapter(Activity activity, int resource, List<ProfileConfigsModel> profileConfigsModelList) {
        this.activity = activity;
        this.profileConfigsModelList = profileConfigsModelList;
        this.searchList = new ArrayList<ProfileConfigsModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<ProfileConfigsModel> profileConfigsModelList) {
        this.profileConfigsModelList = profileConfigsModelList;
        this.searchList = new ArrayList<ProfileConfigsModel>();
        this.searchList.addAll(profileConfigsModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return profileConfigsModelList != null ? profileConfigsModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return profileConfigsModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.personaleditcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;

        holder.labelField.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAstrek.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.labelField.setText(profileConfigsModelList.get(position).attributedisplaytext);
        holder.edit_field.setText(profileConfigsModelList.get(position).valueName);
        holder.edit_field.setMaxLines(returnLines(profileConfigsModelList.get(position).names));

        if (profileConfigsModelList.get(position).isrequired.contains("true")) {
            holder.txtAstrek.setText("*");
            holder.txtAstrek.setTextColor(convertView.getResources().getColor(R.color.colorRed));
        }else {
            holder.txtAstrek.setText("");
        }

        holder.edit_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    final EditText Caption = (EditText) v;

                    profileConfigsModelList.get(position).valueName = Caption.getText().toString();

                }
            }
        });


        return convertView;
    }

    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.labelField)
        TextView labelField;

        @Nullable
        @BindView(R.id.astrekd)
        TextView txtAstrek;

        @Nullable
        @BindView(R.id.editField)
        EditText edit_field;

    }

    public int returnLines(String linesRequired) {
        int returnValue = 1;
        switch (linesRequired) {
            case "SingleLineTextField":
                returnValue = 2;
                break;
            case "EmailTextField":
                returnValue = 2;
                break;
            case "MultiLineTextField":
                returnValue = 100;
                break;
        }
        return returnValue;
    }
}


