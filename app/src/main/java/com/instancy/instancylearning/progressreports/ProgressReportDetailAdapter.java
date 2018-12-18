package com.instancy.instancylearning.progressreports;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
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
import com.instancy.instancylearning.models.ReportDetail;
import com.instancy.instancylearning.models.ReportDetailsForQuestions;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.sendAppToBackground;
import static com.instancy.instancylearning.utils.Utilities.upperCaseWords;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ProgressReportDetailAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ProgressReportQuestionDetailsModel> reportDetailsForQuestionsList = null;

    private String TAG = ProgressReportDetailAdapter.class.getSimpleName();

    private List<ProgressReportQuestionDetailsModel> searchList;
    UiSettingsModel uiSettingsModel;

    public ProgressReportDetailAdapter(Activity activity, List<ProgressReportQuestionDetailsModel> reportDetailsForQuestionsList) {
        this.activity = activity;
        this.reportDetailsForQuestionsList = reportDetailsForQuestionsList;

        this.searchList = new ArrayList<ProgressReportQuestionDetailsModel>();

        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    public void refreshList(List<ProgressReportQuestionDetailsModel> reportDetailsForQuestionsList) {
        this.reportDetailsForQuestionsList = reportDetailsForQuestionsList;
        this.searchList = new ArrayList<ProgressReportQuestionDetailsModel>();
        this.searchList.addAll(reportDetailsForQuestionsList);

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return reportDetailsForQuestionsList != null ? reportDetailsForQuestionsList.size() : 0;

    }

    @Override
    public Object getItem(int position) {

        return reportDetailsForQuestionsList.get(position);


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
        convertView = inflater.inflate(R.layout.progressreportsdetailcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;

//        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));


        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        holder.txtTitle.setText(reportDetailsForQuestionsList.get(position).questionTitle);

        holder.txtViewQuestion.setVisibility(View.GONE);

        String status = reportDetailsForQuestionsList.get(position).status;


        if (status.equalsIgnoreCase("complete") || status.equalsIgnoreCase("passed")) {

            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusCompleted));
        } else if (status.equalsIgnoreCase("incomplete")) {
            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusNotStarted));

        } else if (status.equalsIgnoreCase("undefined")) {

            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorBlack));
        } else if (status.equalsIgnoreCase("not attempted")) {
            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorStatusNotStarted));

        } else if (status.equalsIgnoreCase("attempted")) {

            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorBlack));
        } else if (status.equalsIgnoreCase("not visited")) {

            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorBlack));
        } else if (status.equalsIgnoreCase("na")) {

            holder.txtStatus.setTextColor(convertView.getResources().getColor(R.color.colorBlack));
        }

        holder.txtStatus.setText(upperCaseWords(status));

        return convertView;
    }


    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.txtTitle)
        TextView txtTitle;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtStatus)
        TextView txtStatus;

        @Nullable
        @BindView(R.id.txtViewQuestion)
        TextView txtViewQuestion;

    }
}


