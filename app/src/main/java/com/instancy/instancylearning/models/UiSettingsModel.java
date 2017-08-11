package com.instancy.instancylearning.models;

/**
 * Created by Upendranath on 5/24/2017.
 */

public class UiSettingsModel {


    private String menuTextColor = "";
    private String defaultTextColor = "";

    public String getAppTextColor() {
        return appTextColor.isEmpty() ? "" : appTextColor;
    }

    public void setAppTextColor(String appTextColor) {
        this.appTextColor = appTextColor;
    }

    public String getAppBGColor() {
        return appBGColor.isEmpty() ? "" : appBGColor;
    }

    public void setAppBGColor(String appBGColor) {
        this.appBGColor = appBGColor;
    }

    public String getIsFaceBook() {
        return isFaceBook.isEmpty() ? "false" : isFaceBook;
    }

    public void setIsFaceBook(String isFaceBook) {
        this.isFaceBook = isFaceBook;
    }

    public String getIsGoogle() {
        return isGoogle.isEmpty() ? "false" : isGoogle;
    }

    public void setIsGoogle(String isGoogle) {
        this.isGoogle = isGoogle;
    }

    public String getIsTwitter() {
        return isTwitter.isEmpty() ? "false" : isTwitter;
    }

    public void setIsTwitter(String isTwitter) {
        this.isTwitter = isTwitter;
    }

    public String getIsLinkedIn() {
        return isLinkedIn.isEmpty() ? "false" : isLinkedIn;
    }

    public void setIsLinkedIn(String isLinkedIn) {
        this.isLinkedIn = isLinkedIn;
    }

    private String appTextColor = "";
    private String appBGColor = "";

    private String isFaceBook = "";
    private String isGoogle = "";

    private String isTwitter = "";
    private String isLinkedIn = "";

    private String menuBGColor = "";
    private String selectedMenuTextColor = "";

    private String selectedMenuBGColor = "";
    private String headerTextColor = "";

    private String headerBGColor = "";
    private String listBGColor = "";

    private String listBorderColor = "";
    private String menuHeaderBGColor = "";

    private String menuHeaderTextColor = "";
    private String menuBGAlternativeColor = "";

    private String menuBGSelectTextColor = "";
    private String viewButtonColor = "";

    private String viewButtonTextColor = "";
    private String detailButtonColor = "";

    private String detailButtonTextColor = "";
    private String reportButtonColor = "";

    private String reportButtonTextColor = "";
    private String setCompleteButtonColor = "";

    private String setCompleteTextColor = "";
    private String fileUploadButtonColor = "";

    private String appHeaderColor = "";
    private String selfRegistrationAllowed = "";

    private String contentDownloadType = "";
    private String courseAppContent = "";

    private String enableNativeCatlog = "";
    private String enablePushNotification = "";
    private String nativeAppType = "";

    private String autodownloadsizelimit = "";
    private String catalogContentDownloadType = "";

    private String firstTarget = "";
    private String secondTarget = "";

    private String thirdTarget = "";
    private String contentAssignment = "";

    private String newContentAvailable = "";
    private String contentUnassigned = "";
    private String firstEvent = "";

    public String getMenuTextColor() {
        return menuTextColor.isEmpty() ? "#000000" : menuTextColor;
    }

    public void setMenuTextColor(String menuTextColor) {
        this.menuTextColor = menuTextColor;
    }

    public String getDefaultTextColor() {

        return defaultTextColor.isEmpty() ? "#000000" : defaultTextColor;
    }

    public void setDefaultTextColor(String defaultTextColor) {

        this.defaultTextColor = defaultTextColor;
        ;
    }

    public String getMenuBGColor() {
        return menuBGColor.isEmpty() ? "#ffffff" : menuBGColor;
    }

    public void setMenuBGColor(String menuBGColor) {
        this.menuBGColor = menuBGColor;
    }

    public String getSelectedMenuTextColor() {
        return selectedMenuTextColor;
    }

    public void setSelectedMenuTextColor(String selectedMenuTextColor) {
        this.selectedMenuTextColor = selectedMenuTextColor;
    }

    public String getSelectedMenuBGColor() {
        return selectedMenuBGColor.isEmpty() ? "#8dc73f" : selectedMenuBGColor;
    }

    public void setSelectedMenuBGColor(String selectedMenuBGColor) {
        this.selectedMenuBGColor = selectedMenuBGColor;
    }

    public String getHeaderTextColor() {
        return headerTextColor.isEmpty() ? "#ffffff" : headerTextColor;
    }

    public void setHeaderTextColor(String headerTextColor) {
        this.headerTextColor = headerTextColor;
    }

    public String getHeaderBGColor() {
        return headerBGColor.isEmpty() ? "#8dc73f" : headerBGColor;
    }

    public void setHeaderBGColor(String headerBGColor) {
        this.headerBGColor = headerBGColor;
    }

    public String getListBGColor() {
        return listBGColor;
    }

    public void setListBGColor(String listBGColor) {
        this.listBGColor = listBGColor;
    }

    public String getListBorderColor() {
        return listBorderColor;
    }

    public void setListBorderColor(String listBorderColor) {
        this.listBorderColor = listBorderColor;
    }

    public String getMenuHeaderBGColor() {
        return menuHeaderBGColor.isEmpty() ? "#8dc73f" : menuHeaderBGColor;
    }

    public void setMenuHeaderBGColor(String menuHeaderBGColor) {
        this.menuHeaderBGColor = menuHeaderBGColor;
    }

    public String getMenuHeaderTextColor() {
        return menuHeaderTextColor;
    }

    public void setMenuHeaderTextColor(String menuHeaderTextColor) {
        this.menuHeaderTextColor = menuHeaderTextColor;
    }

