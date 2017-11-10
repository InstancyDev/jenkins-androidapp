package com.instancy.instancylearning.profile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.HashMap;
import java.util.List;

public class ProfileDynamicAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<SideMenusModel> mainMenuList;
    private HashMap<Integer, List<SideMenusModel>> subMenuList;

    UiSettingsModel uiSettingsModel;

    public List<SideMenusModel> getMainMenuList() {
        return mainMenuList;
    }

    public void setMainMenuList(List<SideMenusModel> mainMenuList) {
        this.mainMenuList = mainMenuList;
    }

    public HashMap<Integer, List<SideMenusModel>> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(HashMap<Integer, List<SideMenusModel>> subMenuList) {
        this.subMenuList = subMenuList;
    }

    public ProfileDynamicAdapter(Context ctx, HashMap<Integer, List<SideMenusModel>> subMenuList,
                                 List<SideMenusModel> mainMenuList) {
        this.ctx = ctx;
        this.subMenuList = subMenuList;
        this.mainMenuList = mainMenuList;
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    @Override
    public int getGroupCount() {

        return 2;

    }

    @Override
    public int getChildrenCount(int parentPosition) {

        return 3;
    }

    @Override
    public SideMenusModel getGroup(int parentPosition) {
        if (mainMenuList != null) {
            return mainMenuList.get(parentPosition);
        } else {
            return null;
        }
    }

    @Override
    public SideMenusModel getChild(int parentPosition, int childPosition) {
        if (mainMenuList != null && subMenuList != null) {
            List<SideMenusModel> childMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
            if (childMenus != null) {
                return childMenus.get(childPosition);
            } else {
                return null;
            }
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
//        SideMenusModel mainMenu = getGroup(parentPosition);

        View pView = convertView;
        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.profilesectionview, parentView, false);
        }

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
            cView = inflater.inflate(R.layout.profile_item, parentView, false);

//            Log.d("TAG", "getChildView: "+getChildrenCount(childPosition));
            if (isLastChild) {
                cView.setBackground(cView.getResources().getDrawable(R.drawable.profileitembottom));
            }
        }


        return cView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
