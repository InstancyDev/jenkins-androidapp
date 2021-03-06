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
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionCommentsModel;
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

public class DiscussionCommentsAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionCommentsModel> discussionCommentsModels = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionCommentsAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionCommentsModel> searchList;
    AppController appcontroller;

    public DiscussionCommentsAdapter(Activity activity, int resource, List<DiscussionCommentsModel> discussionTopicModelList) {
        this.activity = activity;
        this.discussionCommentsModels = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionCommentsModel>();
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

    public void refreshList(List<DiscussionCommentsModel> discussionTopicModelList) {
        this.discussionCommentsModels = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionCommentsModel>();
        this.searchList.addAll(discussionTopicModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionCommentsModels != null ? discussionCommentsModels.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return discussionCommentsModels.get(position);
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
        convertView = inflater.inflate(R.layout.discussionfourmcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(discussionCommentsModels.get(position).displayName + " : " + discussionCommentsModels.get(position).postedDate);
        holder.txtShortDisc.setText(discussionCommentsModels.get(position).message);
        holder.txtShortDisc.setMaxLines(200);

        String imgUrl = appUserModel.getSiteURL() + discussionCommentsModels.get(position).imagedata;

        if (imgUrl.startsWith("http:"))
            imgUrl = imgUrl.replace("http:", "https:");

        Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        if (discussionCommentsModels.get(position).attachment.length() == 0) {
            holder.txtCommentsAttachment.setVisibility(View.INVISIBLE);
        } else {
            holder.txtCommentsAttachment.setVisibility(View.VISIBLE);
        }

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLastUpdate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCommentsAttachment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtLastUpdate.setVisibility(View.GONE);
        holder.txtTopicsCount.setVisibility(View.GONE);
        holder.txtCommentsCount.setVisibility(View.GONE);
        holder.txtAuthor.setVisibility(View.GONE);
        holder.view.setVisibility(View.INVISIBLE);

        if (discussionCommentsModels.get(position).postedBy.equalsIgnoreCase(appUserModel.getUserIDValue())) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);
        }
        else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }


        convertView.setTag(holder);
        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionCommentsModels.clear();
        if (charText.length() == 0) {
            discussionCommentsModels.addAll(searchList);
        } else {
            for (DiscussionCommentsModel s : searchList) {
                if (s.message.toLowerCase(Locale.getDefault()).contains(charText) || s.displayName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    discussionCommentsModels.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {

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
        TextView txtCommentsAttachment;

        @Nullable
        @BindView(R.id.lineview)
        View view;


        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.btn_attachment})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


