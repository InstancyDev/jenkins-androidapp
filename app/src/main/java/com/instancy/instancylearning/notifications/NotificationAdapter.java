package com.instancy.instancylearning.notifications;

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
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.NotificationModel;
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

public class NotificationAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationModel> discussionForumModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = NotificationAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<NotificationModel> searchList;

    public NotificationAdapter(Activity activity, int resource, List<NotificationModel> discussionForumModelList) {
        this.activity = activity;
        this.discussionForumModelList = discussionForumModelList;
        this.searchList = new ArrayList<NotificationModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

    }

    public void refreshList(List<NotificationModel> myLearningModel) {
        this.discussionForumModelList = myLearningModel;
        this.searchList = new ArrayList<NotificationModel>();
        this.searchList.addAll(myLearningModel);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionForumModelList != null ? discussionForumModelList.size() : 10;
    }

    @Override
    public Object getItem(int position) {
        return discussionForumModelList.get(position);
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
        convertView = inflater.inflate(R.layout.notificationcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

//        holder.txtTitle.setText("");
//        holder.txtDescription.setText(");
//        holder.txtDate.setText("08-06-2018:");



        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionForumModelList.clear();
        if (charText.length() == 0) {
            discussionForumModelList.addAll(searchList);
        } else {
            for (NotificationModel s : searchList) {
                if (s.fromusername.toLowerCase(Locale.getDefault()).contains(charText) || s.forumname.toLowerCase(Locale.getDefault()).contains(charText)) {
                    discussionForumModelList.add(s);
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
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.txtBolt), iconFont);
        }

        @Nullable
        @BindView(R.id.txtBolt)
        TextView txtBolt;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtTitle)
        TextView txtTitle;

        @Nullable
        @BindView(R.id.txtDescription)
        TextView txtDescription;

        @Nullable
        @BindView(R.id.txtDate)
        TextView txtDate;

        @OnClick({R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