    public String getMenuBGAlternativeColor() {
        return menuBGAlternativeColor.isEmpty() ? "#8dc73f" : menuBGAlternativeColor;
    }

    public void setMenuBGAlternativeColor(String menuBGAlternativeColor) {
        this.menuBGAlternativeColor = menuBGAlternativeColor;
    }

    public String getMenuBGSelectTextColor() {
        return menuBGSelectTextColor;
    }

    public void setMenuBGSelectTextColor(String menuBGSelectTextColor) {
        this.menuBGSelectTextColor = menuBGSelectTextColor;
    }

    public String getViewButtonColor() {
        return viewButtonColor;
    }

    public void setViewButtonColor(String viewButtonColor) {
        this.viewButtonColor = viewButtonColor;
    }

    public String getViewButtonTextColor() {
        return viewButtonTextColor;
    }

    public void setViewButtonTextColor(String viewButtonTextColor) {
        this.viewButtonTextColor = viewButtonTextColor;
    }

    public String getDetailButtonColor() {
        return detailButtonColor;
    }

    public void setDetailButtonColor(String detailButtonColor) {
        this.detailButtonColor = detailButtonColor;
    }

    public String getDetailButtonTextColor() {
        return detailButtonTextColor;
    }

    public void setDetailButtonTextColor(String detailButtonTextColor) {
        this.detailButtonTextColor = detailButtonTextColor;
    }

    public String getReportButtonColor() {
        return reportButtonColor;
    }

    public void setReportButtonColor(String reportButtonColor) {
        this.reportButtonColor = reportButtonColor;
    }

    public String getReportButtonTextColor() {
        return reportButtonTextColor;
    }

    public void setReportButtonTextColor(String reportButtonTextColor) {
        this.reportButtonTextColor = reportButtonTextColor;
    }

    public String getSetCompleteButtonColor() {
        return setCompleteButtonColor;
    }

    public void setSetCompleteButtonColor(String setCompleteButtonColor) {
        this.setCompleteButtonColor = setCompleteButtonColor;
    }

    public String getSetCompleteTextColor() {
        return setCompleteTextColor;
    }

    public void setSetCompleteTextColor(String setCompleteTextColor) {
        this.setCompleteTextColor = setCompleteTextColor;
    }

    public String getFileUploadButtonColor() {
        return fileUploadButtonColor;
    }

    public void setFileUploadButtonColor(String fileUploadButtonColor) {
        this.fileUploadButtonColor = fileUploadButtonColor;
    }

    public String getAppHeaderColor() {
        return appHeaderColor.isEmpty() ? "#8dc73f" : appHeaderColor;
    }

    public void setAppHeaderColor(String appHeaderColor) {
        this.appHeaderColor = appHeaderColor;
    }

    public String getSelfRegistrationAllowed() {
        return selfRegistrationAllowed;
    }

    public void setSelfRegistrationAllowed(String selfRegistrationAllowed) {
        this.selfRegistrationAllowed = selfRegistrationAllowed;
    }

    public String getContentDownloadType() {
        return contentDownloadType;
    }

    public void setContentDownloadType(String contentDownloadType) {
        this.contentDownloadType = contentDownloadType;
    }

    public String getCourseAppContent() {
        return courseAppContent;
    }

    public void setCourseAppContent(String courseAppContent) {
        this.courseAppContent = courseAppContent;
    }

    public String getEnableNativeCatlog() {
        return enableNativeCatlog;
    }

    public void setEnableNativeCatlog(String enableNativeCatlog) {
        this.enableNativeCatlog = enableNativeCatlog;
    }

    public String getEnablePushNotification() {
        return enablePushNotification;
    }

    public void setEnablePushNotification(String enablePushNotification) {
        this.enablePushNotification = enablePushNotification;
    }

    public String getNativeAppType() {
        return nativeAppType;
    }

    public void setNativeAppType(String nativeAppType) {
        this.nativeAppType = nativeAppType;
    }

    public String getAutodownloadsizelimit() {
        return autodownloadsizelimit;
    }

    public void setAutodownloadsizelimit(String autodownloadsizelimit) {
        this.autodownloadsizelimit = autodownloadsizelimit;
    }

    public String getCatalogContentDownloadType() {
        return catalogContentDownloadType;
    }

    public void setCatalogContentDownloadType(String catalogContentDownloadType) {
        this.catalogContentDownloadType = catalogContentDownloadType;
    }

    public String getFirstTarget() {
        return firstTarget;
    }

    public void setFirstTarget(String firstTarget) {
        this.firstTarget = firstTarget;
    }

    public String getSecondTarget() {
        return secondTarget;
    }

    public void setSecondTarget(String secondTarget) {
        this.secondTarget = secondTarget;
    }

    public String getThirdTarget() {
        return thirdTarget;
    }

    public void setThirdTarget(String thirdTarget) {
        this.thirdTarget = thirdTarget;
    }

    public String getContentAssignment() {
        return contentAssignment;
    }

    public void setContentAssignment(String contentAssignment) {
        this.contentAssignment = contentAssignment;
    }

    public String getNewContentAvailable() {
        return newContentAvailable;
    }

    public void setNewContentAvailable(String newContentAvailable) {
        this.newContentAvailable = newContentAvailable;
    }

    public String getContentUnassigned() {
        return contentUnassigned;
    }

    public void setContentUnassigned(String contentUnassigned) {
        this.contentUnassigned = contentUnassigned;
    }

    public String getFirstEvent() {
        return firstEvent;
    }

    public void setFirstEvent(String firstEvent) {
        this.firstEvent = firstEvent;
    }

    private static UiSettingsModel instance;

    public static synchronized UiSettingsModel getInstance() {
        if (instance == null) {
            instance = new UiSettingsModel();
        }
        return instance;
    }
}
