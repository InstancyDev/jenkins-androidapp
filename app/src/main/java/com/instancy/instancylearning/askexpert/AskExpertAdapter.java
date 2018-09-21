package com.instancy.instancylearning.askexpert;

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

import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
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

public class AskExpertAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertQuestionModel> discussionForumModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskExpertAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertQuestionModel> searchList;
    AppController appcontroller;


    public AskExpertAdapter(Activity activity, int resource, List<AskExpertQuestionModel> discussionForumModelList) {
        this.activity = activity;
        this.discussionForumModelList = discussionForumModelList;
        this.searchList = new ArrayList<AskExpertQuestionModel>();
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

    public void refreshList(List<AskExpertQuestionModel> myLearningModel) {
        this.discussionForumModelList = myLearningModel;
        this.searchList = new ArrayList<AskExpertQuestionModel>();
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
        convertView = inflater.inflate(R.layout.askexpertnewdesigncell,null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtQuestion.setText(discussionForumModelList.get(position).userQuestion);
        holder.txtAskedBy.setText("Asked by: " + discussionForumModelList.get(position).username + " |");
        holder.txtAskedOn.setText("Asked on: " + discussionForumModelList.get(position).postedDate + " |");
        holder.txtNoAnswers.setText(discussionForumModelList.get(position).answers + " Answer(s)");

        holder.txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAskedBy.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAskedOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (discussionForumModelList.get(position).userID.equalsIgnoreCase(discussionForumModelList.get(position).postedUserId)) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);

        } else {

            holder.btnContextMenu.setVisibility(View.INVISIBLE);

        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionForumModelList.clear();
        if (charText.length() == 0) {
            discussionForumModelList.addAll(searchList);
        } else {
            for (AskExpertQuestionModel s : searchList) {
                if (s.userQuestion.toLowerCase(Locale.getDefault()).contains(charText) || s.username.toLowerCase(Locale.getDefault()).contains(charText)) {
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
        @BindView(R.id.txt_question)
        TextView txtQuestion;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txt_askedby)
        TextView txtAskedBy;

        @Nullable
        @BindView(R.id.txt_askedon)
        TextView txtAskedOn;

        @Nullable
        @BindView(R.id.txtno_answers)
        TextView txtNoAnswers;


        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;


        @OnClick({R.id.btn_contextmenu, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


