package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.gettheContentTypeNotImg;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class AskExpertCommentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertCommentModel> askExpertCommentModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskExpertCommentAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertCommentModel> searchList;
    AppController appcontroller;


    public AskExpertCommentAdapter(Activity activity, int resource, List<AskExpertCommentModel> askExpertAnswerModelList) {
        this.activity = activity;
        this.askExpertCommentModelList = askExpertAnswerModelList;
        this.searchList = new ArrayList<AskExpertCommentModel>();
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

    public void refreshList(List<AskExpertCommentModel> discussionTopicModelList) {
        this.askExpertCommentModelList = discussionTopicModelList;
        this.searchList = new ArrayList<AskExpertCommentModel>();
        this.searchList.addAll(askExpertCommentModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return askExpertCommentModelList != null ? askExpertCommentModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return askExpertCommentModelList.get(position);
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
        convertView = inflater.inflate(R.layout.askexpertscommentscell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtName.setText(askExpertCommentModelList.get(position).commentedUserName);
        holder.txtMessage.setText(askExpertCommentModelList.get(position).commentDescription + " ");
        holder.txtAskedWhen.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_commentedontitlelabel, activity) + " " + askExpertCommentModelList.get(position).commentedDate);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAnsweredOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtMessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        assert holder.txtLike != null;
        holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_up), null, null, null);

        holder.txtLike.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.discussionforum_label_likelabel, activity));

        if (askExpertCommentModelList.get(position).isLiked) {
            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        } else {
            holder.txtLike.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
        }

        if (askExpertCommentModelList.get(position).userID == askExpertCommentModelList.get(position).commentUserID) {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }

        String userImgUrl = appUserModel.getSiteURL() + askExpertCommentModelList.get(position).imagePath;
        String atchimgUrl = appUserModel.getSiteURL() + askExpertCommentModelList.get(position).usercCmntImagePath;

//        if (isValidString(askExpertCommentModelList.get(position).usercCmntImagePath)) {
//            holder.attachedimg.setVisibility(View.VISIBLE);
//            Glide.with(convertView.getContext()).load(atchimgUrl).placeholder(R.drawable.cellimage).into(holder.attachedimg);
//        } else {
//            holder.attachedimg.setVisibility(View.GONE);
//        }




        if (isValidString(askExpertCommentModelList.get(position).usercCmntImagePath)) {

//            String imgUrl = appUserModel.getSiteURL() + askExpertQuestionModelList.get(position).userQuestionImagePath;
//            Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imageThumb);

            final String attachimgUrl = appUserModel.getSiteURL() + askExpertCommentModelList.get(position).usercCmntImagePath;

            String fileExtesnion = "";
            if (isValidString(askExpertCommentModelList.get(position).usercCmntImagePath)) {
                fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertCommentModelList.get(position).usercCmntImagePath);
            }

            int resourceId = gettheContentTypeNotImg(fileExtesnion);

            if (isValidString(askExpertCommentModelList.get(position).usercCmntImagePath)) {
                holder.attachedimg.setVisibility(View.VISIBLE);
                if (resourceId == 0)
                    Glide.with(convertView.getContext()).load(attachimgUrl).placeholder(R.drawable.cellimage).into(holder.attachedimg);
                else
                    holder.attachedimg.setImageDrawable(getDrawableFromStringWithColor(activity, resourceId, uiSettingsModel.getAppButtonBgColor()));

            } else {
                holder.attachedimg.setVisibility(View.GONE);
            }

            final int finalResourceId = resourceId;
            final String finalFileExtesnion = fileExtesnion;
            holder.attachedimg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    gotoRespectiveActivity(finalResourceId, finalFileExtesnion, askExpertCommentModelList.get(position), attachimgUrl);
                }
            });

            holder.attachedimg.setVisibility(View.VISIBLE);
        } else {

            holder.attachedimg.setVisibility(View.GONE);

        }

        if (userImgUrl.startsWith("http:"))
            userImgUrl = userImgUrl.replace("http:", "https:");

        Glide.with(convertView.getContext()).load(userImgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        askExpertCommentModelList.clear();
        if (charText.length() == 0) {
            askExpertCommentModelList.addAll(searchList);
        } else {
            for (AskExpertCommentModel s : searchList) {
                if (s.commentedUserName.toLowerCase(Locale.getDefault()).contains(charText) || s.commentDescription.toLowerCase(Locale.getDefault()).contains(charText)) {
                    askExpertCommentModelList.add(s);
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
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.attachedimg)
        ImageView attachedimg;

        @Nullable
        @BindView(R.id.txt_name)
        TextView txtName;

        @Nullable
        @BindView(R.id.txtansweredon)
        TextView txtAnsweredOn;

        @Nullable
        @BindView(R.id.txtmessage)
        TextView txtMessage;


        @Nullable
        @BindView(R.id.txtAskedWhen)
        TextView txtAskedWhen;

        @Nullable
        @BindView(R.id.txtLike)
        TextView txtLike;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtLike})
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

    void gotoRespectiveActivity(int finalResourceId, String finalFileExtesnion, AskExpertCommentModel askExpertQuestionModel, String attachimgUrl) {

        if (finalResourceId == 0) {
            return;
        } else {
            String attachedURL = "http://docs.google.com/gview?embedded=true&url=" + attachimgUrl;
            Intent intentSocial = new Intent(activity, SocialWebLoginsActivity.class);
            switch (finalFileExtesnion) {
                case "pdf":
                case ".pdf":
                    Intent pdfIntent = new Intent(activity, PdfViewer_Activity.class);
                    pdfIntent.putExtra("PDF_URL", attachimgUrl);
                    pdfIntent.putExtra("ISONLINE", "YES");
                    pdfIntent.putExtra("PDF_FILENAME", askExpertQuestionModel.usercCmntImagePath);
                    activity.startActivity(pdfIntent);
                    break;
                case ".mp3":
                case ".wav":
                case ".rmj":
                case ".m3u":
                case ".ogg":
                case ".webm":
                case ".m4a":
                case ".dat":
                case ".wmi":
                case ".avi":
                case ".wm":
                case ".wmv":
                case ".flv":
                case ".rmvb":
                case ".mp4":
                case ".ogv":
                    intentSocial.putExtra("ATTACHMENT", true);
                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, attachimgUrl);
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.commentDescription);
                    activity.startActivity(intentSocial);
                    break;
                case "":
                    Toast.makeText(activity, getLocalizationValue(JsonLocalekeys.commonalerttitle_subtitle_invalidfiletypetitle), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    intentSocial.putExtra("ATTACHMENT", true);
                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, attachedURL);
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.commentDescription);
                    activity.startActivity(intentSocial);
                    break;

            }
        }


    }
    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, activity);

    }
}


