package com.instancy.instancylearning.gameficitation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
import com.instancy.instancylearning.models.LeaderboardList;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.CompetencyJobRoles;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class LeaderBoardAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<LeaderboardList> leaderboardListList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;

    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = LeaderBoardAdapter.class.getSimpleName();
    private List<DiscussionCommentsModel> searchList;


    public LeaderBoardAdapter(Activity activity, int resource, List<LeaderboardList> leaderboardListList) {
        this.activity = activity;
        this.leaderboardListList = leaderboardListList;
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();

    }

    public void refreshList(List<LeaderboardList> leaderboardListList) {
        this.leaderboardListList = leaderboardListList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return leaderboardListList != null ? leaderboardListList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return leaderboardListList.get(position);
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
        convertView = inflater.inflate(R.layout.leaderboardcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.txtDisplayName.setText(leaderboardListList.get(position).userDisplayName);

        String description=  getLocalizationValue(JsonLocalekeys.filter_label_points)+": " + leaderboardListList.get(position).points  +getLocalizationValue(JsonLocalekeys.filter_label_level)+ "   : " + leaderboardListList.get(position).levelName +
                getLocalizationValue(JsonLocalekeys.filter_label_badges)+"   : " + leaderboardListList.get(position).badges;

        holder.txtDescription.setText(description);
        holder.txtRank.setText(""+leaderboardListList.get(position).rank);

        String imgUrl = leaderboardListList.get(position).userPicturePath;
        Glide.with(convertView.getContext()).load(imgUrl).into(holder.userImage);

        holder.txtRank.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDisplayName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
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
        @BindView(R.id.txt_title)
        TextView txtDisplayName;

        @Nullable
        @BindView(R.id.txt_description)
        TextView txtDescription;

        @Nullable
        @BindView(R.id.txtRank)
        TextView txtRank;


        @Nullable
        @BindView(R.id.imagethumb)
        ImageView userImage;


        @OnClick({R.id.txt_description})
        public void actionsForMenu(View view) {

//            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,activity);
    }
}


