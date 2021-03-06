package com.instancy.instancylearning.askexpertenached;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertSkillsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class AskSkillAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertSkillsModelDg> askExpertSkillsModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskSkillAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertSkillsModelDg> searchList;
    AppController appcontroller;


    public AskSkillAdapter(Activity activity, int resource, List<AskExpertSkillsModelDg> askExpertSkillsModelList) {
        this.activity = activity;
        this.askExpertSkillsModelList = askExpertSkillsModelList;
        this.searchList = new ArrayList<AskExpertSkillsModelDg>();
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

    public void refreshList(List<AskExpertSkillsModelDg> askExpertSkillsModelList) {
        this.askExpertSkillsModelList = askExpertSkillsModelList;
        this.searchList = new ArrayList<AskExpertSkillsModelDg>();
        this.searchList.addAll(askExpertSkillsModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return askExpertSkillsModelList != null ? askExpertSkillsModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return askExpertSkillsModelList.get(position);
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
        convertView = inflater.inflate(R.layout.askexpertskillcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.switchSkill.setText(askExpertSkillsModelList.get(position).shortSkillName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            holder.switchSkill.setTrackTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
            holder.switchSkill.setThumbTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        }


        holder.switchSkill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                askExpertSkillsModelList.get(position).isChecked = isChecked;
            }
        });

        if (askExpertSkillsModelList.get(position).isChecked) {
            holder.switchSkill.setChecked(true);
            holder.switchSkill.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        } else {
            holder.switchSkill.setChecked(false);
            holder.switchSkill.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        }


        return convertView;
    }


    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.swtchskills)
        Switch switchSkill;

        @OnClick({R.id.swtchskills})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
}


