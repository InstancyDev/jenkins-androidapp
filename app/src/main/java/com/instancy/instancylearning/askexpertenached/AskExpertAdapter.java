package com.instancy.instancylearning.askexpertenached;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.Gravity;
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
import com.instancy.instancylearning.interfaces.TagClicked;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.mainactivities.SocialWebLoginsActivity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getFileExtensionWithPlaceHolderImage;
import static com.instancy.instancylearning.utils.Utilities.gettheContentTypeNotImg;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class AskExpertAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertQuestionModelDg> askExpertQuestionModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskExpertAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertQuestionModelDg> searchList;
    AppController appcontroller;
    TagClicked tagClicked;


    public AskExpertAdapter(Activity activity, int resource, List<AskExpertQuestionModelDg> askExpertQuestionModelList, TagClicked tagClicked) {
        this.activity = activity;
        this.askExpertQuestionModelList = askExpertQuestionModelList;
        this.searchList = new ArrayList<AskExpertQuestionModelDg>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        this.tagClicked = tagClicked;
        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<AskExpertQuestionModelDg> myLearningModel) {
        this.askExpertQuestionModelList = myLearningModel;
        this.searchList = new ArrayList<AskExpertQuestionModelDg>();
        this.searchList.addAll(myLearningModel);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return askExpertQuestionModelList != null ? askExpertQuestionModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return askExpertQuestionModelList.get(position);
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
        convertView = inflater.inflate(R.layout.askexpertcell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtQuestion.setText(askExpertQuestionModelList.get(position).userQuestion);
        holder.txtAllActivites.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_askedbylabel, activity) + " " + askExpertQuestionModelList.get(position).userName + "   |   " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_askedonlabel, activity) + " " + askExpertQuestionModelList.get(position).postedDate + "   |   " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_lastactivelabel, activity) + " " + askExpertQuestionModelList.get(position).postedDate);
        holder.txtNoAnswers.setText(askExpertQuestionModelList.get(position).totalAnswers + " " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_answerslabel, activity));
        holder.txtNoViews.setText(askExpertQuestionModelList.get(position).totalViews + " " + JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.asktheexpert_label_viewslabel, activity));
        holder.txtDescription.setText(askExpertQuestionModelList.get(position).userQuestionDescription);
        holder.txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAllActivites.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtNoViews.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

//        holder.tagsSkills.setVisibility(View.GONE);

        if (isValidString(askExpertQuestionModelList.get(position).userQuestionImage)) {

//            String imgUrl = appUserModel.getSiteURL() + askExpertQuestionModelList.get(position).userQuestionImagePath;
//            Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imageThumb);

            final String attachimgUrl = appUserModel.getSiteURL() + askExpertQuestionModelList.get(position).userQuestionImagePath;

            String fileExtesnion = "";
            if (isValidString(askExpertQuestionModelList.get(position).userQuestionImagePath)) {
                fileExtesnion = getFileExtensionWithPlaceHolderImage(askExpertQuestionModelList.get(position).userQuestionImagePath);
            }

            int resourceId = gettheContentTypeNotImg(fileExtesnion);

            if (isValidString(askExpertQuestionModelList.get(position).userQuestionImagePath)) {
                holder.imageThumb.setVisibility(View.VISIBLE);
                if (resourceId == 0)
                    Glide.with(convertView.getContext()).load(attachimgUrl).placeholder(R.drawable.cellimage).into(holder.imageThumb);
                else
                    holder.imageThumb.setImageDrawable(getDrawableFromStringWithColor(activity, resourceId, uiSettingsModel.getAppButtonBgColor()));

            } else {
                holder.imageThumb.setVisibility(View.GONE);
            }

            final int finalResourceId = resourceId;
            final String finalFileExtesnion = fileExtesnion;
            holder.imageThumb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    gotoRespectiveActivity(finalResourceId, finalFileExtesnion, askExpertQuestionModelList.get(position), attachimgUrl);
                }
            });

            holder.imageThumb.setVisibility(View.VISIBLE);
        } else {

            holder.imageThumb.setVisibility(View.GONE);

        }
