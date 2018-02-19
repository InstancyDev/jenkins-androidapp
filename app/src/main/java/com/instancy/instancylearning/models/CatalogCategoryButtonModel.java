package com.instancy.instancylearning.models;


public class CatalogCategoryButtonModel {
    int parentid = -1;
    String categoryname = "";
    int categoryid = -1;
    String categoryicon = "";
    String contentcount = "";
    String column1 = "";
    String siteid = "";
    String componentId = "";

    public int getParentId() {
        return parentid;
    }

    public void setParentId(int parentId) {
        this.parentid = parentId;
    }

    public String getCategoryName() {
        return categoryname;
    }

    public void setCategoryName(String categoryName) {
        this.categoryname = categoryName;
    }

    public int getCategoryId() {
        return categoryid;
    }

    public void setCategoryId(int categoryId) {
        this.categoryid = categoryId;
    }

    public String getCategoryIcon() {
        return categoryicon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryicon = categoryIcon;
    }

    public String getContentCount() {
        return contentcount;
    }

    public void setContentCount(String contentCount) {
        this.contentcount = contentCount;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getSiteId() {
        return siteid;
    }

    public void setSiteId(String siteId) {
        this.siteid = siteId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        componentId = componentId;
    }


}
