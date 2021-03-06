package com.instancy.instancylearning.mylearning;

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
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.ReportDetail;
import com.instancy.instancylearning.models.ReportDetailsForQuestions;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ReportAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ReportDetail> reportDetailList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    List<ReportDetailsForQuestions> reportDetailsForQuestionsList;

    private String TAG = ReportAdapter.class.getSimpleName();

    private List<ReportDetail> searchList;
    boolean isAssesment = false;

    public ReportAdapter(Activity activity, int resource, List<ReportDetail> reportDetailList, List<ReportDetailsForQuestions> reportDetailsForQuestionsList, boolean isAssesment) {
        this.activity = activity;
        this.reportDetailsForQuestionsList = reportDetailsForQuestionsList;
        this.reportDetailList = reportDetailList;
        this.searchList = new ArrayList<ReportDetail>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        this.isAssesment = isAssesment;
    }

    public void refreshList(List<ReportDetail> reportDetailList, List<ReportDetailsForQuestions> reportDetailsForQuestionsList, boolean isAssesment) {
        this.reportDetailList = reportDetailList;
        this.reportDetailsForQuestionsList = reportDetailsForQuestionsList;
        this.searchList = new ArrayList<ReportDetail>();
        this.searchList.addAll(reportDetailList);
        this.isAssesment = isAssesment;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (isAssesment) {
            return reportDetailsForQuestionsList != null ? reportDetailsForQuestionsList.size() : 0;
        } else {
            return reportDetailList != null ? reportDetailList.size() : 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (isAssesment) {
            return reportDetailsForQuestionsList.get(position);
        } else {
            return reportDetailList.get(position);
        }

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
        convertView = inflater.inflate(R.layout.reportscell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;

        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtStatusTitle.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_statuslabel));

        if (isAssesment) {
            holder.txtName.setText(reportDetailsForQuestionsList.get(position).questionID + ". " + reportDetailsForQuestionsList.get(position).questionName);

            holder.txtName.setTypeface(null, Typeface.NORMAL);

            if (reportDetailsForQuestionsList.get(position).questionAnswer.equalsIgnoreCase("na")) {
                holder.txtDateCompleted.setText(" " + reportDetailsForQuestionsList.get(position).questionAnswer);
            } else {
                holder.txtDateCompleted.setText(" " + upperCaseWords(reportDetailsForQuestionsList.get(position).questionAnswer));
            }

            holder.lineview.setVisibility(View.GONE);
            if (reportDetailsForQuestionsList.get(position).questionAnswer.contains("in")) {
                holder.txtDateCompleted.setTextColor(convertView.getResources().getColor(R.color.colorRed));
            } else {
                holder.txtDateCompleted.setTextColor(convertView.getResources().getColor(R.color.colorStatusCompleted));
            }
            holder.txtDateCompleted.setTextSize(16);
            holder.txtScore.setVisibility(View.GONE);
            holder.txtStartDate.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.GONE);
            holder.txtTimeSpent.setVisibility(View.GONE);
            holder.txtStatusTitle.setVisibility(View.GONE);


        } else {
            holder.txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

            holder.txtName.setText(reportDetailList.get(position).courseName);

            String dateCompleted = reportDetailList.get(position).dateCompleted;


            String dateStarted = reportDetailList.get(position).dateStarted;

            if (isValidString(dateCompleted)) {
                holder.txtDateCompleted.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_datecompletedlabel) + " " + dateCompleted);
            } else {
                holder.txtDateCompleted.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_datecompletedlabel) + " ");
            }

            if (isValidString(dateStarted)) {

                holder.txtStartDate.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_datestartedlabel) + " " + dateStarted + " ");
            } else {
                holder.txtStartDate.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_datestartedlabel) + " " + " ");
            }


//            holder.txtScore.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_scorelabel) + reportDetailList.get(position).score + " ");


            if (isValidString(reportDetailList.get(position).timeSpent)) {
                holder.txtTimeSpent.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_timespentlabel) + reportDetailList.get(position).timeSpent);
            } else {
                holder.txtTimeSpent.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_timespentlabel) + "0:00:00");
            }

            String statusFromModel = reportDetailList.get(position).status;

            if (isValidString(statusFromModel)) {
                if (statusFromModel.equalsIgnoreCase("Completed") || (statusFromModel.toLowerCase().contains("passed") || statusFromModel.toLowerCase().contains("failed")) || statusFromModel.equalsIgnoreCase("completed")) {

                    if (statusFromModel.toLowerCase().equalsIgnoreCase("failed")) {
                        statusFromModel = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel);
                    }

                    if (statusFromModel.toLowerCase().equalsIgnoreCase("passed")) {
                        statusFromModel = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel);
                    }

                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusCompleted));
                    //  reportDetailList.get(position).score = "100";
                } else if (statusFromModel.equalsIgnoreCase("Not Started")) {
                    //  reportDetailList.get(position).score = "0";
                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusNotStarted));

                } else if (statusFromModel.equalsIgnoreCase("incomplete") || (statusFromModel.toLowerCase().contains("inprogress")) || (statusFromModel.toLowerCase().contains("in progress"))) {
                    //   reportDetailList.get(position).score = "50";
                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusInProgress));
                    holder.txtStatus.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel));
                } else if (statusFromModel.equalsIgnoreCase("pending review") || (statusFromModel.toLowerCase().contains("pendingreview")) || (statusFromModel.toLowerCase().contains("grade"))) {
                    //   reportDetailList.get(position).score = "50";
                    statusFromModel = "Pending Review";
                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusOther));

                } else if (statusFromModel.equalsIgnoreCase("Registered") || (statusFromModel.toLowerCase().contains("registered"))) {
                    //    reportDetailList.get(position).score = "100";

                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorGray));


                } else if (statusFromModel.toLowerCase().contains("attended") || (statusFromModel.toLowerCase().contains("registered"))) {
                    //    reportDetailList.get(position).score = "100";

                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusOther));

                } else if (statusFromModel.toLowerCase().contains("Expired")) {

                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusOther));
                    //   reportDetailList.get(position).score = "0";
                } else {

                    holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorGray));
                }

            } else {
                holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorGray));
            }

            if (isValidString(statusFromModel)) {

                holder.txtStatus.setText(upperCaseWords(statusFromModel));
            } else {
                holder.txtStatus.setText("");
            }

            if (isValidString(reportDetailList.get(position).score)) {
                holder.txtScore.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_scorelabel) + " " + reportDetailList.get(position).score);
            } else {
                holder.txtScore.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_scorelabel) + " 0");
            }

            if (reportDetailList.get(position).objectTypeID.equalsIgnoreCase("9")) {
                holder.txtScore.setText(getLocalizationValue(JsonLocalekeys.mylearning_label_scorelabel) + " NA");
            }

        }

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStartDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTimeSpent.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStartDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStatusTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

///   from here


        return convertView;
    }


    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.txt_title)
        TextView txtName;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txt_starteddate)
        TextView txtStartDate;

        @Nullable
        @BindView(R.id.txt_datecompleted)
        TextView txtDateCompleted;

        @Nullable
        @BindView(R.id.txt_status)
        TextView txtStatus;

        @Nullable
        @BindView(R.id.txt_timespent)
        TextView txtTimeSpent;

        @Nullable
        @BindView(R.id.txt_score)
        TextView txtScore;

        @Nullable
        @BindView(R.id.txt_statustitle)
        TextView txtStatusTitle;

        @Nullable
        @BindView(R.id.lineview)
        View lineview;

    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, activity);
    }
}