//
        if (askExpertQuestionModelList.get(position).userQuestionDescription.length() > 1) {

            holder.txtDescription.setVisibility(View.VISIBLE);

        } else {

            holder.txtDescription.setVisibility(View.GONE);

        }

        if (Integer.parseInt(appUserModel.getUserIDValue()) == askExpertQuestionModelList.get(position).createduserID) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);

        } else {

            holder.btnContextMenu.setVisibility(View.INVISIBLE);

        }

        List<ContentValues> breadcrumbItemsList = null;

        breadcrumbItemsList = new ArrayList<ContentValues>();
        breadcrumbItemsList = generateTagsList(askExpertQuestionModelList.get(position).questionCategoriesArray);

        generateBreadcrumb(breadcrumbItemsList, holder.tagsSkills, convertView.getContext());

        return convertView;
    }

    public List<ContentValues> generateTagsList(List<String> skillSets) {
        List<ContentValues> tagsList = new ArrayList<>();

        for (int i = 0; i < skillSets.size(); i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", i);
            cvBreadcrumbItem.put("categoryname", skillSets.get(i));
            tagsList.add(cvBreadcrumbItem);
        }

        return tagsList;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        askExpertQuestionModelList.clear();
        if (charText.length() == 0) {
            askExpertQuestionModelList.addAll(searchList);
        } else {
            for (AskExpertQuestionModelDg s : searchList) {
                if (s.userQuestion.toLowerCase(Locale.getDefault()).contains(charText) || s.userName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    askExpertQuestionModelList.add(s);
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
        @BindView(R.id.txt_all_activites)
        TextView txtAllActivites;

        @Nullable
        @BindView(R.id.txt_description)
        TextView txtDescription;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imageThumb;

        @Nullable
        @BindView(R.id.txtno_answers)
        TextView txtNoAnswers;


        @Nullable
        @BindView(R.id.txtno_views)
        TextView txtNoViews;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.cflBreadcrumb)
        CustomFlowLayout tagsSkills;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtno_answers, R.id.txtno_views})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }

    public void generateBreadcrumb(List<ContentValues> dicBreadcrumbItems, CustomFlowLayout category_breadcrumb, final Context context) {
        boolean isFirstCategory = true;
        ContentValues cvBreadcrumbItem = null;
        // int lastCategory = 10;
        category_breadcrumb.removeAllViews();
        int breadcrumbCount = dicBreadcrumbItems.size();
        View.OnClickListener onBreadcrumbItemCLick = null;
        onBreadcrumbItemCLick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String categoryId = tv.getTag(R.id.CATALOG_CATEGORY_ID_TAG)
                        .toString();
                int categoryLevel = Integer.valueOf(tv.getTag(
                        R.id.CATALOG_CATEGORY_LEVEL_TAG).toString());

                String categoryName = tv.getText().toString();

                tagClicked.tagClickedInterface(categoryName);

            }
        };

        for (int i = 0; i < breadcrumbCount; i++) {
            if (i == 0) {
                isFirstCategory = true;
            } else {
                isFirstCategory = false;
            }

            TextView textView = new TextView(context);
            TextView arrowView = new TextView(context);

            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(arrowView, iconFont);

            arrowView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><medium><b>"
                    + context.getResources().getString(R.string.fa_icon_angle_right) + "</b></big> </font>"));

            arrowView.setTextSize(12);

            arrowView.setGravity(Gravity.CENTER | Gravity.LEFT);
            arrowView.setVisibility(View.GONE);
            // String text = coountries[i];
            cvBreadcrumbItem = dicBreadcrumbItems.get(i);
            String categoryId = cvBreadcrumbItem.getAsString("categoryid");
            String categoryName = cvBreadcrumbItem.getAsString("categoryname");

