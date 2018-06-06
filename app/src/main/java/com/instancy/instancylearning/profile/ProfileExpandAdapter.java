package com.instancy.instancylearning.profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;
import com.instancy.instancylearning.models.UiSettingsModel;
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
    Typeface iconFon;

    private UiSettingsModel uiSettingsModel;

    public ProfileExpandAdapter(Context context, List<UserExperienceModel> userExperienceModelList, List<UserEducationModel> userEducationModelList, List<ProfileGroupModel> expandableListTitle, HashMap<String, List<ProfileConfigsModel>> expandableHashDetail) {
        this.context = context;
        this.userEducationModelList = userEducationModelList;
        this.userExperienceModelList = userExperienceModelList;
        this.expandableHashDetail = expandableHashDetail;
        this.expandableListTitle = expandableListTitle;
        this.notifyDataSetChanged();
        iconFon = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    public void refreshList(List<UserExperienceModel> userExperienceModelList, List<UserEducationModel> userEducationModelList, List<ProfileGroupModel> expandableListTitle, HashMap<String, List<ProfileConfigsModel>> expandableHashDetail) {
        this.userEducationModelList = userEducationModelList;
        this.userExperienceModelList = userExperienceModelList;
        this.expandableHashDetail = expandableHashDetail;
        this.expandableListTitle = expandableListTitle;
        this.notifyDataSetChanged();
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
//        if (pView == null) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pView = inflater.inflate(R.layout.profilesectionview, parent, false);

        RelativeLayout headerlayout = (RelativeLayout) pView.findViewById(R.id.relativelayout);

        View view = (View) pView.findViewById(R.id.topview);

        Drawable mDrawable = context.getResources().getDrawable(R.drawable.sectionshape);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(uiSettingsModel.getAppBGColor()), PorterDuff.Mode.MULTIPLY));
        headerlayout.setBackground(mDrawable);

        TextView editIcon = (TextView) pView
                .findViewById(R.id.editoptionicon);

        editIcon.setVisibility(View.VISIBLE);

        editIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        FontManager.markAsIconContainer(pView.findViewById(R.id.editoptionicon), iconFon);

        TextView listTitleTextView = (TextView) pView
                .findViewById(R.id.profilesection);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(expandableListTitle.get(groupPosition).groupname);

        listTitleTextView.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education")) {
            editIcon.setText(pView.getResources().getString(R.string.fa_icon_plus_circle));
        } else if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {
            editIcon.setText(pView.getResources().getString(R.string.fa_icon_plus_circle));
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("1")) {
            editIcon.setText(pView.getResources().getString(R.string.fa_icon_edit));
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("2")) {
            editIcon.setText(pView.getResources().getString(R.string.fa_icon_edit));
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("6")) {
            editIcon.setText(pView.getResources().getString(R.string.fa_icon_edit));
        }

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

//        if (cView == null) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cView = inflater.inflate(R.layout.profile_education_item, parent, false);
//        }
//

        TextView profileSchool = (TextView) cView
                .findViewById(R.id.profile_school);
        TextView profileDegree = (TextView) cView
                .findViewById(R.id.profile_degree);

        TextView profileDuration = (TextView) cView
                .findViewById(R.id.profile_duration);

        profileSchool.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        profileDegree.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        profileDuration.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Education")) {
            profileSchool.setText(this.userEducationModelList.get(childPosition).school);
            profileDegree.setText(this.userEducationModelList.get(childPosition).titleeducation);
            profileDuration.setText(this.userEducationModelList.get(childPosition).totalperiod);
            profileDuration.setVisibility(View.VISIBLE);
        } else if (expandableListTitle.get(groupPosition).groupname.equalsIgnoreCase("Experience")) {
            profileSchool.setText(this.userExperienceModelList.get(childPosition).title);
            profileDegree.setText(this.userExperienceModelList.get(childPosition).companyName);
            profileDuration.setText(this.userExperienceModelList.get(childPosition).fromDate + " - " + this.userExperienceModelList.get(childPosition).toDate);
            profileDuration.setVisibility(View.VISIBLE);
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("1")) {
            profileSchool.setText(configsModel.attributedisplaytext);
            profileDegree.setText(configsModel.valueName);
            profileDuration.setVisibility(View.GONE);
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("2")) {
            profileSchool.setText(configsModel.attributedisplaytext);
            profileDegree.setText(configsModel.valueName);
            profileDuration.setVisibility(View.GONE);
        } else if (expandableListTitle.get(groupPosition).groupId.equalsIgnoreCase("6")) {
            profileSchool.setText(configsModel.attributedisplaytext);
            profileDegree.setText(configsModel.valueName);
            profileDuration.setVisibility(View.GONE);
        }

        if (isLastChild) {
//            cView.setBackground(cView.getResources().getDrawable(R.drawable.profileitembottom));

            Drawable mDrawable = context.getResources().getDrawable(R.drawable.profileitembottom);
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor(uiSettingsModel.getAppBGColor()), PorterDuff.Mode.MULTIPLY));
            cView.setBackground(mDrawable);

        } else {
//            cView.setBackground(cView.getResources().getDrawable(R.drawable.profileitemmiddle));
//            cView.getBackground().setColorFilter(Color.parseColor(uiSettingsModel.getAppHeaderColor()), PorterDuff.Mode.DARKEN);

            Drawable mDrawable = context.getResources().getDrawable(R.drawable.profileitemmiddle);
            mDrawable.setColorFilter(new
                    PorterDuffColorFilter(Color.parseColor(uiSettingsModel.getAppBGColor()), PorterDuff.Mode.MULTIPLY));
            cView.setBackground(mDrawable);
        }


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
