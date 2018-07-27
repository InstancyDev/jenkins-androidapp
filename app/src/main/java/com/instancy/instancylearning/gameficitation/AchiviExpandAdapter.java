package com.instancy.instancylearning.gameficitation;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.Ach_UserBadges;
import com.instancy.instancylearning.models.Ach_UserLevel;
import com.instancy.instancylearning.models.Ach_UserPoints;
import com.instancy.instancylearning.models.GamificationModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UserEducationModel;
import com.instancy.instancylearning.models.UserExperienceModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * http://thedeveloperworldisyours.com/android/notifydatasetchanged/
 */

public class AchiviExpandAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<GamificationModel> expandableListTitle;
    private List<Ach_UserBadges> achUserBadgesList;
    private List<Ach_UserLevel> achUserLevelList;
    private List<Ach_UserPoints> achUserPointsList;
    private UiSettingsModel uiSettingsModel;


    public AchiviExpandAdapter(Context context, List<Ach_UserBadges> achUserBadgesList, List<Ach_UserLevel> achUserLevelList, List<GamificationModel> expandableListTitle, List<Ach_UserPoints> achUserPointsList) {
        this.context = context;
        this.achUserBadgesList = achUserBadgesList;
        this.achUserLevelList = achUserLevelList;
        this.achUserPointsList = achUserPointsList;
        this.expandableListTitle = expandableListTitle;
        uiSettingsModel = UiSettingsModel.getInstance();
        this.notifyDataSetChanged();
    }


    public void refreshList( List<Ach_UserBadges> achUserBadgesList, List<Ach_UserLevel> achUserLevelList, List<GamificationModel> expandableListTitle, List<Ach_UserPoints> achUserPointsList) {
        this.achUserLevelList = achUserLevelList;
        this.achUserPointsList = achUserPointsList;
        this.achUserBadgesList = achUserBadgesList;
        this.expandableListTitle = expandableListTitle;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {


        if (expandableListTitle!=null && expandableListTitle.size()>0){
            if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("1")) {

                return this.achUserPointsList == null ? 0 : this.achUserPointsList
                        .size();

            }
            if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("2")) {

                return this.achUserLevelList == null ? 0 : this.achUserLevelList
                        .size();

            }

            if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("3")) {

                return this.achUserBadgesList == null ? 0 : this.achUserBadgesList
                        .size();

            }
            else {

                return 0;
            }

        }
        else {

            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return null;
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
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pView = inflater.inflate(R.layout.achivmentsgroupcell, parent, false);

        TextView listTitleTextView = (TextView) pView
                .findViewById(R.id.groupname);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(expandableListTitle.get(groupPosition).groupname);
        return pView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int childType = groupPosition;

        // We need to create a new "cell container"
        if (convertView == null ) {
            switch (expandableListTitle.get(groupPosition).groupId) {
                case "1":
                    convertView = inflater.inflate(R.layout.achivmentspointscell, null);
                    TextView textCount =convertView.findViewById(R.id.txt_count);
                    TextView textAwardedOn =convertView.findViewById(R.id.txt_awardedon);
                    TextView textTitle =convertView.findViewById(R.id.txt_title);
                    textCount.setText(""+achUserPointsList.get(childPosition).points);
                    textAwardedOn.setText("Awarded on "+achUserPointsList.get(childPosition).userReceivedDate);
                    textTitle.setText(""+achUserPointsList.get(childPosition).pointsDescription);
                    textCount.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    textAwardedOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    textTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    convertView.setTag(childType);
                    break;
                case "2":
                    convertView = inflater.inflate(R.layout.achivmentslevelcell, null);
                    TextView textPoints =convertView.findViewById(R.id.txtPoints);
                    TextView textAwarded =convertView.findViewById(R.id.txtAwardedon);
                    TextView txtLevel =convertView.findViewById(R.id.txtLevel);
                    txtLevel.setText(""+achUserLevelList.get(childPosition).levelName);
                    textAwarded.setText("Awarded on "+achUserLevelList.get(childPosition).levelReceivedDate);
                    textPoints.setText(""+achUserLevelList.get(childPosition).levelPoints);
                    textPoints.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    textAwarded.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    txtLevel.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    convertView.setTag(childType);
                    break;
                case "3":
                    convertView = inflater.inflate(R.layout.achivmentsbadgescell, null);
                    TextView txtTitle =convertView.findViewById(R.id.txt_title);
                    TextView textDesc =convertView.findViewById(R.id.txt_description);
                    TextView textAwardedBadge =convertView.findViewById(R.id.txt_awardedon);
                    ImageView badgeImage= (ImageView)convertView.findViewById(R.id.imageBadge);
                    txtTitle.setText(""+achUserBadgesList.get(childPosition).badgeName);
                    textAwardedBadge.setText("Awarded on "+achUserBadgesList.get(childPosition).badgeReceivedDate);
                    textDesc.setText(""+achUserBadgesList.get(childPosition).badgeDescription);
                    String imgUrl = achUserBadgesList.get(childPosition).badgeImage;
                    Picasso.with(context).load(imgUrl).placeholder(R.drawable.badge).into(badgeImage);
                    txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    textDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    textAwardedBadge.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
                    convertView.setTag(childType);
                    break;
                case "23":
                    convertView = inflater.inflate(R.layout.achivmentsgroupcell, null);
                    convertView.setTag(childType);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 4 child types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }

//        switch (childType) {
//            case CHILD_TYPE_1:
//                break;
//            case CHILD_TYPE_2:
//                //Define how to render the data on the CHILD_TYPE_2 layout
//                break;
//            case CHILD_TYPE_3:
//                //Define how to render the data on the CHILD_TYPE_3 layout
//                break;
//            case CHILD_TYPE_UNDEFINED:
//                //Define how to render the data on the CHILD_TYPE_UNDEFINED layout
//                break;
//        }

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
