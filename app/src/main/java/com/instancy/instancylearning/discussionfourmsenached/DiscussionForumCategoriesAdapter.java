package com.instancy.instancylearning.discussionfourmsenached;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionForumCategoriesAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private Activity activity;
    private List<DiscussionCategoriesModel> discussionCategoriesModelList = null;
    private UiSettingsModel uiSettingsModel;
    private List<DiscussionCategoriesModel> searchList;

    public DiscussionForumCategoriesAdapter(Activity activity, List<DiscussionCategoriesModel> discussionCategoriesModelList) {

        this.discussionCategoriesModelList = discussionCategoriesModelList;
        this.searchList = new ArrayList<DiscussionCategoriesModel>();
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        this.activity = activity;

    }

    public void refreshList(List<DiscussionCategoriesModel> discussionCategoriesModelList) {
        this.discussionCategoriesModelList = discussionCategoriesModelList;
        this.searchList = new ArrayList<DiscussionCategoriesModel>();
        this.searchList.addAll(discussionCategoriesModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return discussionCategoriesModelList != null ? discussionCategoriesModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return discussionCategoriesModelList.get(position);
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
        convertView = inflater.inflate(R.layout.globalcheckcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.cellView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.checkBoxTitle.setText(discussionCategoriesModelList.get(position).fullName);
        holder.checkBoxTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.checkBoxTitle.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        holder.checkBoxTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.checkBoxTitle.setChecked(discussionCategoriesModelList.get(position).isSelected);

        holder.bottomLine.setVisibility(View.GONE);

        convertView.setTag(holder);
        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        discussionCategoriesModelList.clear();
        if (charText.length() == 0) {
            discussionCategoriesModelList.addAll(searchList);
        } else {
            for (DiscussionCategoriesModel s : searchList) {
                if (s.fullName.toLowerCase(Locale.getDefault()).contains(charText) || s.categoryName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    discussionCategoriesModelList.add(s);
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

        }

        @Nullable
        @BindView(R.id.bottomLine)
        TextView bottomLine;

        @Nullable
        @BindView(R.id.chxBox)
        CheckBox checkBoxTitle;

        @Nullable
        @BindView(R.id.cellview)
        RelativeLayout cellView;


        @OnClick({R.id.cellview, R.id.chxBox})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
}


