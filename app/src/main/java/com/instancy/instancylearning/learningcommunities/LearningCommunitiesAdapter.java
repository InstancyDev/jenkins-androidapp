package com.instancy.instancylearning.learningcommunities;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CommunitiesModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
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

public class LearningCommunitiesAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<CommunitiesModel> communitiesModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = LearningCommunitiesAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<CommunitiesModel> searchList;
    AppController appcontroller;


    public LearningCommunitiesAdapter(Activity activity, int resource, List<CommunitiesModel> communitiesModelList) {
        this.activity = activity;
        this.communitiesModelList = communitiesModelList;
        this.searchList = new ArrayList<CommunitiesModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<CommunitiesModel> myLearningModel) {
        this.communitiesModelList = myLearningModel;
        this.searchList = new ArrayList<CommunitiesModel>();
        this.searchList.addAll(myLearningModel);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return communitiesModelList != null ? communitiesModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return communitiesModelList.get(position);
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
        convertView = inflater.inflate(R.layout.learningcommunitycell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtCmtyName.setText(communitiesModelList.get(position).name);
        holder.txtShortDisc.setText(communitiesModelList.get(position).communitydescription);


        holder.txtCmtyName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtIsMember.setTextColor(convertView.getResources().getColor(R.color.colorStatusOther));

        if (communitiesModelList.get(position).labelalreadyamember.equalsIgnoreCase("null") || communitiesModelList.get(position).labelalreadyamember.equalsIgnoreCase("")) {
            holder.txtIsMember.setText("Pending Request");
            holder.txtIsMember.setVisibility(View.GONE);
        } else {

            holder.txtIsMember.setText(communitiesModelList.get(position).labelalreadyamember);
        }

        if (communitiesModelList.get(position).communitydescription.isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }

        String imgUrl = communitiesModelList.get(position).imagepath;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        communitiesModelList.clear();
        if (charText.length() == 0) {
            communitiesModelList.addAll(searchList);
        } else {
            for (CommunitiesModel s : searchList) {
                if (s.name.toLowerCase(Locale.getDefault()).contains(charText) || s.communitydescription.toLowerCase(Locale.getDefault()).contains(charText)) {
                    communitiesModelList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.txt_comminity_name)
        TextView txtCmtyName;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtShortDesc)
        TextView txtShortDisc;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txt_ismember)
        TextView txtIsMember;


        @OnClick({R.id.btn_contextmenu, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


