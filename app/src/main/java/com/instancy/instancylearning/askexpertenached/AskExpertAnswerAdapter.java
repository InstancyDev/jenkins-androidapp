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
import com.instancy.instancylearning.models.AskExpertAnswerModel;
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

public class AskExpertAnswerAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertAnswerModelDg> askExpertAnswerModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskExpertAnswerAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertAnswerModelDg> searchList;
    AppController appcontroller;


    public AskExpertAnswerAdapter(Activity activity, int resource, List<AskExpertAnswerModelDg> askExpertAnswerModelList) {
        this.activity = activity;
        this.askExpertAnswerModelList = askExpertAnswerModelList;
        this.searchList = new ArrayList<AskExpertAnswerModelDg>();
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

    public void refreshList(List<AskExpertAnswerModelDg> askExpertAnswerModelList) {
        this.askExpertAnswerModelList = askExpertAnswerModelList;
        this.searchList = new ArrayList<AskExpertAnswerModelDg>();
        this.searchList.addAll(askExpertAnswerModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return askExpertAnswerModelList != null ? askExpertAnswerModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return askExpertAnswerModelList.get(position);
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
        convertView = inflater.inflate(R.layout.askexpertsanswerscell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtName.setText(askExpertAnswerModelList.get(position).respondedUserName);
        holder.txtDaysAgo.setText(" "+ JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_answeredonlabel, activity)+" " + askExpertAnswerModelList.get(position).daysAgo);
        holder.txtMessage.setText(askExpertAnswerModelList.get(position).response + " ");
        holder.txtTotalViews.setText(askExpertAnswerModelList.get(position).viewsCount +" "+ JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_viewslabel, activity));
        holder.txtComments.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_commentslabel, activity)+" " + askExpertAnswerModelList.get(position).commentCount);
        holder.txtUpvote.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_upvotetitlelabel, activity)+" " + askExpertAnswerModelList.get(position).upvotesCount);

        holder.txtDownnvote.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_downvotetitlelabel, activity));

        holder.txtComment.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.discussionforum_label_commentlabel, activity));

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDaysAgo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtMessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTotalUpvoters.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtComment.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtComments.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (Integer.parseInt(appUserModel.getUserIDValue()) == askExpertAnswerModelList.get(position).respondedUserId) {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);// INVISIBLE
        }

        if (askExpertAnswerModelList.get(position).isLikedStr.equalsIgnoreCase("null")) {
            holder.txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
            holder.txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppTextColor()), null, null, null);
        } else if (askExpertAnswerModelList.get(position).isLikedStr.equalsIgnoreCase("true")) {
            holder.txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            holder.txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            holder.txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppButtonBgColor()), null, null, null);
            holder.txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppTextColor()), null, null, null);

        } else if (askExpertAnswerModelList.get(position).isLikedStr.equalsIgnoreCase("false")) {
            holder.txtUpvote.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
            holder.txtDownnvote.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            holder.txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_up, uiSettingsModel.getAppTextColor()), null, null, null);
            holder.txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromStringWithColor(convertView.getContext(), R.string.fa_icon_thumbs_o_down, uiSettingsModel.getAppButtonBgColor()), null, null, null);
        }


        assert holder.txtComment != null;
        holder.txtComment.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_comments), null, null, null);

        String userImgUrl = appUserModel.getSiteURL() + askExpertAnswerModelList.get(position).picture;
        String atchimgUrl = appUserModel.getSiteURL() + askExpertAnswerModelList.get(position).userResponseImagePath;

//        if (isValidString(askExpertAnswerModelList.get(position).userResponseImagePath)) {
//            holder.attachedImg.setVisibility(View.VISIBLE);
//            Glide.with(convertView.getContext()).load(atchimgUrl).placeholder(R.drawable.cellimage).into(holder.attachedImg);
//        } else {
//            holder.attachedImg.setVisibility(View.GONE);
//        }

        if (isValidString(askExpertAnswerModelList.get(position).userResponseImagePath)) {

//            String imgUrl = appUserModel.getSiteURL() + askExpertQuestionModelList.get(position).userQuestionImagePath;
//            Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imageThumb);

            final String attachimgUrl = appUserModel.getSiteURL() + askExpertAnswerModelList.get(position).userResponseImagePath;

            String fileExtesnion = "";
            if (isValidString(askExpertAnswerModelList.get(position).userResponseImagePath)) {
                fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertAnswerModelList.get(position).userResponseImagePath);
            }

            int resourceId = gettheContentTypeNotImg(fileExtesnion);

            if (isValidString(askExpertAnswerModelList.get(position).userResponseImagePath)) {
                holder.attachedImg.setVisibility(View.VISIBLE);
                if (resourceId == 0)
                    Glide.with(convertView.getContext()).load(attachimgUrl).placeholder(R.drawable.cellimage).into(holder.attachedImg);
                else
                    holder.attachedImg.setImageDrawable(getDrawableFromStringWithColor(activity, resourceId, uiSettingsModel.getAppButtonBgColor()));

            } else {
                holder.attachedImg.setVisibility(View.GONE);
            }

            final int finalResourceId = resourceId;
            final String finalFileExtesnion = fileExtesnion;
            holder.attachedImg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                  gotoRespectiveActivity(finalResourceId, finalFileExtesnion, askExpertAnswerModelList.get(position), attachimgUrl);
                }
            });

            holder.attachedImg.setVisibility(View.VISIBLE);
        } else {

            holder.attachedImg.setVisibility(View.GONE);

        }

        if (userImgUrl.startsWith("http:"))
            userImgUrl = userImgUrl.replace("http:", "https:");

        Glide.with(convertView.getContext()).load(userImgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        askExpertAnswerModelList.clear();
        if (charText.length() == 0) {
            askExpertAnswerModelList.addAll(searchList);
        } else {
            for (AskExpertAnswerModelDg s : searchList) {
                if (s.response.toLowerCase(Locale.getDefault()).contains(charText) || s.respondedUserName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    askExpertAnswerModelList.add(s);
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
        ImageView attachedImg;


        @Nullable
        @BindView(R.id.txt_name)
        TextView txtName;

        @Nullable
        @BindView(R.id.txtDaysAgo)
        TextView txtDaysAgo;

        @Nullable
        @BindView(R.id.txtmessage)
        TextView txtMessage;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtUpvote)
        TextView txtUpvote;

        @Nullable
        @BindView(R.id.txtDownnvote)
        TextView txtDownnvote;

        @Nullable
        @BindView(R.id.txtComment)
        TextView txtComment;

        @Nullable
        @BindView(R.id.txtComments)
        TextView txtComments;


        @Nullable
        @BindView(R.id.txtTotalViews)
        TextView txtTotalViews;

        @Nullable
        @BindView(R.id.txtTotalUpvoters)
        TextView txtTotalUpvoters;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtTotalUpvoters, R.id.txtComment, R.id.txtComments, R.id.txtUpvote, R.id.txtDownnvote})
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


    void gotoRespectiveActivity(int finalResourceId, String finalFileExtesnion, AskExpertAnswerModelDg askExpertQuestionModel, String attachimgUrl) {

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
                    pdfIntent.putExtra("PDF_FILENAME", askExpertQuestionModel.userResponseImagePath);
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
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.response);
                    activity.startActivity(intentSocial);
                    break;
                case "":
                    Toast.makeText(activity, getLocalizationValue(JsonLocalekeys.commonalerttitle_subtitle_invalidfiletypetitle), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    intentSocial.putExtra("ATTACHMENT", true);
                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, attachedURL);
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.response);
                    activity.startActivity(intentSocial);
                    break;

            }
        }


    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, activity);

    }

}


