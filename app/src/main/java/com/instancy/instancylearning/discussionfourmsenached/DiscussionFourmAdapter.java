package com.instancy.instancylearning.discussionfourmsenached;

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
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionFourmAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionForumModelDg> discussionForumModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionFourmAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionForumModelDg> searchList;
    AppController appcontroller;
    boolean isAbleToDelete = false, isAbleToEdit = false;


    public DiscussionFourmAdapter(Activity activity, int resource, List<DiscussionForumModelDg> discussionForumModelList, boolean isAbleToDelete, boolean isAbleToEdit) {
        this.activity = activity;
        this.discussionForumModelList = discussionForumModelList;
        this.searchList = new ArrayList<DiscussionForumModelDg>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appcontroller = AppController.getInstance();
        this.isAbleToDelete = isAbleToDelete;
        this.isAbleToEdit = isAbleToEdit;
    }

    public void refreshList(List<DiscussionForumModelDg> myLearningModel) {
        this.discussionForumModelList = myLearningModel;
        this.searchList = new ArrayList<DiscussionForumModelDg>();
        this.searchList.addAll(myLearningModel);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionForumModelList != null ? discussionForumModelList.size() : 0;
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
        convertView = inflater.inflate(R.layout.discussionfourmcell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(discussionForumModelList.get(position).name);
        holder.txtShortDisc.setText(discussionForumModelList.get(position).description);
//        holder.txtAuthor.setText("Moderator: " + discussionForumModelList.get(position).moderatorName + " | Created by: " + discussionForumModelList.get(position).author + " on " + discussionForumModelList.get(position).createdDate + " | Last Updated by : " + discussionForumModelList.get(position).updatedAuthor + " on " + discussionForumModelList.get(position).updatedDate);

        holder.txtTopics.setText(discussionForumModelList.get(position).noOfTopics + " "+getLocalizationValue(JsonLocalekeys.discussionforum_label_topicslabel));
        holder.txtLikes.setText(discussionForumModelList.get(position).totalLikes + " "+getLocalizationValue(JsonLocalekeys.discussionforum_label_likeslabel));

        String totalActivityStr = getLocalizationValue(JsonLocalekeys.discussionforum_label_moderatorlabel)+" ";

        if (isValidString(discussionForumModelList.get(position).moderatorName)) {

            totalActivityStr = totalActivityStr + discussionForumModelList.get(position).moderatorName;
        }

        if (isValidString(discussionForumModelList.get(position).author)) {
            totalActivityStr = totalActivityStr + " | "+getLocalizationValue(JsonLocalekeys.discussionforum_label_createdbylabel)+": " + discussionForumModelList.get(position).author + " "+getLocalizationValue(JsonLocalekeys.commoncomponent_label_on)+" " + discussionForumModelList.get(position).createdDate;
        }

        if (isValidString(discussionForumModelList.get(position).updatedAuthor)) {
            totalActivityStr = totalActivityStr + " | "+getLocalizationValue(JsonLocalekeys.discussionforum_label_lastupdatedlabel)+": " + discussionForumModelList.get(position).updatedAuthor + " "+getLocalizationValue(JsonLocalekeys.commoncomponent_label_on)+" " +discussionForumModelList.get(position).updatedDate;
        }

        holder.txtAuthor.setText(totalActivityStr);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTopics.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (discussionForumModelList.get(position).description.isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }
//   user id logic for edit and delete
//        if (discussionForumModelList.get(position).createdUserID == Integer.parseInt(appUserModel.getUserIDValue())) {
//            holder.btnContextMenu.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnContextMenu.setVisibility(View.INVISIBLE);
//        }

        if (isAbleToEdit || isAbleToDelete) {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }

        String imgUrl = appUserModel.getSiteURL() + discussionForumModelList.get(position).forumThumbnailPath;

        if (isValidString(discussionForumModelList.get(position).forumThumbnailPath)) {
            holder.attachedImg.setVisibility(View.VISIBLE);
            Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.attachedImg);
        } else {

            holder.attachedImg.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionForumModelList.clear();
        if (charText.length() == 0) {
            discussionForumModelList.addAll(searchList);
        } else {
            for (DiscussionForumModelDg s : searchList) {
                if (s.name.toLowerCase(Locale.getDefault()).contains(charText) || s.moderatorName.toLowerCase(Locale.getDefault()).contains(charText)) {
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

        }

        @Nullable
        @BindView(R.id.txt_name)
        TextView txtName;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtShortDesc)
        TextView txtShortDisc;

        @Nullable
        @BindView(R.id.attachedimg)
        ImageView attachedImg;


        @Nullable
        @BindView(R.id.txt_author)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.txtLikes)
        TextView txtLikes;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtTopics)
        TextView txtTopics;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtLikes})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }

    public void applySortBy(final boolean isAscn, String configid) {

        switch (configid) {
            case "1":
                Collections.sort(discussionForumModelList, new Comparator<DiscussionForumModelDg>() {

                    @Override
                    public int compare(DiscussionForumModelDg obj1, DiscussionForumModelDg obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.createdDate.compareToIgnoreCase(obj2.createdDate);

                        } else {
                            return obj2.createdDate.compareToIgnoreCase(obj1.createdDate);
                        }
                    }
                });
                break;
            case "default":
                break;

        }

        this.notifyDataSetChanged();
    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,activity);

    }
}


