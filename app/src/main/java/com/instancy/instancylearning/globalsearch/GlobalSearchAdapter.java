package com.instancy.instancylearning.globalsearch;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.GlobalSearchCategoryModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail;
    UiSettingsModel uiSettingsModel;

    public GlobalSearchAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    public void refreshList(List<String> expandableListTitle, HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail) {
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        this.notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return this.expandableListDetail.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.nativesettingsplaingroup, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.category_title);

        listTitleTextView.setText(listTitle);


        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final GlobalSearchCategoryModel expandedListText = (GlobalSearchCategoryModel) getChild(groupPosition, childPosition);


        View vi = convertView;
        ViewHolder holder;
        if (vi == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.globalcheckcell, null);
            holder = new ViewHolder(vi);
            holder.getPosition = childPosition;
            holder.cellView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
            holder.bottomLine.setVisibility(View.GONE);
            holder.txtTitle.setText(expandedListText.displayName);


            holder.txtTitle.setChecked(expandedListText.chxBoxChecked);


            holder.txtTitle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {

                        expandedListText.chxBoxChecked = true;


                    } else {
                        expandedListText.chxBoxChecked = false;

                    }
                }
            });


        }

        return vi;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;

    }

    class ViewHolder {
        public int getPosition;

        @Nullable
        @BindView(R.id.bottomLine)
        TextView bottomLine;

        @Nullable
        @BindView(R.id.chxBox)
        CheckBox txtTitle;

        @Nullable
        @BindView(R.id.cellview)
        RelativeLayout cellView;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);


        }
    }


}

