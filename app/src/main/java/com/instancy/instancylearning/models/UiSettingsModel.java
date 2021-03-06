package com.instancy.instancylearning.models;

/**
 * Created by Upendranath on 5/24/2017.
 */

public class UiSettingsModel {

    // for acclife
    private String brandingColor = "#5db100";

    private String appBackGroundColor = "#ffffff";

    private String defaultTextColor = "#000000";

// for trackton_accilife for all buttons
//    private String brandingColor = "#0062a6";


    // for instancy for all buttons
//    private String brandingColor = "#1c4585";
//    private String appBackGroundColor = "#ffffff";
//    private String defaultTextColor = "#000000";
    // for instancy for all buttons
//    private String appBackGroundColor = "#8dc73f";


// // for instancy for all buttons

//    private String appBackGroundColor = "#8dc73f";
//
//    private String brandingColor = "#8dc73f";
//
//    private String defaultTextColor = "#ffffff";


// for cle app

//    private String appBackGroundColor = "#4A2D1F";
//
//    private String brandingColor = "#4A2D1F";
//
//    private String defaultTextColor = "#ffffff";


    public boolean isEnableSkillstobeMappedwithJobRoles() {
        return enableSkillstobeMappedwithJobRoles;
    }

    public void setEnableSkillstobeMappedwithJobRoles(boolean enableSkillstobeMappedwithJobRoles) {
        this.enableSkillstobeMappedwithJobRoles = enableSkillstobeMappedwithJobRoles;
    }

    public boolean enableSkillstobeMappedwithJobRoles;

    public boolean isEnableMemberShipConfig() {
        return enableMemberShipConfig;
    }

    public void setEnableMemberShipConfig(boolean enableMemberShipConfig) {
        this.enableMemberShipConfig = enableMemberShipConfig;
    }

    public boolean isEnableIndidvidualPurchaseConfig() {
        return enableIndidvidualPurchaseConfig;
    }

    public void setEnableIndidvidualPurchaseConfig(boolean enableIndidvidualPurchaseConfig) {
        this.enableIndidvidualPurchaseConfig = enableIndidvidualPurchaseConfig;
    }

    private boolean enableMemberShipConfig = false;

    private boolean enableIndidvidualPurchaseConfig = false;


    public String getAutoLaunchMyLearningFirst() {
        return autoLaunchMyLearningFirst;
    }

    public void setAutoLaunchMyLearningFirst(String autoLaunchMyLearningFirst) {
        this.autoLaunchMyLearningFirst = autoLaunchMyLearningFirst;
    }

    private String autoLaunchMyLearningFirst = "false";


    private String menuTextColor = "";

    public String getSignUpName() {
        return signUpName.isEmpty() ? "Sign up" : signUpName;
    }

    public void setSignUpName(String signUpName) {
        this.signUpName = signUpName;
    }

    private String signUpName = "";

    public String getEnableAppLogin() {
        return enableAppLogin;
    }

    public void setEnableAppLogin(String enableAppLogin) {
        this.enableAppLogin = enableAppLogin;
    }

    private String enableAppLogin = "";

    private String nativeAppLoginLogo = "";

    public String getNativeAppLoginLogo() {
        return nativeAppLoginLogo;
    }

    public void setNativeAppLoginLogo(String nativeAppLoginLogo) {
        this.nativeAppLoginLogo = nativeAppLoginLogo;
    }

    public String getEnableBranding() {
        return enableBranding;
    }

    public void setEnableBranding(String enableBranding) {
        this.enableBranding = enableBranding;
    }

    private String enableBranding = "";

    public String getAppLoginBGColor() {
        return appLoginBGColor.isEmpty() ? appBackGroundColor : appBackGroundColor;
    }

    public void setAppLoginBGColor(String appLoginBGColor) {
        this.appLoginBGColor = appLoginBGColor;
    }

    private String appLoginBGColor = "";

    public String getAppLoginTextolor() {

        return appLoginTextolor.isEmpty() ? defaultTextColor : appLoginTextolor;
    }

    public void setAppLoginTextolor(String appLoginTextolor) {
        this.appLoginTextolor = appLoginTextolor;
    }

    private String appLoginTextolor = "";

    public String getAppTextColor() {
        return appTextColor.isEmpty() ? "#000000" : appTextColor;
    }

    public void setAppTextColor(String appTextColor) {
        this.appTextColor = appTextColor;
    }

