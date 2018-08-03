package com.instancy.instancylearning.models;

/**
 * Created by Upendranath on 5/24/2017.
 */

public class NativeMenuModel {

    private String menuid="";

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsofflineMenu() {
        return isofflineMenu;
    }

    public void setIsofflineMenu(String isofflineMenu) {
        this.isofflineMenu = isofflineMenu;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getContextTitle() {
        return contextTitle;
    }

    public void setContextTitle(String contextTitle) {
        this.contextTitle = contextTitle;
    }

    public String getContextmenuId() {
        return contextmenuId;
    }

    public void setContextmenuId(String contextmenuId) {
        this.contextmenuId = contextmenuId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getLandingpageType() {
        return landingpageType;
    }

    public void setLandingpageType(String landingpageType) {
        this.landingpageType = landingpageType;
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

    public String getParameterString() {
        return parameterString;
    }

    public void setParameterString(String parameterString) {
        this.parameterString = parameterString;
    }

    public int getWebMenuId() {
        return webMenuId;
    }

    public void setWebMenuId(int webMenuId) {
        this.webMenuId = webMenuId;
    }

    public int webMenuId = 0;

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    private String displayname="";
    private Integer displayOrder=0;
    private String image="";
    private String isofflineMenu;
    private String isEnabled="";
    private String contextTitle="";
    private String contextmenuId="";
    private String repositoryId="";
    private String landingpageType="";
    private String categoryStyle="";
    private String componentId="";
    private String conditions="";
    private String parentMenuId;
    private String parameterString="";
    private String siteUrl="";
    private String siteId="";

}
