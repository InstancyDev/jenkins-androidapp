package com.instancy.instancylearning.models;

import java.io.Serializable;

public class SideMenusModel implements Serializable {
    public int getSiteID() {
        return siteID;
    }

    public void setSiteID(int siteID) {
        this.siteID = siteID;
    }

    public int siteID;
    public int menuId;
    public String displayName;
    public int displayOrder;
    public String image;
    public int menuImageResId;
    public String isOfflineMenu;
    public String isEnabled;
    public String contextTitle;
    public String contextMenuId;
    public String repositoryId;
    public String landingPageType = "0";
    public String categoryStyle = "0";
    public String componentId = "";
    public String conditions = "";
    public String parentMenuId = "";
    public String parameterStrings = "";
    public int isSubMenuExists = 0;
    public boolean isDataFound = false;



    public int getWebMenuId() {
        return webMenuId;
    }

    public void setWebMenuId(int webMenuId) {
        this.webMenuId = webMenuId;
    }

    public int webMenuId = 0;

    public boolean isDataFound() {
        return isDataFound;
    }

    public void setDataFound(boolean dataFound) {
        isDataFound = dataFound;
    }

    public SideMenusModel() {

    }

    public SideMenusModel(int menuId, String contextName, String displayName, int menuImageResId, int displayOrder, String isOfflineMenu, String isEnabled) {
        this.menuId = menuId;
        this.contextTitle = contextName;
        this.displayName = displayName;
        this.menuImageResId = menuImageResId;
        this.displayOrder = displayOrder;
        this.isOfflineMenu = isOfflineMenu;
        this.isEnabled = isEnabled;
    }

    public int getMenuId() {
        return this.menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String dispalyname) {
        this.displayName = dispalyname;
    }

    public int getDisplayOrder() {
        return this.displayOrder;
    }

    public void setDisplayOrder(int displayorder) {
        this.displayOrder = displayorder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMenuImageResId() {
        return this.menuImageResId;
    }

    public void setMenuImageResId(int menuimageresid) {
        this.menuImageResId = menuimageresid;
    }

    public String getIsOfflineMenu() {
        return this.isOfflineMenu;
    }

    public void setIsOfflineMenu(String isofflinemenu) {
        this.isOfflineMenu = isofflinemenu;
    }

    public String getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(String isenabled) {
        this.isEnabled = isenabled;
    }

    public String getContextTitle() {
        return this.contextTitle;
    }

    public void setContextTitle(String contextname) {
        this.contextTitle = contextname;
    }

    public String getContextMenuId() {
        return contextMenuId;
    }

    public void setContextMenuId(String contextMenuId) {
        this.contextMenuId = contextMenuId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getLandingPageType() {
        return landingPageType;
    }

    public void setLandingPageType(String landingPageType) {
        this.landingPageType = landingPageType;
    }

    public String getCategoryStyle() {
        return categoryStyle;
    }

    public void setCategoryStyle(String categoryStyle) {
        this.categoryStyle = categoryStyle;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public String getParameterStrings() {
        return parameterStrings;
    }

    public void setParameterStrings(String parameterStrings) {
        this.parameterStrings = parameterStrings;
    }

    public int getIsSubMenuExists() {
        return isSubMenuExists;
    }

    public void setIsSubMenuExists(int isSubMenuExists) {
        this.isSubMenuExists = isSubMenuExists;
    }

}
