package com.instancy.instancylearning.mycompetency;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class CompetencyJobRoleAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<CompetencyJobRoles> competencyJobRolesList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;

    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = CompetencyJobRoleAdapter.class.getSimpleName();
    private List<DiscussionCommentsModel> searchList;


    public CompetencyJobRoleAdapter(Activity activity, int resource, List<CompetencyJobRoles> competencyJobRolesList) {
        this.activity = activity;
        this.competencyJobRolesList = competencyJobRolesList;
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();


    }

    public void refreshList(List<CompetencyJobRoles> competencyJobRolesList) {
        this.competencyJobRolesList = competencyJobRolesList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return competencyJobRolesList != null ? competencyJobRolesList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return competencyJobRolesList.get(position);
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
        convertView = inflater.inflate(R.layout.competencyjobcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.txtJobRoleName.setText(competencyJobRolesList.get(position).jobRoleName);
        holder.txtJobRoleName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        convertView.setTag(holder);
        return convertView;
    }

    static class ViewHolder {
        public int getPosition;
        public ViewGroup parent;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @Nullable
        @BindView(R.id.jobrolename)
        TextView txtJobRoleName;

        @OnClick({R.id.jobrolename})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
}


