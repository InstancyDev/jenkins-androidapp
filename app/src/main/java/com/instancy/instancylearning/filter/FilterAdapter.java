package com.instancy.instancylearning.filter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.advancedfilters.AdvancedFilterModel;
import com.instancy.instancylearning.advancedfilters.FiltersSerilization;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.NativeSetttingsModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * http://thedeveloperworldisyours.com/android/notifydatasetchanged/
 */

public class FilterAdapter extends BaseExpandableListAdapter {

    Typeface iconFon;
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<AdvancedFilterModel>> expandableListDetail;

    public FilterAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<AdvancedFilterModel>> expandableListDetail) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        Collections.sort(this.expandableListTitle, Collections.reverseOrder());
        iconFon = FontManager.getTypeface(context, FontManager.FONTAWESOME);
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
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final AdvancedFilterModel expandedListText = (AdvancedFilterModel) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.filter_item, null);
        }

        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.sortfilter);

        ImageView iconimage = (ImageView) convertView.findViewById(R.id.expIcon);
        TextView sortAwasomeIcon = (TextView) convertView
                .findViewById(R.id.sortawasome);

        if (expandedListText.cellIdentifier == 1) {
            expandedListTextView.setText(expandedListText.aliasName);
        } else if (expandedListText.cellIdentifier == 2) {
            expandedListTextView.setText(expandedListText.sortTypeName);
        } else if (expandedListText.cellIdentifier == 3) {
            expandedListTextView.setText(expandedListText.categoryName);
        }
        FontManager.markAsIconContainer(convertView.findViewById(R.id.sortawasome), iconFon);
        if (groupPosition == 0) {
            sortAwasomeIcon.setVisibility(View.VISIBLE);
        } else {
            sortAwasomeIcon.setVisibility(View.GONE);
        }
        expandedListTextView.setTextColor(convertView.getResources().getColor(R.color.colorDarkGrey));
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
