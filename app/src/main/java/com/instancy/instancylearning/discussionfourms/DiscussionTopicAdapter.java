package com.instancy.instancylearning.discussionfourms;

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
import com.instancy.instancylearning.models.DiscussionTopicModel;
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

public class DiscussionTopicAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionTopicModel> discussionTopicModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionTopicAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionTopicModel> searchList;
    AppController appcontroller;


    public DiscussionTopicAdapter(Activity activity, int resource, List<DiscussionTopicModel> discussionTopicModelList) {
        this.activity = activity;
        this.discussionTopicModelList = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionTopicModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
//        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
//        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
//        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
//        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
//        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
//        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<DiscussionTopicModel> discussionTopicModelList) {
        this.discussionTopicModelList = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionTopicModel>();
        this.searchList.addAll(discussionTopicModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionTopicModelList != null ? discussionTopicModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return discussionTopicModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.discussionfourmcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(discussionTopicModelList.get(position).name);
        holder.txtShortDisc.setText(discussionTopicModelList.get(position).longdescription);
        holder.txtAuthor.setText(discussionTopicModelList.get(position).latestreplyby + " ");
        holder.txtLastUpdate.setText(discussionTopicModelList.get(position).createddate + " ");

        holder.txtTopicsCount.setText(discussionTopicModelList.get(position).noofviews + " Topic(s)");
        holder.txtCommentsCount.setText(discussionTopicModelList.get(position).noofreplies + " Comment(s)");

        holder.txtTopicsCount.setVisibility(View.INVISIBLE);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLastUpdate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTopicAttachment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (discussionTopicModelList.get(position).longdescription.isEmpty() || discussionTopicModelList.get(position).longdescription.contains("null")) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }

        if (discussionTopicModelList.get(position).attachment.length() == 0) {
            holder.txtTopicAttachment.setVisibility(View.INVISIBLE);
        } else {
            holder.txtTopicAttachment.setVisibility(View.VISIBLE);
        }

        if (discussionTopicModelList.get(position).createduserid.equalsIgnoreCase(appUserModel.getUserIDValue())) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }


//        discussionTopicModel.attachment = appUserModel.getSiteURL() + "/content/sitefiles/" + result.toString();


        String imgUrl = appUserModel.getSiteURL() + discussionTopicModelList.get(position).imagedata;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionTopicModelList.clear();
        if (charText.length() == 0) {
            discussionTopicModelList.addAll(searchList);
        } else {
            for (DiscussionTopicModel s : searchList) {
                if (s.name.toLowerCase(Locale.getDefault()).contains(charText) || s.longdescription.toLowerCase(Locale.getDefault()).contains(charText)) {
                    discussionTopicModelList.add(s);
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
            FontManager.markAsIconContainer(view.findViewById(R.id.btn_attachment), iconFont);
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
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txtLastUpdate)
        TextView txtLastUpdate;

        @Nullable
        @BindView(R.id.txt_author)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.txttopics)
        TextView txtTopicsCount;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtcomments)
        TextView txtCommentsCount;

        @Nullable
        @BindView(R.id.btn_attachment)
        TextView txtTopicAttachment;

        @OnClick({R.id.btn_contextmenu, R.id.card_view,R.id.btn_attachment})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


