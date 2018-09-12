package com.instancy.instancylearning.utils;


public class StaticValues {

    /**
     * Application Database name to create in SQLite.
     */
    public static final String DATABASE_NAME = "InstancyLMS.db";

    /**
     * Version of Database.
     */
    public static final int DATABASE_VERSION = 1;
    public static final int DEFAULTSITEID = -1;
    public static final int DEFAULTUSERID = -1;

    public static String SITE_URL = "";
    public static final String WEBAPI_URL = "";

    public static final String AUTHENTICATION_KEY = "";

    public static final String CURRENT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_FORMAT = "dd-MM-yyyy h:mm:ss a";
    public static final String CURRENT_DATE_FORMAT = "dd MMM, yyyy";
    public static final String USER_DISPLAY_NAME = "";


    public static final String INSTANCYPREFS_NAME = "instancyprefs";


    //Main Site
    public static final String KEY_USERLOGINID = "KEY_USERLOGINID";
    public static final String KEY_USERPASSWORD = "KEY_USERPASSWORD";
    public static final String KEY_USERID = "KEY_USERID";
    public static final String KEY_WEBAPIURL = "KEY_WEBAPIURL";
    public static final String KEY_SITEURL = "KEY_SITEURL";
    public static final String KEY_AUTHENTICATION = "KEY_AUTHENTICATION";
    public static final String KEY_USERNAME = "KEY_USERNAME";
    public static final String KEY_SITEID = "KEY_SITEID";

    public static final String KEY_USERSTATUS = "KEY_USERSTATUS";

    public static final String KEY_USERPROFILEIMAGE = "KEY_USERIMAGE";

    // SubSite

    public static final String SUB_SITE_ENTERED = "SUB_SITE_ENTERED";

    public static final String SUB_KEY_USERLOGINID = "SUB_KEY_USERLOGINID";
    public static final String SUB_KEY_USERPASSWORD = "SUB_KEY_USERPASSWORD";
    public static final String SUB_KEY_USERID = "SUB_KEY_USERID";
    public static final String SUB_KEY_WEBAPIURL = "SUB_KEY_WEBAPIURL";
    public static final String SUB_KEY_SITEURL = "SUB_KEY_SITEURL";
    public static final String SUB_KEY_AUTHENTICATION = "SUB_KEY_AUTHENTICATION";
    public static final String SUB_KEY_USERNAME = "SUB_KEY_USERNAME";
    public static final String SUB_KEY_SITEID = "SUB_KEY_SITEID";
    public static final String SUB_SITE_NAME = "SUB_SITE_NAME";

    public static final String SUB_KEY_USERSTATUS = "SUB_KEY_USERSTATUS";

    public static final String SUB_KEY_USERPROFILEIMAGE = "SUB_KEY_USERPROFILEIMAGE";

    public static final String KEY_SITENAME = "KEY_SITENAME";

    public static final String KEY_PLATFORMURL = "KEY_PLATFORMURL";

    public static final String KEY_ISLOGIN = "";

    public static final String KEY_SOCIALLOGIN = "SOCIAL_LOGINS_URL";

    public static final String KEY_ACTIONBARTITLE = "KEY_ACTIONBARTITLE";

    public static final String BUNDLE_USERNAME = "BUNDLE_USERNAME";

    public static final String BUNDLE_PASSWORD = "BUNDLE_PASSWORD";

    public static final String KEY_HIDE_ANNOTATION = "KEY_HIDE_ANNOTATION";

    public static final String CONTEXT_TITLE = "COONTEXT_TITLE";

    public static final String CHAT_LIST = "CHAT_LIST";

    public static final String FCM_KEY = "FCMKEY";

    public static final String FCM_OBJECT = "FCM_OBJECT";

    public static final String BACK_STACK_ROOT_TAG = "root_fragment";
    /**
     * To get the FontAwsome font .ttf asset path
     *
     * @author Venu
     */
    public static final String TTF_MYRIADPRO = "fonts/MyriadPro-Regular.ttf";

    /**
     * To get the MyriadPro font .ttf asset path
     *
     * @author Venu
     */
    public static final String TTF_FONTAWESOME = "fonts/fontawesome-webfont.ttf";

    /**
     * Used in {@code onActivityResult} when course window closed to retrieve
     * data from the course window to update the content status(Online only).
     * Start course launch activity for result with this code.
     *
     * @author Venu
     */


    public static final int DETAIL_CATALOG_CODE = 1212;

    public static final int COURSE_CLOSE_CODE = 9595;


    public static final int DETAIL_CLOSE_CODE = 9191;

    public static final int FILTER_CLOSE_CODE = 1234;

    public static final int FORUM_CREATE_NEW_FORUM = 2111;

    public static final int INNER_FILTER_CLOSE = 2121;