    public String getAppBGColor() {
        return appBGColor.isEmpty() ? "#ffffff" : appBGColor;
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


    public void setAppButtonBgColor(String appButtonBgColor) {
        this.appButtonBgColor = appButtonBgColor;
    }

    public String getAppButtonTextColor() {

        return appButtonTextColor.isEmpty() ? defaultTextColor : appButtonTextColor;

    }

    public void setAppButtonTextColor(String appButtonTextColor) {
        this.appButtonTextColor = appButtonTextColor;
    }

    private String appButtonBgColor = "";
    private String appButtonTextColor = "";

    private String appTextColor = "";
    private String appBGColor = "";

    private String isFaceBook = "";
    private String isGoogle = "";

    private String isTwitter = "";
    private String isLinkedIn = "";

    private String menuBGColor = "";
    private String selectedMenuTextColor = "";

    public String getAppLogoBackgroundColor() {
        return appLogoBackgroundColor;
    }

    public void setAppLogoBackgroundColor(String appLogoBackgroundColor) {
        this.appLogoBackgroundColor = appLogoBackgroundColor;
    }

    private String appLogoBackgroundColor = "";

    private String selectedMenuBGColor = "";

    public void setAppHeaderTextColor(String appHeaderTextColor) {
        this.appHeaderTextColor = appHeaderTextColor;

    }

    private String appHeaderTextColor = defaultTextColor;

    private String headerBGColor = "";
    private String listBGColor = "";

    private String listBorderColor = "";
    private String menuHeaderBGColor = "";

    private String menuHeaderTextColor = "";
    private String menuBGAlternativeColor = "";

    private String menuBGSelectTextColor = "";

    private String fileUploadButtonColor = "";

    private String appHeaderColor = "";
    private String selfRegistrationAllowed = "";

    private String myLearningContentDownloadType = "";
    private String catalogContentDownloadType = "";

    public boolean isGlobasearch() {
        return isGlobasearch;
    }

    public void setGlobasearch(boolean globasearch) {
        isGlobasearch = globasearch;
    }

    private boolean isGlobasearch = false;

    private String courseAppContent = "";

    private String enableNativeCatlog = "";
    private String enablePushNotification = "";
    private String nativeAppType = "";

    private String autodownloadsizelimit = "";
    private String firstTarget = "";
    private String secondTarget = "";

    private String thirdTarget = "";
    private String contentAssignment = "";

    private String newContentAvailable = "";
    private String contentUnassigned = "";
    private String firstEvent = "";
    private boolean EnableMultipleInstancesforEvent = false;

    public boolean isEnableEcommerce() {
        return EnableEcommerce;
    }

    public void setEnableEcommerce(boolean enableEcommerce) {
        EnableEcommerce = enableEcommerce;
    }

    private boolean EnableEcommerce = false;


    private int NumberOfRatingsRequiredToShowRating = 0; //638

    public int getNumberOfRatingsRequiredToShowRating() {
        return NumberOfRatingsRequiredToShowRating;
    }

    public void setNumberOfRatingsRequiredToShowRating(int numberOfRatingsRequiredToShowRating) {
        NumberOfRatingsRequiredToShowRating = numberOfRatingsRequiredToShowRating;
    }

    public String getMinimimRatingRequiredToShowRating() {
        return MinimimRatingRequiredToShowRating;
    }

    public void setMinimimRatingRequiredToShowRating(String minimimRatingRequiredToShowRating) {
        MinimimRatingRequiredToShowRating = minimimRatingRequiredToShowRating;
    }

    public int getNoOfDaysForCourseTargetDate() {
        return NoOfDaysForCourseTargetDate;
    }

    public void setNoOfDaysForCourseTargetDate(int noOfDaysForCourseTargetDate) {
        NoOfDaysForCourseTargetDate = noOfDaysForCourseTargetDate;
    }

    private String MinimimRatingRequiredToShowRating = "0";
    private int NoOfDaysForCourseTargetDate = 0; //640

    public String getDiscussionForumFileTypes() {
        return discussionForumFileTypes;
    }

    public void setDiscussionForumFileTypes(String discussionForumFileTypes) {
        this.discussionForumFileTypes = discussionForumFileTypes;
    }

    public String getUserUploadFileSize() {
        return userUploadFileSize;
    }

    public void setUserUploadFileSize(String userUploadFileSize) {
        this.userUploadFileSize = userUploadFileSize;
    }

    private String discussionForumFileTypes = "jpg,jpeg,png";
    private String userUploadFileSize = "";


    public int getShowEventAvailableFewSeatsLeft() {
        return showEventAvailableFewSeatsLeft;
    }

    public void setShowEventAvailableFewSeatsLeft(int showEventAvailableFewSeatsLeft) {
        this.showEventAvailableFewSeatsLeft = showEventAvailableFewSeatsLeft;
    }

    private int showEventAvailableFewSeatsLeft = 0;


    public boolean isPricebyStrikeThrough() {
        return pricebyStrikeThrough;
    }

    public void setPricebyStrikeThrough(boolean pricebyStrikeThrough) {
        this.pricebyStrikeThrough = pricebyStrikeThrough;
    }

    public boolean pricebyStrikeThrough = false;

    public boolean isEnableMultipleInstancesforEvent() {
        return EnableMultipleInstancesforEvent;
    }

    public void setEnableMultipleInstancesforEvent(boolean enableMultipleInstancesforEvent) {
        EnableMultipleInstancesforEvent = enableMultipleInstancesforEvent;
    }

    public String getAddProfileAdditionalTab() {
        return AddProfileAdditionalTab;
    }

    public void setAddProfileAdditionalTab(String addProfileAdditionalTab) {
        AddProfileAdditionalTab = addProfileAdditionalTab;
    }

    private String AddProfileAdditionalTab = "";

    public String getCommonPasswordValue() {
        return commonPasswordValue;
    }

    public void setCommonPasswordValue(String commonPasswordValue) {
        this.commonPasswordValue = commonPasswordValue;
    }

    private String commonPasswordValue = "";

//    public boolean isEnableUserEvaluation() {
//        return enableUserEvaluation;
//    }
//
//    public void setEnableUserEvaluation(boolean enableUserEvaluation) {
//        this.enableUserEvaluation = enableUserEvaluation;
//    }

//    private boolean enableUserEvaluation = false;

    private boolean enableContentEvaluation = false;

    private boolean enableAzureSSOForLearner = false;

    private boolean allowExpiredEventsSubscription = false;

    public String getMenuTextColor() {
        return menuTextColor.isEmpty() ? "#000000" : menuTextColor;
    }

    public boolean isEnableContentEvaluation() {
        return enableContentEvaluation;
    }

    public void setEnableContentEvaluation(boolean enableContentEvaluation) {
        this.enableContentEvaluation = enableContentEvaluation;
    }

    public String getcCEventStartdate() {
        return cCEventStartdate;
    }

    public void setcCEventStartdate(String cCEventStartdate) {
        this.cCEventStartdate = cCEventStartdate;
    }

    private String cCEventStartdate = "";

    public boolean isEnableAzureSSOForLearner() {
        return enableAzureSSOForLearner;
    }

    public void setEnableAzureSSOForLearner(boolean enableAzureSSOForLearner) {
        this.enableAzureSSOForLearner = enableAzureSSOForLearner;
    }

    public boolean isAllowExpiredEventsSubscription() {
        return allowExpiredEventsSubscription;
    }

    public void setAllowExpiredEventsSubscription(boolean allowExpiredEventsSubscription) {
        this.allowExpiredEventsSubscription = allowExpiredEventsSubscription;
    }

    public void setMenuTextColor(String menuTextColor) {
        this.menuTextColor = menuTextColor;
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


    public void setSelectedMenuBGColor(String selectedMenuBGColor) {
        this.selectedMenuBGColor = selectedMenuBGColor;
    }

    public String getHeaderTextColor() {
        return appHeaderTextColor.isEmpty() ? defaultTextColor : appHeaderTextColor;
//        return "#ffffff";

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


    public String getMenuHeaderTextColor() {
        return menuHeaderTextColor.isEmpty() ? "#FFFFFF" : menuHeaderTextColor;
//        return "#FFFFFF";
    }

    public void setMenuHeaderTextColor(String menuHeaderTextColor) {
        this.menuHeaderTextColor = menuHeaderTextColor;
    }


    public void setMenuBGAlternativeColor(String menuBGAlternativeColor) {
        this.menuBGAlternativeColor = menuBGAlternativeColor;
    }

    public String getMenuBGSelectTextColor() {

        return menuBGSelectTextColor.isEmpty() ? "#FFFFFF" : menuBGSelectTextColor;
    }

    public void setMenuBGSelectTextColor(String menuBGSelectTextColor) {
        this.menuBGSelectTextColor = menuBGSelectTextColor;
    }

    public String getFileUploadButtonColor() {
        return fileUploadButtonColor;
    }

    public void setFileUploadButtonColor(String fileUploadButtonColor) {
        this.fileUploadButtonColor = fileUploadButtonColor;
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

    public String getMyLearningContentDownloadType() {
        return myLearningContentDownloadType;
    }

    public void setMyLearningContentDownloadType(String myLearningContentDownloadType) {
        this.myLearningContentDownloadType = myLearningContentDownloadType;
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


    public String getAppButtonBgColor() {
        return appButtonBgColor.isEmpty() ? brandingColor : appButtonBgColor;
    }

    public String getAppHeaderTextColor() {

        return appHeaderTextColor.isEmpty() ? brandingColor : appHeaderTextColor;

    }

    public String getSelectedMenuBGColor() {
        return selectedMenuBGColor.isEmpty() ? brandingColor : selectedMenuBGColor;
    }

    public String getHeaderBGColor() {
        return headerBGColor.isEmpty() ? brandingColor : headerBGColor;
    }


    public String getMenuHeaderBGColor() {
        return menuHeaderBGColor.isEmpty() ? brandingColor : menuHeaderBGColor;
    }

    public void setMenuHeaderBGColor(String menuHeaderBGColor) {
        this.menuHeaderBGColor = menuHeaderBGColor;
    }


    public String getMenuBGAlternativeColor() {
        return menuBGAlternativeColor.isEmpty() ? "#ffffff" : menuBGAlternativeColor;
    }

    public String getAppHeaderColor() {
        return appHeaderColor.isEmpty() ? brandingColor : appHeaderColor;
    }

}
