package com.instancy.instancylearning.discussionfourms;

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
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.MyLearningModel;
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

public class DiscussionFourmAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionForumModel> discussionForumModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionFourmAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionForumModel> searchList;
    AppController appcontroller;


    public DiscussionFourmAdapter(Activity activity, int resource, List<DiscussionForumModel> discussionForumModelList) {
        this.activity = activity;
        this.discussionForumModelList = discussionForumModelList;
        this.searchList = new ArrayList<DiscussionForumModel>();
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

    public void refreshList(List<DiscussionForumModel> myLearningModel) {
        this.discussionForumModelList = myLearningModel;
        this.searchList = new ArrayList<DiscussionForumModel>();
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
        convertView = inflater.inflate(R.layout.discussionfourmcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(discussionForumModelList.get(position).name);
        holder.txtShortDisc.setText(discussionForumModelList.get(position).descriptionValue);
        holder.txtAuthor.setText("Moderator: " + discussionForumModelList.get(position).author + " ");
        holder.txtLastUpdate.setText("Last update: " + discussionForumModelList.get(position).createddate + " ");

        holder.txtTopicsCount.setText(discussionForumModelList.get(position).nooftopics + " Topic(s)");
        holder.txtCommentsCount.setText(discussionForumModelList.get(position).totalposts + " Comment(s)");

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLastUpdate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTopicsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (discussionForumModelList.get(position).descriptionValue.isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }

        if (discussionForumModelList.get(position).createduserid.equalsIgnoreCase(appUserModel.getUserIDValue())) {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }


        String imgUrl = appUserModel.getSiteURL() + discussionForumModelList.get(position).imagedata;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionForumModelList.clear();
        if (charText.length() == 0) {
            discussionForumModelList.addAll(searchList);
        } else {
            for (DiscussionForumModel s : searchList) {
                if (s.name.toLowerCase(Locale.getDefault()).contains(charText) || s.author.toLowerCase(Locale.getDefault()).contains(charText)) {
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

        @OnClick({R.id.btn_contextmenu, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


