package com.instancy.instancylearning.adapters;


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

public class MenuDrawerDynamicAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<SideMenusModel> mainMenuList;
    private HashMap<Integer, List<SideMenusModel>> subMenuList;
    Typeface iconFon;


    UiSettingsModel uiSettingsModel;

    public List<SideMenusModel> getMainMenuList() {
        return mainMenuList;
    }

    public void setMainMenuList(List<SideMenusModel> mainMenuList) {
        this.mainMenuList = mainMenuList;
    }

    public void refreshList(List<SideMenusModel> mainMenuList, HashMap<Integer, List<SideMenusModel>> subMenuList) {
        this.mainMenuList = mainMenuList;
        this.subMenuList = subMenuList;

        this.notifyDataSetChanged();
    }


    public HashMap<Integer, List<SideMenusModel>> getSubMenuList() {
        return subMenuList;
    }

    public void setSubMenuList(HashMap<Integer, List<SideMenusModel>> subMenuList) {
        this.subMenuList = subMenuList;
    }

    public MenuDrawerDynamicAdapter(Context ctx, HashMap<Integer, List<SideMenusModel>> subMenuList,
                                    List<SideMenusModel> mainMenuList) {

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
        if (mainMenuList != null && subMenuList != null) {
            List<SideMenusModel> subMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
            if (subMenus != null) {
                return subMenus.size();
            } else {
                return 0;
            }

        } else {
            return 0;
        }
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
        SideMenusModel mainMenu = getGroup(parentPosition);

        View pView = convertView;
        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.drawermenu_item, parentView, false);
        }

        TextView txtTitle = (TextView) pView.findViewById(R.id.menuText);
        TextView expTxtIcon = (TextView) pView.findViewById(R.id.expIcon);
        TextView fontIcon = (TextView) pView.findViewById(R.id.fontawasomeIcon);

//        if (parentPosition % 2 == 0) {
//            pView.setBackgroundColor(Color
//                    .parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
//        } else {
////            pView.setBackgroundColor(Color
////                    .parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
//            pView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
//        }

        if (isExpanded) {
            pView.setBackgroundColor(Color.parseColor(uiSettingsModel.getSelectedMenuBGColor()));
            txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuBGSelectTextColor()));
            fontIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuBGSelectTextColor()));
            expTxtIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuBGSelectTextColor()));
            expTxtIcon.setVisibility(View.VISIBLE);
            expTxtIcon.setText(pView.getResources().getString(R.string.fa_icon_angle_up));
        } else {

//            if (parentPosition % 2 == 0) {
//                pView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
//            } else {
            pView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
//            }
            txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            fontIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            expTxtIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            expTxtIcon.setVisibility(View.VISIBLE);
            expTxtIcon.setText(pView.getResources().getString(R.string.fa_icon_angle_down));
        }

        FontManager.markAsIconContainer(pView.findViewById(R.id.fontawasomeIcon), iconFon);
        FontManager.markAsIconContainer(pView.findViewById(R.id.expIcon), iconFon);

        switch (mainMenu.getContextMenuId()) {

            case "1":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_leanpub));
                break;
            case "2":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_book));
                break;
            case "3":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_user));
                break;
            case "4":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_list_alt));
                break;
            case "5":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_question_circle));
                break;
            case "6":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_home));
                break;
            case "7":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_firefox));
                break;
            case "8":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_calendar));
                break;
            case "9":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_users));
                break;
            case "10":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_user_plus));
                break;
            case "11":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_bullseye));
                break;
            case "12":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_group));
                break;
            case "13":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_trophy));
                break;
            case "14":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_bar_chart));
                break;
            case "9999":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_sign_out));
                break;
            default:
                if (mainMenu.displayName.equalsIgnoreCase("connect")) {
                    fontIcon.setText(pView.getResources().getString(R.string.fa_icon_connectdevelop));
                } else {
                    fontIcon.setText(pView.getResources().getString(R.string.fa_icon_align_left));
                }
                break;
        }

        txtTitle.setText(mainMenu.getDisplayName());

        if (mainMenu.getIsSubMenuExists() == 1) {
            expTxtIcon.setVisibility(View.VISIBLE);

        } else {
            expTxtIcon.setVisibility(View.INVISIBLE);
        }

        return pView;
    }


    public String getRequiredIconForCatalog(String faIconFromServer) {
        String fa_Icon = ctx.getResources().getString(R.string.fa_icon_bar_chart);

        if (faIconFromServer.contains("far fa-book-reader")) {
            fa_Icon = ctx.getResources().getString(R.string.fa_icon_book);
        } else if (faIconFromServer.contains("far fa-chart-bar")) {
            fa_Icon = ctx.getResources().getString(R.string.fa_icon_bar_chart);
        } else if (faIconFromServer.contains("far fa-book-open")) {
            fa_Icon = ctx.getResources().getString(R.string.fa_icon_book);
        } else if (faIconFromServer.contains("far fa-project-diagram")) {
            fa_Icon = ctx.getResources().getString(R.string.fa_icon_diamond);
        } else {

        }

        return fa_Icon;
    }


    @Override
    public View getChildView(int parentPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parentView) {
        SideMenusModel childMenu = getChild(parentPosition, childPosition);

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

        switch (childMenu.getContextMenuId()) {

            case "1":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_leanpub));
                break;
            case "2":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_book));
                break;
            case "3":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_user));
                break;
            case "4":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_list_alt));
                break;
            case "5":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_question_circle));
                break;
            case "6":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_home));
                break;
            case "7":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_firefox));
                break;
            case "8":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_calendar));
                break;
            case "9":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_users));
                break;
            case "10":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_user_plus));
                break;
            case "11":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_bullseye));
                break;
            case "12":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_group));
                break;
            case "13":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_trophy));
                break;
            case "14":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_bar_chart));
                break;
            case "9999":
                fontIcon.setText(cView.getResources().getString(R.string.fa_icon_sign_out));
                break;
            default:

                if (childMenu.displayName.equalsIgnoreCase("connect")) {
                    fontIcon.setText(cView.getResources().getString(R.string.fa_icon_connectdevelop));
                } else {
                    fontIcon.setText(cView.getResources().getString(R.string.fa_icon_align_left));
                }
                break;
        }

        txtTitle.setText(childMenu.getDisplayName());
        if (MAIN_MENU_POSITION == -1) {
            for (int i = 0; i < mainMenuList.size() - 1; i++) {
                if (mainMenuList.get(i).getContextMenuId()
                        .equals("1")) {

                    MAIN_MENU_POSITION = i;
                }
            }
            cView.setBackgroundColor(Color.parseColor(uiSettingsModel.getMenuBGColor()));
            txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            fontIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));

        } else {
            if (parentPosition == MAIN_MENU_POSITION && childPosition == StaticValues.SUB_MENU_POSITION) {
                cView.setBackgroundColor(Color.parseColor(uiSettingsModel.getSelectedMenuBGColor()));
                txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuBGSelectTextColor()));
                fontIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuBGSelectTextColor()));
            } else {
//                if (parentPosition % 2 == 0) {
                cView.setBackgroundColor(Color
                        .parseColor(uiSettingsModel.getMenuBGColor()));
//                } else {
//                    cView.setBackgroundColor(Color
//                            .parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
//                }
                txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
                fontIcon.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
            }
        }
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