    public static final int COURSE_CLOSE_FROM_WEBSCREEN_CODE = 8888;

    public static final int REVIEW_REFRESH = 4238;

    public static final int EDUCATION_ACT = 1010;

    public static final int EXPERIENCE_ACT = 10101;

    /**
     * Start course launch activity of media content for result with this code.
     *
     * @author Venu
     */
    public static final int MEDIA_CONTENT_LAUNCH_CODE = 5555;

    /**
     * Start course launch activity of document content for result with this
     * code.
     *
     * @author Venu
     */
    public static final int DOC_CONTENT_LAUNCH_CODE = 6666;

    /**
     * Start settings activity for result with this code.
     *
     * @author Venu
     */
    public static final int SETTINGS_CODE = 8585;

    /**
     * Start forgot password activity for result with this code.
     *
     * @author Venu
     */
    public static final int FORGOT_CODE = 7575;

    /**
     * Start file chooser(gallery) activity for result with this code(For essay
     * question attachment).
     *
     * @author Venu
     */
    public static final int FILE_CHOOSER_CODE = 6384;

    /**
     * Start video recording activity for result with this code.
     *
     * @author Venu
     */
    public static final int WIDGET_CODE = 8080;

    /**
     * Start file chooser(gallery)/capture activity for result with this
     * code(For edit profile image).
     *
     * @author Venu
     */
    public static final int FILE_CHOOSER_FOR_PROFILE = 9090;


    /**
     * To start In App Purchase launch flow for result.
     *
     * @author Venu
     */
    public static final int IAP_LAUNCH_FLOW_CODE = 9797;

    /**
     * To start In App Purchase launch flow for result.
     *
     * @author Venu
     */

    public static final int IAP_CODE = 9191;
    /**
     * Use to set activity result as success.
     *
     * @author Venu
     */
    public static final int ACTIVITY_RESULT_SUCCESS = 1111;

    /**
     * Use to set activity result as failed.
     *
     * @author Venu
     */
    public static final int ACTIVITY_RESULT_FAILED = 1010;

    //static final int TOPICS_CONTENT_LAUNCH_CODE = 1212;

    /**
     * Default maximum download file size(in kB).
     *
     * @author Venu
     */
    public static final long AUTO_DOWNLOAD_FILE_SIZE = 512000L;

    /**
     * Start ask question(new) activity for result with this code.
     *
     * @author Venu
     */
    public static final int ASK_QUESTION_POST_CODE = 1451;

    /**
     * Start ask question responses activity for result with this code.
     *
     * @author Venu
     */
    static final int ASK_QUESTION_VIEW_RESPONSES_CODE = 1452;

    /**
     * Start add content to catalog activity for result with this code.
     *
     * @author Venu
     */
    static final int ADD_CONTENT_CODE = 1453;

    /**
     * Start Create new forum activity for result with this code.
     *
     * @author Venu
     */
    static final int NEW_FORUM_CODE = 1454;

    /**
     * Start Create new topic activity for result with this code.
     *
     * @author Venu
     */
    public static final int NEW_TOPIC_CODE = 1455;

    /**
     * Start file choose intent for adding content to catalog for result with this code.
     *
     * @author Venu
     */
    public static final int SELECT_FILE_CODE = 1001;

    /**
     * To set the default selected menu & default title.
     *
     * @author Venu
     */
    public static boolean IS_MENUS_FIRST_TIME = true;

    /**
     * To store the selected main menu position.
     *
     * @author Venu
     */
    public static int MAIN_MENU_POSITION = -1;

    /**
     * To store the selected sub menu position in a main menu.
     *
     * @author Venu
     */
    public static int SUB_MENU_POSITION = -1;

    public static int REFRESH_PEOPLE = 0;

    public static int MYLEARNING_FRAGMENT_OPENED_FIRSTTIME = 0;

    public static int CATALOG_FRAGMENT_OPENED_FIRSTTIME = 0;


    public static int EVENT_FRAGMENT_OPENED_FIRSTTIME = 0;

    public static int SIDEMENUOPENED_FIRSTTIME = 0;

    public static int NOTIFICATIONVIWED = 0;

    public static int PROFILE_FRAGMENT_OPENED_FIRSTTIME = 0;

    public static int BACKTOMAINSITE = 0;

    public static final String ACTION_INTENT_TEXT_MESSAGE_INCOMING = "ACTION_INTENT_TEXT_MESSAGE_INCOMING";

    public static int ISPROFILENAMEORIMAGEUPDATED = 0;

    public static int CALENDARCALLED = 0;

    public static int PASTCALLED = 0;

    public static int UPCOMINGCALLED = 0;

    public static int REPORTPREVILAGEID = 1072;


    public static int ASKEXPERTPREVILAGEID = 1273;


    /// all app configurations

}
