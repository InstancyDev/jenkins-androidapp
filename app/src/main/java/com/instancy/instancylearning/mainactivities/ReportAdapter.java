package com.instancy.instancylearning.mainactivities;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.ReportDetail;
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

public class ReportAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ReportDetail> reportDetailList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;


    private String TAG = ReportAdapter.class.getSimpleName();

    private List<ReportDetail> searchList;


    public ReportAdapter(Activity activity, int resource, List<ReportDetail> reportDetailList ) {
        this.activity = activity;
        this.reportDetailList = reportDetailList;
        this.searchList = new ArrayList<ReportDetail>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();


    }

    public void refreshList(List<ReportDetail> reportDetailList) {
        this.reportDetailList = reportDetailList;
        this.searchList = new ArrayList<ReportDetail>();
        this.searchList.addAll(reportDetailList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reportDetailList != null ? reportDetailList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return reportDetailList.get(position);
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

        holder.txtName.setText(reportDetailList.get(position).courseName);
        holder.txtDateCompleted.setText("Date Completed:" + reportDetailList.get(position).dateCompleted);
        holder.txtScore.setText("Score:" + reportDetailList.get(position).score + " ");
        holder.txtStartDate.setText("Date Started :" + reportDetailList.get(position).dateStarted + " ");
        holder.txtStatus.setText("Status:" + reportDetailList.get(position).status);
        holder.txtTimeSpent.setText("Time Spent:" + reportDetailList.get(position).timeSpent);


        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStartDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTimeSpent.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


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


    }
}


