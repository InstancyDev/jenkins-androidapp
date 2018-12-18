package com.instancy.instancylearning.globalsearch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
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
    ExpandableListView expandableListView;

    public GlobalSearchAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<GlobalSearchCategoryModel>> expandableListDetail, ExpandableListView expandableListView) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        uiSettingsModel = UiSettingsModel.getInstance();
        this.expandableListView = expandableListView;
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
            convertView = layoutInflater.inflate(R.layout.globalgroup, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.groupheader);

        listTitleTextView.setText(listTitle);

        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final GlobalSearchCategoryModel expandedListText = (GlobalSearchCategoryModel) getChild(groupPosition, childPosition);

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.globalcheckcell, null);
            holder = new ViewHolder(convertView);
            holder.childPosition = childPosition;
            holder.groupPosition = groupPosition;
            holder.parent = parent;
            holder.view = convertView;
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.cellView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.bottomLine.setVisibility(View.GONE);

        holder.txtTitle.setText(expandedListText.displayName);

        holder.txtTitle.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));
        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTitle.setChecked(expandedListText.chxBoxChecked);
//        Log.d("CHX", "getChildView: " + expandedListText.chxBoxChecked + " @ " + childPosition);
        holder.txtTitle.setFocusable(false);

        final View finalVi = convertView;


        holder.txtTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                long packedPos = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
                int flatPos = expandableListView.getFlatListPosition(packedPos);

//Getting the ID for our child
                long id = expandableListView.getExpandableListAdapter().getChildId(groupPosition, childPosition);

                ((ExpandableListView) parent).performItemClick(finalVi, flatPos, id);
            }
        });

        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;

    }

    class ViewHolder {
        public int childPosition;
        public int groupPosition;
        public ViewGroup parent;
        public View view;
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


//        @OnCheckedChanged(R.id.chxBox)
//        void onGenderSelected(CheckBox button, boolean checked) {
//            //do your stuff.
//            Log.d("CHX", "onGenderSelected: " + childPosition);
//            ((ExpandableListView) parent).performItemClick(view, childPosition, groupPosition);
//
//        }

//        @OnClick({R.id.cellview})
//        public void actionsForMenu(View view) {
//
//        }

    }

}

