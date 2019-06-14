package com.instancy.instancylearning.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 */

public class SettingsInnerModelAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<InnerSettingsModel>> expandableListDetail;
    ExpandableListView expandableListView;

    public SettingsInnerModelAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<InnerSettingsModel>> expandableListDetail, ExpandableListView expandableListView) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
    }


    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();

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
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final InnerSettingsModel innerSettingsModel = (InnerSettingsModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.loclaeselect, null);
        }

        Switch switchSkill = (Switch) convertView.findViewById(R.id.swtchskills);

        switchSkill.setEnabled(false);
        TextView labelTxt = (TextView) convertView
                .findViewById(R.id.languagelabel);

        TextView applanguageTxt = (TextView) convertView
                .findViewById(R.id.applanguage);

        if (innerSettingsModel.settingId == 2) {
            switchSkill.setVisibility(View.GONE);
            labelTxt.setVisibility(View.VISIBLE);
            applanguageTxt.setVisibility(View.VISIBLE);

            applanguageTxt.setText(PreferencesManager.getInstance().getLocalizationDisplayStringValue(context.getResources().getString(R.string.locale_display_name)));

            labelTxt.setText(innerSettingsModel.settingName);

//            labelTxt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    long packedPos = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
//
//                    int flatPos = expandableListView.getFlatListPosition(packedPos);
//
////Getting the ID for our child
//                    long id = expandableListView.getExpandableListAdapter().getChildId(groupPosition, childPosition);
//
//                    ((ExpandableListView) parent).performItemClick(view, flatPos, id);
//
//                }
//            });

        }

        switchSkill.setText(innerSettingsModel.settingName);

        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
