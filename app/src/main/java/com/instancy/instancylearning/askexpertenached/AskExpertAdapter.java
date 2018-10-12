package com.instancy.instancylearning.askexpertenached;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.interfaces.TagClicked;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.CustomFlowLayout;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private List<AskExpertQuestionModel> askExpertQuestionModelList = null;
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
    TagClicked tagClicked;


    public AskExpertAdapter(Activity activity, int resource, List<AskExpertQuestionModel> askExpertQuestionModelList, TagClicked tagClicked) {
        this.activity = activity;
        this.askExpertQuestionModelList = askExpertQuestionModelList;
        this.searchList = new ArrayList<AskExpertQuestionModel>();
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

    public void refreshList(List<AskExpertQuestionModel> myLearningModel) {
        this.askExpertQuestionModelList = myLearningModel;
        this.searchList = new ArrayList<AskExpertQuestionModel>();
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
        holder.txtAllActivites.setText("Asked by: " + askExpertQuestionModelList.get(position).username + "   |   " + "Asked on: " + askExpertQuestionModelList.get(position).postedDate + "   |   " + "Last active: " + askExpertQuestionModelList.get(position).postedDate);
        holder.txtNoAnswers.setText(askExpertQuestionModelList.get(position).answers + " Answer(s)");
        holder.txtQuestion.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAllActivites.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtNoAnswers.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        holder.tagsSkills.setVisibility(View.GONE);


        if (askExpertQuestionModelList.get(position).userID.equalsIgnoreCase(askExpertQuestionModelList.get(position).postedUserId)) {

            holder.btnContextMenu.setVisibility(View.VISIBLE);

        } else {

            holder.btnContextMenu.setVisibility(View.INVISIBLE);

        }

        List<ContentValues> breadcrumbItemsList = null;

        breadcrumbItemsList = new ArrayList<ContentValues>();
        breadcrumbItemsList = generateTagsList(position);

        generateBreadcrumb(breadcrumbItemsList, holder.tagsSkills, convertView.getContext());

        return convertView;
    }

    public List<ContentValues> generateTagsList(int position) {
        List<ContentValues> tagsList = new ArrayList<>();

        for (int i = 1; i < position; i++) {
            ContentValues cvBreadcrumbItem = new ContentValues();
            cvBreadcrumbItem.put("categoryid", i);
            cvBreadcrumbItem.put("categoryname", "Skill " + i);
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
            for (AskExpertQuestionModel s : searchList) {
                if (s.userQuestion.toLowerCase(Locale.getDefault()).contains(charText) || s.username.toLowerCase(Locale.getDefault()).contains(charText)) {
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
        @BindView(R.id.txtno_answers)
        TextView txtNoAnswers;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.cflBreadcrumb)
        CustomFlowLayout tagsSkills;

        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtno_answers})
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

                tagClicked.tagClickedInterface();

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

            arrowView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><medium><b>"
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

            textView.setText(Html.fromHtml("<font color='" + context.getResources().getColor(R.color.colorInGreen) + "'><small>"
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

            case "1":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModel>() {

                    @Override
                    public int compare(AskExpertQuestionModel obj1, AskExpertQuestionModel obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.createdDate.compareToIgnoreCase(obj2.createdDate);

                        } else {
                            return obj2.createdDate.compareToIgnoreCase(obj1.createdDate);
                        }
                    }
                });
                break;
            case "2":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModel>() {

                    @Override
                    public int compare(AskExpertQuestionModel obj1, AskExpertQuestionModel obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.answers.compareToIgnoreCase(obj2.answers);

                        } else {
                            return obj2.answers.compareToIgnoreCase(obj1.answers);
                        }

                    }
                });
                break;
            case "3":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModel>() {
                    @Override
                    public int compare(AskExpertQuestionModel obj1, AskExpertQuestionModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.userQuestion.compareToIgnoreCase(obj2.userQuestion);

                        } else {
                            return obj2.userQuestion.compareToIgnoreCase(obj1.userQuestion);
                        }
                    }
                });
                break;
            case "4":
                Collections.sort(askExpertQuestionModelList, new Comparator<AskExpertQuestionModel>() {

                    @Override
                    public int compare(AskExpertQuestionModel obj1, AskExpertQuestionModel obj2) {
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

}


