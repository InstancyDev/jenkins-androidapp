package com.instancy.instancylearning.discussionfourmsenached;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.mcoy_jiang.videomanager.ui.McoyVideoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionCommentsAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionCommentsModelDg> discussionCommentsModels = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionCommentsAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionCommentsModelDg> searchList;
    AppController appcontroller;

    public DiscussionCommentsAdapter(Activity activity, int resource, List<DiscussionCommentsModelDg> discussionTopicModelList) {
        this.activity = activity;
        this.discussionCommentsModels = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionCommentsModelDg>();
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

    public void refreshList(List<DiscussionCommentsModelDg> discussionTopicModelList) {
        this.discussionCommentsModels = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionCommentsModelDg>();
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
        convertView = inflater.inflate(R.layout.discussioncommentcell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_createdbylabel)+discussionCommentsModels.get(position).commentedBy);
        holder.txtDaysAgo.setText(getLocalizationValue(JsonLocalekeys.discussionforum_label_commentedontitlelabel)+" " + discussionCommentsModels.get(position).commentedFromDays);
        holder.txtmessage.setText(discussionCommentsModels.get(position).message);
        holder.txtmessage.setMaxLines(200);

        holder.txtLikesCount.setText(discussionCommentsModels.get(position).commentLikes + ""+getLocalizationValue(JsonLocalekeys.discussionforum_label_likeslabel));
        holder.txtRepliesCount.setText(discussionCommentsModels.get(position).commentRepliesCount + " "+getLocalizationValue(JsonLocalekeys.discussionforum_label_replys));


        assert holder.txtLike != null;
        holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_up), null, null, null);


        assert holder.txtReply != null;
        holder.txtReply.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_reply), null, null, null);


        String imgUrl = appUserModel.getSiteURL() + discussionCommentsModels.get(position).commentUserProfile;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imagethumb);

        if (discussionCommentsModels.get(position).commentFileUploadPath.length() == 0) {
            holder.attachedImg.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
        } else {
            holder.attachedImg.setVisibility(View.VISIBLE);
            String attachimgUrl = appUserModel.getSiteURL() + discussionCommentsModels.get(position).commentFileUploadPath;
            Picasso.with(convertView.getContext()).load(attachimgUrl).into(holder.attachedImg);


            if (attachimgUrl.contains(".mp4")) {
                holder.attachedImg.setVisibility(View.GONE);
                holder.videoLayout.setVisibility(View.VISIBLE);
                assert holder.videoView != null;
                holder.videoView.setVideoUrl(attachimgUrl);
            }
        }


        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtmessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDaysAgo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtRepliesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLikesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtReply.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        assert holder.txtLike != null;
        holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_up), null, null, null);

//        if (discussionCommentsModels.get(position).likeState) {
//            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//        } else {
//            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        }

        if (discussionCommentsModels.get(position).likeState) {
            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

            assert holder.txtLike != null;
            holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        } else {
            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);

        }


        if (discussionCommentsModels.get(position).postedBy == Integer.parseInt(appUserModel.getUserIDValue())) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
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
            for (DiscussionCommentsModelDg s : searchList) {
                if (s.message.toLowerCase(Locale.getDefault()).contains(charText) || s.message.toLowerCase(Locale.getDefault()).contains(charText)) {
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
        @BindView(R.id.imagethumb)
        ImageView imagethumb;

        @Nullable
        @BindView(R.id.txt_name)
        TextView txtName;

        @Nullable
        @BindView(R.id.txtDaysAgo)
        TextView txtDaysAgo;

        @Nullable
        @BindView(R.id.txtmessage)
        TextView txtmessage;


        @Nullable
        @BindView(R.id.attachedimg)
        ImageView attachedImg;


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtLike)
        TextView txtLike;

        @Nullable
        @BindView(R.id.txtReply)
        TextView txtReply;

        @Nullable
        @BindView(R.id.txtLikesCount)
        TextView txtLikesCount;

        @Nullable
        @BindView(R.id.txtRepliesCount)
        TextView txtRepliesCount;

        @Nullable
        @BindView(R.id.videoLayout)
        FrameLayout videoLayout;

        @Nullable
        @BindView(R.id.videoView)
        McoyVideoView videoView;


        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtLikesCount, R.id.txtLike, R.id.txtReply})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }

    @SuppressLint("ResourceAsColor")
    public Drawable getDrawableFromString(Context context, int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(R.color.colorDarkGrey);
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }
    private String getLocalizationValue(String key){
        return  JsonLocalization.getInstance().getStringForKey(key,activity);

    }
}