//            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><big><b>"
//                    + categoryName + "</b></small>  </font>"));

            textView.setText(Html.fromHtml("<font color='" + uiSettingsModel.getAppTextColor() + "'><small>"
                    + categoryName + "</small>  </font>"));

            textView.setGravity(Gravity.CENTER | Gravity.CENTER);
            textView.setTag(R.id.CATALOG_CATEGORY_ID_TAG, categoryId);
            textView.setTag(R.id.CATALOG_CATEGORY_LEVEL_TAG, i);
            // textView.setBackgroundColor(R.color.alert_no_button);
//            textView.setBackgroundColor(context.getResources().getColor(R.color.colorDarkGrey));
            textView.setBackground(context.getResources().getDrawable(R.drawable.cornersround));
            textView.setOnClickListener(onBreadcrumbItemCLick);
            textView.setClickable(true);
            if (!isFirstCategory) {
                category_breadcrumb.addView(arrowView, new CustomFlowLayout.LayoutParams(
                        CustomFlowLayout.LayoutParams.WRAP_CONTENT, 50));
            }
            category_breadcrumb.addView(textView, new CustomFlowLayout.LayoutParams(
                    CustomFlowLayout.LayoutParams.WRAP_CONTENT, CustomFlowLayout.LayoutParams.WRAP_CONTENT));

        }

    }

    public void applySortBy(final boolean isAscn, String configid) {

//        Collections.sort(myLearningModel, Collections.reverseOrder());

        switch (configid) {
            case "0":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModelDg>() {

                    @Override
                    public int compare(AskExpertQuestionModelDg obj1, AskExpertQuestionModelDg obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.lastActivatedDateFormat.compareToIgnoreCase(obj2.lastActivatedDateFormat);

                        } else {
                            return obj2.lastActivatedDateFormat.compareToIgnoreCase(obj1.lastActivatedDateFormat);
                        }
                    }
                });

                break;
            case "1":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModelDg>()

                {

                    @Override
                    public int compare(AskExpertQuestionModelDg obj1, AskExpertQuestionModelDg obj2) {
                        // ## Ascending order
                        if (isAscn) {

                            return Integer.compare(obj1.totalAnswers, obj2.totalAnswers);

                        } else {
                            return Integer.compare(obj2.totalAnswers, obj1.totalAnswers);
                        }

                    }


                });

                break;
            case "2":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModelDg>()

                {
                    @Override
                    public int compare(AskExpertQuestionModelDg obj1, AskExpertQuestionModelDg obj2) {
                        // ## Ascending order

                        if (isAscn) {

                            return Integer.compare(obj2.totalViews, obj1.totalViews);

                        } else {

                            return Integer.compare(obj1.totalViews, obj2.totalViews);
                        }

                    }
                });

                break;
            case "3":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModelDg>()

                {

                    @Override
                    public int compare(AskExpertQuestionModelDg obj1, AskExpertQuestionModelDg obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.userQuestion.compareToIgnoreCase(obj2.userQuestion);

                        } else {
                            return obj2.userQuestion.compareToIgnoreCase(obj1.userQuestion);
                        }
                    }
                });

                break;
            case "default":
                break;

        }

        this.notifyDataSetChanged();
    }

    void gotoRespectiveActivity(int finalResourceId, String finalFileExtesnion, AskExpertQuestionModelDg askExpertQuestionModel, String attachimgUrl) {

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
                    pdfIntent.putExtra("PDF_FILENAME", askExpertQuestionModel.userQuestionImagePath);
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
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.userQuestion);
                    activity.startActivity(intentSocial);
                    break;
                case "":
                    Toast.makeText(activity, getLocalizationValue(JsonLocalekeys.commonalerttitle_subtitle_invalidfiletypetitle), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    intentSocial.putExtra("ATTACHMENT", true);
                    intentSocial.putExtra(StaticValues.KEY_SOCIALLOGIN, attachedURL);
                    intentSocial.putExtra(StaticValues.KEY_ACTIONBARTITLE, askExpertQuestionModel.userQuestion);
                    activity.startActivity(intentSocial);
                    break;

            }
        }

    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, activity);

    }
}


