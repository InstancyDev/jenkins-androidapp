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
import com.instancy.instancylearning.models.DiscussionTopicModel;
import com.instancy.instancylearning.models.UiSettingsModel;
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

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionTopicAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DiscussionTopicModelDg> discussionTopicModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = DiscussionTopicAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<DiscussionTopicModelDg> searchList;
    AppController appcontroller;


    public DiscussionTopicAdapter(Activity activity, int resource, List<DiscussionTopicModelDg> discussionTopicModelList) {
        this.activity = activity;
        this.discussionTopicModelList = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionTopicModelDg>();
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

    public void refreshList(List<DiscussionTopicModelDg> discussionTopicModelList) {
        this.discussionTopicModelList = discussionTopicModelList;
        this.searchList = new ArrayList<DiscussionTopicModelDg>();
        this.searchList.addAll(discussionTopicModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return discussionTopicModelList != null ? discussionTopicModelList.size() : 10;

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
        convertView = inflater.inflate(R.layout.discussiontopiccell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(discussionTopicModelList.get(position).name);
        holder.txtShortDisc.setText(discussionTopicModelList.get(position).longDescription);


        String totalActivityStr = "Created by: ";

        if (isValidString(discussionTopicModelList.get(position).author)) {

            totalActivityStr = totalActivityStr + discussionTopicModelList.get(position).author + " " + discussionTopicModelList.get(position).createdTime;
        }

        if (isValidString(discussionTopicModelList.get(position).modifiedUserName)) {

            totalActivityStr = totalActivityStr + " |  Last Updated by: " + discussionTopicModelList.get(position).author + " " + discussionTopicModelList.get(position).updatedTime;
        }

        holder.txtAuthor.setText(totalActivityStr);

        holder.txtLikesCount.setText(discussionTopicModelList.get(position).likes + " Like(s)");
        holder.txtCommentsCount.setText(discussionTopicModelList.get(position).noOfReplies + " Comment(s)");

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLikesCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCommentsCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


//        assert holder.txtLikes != null;
//        holder.txtLikes.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_up), null, null, null);


        if (discussionTopicModelList.get(position).likeState) {
            holder.txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            assert holder.txtLikes != null;
            holder.txtLikes.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        } else {
            holder.txtLikes.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtLikes.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
        }

        assert holder.txtComment != null;
        holder.txtComment.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_comment), null, null, null);


        if (discussionTopicModelList.get(position).longDescription.isEmpty() || discussionTopicModelList.get(position).longDescription.contains("null")) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }

        if (discussionTopicModelList.get(position).createdUserID == Integer.parseInt(appUserModel.getUserIDValue())) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }


        String imgUrl = appUserModel.getSiteURL() + discussionTopicModelList.get(position).uploadFileName;

        if (isValidString(discussionTopicModelList.get(position).uploadFileName)) {
            holder.attachedImg.setVisibility(View.VISIBLE);
            Picasso.with(convertView.getContext()).load(imgUrl).into(holder.attachedImg);
        } else {

            holder.attachedImg.setVisibility(View.GONE);
        }

        String profileImg = appUserModel.getSiteURL() + discussionTopicModelList.get(position).topicUserProfile;

        Picasso.with(convertView.getContext()).load(profileImg).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionTopicModelList.clear();
        if (charText.length() == 0) {
            discussionTopicModelList.addAll(searchList);
        } else {
            for (DiscussionTopicModelDg s : searchList) {
                if (s.name.toLowerCase(Locale.getDefault()).contains(charText) || s.longDescription.toLowerCase(Locale.getDefault()).contains(charText)) {
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
        @BindView(R.id.attachedimg)
        ImageView attachedImg;

        @Nullable
        @BindView(R.id.txt_author)
        TextView txtAuthor;


        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtCommentsCount)
        TextView txtCommentsCount;

        @Nullable
        @BindView(R.id.txtComment)
        TextView txtComment;

        @Nullable
        @BindView(R.id.txtLikes)
        TextView txtLikes;

        @Nullable
        @BindView(R.id.txtLikesCount)
        TextView txtLikesCount;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtLikesCount, R.id.txtLikes, R.id.txtComment})
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

    public void applySortBy(final boolean isAscn, String configid) {

        switch (configid) {
            case "1":
                Collections.sort(discussionTopicModelList, new Comparator<DiscussionTopicModelDg>() {

                    @Override
                    public int compare(DiscussionTopicModelDg obj1, DiscussionTopicModelDg obj2) {
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

}


