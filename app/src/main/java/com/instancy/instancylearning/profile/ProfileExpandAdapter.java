package com.instancy.instancylearning.profile;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.ProfileGroupModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * http://thedeveloperworldisyours.com/android/notifydatasetchanged/
 */

public class ProfileExpandAdapter extends BaseExpandableListAdapter {

    Typeface iconFon;
    private Context context;
    private List<ProfileGroupModel> expandableListTitle;
    private HashMap<String, List<ProfileConfigsModel>> expandableHashDetail;

    public ProfileExpandAdapter(Context context, List<ProfileGroupModel> expandableListTitle, HashMap<String, List<ProfileConfigsModel>> expandableHashDetail) {
        this.context = context;
        this.expandableHashDetail = expandableHashDetail;
        this.expandableListTitle = expandableListTitle;
//        Collections.sort(this.expandableListTitle, Collections.reverseOrder());
        iconFon = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableHashDetail.get(expandableListTitle.get(groupPosition).groupname) == null ? 0 : this.expandableHashDetail.get(expandableListTitle.get(groupPosition).groupname)
                .size();


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
        ImageView moreOptions=(ImageView)pView.findViewById(R.id.moreoptionsicon);
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
        return pView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ProfileConfigsModel configsModel = (ProfileConfigsModel) getChild(groupPosition, childPosition);

        View cView = convertView;
        if (cView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = inflater.inflate(R.layout.profile_item, parent, false);

            if (isLastChild) {
                cView.setBackground(cView.getResources().getDrawable(R.drawable.profileitembottom));
            }
        }
            TextView profileLabel = (TextView) cView
                    .findViewById(R.id.profile_label);
            TextView profileValue = (TextView) cView
                    .findViewById(R.id.profile_value);
            profileLabel.setText(configsModel.attributedisplaytext);
            profileValue.setText(configsModel.valueName);
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
