package com.instancy.instancylearning.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.UserEducationModel;
import com.instancy.instancylearning.models.UserExperienceModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * http://thedeveloperworldisyours.com/android/notifydatasetchanged/
 */

public class ProfileExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ProfileGroupModel> expandableListTitle;
    private List<UserEducationModel> userEducationModelList;
    private List<UserExperienceModel> userExperienceModelList;
    private HashMap<String, List<ProfileConfigsModel>> expandableHashDetail;
    private ExpandableListView exp;

    public ProfileExpandAdapter(Context context, ExpandableListView exp, List<UserExperienceModel> userExperienceModelList, List<UserEducationModel> userEducationModelList, List<ProfileGroupModel> expandableListTitle, HashMap<String, List<ProfileConfigsModel>> expandableHashDetail) {
        this.context = context;
        this.userEducationModelList = userEducationModelList;
        this.userExperienceModelList = userExperienceModelList;
        this.expandableHashDetail = expandableHashDetail;
        this.expandableListTitle = expandableListTitle;
        this.exp = exp;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education")) {

            return this.userEducationModelList == null ? 0 : this.userEducationModelList
                    .size();

        }
        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {

            return this.userExperienceModelList == null ? 0 : this.userExperienceModelList
                    .size();

        } else {
            return this.expandableHashDetail.get(expandableListTitle.get(groupPosition).groupname) == null ? 0 : this.expandableHashDetail.get(expandableListTitle.get(groupPosition).groupname)
                    .size();
        }

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.expandableHashDetail.get(this.expandableListTitle.get(groupPosition).groupname)
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        View pView = convertView;
        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.profilesectionview, parent, false);

        }
        ImageView moreOptions = (ImageView) pView.findViewById(R.id.moreoptionsicon);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("TAG", "onClick: ");
            }
        });

        TextView listTitleTextView = (TextView) pView
                .findViewById(R.id.profilesection);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(expandableListTitle.get(groupPosition).groupname);
//        exp.setDividerHeight(20);

        return pView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ProfileConfigsModel configsModel = new ProfileConfigsModel();
        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education") || expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {

        } else {
            configsModel = (ProfileConfigsModel) getChild(groupPosition, childPosition);
        }
        View cView = convertView;

        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education") || expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {

//            if (cView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cView = inflater.inflate(R.layout.profile_education_item, parent, false);
//            }

            TextView profileSchool = (TextView) cView
                    .findViewById(R.id.profile_school);
            TextView profileDegree = (TextView) cView
                    .findViewById(R.id.profile_degree);

            TextView profileDuration = (TextView) cView
                    .findViewById(R.id.profile_duration);

            if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education")) {
                profileSchool.setText(this.userEducationModelList.get(childPosition).school);
                profileDegree.setText(this.userEducationModelList.get(childPosition).titleeducation);
                profileDuration.setText(this.userEducationModelList.get(childPosition).totalperiod);
            } else if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {
                profileSchool.setText(this.userExperienceModelList.get(childPosition).title);
                profileDegree.setText(this.userExperienceModelList.get(childPosition).companyName);
                profileDuration.setText(this.userExperienceModelList.get(childPosition).fromDate);

            }
        } else {

            if (cView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cView = inflater.inflate(R.layout.profile_item, parent, false);

            }

            try {
                TextView profileLabel = (TextView) cView
                        .findViewById(R.id.profile_label);
                TextView profileValue = (TextView) cView
                        .findViewById(R.id.profile_value);
                profileLabel.setText(configsModel.attributedisplaytext);
                profileValue.setText(configsModel.valueName);
            } catch (NullPointerException ex) {

                ex.printStackTrace();
            }
        }
        if (isLastChild) {
            cView.setBackground(cView.getResources().getDrawable(R.drawable.profileitembottom));
        }

//        exp.setDividerHeight(0);
        return cView;
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