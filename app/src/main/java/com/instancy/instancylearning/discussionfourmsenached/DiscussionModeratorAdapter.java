package com.instancy.instancylearning.discussionfourmsenached;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionModeratorAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionModeratorModel> discussionModeratorModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionModeratorAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionModeratorModel> searchList;
    AppController appcontroller;

    public DiscussionModeratorAdapter(Activity activity, int resource, List<DiscussionModeratorModel> discussionModeratorModelList) {
        this.activity = activity;
        this.discussionModeratorModelList = discussionModeratorModelList;
        this.searchList = new ArrayList<DiscussionModeratorModel>();
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

    public void refreshList(List<DiscussionModeratorModel> discussionModeratorModelList) {
        this.discussionModeratorModelList = discussionModeratorModelList;
        this.searchList = new ArrayList<DiscussionModeratorModel>();
        this.searchList.addAll(discussionModeratorModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionModeratorModelList != null ? discussionModeratorModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return discussionModeratorModelList.get(position);
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
        convertView = inflater.inflate(R.layout.discussionmoderatorcellmultiselect, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtUserName.setText(discussionModeratorModelList.get(position).userName);
//        if (isValidString(discussionModeratorModelList.get(position).userDesg)) {
//            holder.txtAddress.setText(discussionModeratorModelList.get(position).userAddress + ", " + discussionModeratorModelList.get(position).userDesg);
//        } else {
            holder.txtAddress.setText(discussionModeratorModelList.get(position).userAddress);
//        }
        String imgUrl = appUserModel.getSiteURL() + discussionModeratorModelList.get(position).userThumb;

        if (imgUrl.startsWith("http:"))
            imgUrl = imgUrl.replace("http:", "https:");

        Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imagethumb);
        holder.txtUserName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAddress.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        holder.checkBox.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.checkBox.setChecked(discussionModeratorModelList.get(position).isSelected);

        convertView.setTag(holder);
        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionModeratorModelList.clear();
        if (charText.length() == 0) {
            discussionModeratorModelList.addAll(searchList);
        } else {
            for (DiscussionModeratorModel s : searchList) {
                if (s.userName.toLowerCase(Locale.getDefault()).contains(charText) || s.userAddress.toLowerCase(Locale.getDefault()).contains(charText)) {
                    discussionModeratorModelList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }


//    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        discussionModeratorModelList.clear();
//        if (charText.length() == 0) {
//            discussionModeratorModelList.addAll(searchList);
//        } else {
//            for (DiscussionModeratorModel s : searchList) {
//                if (s.userName.toLowerCase(Locale.getDefault()).contains(charText) || s.userAddress.toLowerCase(Locale.getDefault()).contains(charText)) {
//                    discussionModeratorModelList.add(s);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }


    static class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btn_attachment), iconFont);
        }


        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imagethumb;

        @Nullable
        @BindView(R.id.txtUserName)
        TextView txtUserName;

        @Nullable
        @BindView(R.id.txtAddress)
        TextView txtAddress;


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.chxBox)
        CheckBox checkBox;


        @OnClick({R.id.card_view, R.id.chxBox})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }

}


