package com.instancy.instancylearning.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
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
//    private SharedPreferences sharedPrefs;
//    private String menubgcolor, menuslcolor, menubgalternatecolor,
//            menutextcolor, menusltextcolor; 17061790438

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

    public MenuDrawerDynamicAdapter(Context ctx,
                                    HashMap<Integer, List<SideMenusModel>> subMenuList,
                                    List<SideMenusModel> mainMenuList) {

        this.ctx = ctx;
        this.subMenuList = subMenuList;
        this.mainMenuList = mainMenuList;

        uiSettingsModel = UiSettingsModel.getInstance();

//
//        menubgcolor = sharedPrefs.getString("#MENU_BG_COLOR#", "#ffffff");
//        menuslcolor = sharedPrefs.getString("#MENU_SL_BG_COLOR#", "#E2FABB");
//        menubgalternatecolor = sharedPrefs.getString(
//                "#MENU_BG_ALTERNATIVECOLOR#", "#ffffff");
//
//        menutextcolor = sharedPrefs.getString("#MENU_TEXT_COLOR#", "#000000");
//        menusltextcolor = sharedPrefs.getString("#MENU_SL_TEXT_COLOR#",
//                "#000000");
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
        ImageView expIcon = (ImageView) pView.findViewById(R.id.expIcon);
        TextView fontIcon = (TextView) pView.findViewById(R.id.fontawasomeIcon);
        fontIcon.setTextColor(pView.getResources().getColor(R.color.colorBlack));
        FontManager.markAsIconContainer(pView.findViewById(R.id.fontawasomeIcon), iconFon);

        switch (mainMenu.getContextMenuId()) {

            case "1":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_graduation_cap));
                break;
            case "2":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_book));
                break;
            case "3":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_user));
                break;
            case "4":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_comments));
                break;
            case "6":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_home));
                break;
            case "7":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_wikipedia_w));
                break;
            case "8":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_calendar));
                break;
            case "9":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_users));
                break;
            case "9999":
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_sign_out));
                break;
            default:
                fontIcon.setText(pView.getResources().getString(R.string.fa_icon_align_left));
                break;
        }

        txtTitle.setText(mainMenu.getDisplayName());

        if (mainMenu.getIsSubMenuExists() == 1) {
            expIcon.setVisibility(View.VISIBLE);
            if (isExpanded) {
                Drawable exp = ctx.getResources().getDrawable(
                        R.drawable.ic_launcher);
                exp.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()),
                        PorterDuff.Mode.SRC_ATOP);

                expIcon.setImageDrawable(exp);
            } else {
                Drawable exp = ctx.getResources().getDrawable(
                        R.drawable.ic_launcher);
                exp.setColorFilter(Color.parseColor(uiSettingsModel.getHeaderTextColor()),
                        PorterDuff.Mode.SRC_ATOP);

                expIcon.setImageDrawable(exp);
            }
        } else {
            expIcon.setVisibility(View.GONE);
        }
//        if (parentPosition % 2 == 0) {
//            pView.setBackgroundColor(Color
//                    .parseColor("#5DB100"));
//        } else {
//            pView.setBackgroundColor(Color
//                    .parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
//        }
        return pView;
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

        ImageView imgIcon = (ImageView) cView.findViewById(R.id.subMenuIcon);
        TextView txtTitle = (TextView) cView.findViewById(R.id.subMenuText);
        imgIcon.setImageResource(childMenu.getMenuImageResId());
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
            imgIcon.getDrawable().mutate().setColorFilter(Color.parseColor(uiSettingsModel.getAppHeaderColor()), PorterDuff.Mode.SRC_ATOP);
        } else {
            if (parentPosition == MAIN_MENU_POSITION && childPosition == StaticValues.SUB_MENU_POSITION) {
                cView.setBackgroundColor(Color.parseColor(uiSettingsModel.getSelectedMenuBGColor()));
                txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
                imgIcon.getDrawable().mutate().setColorFilter(Color.parseColor(uiSettingsModel.getMenuTextColor()), PorterDuff.Mode.SRC_ATOP);
            } else {
                if (parentPosition % 2 == 0) {
                    cView.setBackgroundColor(Color
                            .parseColor(uiSettingsModel.getMenuBGColor()));
                } else {
                    cView.setBackgroundColor(Color
                            .parseColor(uiSettingsModel.getMenuBGAlternativeColor()));
                }
                txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getMenuTextColor()));
                imgIcon.getDrawable().mutate().setColorFilter(Color.parseColor(uiSettingsModel.getMenuTextColor()), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return cView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
