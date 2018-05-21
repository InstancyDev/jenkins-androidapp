package com.instancy.instancylearning.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.instancy.instancylearning.R;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 */

public class SettingsInnerAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;


    public SettingsInnerAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.askexpertskillcell, null);
        }

        Switch switchSkill = (Switch) convertView.findViewById(R.id.swtchskills);


        TextView labelTxt = (TextView) convertView
                .findViewById(R.id.languagelabel);

        TextView applanguageTxt = (TextView) convertView
                .findViewById(R.id.applanguage);

        if (expandableListTitle.get(groupPosition).contains("LANGUAGE")) {
            switchSkill.setVisibility(View.GONE);
            labelTxt.setVisibility(View.VISIBLE);
            applanguageTxt.setVisibility(View.VISIBLE);

            applanguageTxt.setText("English");

            labelTxt.setText(expandedListText);


        }


        switchSkill.setText(expandedListText);

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
