package com.instancy.instancylearning.mycompetency;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.StaticValues;

import java.util.HashMap;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;

public class CompetencyCatSkillAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<CompetencyCategoryModel> mainMenuList;
    private HashMap<Integer, List<CompetencyCategoryModel>> subMenuList;
    private List<SkillModel> skillModelList;
    Typeface iconFon;


    UiSettingsModel uiSettingsModel;

    public List<CompetencyCategoryModel> getMainMenuList() {
        return mainMenuList;
    }

    public void setMainMenuList(List<CompetencyCategoryModel> mainMenuList) {
        this.mainMenuList = mainMenuList;
    }

    public void refreshList(List<CompetencyCategoryModel> mainMenuList, List<SkillModel> skillModelList) {
        this.mainMenuList = mainMenuList;
        this.skillModelList = skillModelList;
        this.notifyDataSetChanged();
    }

    public HashMap<Integer, List<CompetencyCategoryModel>> getSubMenuList() {
        return subMenuList;
    }


    public CompetencyCatSkillAdapter(Context ctx, int resource,
                                     List<CompetencyCategoryModel> mainMenuList) {

        this.ctx = ctx;
        this.subMenuList = subMenuList;
        this.mainMenuList = mainMenuList;

        uiSettingsModel = UiSettingsModel.getInstance();

        iconFon = FontManager.getTypeface(ctx, FontManager.FONTAWESOME);

    }

    @Override
    public int getGroupCount() {
        if (mainMenuList != null) {
            return mainMenuList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int parentPosition) {
        if (mainMenuList != null && skillModelList != null) {
//            List<SideMenusModel> subMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
            if (skillModelList != null) {
                return skillModelList.size();
            } else {
                return 0;
            }

        } else {
            return 0;
        }
    }

    @Override
    public CompetencyCategoryModel getGroup(int parentPosition) {
        if (mainMenuList != null) {
            return mainMenuList.get(parentPosition);
        } else {
            return null;
        }
    }

    @Override
    public SideMenusModel getChild(int parentPosition, int childPosition) {
        if (mainMenuList != null && subMenuList != null) {
//            List<SideMenusModel> childMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
//            if (childMenus != null) {
//                return childMenus.get(childPosition);
//            } else {
            return null;
//            }
        } else {
            return null;
        }
    }

    @Override
    public long getGroupId(int parentPosition) {
        if (mainMenuList != null) {
            return parentPosition;
        } else {
            return 0;
        }
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        if (subMenuList != null) {
            return childPosition;
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parentPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView) {
        CompetencyCategoryModel mainMenu = getGroup(parentPosition);

        View pView = convertView;
        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.competencyjobcell, parentView, false);
        }

        TextView txtTitle = (TextView) pView.findViewById(R.id.jobrolename);
        txtTitle.setText(mainMenu.prefCategoryTitle);
        return pView;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parentView) {
//        SideMenusModel childMenu = getChild(parentPosition, childPosition);

        View cView = convertView;
        if (cView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = inflater.inflate(R.layout.drawermenu_sub_item, parentView, false);
        }

        TextView txtTitle = (TextView) cView.findViewById(R.id.submenuText);
        TextView fontIcon = (TextView) cView.findViewById(R.id.fontawasomesubIcon);
        FontManager.markAsIconContainer(cView.findViewById(R.id.fontawasomesubIcon), iconFon);
        fontIcon.setText(cView.getResources().getString(R.string.fa_icon_home));

        txtTitle.setText(skillModelList.get(childPosition).skillName);

        return cView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v,
//                                int groupPosition, int childPosition, long id) {
//
//        int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
//        parent.setItemChecked(index, true);
//
//
//        return true;
//    }

}
