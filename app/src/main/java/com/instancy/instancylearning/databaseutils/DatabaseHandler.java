package com.instancy.instancylearning.databaseutils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Spanned;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.LearnerSessionModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.NativeMenuModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.StudentResponseModel;
import com.instancy.instancylearning.models.TrackObjectsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.instancy.instancylearning.utils.StaticValues.DATABASE_NAME;
import static com.instancy.instancylearning.utils.StaticValues.DATABASE_VERSION;
import static com.instancy.instancylearning.utils.Utilities.convertStreamToString;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.generateHashMap;
import static com.instancy.instancylearning.utils.Utilities.getCurrentDateTime;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 5/16/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    /*
    *
    *  to get simple name of the class
    * */

    public static final String TAG = DatabaseHandler.class.getSimpleName();

    /**
     * TO store the my learning content metadata details
     */
    public static final String TBL_DOWNLOADDATA = "DOWNLOADDATA";
    /**
     * To store the catalog content metadata details
     */
    public static final String TBL_CATALOGDATA = "CATALOGDATA";
    /**
     * To store the user session details which tracked in offline course viewing
     */
    public static final String TBL_USERSESSION = "USERSESSION";
    /**
     * To store the student responses which tracked in offline course viewing
     */
    public static final String TBL_STUDENTRESPONSES = "STUDENTRESPONSES";

    /**
     * To store the question details of an assessment when downloaded
     */
    public static final String TBL_QUESTIONS = "QUESTIONS";
    /**
     * To store the offline tracking data
     */
    public static final String TBL_CMI = "CMI";
    /**
     * To store the track list content object details(track & content relation)
     */
    public static final String TBL_TRACKOBJECTS = "TRACKOBJECTS";
    /**
     * To store the track list content metadata details
     */
    public static final String TBL_TRACKLISTDATA = "TRACKLISTDATA";

    public static final String TBL_RELATEDCONTENTDATA = "RELATEDCONTENTDATA";

    public static final String TBL_EVENTCONTENTDATA = "EVENTCONTENTDATA";
    public static final String TBL_LRSDATA = "LRSDATA";
    public static final String TBL_SITETINCANCONFIG = "SITETINCANCONFIG";
    public static final String TBL_CATEGORY = "CATEGORY";

    public static final String TBL_JOBROLES = "JOBROLES";
    public static final String TBL_CONTENTTYPES = "CONTENTTYPES";
    public static final String TBL_CATEGORYCONTENT = "CATEGORYCONTENT";
    public static final String TBL_JOBROLECONTENT = "JOBROLECONTENT";

    public static final String TBL_NOTIFICATIONSETTINGS = "NOTIFICATIONSETTINGS";
    public static final String TBL_USERSETTINGS = "USERSETTINGS";
    public static final String TBL_AUTODOWNLOADSETTINGS = "AUTODOWNLOADSETTINGS";
    public static final String TBL_USERPAGENOTES = "UserPageNotes";

    /**
     * To store the drawer menu details
     */
    public static final String TBL_NATIVEMENUS = "NATIVEMENUS";
    public static final String TBL_NATIVESETTINGS = "NATIVESETTINGS";

    // ////////////////////////////USER
    // RELATED/////////////////////////////////////////
    /**
     * To store the User settings details
     */
    public static final String TBL_USERPREFERENCES = "USERPREFERENCES";
    /**
     * To store the the user action privileges/rights
     */
    public static final String TBL_USERPRIVILEGES = "USERPRIVILEGES";
    /**
     * This table is to store all the user profile field details
     */
    public static final String TBL_USERPROFILEFIELDS = "USERPROFILEFIELDS";
    /**
     * This table is to store the user profile groups/sections details
     */
    public static final String TBL_USERPROFILEGROUPS = "USERPROFILEGROUPS";
    /**
     * This table is to store the user profile groups/sections and its related
     * profile fields details
     */
    public static final String TBL_USERPROFILECONFIGS = "USERPROFILECONFIGS";
    /**
     * This is to store the options to show for user profile fields in editing
     * mode
     */
    public static final String TBL_USERPROFILEFIELDOPTIONS = "USERPROFILEFIELDOPTIONS";

    public static final String TBL_CALENDARADDEDEVENTS = "CALENDERADDEDEVENTS";

    // ////////////////////////////DISCUSSION
    // FORUMS/////////////////////////////////////////
    /**
     * To store the discussion forum details
     */
    public static final String TBL_FORUMS = "FORUMDETAILS";
    /**
     * To store the discussion forum topic details
     */
    public static final String TBL_FORUMTOPICS = "FORUMTOPICDETAILS";
    /**
     * To store the discussion forum comment details
     */
    public static final String TBL_TOPICCOMMENTS = "TOPICCOMMENTDETAILS";

    // //////////////////////////////ASKE THE
    // EXPERT/////////////////////////////////////////
    /**
     * To store Ask the expert Question details
     */
    public static final String TBL_ASKQUESTIONS = "ASKQUESTIONS";

    /**
     * To store Ask the expert Response details
     */
    public static final String TBL_ASKRESPONSES = "ASKRESPONSES";
    public static final String TBL_ASKQUESTIONCATEGORIES = "ASKQUESTIONCATEGORIES";
    public static final String TBL_ASKQUESTIONCATEGORYMAPPING = "ASKQUESTIONCATEGORYMAPPING";
    public static final String TBL_ASKQUESTIONSKILLS = "ASKQUESTIONSKILLS";

    // /////////////////////////////////////////////////////////////////////
    /**
     * To store the user details who is currently or previously logged in for
     * offline login verification
     */
    public static final String TBL_OFFLINEUSERS = "OFFLINEUSERS";
    /**
     * To store the details of the users who involved in forums/ask the expert
     */
    public static final String TBL_ALLUSERSINFO = "ALLUSERSINFO";

    public static final String TBL_CATEGORIES = "CATEGORIES";
    public static final String TBL_SUBCATEGORIES = "SUBCATEGORIES";
    public static final String TBL_CATEGORIESCONTENT = "CATEGORIESCONTENT";
    public static final String TBL_SKILLS = "SKILLS";
    public static final String TBL_SUBSKILLS = "SUBSKILLS";
    public static final String TBL_SKILLCONTENT = "SKILLCONTENT";
    public static final String TBL_JOBROLESNEW = "JOBROLESNEW";
    public static final String TBL_SUBJOBROLESNEW = "SUBJOBROLESNEW";
    public static final String TBL_JOBROLECONTENTNEW = "JOBROLECONTENTNEW";

    public static final String TBL_COMMUNITYLISTING = "COMMUNITYLISTING";
    public static final String TBL_CATEGORYCOMMUNITYLISTING = "COMMUNITYCATEGORYLISTING";

    public static final String TBL_APP_SETTINGS = "APPSETTINGS";

    public static final String TBL_TINCAN = "TINCAN";

    private Context dbctx;
    private WebAPIClient wap;
    private SharedPreferences sharedPreferences;
    //    private SharedPreferences.Editor prefEditor;
    private AppController appController;
    private PreferencesManager preferencesManager;
    AppUserModel appUserModel;

    SetCompleteListner listner;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbctx = context;
        wap = new WebAPIClient();
        sharedPreferences = dbctx.getSharedPreferences(StaticValues.INSTANCYPREFS_NAME, MODE_PRIVATE);
        appController = ((AppController) context.getApplicationContext());
        appUserModel = AppUserModel.getInstance();
        PreferencesManager.initializeInstance(context);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERLOGINID));
        appUserModel.setPassword(preferencesManager.getStringValue(StaticValues.KEY_USERPASSWORD));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_APP_SETTINGS + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, appTextColor TEXT, appBGColor TEXT, menuTextColor TEXT, menuBGColor TEXT, selectedMenuTextColor TEXT, selectedMenuBGColor TEXT, listBGColor TEXT, listBorderColor TEXT, menuHeaderBGColor TEXT, menuHeaderTextColor TEXT, menuBGAlternativeColor TEXT, menuBGSelectTextColor TEXT, appButtonBGColor TEXT, appButtonTextColor TEXT, appHeaderTextColor TEXT, appHeaderColor TEXT, appLoginBGColor TEXT,appLoginPGTextColor TEXT, selfRegistrationAllowed TEXT, contentDownloadType TEXT, courseAppContent TEXT, enableNativeCatlog TEXT, enablePushNotification TEXT, nativeAppType TEXT, autodownloadsizelimit TEXT, catalogContentDownloadType TEXT, fileUploadButtonColor TEXT, firstTarget TEXT, secondTarget TEXT, thirdTarget TEXT, contentAssignment TEXT, newContentAvailable TEXT, contentUnassigned TEXT,enableNativeLogin TEXT, nativeAppLoginLogo TEXT,enableBranding TEXT,selfRegDisplayName TEXT, firstEvent TEXT, isFacebook  TEXT, isLinkedin TEXT, isGoogle TEXT, isTwitter TEXT, siteID TEXT, siteURL TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_NATIVEMENUS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, menuid TEXT, displayname TEXT, displayorder INTEGER, image TEXT, isofflinemenu TEXT, isenabled TEXT, contexttitle TEXT, contextmenuid TEXT, repositoryid TEXT, landingpagetype TEXT, categorystyle TEXT, componentid TEXT, conditions TEXT, parentmenuid TEXT, parameterstrings TEXT, siteid TEXT, siteurl TEXT)");


        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_DOWNLOADDATA
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,userid TEXT,siteid TEXT,siteurl TEXT,sitename TEXT,contentid TEXT,objectid TEXT,coursename TEXT,author TEXT,shortdes TEXT,longdes TEXT,imagedata TEXT,medianame TEXT,createddate TEXT,startpage TEXT,eventstarttime TEXT,eventendtime TEXT,objecttypeid TEXT,locationname TEXT,timezone TEXT,scoid TEXT,participanturl TEXT,status TEXT,password TEXT,displayname TEXT,islistview TEXT,isdownloaded TEXT,courseattempts TEXT,eventcontentid TEXT,relatedcontentcount TEXT,durationenddate TEXT,ratingid TEXT,publisheddate TEXT,isExpiry TEXT, mediatypeid TEXT, dateassigned TEXT, keywords TEXT, downloadurl TEXT, offlinepath TEXT, presenter TEXT, eventaddedtocalender TEXT, joinurl TEXT, typeofevent TEXT,progress TEXT)");

        //used upto here

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATALOGDATA
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,siteid TEXT,siteurl TEXT,sitename TEXT,displayname TEXT, username TEXT, password TEXT, userid TEXT, contentid TEXT,coursename TEXT,author TEXT,shortdes TEXT,longdes TEXT,imagedata TEXT,medianame TEXT,createddate TEXT,startpage TEXT,objecttypeid TEXT,locationname TEXT,timezone TEXT,scoid TEXT,participanturl TEXT,viewtype TEXT, price TEXT, islistview TEXT, ratingid TEXT,publisheddate TEXT, mediatypeid TEXT, keywords TEXT, googleproductid TEXT, currency TEXT,itemtype TEXT,categorycompid TEXT, downloadurl TEXT, offlinepath TEXT, isaddedtomylearning INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERSESSION
                + "(sessionid INTEGER PRIMARY KEY AUTOINCREMENT,userid INTEGER,scoid INTEGER,siteid INTEGR,attemptnumber INTEGER,sessiondatetime DATETIME,timespent TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_STUDENTRESPONSES
                + "(RESPONSEID INTEGER PRIMARY KEY AUTOINCREMENT,siteid INTEGER,scoid INTEGER,userid INTEGER,questionid INTEGER,assessmentattempt INTEGER,questionattempt INTEGER,attemptdate DATETIME,studentresponses TEXT,result TEXT,attachfilename TEXT,attachfileid TEXT,rindex INTEGER,attachedfilepath TEXT,optionalNotes TEXT,capturedVidFileName TEXT,capturedVidId TEXT,capturedVidFilepath TEXT,capturedImgFileName TEXT,capturedImgId TEXT,capturedImgFilepath TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_QUESTIONS
                + "(RESPONSEID INTEGER PRIMARY KEY AUTOINCREMENT,siteid INTEGER,scoid INTEGER,userid INTEGER,questionid INTEGER,quesname TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CMI
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,siteid INTEGER,scoid INTEGER,userid INTEGER,location TEXT,status TEXT,suspenddata TEXT,isupdate TEXT,siteurl TEXT,datecompleted DATETIME,noofattempts INTEGER,score TEXT,sequencenumber INTEGER,startdate DATETIME,timespent TEXT,coursemode TEXT,scoremin TEXT,scoremax TEXT,submittime TEXT,randomquesseq TEXT,pooledquesseq TEXT,textResponses TEXT, objecttypeid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_TRACKOBJECTS
                + "(RESPONSEID INTEGER PRIMARY KEY AUTOINCREMENT,trackscoid INTEGER,scoid INTEGER,sequencenumber INTEGER,siteid INTEGER,userid INTEGER,objecttypeid INTEGER,name TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_TINCAN
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, istincan TEXT, lrsendpoint TEXT, lrsauthorization TEXT, lrsauthorizationpassword TEXT, enabletincansupportforco TEXT, enabletincansupportforao TEXT, enabletincansupportforlt TEXT, base64lrsAuthKey TEXT, siteid TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_TRACKLISTDATA
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,userid INTEGER,siteid INTEGER,siteurl TEXT,sitename TEXT,contentid TEXT,coursename TEXT,author TEXT,shortdes TEXT,longdes TEXT,imagedata TEXT,medianame TEXT,createddate DATE,startpage TEXT,eventstarttime DATE,eventendtime DATE,objecttypeid INTEGER,locationname TEXT,timezone TEXT,scoid INTEGER,participanturl TEXT,courselaunchpath TEXT,status TEXT,password TEXT,eventid TEXT,displayname TEXT,trackscoid TEXT,parentid TEXT,blockname TEXT,showstatus TEXT,timedelay TEXT,isdiscussion TEXT,eventcontentid TEXT, sequencenumber TEXT,courseattempts TEXT,mediatypeid TEXT, relatedcontentcount INTEGER, downloadurl TEXT,eventaddedtocalender TEXT, joinurl TEXT,offlinepath TEXT, typeofevent INTEGER,presenter TEXT,isdownloaded TEXT, progress TEXT, stepid  TEXT, ruleid  TEXT,wmessage TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_RELATEDCONTENTDATA
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,userid TEXT,siteid TEXT,siteurl TEXT,sitename TEXT,contentid TEXT,coursename TEXT,author TEXT,shortdes TEXT,longdes TEXT,imagedata TEXT,medianame TEXT,createddate TEXT,startpage TEXT,eventstarttime TEXT,eventendtime TEXT,objecttypeid TEXT,locationname TEXT,timezone TEXT,scoid TEXT,participanturl TEXT,status TEXT,password TEXT,displayname TEXT,islistview TEXT,isdiscussion TEXT,isdownloaded TEXT,courseattempts TEXT,eventcontentid TEXT,wresult TEXT, wmessage TEXT, durationenddate TEXT, isExpiry TEXT, ratingid TEXT, publisheddate TEXT,mediatypeid TEXT,dateassigned TEXT, keywords TEXT, downloadurl TEXT, offlinepath TEXT, presenter TEXT, joinurl TEXT,blockname TEXT,trackscoid TEXT, progress TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_EVENTCONTENTDATA
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,username TEXT,userid INTEGER,siteid INTEGER,siteurl TEXT,sitename TEXT,contentid TEXT,coursename TEXT,author TEXT,shortdes TEXT,longdes TEXT,imagedata TEXT,medianame TEXT,createddate DATE,startpage TEXT,eventstarttime DATE,eventendtime DATE,objecttypeid INTEGER,locationname TEXT,timezone TEXT,scoid INTEGER,participanturl TEXT,courselaunchpath TEXT,status TEXT,password TEXT,eventid TEXT,displayname TEXT,eventcontentid TEXT,islistview TEXT,isdiscussion TEXT,isdownloaded TEXT,attemptnumber TEXT,wresult TEXT,wmessage TEXT,expirydate DATE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_LRSDATA
                + "(lrsid INTEGER PRIMARY KEY AUTOINCREMENT,LRS TEXT,url TEXT,method TEXT,data TEXT,auth TEXT,callback TEXT,lrsactor TEXT,extraHeaders TEXT,siteid INTEGER,scoid INTEGER,userid INTEGER,isupdate TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_SITETINCANCONFIG
                + "(configid INTEGER PRIMARY KEY AUTOINCREMENT ,configkeyvalue TEXT,configkey TEXT,siteid INTEGER,userid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATEGORY
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,parentid INTEGER,categoryname TEXT,categoryid INTEGER,categoryicon TEXT,contentcount INTEGER,siteid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_JOBROLES
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,categoryid INTEGER,categoryname TEXT,parentid INTEGER,contentcount INTEGER,siteid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CONTENTTYPES
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,attributeconfigid INTEGER,objecttypeid INTEGER,endUservisibility TEXT,displayorder INTEGER,localename TEXT,displaytext TEXT,siteid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATEGORYCONTENT
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,categoryid INTEGER,contentid TEXT,displayorder INTEGER,modifieddate DATE,siteid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_JOBROLECONTENT
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,contentid TEXT,categoryid INTEGER,modifieddate DATE,assignedby TEXT,siteid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_NOTIFICATIONSETTINGS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,siteid INTEGER,notificationid TEXT,notificationname TEXT,notificationstatus TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERSETTINGS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,userid INTEGER,siteid INTEGER,notificationid TEXT,notificationstatus TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_AUTODOWNLOADSETTINGS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,userid INTEGER,siteid INTEGER,enableautodownload Text,usingmobiledata TEXT,autodownloadstatus TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPAGENOTES
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,ContentID TEXT,PageID TEXT,UserID TEXT,Usernotestext TEXT,TrackID TEXT,SequenceID TEXT,NoteDate TEXT,Notecount TEXT,ModifiedNotedate TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS UserProfile(ID INTEGER PRIMARY KEY AUTOINCREMENT,userID TEXT,siteId INTEGER,FirstName TEXT,LastName TEXT,DisplayName TEXT,Organization TEXT,Email TEXT,StreetAddress TEXT,City TEXT,State TEXT,Phone TEXT,isUpdated TEXT,DOB TEXT)");
        // db.execSQL("CREATE TABLE IF NOT EXISTS "
        // + TBL_NATIVEMENUS
        // +
        // "(ID INTEGER PRIMARY KEY AUTOINCREMENT,menuID INTEGER,menuContextName TEXT,menuDisplayName TEXT,menuImage TEXT,siteid INTEGER,menuDisplayOrder TEXT,isOfflineMenu TEXT,isEnabled TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPREFERENCES
                + "(userid INTEGER,siteid INTEGER, keyname TEXT, prefvalue TEXT,PRIMARY KEY(siteid,userid,keyname))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_NATIVESETTINGS
                + "(siteid INTEGER, prefid INTEGER, keyname TEXT, defaultvalue TEXT, displaytext TEXT, PRIMARY KEY(siteid,keyname))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPROFILEFIELDS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, objectid TEXT,accounttype TEXT,orgunitid TEXT, siteid TEXT, approvalstatus TEXT, firstname TEXT, lastname TEXT, displayname TEXT, organization TEXT, email TEXT, usersite TEXT, supervisoremployeeid TEXT, addressline1 TEXT, addresscity TEXT, addressstate TEXT, addresszip TEXT , addresscountry TEXT, phone TEXT, mobilephone TEXT, imaddress TEXT,dateofbirth TEXT, gender TEXT, nvarchar6 TEXT, paymentmode TEXT, nvarchar7 TEXT, nvarchar8 TEXT, nvarchar9 TEXT, securepaypalid TEXT, nvarchar10 TEXT, picture TEXT, highschool TEXT, college TEXT, highestdegree TEXT, jobtitle TEXT, businessfunction TEXT, primaryjobfunction TEXT, payeeaccountno TEXT, payeename TEXT, paypalaccountname TEXT, paypalemail TEXT, shipaddline1 TEXT, shipaddcity TEXT, shipaddstate TEXT, shipaddzip TEXT, shipaddcountry TEXT, shipaddphone TEXT, isupdated TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPROFILEGROUPS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, groupid  INTEGER,groupname TEXT, objecttypeid TEXT, siteid TEXT,showinprofile TEXT,displayorder INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPROFILECONFIGS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, siteid TEXT, datafieldname TEXT, aliasname TEXT, attributedisplaytext TEXT, groupid TEXT, displayorder INTEGER, attributeconfigid TEXT, isrequired TEXT, iseditable TEXT, enduservisibility TEXT, uicontroltypeid TEXT, name TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPROFILEFIELDOPTIONS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, choiceid INTEGER, attributeconfigid INTEGER, choicetext TEXT, choicevalue TEXT, localename TEXT, parenttext TEXT, siteid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CALENDARADDEDEVENTS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, userid INTEGER,siteid INTEGER,scoid INTEGER,eventid INTEGER, eventname TEXT, reminderid INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_FORUMS
                + "(forumname TEXT,forumid INTEGER,name TEXT, createddate TEXT,author TEXT,nooftopics TEXT,totalposts TEXT,existing TEXT,description TEXT,isprivate TEXT,active TEXT,siteid TEXT,createduserid TEXT,parentforumid TEXT,displayorder TEXT,requiressubscription TEXT,createnewtopic TEXT,attachfile TEXT,likeposts TEXT,sendemail TEXT,moderation TEXT,siteurl TEXT, PRIMARY KEY(siteurl,forumid))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_FORUMTOPICS
                + "(contentid TEXT,forumid TEXT,name TEXT, createddate TEXT,createduserid TEXT,noofreplies TEXT,noofviews TEXT,siteid INTEGER, longdescription TEXT,uploadfilename TEXT, siteurl TEXT, PRIMARY KEY(siteurl,forumid,contentid))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_TOPICCOMMENTS
                + "(commentid INTEGER,topicid TEXT,forumid TEXT,message TEXT,posteddate TEXT,postedby TEXT,replyid TEXT,siteid INTEGER,uploadfilename TEXT, siteurl TEXT, PRIMARY KEY(siteurl,forumid,topicid,commentid))");
        // db.execSQL("CREATE TABLE IF NOT EXISTS "
        // + TBL_TOPICCOMMENTS
        // +
        // "(contentid TEXT,topicid TEXT,forumid TEXT,name TEXT,createddate TEXT,latestreplyby TEXT,noofreplies TEXT,noofviews TEXT,siteid INTEGER,PRIMARY KEY(siteid,forumid,topicid,contentid))");

//        db.execSQL("CREATE TABLE IF NOT EXISTS "
//                + TBL_OFFLINEUSERS
//                + "(userid TEXT, username TEXT, password TEXT, siteid TEXT, siteurl TEXT, PRIMARY KEY(username, password, siteurl))");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_OFFLINEUSERS
                + "(userid TEXT, orgunitid TEXT, userstatus TEXT, displayname TEXT, siteid TEXT, username TEXT, password TEXT, siteurl TEXT )");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ALLUSERSINFO
                + "(picture TEXT,userid TEXT,displayname TEXT,email TEXT,profileimagepath TEXT, siteid TEXT, PRIMARY KEY(userid, siteid))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_USERPRIVILEGES
                + "(userid TEXT,privilegeid TEXT,componentid TEXT,parentprivilegeid TEXT, objecttypeid TEXT, roleid TEXT,siteid TEXT, siteurl TEXT, PRIMARY KEY(userid, privilegeid, roleid, siteurl))");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ASKQUESTIONS
                + "(questionid INTEGER, userid TEXT, username TEXT, userquestion TEXT, posteddate TEXT, createddate TEXT, answers TEXT, questioncategories TEXT, siteid TEXT,siteurl TEXT, PRIMARY KEY(questionid, userid, siteurl))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ASKRESPONSES
                + "(questionid INTEGER, response TEXT, respondeduserid TEXT, respondedusername TEXT, respondeddate TEXT, responsedate TEXT, responseid INTEGER, siteid TEXT,siteurl TEXT, PRIMARY KEY(questionid, responseid, siteurl))");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ASKQUESTIONCATEGORIES
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, categoryid INTEGER, category TEXT, siteid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ASKQUESTIONCATEGORYMAPPING
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, questionid INTEGER, categoryid INTEGER, siteid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_ASKQUESTIONSKILLS
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, orgunitid TEXT, preferrenceid INTEGER, preferrencetitle TEXT, shortskillname TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATEGORIES
                + "(parentid TEXT, categoryname TEXT, categoryid TEXT, categoryicon TEXT, contentcount TEXT, column1 TEXT, siteid TEXT, componentid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_SUBCATEGORIES
                + "(parentid TEXT, categoryname TEXT, categoryid TEXT, subcategoryicon TEXT, contentcount TEXT, column1 TEXT, siteid TEXT, componentid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATEGORIESCONTENT
                + "(categoryid TEXT, contentid TEXT, displayorder TEXT, modifieddate TEXT, siteid TEXT, componentid TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_SKILLS
                + "(categoryid TEXT, categoryname TEXT, categoryicon TEXT, coursecount TEXT, siteid TEXT, componentid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_SUBSKILLS
                + "(subcategoryid TEXT, subcategoryname TEXT, categoryid TEXT, subcategoryicon TEXT, siteid TEXT, componentid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_SKILLCONTENT
                + "(contentid TEXT, preferenceid TEXT, dateassigned TEXT, assignedby TEXT, siteid TEXT, componentid TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_JOBROLESNEW
                + "(jobroleid TEXT, jobroleparentid TEXT, jobrolename TEXT, shortjobrolename TEXT, siteid TEXT, componentid TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_JOBROLECONTENTNEW
                + "(contentid TEXT, jobroleid TEXT, dateassigned TEXT, assignedby TEXT, siteid TEXT, componentid TEXT)");

        // MY IMPLEMENTATIONS FOR COMMUNITY LISTING UPENDRA

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_COMMUNITYLISTING
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,learningportalid INTEGER, learningprovidername TEXT, communitydescription TEXT, keywords TEXT, userid INTEGER, siteid INTEGER, siteurl TEXT, parentsiteid INTEGER, parentsiteurl TEXT, orgunitid INTEGER, objectid INTEGER, name TEXT, categoryid INTEGER, imagepath TEXT, actiongoto INTEGER, labelalreadyamember TEXT, actionjoincommunity INTEGER, labelpendingrequest TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TBL_CATEGORYCOMMUNITYLISTING
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, categoryid INTEGER, name TEXT, shortcategoryname TEXT, categorydescription TEXT, parentid INTEGER, displayorder INTEGER, componentid INTEGER, parentsiteid INTEGER, userid INTEGER, parentsiteurl TEXT)");

        Log.d(TAG, "onCreate:  TABLES CREATED");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public void deleteAllTableData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TBL_DOWNLOADDATA, null, null);
        db.delete(TBL_CATALOGDATA, null, null);
        db.delete(TBL_USERSESSION, null, null);
        db.delete(TBL_STUDENTRESPONSES, null, null);
        db.delete(TBL_QUESTIONS, null, null);
        db.delete(TBL_CMI, null, null);
        db.delete(TBL_TRACKOBJECTS, null, null);
        db.delete(TBL_TRACKLISTDATA, null, null);
        db.delete(TBL_EVENTCONTENTDATA, null, null);
        db.delete(TBL_LRSDATA, null, null);
        db.delete(TBL_SITETINCANCONFIG, null, null);
        db.delete(TBL_CATEGORY, null, null);
        db.delete(TBL_JOBROLES, null, null);
        db.delete(TBL_CONTENTTYPES, null, null);
        db.delete(TBL_CATEGORYCONTENT, null, null);
        db.delete(TBL_JOBROLECONTENT, null, null);
        db.delete(TBL_NOTIFICATIONSETTINGS, null, null);
        db.delete(TBL_USERSETTINGS, null, null);
        db.delete(TBL_AUTODOWNLOADSETTINGS, null, null);
        db.delete(TBL_USERPAGENOTES, null, null);
        db.delete(TBL_NATIVEMENUS, null, null);
        db.delete(TBL_USERPREFERENCES, null, null);
        db.delete(TBL_NATIVESETTINGS, null, null);
        db.delete(TBL_USERPROFILEFIELDS, null, null);
        db.delete(TBL_USERPROFILEGROUPS, null, null);
        db.delete(TBL_USERPROFILECONFIGS, null, null);
        db.delete(TBL_USERPROFILEFIELDOPTIONS, null, null);
        db.delete(TBL_CALENDARADDEDEVENTS, null, null);
        db.delete(TBL_FORUMS, null, null);
        db.delete(TBL_FORUMTOPICS, null, null);
        db.delete(TBL_TOPICCOMMENTS, null, null);
        db.delete(TBL_ASKQUESTIONS, null, null);
        db.delete(TBL_ASKRESPONSES, null, null);
        db.delete(TBL_ASKQUESTIONCATEGORIES, null, null);
        db.delete(TBL_ASKQUESTIONCATEGORYMAPPING, null, null);
        db.delete(TBL_ASKQUESTIONSKILLS, null, null);
        db.delete(TBL_OFFLINEUSERS, null, null);
        db.delete(TBL_ALLUSERSINFO, null, null);
        db.delete(TBL_USERPRIVILEGES, null, null);
        db.delete(TBL_CATEGORIES, null, null);
        db.delete(TBL_SUBCATEGORIES, null, null);
        db.delete(TBL_CATEGORIESCONTENT, null, null);
        db.delete(TBL_SKILLS, null, null);
        db.delete(TBL_SUBSKILLS, null, null);
        db.delete(TBL_SKILLCONTENT, null, null);
        db.delete(TBL_JOBROLESNEW, null, null);
        db.delete(TBL_SUBJOBROLESNEW, null, null);
        db.delete(TBL_JOBROLECONTENTNEW, null, null);

    }

    // Methods for inserting the values into tables

    public void getSiteSettingsServer(String webApiUrl, String siteUrl) {

        appController.setAuthentication(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));

        String paramsString = "SiteURL=" + siteUrl;

        InputStream inputStream = wap.callWebAPIMethod(webApiUrl, "MobileLMS",
                "MobileGetLearningPortalInfo", appController.getAuthentication(), paramsString);

        if (inputStream != null) {

            String result = convertStreamToString(inputStream);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();
            UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
            String siteid = "";
            try {

                if (jsonObject.has("table")) {
                    JsonArray jsonTable = jsonObject.get("table").getAsJsonArray();
                    for (int i = 0; i < jsonTable.size(); i++) {
                        Log.d(TAG, "siteSettingJsonObj: inDB " + jsonTable);
                        JsonObject siteSettingJsonObj = jsonTable.get(i).getAsJsonObject();
                        if (siteSettingJsonObj.has("siteid")) {
//                            Log.d(TAG, "siteSettingJsonObj: siteid  " + siteSettingJsonObj.get("siteid"));
//                            [{"SiteID":374,"Name":"Playground","SiteURL":"http://Test-admin.instancy.net/"}]
                            siteid = siteSettingJsonObj.get("siteid").getAsString();
                            String strsitename = siteSettingJsonObj.get("name").getAsString();
                            String platformurl = siteSettingJsonObj.get("siteurl").getAsString();
                            preferencesManager.setStringValue(siteid, StaticValues.KEY_SITEID);
                            preferencesManager.setStringValue(strsitename, StaticValues.KEY_SITENAME);
                            preferencesManager.setStringValue(platformurl, StaticValues.KEY_PLATFORMURL);
//                            prefEditor = sharedPreferences.edit();
//                            prefEditor.putString(StaticValues.KEY_SITEID, siteid);
//                            prefEditor.putString(StaticValues.KEY_SITENAME, strsitename);
//                            prefEditor.putString(StaticValues.KEY_PLATFORMURL, platformurl);
                            appUserModel.setSiteIDValue(siteid);
                            appUserModel.setSiteName(strsitename);
//                            prefEditor.commit();
                        }
                    }
                }
                if (jsonObject.has("table1")) {
                    JsonArray jsonTableOne = jsonObject.get("table1").getAsJsonArray();
                    if (jsonTableOne.size() > 0) {

                        for (int i = 0; i < jsonTableOne.size(); i++) {
                            JsonObject uisettingsJsonOjb = jsonTableOne.get(i).getAsJsonObject();

                            if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#APP_BACKGROUNDCOLOR#")) {
                                uiSettingsModel.setAppBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#App_backgrndText#")) {
                                uiSettingsModel.setAppTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb.get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#HEADER_BACKGROUNDCOLOR#")) {
                                uiSettingsModel.setAppHeaderColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#NativeApp_Header_Text#")) {
                                uiSettingsModel.setAppHeaderTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_BG_COLOR#")) {
                                uiSettingsModel.setMenuBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_TEXT_COLOR#")) {
                                uiSettingsModel.setMenuTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_SL_BG_COLOR#")) {
                                uiSettingsModel.setMenuBGSelectTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_SL_TEXT_COLOR#")) {
                                uiSettingsModel.setSelectedMenuTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#HEADER_BG_COLOR#")) {
                                uiSettingsModel.setHeaderBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb.get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#HEADER_TEXT_COLOR#")) {
                                uiSettingsModel.setAppHeaderTextColor(uisettingsJsonOjb.get("csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_HEADER_BGCOLOR#")) {
                                uiSettingsModel.setMenuHeaderBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_HEADER_TEXT_COLOR#")) {
                                uiSettingsModel.setMenuHeaderTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_BG_ALTERNATIVECOLOR#")) {
                                uiSettingsModel.setMenuBGAlternativeColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#MENU_BG_SELECT_TEXTCOLOR#")) {
                                uiSettingsModel.setMenuBGSelectTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#Button_Background_Color#")) {
                                uiSettingsModel.setAppButtonBgColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#Button_Text_Color#")) {
                                uiSettingsModel.setAppButtonTextColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#FileUplad_BUTTON#")) {
                                uiSettingsModel.setFileUploadButtonColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb.get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#LIST_BG_COLOR#")) {
                                uiSettingsModel.setListBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb.get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#LIST_BORDER_COLOR#")) {
                                uiSettingsModel.setListBorderColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#NativeLogin_Page_Background#")) {
                                uiSettingsModel.setAppLoginBGColor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            } else if (uisettingsJsonOjb
                                    .get("csseditingpalceholdername")
                                    .getAsString()
                                    .equals("#NativeAppLogin_Page_Text#")) {
                                uiSettingsModel.setAppLoginTextolor(uisettingsJsonOjb.get(
                                        "csseditingpalceholdervalue")
                                        .getAsString()
                                        .substring(0, 7));
                            }

                        }

                    }

                }

                if (jsonObject.has("table2")) {
                    JsonArray jsonTableTwo = jsonObject.get("table2").getAsJsonArray();
                    if (jsonTableTwo.size() > 0) {

                        for (int i = 0; i < jsonTableTwo.size(); i++) {
                            JsonObject nativeSettingsObj = jsonTableTwo.get(i).getAsJsonObject();
//                            Log.d(TAG, "uisettingsJsonOjb: "+nativeSettingsObj);

                            if (nativeSettingsObj.get("name").getAsString().equals("autodownloadsizelimit")) {

                                uiSettingsModel.setAutodownloadsizelimit(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if (nativeSettingsObj.get("name").getAsString().equals("CatalogContentDownloadType")) {

                                uiSettingsModel.setCatalogContentDownloadType(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equals("courseappcontent"))) {

                                uiSettingsModel.setCourseAppContent(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equals("ContentDownloadType"))) {

                                uiSettingsModel.setContentDownloadType(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equals("EnableNativeCatlog"))) {

                                uiSettingsModel.setEnableNativeCatlog(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equals("EnablePushNotification"))) {

                                uiSettingsModel.setEnablePushNotification(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equals("nativeapptype"))) {

                                uiSettingsModel.setNativeAppType(nativeSettingsObj.get("keyvalue").getAsString());

                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("SelfRegistrationAllowed"))) {

                                uiSettingsModel.setSelfRegistrationAllowed(nativeSettingsObj.get("keyvalue").getAsString());
                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("EnableNativeSplashImage"))) {

                                uiSettingsModel.setEnableBranding(nativeSettingsObj.get("keyvalue").getAsString());
                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("EnableNativeSplashImage"))) {

                                uiSettingsModel.setEnableBranding(nativeSettingsObj.get("keyvalue").getAsString());
                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("SelfRegistrationDisplayName"))) {

                                uiSettingsModel.setSignUpName(nativeSettingsObj.get("keyvalue").getAsString());
                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("EnableNativeAppLoginSetting"))) {

                                uiSettingsModel.setEnableAppLogin(nativeSettingsObj.get("keyvalue").getAsString());
                            } else if ((nativeSettingsObj.get("name").getAsString().equalsIgnoreCase("NativeAppLoginLogo"))) {

                                String appLogo = nativeSettingsObj.get("keyvalue").getAsString();
                                if (appLogo.length() == 0) {
                                    String appLogos = appUserModel.getSiteURL() + "/Content/SiteConfiguration/374" + "/LoginSettingLogo/" + appLogo;
                                    uiSettingsModel.setEnableAppLogin(appLogos);
                                }
                            }

                        }
                    }

                }

                if (jsonObject.has("table3")) {
                    JsonArray jsonTableThree = jsonObject.get("table3").getAsJsonArray();
                    if (jsonTableThree.size() > 0) {

                        for (int i = 0; i < jsonTableThree.size(); i++) {
                            JsonObject nativeSettingsObj = jsonTableThree.get(i).getAsJsonObject();
                            Log.d(TAG, "table three: " + nativeSettingsObj);
                        }
                    }
                }

                if (jsonObject.has("table4")) {
                    JsonArray jsonTableFour = jsonObject.get("table4").getAsJsonArray();
                    if (jsonTableFour.size() > 0) {

                        for (int i = 0; i < jsonTableFour.size(); i++) {
                            JsonObject nativeSettingsObj = jsonTableFour.get(i).getAsJsonObject();
                            Log.d(TAG, "table three: " + nativeSettingsObj);
                            if (nativeSettingsObj.get("privilegeid").getAsString().equals("908")) {

                                uiSettingsModel.setIsFaceBook(nativeSettingsObj.get("ismobileprivilege").getAsString());

                            } else if (nativeSettingsObj.get("privilegeid").getAsString().equals("911")) {

                                uiSettingsModel.setIsTwitter(nativeSettingsObj.get("ismobileprivilege").getAsString());

                            } else if ((nativeSettingsObj.get("privilegeid").getAsString().equals("909"))) {

                                uiSettingsModel.setIsLinkedIn(nativeSettingsObj.get("ismobileprivilege").getAsString());

                            } else if ((nativeSettingsObj.get("privilegeid").getAsString().equals("910"))) {

                                uiSettingsModel.setIsGoogle(nativeSettingsObj.get("ismobileprivilege").getAsString());

                            }


                        }
                    }
                }

                Log.d(TAG, "getNativeAppType: " + uiSettingsModel.getNativeAppType());

                insertIntoAppSettingsTable(uiSettingsModel, siteid, siteUrl);
            } catch (JsonIOException jsonExce) {
                jsonExce.printStackTrace();
            }
        }
    }


    public void insertIntoAppSettingsTable(UiSettingsModel uiSettingsModel, String siteid, String siteUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteRecordsinTable(siteid, siteUrl, TBL_APP_SETTINGS);
        try {
            String strExeQuery = "";
            strExeQuery = "INSERT INTO APPSETTINGS (appTextColor , appBGColor , menuTextColor , menuBGColor , selectedMenuTextColor , selectedMenuBGColor , listBGColor , listBorderColor , menuHeaderBGColor , menuHeaderTextColor , menuBGAlternativeColor , menuBGSelectTextColor , appButtonBGColor , appButtonTextColor , appHeaderTextColor , appHeaderColor , appLoginBGColor ,appLoginPGTextColor , selfRegistrationAllowed , contentDownloadType , courseAppContent , enableNativeCatlog , enablePushNotification , nativeAppType , autodownloadsizelimit , catalogContentDownloadType , fileUploadButtonColor , firstTarget , secondTarget , thirdTarget , contentAssignment , newContentAvailable , contentUnassigned ,enableNativeLogin , nativeAppLoginLogo ,enableBranding ,selfRegDisplayName , firstEvent , isFacebook  , isLinkedin , isGoogle , isTwitter , siteID , siteURL )"
                    + " VALUES ('"
                    + uiSettingsModel.getAppTextColor()
                    + "','"
                    + uiSettingsModel.getAppBGColor()
                    + "','"
                    + uiSettingsModel.getMenuTextColor()
                    + "','"
                    + uiSettingsModel.getMenuBGColor()
                    + "','"
                    + uiSettingsModel.getSelectedMenuTextColor()
                    + "','"
                    + uiSettingsModel.getSelectedMenuBGColor()
                    + "','"
                    + uiSettingsModel.getListBGColor()
                    + "','"
                    + uiSettingsModel.getListBorderColor()
                    + "','"
                    + uiSettingsModel.getMenuBGColor()
                    + "','"
                    + uiSettingsModel.getMenuHeaderTextColor()
                    + "','"
                    + uiSettingsModel.getMenuBGAlternativeColor()
                    + "','"
                    + uiSettingsModel.getSelectedMenuTextColor()
                    + "','"
                    + uiSettingsModel.getAppButtonBgColor()
                    + "','"
                    + uiSettingsModel.getAppButtonTextColor()
                    + "','"
                    + uiSettingsModel.getAppHeaderTextColor()
                    + "','"
                    + uiSettingsModel.getAppHeaderColor()
                    + "','"
                    + uiSettingsModel.getAppLoginBGColor()
                    + "','"
                    + uiSettingsModel.getAppLoginTextolor()
                    + "','"
                    + uiSettingsModel.getSelfRegistrationAllowed()
                    + "','"
                    + uiSettingsModel.getContentDownloadType()
                    + "','"
                    + uiSettingsModel.getCourseAppContent()
                    + "','"
                    + uiSettingsModel.getEnableNativeCatlog()
                    + "','"
                    + uiSettingsModel.getEnablePushNotification()
                    + "','"
                    + uiSettingsModel.getNativeAppType()
                    + "','"
                    + uiSettingsModel.getAutodownloadsizelimit()
                    + "','"
                    + uiSettingsModel.getCatalogContentDownloadType()
                    + "','"
                    + uiSettingsModel.getFileUploadButtonColor()
                    + "','"
                    + uiSettingsModel.getFirstTarget()
                    + "','"
                    + uiSettingsModel.getSecondTarget()
                    + "','"
                    + uiSettingsModel.getThirdTarget()
                    + "','"
                    + uiSettingsModel.getContentAssignment()
                    + "','"
                    + uiSettingsModel.getNewContentAvailable()
                    + "','"
                    + uiSettingsModel.getContentUnassigned()
                    + "','"
                    + uiSettingsModel.getEnableAppLogin()
                    + "','"
                    + uiSettingsModel.getNativeAppLoginLogo()
                    + "','"
                    + uiSettingsModel.getEnableBranding()
                    + "','"
                    + uiSettingsModel.getSignUpName()
                    + "','"
                    + uiSettingsModel.getFirstEvent()
                    + "','"
                    + uiSettingsModel.getIsFaceBook()
                    + "','"
                    + uiSettingsModel.getIsLinkedIn()
                    + "','"
                    + uiSettingsModel.getIsGoogle()
                    + "','"
                    + uiSettingsModel.getIsTwitter()
                    + "','"
                    + siteid
                    + "','"
                    + siteUrl
                    + "')";
            db.execSQL(strExeQuery);
        } catch (SQLiteException sqlEx) {
            sqlEx.printStackTrace();

        }
        db.close();
    }

    // Method for getting appsetting model from local sqlite db

    public UiSettingsModel getAppSettingsFromLocal(String siteUrl,
                                                   String siteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        UiSettingsModel uiSettingsModel = UiSettingsModel.getInstance();
        String selQuery = "select * from APPSETTINGS where siteid = '" + siteId + "' and siteurl= '" + siteUrl + "'";
        Cursor cursor = db.rawQuery(selQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            uiSettingsModel.setMenuTextColor(cursor.getString(cursor
                    .getColumnIndex("menuTextColor")));

            uiSettingsModel.setAppTextColor(cursor.getString(cursor
                    .getColumnIndex("appTextColor")));

            uiSettingsModel.setMenuBGColor(cursor.getString(cursor
                    .getColumnIndex("menuBGColor")));

            uiSettingsModel.setSelectedMenuTextColor(cursor.getString(cursor
                    .getColumnIndex("selectedMenuTextColor")));

            uiSettingsModel.setSelectedMenuBGColor(cursor.getString(cursor
                    .getColumnIndex("selectedMenuBGColor")));

            uiSettingsModel.setAppHeaderTextColor(cursor.getString(cursor
                    .getColumnIndex("appHeaderTextColor")));

            uiSettingsModel.setListBorderColor(cursor.getString(cursor
                    .getColumnIndex("listBorderColor")));

            uiSettingsModel.setListBGColor(cursor.getString(cursor
                    .getColumnIndex("listBGColor")));

            uiSettingsModel.setMenuHeaderTextColor(cursor.getString(cursor
                    .getColumnIndex("menuHeaderTextColor")));

            uiSettingsModel.setMenuHeaderBGColor(cursor.getString(cursor
                    .getColumnIndex("menuHeaderBGColor")));

            uiSettingsModel.setMenuBGAlternativeColor(cursor.getString(cursor
                    .getColumnIndex("menuBGAlternativeColor")));

            uiSettingsModel.setMenuBGSelectTextColor(cursor.getString(cursor
                    .getColumnIndex("menuBGSelectTextColor")));

            uiSettingsModel.setAppHeaderColor(cursor.getString(cursor
                    .getColumnIndex("appHeaderColor")));

            uiSettingsModel.setSelfRegistrationAllowed(cursor.getString(cursor
                    .getColumnIndex("selfRegistrationAllowed")));

            uiSettingsModel.setContentDownloadType(cursor.getString(cursor
                    .getColumnIndex("contentDownloadType")));

            uiSettingsModel.setCourseAppContent(cursor.getString(cursor
                    .getColumnIndex("courseAppContent")));

            uiSettingsModel.setEnableNativeCatlog(cursor.getString(cursor
                    .getColumnIndex("enableNativeCatlog")));

            uiSettingsModel.setEnablePushNotification(cursor.getString(cursor
                    .getColumnIndex("enablePushNotification")));

            uiSettingsModel.setNativeAppType(cursor.getString(cursor
                    .getColumnIndex("nativeAppType")));

            uiSettingsModel.setAutodownloadsizelimit(cursor.getString(cursor
                    .getColumnIndex("autodownloadsizelimit")));

            uiSettingsModel.setCatalogContentDownloadType(cursor.getString(cursor
                    .getColumnIndex("catalogContentDownloadType")));

            uiSettingsModel.setFileUploadButtonColor(cursor.getString(cursor
                    .getColumnIndex("fileUploadButtonColor")));

            uiSettingsModel.setFirstTarget(cursor.getString(cursor
                    .getColumnIndex("firstTarget")));

            uiSettingsModel.setSecondTarget(cursor.getString(cursor
                    .getColumnIndex("secondTarget")));

            uiSettingsModel.setThirdTarget(cursor.getString(cursor
                    .getColumnIndex("thirdTarget")));

            uiSettingsModel.setContentAssignment(cursor.getString(cursor
                    .getColumnIndex("contentAssignment")));

            uiSettingsModel.setNewContentAvailable(cursor.getString(cursor
                    .getColumnIndex("newContentAvailable")));

            uiSettingsModel.setContentUnassigned(cursor.getString(cursor
                    .getColumnIndex("contentUnassigned")));

            uiSettingsModel.setFirstEvent(cursor.getString(cursor
                    .getColumnIndex("firstEvent")));

            uiSettingsModel.setIsFaceBook(cursor.getString(cursor
                    .getColumnIndex("isFacebook")));

            uiSettingsModel.setIsLinkedIn(cursor.getString(cursor
                    .getColumnIndex("isLinkedin")));

            uiSettingsModel.setIsTwitter(cursor.getString(cursor
                    .getColumnIndex("isTwitter")));

            uiSettingsModel.setIsGoogle(cursor.getString(cursor
                    .getColumnIndex("isGoogle")));

            Log.d(TAG, "getReportButtonTextColor: " + uiSettingsModel.getAppHeaderColor());
        }
        cursor.close();
        db.close();
        return uiSettingsModel;
    }


    // Methods for gettting native menus from server

    public void getNativeMenusFromServer(String webApiUrl, String siteUrl) {
        String paramsString = "SiteURL=" + siteUrl;
        InputStream inputStream = wap.callWebAPIMethod(webApiUrl,
                "MobileLMS", "MobileGetNativeMenus", appController.getAuthentication(),
                paramsString);
        appController.setSiteId(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
        if (inputStream != null) {

            String result = convertStreamToString(inputStream);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();

            try {
                JsonArray jsonTable = jsonObject.get("table").getAsJsonArray();
                // for deleting records in table for respective table
                deleteRecordsinTable(appController.getSiteId(), siteUrl, TBL_NATIVEMENUS);

                for (int i = 0; i < jsonTable.size(); i++) {

                    JsonObject nativeMenuJsonObj = jsonTable.get(i).getAsJsonObject();
                    NativeMenuModel nativeMenuModel = new NativeMenuModel();

                    Log.d(TAG, "nativeMenuJsonObj: inDB " + nativeMenuJsonObj);

                    if (nativeMenuJsonObj.has("menuid")) {

                        JsonElement el = nativeMenuJsonObj.get("menuid");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setMenuid(nativeMenuJsonObj.get("menuid").getAsString());
                        } else {

                            nativeMenuModel.setMenuid("");
                        }

                    }
                    if (nativeMenuJsonObj.has("displayname")) {

                        JsonElement el = nativeMenuJsonObj.get("displayname");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setDisplayname(nativeMenuJsonObj.get("displayname").getAsString());
                        } else {

                            nativeMenuModel.setDisplayname("");
                        }


                    }
                    if (nativeMenuJsonObj.has("displayorder")) {


                        JsonElement el = nativeMenuJsonObj.get("displayorder");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setDisplayOrder(nativeMenuJsonObj.get("displayorder").getAsInt());
                        } else {

                            nativeMenuModel.setDisplayOrder(0);
                        }


                    }
                    if (nativeMenuJsonObj.has("image")) {

                        JsonElement el = nativeMenuJsonObj.get("image");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setImage(nativeMenuJsonObj.get("image").getAsString());
                        } else {

                            nativeMenuModel.setImage("");
                        }


                    }
                    if (nativeMenuJsonObj.has("isofflinemenu")) {
                        JsonElement el = nativeMenuJsonObj.get("isofflinemenu");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setIsofflineMenu(nativeMenuJsonObj.get("isofflinemenu").getAsString());

                        } else {

                            nativeMenuModel.setIsofflineMenu("");
                        }

                    }
                    if (nativeMenuJsonObj.has("isenabled")) {

                        JsonElement el = nativeMenuJsonObj.get("isenabled");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setIsEnabled(nativeMenuJsonObj.get("isenabled").getAsString());

                        } else {

                            nativeMenuModel.setIsEnabled("");
                        }

                    }
                    if (nativeMenuJsonObj.has("contexttitle")) {


                        JsonElement el = nativeMenuJsonObj.get("contexttitle");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setContextTitle(nativeMenuJsonObj.get("contexttitle").getAsString());

                        } else {

                            nativeMenuModel.setContextTitle("");
                        }

                    }
                    if (nativeMenuJsonObj.has("contextmenuid")) {


                        JsonElement el = nativeMenuJsonObj.get("contextmenuid");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setContextmenuId(nativeMenuJsonObj.get("contextmenuid").getAsString());

                        } else {

                            nativeMenuModel.setContextmenuId("");
                        }
                    }
                    if (nativeMenuJsonObj.has("repositoryid")) {


                        JsonElement el = nativeMenuJsonObj.get("repositoryid");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setRepositoryId(nativeMenuJsonObj.get("repositoryid").getAsString());


                        } else {

                            nativeMenuModel.setRepositoryId("");
                        }


                    }
                    if (nativeMenuJsonObj.has("landingpagetype")) {


                        JsonElement el = nativeMenuJsonObj.get("landingpagetype");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setLandingpageType(nativeMenuJsonObj.get("landingpagetype").getAsString());


                        } else {

                            nativeMenuModel.setLandingpageType("");
                        }

                    }

                    if (nativeMenuJsonObj.has("parameterstrings")) {


                        JsonElement el = nativeMenuJsonObj.get("parameterstrings");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setParameterString(nativeMenuJsonObj.get("parameterstrings").getAsString());


                        } else {

                            nativeMenuModel.setParameterString("");
                        }

                    }

                    if (nativeMenuJsonObj.has("parentmenuid")) {

                        JsonElement el = nativeMenuJsonObj.get("parentmenuid");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setParentMenuId(nativeMenuJsonObj.get("parentmenuid").getAsString());


                        } else {

                            nativeMenuModel.setParentMenuId("");
                        }

                    }
                    if (nativeMenuJsonObj.has("categorystyle")) {

                        JsonElement el = nativeMenuJsonObj.get("categorystyle");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setCategoryStyle(nativeMenuJsonObj.get("categorystyle").getAsString());


                        } else {

                            nativeMenuModel.setCategoryStyle("");
                        }


                    }
                    if (nativeMenuJsonObj.has("componentid")) {

                        JsonElement el = nativeMenuJsonObj.get("componentid");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setComponentId(nativeMenuJsonObj.get("componentid").getAsString());


                        } else {

                            nativeMenuModel.setComponentId("");
                        }

                    }
                    if (nativeMenuJsonObj.has("conditions")) {

                        JsonElement el = nativeMenuJsonObj.get("conditions");

                        if (el != null && !el.isJsonNull()) {

                            nativeMenuModel.setConditions(nativeMenuJsonObj.get("conditions").getAsString());


                        } else {

                            nativeMenuModel.setConditions("");
                        }

                    }
                    insertIntoNativeMenusTable(nativeMenuModel, appController.getSiteId(), siteUrl);
                }

            } catch (JsonIOException ex) {

                ex.printStackTrace();
            }


        }


    }

    /**
     * To get the first level menus details from local DB.
     *
     * @return {@code List} of {@code Menus} model.
     * @author Upendra
     */

    public List<SideMenusModel> getNativeMainMenusData() {
        List<SideMenusModel> menuList = null;
        SideMenusModel menu = null;
        Boolean isMylearning = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "SELECT *,CASE WHEN menuid IN (SELECT parentmenuid FROM "
                + TBL_NATIVEMENUS
                + ") THEN 1 ELSE 0 END AS issubmenuexists FROM "
                + TBL_NATIVEMENUS
                + " WHERE siteurl= '"
                + appUserModel.getSiteURL()
                + "' AND isenabled='true' AND parentmenuid='0' ORDER BY displayorder";

        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);
            menuList = new ArrayList<SideMenusModel>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    isMylearning = true;
                    menu = new SideMenusModel();
                    menu.setMenuId(cursor.getInt(cursor
                            .getColumnIndex("menuid")));
                    menu.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("displayname")));
                    menu.setDisplayOrder(cursor.getInt(cursor
                            .getColumnIndex("displayorder")));
                    menu.setImage(cursor.getString(cursor
                            .getColumnIndex("image")));
                    int menuIconResId = -1;
                    switch (cursor.getString(
                            cursor.getColumnIndex("contextmenuid"))
                            .toLowerCase()) {
                        case "1":
                            menuIconResId = R.drawable.ic_menu_manage;
                            break;
                        case "2":
                            menuIconResId = R.drawable.ic_menu_manage;
                            break;
                        case "3":
                            menuIconResId = R.drawable.ic_menu_manage;
                            break;
                        case "4":
                            menuIconResId = R.drawable.ic_menu_manage;
                            break;
                        case "5":
                            menuIconResId = R.drawable.ic_menu_manage;
                            break;
                        // case "events":
                        // menuIconResId = R.drawable.event;
                        // break;

                        default:
                            menuIconResId = R.drawable.ic_menu_camera;
                            break;
                    }
                    menu.setMenuImageResId(menuIconResId);
                    menu.setIsOfflineMenu(cursor.getString(cursor
                            .getColumnIndex("isofflinemenu")));
                    menu.setIsEnabled(cursor.getString(cursor
                            .getColumnIndex("isenabled")));
                    menu.setContextTitle(cursor.getString(cursor
                            .getColumnIndex("contexttitle")));
                    menu.setContextMenuId(cursor.getString(cursor
                            .getColumnIndex("contextmenuid")));
                    menu.setRepositoryId(cursor.getString(cursor
                            .getColumnIndex("repositoryid")));
                    menu.setLandingPageType(cursor.getString(cursor
                            .getColumnIndex("landingpagetype")));
                    menu.setCategoryStyle(cursor.getString(cursor
                            .getColumnIndex("categorystyle")));
                    menu.setComponentId(cursor.getString(cursor
                            .getColumnIndex("componentid")));
                    menu.setConditions(cursor.getString(cursor
                            .getColumnIndex("conditions")));
                    menu.setParentMenuId(cursor.getString(cursor
                            .getColumnIndex("parentmenuid")));
                    menu.setParameterStrings(cursor.getString(cursor
                            .getColumnIndex("parameterstrings")));
                    menu.setIsSubMenuExists(cursor.getInt(cursor
                            .getColumnIndex("issubmenuexists")));

                    menuList.add(menu);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            if (db.isOpen()) {
                db.close();
            }
            Log.d("getNativeMenusData", e.getMessage() != null ? e.getMessage()
                    : "Error getting menus");

        }

        if (!isMylearning) {
            menu = new SideMenusModel();
            menu.setMenuId(1);
            menu.setDisplayName("My Learning");
            menu.setDisplayOrder(1);
            menu.setImage("");
            menu.setMenuImageResId(R.drawable.ic_info_black_24dp);
            menu.setIsOfflineMenu("true");
            menu.setIsEnabled("true");
            menu.setContextTitle("My Learning");
            menu.setContextMenuId("1");
            menu.setRepositoryId("");
            menu.setLandingPageType("0");
            menu.setCategoryStyle("0");
            menu.setComponentId("0");
            menu.setConditions("0");
            menu.setParentMenuId("0");
            menu.setParameterStrings("");
            menu.setIsSubMenuExists(0);
            menuList.add(menu);

        }

        menu = new SideMenusModel();
        menu.setMenuId(5555);
        menu.setDisplayName("Sign out");
        menu.setDisplayOrder(9999);
        menu.setImage("");
        menu.setMenuImageResId(R.drawable.ic_info_black_24dp);
        menu.setIsOfflineMenu("true");
        menu.setIsEnabled("true");
        menu.setContextTitle("Logout");
        menu.setContextMenuId("9999");
        menu.setRepositoryId("");
        menu.setLandingPageType("0");
        menu.setCategoryStyle("0");
        menu.setComponentId("0");
        menu.setConditions("0");
        menu.setParentMenuId("0");
        menu.setParameterStrings("");
        menu.setIsSubMenuExists(0);
        menuList.add(menu);

        return menuList;
    }

    public void deleteRecordsinTable(String siteid, String siteUrl, String TABLENAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String strDelete = "DELETE FROM " + TABLENAME + " WHERE  siteID ='"
                    + siteid + "' AND siteURL='" + siteUrl + "'";
            db.execSQL(strDelete);

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }


    }

    public void ejectRecordsinTable(String TABLENAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String strDelete = "DELETE FROM " + TABLENAME;
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }


    }

    public void ejectRecordsinTracklistTable(String siteID, String trackscoID, String userID, Boolean isTrackList) {
        SQLiteDatabase db = this.getWritableDatabase();

        String TBL_NAME;
        if (!isTrackList) {
            TBL_NAME = TBL_RELATEDCONTENTDATA;
        } else {
            TBL_NAME = TBL_TRACKLISTDATA;

        }

        try {
            String strDelete = "DELETE FROM " + TBL_NAME + " WHERE siteid= '" + siteID +
                    "' AND trackscoid= '" + trackscoID +
                    "' AND userid= '" + userID + "'";
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }


    }

    public void ejectRecordsinTrackObjDb(MyLearningModel learningModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_TRACKOBJECTS + " WHERE siteid= '" + learningModel.getSiteID() +
                    "' AND trackscoid= '" + learningModel.getTrackScoid() +
                    "' AND userid= '" + learningModel.getUserID() + "'";
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }


    }
    // Methods for inserting naive menus to local db

    public void insertIntoNativeMenusTable(NativeMenuModel nativeMenuModel, String siteid, String siteUrl) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strExeQuery = "";
            strExeQuery = "INSERT INTO NATIVEMENUS (menuid , displayname , displayorder, image, isofflinemenu , isenabled, contexttitle , contextmenuid , repositoryid, landingpagetype, categorystyle, componentid, conditions, parentmenuid, parameterstrings, siteid, siteurl)"
                    + " VALUES ('"
                    + nativeMenuModel.getMenuid()
                    + "','"
                    + nativeMenuModel.getDisplayname()
                    + "','"
                    + nativeMenuModel.getDisplayOrder()
                    + "','"
                    + nativeMenuModel.getImage()
                    + "','"
                    + nativeMenuModel.getIsofflineMenu()
                    + "','"
                    + nativeMenuModel.getIsEnabled()
                    + "','"
                    + nativeMenuModel.getContextTitle()
                    + "','"
                    + nativeMenuModel.getContextmenuId()
                    + "','"
                    + nativeMenuModel.getRepositoryId()
                    + "','"
                    + nativeMenuModel.getLandingpageType()
                    + "','"
                    + nativeMenuModel.getCategoryStyle()
                    + "','"
                    + nativeMenuModel.getComponentId()
                    + "','"
                    + nativeMenuModel.getConditions()
                    + "','"
                    + nativeMenuModel.getParentMenuId()
                    + "','"
                    + nativeMenuModel.getParameterString()
                    + "','"
                    + siteid
                    + "','"
                    + siteUrl
                    + "')";
            db.execSQL(strExeQuery);
        } catch (SQLiteException sqlEx) {
            sqlEx.printStackTrace();

        }
        db.close();
    }


    // Method for tincan details

    public void getSiteTinCanDetails(String webAPiUrl, String siteUrl) {
        String paramsString = "SiteURL=" + siteUrl;
        InputStream inputStream = wap.callWebAPIMethod(webAPiUrl, "MobileLMS",
                "MobileTinCanConfigurations", appController.getAuthentication(), paramsString);

        if (inputStream != null) {

            String result = Utilities.convertStreamToString(inputStream);

            result = result.replaceAll("\"", "");

            String replaceStr = result.replaceAll("\'", "\"");
            String siteID = preferencesManager.getStringValue(StaticValues.KEY_SITEID);
            ;
            try {
                JSONObject jsonObj = new JSONObject(replaceStr);
                Log.d(TAG, "getSiteTinCanDetails: " + jsonObj);
                injectTinCanConfigurationValues(jsonObj, siteID);
            } catch (JSONException e) {

                e.printStackTrace();

            }
        }

    }

    // Method for branding screen images

    public void downloadSplashImages(String siteUrl) {
        String userID = preferencesManager.getStringValue(StaticValues.KEY_USERID);

        if (userID != null && !userID.equalsIgnoreCase("")) {


        } else {
            try {

                String downloadpath = siteUrl + "content/SiteConfiguration/"
                        + "374/SplashImages.zip";

                String splashImagesPath = dbctx.getExternalFilesDir(null)
                        + "/Mydownloads/SplashImages" + "";

                File f = new File(splashImagesPath);
                deleteFilesRecursively(f);

                boolean success = (new File(splashImagesPath)).mkdirs();
                if (success) {
                    Log.d("Directories: ", splashImagesPath + " created");
                }

                URL u = new URL(downloadpath);
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();

                InputStream input = u.openStream();
                OutputStream output1 = null;
                try {
                    output1 = new FileOutputStream(splashImagesPath
                            + "/SplashImages.zip");

                    byte[] buffer = new byte[contentLength];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output1.write(buffer, 0, bytesRead);
                    }

                    UnZip d = new UnZip(splashImagesPath + "/SplashImages.zip",
                            splashImagesPath);
                    File zipfile = new File(splashImagesPath + "/SplashImages.zip");
                    zipfile.delete();
                } catch (IOException e) {
                    Log.e("In DownloadSplashImages",
                            "Error establishing connection" + e.getMessage());
                } finally {
                    input.close();
                    output1.close();
                }

            } catch (IOException e) {
                Log.e("In DownloadSplashImages", "Error establishing connection"
                        + e.getMessage());
            }
        }

    }

    private void deleteFilesRecursively(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles())
                deleteFilesRecursively(child);
        }
        fileOrDirectory.delete();
    }

//////////////////////////  DB INSERTIONS FOR MYLEARNING


    public void injectMyLearningData(JSONObject jsonObject) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        JSONArray jsonTableAry = jsonObject.getJSONArray("table2");
        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_DOWNLOADDATA);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel myLearningModel = new MyLearningModel();
            ContentValues contentValues = null;


            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("siteid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("siteid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }

            // shortdes
            if (jsonMyLearningColumnObj.has("shortdescription")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                myLearningModel.setShortDes(result.toString());

            }

            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (authorName.length() != 0) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }


            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }
            // displayName

            myLearningModel.setDisplayName(appUserModel.getDisplayName());
            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


                if (isValidString(imageurl)) {

                    myLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                    myLearningModel.setImageData(imagePathSet);


                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            myLearningModel.setImageData(imagePathSet);

                        }
                    }


                }
//                // imagedata
//                if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {
//
//
//                } else {
//
//                }
                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

                    myLearningModel.setShortDes(result.toString());
                    myLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("typeofevent")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                    myLearningModel.setTypeofevent(typeoFEvent);

                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (myLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (myLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    myLearningModel.setMediaName(medianame);

                }       // ratingid
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

                }
                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                }

                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // dateassigned
                if (jsonMyLearningColumnObj.has("dateassigned")) {

                    myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());

                }
                // keywords
                if (jsonMyLearningColumnObj.has("seokeywords")) {

                    myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                myLearningModel.setEventAddedToCalender(false);

//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                String checkfalseOrTrue = jsonMyLearningColumnObj.get("startdate").toString();
//                if (checkfalseOrTrue.equalsIgnoreCase("false")) {
//                    myLearningModel.setEventAddedToCalender(false);
//                } else {
//                    myLearningModel.setEventAddedToCalender(true);
//                }
//            }
                // isExpiry
                myLearningModel.setIsExpiry("false");
//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                myLearningModel.setIsExpiry(jsonMyLearningColumnObj.get("startdate").toString());
//
//            }
                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // password

                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
                            + "/Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    myLearningModel.setOfflinepath(finalDownloadedFilePath);
                }
//


                // wresult
                if (jsonMyLearningColumnObj.has("wresult")) {

                    myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

                }
                // wmessage
                if (jsonMyLearningColumnObj.has("wmessage")) {

                    myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presenter")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("progress")) {

                    myLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
                    if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                        ejectRecordsinCmi(myLearningModel);
                    }
                }

//            injectIntoRowWise(myLearningModel);
                injectMyLearningIntoTable(myLearningModel, false);
            }

            db.close();
        }

    }

    public void injectMyLearningIntoTable(MyLearningModel myLearningModel, boolean subscibed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("username", myLearningModel.getUserName());
            contentValues.put("siteid", myLearningModel.getSiteID());
            contentValues.put("userid", myLearningModel.getUserID());
            contentValues.put("scoid", myLearningModel.getScoId());
            contentValues.put("siteurl", myLearningModel.getSiteURL());
            contentValues.put("sitename", myLearningModel.getSiteName());
            contentValues.put("contentid", myLearningModel.getContentID());
            contentValues.put("objectid", myLearningModel.getObjectId());
            contentValues.put("coursename", myLearningModel.getCourseName());
            contentValues.put("author", myLearningModel.getAuthor());
            contentValues.put("shortdes", myLearningModel.getShortDes());
            contentValues.put("longdes", myLearningModel.getLongDes());
            contentValues.put("imagedata", myLearningModel.getImageData());
            contentValues.put("medianame", myLearningModel.getMediaName());
            contentValues.put("createddate", myLearningModel.getCreatedDate());
            contentValues.put("startpage", myLearningModel.getStartPage());
            contentValues.put("eventstarttime", myLearningModel.getEventstartTime());
            contentValues.put("eventendtime", myLearningModel.getEventendTime());
            contentValues.put("objecttypeid", myLearningModel.getObjecttypeId());
            contentValues.put("locationname", myLearningModel.getLocationName());
            contentValues.put("timezone", myLearningModel.getTimeZone());
            contentValues.put("participanturl", myLearningModel.getParticipantUrl());
//            contentValues.put("courselaunchpath", myLearningModel.getCourseName());
            contentValues.put("status", myLearningModel.getStatus());
//            contentValues.put("eventid", myLearningModel.getEventContentid());
            contentValues.put("islistview", myLearningModel.getIsListView());
            contentValues.put("password", myLearningModel.getPassword());
            contentValues.put("displayname", myLearningModel.getDisplayName());
            contentValues.put("isdownloaded", myLearningModel.getIsDownloaded());
            contentValues.put("courseattempts", myLearningModel.getCourseAttempts());
            contentValues.put("eventcontentid", "false");
            contentValues.put("relatedcontentcount", myLearningModel.getRelatedContentCount());
            contentValues.put("durationenddate", myLearningModel.getDurationEndDate());
            contentValues.put("ratingid", myLearningModel.getRatingId());
            contentValues.put("publisheddate", myLearningModel.getPublishedDate());
            contentValues.put("isExpiry", myLearningModel.getIsExpiry());
            contentValues.put("mediatypeid", myLearningModel.getMediatypeId());
            contentValues.put("dateassigned", myLearningModel.getDateAssigned());
            contentValues.put("keywords", myLearningModel.getKeywords());
            contentValues.put("downloadurl", myLearningModel.getDownloadURL());
            contentValues.put("offlinepath", myLearningModel.getOfflinepath());
            contentValues.put("presenter", myLearningModel.getPresenter());
            contentValues.put("eventaddedtocalender", myLearningModel.getEventAddedToCalender());
            contentValues.put("joinurl", myLearningModel.getJoinurl());
            contentValues.put("typeofevent", myLearningModel.getTypeofevent());
            contentValues.put("progress", myLearningModel.getProgress());

            if (subscibed) {

                db.delete(TBL_DOWNLOADDATA, "siteid='" + myLearningModel.getSiteID()
                        + "' AND userid='" + myLearningModel.getUserID() + "' AND scoid='"
                        + myLearningModel.getScoId() + "'", null);


//                db.delete(TBL_STUDENTRESPONSES, "siteid='" + myLearningModel.getSiteID()
//                        + "' AND userid='" + myLearningModel.getUserID() + "' AND scoid='"
//                        + myLearningModel.getScoId() + "'", null);

            }


            db.insert(TBL_DOWNLOADDATA, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }


    }

    public List<MyLearningModel> fetchMylearningModel() {
        List<MyLearningModel> myLearningModelList = null;
        MyLearningModel myLearningModel = new MyLearningModel();
        SQLiteDatabase db = this.getWritableDatabase();
//        String strSelQuery = "SELECT * FROM " + TBL_DOWNLOADDATA;
////                + " ORDER BY ";

        String strSelQuery = "SELECT DISTINCT D.*, CASE WHEN C.status is NOT NULL OR NOT C.status = '' then C.status ELSE D.status END as objStatus FROM " + TBL_DOWNLOADDATA + " D LEFT OUTER JOIN " + TBL_CMI + " C ON D.userid = C.userid AND D.scoid = C.scoid AND D.siteid = C.siteid ORDER BY D.dateassigned DESC";

        Log.d(TAG, "fetchMylearningModel: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                myLearningModelList = new ArrayList<MyLearningModel>();
                do {
                    myLearningModel = new MyLearningModel();
                    myLearningModel.setUserID(cursor.getString(cursor
                            .getColumnIndex("userid")));
                    myLearningModel.setUserName(cursor.getString(cursor
                            .getColumnIndex("username")));
                    myLearningModel.setSiteID(cursor.getString(cursor
                            .getColumnIndex("siteid")));
                    myLearningModel.setSiteURL(cursor.getString(cursor
                            .getColumnIndex("siteurl")));
                    myLearningModel.setSiteName(cursor.getString(cursor
                            .getColumnIndex("sitename")));
                    myLearningModel.setContentID(cursor.getString(cursor
                            .getColumnIndex("contentid")));
                    myLearningModel.setObjectId(cursor.getString(cursor
                            .getColumnIndex("objectid")));
                    myLearningModel.setCourseName(cursor.getString(cursor
                            .getColumnIndex("coursename")));
                    myLearningModel.setAuthor(cursor.getString(cursor
                            .getColumnIndex("author")));
                    myLearningModel.setShortDes(cursor.getString(cursor
                            .getColumnIndex("shortdes")));
                    myLearningModel.setLongDes(cursor.getString(cursor
                            .getColumnIndex("longdes")));
                    myLearningModel.setImageData(cursor.getString(cursor
                            .getColumnIndex("imagedata")));
                    myLearningModel.setMediaName(cursor.getString(cursor
                            .getColumnIndex("medianame")));
                    myLearningModel.setCreatedDate(cursor.getString(cursor
                            .getColumnIndex("createddate")));
                    myLearningModel.setStartPage(cursor.getString(cursor
                            .getColumnIndex("startpage")));
                    myLearningModel.setEventstartTime(cursor.getString(cursor
                            .getColumnIndex("eventstarttime")));
                    myLearningModel.setEventendTime(cursor.getString(cursor
                            .getColumnIndex("eventendtime")));
                    myLearningModel.setObjecttypeId(cursor.getString(cursor
                            .getColumnIndex("objecttypeid")));
                    myLearningModel.setLocationName(cursor.getString(cursor
                            .getColumnIndex("locationname")));
                    myLearningModel.setScoId(cursor.getString(cursor
                            .getColumnIndex("scoid")));
                    myLearningModel.setParticipantUrl(cursor.getString(cursor
                            .getColumnIndex("participanturl")));
                    myLearningModel.setStatus(cursor.getString(cursor
                            .getColumnIndex("objStatus")));
                    myLearningModel.setPassword(cursor.getString(cursor
                            .getColumnIndex("password")));
                    myLearningModel.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("displayname")));
                    myLearningModel.setIsListView(cursor.getString(cursor
                            .getColumnIndex("islistview")));
                    myLearningModel.setIsDownloaded(cursor.getString(cursor
                            .getColumnIndex("isdownloaded")));
                    myLearningModel.setCourseAttempts(cursor.getString(cursor
                            .getColumnIndex("courseattempts")));
                    myLearningModel.setEventContentid(cursor.getString(cursor
                            .getColumnIndex("eventcontentid")));
                    myLearningModel.setRelatedContentCount(cursor.getString(cursor
                            .getColumnIndex("relatedcontentcount")));
                    myLearningModel.setDurationEndDate(cursor.getString(cursor
                            .getColumnIndex("durationenddate")));
                    myLearningModel.setRatingId(cursor.getString(cursor
                            .getColumnIndex("ratingid")));
                    myLearningModel.setPublishedDate(cursor.getString(cursor
                            .getColumnIndex("publisheddate")));
                    myLearningModel.setIsExpiry(cursor.getString(cursor
                            .getColumnIndex("isExpiry")));
                    myLearningModel.setMediatypeId(cursor.getString(cursor
                            .getColumnIndex("mediatypeid")));
                    myLearningModel.setDateAssigned(cursor.getString(cursor
                            .getColumnIndex("dateassigned")));
                    myLearningModel.setKeywords(cursor.getString(cursor
                            .getColumnIndex("keywords")));
                    myLearningModel.setDownloadURL(cursor.getString(cursor
                            .getColumnIndex("downloadurl")));
                    myLearningModel.setOfflinepath(cursor.getString(cursor
                            .getColumnIndex("offlinepath")));
                    myLearningModel.setPresenter(cursor.getString(cursor
                            .getColumnIndex("presenter")));
                    myLearningModel.setEventAddedToCalender(false);
//                    myLearningModel.setEventAddedToCalender(cursor.getb(cursor
//                            .getColumnIndex("eventaddedtocalender")));
                    myLearningModel.setJoinurl(cursor.getString(cursor
                            .getColumnIndex("joinurl")));
                    myLearningModel.setTypeofevent(cursor.getInt(cursor
                            .getColumnIndex("typeofevent")));
                    myLearningModel.setProgress(cursor.getString(cursor
                            .getColumnIndex("progress")));

                    myLearningModelList.add(myLearningModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchmylearningfrom db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return myLearningModelList;
    }


    public void injectCatalogData(JSONObject jsonObject) throws JSONException {


        JSONArray jsonTableAry = jsonObject.getJSONArray("table2");
        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_CATALOGDATA);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel myLearningModel = new MyLearningModel();


            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("orgunitid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("orgunitid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }

            // shortdes
            if (jsonMyLearningColumnObj.has("shortdescription")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                myLearningModel.setShortDes(result.toString());

            }

            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (authorName.length() != 0) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }


            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }
            // displayNam


            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


                if (isValidString(imageurl)) {

                    myLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                    myLearningModel.setImageData(imagePathSet);


                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            myLearningModel.setImageData(imagePathSet);

                        }
                    }


                }
//                // imagedata
//                if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {
//
//
//                } else {
//
//                }
                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }

                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

                    myLearningModel.setShortDes(result.toString());
                    myLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("typeofevent")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                    myLearningModel.setTypeofevent(typeoFEvent);

                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (myLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (myLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    myLearningModel.setMediaName(medianame);

                }       // ratingid
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

                }
                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                }

                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // dateassigned
                if (jsonMyLearningColumnObj.has("dateassigned")) {

                    myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());

                }
                // keywords
                if (jsonMyLearningColumnObj.has("seokeywords")) {

                    myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                myLearningModel.setEventAddedToCalender(false);


                // isExpiry
                myLearningModel.setIsExpiry("false");

                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // display
                myLearningModel.setDisplayName(appUserModel.getDisplayName());
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // password
                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
                            + "/Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    myLearningModel.setOfflinepath(finalDownloadedFilePath);
                }
//

                // wresult
                if (jsonMyLearningColumnObj.has("wresult")) {

                    myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

                }
                // wmessage
                if (jsonMyLearningColumnObj.has("wmessage")) {

                    myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presenter")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("price")) {

                    myLearningModel.setPrice(jsonMyLearningColumnObj.get("price").toString());

                }

                //googleproductid
                if (jsonMyLearningColumnObj.has("googleproductid")) {

                    myLearningModel.setGoogleProductID(jsonMyLearningColumnObj.get("googleproductid").toString());

                }

                //componentid
                if (jsonMyLearningColumnObj.has("componentid")) {

                    myLearningModel.setComponentId(jsonMyLearningColumnObj.get("componentid").toString());

                }

                //currency
                if (jsonMyLearningColumnObj.has("currency")) {

                    myLearningModel.setCurrency(jsonMyLearningColumnObj.get("currency").toString());

                }

                //viewtype
                if (jsonMyLearningColumnObj.has("viewtype")) {

                    myLearningModel.setViewType(jsonMyLearningColumnObj.get("viewtype").toString());

                }
                //isaddedtomylearning
                if (jsonMyLearningColumnObj.has("isaddedtomylearning")) {

                    myLearningModel.setAddedToMylearning(Integer.parseInt(jsonMyLearningColumnObj.get("isaddedtomylearning").toString()));

                }
                injectCatalogDataIntoTable(myLearningModel);
            }

        }

    }

    public void injectCatalogDataIntoTable(MyLearningModel myLearningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("siteid", myLearningModel.getSiteID());
            contentValues.put("siteurl", myLearningModel.getSiteURL());
            contentValues.put("sitename", myLearningModel.getSiteName());
            contentValues.put("displayname", myLearningModel.getDisplayName());
            contentValues.put("username", myLearningModel.getUserName());
            contentValues.put("password", myLearningModel.getPassword());
            contentValues.put("userid", myLearningModel.getUserID());
            contentValues.put("contentid", myLearningModel.getContentID());
            contentValues.put("coursename", myLearningModel.getCourseName());
            contentValues.put("author", myLearningModel.getAuthor());
            contentValues.put("shortdes", myLearningModel.getShortDes());
            contentValues.put("longdes", myLearningModel.getLongDes());
            contentValues.put("imagedata", myLearningModel.getImageData());
            contentValues.put("medianame", myLearningModel.getMediaName());
            contentValues.put("createddate", myLearningModel.getCreatedDate());
            contentValues.put("startpage", myLearningModel.getStartPage());
            contentValues.put("objecttypeid", myLearningModel.getObjecttypeId());
            contentValues.put("locationname", myLearningModel.getLocationName());
            contentValues.put("timezone", myLearningModel.getTimeZone());
            contentValues.put("scoid", myLearningModel.getScoId());
            contentValues.put("participanturl", myLearningModel.getParticipantUrl());
            contentValues.put("viewtype", myLearningModel.getViewType());
            contentValues.put("price", myLearningModel.getPrice());
            contentValues.put("islistview", myLearningModel.getIsListView());
            contentValues.put("ratingid", myLearningModel.getRatingId());
            contentValues.put("publisheddate", myLearningModel.getPublishedDate());
            contentValues.put("mediatypeid", myLearningModel.getMediatypeId());
            contentValues.put("keywords", myLearningModel.getKeywords());
            contentValues.put("googleproductid", myLearningModel.getGoogleProductID());
            contentValues.put("currency", myLearningModel.getCurrency());
            contentValues.put("itemtype", myLearningModel.getItemType());
            contentValues.put("categorycompid", myLearningModel.getComponentId());
            contentValues.put("downloadurl", myLearningModel.getDownloadURL());
            contentValues.put("offlinepath", myLearningModel.getOfflinepath());
            contentValues.put("isaddedtomylearning", myLearningModel.getAddedToMylearning());

            db.insert(TBL_CATALOGDATA, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }


    public List<MyLearningModel> fetchCatalogModel(String componentID) {
        List<MyLearningModel> myLearningModelList = null;
        MyLearningModel myLearningModel = new MyLearningModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuery = "SELECT DISTINCT * FROM " + TBL_CATALOGDATA + " WHERE categorycompid = " + componentID + "  ORDER BY publisheddate DESC";

        Log.d(TAG, "fetchCatalogModel: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                myLearningModelList = new ArrayList<MyLearningModel>();
                do {
                    myLearningModel = new MyLearningModel();

                    myLearningModel.setSiteID(cursor.getString(cursor
                            .getColumnIndex("siteid")));


                    myLearningModel.setSiteURL(cursor.getString(cursor
                            .getColumnIndex("siteurl")));

                    myLearningModel.setSiteName(cursor.getString(cursor
                            .getColumnIndex("sitename")));

                    myLearningModel.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("displayname")));

                    myLearningModel.setUserName(cursor.getString(cursor
                            .getColumnIndex("username")));

                    myLearningModel.setPassword(cursor.getString(cursor
                            .getColumnIndex("password")));

                    myLearningModel.setUserID(cursor.getString(cursor
                            .getColumnIndex("userid")));

                    myLearningModel.setContentID(cursor.getString(cursor
                            .getColumnIndex("contentid")));

                    myLearningModel.setCourseName(cursor.getString(cursor
                            .getColumnIndex("coursename")));

                    myLearningModel.setAuthor(cursor.getString(cursor
                            .getColumnIndex("author")));

                    myLearningModel.setShortDes(cursor.getString(cursor
                            .getColumnIndex("shortdes")));

                    myLearningModel.setLongDes(cursor.getString(cursor
                            .getColumnIndex("longdes")));

                    myLearningModel.setImageData(cursor.getString(cursor
                            .getColumnIndex("imagedata")));

                    myLearningModel.setMediaName(cursor.getString(cursor
                            .getColumnIndex("medianame")));

                    myLearningModel.setCreatedDate(cursor.getString(cursor
                            .getColumnIndex("createddate")));

                    myLearningModel.setStartPage(cursor.getString(cursor
                            .getColumnIndex("startpage")));

                    myLearningModel.setObjecttypeId(cursor.getString(cursor
                            .getColumnIndex("objecttypeid")));

                    myLearningModel.setLocationName(cursor.getString(cursor
                            .getColumnIndex("locationname")));

                    myLearningModel.setTimeZone(cursor.getString(cursor
                            .getColumnIndex("timezone")));

                    myLearningModel.setScoId(cursor.getString(cursor
                            .getColumnIndex("scoid")));

                    myLearningModel.setParticipantUrl(cursor.getString(cursor
                            .getColumnIndex("participanturl")));

                    myLearningModel.setViewType(cursor.getString(cursor
                            .getColumnIndex("viewtype")));

                    myLearningModel.setIsListView(cursor.getString(cursor
                            .getColumnIndex("islistview")));
                    myLearningModel.setPrice(cursor.getString(cursor
                            .getColumnIndex("price")));

                    myLearningModel.setRatingId(cursor.getString(cursor
                            .getColumnIndex("ratingid")));

                    myLearningModel.setPublishedDate(cursor.getString(cursor
                            .getColumnIndex("publisheddate")));

                    myLearningModel.setMediatypeId(cursor.getString(cursor
                            .getColumnIndex("mediatypeid")));

                    myLearningModel.setKeywords(cursor.getString(cursor
                            .getColumnIndex("keywords")));

                    myLearningModel.setGoogleProductID(cursor.getString(cursor
                            .getColumnIndex("googleproductid")));
                    myLearningModel.setCurrency(cursor.getString(cursor
                            .getColumnIndex("currency")));
                    myLearningModel.setItemType(cursor.getString(cursor
                            .getColumnIndex("itemtype")));
                    myLearningModel.setComponentId(cursor.getString(cursor
                            .getColumnIndex("categorycompid")));

                    myLearningModel.setDownloadURL(cursor.getString(cursor
                            .getColumnIndex("downloadurl")));
                    myLearningModel.setOfflinepath(cursor.getString(cursor
                            .getColumnIndex("offlinepath")));

                    myLearningModel.setAddedToMylearning(cursor.getInt(cursor
                            .getColumnIndex("isaddedtomylearning")));

                    myLearningModel.setEventAddedToCalender(false);

                    myLearningModelList.add(myLearningModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchmylearningfrom db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return myLearningModelList;
    }

    /*
    *
    *
    *
    * */
    public void updateContentRatingToLocalDB(MyLearningModel myLearningModel, String rating) {
        String strUpdateML = "UPDATE " + TBL_DOWNLOADDATA + " SET ratingid='"
                + rating + "' WHERE siteid ='" + myLearningModel.getSiteID() + "' AND userid ='"
                + myLearningModel.getUserID() + "' AND scoid='" + myLearningModel.getScoId() + "'";
        String strUpdateCL = "UPDATE " + TBL_CATALOGDATA + " SET ratingid='"
                + rating + "' WHERE siteid ='" + myLearningModel.getSiteID() + "' AND scoid='"
                + myLearningModel.getScoId() + "'";

        try {
            executeQuery(strUpdateML);
            executeQuery(strUpdateCL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeQuery(String strQuery) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(strQuery);
            db.close();

        } catch (Exception e) {
            Log.d("executeQuery", e.getMessage());
        }
    }

    public void injectTracklistData(Boolean isTrackList, JSONObject jsonObject, MyLearningModel parentModel) throws JSONException {

        JSONArray jsonTableBlocks = null;
        JSONArray jsonTableAry = null;

        if (isTrackList) {
            jsonTableAry = jsonObject.getJSONArray("table5");
            jsonTableBlocks = jsonObject.getJSONArray("table6");
            ejectRecordsinTracklistTable(parentModel.getSiteID(), parentModel.getScoId(), parentModel.getUserID(), true);

        } else {
            jsonTableAry = jsonObject.getJSONArray("table7");
            jsonTableBlocks = jsonObject.getJSONArray("table6");
            ejectRecordsinTracklistTable(parentModel.getSiteID(), parentModel.getScoId(), parentModel.getUserID(), false);
        }
        // for deleting records in table for respective table

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel trackLearningModel = new MyLearningModel();


            // trackscoid
            trackLearningModel.setTrackScoid(parentModel.getScoId());

            // userid
            trackLearningModel.setUserID(parentModel.getUserID());

            // username
            trackLearningModel.setUserName(parentModel.getUserName());

            // password
            trackLearningModel.setPassword(parentModel.getPassword());

            //sitename

            trackLearningModel.setSiteName(parentModel.getSiteName());
            // siteurl

            trackLearningModel.setSiteURL(parentModel.getSiteURL());

            // siteid

            trackLearningModel.setSiteID(parentModel.getSiteID());

            // parentid
            if (jsonMyLearningColumnObj.has("parentid")) {

                trackLearningModel.setParentID(jsonMyLearningColumnObj.get("parentid").toString());

            }

            //showstatus
            if (jsonMyLearningColumnObj.has("showstatus")) {

                trackLearningModel.setShowStatus(jsonMyLearningColumnObj.get("showstatus").toString());
            } else {

                trackLearningModel.setShowStatus("show");

            }
            //timedelay
            if (jsonMyLearningColumnObj.has("timedelay")) {

                trackLearningModel.setShowStatus(jsonMyLearningColumnObj.get("timedelay").toString());
            }
            //isdiscussion
            if (jsonMyLearningColumnObj.has("isdiscussion")) {

                trackLearningModel.setIsDiscussion(jsonMyLearningColumnObj.get("isdiscussion").toString());
            }

            //eventcontendid
            if (jsonMyLearningColumnObj.has("eventcontentid")) {

                trackLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());
            }

            //eventid
            if (jsonMyLearningColumnObj.has("eventid")) {

                trackLearningModel.setEventID(jsonMyLearningColumnObj.get("eventid").toString());
            }

            //sequencenumber
            if (jsonMyLearningColumnObj.has("sequencenumber")) {

                Integer parseInteger = Integer.parseInt(jsonMyLearningColumnObj.get("sequencenumber").toString());
                trackLearningModel.setSequenceNumber(parseInteger);
            }
            // mediatypeid
            if (jsonMyLearningColumnObj.has("mediatypeid")) {

                trackLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());
            }
            // relatedcontentcount
            if (jsonMyLearningColumnObj.has("relatedcontentcount")) {
                trackLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedcontentcount").toString());
            }

//            //sitename
//            if (jsonMyLearningColumnObj.has("sitename")) {
//
//                trackLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
//            }
//            // siteurl
//            if (jsonMyLearningColumnObj.has("siteurl")) {
//
//                trackLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());
//
//            }
//            // siteid
//            if (jsonMyLearningColumnObj.has("corelessonstatus")) {
//
//                trackLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());
//
//            }

            // coursename
            if (jsonMyLearningColumnObj.has("name")) {

                trackLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }
            // shortdes
            if (jsonMyLearningColumnObj.has("shortdescription")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                trackLearningModel.setShortDes(result.toString());

            }


            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (authorName.length() != 0) {
                trackLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    authorName = jsonMyLearningColumnObj.getString("author");

                    String authorReplace = authorName.replaceAll(" ", "");

                    trackLearningModel.setAuthor(authorReplace);

                }
            }
//
//
//            // author
//            if (jsonMyLearningColumnObj.has("author")) {
//
//                trackLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());
//
//            }
            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                trackLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                trackLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }
            // displayName
            trackLearningModel.setDisplayName(appUserModel.getDisplayName());
            // objectID
//            if (jsonMyLearningColumnObj.has("objectid")) {
//
//                trackLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());
//
//            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");

                if (isValidString(imageurl)) {

                    trackLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = trackLearningModel.getSiteURL() + "/content/sitefiles/Images/" + trackLearningModel.getContentID() + "/" + imageurl;
                    trackLearningModel.setImageData(imagePathSet);

                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = trackLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            trackLearningModel.setImageData(imagePathSet);

                        }
                    }

                }

                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
                            + "/Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    trackLearningModel.setOfflinepath(finalDownloadedFilePath);
                }

                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    trackLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    trackLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    trackLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    trackLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    trackLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    trackLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    trackLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }

                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

                    trackLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("typeofevent")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                    trackLearningModel.setTypeofevent(typeoFEvent);
                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!trackLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (trackLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (trackLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    trackLearningModel.setMediaName(medianame);

                }

                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    trackLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    trackLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                }

                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    trackLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    trackLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                trackLearningModel.setEventAddedToCalender(false);

//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                String checkfalseOrTrue = jsonMyLearningColumnObj.get("startdate").toString();
//                if (checkfalseOrTrue.equalsIgnoreCase("false")) {
//                    myLearningModel.setEventAddedToCalender(false);
//                } else {
//                    myLearningModel.setEventAddedToCalender(true);
//                }
//            }
                // isExpiry
//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                myLearningModel.setIsExpiry(jsonMyLearningColumnObj.get("startdate").toString());
//
//            }
                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    trackLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    trackLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    trackLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    trackLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    trackLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presenter")) {

                    trackLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("progress")) {

                    trackLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
                }

                for (int j = 0; j < jsonTableBlocks.length(); j++) {
                    JSONObject jsonBlockColumn = jsonTableBlocks.getJSONObject(j);
                    //blockname
                    if (jsonBlockColumn.has("blockname")) {
                        if (jsonBlockColumn.get("blockid").toString().equalsIgnoreCase(jsonMyLearningColumnObj.get("parentid").toString()))
                            trackLearningModel.setBlockName(jsonBlockColumn.get("blockname").toString());

                    } else {
                        trackLearningModel.setBlockName("");
                    }
                }

                if (isTrackList) {
                    injectIntoTrackTable(trackLearningModel);
                } else {
                    injectIntoEventRelatedContentTable(trackLearningModel);
                }
            }
        }
    }

    public void insertTrackObjects(JSONObject jsonResponse, MyLearningModel myLearningModel) throws JSONException {
        JSONArray jsonTrackObjects = null;
        JSONArray jsonTrackList = null;
        jsonTrackObjects = jsonResponse.getJSONArray("table3");
        jsonTrackList = jsonResponse.getJSONArray("table5");

        if (jsonTrackObjects.length() > 0) {
            ejectRecordsinTrackObjDb(myLearningModel);

            TrackObjectsModel trackObjectsModel = new TrackObjectsModel();
            for (int i = 0; i < jsonTrackObjects.length(); i++) {
                JSONObject jsonTrackObj = jsonTrackObjects.getJSONObject(i);
                Log.d(TAG, "insertTrackObjects: " + jsonTrackObj);

                if (jsonTrackObj.has("name")) {

                    trackObjectsModel.setName(jsonTrackObj.getString("name"));
                }
                if (jsonTrackObj.has("objecttypeid")) {

                    trackObjectsModel.setObjTypeId(jsonTrackObj.getString("objecttypeid"));
                }
                if (jsonTrackObj.has("scoid")) {

                    trackObjectsModel.setScoId(jsonTrackObj.getString("scoid"));
                }

                if (jsonTrackObj.has("sequencenumber")) {

                    trackObjectsModel.setSequenceNumber(jsonTrackObj.getString("sequencenumber"));
                }

                if (jsonTrackObj.has("trackscoid")) {

                    trackObjectsModel.setTrackSoId(jsonTrackObj.getString("trackscoid"));
                }
                trackObjectsModel.setUserID(myLearningModel.getUserID());
                trackObjectsModel.setSiteID(myLearningModel.getSiteID());

                injectIntoTrackObjectsTable(trackObjectsModel);
            }
        }

        if (jsonTrackList.length() > 0) {
            ejectRecordsinTracklistTable(myLearningModel.getSiteID(), myLearningModel.getTrackScoid(), myLearningModel.getUserID(), true);
            for (int i = 0; i < jsonTrackList.length(); i++) {
                JSONObject jsonMyLearningColumnObj = jsonTrackList.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

                MyLearningModel trackLearningModel = new MyLearningModel();
                ContentValues contentValues = null;

                // trackscoid
                trackLearningModel.setTrackScoid(myLearningModel.getScoId());

                // userid
                trackLearningModel.setUserID(myLearningModel.getUserID());

                // username
                trackLearningModel.setUserName(myLearningModel.getUserName());

                // password
                trackLearningModel.setPassword(myLearningModel.getPassword());

                //sitename

                trackLearningModel.setSiteName(myLearningModel.getSiteName());
                // siteurl

                trackLearningModel.setSiteURL(myLearningModel.getSiteURL());

                // siteid

                trackLearningModel.setSiteID(myLearningModel.getSiteID());

                // parentid
                if (jsonMyLearningColumnObj.has("parentid")) {

                    trackLearningModel.setParentID(jsonMyLearningColumnObj.get("parentid").toString());

                }

                //showstatus
                if (jsonMyLearningColumnObj.has("showstatus")) {

                    trackLearningModel.setShowStatus(jsonMyLearningColumnObj.get("showstatus").toString());
                } else {

                    trackLearningModel.setShowStatus("show");

                }
                //timedelay
                if (jsonMyLearningColumnObj.has("timedelay")) {

                    trackLearningModel.setShowStatus(jsonMyLearningColumnObj.get("timedelay").toString());
                }
                //isdiscussion
                if (jsonMyLearningColumnObj.has("isdiscussion")) {

                    trackLearningModel.setIsDiscussion(jsonMyLearningColumnObj.get("isdiscussion").toString());
                }

                //eventcontendid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    trackLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());
                }

                //eventid
                if (jsonMyLearningColumnObj.has("eventid")) {

                    trackLearningModel.setEventID(jsonMyLearningColumnObj.get("eventid").toString());
                }

                //sequencenumber
                if (jsonMyLearningColumnObj.has("sequencenumber")) {

                    Integer parseInteger = Integer.parseInt(jsonMyLearningColumnObj.get("sequencenumber").toString());
                    trackLearningModel.setSequenceNumber(parseInteger);
                }
                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    trackLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());
                }
                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedcontentcount")) {
                    trackLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedcontentcount").toString());
                }

//            //sitename
//            if (jsonMyLearningColumnObj.has("sitename")) {
//
//                trackLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
//            }
//            // siteurl
//            if (jsonMyLearningColumnObj.has("siteurl")) {
//
//                trackLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());
//
//            }
//            // siteid
//            if (jsonMyLearningColumnObj.has("corelessonstatus")) {
//
//                trackLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());
//
//            }

                // coursename
                if (jsonMyLearningColumnObj.has("name")) {

                    trackLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

                }
                // shortdes
                if (jsonMyLearningColumnObj.has("shortdescription")) {


                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                    trackLearningModel.setShortDes(result.toString());

                }
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    trackLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
                // contentID
                if (jsonMyLearningColumnObj.has("contentid")) {

                    trackLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

                }
                // createddate
                if (jsonMyLearningColumnObj.has("createddate")) {

                    trackLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

                }
                // displayName
                trackLearningModel.setDisplayName(appUserModel.getDisplayName());
                // objectID
//            if (jsonMyLearningColumnObj.has("objectid")) {
//
//                trackLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());
//
//            }
                // thumbnailimagepath
                if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                    String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");

                    if (isValidString(imageurl)) {

                        trackLearningModel.setThumbnailImagePath(imageurl);
                        String imagePathSet = trackLearningModel.getSiteURL() + "/content/sitefiles/Images/" + trackLearningModel.getContentID() + "/" + imageurl;
                        trackLearningModel.setImageData(imagePathSet);

                    } else {
                        if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                            String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                            if (isValidString(imageurlContentType)) {
                                String imagePathSet = trackLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                                trackLearningModel.setImageData(imagePathSet);
                            }
                        }
                    }

                    if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                        String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                        String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                        String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                        String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
                                + "/Mydownloads/Contentdownloads" + "/" + contentid;

                        String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                        trackLearningModel.setOfflinepath(finalDownloadedFilePath);
                    }

                    // relatedcontentcount
                    if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                        trackLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());
                    }
                    // isDownloaded
                    if (jsonMyLearningColumnObj.has("isdownloaded")) {

                        trackLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                    }
                    // courseattempts
                    if (jsonMyLearningColumnObj.has("courseattempts")) {

                        trackLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                    }
                    // objecttypeid
                    if (jsonMyLearningColumnObj.has("objecttypeid")) {

                        trackLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                    }
                    // scoid
                    if (jsonMyLearningColumnObj.has("scoid")) {

                        trackLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                    }
                    // startpage
                    if (jsonMyLearningColumnObj.has("startpage")) {

                        trackLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                    }
                    // status
                    if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                        trackLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                    }

                    // longdes
                    if (jsonMyLearningColumnObj.has("longdescription")) {

                        Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

                        trackLearningModel.setLongDes(result.toString());

                    }
                    // typeofevent
                    if (jsonMyLearningColumnObj.has("typeofevent")) {

                        int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                        trackLearningModel.setTypeofevent(typeoFEvent);
                    }

                    // medianame
                    if (jsonMyLearningColumnObj.has("medianame")) {
                        String medianame = "";

                        if (!trackLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                            if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                                medianame = "Assessment(Test)";

                            } else {
                                medianame = jsonMyLearningColumnObj.get("medianame").toString();
                            }
                        } else {
                            if (trackLearningModel.getTypeofevent() == 2) {
                                medianame = "Event (Online)";


                            } else if (trackLearningModel.getTypeofevent() == 1) {
                                medianame = "Event (Face to Face)";

                            }
                        }

                        trackLearningModel.setMediaName(medianame);

                    }

                    // eventstarttime
                    if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                        trackLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                    }
                    // eventendtime
                    if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                        trackLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                    }

                    // mediatypeid
                    if (jsonMyLearningColumnObj.has("mediatypeid")) {

                        trackLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                    }
                    // eventcontentid
                    if (jsonMyLearningColumnObj.has("eventcontentid")) {

                        trackLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                    }
                    // eventAddedToCalender
                    trackLearningModel.setEventAddedToCalender(false);

//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                String checkfalseOrTrue = jsonMyLearningColumnObj.get("startdate").toString();
//                if (checkfalseOrTrue.equalsIgnoreCase("false")) {
//                    myLearningModel.setEventAddedToCalender(false);
//                } else {
//                    myLearningModel.setEventAddedToCalender(true);
//                }
//            }
                    // isExpiry
//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                myLearningModel.setIsExpiry(jsonMyLearningColumnObj.get("startdate").toString());
//
//            }
                    // locationname
                    if (jsonMyLearningColumnObj.has("locationname")) {

                        trackLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                    }
                    // timezone
                    if (jsonMyLearningColumnObj.has("timezone")) {

                        trackLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                    }
                    // participanturl
                    if (jsonMyLearningColumnObj.has("participanturl")) {

                        trackLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                    }

                    // isListView
                    if (jsonMyLearningColumnObj.has("bit5")) {

                        trackLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                    }

                    // joinurl
                    if (jsonMyLearningColumnObj.has("joinurl")) {

                        trackLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                    }

                    // presenter
                    if (jsonMyLearningColumnObj.has("presenter")) {

                        trackLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                    }

                    //sitename
                    if (jsonMyLearningColumnObj.has("progress")) {

                        trackLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
                    }


                    trackLearningModel.setBlockName("");

                }

                injectIntoTrackTable(trackLearningModel);
            }

        }


    }


    public void injectIntoTrackObjectsTable(TrackObjectsModel trackObjModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {
            contentValues = new ContentValues();
            contentValues.put("name", trackObjModel.getName());
            contentValues.put("siteid", trackObjModel.getSiteID());
            contentValues.put("userid", trackObjModel.getUserID());
            contentValues.put("scoid", trackObjModel.getScoId());
            contentValues.put("sequencenumber", trackObjModel.getSequenceNumber());
            contentValues.put("objecttypeid", trackObjModel.getObjTypeId());
            contentValues.put("trackscoid", trackObjModel.getTrackSoId());

            db.insert(TBL_TRACKOBJECTS, null, contentValues);

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }


    public void injectIntoTrackTable(MyLearningModel trackListModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("username", trackListModel.getUserName());
            contentValues.put("siteid", trackListModel.getSiteID());
            contentValues.put("userid", trackListModel.getUserID());
            contentValues.put("scoid", trackListModel.getScoId());
            contentValues.put("siteurl", trackListModel.getSiteURL());
            contentValues.put("sitename", trackListModel.getSiteName());
            contentValues.put("contentid", trackListModel.getContentID());
            contentValues.put("coursename", trackListModel.getCourseName());
            contentValues.put("author", trackListModel.getAuthor());
            contentValues.put("shortdes", trackListModel.getShortDes());
            contentValues.put("longdes", trackListModel.getLongDes());
            contentValues.put("imagedata", trackListModel.getImageData());
            contentValues.put("medianame", trackListModel.getMediaName());
            contentValues.put("createddate", trackListModel.getCreatedDate());
            contentValues.put("startpage", trackListModel.getStartPage());
            contentValues.put("eventstarttime", trackListModel.getEventstartTime());
            contentValues.put("eventendtime", trackListModel.getEventendTime());
            contentValues.put("objecttypeid", trackListModel.getObjecttypeId());
            contentValues.put("locationname", trackListModel.getLocationName());
            contentValues.put("timezone", trackListModel.getTimeZone());
            contentValues.put("participanturl", trackListModel.getParticipantUrl());
            contentValues.put("trackscoid", trackListModel.getTrackScoid());
            contentValues.put("status", trackListModel.getStatus());
            contentValues.put("eventid", trackListModel.getEventID());
            contentValues.put("password", trackListModel.getPassword());
            contentValues.put("displayname", trackListModel.getDisplayName());
            contentValues.put("isdownloaded", trackListModel.getIsDownloaded());
            contentValues.put("courseattempts", trackListModel.getCourseAttempts());
            contentValues.put("eventcontentid", "false");
            contentValues.put("relatedcontentcount", trackListModel.getRelatedContentCount());
            contentValues.put("mediatypeid", trackListModel.getMediatypeId());
            contentValues.put("downloadurl", trackListModel.getDownloadURL());
            contentValues.put("progress", trackListModel.getProgress());
            contentValues.put("presenter", trackListModel.getPresenter());
            contentValues.put("eventaddedtocalender", trackListModel.getEventAddedToCalender());
            contentValues.put("joinurl", trackListModel.getJoinurl());
            contentValues.put("typeofevent", trackListModel.getTypeofevent());
            contentValues.put("blockname", trackListModel.getBlockName());
            contentValues.put("showstatus", trackListModel.getShowStatus());
            contentValues.put("parentid", trackListModel.getParentID());
            contentValues.put("timedelay", trackListModel.getTimeDelay());
            contentValues.put("isdiscussion", trackListModel.getIsDiscussion());
            contentValues.put("sequencenumber", trackListModel.getSequenceNumber());
            contentValues.put("courseattempts", trackListModel.getCourseAttempts());
            contentValues.put("offlinepath", trackListModel.getOfflinepath());

            db.insert(TBL_TRACKLISTDATA, null, contentValues);

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public List<MyLearningModel> fetchTrackListModel(MyLearningModel parentModel, Boolean isTrackListView) {
        List<MyLearningModel> trackListModelList = null;
        MyLearningModel trackListModel;
        SQLiteDatabase db = this.getWritableDatabase();
//        String strSelQuery = "SELECT * FROM " + TBL_TRACKLISTDATA;

        String TBLNAME;
        if (!isTrackListView) {

            TBLNAME = TBL_RELATEDCONTENTDATA;
        } else {


            TBLNAME = TBL_TRACKLISTDATA;
        }

        String strSelQuery = "SELECT DISTINCT D.*,CASE WHEN C.status IS NOT NULL THEN C.status ELSE D.status END AS objStatus,C.score FROM "
                + TBLNAME
                + " D LEFT OUTER JOIN "
                + TBL_CMI
                + " C ON D.userid=C.userid AND D.scoid =C.scoid WHERE D.trackscoid ='"
                + parentModel.getScoId()
                + "' AND D.userid ='"
                + parentModel.getUserID()
                + "' AND D.siteid ='"
                + parentModel.getSiteID() + "' ORDER BY blockname";

        Log.d(TAG, "fetchTrackListModel strSelQuery : " + strSelQuery);

        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                trackListModelList = new ArrayList<MyLearningModel>();
                do {
                    trackListModel = new MyLearningModel();
                    trackListModel.setUserID(cursor.getString(cursor
                            .getColumnIndex("userid")));
                    trackListModel.setUserName(cursor.getString(cursor
                            .getColumnIndex("username")));
                    trackListModel.setSiteID(cursor.getString(cursor
                            .getColumnIndex("siteid")));
                    trackListModel.setSiteURL(cursor.getString(cursor
                            .getColumnIndex("siteurl")));
                    trackListModel.setSiteName(cursor.getString(cursor
                            .getColumnIndex("sitename")));
                    trackListModel.setContentID(cursor.getString(cursor
                            .getColumnIndex("contentid")));
                    trackListModel.setCourseName(cursor.getString(cursor
                            .getColumnIndex("coursename")));
                    trackListModel.setAuthor(cursor.getString(cursor
                            .getColumnIndex("author")));
                    trackListModel.setShortDes(cursor.getString(cursor
                            .getColumnIndex("shortdes")));
                    trackListModel.setLongDes(cursor.getString(cursor
                            .getColumnIndex("longdes")));
                    trackListModel.setImageData(cursor.getString(cursor
                            .getColumnIndex("imagedata")));
                    trackListModel.setMediaName(cursor.getString(cursor
                            .getColumnIndex("medianame")));
                    trackListModel.setCreatedDate(cursor.getString(cursor
                            .getColumnIndex("createddate")));
                    trackListModel.setStartPage(cursor.getString(cursor
                            .getColumnIndex("startpage")));
                    trackListModel.setEventstartTime(cursor.getString(cursor
                            .getColumnIndex("eventstarttime")));
                    trackListModel.setEventendTime(cursor.getString(cursor
                            .getColumnIndex("eventendtime")));
                    trackListModel.setObjecttypeId(cursor.getString(cursor
                            .getColumnIndex("objecttypeid")));
                    trackListModel.setLocationName(cursor.getString(cursor
                            .getColumnIndex("locationname")));
                    trackListModel.setScoId(cursor.getString(cursor
                            .getColumnIndex("scoid")));
                    trackListModel.setParticipantUrl(cursor.getString(cursor
                            .getColumnIndex("participanturl")));
//                    trackListModel.setStatus(cursor.getString(cursor
//                            .getColumnIndex("objStatus")));

                    String objStatus = cursor.getString(cursor
                            .getColumnIndex("objStatus"));

                    if (objStatus.toLowerCase().contains("passed")) {
                        trackListModel.setStatus("Completed (passed)");
                    } else if (objStatus.toLowerCase().contains("failed")) {
                        trackListModel.setStatus("Completed (failed)");
                    } else {

                        trackListModel.setStatus(objStatus);
                    }


                    trackListModel.setPassword(cursor.getString(cursor
                            .getColumnIndex("password")));
                    trackListModel.setDisplayName(cursor.getString(cursor
                            .getColumnIndex("displayname")));
                    trackListModel.setIsDownloaded(cursor.getString(cursor
                            .getColumnIndex("isdownloaded")));
                    trackListModel.setCourseAttempts(cursor.getString(cursor
                            .getColumnIndex("courseattempts")));
                    trackListModel.setEventContentid(cursor.getString(cursor
                            .getColumnIndex("eventcontentid")));


                    trackListModel.setMediatypeId(cursor.getString(cursor
                            .getColumnIndex("mediatypeid")));
                    trackListModel.setDownloadURL(cursor.getString(cursor
                            .getColumnIndex("downloadurl")));
                    trackListModel.setOfflinepath(cursor.getString(cursor
                            .getColumnIndex("offlinepath")));
                    trackListModel.setPresenter(cursor.getString(cursor
                            .getColumnIndex("presenter")));
                    trackListModel.setEventAddedToCalender(false);
//                    trackListModel.setEventAddedToCalender(cursor.get(cursor
//                            .getColumnIndex("eventaddedtocalender")));
                    trackListModel.setJoinurl(cursor.getString(cursor
                            .getColumnIndex("joinurl")));

                    trackListModel.setBlockName(cursor.getString(cursor
                            .getColumnIndex("blockname")));

                    trackListModel.setTimeZone(cursor.getString(cursor
                            .getColumnIndex("timezone")));

                    trackListModel.setTrackScoid(cursor.getString(cursor
                            .getColumnIndex("trackscoid")));

                    trackListModel.setIsDiscussion(cursor.getString(cursor
                            .getColumnIndex("isdiscussion")));

// for tracklis

                    if (isTrackListView) {

                        trackListModel.setParentID(cursor.getString(cursor
                                .getColumnIndex("parentid")));

                        trackListModel.setShowStatus(cursor.getString(cursor
                                .getColumnIndex("showstatus")));

                        trackListModel.setProgress(cursor.getString(cursor
                                .getColumnIndex("progress")));


                        trackListModel.setTimeDelay(cursor.getString(cursor
                                .getColumnIndex("timedelay")));

                        trackListModel.setSequenceNumber(cursor.getInt(cursor
                                .getColumnIndex("sequencenumber")));

                        trackListModel.setEventID(cursor.getString(cursor
                                .getColumnIndex("eventid")));


                        trackListModel.setTypeofevent(cursor.getInt(cursor
                                .getColumnIndex("typeofevent")));
                        trackListModel.setProgress(cursor.getString(cursor
                                .getColumnIndex("progress")));

                        trackListModel.setRelatedContentCount(cursor.getString(cursor
                                .getColumnIndex("relatedcontentcount")));

                    }

                    trackListModelList.add(trackListModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            if (db.isOpen()) {
                db.close();
                e.printStackTrace();
            }
            Log.d("fetchmylearningfrom db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");
        }

        return trackListModelList;
    }

    public List<String> fetchBlockNames(String scoID, Boolean isTrackListView) {
        List<String> blockNamesList = null;
        String blockName = "default";
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "";
        if (!isTrackListView) {

            strSelQuery = "SELECT DISTINCT blockname FROM " + TBL_RELATEDCONTENTDATA + " WHERE trackscoid= '" + scoID + "'";
        } else {


            strSelQuery = "SELECT DISTINCT blockname FROM " + TBL_TRACKLISTDATA + " WHERE trackscoid= '" + scoID + "'";
        }

        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                blockNamesList = new ArrayList<String>();
                do {

                    blockName = cursor.getString(cursor.getColumnIndex("blockname"));

                    blockNamesList.add(blockName);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchmylearningfrom db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return blockNamesList;
    }


    /**
     * To delete the track list items of respective track content.
     * <p>
     * author Upendra
     */
    public void deteleTackListItems(MyLearningModel trackParentModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strDelete = "DELETE FROM " + TBL_TRACKLISTDATA
                + " WHERE siteid='" + trackParentModel.getSiteID() + "' AND userid='" + trackParentModel.getUserID()
                + "' AND siteurl='" + trackParentModel.getSiteURL() + "' AND trackscoid='"
                + trackParentModel.getTrackScoid() + "'";
        try {
            db.execSQL(strDelete);
        } catch (Exception ex) {
            Log.d("deteleTackListItems", ex.getMessage());
        }

        db.close();
    }


    public void injectIntoEventRelatedContentTable(MyLearningModel trackListModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {
            contentValues = new ContentValues();
            contentValues.put("username", trackListModel.getUserName());
            contentValues.put("siteid", trackListModel.getSiteID());
            contentValues.put("userid", trackListModel.getUserID());
            contentValues.put("scoid", trackListModel.getScoId());
            contentValues.put("siteurl", trackListModel.getSiteURL());
            contentValues.put("sitename", trackListModel.getSiteName());
            contentValues.put("contentid", trackListModel.getContentID());
            contentValues.put("coursename", trackListModel.getCourseName());
            contentValues.put("author", trackListModel.getAuthor());
            contentValues.put("shortdes", trackListModel.getShortDes());
            contentValues.put("longdes", trackListModel.getLongDes());
            contentValues.put("imagedata", trackListModel.getImageData());
            contentValues.put("medianame", trackListModel.getMediaName());
            contentValues.put("createddate", trackListModel.getCreatedDate());
            contentValues.put("startpage", trackListModel.getStartPage());
            contentValues.put("eventstarttime", trackListModel.getEventstartTime());
            contentValues.put("eventendtime", trackListModel.getEventendTime());
            contentValues.put("objecttypeid", trackListModel.getObjecttypeId());
            contentValues.put("locationname", trackListModel.getLocationName());
            contentValues.put("timezone", trackListModel.getTimeZone());
            contentValues.put("participanturl", trackListModel.getParticipantUrl());
            contentValues.put("trackscoid", trackListModel.getTrackScoid());
            contentValues.put("status", trackListModel.getStatus());
            contentValues.put("islistview", trackListModel.getIsListView());
            contentValues.put("password", trackListModel.getPassword());
            contentValues.put("displayname", trackListModel.getDisplayName());
            contentValues.put("isdiscussion", trackListModel.getIsDiscussion());
            contentValues.put("isdownloaded", trackListModel.getIsDownloaded());
            contentValues.put("courseattempts", trackListModel.getCourseAttempts());
            contentValues.put("eventcontentid", "false");
            contentValues.put("mediatypeid", trackListModel.getMediatypeId());
            contentValues.put("downloadurl", trackListModel.getDownloadURL());
            contentValues.put("progress", trackListModel.getProgress());
            contentValues.put("presenter", trackListModel.getPresenter());
            contentValues.put("joinurl", trackListModel.getJoinurl());
            contentValues.put("blockname", trackListModel.getBlockName());
            contentValues.put("wresult", trackListModel.getWresult());
            contentValues.put("wmessage", trackListModel.getWmessage());
            contentValues.put("durationenddate", trackListModel.getDurationEndDate());
            contentValues.put("ratingid", trackListModel.getRatingId());
            contentValues.put("publisheddate", trackListModel.getPublishedDate());
            contentValues.put("dateassigned", trackListModel.getDateAssigned());
            contentValues.put("keywords", trackListModel.getKeywords());
            contentValues.put("offlinepath", trackListModel.getOfflinepath());

            db.insert(TBL_RELATEDCONTENTDATA, null, contentValues);

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public int updateContentStatus(MyLearningModel myLearningModel, String updatedStatus, String progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;

        try {

            String strUpdate = "UPDATE " + TBL_DOWNLOADDATA + " SET status = '"
                    + updatedStatus + "', progress = '" + progress
                    + "' WHERE siteid ='"
                    + myLearningModel.getSiteID() + "'"
                    + " AND " + " scoid=" + "'"
                    + myLearningModel.getScoId() + "'" + " AND "
                    + " userid=" + "'"
                    + myLearningModel.getUserID() + "'";
            Log.d(TAG, "updateContentStatus: " + strUpdate);
            db.execSQL(strUpdate);
            status = 1;
        } catch (Exception e) {
            status = -1;
            Log.e("updateContentStatus", e.toString());
        }
        db.close();

        return status;

    }

    public int updateContentStatusInTrackList(MyLearningModel myLearningModel,
                                              String updatedStatus, String progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;

        try {

            String strUpdate = "UPDATE " + TBL_TRACKLISTDATA + " SET status = '"
                    + updatedStatus + "', progress = '" + progress
                    + "' WHERE siteid ='"
                    + myLearningModel.getSiteID() + "'"
                    + " AND " + " scoid=" + "'"
                    + myLearningModel.getScoId() + "'" + " AND "
                    + " userid=" + "'"
                    + myLearningModel.getUserID() + "'";
            db.execSQL(strUpdate);
            status = 1;
        } catch (Exception e) {
            status = -1;
            Log.e("updateContentStatus", e.toString());
        }

        try {

            if (updatedStatus.toLowerCase().contains("failed")) {
                String strUpdate = "UPDATE " + TBL_CMI + " SET status = 'failed'"
                        + " WHERE siteid ='"
                        + myLearningModel.getSiteID() + "'"
                        + " AND " + " scoid=" + "'"
                        + myLearningModel.getScoId() + "'" + " AND "
                        + " userid=" + "'"
                        + myLearningModel.getUserID() + "'";
                db.execSQL(strUpdate);

            } else if (updatedStatus.toLowerCase().contains("passed")) {
                String strUpdate = "UPDATE " + TBL_CMI + " SET status = 'passed'"
                        + " WHERE siteid ='"
                        + myLearningModel.getSiteID() + "'"
                        + " AND " + " scoid=" + "'"
                        + myLearningModel.getScoId() + "'" + " AND "
                        + " userid=" + "'"
                        + myLearningModel.getUserID() + "'";
                db.execSQL(strUpdate);

            } else if (updatedStatus.toLowerCase().contains("completed")) {
                String strUpdate = "UPDATE " + TBL_CMI + " SET status = 'completed'"
                        + " WHERE siteid ='"
                        + myLearningModel.getSiteID() + "'"
                        + " AND " + " scoid=" + "'"
                        + myLearningModel.getScoId() + "'" + " AND "
                        + " userid=" + "'"
                        + myLearningModel.getUserID() + "'";
                db.execSQL(strUpdate);

            }

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }


        db.close();

        return status;

    }


    public void injectCMIDataInto(JSONObject jsonObjectCMI, MyLearningModel learningModel) throws JSONException {

        JSONArray jsonCMiAry = jsonObjectCMI.getJSONArray("cmi");
        JSONArray jsonStudentAry = jsonObjectCMI.getJSONArray("studentresponse");
        JSONArray jsonLearnerSessionAry = jsonObjectCMI.getJSONArray("learnersession");
        if (jsonCMiAry != null) {
            ejectRecordsinCmi(learningModel);
            insertCMIData(jsonCMiAry, learningModel);

        }
        if (jsonStudentAry != null) {
            ejectRecordsinStudentResponse(learningModel);
            insertStudentResponsData(jsonStudentAry, learningModel);

        }

        if (jsonLearnerSessionAry != null) {
            ejectRecordsinLearnerSession(learningModel);
            insertLearnerSession(jsonLearnerSessionAry, learningModel);
        }
    }

    public void insertCMIData(JSONArray jsonArray, MyLearningModel learningModel) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCMiColumnObj = jsonArray.getJSONObject(i);
            Log.d(TAG, "injectINTCMI: " + jsonCMiColumnObj);

            CMIModel cmiModel = new CMIModel();

            //corelessonstatus
            if (jsonCMiColumnObj.has("corelessonstatus")) {

                cmiModel.set_status(jsonCMiColumnObj.get("corelessonstatus").toString());
            }
//            // statusdisplayname
//            if (jsonCMiColumnObj.has("statusdisplayname")) {
//
//                cmiModel.set_status(jsonCMiColumnObj.get("statusdisplayname").toString());
//
//            }
            // scoid
            if (jsonCMiColumnObj.has("scoid")) {

                int scoID = Integer.parseInt(jsonCMiColumnObj.get("scoid").toString());
                cmiModel.set_scoId(scoID);

            }
            // userid
            if (jsonCMiColumnObj.has("userid")) {
                int userID = Integer.parseInt(jsonCMiColumnObj.get("userid").toString());
                cmiModel.set_userId(userID);

            }
            // corelessonlocation
            if (jsonCMiColumnObj.has("corelessonlocation")) {

                cmiModel.set_location(jsonCMiColumnObj.get("corelessonlocation").toString());

            }

            // author
            if (jsonCMiColumnObj.has("totalsessiontime")) {

                cmiModel.set_timespent(jsonCMiColumnObj.get("totalsessiontime").toString());

            }
            // scoreraw
            if (jsonCMiColumnObj.has("scoreraw")) {

                cmiModel.set_score(jsonCMiColumnObj.get("scoreraw").toString());

            }
            // sequencenumber
            if (jsonCMiColumnObj.has("sequencenumber")) {

                cmiModel.set_seqNum(jsonCMiColumnObj.get("sequencenumber").toString());

            }
            // durationEndDate
            if (jsonCMiColumnObj.has("corelessonmode")) {

                cmiModel.set_coursemode(jsonCMiColumnObj.get("corelessonmode").toString());

            }
            // scoremin
            if (jsonCMiColumnObj.has("scoremin")) {

                cmiModel.set_scoremin(jsonCMiColumnObj.get("scoremin").toString());


            }

            // scoremax
            if (jsonCMiColumnObj.has("scoremax")) {

                cmiModel.set_scoremax(jsonCMiColumnObj.get("scoremax").toString());

            }
//            // startdate
//            if (jsonCMiColumnObj.has("startdate")) {
//
//
//                String s = jsonCMiColumnObj.getString("startdate").toUpperCase();
//                String dateStr = s.substring(0, 19);
//                SimpleDateFormat curFormater = new SimpleDateFormat(
//                        "yyyy-MM-dd'T'HH:mm:ss");
//                java.util.Date dateObj = null;
//                try {
//                    dateObj = curFormater.parse(dateStr);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                SimpleDateFormat postFormater = new SimpleDateFormat(
//                        "yyyy-MM-dd hh:mm:ss");
//                String strCreatedDate1 = postFormater
//                        .format(dateObj);
//
//            }
            cmiModel.set_startdate("");

            // datecompleted
            if (jsonCMiColumnObj.has("datecompleted")) {

                String s = jsonCMiColumnObj.getString("datecompleted").toUpperCase();

                if (!s.equalsIgnoreCase("NULL")) {
                    String dateStr = s.substring(0, 19);
                    SimpleDateFormat curFormater = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss");
                    java.util.Date dateObj = null;
                    try {
                        dateObj = curFormater.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat postFormater = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    String strCreatedDate1 = postFormater
                            .format(dateObj);
                    cmiModel.set_datecompleted(strCreatedDate1);
                } else {
                    cmiModel.set_datecompleted("");
                }

            }
            // objecttypeid
            if (jsonCMiColumnObj.has("suspenddata")) {

                cmiModel.set_suspenddata(jsonCMiColumnObj.get("suspenddata").toString());

            }
            // scoid
            if (jsonCMiColumnObj.has("textresponses")) {

                cmiModel.set_textResponses(jsonCMiColumnObj.get("textresponses").toString());

            }
            cmiModel.set_siteId(learningModel.getSiteID());
            cmiModel.set_sitrurl(learningModel.getSiteURL());
            cmiModel.set_isupdate("true");
            // status
            if (jsonCMiColumnObj.has("noofattempts") && !jsonCMiColumnObj.isNull("noofattempts")) {

                int numberAtmps = Integer.parseInt(jsonCMiColumnObj.get("noofattempts").toString());
                cmiModel.set_noofattempts(numberAtmps);

            }
            injectIntoCMITable(cmiModel, "true");
        }

    }

    public void injectIntoCMITable(CMIModel cmiModel, String isviewd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("siteid", cmiModel.get_siteId());
            contentValues.put("scoid", cmiModel.get_scoId());
            contentValues.put("userid", cmiModel.get_userId());
            contentValues.put("location", cmiModel.get_location());
            contentValues.put("status", cmiModel.get_status());
            contentValues.put("suspenddata", cmiModel.get_suspenddata());
            contentValues.put("isupdate", isviewd);
            contentValues.put("siteurl", cmiModel.get_sitrurl());
            contentValues.put("datecompleted", cmiModel.get_datecompleted());
            contentValues.put("noofattempts", cmiModel.get_noofattempts());
            contentValues.put("score", cmiModel.get_score());
            contentValues.put("sequencenumber", cmiModel.get_score());
            contentValues.put("startdate", cmiModel.get_startdate());
            contentValues.put("timespent", cmiModel.get_timespent());
            contentValues.put("coursemode", cmiModel.get_coursemode());
            contentValues.put("scoremin", cmiModel.get_scoremin());
            contentValues.put("scoremax", cmiModel.get_scoremax());
            contentValues.put("submittime", cmiModel.get_submittime());
            contentValues.put("randomquesseq", cmiModel.get_qusseq());
            contentValues.put("pooledquesseq", cmiModel.get_pooledqusseq());
            contentValues.put("textResponses", cmiModel.get_textResponses());

            db.insert(TBL_CMI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public void insertStudentResponsData(JSONArray jsonArray, MyLearningModel learningModel) throws JSONException {


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCMiColumnObj = jsonArray.getJSONObject(i);
            Log.d(TAG, "injectINTCMI: " + jsonCMiColumnObj);

            StudentResponseModel studentResponseModel = new StudentResponseModel();

            //userid
            if (jsonCMiColumnObj.has("userid")) {

                int userID = Integer.parseInt(jsonCMiColumnObj.get("userid").toString());
                studentResponseModel.set_userId(userID);
            }
            // statusdisplayname
            if (jsonCMiColumnObj.has("studentresponses")) {

                studentResponseModel.set_studentresponses(jsonCMiColumnObj.get("studentresponses").toString());

            }
            // scoid
            if (jsonCMiColumnObj.has("scoid")) {

                int scoID = Integer.parseInt(jsonCMiColumnObj.get("scoid").toString());
                studentResponseModel.set_scoId(scoID);

            }
            // userid
            if (jsonCMiColumnObj.has("result")) {
                studentResponseModel.set_result(jsonCMiColumnObj.get("result").toString());

            }
//            // corelessonlocation
//            if (jsonCMiColumnObj.has("responseid")) {
//
//                studentResponseModel.id(jsonCMiColumnObj.get("responseid").toString());
//
//            }

            // author
            if (jsonCMiColumnObj.has("questionid") && !jsonCMiColumnObj.isNull("questionid")) {

                int questionID = Integer.parseInt(jsonCMiColumnObj.get("questionid").toString());
                studentResponseModel.set_questionid(questionID);

            }
            // scoreraw
            if (jsonCMiColumnObj.has("questionattempt") && !jsonCMiColumnObj.isNull("questionattempt")) {

                int questionattempts = Integer.parseInt(jsonCMiColumnObj.get("questionattempt").toString());
                studentResponseModel.set_questionattempt(questionattempts);

            }
            // sequencenumber
            if (jsonCMiColumnObj.has("optionalnotes")) {

                studentResponseModel.set_optionalNotes(jsonCMiColumnObj.get("optionalnotes").toString());

            }
//            // durationEndDate
//            if (jsonCMiColumnObj.has("isflaged")) {
//
//                studentResponseModel.(jsonCMiColumnObj.get("isflaged").toString());
//
//            }
            // indexs
            if (jsonCMiColumnObj.has("index")) {

                int indexs = Integer.parseInt(jsonCMiColumnObj.get("index").toString());
                studentResponseModel.set_rindex(indexs);

            }

            // scoremax
            if (jsonCMiColumnObj.has("capturedvidid")) {

                studentResponseModel.set_capturedVidId(jsonCMiColumnObj.get("capturedvidid").toString());

            }
            // startdate
            if (jsonCMiColumnObj.has("capturedvidfilename")) {

                studentResponseModel.set_capturedVidFileName(jsonCMiColumnObj.get("capturedvidfilename").toString());

            }
            // datecompleted
            if (jsonCMiColumnObj.has("capturedimgid")) {

                studentResponseModel.set_capturedImgId(jsonCMiColumnObj.get("capturedimgid").toString());

            }
            // objecttypeid
            if (jsonCMiColumnObj.has("capturedimgfilename")) {

                studentResponseModel.set_capturedImgFileName(jsonCMiColumnObj.get("capturedimgfilename").toString());

            }
            // scoid
            if (jsonCMiColumnObj.has("attemptdate")) {

                studentResponseModel.set_attemptdate(jsonCMiColumnObj.get("attemptdate").toString());

            }
            studentResponseModel.set_siteId(learningModel.getSiteID());
            // status
            if (jsonCMiColumnObj.has("attachfileid") && !jsonCMiColumnObj.isNull("attachfileid")) {

                studentResponseModel.set_attachfileid(jsonCMiColumnObj.get("attachfileid").toString());

            }

            // status
            if (jsonCMiColumnObj.has("attachfilename") && !jsonCMiColumnObj.isNull("attachfilename")) {

                studentResponseModel.set_attachfilename(jsonCMiColumnObj.get("attachfilename").toString());

            }
            // status
            if (jsonCMiColumnObj.has("assessmentattempt") && !jsonCMiColumnObj.isNull("assessmentattempt")) {

                int numberAtmps = Integer.parseInt(jsonCMiColumnObj.get("assessmentattempt").toString());
                studentResponseModel.set_assessmentattempt(numberAtmps);

            }
            injectIntoStudentResponseTable(studentResponseModel);
        }

    }


    public void injectIntoStudentResponseTable(StudentResponseModel studentResponseModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("siteid", studentResponseModel.get_siteId());
            contentValues.put("scoid", studentResponseModel.get_scoId());
            contentValues.put("userid", studentResponseModel.get_userId());
            contentValues.put("questionid", studentResponseModel.get_questionid());
            contentValues.put("assessmentattempt", studentResponseModel.get_assessmentattempt());
            contentValues.put("questionattempt", studentResponseModel.get_questionattempt());
            contentValues.put("attemptdate", studentResponseModel.get_attemptdate());
            contentValues.put("studentresponses", studentResponseModel.get_studentresponses());
            contentValues.put("result", studentResponseModel.get_result());
            contentValues.put("attachfilename", studentResponseModel.get_attachfilename());
            contentValues.put("attachfileid", studentResponseModel.get_attachfileid());
//            contentValues.put("score", studentResponseModel.get_scoId());
//            contentValues.put("sequencenumber", studentResponseModel.se()); need to check
            contentValues.put("rindex", studentResponseModel.get_rindex());
            contentValues.put("attachedfilepath", studentResponseModel.get_attachfilename());
            contentValues.put("optionalNotes", studentResponseModel.get_optionalNotes());
            contentValues.put("capturedVidFileName", studentResponseModel.get_capturedImgFileName());
            contentValues.put("capturedVidId", studentResponseModel.get_capturedVidId());
            contentValues.put("capturedVidFilepath", studentResponseModel.get_capturedVidFilepath());

            contentValues.put("capturedImgFileName", studentResponseModel.get_capturedImgFileName());
            contentValues.put("capturedImgId", studentResponseModel.get_capturedImgId());
            contentValues.put("capturedImgFilepath", studentResponseModel.get_capturedImgFilepath());


            db.insert(TBL_STUDENTRESPONSES, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public void insertLearnerSession(JSONArray jsonArray, MyLearningModel learningModel) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonCMiColumnObj = jsonArray.getJSONObject(i);
            Log.d(TAG, "injectINTCMI: " + jsonCMiColumnObj);

            LearnerSessionModel learnerSessionTable = new LearnerSessionModel();


            learnerSessionTable.setSiteID(learningModel.getSiteID());
            //userid
            if (jsonCMiColumnObj.has("userid")) {

                learnerSessionTable.setUserID(jsonCMiColumnObj.get("userid").toString());
            }
            // timespent
            if (jsonCMiColumnObj.has("timespent")) {

                learnerSessionTable.setTimeSpent(jsonCMiColumnObj.get("timespent").toString());

            }
            // sessionid
            if (jsonCMiColumnObj.has("sessionid")) {


                learnerSessionTable.setSessionID(jsonCMiColumnObj.get("sessionid").toString());

            }
            // sessiondatetime
            if (jsonCMiColumnObj.has("sessiondatetime")) {
                String s = jsonCMiColumnObj.getString("sessiondatetime").toUpperCase();
                if (!s.equalsIgnoreCase("NULL")) {

                    String dateStr = s.substring(0, 19);
                    SimpleDateFormat curFormater = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss");
                    java.util.Date dateObj = null;
                    try {
                        dateObj = curFormater.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat postFormater = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    String strCreatedDate = postFormater
                            .format(dateObj);
                    learnerSessionTable.setSessionDateTime(strCreatedDate);
                } else {

                    learnerSessionTable.setSessionDateTime("");
                }
            }

            // scoID
            if (jsonCMiColumnObj.has("scoid")) {
//                int userID = Integer.parseInt();
                learnerSessionTable.setScoID(jsonCMiColumnObj.get("scoid").toString());

            }

            // attemptnumber
            if (jsonCMiColumnObj.has("attemptnumber") && !jsonCMiColumnObj.isNull("attemptnumber")) {

                learnerSessionTable.setAttemptNumber(jsonCMiColumnObj.get("attemptnumber").toString());
            }
            injectIntoLearnerTable(learnerSessionTable);
        }

    }


    public void injectIntoLearnerTable(LearnerSessionModel learnerSessionModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

//          contentValues.put("sessionid", learnerSessionModel.getSessionID());
            contentValues.put("userid", learnerSessionModel.getUserID());
            contentValues.put("scoid", learnerSessionModel.getScoID());
            contentValues.put("siteid", learnerSessionModel.getSiteID());
            contentValues.put("attemptnumber", learnerSessionModel.getAttemptNumber());
            contentValues.put("sessiondatetime", learnerSessionModel.getSessionDateTime());
            contentValues.put("timespent", learnerSessionModel.getTimeSpent());


            db.insert(TBL_USERSESSION, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }


    public void ejectRecordsinCmi(MyLearningModel learnerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_CMI + " WHERE siteid= '" + learnerModel.getSiteID() +
                    "' AND scoid= '" + learnerModel.getScoId() +
                    "' AND userid= '" + learnerModel.getUserID() + "'";
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void ejectRecordsinStudentResponse(MyLearningModel learnerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_STUDENTRESPONSES + " WHERE siteid= '" + learnerModel.getSiteID() +
                    "' AND scoid= '" + learnerModel.getScoId() +
                    "' AND userid= '" + learnerModel.getUserID() + "'";
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void ejectRecordsinLearnerSession(MyLearningModel learnerModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_USERSESSION + " WHERE siteid= '" + learnerModel.getSiteID() +
                    "' AND scoid= '" + learnerModel.getScoId() +
                    "' AND userid= '" + learnerModel.getUserID() + "'";
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }


    public void preFunctionalityBeforeNonLRSOfflineContentPathCreation(MyLearningModel mylearningModel, Context context) {


        String offlinePath = "";
        boolean isTrackList = false;
//        Toast.makeText(context, "This is ofline view", Toast.LENGTH_SHORT).show();
        String numberOfAttempts = "";
        String strObjectTypeID = "";
        String strStatus = "";

        if (isTrackList) {


        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            String queryStr = "SELECT D.courseattempts,D.objecttypeid,case when C.Status is NOT NULL then C.status else D.status end as ObjStatus FROM DOWNLOADDATA D left outer join CMI C On C.UserId=D.UserId and C.scoid =D.scoid and C.siteid=D.siteid WHERE D.SITEID =" + mylearningModel.getSiteID() + " AND D.SCOID = " + mylearningModel.getScoId() + " AND D.USERID = " + mylearningModel.getUserID();

            Cursor cursor = null;
            cursor = db.rawQuery(queryStr, null);

            if (cursor.moveToFirst()) {
                do {
                    numberOfAttempts = cursor.getString(cursor.getColumnIndex("courseattempts"));
                    strObjectTypeID = cursor.getString(cursor.getColumnIndex("objecttypeid"));
                    strStatus = cursor.getString(cursor.getColumnIndex("ObjStatus"));

                } while (cursor.moveToNext());
            }

        }
        if (!numberOfAttempts.equals("")) {
            if (strStatus.toLowerCase().contains("completed") || strStatus.toLowerCase().contains("failed") || strStatus.toLowerCase().contains("passed") || strStatus.toLowerCase().contains("not started")) {
                int numberofAtmInt = Integer.parseInt(numberOfAttempts);

                if (numberofAtmInt > 0) {

                    numberofAtmInt = numberofAtmInt - 1;

                    if (isTrackList) {
                        updateTrackContentAttemptsInTrackListData(mylearningModel, numberofAtmInt);

                    } else {
                        updateCourseAttemptsinDownloadData(mylearningModel, numberofAtmInt);
                    }
                    if (strStatus.toLowerCase().contains("completed") || strStatus.toLowerCase().contains("failed") || strStatus.toLowerCase().contains("passed")) {

                        //delete two tables CMI and student responses for respective ids
                        ejectRecordsinLearnerSession(mylearningModel);

                        ejectRecordsinStudentResponse(mylearningModel);

                        ejectRecordsinCmi(mylearningModel);

                    }

                }
            }
        }
    }

    public String generateOfflinePathForCourseView(MyLearningModel mylearningModel, Context context) {

//        String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
//                + "/Mydownloads/Contentdownloads" + "/";

        String downloadDestFolderPath = mylearningModel.getOfflinepath();

        String offlineCourseLaunch = "";

        if (downloadDestFolderPath.contains("file:")) {
            offlineCourseLaunch = downloadDestFolderPath;
        } else {
            offlineCourseLaunch = "file://" + downloadDestFolderPath;
        }
        String requestString = "";
        String query = "";
        String question = "";
        String locationValue = "";
        String statusValue = "";
        String suspendDataValue = "";
        String sequenceNumberValue = "1";
        int flag = 0;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String getCourseProgress = "SELECT location,status,suspenddata,sequencenumber,CourseMode FROM CMI WHERE siteid =" + mylearningModel.getSiteID() + " AND scoid = " +
                    mylearningModel.getScoId() +
                    " AND userid = " + mylearningModel.getUserID();

            Cursor cursor = null;
            cursor = db.rawQuery(getCourseProgress, null);

            if (cursor.moveToFirst()) {
                do {
                    flag = 1;
                    locationValue = cursor.getString(cursor.getColumnIndex("location"));
                    statusValue = cursor.getString(cursor.getColumnIndex("status"));
                    suspendDataValue = cursor.getString(cursor.getColumnIndex("suspenddata"));
                    sequenceNumberValue = cursor.getString(cursor.getColumnIndex("sequencenumber"));
                    sequenceNumberValue = "1";

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {

            ex.printStackTrace();

        }
        suspendDataValue = suspendDataValue.replaceAll("#", "%23");

        if (flag == 1) {

            if (mylearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                query = getTrackObjectList(mylearningModel, statusValue, suspendDataValue, sequenceNumberValue);

            } else if (mylearningModel.getObjecttypeId().equalsIgnoreCase("8") || mylearningModel.getObjecttypeId().equalsIgnoreCase("9")) {

                String assessmentAttempt = "";
                try {
                    String sqlQuery = "SELECT NoOfAttempts FROM CMI WHERE SITEID = " + mylearningModel.getSiteID() + " AND SCOID = " + mylearningModel.getScoId() + " AND USERID = " + mylearningModel.getUserID();
                    SQLiteDatabase db = this.getWritableDatabase();
                    Cursor cursor = null;
                    cursor = db.rawQuery(sqlQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            assessmentAttempt = cursor.getString(cursor.getColumnIndex("noofattempts"));
                            if (assessmentAttempt.equalsIgnoreCase("0") || assessmentAttempt.length() == 0) {
                                assessmentAttempt = "1";
                            }

                        } while (cursor.moveToNext());
                    }

                } catch (SQLiteException ex) {


                }

                if (!assessmentAttempt.equalsIgnoreCase("0")) {

                    try {
                        String sqlQuery = "select QUESTIONID,studentresponses,Result,attachfilename,attachfileid,optionalNotes,capturedVidFileName,capturedVidId,capturedImgFileName,capturedImgId from " + TBL_STUDENTRESPONSES + " WHERE SITEID = " + mylearningModel.getSiteID() + " AND SCOID = " + mylearningModel.getScoId() + " AND USERID = " + mylearningModel.getUserID() + " AND QuestionAttempt = 1";
                        SQLiteDatabase db = this.getWritableDatabase();
                        Cursor cursor = null;
                        cursor = db.rawQuery(sqlQuery, null);


                        if (cursor != null && cursor.moveToFirst()) {
                            do {

                                if (question.length() != 0) {
                                    question = question + "$";
                                }

                                String questionID = cursor.getString(cursor.getColumnIndex("questionid"));

                                if (!questionID.equalsIgnoreCase("null") && !questionID.isEmpty()) {
                                    question = question + questionID;
                                    question = question + "@";
                                }

                                String studentResponse = "";
                                String studentresp = cursor.getString(cursor.getColumnIndex("studentresponses"));

                                if (!studentresp.equalsIgnoreCase("null") && !studentresp.isEmpty()) {
                                    if ((studentresp.toLowerCase().equalsIgnoreCase("undefined")) || studentResponse.equalsIgnoreCase("null")) {
                                        studentResponse = "";
                                    } else {
                                        studentResponse = studentresp;

                                    }

                                }
                                question = question + studentResponse;
                                question = question + "@";


                                String result = cursor.getString(cursor.getColumnIndex("result"));

                                if (!result.equalsIgnoreCase("null") && !result.isEmpty()) {
                                    question = question + result;
                                    question = question + "@";
                                }

                                String attachFile = cursor.getString(cursor.getColumnIndex("attachfilename"));

                                if (!attachFile.equalsIgnoreCase("null") && !attachFile.isEmpty()) {
                                    question = question + attachFile;
                                    question = question + "@";
                                }

                                String attachFileID = cursor.getString(cursor.getColumnIndex("attachfileid"));

                                if (!attachFileID.equalsIgnoreCase("null") && !attachFileID.isEmpty()) {
                                    question = question + attachFileID;
                                    question = question + "@";
                                }

                                String optionalNotes = cursor.getString(cursor.getColumnIndex("optionalNotes"));

                                if (!optionalNotes.equalsIgnoreCase("null") && !optionalNotes.isEmpty()) {
                                    question = question + optionalNotes;
                                    question = question + "@";
                                }


                                String capturedVidFileName = cursor.getString(cursor.getColumnIndex("capturedVidFileName"));

                                if (!capturedVidFileName.equalsIgnoreCase("null") && !capturedVidFileName.isEmpty()) {
                                    question = question + capturedVidFileName;
                                    question = question + "@";
                                }

                                String capturedVidID = cursor.getString(cursor.getColumnIndex("capturedVidId"));

                                if (!capturedVidID.equalsIgnoreCase("null") && !capturedVidID.isEmpty()) {
                                    question = question + capturedVidID;
                                    question = question + "@";
                                }

                                String capturedImgFileName = cursor.getString(cursor.getColumnIndex("capturedImgFileName"));

                                if (!capturedImgFileName.equalsIgnoreCase("null") && !capturedImgFileName.isEmpty()) {
                                    question = question + capturedImgFileName;
                                    question = question + "@";
                                }

                                String capturedImgID = cursor.getString(cursor.getColumnIndex("capturedImgId"));

                                if (!capturedImgID.equalsIgnoreCase("null") && !capturedImgID.isEmpty()) {
                                    question = question + capturedImgID;
                                    question = question + "@";
                                }

//                                Log.d(TAG, "generateOfflinePathForCourseView: " + question);
                            } while (cursor.moveToNext());
                        }
                    } catch (SQLiteException ex) {
                        ex.printStackTrace();

                    }
                    question = question.replaceAll("null", "");

                    Log.d(TAG, "generateOfflinePathForCourseView: " + question);

                    query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&quesdata=" + question + "&sname=" + mylearningModel.getUserName();

                } else {

                    query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&sname=" + mylearningModel.getUserName();
                }
            }

        } else {
            sequenceNumberValue = "1";
            query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&quesdata=" + question + "&sname=" + mylearningModel.getUserName();
        }
//      not required for now
        boolean isSessionExists = false;
        int numberOfAttemptsInt = 0;
//            var timeSpent = "00:00:00"
        String sqlQuery = "SELECT count(sessionid) as attemptscount FROM " + TBL_USERSESSION + " WHERE siteid = " + mylearningModel.getSiteID() + " AND scoid = " + mylearningModel.getScoId() + " AND userid = " + mylearningModel.getUserID();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(sqlQuery, null);
        try {

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    isSessionExists = true;
                    String counts = cursor.getString(cursor.getColumnIndex("attemptscount"));
                    numberOfAttemptsInt = Integer.parseInt(counts);
                    numberOfAttemptsInt = numberOfAttemptsInt + 1;

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {


        }

        if (isSessionExists) {
            LearnerSessionModel learnersessionTb = new LearnerSessionModel();
            if (mylearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                String[] tupleValues = getTrackObjectTypeIDAndScoidBasedOnSequenceNumber(mylearningModel, sequenceNumberValue);
                String objectScoidValue = tupleValues[0];


                int latestAttemptNo = getLatestAttempt(objectScoidValue,
                        mylearningModel.getUserID(), mylearningModel.getSiteID());

                learnersessionTb.setScoID(objectScoidValue);

                learnersessionTb.setAttemptNumber("" + latestAttemptNo);
            } else {
                learnersessionTb.setScoID(mylearningModel.getScoId());
                learnersessionTb.setAttemptNumber("" + numberOfAttemptsInt);
            }

            learnersessionTb.setSiteID(mylearningModel.getSiteID());
            learnersessionTb.setUserID(mylearningModel.getUserID());
            learnersessionTb.setSessionDateTime(GetCurrentDateTime());

            insertUserSession(learnersessionTb);
        }


        query = query.replaceAll("#", "%23");

        requestString = offlineCourseLaunch + query + "&IsInstancyContent=true";


        Log.d(TAG, "generateOfflinePathForCourseView: " + requestString);

        return requestString;
    }

    public int getLatestAttempt(String scoId, String userId, String siteID) {

        String sqlQuery = "SELECT count(sessionid) as attemptscount FROM " + TBL_USERSESSION + " WHERE siteid = " + siteID + " AND scoid = " + scoId + " AND userid = " + userId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(sqlQuery, null);

        int numberOfAttemptsInt = 0;
        try {

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    String counts = cursor.getString(cursor.getColumnIndex("attemptscount"));
                    numberOfAttemptsInt = Integer.parseInt(counts);


                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {


        }

        return numberOfAttemptsInt + 1;
    }


    public String[] getTrackObjectTypeIDAndScoidBasedOnSequenceNumber(MyLearningModel learningModel, String seqNumber) {

        String[] strAry = new String[2];

        String sqlQuery = "SELECT scoid, objecttypeid FROM " + TBL_TRACKOBJECTS + " WHERE siteid = " + learningModel.getSiteID() + " AND trackscoid = " + learningModel.getScoId() + " AND sequencenumber = " + seqNumber + " AND userid = " + learningModel.getUserID();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery(sqlQuery, null);

        try {

            if (cursor != null && cursor.moveToFirst()) {
                while (cursor.moveToNext()) {

                    String scoID = cursor.getString(cursor.getColumnIndex("scoid"));
                    String objectTypeID = cursor.getString(cursor.getColumnIndex("objecttypeid"));

                    strAry[0] = scoID;
                    strAry[1] = objectTypeID;

                }


            }
        } catch (SQLiteException ex) {


        }
        return strAry;
    }


    public String generateOfflinePathForCourseViewTemplateView(MyLearningModel mylearningModel) {

//        String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
//                + "/Mydownloads/Contentdownloads" + "/";

        String downloadDestFolderPath = mylearningModel.getOfflinepath();

        String offlineCourseLaunch = "";

        if (downloadDestFolderPath.contains("file:")) {
            offlineCourseLaunch = downloadDestFolderPath;
        } else {
            offlineCourseLaunch = "file://" + downloadDestFolderPath;
        }
        String requestString = "";
        String query = "";
        String question = "";
        String locationValue = "";
        String statusValue = "";
        String suspendDataValue = "";
        String sequenceNumberValue = "1";
        int flag = 0;

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String getCourseProgress = "SELECT location,status,suspenddata,sequencenumber,CourseMode FROM CMI WHERE siteid =" + mylearningModel.getSiteID() + " AND scoid = " +
                    mylearningModel.getScoId() +
                    " AND userid = " + mylearningModel.getUserID();

            Cursor cursor = null;
            cursor = db.rawQuery(getCourseProgress, null);

            if (cursor.moveToFirst()) {
                do {
                    flag = 1;
                    locationValue = cursor.getString(cursor.getColumnIndex("location"));
                    statusValue = cursor.getString(cursor.getColumnIndex("status"));
                    suspendDataValue = cursor.getString(cursor.getColumnIndex("suspenddata"));
                    sequenceNumberValue = cursor.getString(cursor.getColumnIndex("sequencenumber"));

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {

            ex.printStackTrace();

        }
        suspendDataValue = suspendDataValue.replaceAll("#", "%23");

        if (flag == 1) {
            if (mylearningModel.getObjecttypeId().equalsIgnoreCase("8") || mylearningModel.getObjecttypeId().equalsIgnoreCase("9") || mylearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                String assessmentAttempt = "";
                try {
                    String sqlQuery = "SELECT NoOfAttempts FROM CMI WHERE SITEID = " + mylearningModel.getSiteID() + " AND SCOID = " + mylearningModel.getScoId() + " AND USERID = " + mylearningModel.getUserID();
                    SQLiteDatabase db = this.getWritableDatabase();
                    Cursor cursor = null;
                    cursor = db.rawQuery(sqlQuery, null);

                    if (cursor.moveToFirst()) {
                        do {
                            assessmentAttempt = cursor.getString(cursor.getColumnIndex("noofattempts"));
                            if (assessmentAttempt.equalsIgnoreCase("0") || assessmentAttempt.length() == 0) {
                                assessmentAttempt = "1";
                            }

                        } while (cursor.moveToNext());
                    }

                } catch (SQLiteException ex) {


                }

                if (!assessmentAttempt.equalsIgnoreCase("0")) {

                    try {
                        String sqlQuery = "select QUESTIONID,studentresponses,Result,attachfilename,attachfileid,optionalNotes,capturedVidFileName,capturedVidId,capturedImgFileName,capturedImgId from " + TBL_STUDENTRESPONSES + " WHERE SITEID = " + mylearningModel.getSiteID() + " AND SCOID = " + mylearningModel.getScoId() + " AND USERID = " + mylearningModel.getUserID() + " AND QuestionAttempt = 1";
                        SQLiteDatabase db = this.getWritableDatabase();
                        Cursor cursor = null;
                        cursor = db.rawQuery(sqlQuery, null);


                        if (cursor != null && cursor.moveToFirst()) {
                            do {

                                if (question.length() != 0) {
                                    question = question + "$";
                                }

                                String questionID = cursor.getString(cursor.getColumnIndex("questionid"));

                                if (!questionID.equalsIgnoreCase("null") && !questionID.isEmpty()) {
                                    question = question + questionID;
                                    question = question + "@";
                                }

                                String studentResponse = "";
                                String studentresp = cursor.getString(cursor.getColumnIndex("studentresponses"));

                                if (!studentresp.equalsIgnoreCase("null") && !studentresp.isEmpty()) {
                                    if ((studentresp.toLowerCase().equalsIgnoreCase("undefined")) || studentResponse.equalsIgnoreCase("null")) {
                                        studentResponse = "";
                                    } else {
                                        studentResponse = studentresp;

                                    }

                                }
                                question = question + studentResponse;
                                question = question + "@";


                                String result = cursor.getString(cursor.getColumnIndex("result"));

                                if (!result.equalsIgnoreCase("null") && !result.isEmpty()) {
                                    question = question + result;
                                    question = question + "@";
                                }

                                String attachFile = cursor.getString(cursor.getColumnIndex("attachfilename"));

                                if (!attachFile.equalsIgnoreCase("null") && !attachFile.isEmpty()) {
                                    question = question + attachFile;
                                    question = question + "@";
                                }

                                String attachFileID = cursor.getString(cursor.getColumnIndex("attachfileid"));

                                if (!attachFileID.equalsIgnoreCase("null") && !attachFileID.isEmpty()) {
                                    question = question + attachFileID;
                                    question = question + "@";
                                }

                                String optionalNotes = cursor.getString(cursor.getColumnIndex("optionalNotes"));

                                if (!optionalNotes.equalsIgnoreCase("null") && !optionalNotes.isEmpty()) {
                                    question = question + optionalNotes;
                                    question = question + "@";
                                }


                                String capturedVidFileName = cursor.getString(cursor.getColumnIndex("capturedVidFileName"));

                                if (!capturedVidFileName.equalsIgnoreCase("null") && !capturedVidFileName.isEmpty()) {
                                    question = question + capturedVidFileName;
                                    question = question + "@";
                                }

                                String capturedVidID = cursor.getString(cursor.getColumnIndex("capturedVidId"));

                                if (!capturedVidID.equalsIgnoreCase("null") && !capturedVidID.isEmpty()) {
                                    question = question + capturedVidID;
                                    question = question + "@";
                                }

                                String capturedImgFileName = cursor.getString(cursor.getColumnIndex("capturedImgFileName"));

                                if (!capturedImgFileName.equalsIgnoreCase("null") && !capturedImgFileName.isEmpty()) {
                                    question = question + capturedImgFileName;
                                    question = question + "@";
                                }

                                String capturedImgID = cursor.getString(cursor.getColumnIndex("capturedImgId"));

                                if (!capturedImgID.equalsIgnoreCase("null") && !capturedImgID.isEmpty()) {
                                    question = question + capturedImgID;
                                    question = question + "@";
                                }

//                                Log.d(TAG, "generateOfflinePathForCourseView: " + question);
                            } while (cursor.moveToNext());
                        }
                    } catch (SQLiteException ex) {
                        ex.printStackTrace();

                    }
                    question = question.replaceAll("null", "");

                    Log.d(TAG, "generateOfflinePathForCourseView: " + question);

                    query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&quesdata=" + question + "&sname=" + mylearningModel.getUserName();

                } else {

                    query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&sname=" + mylearningModel.getUserName();
                }
            }

        } else {
            sequenceNumberValue = "1";
            query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + statusValue + "&susdata=" + suspendDataValue + "&quesdata=" + question + "&sname=" + mylearningModel.getUserName();
        }
//      not required for now
//      boolean isSessionExists = false;
//        int numberOfAttemptsInt = 0;
////            var timeSpent = "00:00:00"
//        String sqlQuery = "SELECT count(ID) as attemptscount FROM" + TBL_USERSESSION + " WHERE siteid = " + mylearningModel.getSiteID() + " AND scoid = " + mylearningModel.getScoId() + " AND userid = " + mylearningModel.getUserID();
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = null;
//        cursor = db.rawQuery(sqlQuery, null);
//        try {
//
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//
//                    isSessionExists = true;
//                    String counts = cursor.getString(cursor.getColumnIndex("attemptscount"));
//                    numberOfAttemptsInt = Integer.parseInt(counts);
//                    numberOfAttemptsInt = numberOfAttemptsInt + 1;
//
//                } while (cursor.moveToNext());
//            }
//        } catch (SQLiteException ex) {
//
//
//        }
//
//        {
//
//            LearnerSessionModel learnersessionTb = new LearnerSessionModel();
//            if (mylearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
////                let tupleValues = self.getTrackObjectTypeIDAndScoidBasedOnSequenceNumber(scoidValue, sequenceNumber:
//                sequenceNumberValue, siteID:siteIDValue, userID:userIDValue)
//                let objectScoidValue = tupleValues.scoid
//                let latestAttemptNo = self.getlatestAtempt(objectScoidValue, userid:
//                userIDValue, siteid:siteIDValue)
//                learnersessionTb.setScoID(mylearningModel.getScoId());
//                userSessionModel.attemptNumber = String(latestAttemptNo + 1)
//            } else {
//                userSessionModel.scoID = scoidValue
//                userSessionModel.attemptNumber = String(numberOfAttemptsInt)
//            }
//            userSessionModel.siteID = siteIDValue
//            userSessionModel.userID = userIDValue
//            userSessionModel.sessionDateTime = Singleton.sharedInstance.getCurrentDate()
//            insertUsersessionData(userSessionModel);
//        }
//
//        if (isSessionExists) {
//
//            learnersessionTb.setSiteID(mylearningModel.getSiteID());
//            learnersessionTb.setUserID(mylearningModel.getUserID());
//            learnersessionTb.setScoID(mylearningModel.getScoId());
//            learnersessionTb.setAttemptNumber("" + numberOfAttemptsInt + 1);
//            learnersessionTb.setSessionDateTime(GetCurrentDateTime());
//            insertUserSession(learnersessionTb);
//
//            if (mylearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
//                int tempTrackObjScoId = GetTrackObjectScoid(mylearningModel);
//                int objLastAttempt = getLatestAttempt(tempTrackObjScoId,
//                        Integer.parseInt(userid), Integer.parseInt(siteId));
//                LearnerSessionModel objSession = new UserSessionDetails();
//                objSession.set_siteId(siteId);
//                objSession.set_userId(Integer.parseInt(userid));
//                objSession.set_scoId(tempTrackObjScoId);
//                objSession.set_attemptnumber(objLastAttempt + 1);
//                objSession.se(getCurrentDateTime());
//                dbh.insertUserSession(objSession);
//            }
//        }

        query = query.replaceAll("#", "%23");

        requestString = offlineCourseLaunch + query + "&IsInstancyContent=true";


        Log.d(TAG, "generateOfflinePathForCourseView: " + requestString);

        return requestString;
    }

    public String getTrackObjectList(MyLearningModel mylearningModel, String lStatusValue, String susData, String seqNumnber) {

        String query = "";
        String locationValue = "";
        String statusValue = "";
        String suspendDataValue = "";
        String question = "";
        String tempSeqNo = "";

        SQLiteDatabase db = this.getWritableDatabase();
        String sqlQuery = "SELECT C.status,C.suspenddata,C.location,T.sequencenumber FROM CMI C inner join " + TBL_TRACKOBJECTS + " T on C.scoid=T.scoid AND C.userid=T.userid WHERE C.siteid =" + mylearningModel.getSiteID() + " AND C.userid = " + mylearningModel.getUserID() + " AND T.TrackSCOID =" + mylearningModel.getScoId() + " order by T.SequenceNumber ";
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(sqlQuery, null);

            if (cursor.moveToFirst()) {
                do {

                    String status = "";
                    String suspendData = "";
                    String location = "";
                    String sequenceNo = "";

                    status = cursor.getString(cursor.getColumnIndex("status"));
                    suspendData = cursor.getString(cursor.getColumnIndex("suspenddata"));
                    location = cursor.getString(cursor.getColumnIndex("location"));
                    sequenceNo = cursor.getString(cursor.getColumnIndex("sequencenumber"));

                    if (!statusValue.equalsIgnoreCase("null") && !statusValue.isEmpty()) {

                        statusValue = statusValue + "@" + status;
                    } else {

                        statusValue = status + "$" + sequenceNo;
                    }


                    if (!suspendDataValue.equalsIgnoreCase("null") && !suspendDataValue.isEmpty()) {

                        suspendDataValue = suspendDataValue + "@" + suspendData;
                    } else {

                        suspendDataValue = suspendData + "$" + sequenceNo;
                    }

                    if (!locationValue.equalsIgnoreCase("null") && !locationValue.isEmpty()) {

                        locationValue = locationValue + "@" + location;
                    } else {

                        locationValue = location + "$" + sequenceNo;
                    }

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }


        String sqlQuerys = "select S.QUESTIONID,S.StudentResponses,S.result,S.attachfilename,S.attachfileid,T.Sequencenumber,S.OptionalNotes,S.capturedVidFileName,S.capturedVidId,S.capturedImgFileName,S.capturedImgId from " + TBL_STUDENTRESPONSES + " S inner join " + TBL_TRACKOBJECTS + " T on S.scoid=T.scoid AND S.userid=T.userid WHERE S.SITEID =" + mylearningModel.getSiteID() + " AND S.USERID =" + mylearningModel.getUserID() + " AND T.TrackSCOID = " + mylearningModel.getScoId() + " AND S.assessmentattempt = (select max(assessmentattempt) from " + TBL_STUDENTRESPONSES + " where scoid= T.scoid) order by T.SequenceNumber";

        try {
            Cursor cursor = null;
            cursor = db.rawQuery(sqlQuerys, null);

            if (cursor.moveToFirst()) {
                do {

                    if (question.length() != 0) {
                        question = question + "$";
                    }


                    String sqNO = "";
                    String seqNo = cursor.getString(cursor.getColumnIndex("questionid"));

                    if (!seqNo.equalsIgnoreCase("null") && !seqNo.isEmpty()) {
                        sqNO = seqNo;
                    }

                    if (!tempSeqNo.toLowerCase().contains(sqNO)) {

                        if (!question.equalsIgnoreCase("null") && !question.isEmpty()) {
                            question = question + "~";
                        }
                        tempSeqNo = seqNo;
                        question = question + seqNo + "-";

                    }

                    String questionID = cursor.getString(cursor.getColumnIndex("questionid"));

                    if (!questionID.equalsIgnoreCase("null") && !questionID.isEmpty()) {
                        question = question + questionID;
                        question = question + "@";
                    }

                    String studentResponse = "";
                    String studentresp = cursor.getString(cursor.getColumnIndex("studentresponses"));

                    if (!studentresp.equalsIgnoreCase("null") && !studentresp.isEmpty()) {
                        if ((studentresp.toLowerCase().equalsIgnoreCase("undefined")) || studentResponse.equalsIgnoreCase("null")) {
                            studentResponse = "";
                        } else {
                            studentResponse = studentresp;

                        }

                    }
                    question = question + studentResponse;
                    question = question + "@";


                    String result = cursor.getString(cursor.getColumnIndex("result"));

                    if (!result.equalsIgnoreCase("null") && !result.isEmpty()) {
                        question = question + result;
                        question = question + "@";
                    }

                    String attachFile = cursor.getString(cursor.getColumnIndex("attachfilename"));

                    if (!attachFile.equalsIgnoreCase("null") && !attachFile.isEmpty()) {
                        question = question + attachFile;
                        question = question + "@";
                    }

                    String attachFileID = cursor.getString(cursor.getColumnIndex("attachfileid"));

                    if (!attachFileID.equalsIgnoreCase("null") && !attachFileID.isEmpty()) {
                        question = question + attachFileID;
                        question = question + "@";
                    }

                    String optionalNotes = cursor.getString(cursor.getColumnIndex("optionalNotes"));

                    if (!optionalNotes.equalsIgnoreCase("null") && !optionalNotes.isEmpty()) {
                        question = question + optionalNotes;
                        question = question + "@";
                    }


                    String capturedVidFileName = cursor.getString(cursor.getColumnIndex("capturedVidFileName"));

                    if (!capturedVidFileName.equalsIgnoreCase("null") && !capturedVidFileName.isEmpty()) {
                        question = question + capturedVidFileName;
                        question = question + "@";
                    }

                    String capturedVidID = cursor.getString(cursor.getColumnIndex("capturedVidId"));

                    if (!capturedVidID.equalsIgnoreCase("null") && !capturedVidID.isEmpty()) {
                        question = question + capturedVidID;
                        question = question + "@";
                    }

                    String capturedImgFileName = cursor.getString(cursor.getColumnIndex("capturedImgFileName"));

                    if (!capturedImgFileName.equalsIgnoreCase("null") && !capturedImgFileName.isEmpty()) {
                        question = question + capturedImgFileName;
                        question = question + "@";
                    }

                    String capturedImgID = cursor.getString(cursor.getColumnIndex("capturedImgId"));

                    if (!capturedImgID.equalsIgnoreCase("null") && !capturedImgID.isEmpty()) {
                        question = question + capturedImgID;
                        question = question + "@";
                    }

                } while (cursor.moveToNext());

            }
        } catch (SQLiteException ex)

        {

            ex.printStackTrace();
        }

        Log.d(TAG, "getTrackObjectList: " + question);
        question = question.replaceAll("%25", "%");

        Log.d(TAG, "getTrackObjectList: " + suspendDataValue);

        String displayName = mylearningModel.getUserName();

        String replaceString = displayName.replaceAll(" ", "%20");


        query = "?nativeappURL=true&cid=" + mylearningModel.getScoId() + "&stid=" + mylearningModel.getUserID() + "&lloc=" + locationValue + "&lstatus=" + lStatusValue + "&susdata=" + susData + "&tbookmark=" + seqNumnber + "&LtSusdata=" + suspendDataValue + "&LtQuesData=" + question + "&LtStatus=" + statusValue + "&sname=" + replaceString;

        query = query.replaceAll("null", "");

        String replaceStr = query.replace(" ", "%20");
        return replaceStr;
    }

    private String GetCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }

    public void updateCourseAttemptsinDownloadData(MyLearningModel myLearningModel,
                                                   int numberAtempts) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String strUpdate = "UPDATE " + TBL_DOWNLOADDATA + " SET courseattempts = '"
                    + numberAtempts
                    + "' WHERE siteid ='"
                    + myLearningModel.getSiteID() + "'"
                    + " AND " + " scoid=" + "'"
                    + myLearningModel.getScoId() + "'" + " AND "
                    + " userid=" + "'"
                    + myLearningModel.getUserID() + "'";
            db.execSQL(strUpdate);

        } catch (Exception e) {

            Log.e("updateContentStatus", e.toString());
        }
        db.close();

    }

    public void updateTrackContentAttemptsInTrackListData(MyLearningModel myLearningModel, int numberofAtmInt) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String strUpdate = "UPDATE " + TBL_TRACKLISTDATA + " SET status = '"
                    + numberofAtmInt
                    + "' WHERE siteid ='"
                    + myLearningModel.getSiteID() + "'"
                    + " AND " + " scoid=" + "'"
                    + myLearningModel.getScoId() + "'" + " AND "
                    + " userid=" + "'"
                    + myLearningModel.getUserID() + "'";
            db.execSQL(strUpdate);

        } catch (Exception e) {

            Log.e("updateContentStatus", e.toString());
        }
        db.close();

    }

    public void updateCMiRecordForTemplateView(MyLearningModel learningModel, String seqId, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSessionExist = false;
        String scoID = "";
        try {
            String sqlQuery = "SELECT scoid FROM " + TBL_TRACKOBJECTS + " WHERE siteid = " + learningModel.getSiteID() + " AND trackscoid = " + learningModel.getScoId() + " AND userid = " + learningModel.getUserID() + " AND sequencenumber = " + seqId;
            Cursor cursor = null;
            cursor = db.rawQuery(sqlQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    scoID = cursor.getString(cursor.getColumnIndex("scoid"));

                } while (cursor.moveToNext());
            }
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

        try {


            String strExeQuery = "SELECT count(sessionid) AS attemptscount from  " + TBL_USERSESSION + " WHERE scoid="
                    + scoID + " AND siteid=" + learningModel.getSiteID()
                    + " AND userid=" + learningModel.getUserID();
            Cursor cursor = null;
            cursor = db.rawQuery(strExeQuery, null);


            if (cursor.moveToFirst()) {
                do {
                    int attemptCount = cursor.getInt(cursor.getColumnIndex("attemptscount"));

                    if (attemptCount > 0) {
                        isSessionExist = true;
                    }

                } while (cursor.moveToNext());
            }


        } catch (Exception e) {
            Log.e("templateinsert", e.toString());
        }

        if (isSessionExist) {

            LearnerSessionModel learneModel = new LearnerSessionModel();
            learneModel.setScoID(scoID);
            learneModel.setAttemptNumber("1");
            learneModel.setSiteID(learningModel.getSiteID());
            learneModel.setUserID(learningModel.getUserID());
            learneModel.setSessionDateTime(GetCurrentDateTime());
            learneModel.setTimeSpent("00:00::00");
            insertUserSession(learneModel);
        }

        if (!scoID.equalsIgnoreCase("")) {
            try {
                String strExeQuery = "UPDATE CMI SET location = " + location
                        + ", isupdate= 'false'" + " WHERE scoid="
                        + scoID + " AND siteid=" + learningModel.getSiteID()
                        + " AND userid=" + learningModel.getUserID();
                db.execSQL(strExeQuery);
            } catch (Exception e) {
                Log.e("templateinsert", e.toString());
            }
        }

        db.close();

    }

    public String SaveQuestionDataWithQuestionDataMethod(MyLearningModel learningModel, String questionData) {

        String uploadfilepath = dbctx.getExternalFilesDir(null) + "/Mydownloads/";

        String strTempUploadPath = uploadfilepath.substring(1,
                uploadfilepath.lastIndexOf("/"))
                + "/Offline_Attachments";

        String tempStr = questionData.replaceAll("undefined", "");
        String[] quesAry = tempStr.split("@");

        int assessmentAttempt = Getassessmentattempt(learningModel, "false");

        if (quesAry.length > 3) {

            StudentResponseModel studentresponse = new StudentResponseModel();
            studentresponse.set_scoId(Integer.parseInt(learningModel.getScoId()));
            studentresponse.set_siteId(learningModel.getSiteID());
            studentresponse.set_userId(Integer.parseInt(learningModel.getUserID()));

            studentresponse.set_studentresponses(quesAry[2]);
            studentresponse.set_result(quesAry[3]);
            studentresponse.set_assessmentattempt(assessmentAttempt);
            String formattedDate = GetCurrentDateTime();
            studentresponse.set_attemptdate(formattedDate);

            if (learningModel.getObjecttypeId().equalsIgnoreCase("8")) {
                studentresponse.set_questionid(Integer
                        .parseInt(quesAry[0]) + 1);

            } else {
                studentresponse.set_questionid(Integer
                        .parseInt(quesAry[0]));
            }

            if (quesAry.length > 4) {

                String tempOptionalNotes = quesAry[4];
                if (tempOptionalNotes.contains("^notes^")) {
                    tempOptionalNotes = tempOptionalNotes.replace(
                            "^notes^", "");
                    studentresponse
                            .set_optionalNotes(tempOptionalNotes);
                } else {
                    if (quesAry.length > 5) {
                        studentresponse
                                .set_attachfilename(quesAry[4]);
                        studentresponse.set_attachfileid(quesAry[5]);
                        String strManyDirectories = strTempUploadPath
                                .substring(1,
                                        strTempUploadPath.lastIndexOf("/"))
                                + "/Offline_Attachments/";
                        studentresponse
                                .set_attachedfilepath(strManyDirectories
                                        + quesAry[5]);
                    }
                }
                if (quesAry.length > 6) {

                    if (quesAry[6].length() == 0
                            && quesAry[6].equals("undefined")) {
                        studentresponse.set_capturedVidFileName("");
                        studentresponse.set_capturedVidId("");
                        studentresponse.set_capturedVidFilepath("");
                    }

                    studentresponse
                            .set_capturedVidFileName(quesAry[6]);
                    studentresponse.set_capturedVidId(quesAry[7]);
                    String strManyDirectories = strTempUploadPath.substring(
                            1, strTempUploadPath.lastIndexOf("/"))
                            + "/mediaresource/mediacapture/";
                    studentresponse
                            .set_capturedVidFilepath(strManyDirectories
                                    + quesAry[7]);
                }
                if (quesAry.length > 8) {

                    if (quesAry[8].length() == 0
                            && quesAry[8].equals("undefined")) {
                        studentresponse.set_capturedImgFileName("");
                        studentresponse.set_capturedImgId("");
                        studentresponse.set_capturedImgFilepath("");
                    }

                    studentresponse
                            .set_capturedImgFileName(quesAry[8]);
                    studentresponse.set_capturedImgId(quesAry[9]);
                    String strManyDirectories = strTempUploadPath.substring(
                            1, strTempUploadPath.lastIndexOf("/"))
                            + "/mediaresource/mediacapture/";
                    studentresponse
                            .set_capturedImgFilepath(strManyDirectories
                                    + quesAry[9]);
                }
            } else {

                studentresponse.set_attachfilename("");
                studentresponse.set_attachfileid("");
                studentresponse.set_attachedfilepath("");
                studentresponse.set_rindex(0);
                studentresponse.set_optionalNotes("");

                studentresponse.set_capturedVidFileName("");
                studentresponse.set_capturedVidId("");
                studentresponse.get_capturedVidFilepath();

                studentresponse.set_capturedImgFileName("");
                studentresponse.set_capturedImgId("");
                studentresponse.set_capturedImgFilepath("");

            }
            insertStudentResponses(studentresponse);

        }
        return "true";
    }


    public void insertStudentResponses(StudentResponseModel resDetails) {

        if (!isValidString(resDetails.get_attachfilename())) {
            resDetails.set_attachfileid("");
            resDetails.set_attachfilename("");
            resDetails.set_attachedfilepath("");
        }

        if (!isValidString(resDetails.get_optionalNotes())) {
            resDetails.set_optionalNotes("");
        }

        if (!isValidString(resDetails.get_capturedVidFileName())) {
            resDetails.set_capturedVidFileName("");
            resDetails.set_capturedVidId("");
            resDetails.set_capturedVidFilepath("");
        }

        if (!isValidString(resDetails.get_capturedImgFileName())) {
            resDetails.set_capturedImgFileName("");
            resDetails.set_capturedImgId("");
            resDetails.set_capturedImgFilepath("");
        }

        Boolean isStudentResExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            String strExeQuery1 = "";
            strExeQuery1 = "SELECT MAX(AssessmentAttempt) AS assessmentnumber from studentresponses WHERE scoid="
                    + resDetails.get_scoId()
                    + " and userid="
                    + resDetails.get_userId()
                    + " and questionid="
                    + resDetails.get_questionid()
                    + " and siteid="
                    + resDetails.get_siteId();

            cursor = db.rawQuery(strExeQuery1, null);
            int assesmentNumber = 1;
            int quesAttempt = 1;
            if (cursor != null & cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {

                    if (cursor.getString(0) != null) {

                        assesmentNumber = Integer.parseInt(cursor.getString(0));

                        Log.d("TAG", "Here assesmentNumber " + assesmentNumber);

                    }

                }

                String strExeQuery2 = "";
                strExeQuery2 = "SELECT QUESTIONID FROM studentresponses WHERE scoid="
                        + resDetails.get_scoId()
                        + " and userid="
                        + resDetails.get_userId()
                        + " and questionid="
                        + resDetails.get_questionid()
                        + " and siteid="
                        + resDetails.get_siteId()
                        + " and AssessmentAttempt="
                        + assesmentNumber;

                cursor = db.rawQuery(strExeQuery2, null);

                if (cursor != null & cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {

                        isStudentResExist = true;

                    }

                }
                String strExeQuery = "";
                if (isStudentResExist) {
                    strExeQuery = "UPDATE studentresponses SET studentresponses ='"
                            + resDetails.get_studentresponses()
                            + "',result='"
                            + resDetails.get_result()
                            + "',attachfilename= '"
                            + resDetails.get_attachfilename()
                            + "',attachfileid='"
                            + resDetails.get_attachfileid()
                            + "' ,attachedfilepath='"
                            + resDetails.get_attachedfilepath()
                            + "' ,optionalNotes='"
                            + resDetails.get_optionalNotes()
                            + "' ,capturedVidFileName ='"
                            + resDetails.get_capturedVidFileName()
                            + "' ,capturedVidId ='"
                            + resDetails.get_capturedVidId()
                            + "' ,capturedVidFilepath ='"
                            + resDetails.get_capturedVidFilepath()
                            + "' ,capturedImgFileName ='"
                            + resDetails.get_capturedImgFileName()
                            + "' ,capturedImgId ='"
                            + resDetails.get_capturedImgId()
                            + "' ,capturedImgFilepath ='"
                            + resDetails.get_capturedImgFilepath()
                            + "' where scoid="
                            + resDetails.get_scoId()
                            + " and userid="
                            + resDetails.get_userId()
                            + " and questionid="
                            + resDetails.get_questionid()
                            + " and siteid="
                            + resDetails.get_siteId()
                            + " and assessmentattempt=" + assesmentNumber;

                } else {
                    strExeQuery = "INSERT INTO STUDENTRESPONSES(siteid,scoid,userid,questionid,assessmentattempt,questionattempt,attemptdate,studentresponses,result,attachfilename,attachfileid,attachedfilepath,optionalNotes,capturedVidFileName,capturedVidId,capturedVidFilepath,capturedImgFileName,capturedImgId,capturedImgFilepath)"
                            + " values ("
                            + resDetails.get_siteId()
                            + ","
                            + resDetails.get_scoId()
                            + ","
                            + resDetails.get_userId()
                            + ","
                            + resDetails.get_questionid()
                            + ","
                            + assesmentNumber
                            + ","
                            + quesAttempt
                            + ",'"
                            + resDetails.get_attemptdate()
                            + "','"
                            + resDetails.get_studentresponses()
                            + "','"
                            + resDetails.get_result()
                            + "','"
                            + resDetails.get_attachfilename()
                            + "','"
                            + resDetails.get_attachfileid()
                            + "','"
                            + resDetails.get_attachedfilepath()
                            + "','"
                            + resDetails.get_optionalNotes()
                            + "','"
                            + resDetails.get_capturedVidFileName()
                            + "','"
                            + resDetails.get_capturedVidId()
                            + "','"
                            + resDetails.get_capturedVidFilepath()
                            + "','"
                            + resDetails.get_capturedImgFileName()
                            + "','"
                            + resDetails.get_capturedImgId()
                            + "','"
                            + resDetails.get_capturedImgFilepath() + "')";
                }

                try {
                    db.execSQL(strExeQuery);
                } catch (Exception e) {
                    Log.d("Insertstudentresponses", e.getMessage());

                }
            }
        } catch (Exception e) {
            Log.d("Insertstudentresponses ", e.getMessage());
        }

        cursor.close();
        db.close();
    }


    public String saveResponseCMI(MyLearningModel cmiNew, String getname, String getvalue) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String strExeQuery = "";
//            if (getname.equals("timespent")) {
//                String pretime;
//                Cursor cursor = null;
//
//                strExeQuery = "SELECT timespent,noofattempts,objecttypeid FROM cmi WHERE scoid="
//                        + cmiNew.getScoId()
//                        + " AND userid="
//                        + cmiNew.getUserID()
//                        + " AND siteid="
//                        + cmiNew.getSiteID();
//                cursor = db.rawQuery(strExeQuery, null);
//
//                if (cursor != null && cursor.getCount() > 0) {
//                    if (cursor.moveToFirst()) {
//
//                        if (isValidString(cursor.getString(cursor
//                                .getColumnIndex("timespent")))) {
//                            pretime = cursor.getString(cursor
//                                    .getColumnIndex("timespent"));
//
//                            if (!isValidString(getvalue)) {
//
//                            } else {
//                                String[] strSplitvalues = pretime.split(":");
//                                String[] strSplitvalues1 = getvalue.split(":");
//                                if (strSplitvalues.length == 3
//                                        && strSplitvalues1.length == 3) {
//                                    try {
//                                        int hours1 = (Integer
//                                                .parseInt(strSplitvalues[0]) + Integer
//                                                .parseInt(strSplitvalues1[0])) * 3600;
//                                        int mins1 = (Integer
//                                                .parseInt(strSplitvalues[1]) + Integer
//                                                .parseInt(strSplitvalues1[1])) * 60;
//                                        int secs1 = (int) (Float
//                                                .parseFloat(strSplitvalues[2]) + Float
//                                                .parseFloat(strSplitvalues1[2]));
//                                        int totaltime = hours1 + mins1 + secs1;
//                                        long longVal = totaltime;
//                                        int hours = (int) longVal / 3600;
//                                        int remainder = (int) longVal - hours
//                                                * 3600;
//                                        int mins = remainder / 60;
//                                        remainder = remainder - mins * 60;
//                                        int secs = remainder;
//
//                                        // cmiNew.set_timespent(hours+":"+mins+":"+secs);
//                                        getvalue = hours + ":" + mins + ":"
//                                                + secs;
//                                    } catch (Exception ex) {
//
//                                    }
//                                }
//                            }
//                        }
//                        if (cursor.getString(1).equals("9")
//                                || cursor.getString(1).equals("8")) {
//                            if (!isValidString(cmiNew.getScore())) {
//
//                            } else {
//                                int intNoAtt = Integer.parseInt(cursor
//                                        .getString(1));
//
//                                intNoAtt = intNoAtt + 1;
//                                strExeQuery = "UPDATE CMI SET noofattempts="
//                                        + intNoAtt + "" + ", isupdate= 'false'"
//                                        + " WHERE scoid=" + cmiNew.getScoId()
//                                        + " AND siteid=" + cmiNew.getSiteID()
//                                        + " AND userid=" + cmiNew.getUserID();
//
//                                db.execSQL(strExeQuery);
//                            }
//                        }
//                    }
//                }
//            }

//            if (cmiNew.getStatus().equalsIgnoreCase("Not Started")) {
//
//                strExeQuery = "INSERT INTO "
//                        + TBL_CMI
//                        + "(siteid,scoid,userid,location,status,isupdate)"
//                        + " VALUES (" + cmiNew.getSiteID() + ","
//                        + cmiNew.getScoId() + "," + cmiNew.getUserID()
//                        + "," + getvalue + ","
//                        + "'In Progress" + "','false')";
//                db.execSQL(strExeQuery);
//            } else {
            strExeQuery = "UPDATE CMI SET " + getname + "='" + getvalue + "'"
                    + ", isupdate= 'false'" + " WHERE scoid="
                    + cmiNew.getScoId() + " AND siteid=" + cmiNew.getSiteID()
                    + " AND userid=" + cmiNew.getUserID();

            db.execSQL(strExeQuery);

//            }

            db.close();

        } catch (Exception e) {
            Log.d("UpdatetScormCMI", e.getMessage());
        }

        return "true";
    }


    public int Getassessmentattempt(MyLearningModel learningModel, String reTake) {
        int attempt = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            String strSelQuery = "SELECT noofattempts FROM cmi  WHERE siteid="
                    + learningModel.getSiteID() + " AND scoid=" + learningModel.getScoId() + " AND userid="
                    + learningModel.getUserID();
            Cursor cursor = db.rawQuery(strSelQuery, null);
            if (cursor.moveToFirst()) {

                attempt = Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex("noofattempts")));
                if (attempt == 0)
                    attempt = attempt + 1;

                if (reTake.equals("true")) {
                    String strDelQuery = "DELETE FROM STUDENTRESPONSES WHERE siteid="
                            + learningModel.getSiteID()
                            + " AND scoid="
                            + learningModel.getScoId()
                            + " AND userid=" + learningModel.getUserID();
                    db.execSQL(strDelQuery);
                    attempt = 1;
                }
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("Error Message : ",
                    e.getMessage() + "__" + e.getLocalizedMessage() + "___"
                            + e.getCause());
            db.close();
        }
        return attempt;

    }

    public int GetTrackObjectScoid(MyLearningModel learningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "SELECT scoid FROM TRACKOBJECTS WHERE  siteid="
                + learningModel.getSiteID() + " AND trackscoid=" + learningModel.getScoId() + " AND userid="
                + learningModel.getUserID() + " AND sequencenumber=" + learningModel.getSequenceNumber();

        Cursor cursor = db.rawQuery(strSelQuery, null);
        int intScoid = 0;
        if (cursor.moveToFirst()) {
            intScoid = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return intScoid;
    }

    public String GetObjectScoDetails(MyLearningModel learningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "SELECT scoid,objecttypeid FROM TRACKOBJECTS WHERE  siteid="
                + learningModel.getSiteID()
                + " AND trackscoid="
                + learningModel.getScoId()
                + " AND userid="
                + learningModel.getUserID() + " AND sequencenumber=" + learningModel.getSequenceNumber();
        Cursor cursor = db.rawQuery(strSelQuery, null);
        String dtails = "";
        if (cursor.moveToFirst()) {
            dtails = cursor.getString(0) + "$" + cursor.getString(1);
        }
        cursor.close();
        db.close();
        return dtails;
    }

    public List<LearnerSessionModel> getAllSessionDetails(String userId,
                                                          String siteId, String scoid) {
        List<LearnerSessionModel> sessionList = new ArrayList<LearnerSessionModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT sessionid,scoid,attemptnumber,sessiondatetime,timespent FROM USERSESSION WHERE userid="
                + userId + " AND siteid=" + siteId + " AND scoid=" + scoid;
        Cursor cursor = db.rawQuery(selQuery, null);
        if (cursor.moveToFirst()) {
            do {
                LearnerSessionModel sesDetails = new LearnerSessionModel();
                sesDetails.setSessionID((cursor
                        .getString(cursor.getColumnIndex("sessionid"))));
                sesDetails.setScoID((cursor.getString(cursor
                        .getColumnIndex("scoid"))));
                sesDetails.setAttemptNumber((cursor
                        .getString(cursor.getColumnIndex("attemptnumber"))));
                sesDetails.setSessionDateTime(cursor.getString(cursor
                        .getColumnIndex("sessiondatetime")));
                sesDetails.setTimeSpent(cursor.getString(cursor
                        .getColumnIndex("timespent")));
                sessionList.add(sesDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sessionList;
    }

    public void insertUserSession(LearnerSessionModel sessionDetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String strExeQuery = "";
        String timeSpent = "00:00:00";
        try {
            strExeQuery = "SELECT * FROM " + TBL_USERSESSION + " WHERE scoid="
                    + sessionDetails.getScoID() + " AND userid="
                    + sessionDetails.getUserID() + " AND attemptnumber="
                    + sessionDetails.getAttemptNumber() + " AND siteid="
                    + sessionDetails.getSiteID();
            cursor = db.rawQuery(strExeQuery, null);

//            while (cursor.moveToNext()) {
//
//                timeSpent = (cursor.getString(cursor
//                        .getColumnIndex("timespent")));
//            }

            if (cursor != null && cursor.getCount() > 0) {
//                try {
//                    strExeQuery = "UPDATE " + TBL_USERSESSION
//                            + " SET timespent='"
//                            + timeSpent + "' WHERE scoid="
//                            + sessionDetails.getScoID() + " AND siteid="
//                            + sessionDetails.getSiteID() + " AND userid="
//                            + sessionDetails.getUserID()
//                            + " AND attemptnumber="
//                            + sessionDetails.getAttemptNumber();
//                    db.execSQL(strExeQuery);
//                } catch (Exception e) {
//                Log.d("InsertUserSession", e.getMessage());
//                }
            } else {
                try {
                    strExeQuery = "INSERT INTO "
                            + TBL_USERSESSION
                            + "(siteid,scoid,userid,attemptnumber,sessiondatetime,timespent)"
                            + " VALUES (" + sessionDetails.getSiteID() + ","
                            + sessionDetails.getScoID() + ","
                            + sessionDetails.getUserID() + ","
                            + sessionDetails.getAttemptNumber() + ",'"
                            + sessionDetails.getSessionDateTime() + "','00:00:00')";
                    db.execSQL(strExeQuery);
                } catch (Exception e) {
                    Log.d("InsertUserSession", e.getMessage());
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("InsertUserSession", e.getMessage());
        }
        db.close();
    }

    public int getLatestAttempt(MyLearningModel learningModel) {
        int attempt = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "SELECT COUNT(SessionID) FROM " + TBL_USERSESSION
                + " WHERE siteid='" + learningModel.getSiteID() + "' AND scoid='" + learningModel.getScoId()
                + "' AND userid='" + learningModel.getUserID() + "'";
        try {
            Cursor cursor = db.rawQuery(strSelQuery, null);
            if (cursor != null && cursor.moveToFirst()) {
                attempt = Integer.parseInt(cursor.getString(0));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.d("getLatestAttempt", e.getMessage());
            if (db.isOpen()) {
                db.close();
            }
        }
        return attempt;
    }

//    public void finishSynch(CMIModel cmimodel) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String selQuery = "SELECT C.scoid,C.siteid,D.objecttypeid,C.userid,C.status,D.attemptnumber FROM "
//                + TBL_CMI
//                + " C LEFT OUTER JOIN "
//                + TBL_DOWNLOADDATA
//                + " D ON D.userid=C.userid AND D.scoid =C.scoid AND D.siteid=C.siteid WHERE C.userid='"
//                + cmimodel.get_userId()
//                + "' AND C.siteid='"
//                + cmimodel.get_siteId()
//                + "' AND C.scoid ='"
//                + cmimodel.get_scoId() + "' AND C.isupdate='false'";
//        Cursor cursor = db.rawQuery(selQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                // Update CMI
//                String strUpdate = "UPDATE " + TBL_CMI
//                        + " SET isupdate='true' WHERE userid ="
//                        + cursor.getString(cursor.getColumnIndex("userid"))
//                        + " AND scoid="
//                        + cursor.getString(cursor.getColumnIndex("scoid"))
//                        + " AND siteid="
//                        + cursor.getString(cursor.getColumnIndex("siteid"));
//                db.execSQL(strUpdate);
//
//                // Delete UserSession
//                String strDelete = "DELETE FROM " + TBL_USERSESSION
//                        + " WHERE userid ="
//                        + cursor.getString(cursor.getColumnIndex("userid"))
//                        + " AND scoid="
//                        + cursor.getString(cursor.getColumnIndex("scoid"))
//                        + " AND siteid="
//                        + cursor.getString(cursor.getColumnIndex("siteid"));
//                db.execSQL(strDelete);
//
//                // Delete student responses
//                if (cursor.getString(cursor.getColumnIndex("objecttypeid"))
//                        .toString() == "9"
//                        || cursor.getString(
//                        cursor.getColumnIndex("objecttypeid"))
//                        .toString() == "8") {
//                    int lastAttempt = getLatestAttempt(learningModel);
//                    String strSDelete = "DELETE FROM " + TBL_STUDENTRESPONSES
//                            + " WHERE userid ="
//                            + cursor.getString(cursor.getColumnIndex("userid"))
//                            + " AND scoid="
//                            + cursor.getString(cursor.getColumnIndex("scoid"))
//                            + " AND siteid="
//                            + cursor.getString(cursor.getColumnIndex("siteid"))
//                            + " AND assessmentattempt !=" + lastAttempt;
//                    db.execSQL(strSDelete);
//                }
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        db.close();
//    }

    public List<StudentResponseModel> getAllResponseDetails(String userId,
                                                            String siteId, String scoId) {
        List<StudentResponseModel> responseList = new ArrayList<StudentResponseModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT scoid,questionid,assessmentattempt,questionattempt,attemptdate,studentresponses,result,attachfilename,attachfileid,attachedfilepath,optionalNotes,capturedVidFileName,capturedVidId,capturedVidFilepath,capturedImgFileName,capturedImgId,capturedImgFilepath FROM studentresponses WHERE userid="
                + userId + " AND siteid=" + siteId + " AND scoid=" + scoId;
        Cursor cursor = db.rawQuery(selQuery, null);
        if (cursor.moveToFirst()) {
            do {
                StudentResponseModel resDetails = new StudentResponseModel();
                resDetails.set_scoId(Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex("scoid"))));
                resDetails.set_questionid(Integer.parseInt(cursor
                        .getString(cursor.getColumnIndex("questionid"))));
                resDetails
                        .set_assessmentattempt(Integer.parseInt(cursor
                                .getString(cursor
                                        .getColumnIndex("assessmentattempt"))));
                resDetails.set_questionattempt(Integer.parseInt(cursor
                        .getString(cursor.getColumnIndex("questionattempt"))));
                resDetails.set_attemptdate(cursor.getString(cursor
                        .getColumnIndex("attemptdate")));
                resDetails.set_studentresponses(cursor.getString(cursor
                        .getColumnIndex("studentresponses")));
                resDetails.set_result(cursor.getString(cursor
                        .getColumnIndex("result")));
                resDetails.set_attachfilename(cursor.getString(cursor
                        .getColumnIndex("attachfilename")));
                resDetails.set_attachfileid(cursor.getString(cursor
                        .getColumnIndex("attachfileid")));
                resDetails.set_attachedfilepath(cursor.getString(cursor
                        .getColumnIndex("attachedfilepath")));
                resDetails.set_optionalNotes(cursor.getString(cursor
                        .getColumnIndex("optionalNotes")));

                resDetails.set_capturedVidFileName(cursor.getString(cursor
                        .getColumnIndex("capturedVidFileName")));
                resDetails.set_capturedVidId(cursor.getString(cursor
                        .getColumnIndex("capturedVidId")));
                resDetails.set_capturedVidFilepath(cursor.getString(cursor
                        .getColumnIndex("capturedVidFilepath")));

                resDetails.set_capturedImgFileName(cursor.getString(cursor
                        .getColumnIndex("capturedImgFileName")));
                resDetails.set_capturedImgId(cursor.getString(cursor
                        .getColumnIndex("capturedImgId")));
                resDetails.set_capturedImgFilepath(cursor.getString(cursor
                        .getColumnIndex("capturedImgFilepath")));

                responseList.add(resDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return responseList;
    }

    public void updateTrackListItemShowstatus(MyLearningModel cmiNew) {
        SQLiteDatabase db = this.getWritableDatabase();

        String strExeQuery = "";
        strExeQuery = "UPDATE " + TBL_TRACKLISTDATA + " SET showstatus='"
                + cmiNew.getShowStatus() + "' WHERE siteid='"
                + cmiNew.getSiteID() + "' AND userid='" + cmiNew.getUserID()
                + "' AND scoid='" + cmiNew.getScoId() + "'";

        try {
            db.execSQL(strExeQuery);
        } catch (SQLException e) {
            Log.e("updateTrackList: ", strExeQuery);
        }
        db.close();
    }

    public String getTrackTimedelay(MyLearningModel learningModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        String strExeQuery = "";
        Cursor cursor = null;
        String timeDelay = "0";
        strExeQuery = "SELECT timedelay FROM " + TBL_TRACKLISTDATA
                + " WHERE userid='" + learningModel.getUserID() + "' AND scoid='" + learningModel.getTrackScoid()
                + "' AND siteid='" + learningModel.getSiteID() + "'";
        try {
            cursor = db.rawQuery(strExeQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    timeDelay = cursor.getString(cursor
                            .getColumnIndex("timedelay"));
                    if (!isValidString(timeDelay)) {
                        timeDelay = "0";
                    }
                }
            }
        } catch (SQLException e) {
            Log.d("getTrackTimedelay", e.getMessage());
        }
        db.close();
        return timeDelay;
    }

    public List<CMIModel> getAllCmiDetails(String TBL_GENERIC) {
        List<CMIModel> cmiList = new ArrayList<CMIModel>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT C.location, C.status, C.suspenddata, C.datecompleted, C.noofattempts, C.score, D.objecttypeid, C.sequencenumber, C.scoid, C.userid, C.siteid, D.courseattempts, D.contentid, C.coursemode, C.scoremin, C.scoreMax, C.randomQuesSeq, C.textResponses, C.ID, C.siteurl FROM "
                + TBL_CMI
                + " C inner join "
                + TBL_GENERIC
                + " D On D.userid = C.userid and D.scoid = C.scoid and D.siteid = C.siteid WHERE C.isupdate = 'false'";
        Cursor cursor = db.rawQuery(selQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CMIModel cmiDetails = new CMIModel();
                cmiDetails.set_scoId(Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex("scoid"))));
                cmiDetails.set_location(cursor.getString(cursor
                        .getColumnIndex("location")));
                cmiDetails.set_status(cursor.getString(cursor
                        .getColumnIndex("status")));
                cmiDetails.set_suspenddata(cursor.getString(cursor
                        .getColumnIndex("suspenddata")));
                cmiDetails.set_Id(Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex("ID"))));
                cmiDetails.set_siteId(cursor.getString(cursor
                        .getColumnIndex("siteid")));
                cmiDetails.set_score(cursor.getString(cursor
                        .getColumnIndex("score")));
                cmiDetails.set_objecttypeid(cursor.getString(cursor
                        .getColumnIndex("objecttypeid")));
                cmiDetails.set_seqNum(cursor.getString(cursor
                        .getColumnIndex("sequencenumber")));
                cmiDetails.set_userId(Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex("userid"))));
                cmiDetails.set_datecompleted(cursor.getString(cursor
                        .getColumnIndex("datecompleted")));
                cmiDetails.set_noofattempts(Integer.parseInt(cursor
                        .getString(cursor.getColumnIndex("noofattempts"))));
//                cmiDetails.set_attemptsleft(cursor.getString(cursor
//                        .getColumnIndex("attemptnumber")));
                cmiDetails.set_coursemode(cursor.getString(cursor
                        .getColumnIndex("coursemode")));
                cmiDetails.set_qusseq(cursor.getString(cursor
                        .getColumnIndex("randomquesseq")));
                cmiDetails.set_sitrurl(cursor.getString(cursor
                        .getColumnIndex("siteurl")));
                cmiDetails.set_textResponses(cursor.getString(cursor
                        .getColumnIndex("textResponses")));
//                cmiDetails.set_pooledqusseq(cursor.getString(cursor
//                        .getColumnIndex("pooledquesseq")));
                cmiList.add(cmiDetails);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cmiList;
    }


    public void insertCmiIsUpdate(MyLearningModel learningModel) {

        String strExeQuery = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            strExeQuery = "UPDATE " + TBL_CMI + " SET isupdate= 'false'" + " WHERE siteid ='" + learningModel.getSiteID() + "' AND userid ='" + learningModel.getUserID() + "' AND scoid='" + learningModel.getScoId() + "'";

            db.execSQL(strExeQuery);

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

    }

    public void insertCMiIsViewd(CMIModel learningModel) {

        String strExeQuery = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            strExeQuery = "UPDATE " + TBL_CMI + " SET isupdate = 'true'" + " WHERE siteid ='" + learningModel.get_siteId() + "' AND userid ='" + learningModel.get_userId() + "' AND scoid='" + learningModel.get_scoId() + "'";

            db.execSQL(strExeQuery);

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

    }

    public int insertCMI(CMIModel cmiNew, boolean isUpdate) {
        int seqNo = 1;
        String pretime;
        SQLiteDatabase db = this.getWritableDatabase();

        if (cmiNew.get_isupdate() == null) {

            cmiNew.set_isupdate("false");
        }

        String strExeQuery = "";
        try {
            if (isUpdate) {
                strExeQuery = "SELECT sequencenumber,noofattempts,timespent FROM "
                        + TBL_CMI
                        + " WHERE scoid="
                        + cmiNew.get_scoId()
                        + " AND userid="
                        + cmiNew.get_userId()
                        + " AND siteid="
                        + cmiNew.get_siteId();
                Cursor cursor = null;
                cursor = db.rawQuery(strExeQuery, null);

                if (cursor != null && cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        seqNo = 0;

                        if (isValidString(cursor.getString(cursor
                                .getColumnIndex("timespent")))) {
                            pretime = cursor.getString(cursor
                                    .getColumnIndex("timespent"));
                            if (isValidString(cmiNew.get_timespent())) {
                                String[] strSplitvalues = pretime.split(":");
                                String[] strSplitvalues1 = cmiNew
                                        .get_timespent().split(":");
                                if (strSplitvalues.length == 3
                                        && strSplitvalues1.length == 3) {
                                    try {
                                        int hours1 = (Integer
                                                .parseInt(strSplitvalues[0]) + Integer
                                                .parseInt(strSplitvalues1[0])) * 3600;
                                        int mins1 = (Integer
                                                .parseInt(strSplitvalues[1]) + Integer
                                                .parseInt(strSplitvalues1[1])) * 60;
                                        int secs1 = (int) (Float
                                                .parseFloat(strSplitvalues[2]) + Float
                                                .parseFloat(strSplitvalues1[2]));

                                        int totaltime = hours1 + mins1 + secs1;
                                        long longVal = totaltime;

                                        int hours = (int) longVal / 3600;

                                        int remainder = (int) longVal - hours
                                                * 3600;

                                        int mins = remainder / 60;

                                        remainder = remainder - mins * 60;

                                        int secs = remainder;

                                        cmiNew.set_timespent(hours + ":" + mins
                                                + ":" + secs);
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }

                        if (isValidString(cmiNew.get_objecttypeid())) {
                            if (cmiNew.get_objecttypeid().equals("9")
                                    || cmiNew.get_objecttypeid().equals("8")) {

                                if (isValidString(cmiNew.get_score())) {
                                    if (cmiNew.get_noofattempts() == 0) {
                                        int intNoAtt = Integer
                                                .parseInt(cursor.getString(cursor
                                                        .getColumnIndex("noofattempts")));
                                        cmiNew.set_noofattempts(intNoAtt + 1);
                                    }
                                }
                            }
                        }

                    }

                    if (!isValidString(cmiNew.get_seqNum())) {
                        cmiNew.set_seqNum("0");
                    }

                    if (!isValidString(cmiNew.get_datecompleted())) {
                        strExeQuery = "UPDATE " + TBL_CMI + " SET location='"
                                + cmiNew.get_location() + "',status='"
                                + cmiNew.get_status() + "',suspenddata='"
                                + cmiNew.get_suspenddata() + "',isupdate='"
                                + cmiNew.get_isupdate() + "',score='"
                                + cmiNew.get_score() + "',noofattempts="
                                + cmiNew.get_noofattempts() + ",objecttypeid='"
                                + cmiNew.get_objecttypeid()
                                + "',sequencenumber=" + cmiNew.get_seqNum()
                                + ",timespent='" + cmiNew.get_timespent()
                                + "',coursemode='" + cmiNew.get_coursemode()
                                + "',scoremin='" + cmiNew.get_scoremin()
                                + "',scoremax='" + cmiNew.get_scoremax()
                                + "' WHERE scoid=" + cmiNew.get_scoId()
                                + " AND siteid=" + cmiNew.get_siteId()
                                + " AND userid=" + cmiNew.get_userId();
                    } else {
                        strExeQuery = "UPDATE " + TBL_CMI
                                + " SET datecompleted='"
                                + cmiNew.get_datecompleted() + "',location='"
                                + cmiNew.get_location() + "',status='"
                                + cmiNew.get_status() + "',suspenddata='"
                                + cmiNew.get_suspenddata() + "',isupdate='"
                                + cmiNew.get_isupdate() + "',score='"
                                + cmiNew.get_score() + "',noofattempts="
                                + cmiNew.get_noofattempts() + ",objecttypeid='"
                                + cmiNew.get_objecttypeid()
                                + "',sequencenumber=" + cmiNew.get_seqNum()
                                + ",timespent='" + cmiNew.get_timespent()
                                + "',coursemode='" + cmiNew.get_coursemode()
                                + "',scoremin='" + cmiNew.get_scoremin()
                                + "',scoremax='" + cmiNew.get_scoremax()
                                + "' WHERE scoid=" + cmiNew.get_scoId()
                                + " AND siteid=" + cmiNew.get_siteId()
                                + " AND userid=" + cmiNew.get_userId();
                    }
                    db.execSQL(strExeQuery);
                    cursor.close();
                } else {
                    if (isValidString(cmiNew.get_objecttypeid())) {
                        if (cmiNew.get_objecttypeid().equals("9")
                                || cmiNew.get_objecttypeid().equals("8")) {
                            if (isValidString(cmiNew.get_score())) {
                                if (cmiNew.get_noofattempts() == 0)
                                    cmiNew.set_noofattempts(1);
                            }
                        }
                    }
                    strExeQuery = "INSERT INTO "
                            + TBL_CMI
                            + "(siteid,scoid,userid,location,status,suspenddata,objecttypeid,datecompleted,noofattempts,score,sequencenumber,isupdate,startdate,timespent,coursemode,scoremin,scoremax,randomquesseq,siteurl,textResponses)"
                            + " VALUES (" + cmiNew.get_siteId() + ","
                            + cmiNew.get_scoId() + "," + cmiNew.get_userId()
                            + ",'" + cmiNew.get_location() + "','"
                            + cmiNew.get_status() + "','"
                            + cmiNew.get_suspenddata() + "','"
                            + cmiNew.get_objecttypeid() + "','"
                            + cmiNew.get_datecompleted() + "',"
                            + cmiNew.get_noofattempts() + ",'"
                            + cmiNew.get_score() + "','" + cmiNew.get_seqNum()
                            + "','" + cmiNew.get_isupdate() + "','"
                            + cmiNew.get_startdate() + "','"
                            + cmiNew.get_timespent() + "','"
                            + cmiNew.get_coursemode() + "','"
                            + cmiNew.get_scoremin() + "','"
                            + cmiNew.get_scoremax() + "','"
                            + cmiNew.get_qusseq() + "','"
                            + cmiNew.get_sitrurl() + "','"
                            + cmiNew.get_textResponses() + "')";

                    db.execSQL(strExeQuery);
                }
            } else {
                strExeQuery = "DELETE FROM CMI WHERE scoid="
                        + cmiNew.get_scoId() + " AND siteid="
                        + cmiNew.get_siteId() + " AND userid="
                        + cmiNew.get_userId();

                db.execSQL(strExeQuery);

                if (cmiNew.get_objecttypeid().equals("9")
                        || cmiNew.get_objecttypeid().equals("8")) {
                    if (isValidString(cmiNew.get_score())) {
                        if (cmiNew.get_noofattempts() == 0)
                            cmiNew.set_noofattempts(1);
                    }
                }
                strExeQuery = "INSERT INTO CMI(siteid,scoid,userid,location,status,suspenddata,objecttypeid,datecompleted,noofattempts,score,sequencenumber,isupdate,startdate,timespent,coursemode,scoremin,scoremax,randomquesseq,siteurl,textResponses)"
                        + " VALUES ("
                        + cmiNew.get_siteId()
                        + ","
                        + cmiNew.get_scoId()
                        + ","
                        + cmiNew.get_userId()
                        + ",'"
                        + cmiNew.get_location()
                        + "','"
                        + cmiNew.get_status()
                        + "','"
                        + cmiNew.get_suspenddata()
                        + "',"
                        + cmiNew.get_objecttypeid()
                        + ",'"
                        + cmiNew.get_datecompleted()
                        + "',"
                        + cmiNew.get_noofattempts()
                        + ",'"
                        + cmiNew.get_score()
                        + "','"
                        + cmiNew.get_seqNum()
                        + "','"
                        + cmiNew.get_isupdate()
                        + "','"
                        + cmiNew.get_startdate()
                        + "','"
                        + cmiNew.get_timespent()
                        + "','"
                        + cmiNew.get_coursemode()
                        + "','"
                        + cmiNew.get_scoremin()
                        + "','"
                        + cmiNew.get_scoremax()
                        + "','"
                        + cmiNew.get_qusseq()
                        + "','"
                        + cmiNew.get_sitrurl()
                        + "','"
                        + cmiNew.get_textResponses() + "')";

                db.execSQL(strExeQuery);
            }

            db.close();
        } catch (Exception e) {
            Log.d("insertCMI", e.getMessage() != null ? e.getMessage()
                    : "Error");
            if (db.isOpen()) {
                db.close();
            }
        }
        return seqNo;

    }


    public int saveCourseClose(String url, MyLearningModel learningModel) {


        int completed = -1;
        String subURL[] = url.split("\\?");

        String score = learningModel.getScore(), lStatus = "", ltStatus = "", lLoc = "", susData = "", scoid = "", seqID = "", siteIDValue = "", userIDValue = "", siteURL = "", timeSpent = "";
        String sqlTimeSpent = "";
        String sqlNoOfAttempts = "";
        String sqlPreviousState = "";
        String isSqlDataPresent = "false";
        String totalSessionTimeSpent = "";
        String totalNoOfAttempts = "0";
        String dateCompleted = "";
        String sqlScore = "0";
        if (subURL.length > 1) {
            HashMap<String, String> responMap = null;
            Log.d(TAG, "saveCourseClose: " + subURL[1]);
            String courseCloseStr = subURL[1];
            if (courseCloseStr.contains("&")) {
                String[] conditionsArray = courseCloseStr.split("&");
                int conditionCount = conditionsArray.length;
                if (conditionCount > 0) {
                    responMap = generateHashMap(conditionsArray);
//                    Log.d("Type", "Called On saveCourseClose" + responMap.keySet());
//                    Log.d("Type", "Called On saveCourseClose" + responMap.values());
//                    ioscourseclose=true&cid=338&seqid=1&stid=1&lloc=2&lstatus=incomplete&susdata=#pgvs_start#1;2;#pgvs_end#&timespent=00:00:06.88&quesdata=&ltstatus=incomplete

                    if (responMap.containsKey("susdata")) {

                        susData = responMap.get("susdata");

                    } else {

                        susData = "";

                    }
                    if (responMap.containsKey("cid")) {

                        scoid = responMap.get("cid");
                    } else {
                        scoid = "0";

                    }
                    if (responMap.containsKey("seqid")) {

                        seqID = responMap.get("seqid");
                    } else {
                        seqID = "0";

                    }
                    if (responMap.containsKey("lloc")) {

                        lLoc = responMap.get("lloc");
                    } else {
                        lLoc = "";

                    }
                    if (responMap.containsKey("lstatus")) {
                        lStatus = responMap.get("lstatus");

                    } else {
                        lStatus = "";

                    }
                    if (responMap.containsKey("timespent")) {

                        timeSpent = responMap.get("timespent");
                    } else {
                        timeSpent = "";

                    }
                    if (responMap.containsKey("ltstatus")) {

                        ltStatus = responMap.get("ltstatus");
                    } else {
                        ltStatus = "";

                    }
                    if (responMap.containsKey("score")) {

                        score = responMap.get("score");
                    } else {
                        score = "";

                    }
                    if (responMap.containsKey("stid")) {

                        userIDValue = responMap.get("stid");
                    } else {
                        userIDValue = "0";

                    }


                }
            }
            SQLiteDatabase db = this.getWritableDatabase();
            String selQuery = "SELECT location,status,suspenddata,datecompleted,score,noofattempts,timespent FROM CMI WHERE userid="
                    + learningModel.getUserID() + " AND siteid=" + learningModel.getSiteID() + " AND scoid=" + learningModel.getScoId();
            Cursor cursor = db.rawQuery(selQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    isSqlDataPresent = "true";
                    sqlScore = ((cursor.getString(cursor.getColumnIndex("score"))));
                    sqlNoOfAttempts = ((cursor.getString(cursor.getColumnIndex("noofattempts"))));
                    sqlPreviousState = (cursor.getString(cursor.getColumnIndex("status")));
                    sqlTimeSpent = (cursor.getString(cursor.getColumnIndex("timespent")));
                    dateCompleted = (cursor.getString(cursor.getColumnIndex("datecompleted")));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            if (isSqlDataPresent.equalsIgnoreCase("true")) {
                if (sqlNoOfAttempts.equalsIgnoreCase("")) {
                    sqlNoOfAttempts = "0";
                }
                if (!score.equalsIgnoreCase("")) {
                    totalNoOfAttempts = "" + Integer.parseInt(sqlNoOfAttempts) + 1;
                } else {
                    totalNoOfAttempts = "" + Integer.parseInt(sqlNoOfAttempts);
                }

                if (learningModel.getStatus().equalsIgnoreCase("passed") || learningModel.getStatus().equalsIgnoreCase("completed") || learningModel.getStatus().equalsIgnoreCase("failed")) {

                    if (!(sqlPreviousState.toLowerCase().contains("completed") || sqlPreviousState.toLowerCase().contains("failed") || sqlPreviousState.toLowerCase().contains("passed"))) {
                        dateCompleted = GetCurrentDateTime();
                    }
                    dateCompleted = dateCompleted.isEmpty() ? GetCurrentDateTime() : dateCompleted;

                }
            }
        } else {

            if (!score.equalsIgnoreCase("")) {
                totalNoOfAttempts = "1";
            } else {
                totalNoOfAttempts = "0";
            }
            if (learningModel.getStatus().equalsIgnoreCase("passed") || learningModel.getStatus().equalsIgnoreCase("completed") || learningModel.getStatus().equalsIgnoreCase("failed")) {
                dateCompleted = GetCurrentDateTime();
            }
        }
        CMIModel cmiModel = new CMIModel();
        if (learningModel.getObjecttypeId().equalsIgnoreCase("10")) {

            cmiModel.set_status(lStatus);
            cmiModel.set_datecompleted(dateCompleted);
            cmiModel.set_score("0");
            cmiModel.set_suspenddata("");
            cmiModel.set_location("");

        } else {
            cmiModel.set_suspenddata(susData);
            cmiModel.set_status(lStatus);
            cmiModel.set_location(lLoc);
            cmiModel.set_datecompleted(dateCompleted);

            if (!score.equalsIgnoreCase("")) {
                cmiModel.set_score(sqlScore);
            } else {
                cmiModel.set_score(score);
            }
        }


        if (seqID.equalsIgnoreCase("")) {

            seqID = "0";
        } else {

            cmiModel.set_seqNum("" + seqID);
        }


        cmiModel.set_timespent(totalSessionTimeSpent);


        try {

            if (userIDValue.length() == 0) {

                userIDValue = learningModel.getUserID();
            }

            if (scoid.length() == 0) {

                scoid = learningModel.getScoId();
            }
            cmiModel.set_timespent(totalSessionTimeSpent);

            cmiModel.set_siteId(learningModel.getSiteID());
            cmiModel.set_userId(Integer.parseInt(userIDValue));
            cmiModel.set_isupdate("false");
            cmiModel.set_scoId(Integer.parseInt(scoid));
            cmiModel.set_objecttypeid(learningModel.getObjecttypeId());
            cmiModel.set_sitrurl(learningModel.getSiteURL());
            cmiModel.set_noofattempts(Integer.parseInt(totalNoOfAttempts));
        } catch (NumberFormatException e) {

            e.printStackTrace();
        }


        if (isSqlDataPresent.equalsIgnoreCase("true")) {

            updateCMIDataOnCourseClose(cmiModel);
            completed = 1;
        } else {
            injectIntoCMITable(cmiModel, "false");
            completed = 1;
        }
        Log.d(TAG, "saveCourseClose: end ");

        return completed;
    }

    public void updateCMIDataOnCourseClose(CMIModel cmiModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        if (cmiModel.get_datecompleted().equalsIgnoreCase("")) {

            try {
                String strUpdate = "UPDATE " + TBL_CMI + " SET location = " + cmiModel.get_location() + ",status = '"
                        + cmiModel.get_status() + "',suspenddata='" + cmiModel.get_suspenddata() + "',isupdate='" + cmiModel.get_isupdate() + "',score='" + cmiModel.get_score() + "',noofattempts='" + cmiModel.get_noofattempts() + "',sequenceNumber='" + cmiModel.get_seqNum() + "',timespent='" + cmiModel.get_timespent()
                        + "' WHERE siteid ='"
                        + cmiModel.get_siteId() + "'"
                        + " AND " + " scoid=" + "'"
                        + cmiModel.get_scoId() + "'" + " AND "
                        + " userid=" + "'"
                        + cmiModel.get_userId() + "'";
                db.execSQL(strUpdate);
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            }
        } else {

            try {
                String strUpdate = "UPDATE " + TBL_CMI + " SET location = " + cmiModel.get_location() + ",status = '"
                        + cmiModel.get_status() + "', suspenddata = '" + cmiModel.get_suspenddata() + "', isupdate= '" + cmiModel.get_isupdate() + "', dateCompleted= '" + cmiModel.get_datecompleted() + "', score= '" + cmiModel.get_score() + "',noofattempts='" + cmiModel.get_noofattempts() + "', sequenceNumber='" + cmiModel.get_seqNum() + "',timespent='" + cmiModel.get_timespent()
                        + "' WHERE siteid ='"
                        + cmiModel.get_siteId() + "'"
                        + " AND " + " scoid=" + "'"
                        + cmiModel.get_scoId() + "'" + " AND "
                        + " userid=" + "'"
                        + cmiModel.get_userId() + "'";
                db.execSQL(strUpdate);
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            }
        }
    }


    public String GetPreviousStatus(MyLearningModel learningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuery = "SELECT status FROM CMI WHERE  siteid=" + learningModel.getSiteID()
                + " AND scoid=" + learningModel.getScoId() + " AND userid=" + learningModel.getUserID();
        Cursor cursor = db.rawQuery(strSelQuery, null);
        String prevStatus = "";
        if (cursor.moveToFirst()) {
            prevStatus = cursor.getString(cursor.getColumnIndex("status"));
        }
        cursor.close();
        db.close();
        return prevStatus;
    }

    void updateTrackListItemBlockname(MyLearningModel cmiNew) {
        SQLiteDatabase db = this.getWritableDatabase();

        String strExeQuery = "";
        strExeQuery = "UPDATE " + TBL_TRACKLISTDATA + " SET BlockName='"
                + cmiNew.getBlockName() + "' WHERE siteid='"
                + cmiNew.getSiteID() + "' AND userid='" + cmiNew.getUserID()
                + "' AND parentid='" + cmiNew.getParentID() + "'";
        try {
            db.execSQL(strExeQuery);
        } catch (SQLException e) {
            Log.e("updateTrac: ", strExeQuery);
        }
        db.close();
    }


    public int updateTrackListItemstatus(String scoId, String siteId,
                                         String userId, String updatedStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;
        Cursor isUpdated = null;
        try {
            String strUpdate = "UPDATE " + TBL_TRACKLISTDATA
                    + " SET status = '" + updatedStatus + "' WHERE siteid ='"
                    + siteId + "'" + " AND " + " scoid=" + "'" + scoId + "'"
                    + " AND " + " userid=" + "'" + userId + "'";
            db.execSQL(strUpdate);
            status = 1;
        } catch (Exception e) {
            status = -1;
            Log.e("updateContentStatus", e.toString());
        }

        db.close();

        return status;

    }

    public void updateCMIstatus(MyLearningModel learningmodel, String updatedStatus) {

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor isUpdated = null;
        try {
            String strUpdate = "UPDATE " + TBL_CMI
                    + " SET status = '" + updatedStatus + "' WHERE siteid ='"
                    + learningmodel.getSiteID() + "'" + " AND " + " scoid=" + "'" + learningmodel.getScoId() + "'"
                    + " AND " + " userid=" + "'" + learningmodel.getUserID() + "'";
            db.execSQL(strUpdate);

        } catch (Exception e) {

            Log.e("updateContentStatus", e.toString());
        }

        db.close();

    }


    public int updateTrackListItemstatusInCMI(String scoId, String siteId,
                                              String userId, String updatedStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        int status = -1;
        try {
            String strUpdate = "UPDATE " + TBL_CMI + " SET status = '"
                    + updatedStatus + "' WHERE siteid ='" + siteId + "'"
                    + " AND " + " scoid=" + "'" + scoId + "'" + " AND "
                    + " userid=" + "'" + userId + "'";
            db.execSQL(strUpdate);
            status = 1;
        } catch (Exception e) {
            status = -1;
            Log.e("updateContentStatus", e.toString());
        }

        db.close();

        return status;

    }

    /**
     * To update the time delay for Track
     *
     * @param cmiNew
     * @author Venu
     */
    public void updateTrackTimedelay(MyLearningModel cmiNew) {
        SQLiteDatabase db = this.getWritableDatabase();

        String strExeQuery = "";
        strExeQuery = "UPDATE " + TBL_TRACKLISTDATA + " SET timedelay='"
                + cmiNew.getTimeDelay() + "' WHERE siteid='"
                + cmiNew.getSiteID() + "' AND userid='" + cmiNew.getUserID()
                + "' AND scoid='" + cmiNew.getScoId() + "'";

        try {
            db.execSQL(strExeQuery);
        } catch (SQLException e) {
            Log.d("timedelayTRACKLISTDATA", e.getMessage());
        }
        db.close();
    }

    /**
     * To get the time delay of a track object or track
     *
     * @param userId
     * @param tackScoId
     * @param sitrId
     * @return
     */
    public String getTrackTimedelay(String userId, String tackScoId,
                                    String sitrId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String strExeQuery = "";
        Cursor cursor = null;
        String timeDelay = "0";
        strExeQuery = "SELECT timedelay FROM " + TBL_TRACKLISTDATA
                + " WHERE userid='" + userId + "' AND scoid='" + tackScoId
                + "' AND siteid='" + sitrId + "'";
        try {
            cursor = db.rawQuery(strExeQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    timeDelay = cursor.getString(cursor
                            .getColumnIndex("timedelay"));
                    if (!isValidString(timeDelay)) {
                        timeDelay = "0";
                    }
                }
            }
        } catch (SQLException e) {
            Log.d("getTrackTimedelay", e.getMessage());
        }
        db.close();
        return timeDelay;
    }


    public String getTrackTemplateWorkflowResults(String trackID, MyLearningModel learningModel) {
        String returnStr = "";

        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT trackscoid, userid, showstatus, ruleid, stepid, contentid, wmessage FROM " + TBL_TRACKLISTDATA + " WHERE trackscoid = '" + trackID + "' AND userid = '" + learningModel.getUserID() + "' AND siteid = '" + learningModel.getSiteID() + "'";
        Cursor cursor = db.rawQuery(selQuery, null);

        JSONArray jsonArray = new JSONArray();
        if (cursor.moveToFirst()) {

            while (cursor.moveToNext()) {

                String ruleID = (cursor.getString(cursor.getColumnIndex("ruleid")));

                if (ruleID.equalsIgnoreCase("0")) {

                    break;
                }

                JSONObject trackObj = new JSONObject();
                try {

                    trackObj.put("userid", (cursor.getString(cursor.getColumnIndex("userid"))));
                    trackObj.put("trackid", (cursor.getString(cursor.getColumnIndex("trackscoid"))));
                    trackObj.put("trackobjectid", (cursor.getString(cursor.getColumnIndex("contentid"))));
                    trackObj.put("result", (cursor.getString(cursor.getColumnIndex("showstatus"))));
                    trackObj.put("wmessage", (cursor.getString(cursor.getColumnIndex("wmessage"))));
                    trackObj.put("ruleid", ruleID);
                    trackObj.put("stepid", (cursor.getString(cursor.getColumnIndex("stepid"))));
                    jsonArray.put(trackObj);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        db.close();

        if (jsonArray.length() == 0) {

            returnStr = "";
        } else returnStr = jsonArray.toString();

        return returnStr;
    }


    public String getTrackTemplateAllItemsResult(String trackID, MyLearningModel learningModel) {
        String returnStr = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT TL.contentid, CASE WHEN C.status is NOT NULL OR NOT C.status = '' THEN C.status ELSE TL.status END AS objStatus, C.score FROM " + TBL_TRACKLISTDATA + " TL LEFT OUTER JOIN " + TBL_CMI + " C ON TL.userid = C.userid AND TL.scoid = C.scoid AND TL.siteid = C.siteid WHERE TL.trackscoid = '" + trackID + "' AND TL.userid = '" + learningModel.getUserID() + "' AND TL.siteid = '" + learningModel.getSiteID() + "'";
        JSONArray jsonArray = new JSONArray();
        try {

            Cursor cursor = db.rawQuery(selQuery, null);

            if (cursor.moveToFirst()) {

                while (cursor.moveToNext()) {

                    String ruleID = (cursor.getString(cursor.getColumnIndex("ruleid")));

                    if (ruleID.equalsIgnoreCase("0")) {

                        break;
                    }

                    JSONObject trackObj = new JSONObject();
                    try {


                        String score = (cursor.getString(cursor.getColumnIndex("contentid")));

                        if (score.equalsIgnoreCase("")) {

                            score = "0";
                        }

                        trackObj.put("contentid", (cursor.getString(cursor.getColumnIndex("contentid"))));
                        trackObj.put("status", (cursor.getString(cursor.getColumnIndex("status"))));
                        trackObj.put("score", score);

                        jsonArray.put(trackObj);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

        if (jsonArray.length() == 0) {
            returnStr = "";
        } else returnStr = jsonArray.toString();

        return returnStr;
    }

    public void updateWorkFlowRulesInDBForTrackTemplate(String trackID, String trackItemID, String trackItemState, String wmessage, String ruleID, String stepID, String siteID, String userID) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sqlQuery = "UPDATE " + TBL_TRACKLISTDATA + " SET showstatus = '" + trackItemState + "' , ruleid = '" + ruleID + "' , stepid = '" + stepID + "' wmessage = '" + wmessage + "' WHERE trackscoid = '" + trackID + "'  AND contentid = '" + trackItemID + "'  AND siteid =' " + siteID + "'  AND userid =  '" + userID + "'";

            db.execSQL(sqlQuery);

        } catch (SQLException ex)

        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public void insertUserCredentialsForOfflineLogin(JSONObject jsonObject) throws JSONException {
        boolean isUserExists = false;
        String queryStr = "";


        try {
            SQLiteDatabase db = this.getWritableDatabase();

            queryStr = "SELECT userid FROM " + TBL_OFFLINEUSERS + " WHERE siteid = " + jsonObject.get("siteid") +
                    "  AND siteurl = '" + jsonObject.get("siteurl") + "' AND username = '" + jsonObject.get("username") + "' AND password = '" + jsonObject.get("password") + "'";

            Cursor cursor = db.rawQuery(queryStr, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    isUserExists = true;
                }
            }
        } catch (SQLiteException ex) {

            ex.printStackTrace();
        }

        if (isUserExists) {

            try {
                SQLiteDatabase db = this.getWritableDatabase();
                queryStr = "UPDATE " + TBL_OFFLINEUSERS + " SET userid = " + jsonObject.get("userid") + " , siteid = " + jsonObject.get("siteid") + ", userstatus ='" + jsonObject.get("userstatus") + "', displayname = '" + jsonObject.get("displayname") + "', orgunitid = " + jsonObject.get("orgunitid") + ", username = '" + jsonObject.get("username") + "', password = '" + jsonObject.get("password") + "', siteurl = '" + jsonObject.get("siteurl") + "' WHERE siteid = " + jsonObject.get("siteid") + "  AND siteurl = '" + jsonObject.get("siteurl") + "' AND username = '" + jsonObject.get("username") + "'";

                db.execSQL(queryStr);

            } catch (SQLiteException ex) {

                ex.printStackTrace();
            }
        } else {
            try {
                SQLiteDatabase db = this.getWritableDatabase();

                queryStr = "INSERT INTO " + TBL_OFFLINEUSERS + "( userid, siteid, userstatus, displayname, orgunitid, username, password, siteurl)" + " VALUES ("
                        + jsonObject.get("userid")
                        + ","
                        + jsonObject.get("siteid")
                        + ",'"
                        + jsonObject.get("userstatus")
                        + "','"
                        + jsonObject.get("displayname")
                        + "','"
                        + jsonObject.get("orgunitid")
                        + "','"
                        + jsonObject.get("username")
                        + "','"
                        + jsonObject.get("password")
                        + "','"
                        + jsonObject.get("siteurl")
                        + "')";

                db.execSQL(queryStr);
            } catch (SQLiteException ex) {

                ex.printStackTrace();
            }
        }
    }

    public JSONObject checkOfflineUserCredintials(JSONObject jsonObject) throws JSONException {
        JSONObject jsonCred = new JSONObject();

        String sqlQuery = "SELECT * FROM " + TBL_OFFLINEUSERS + " WHERE siteid = " + jsonObject.get("siteid") + "  AND siteurl = '" + jsonObject.get("siteurl") + "' AND username = '" + jsonObject.get("username") + "' AND password = '" + jsonObject.get("password") + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        try {

            Cursor cursor = db.rawQuery(sqlQuery, null);

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    jsonCred.put("userid", (cursor.getString(cursor.getColumnIndex("userid"))));
                    jsonCred.put("siteid", (cursor.getString(cursor.getColumnIndex("siteid"))));
                    jsonCred.put("userstatus", (cursor.getString(cursor.getColumnIndex("userstatus"))));
                    jsonCred.put("displayname", (cursor.getString(cursor.getColumnIndex("displayname"))));
                    jsonCred.put("orgunitid", (cursor.getString(cursor.getColumnIndex("orgunitid"))));
                    jsonCred.put("username", (cursor.getString(cursor.getColumnIndex("username"))));
                    jsonCred.put("password", (cursor.getString(cursor.getColumnIndex("password"))));
                    jsonCred.put("siteurl", (cursor.getString(cursor.getColumnIndex("siteurl"))));
                }
            }
            cursor.close();
            db.close();
        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }

        return jsonCred;
    }

    public void setCompleteMethods(Context context, MyLearningModel learningModel) {

        CMIModel cmiDetails = new CMIModel();
        cmiDetails.set_siteId(learningModel.getSiteID());
        cmiDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
        cmiDetails
                .set_startdate(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
        cmiDetails
                .set_datecompleted(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));
        cmiDetails.set_scoId(Integer.parseInt(learningModel.getScoId()));
        cmiDetails.set_isupdate("false");
        cmiDetails.set_status("Completed");
        cmiDetails.set_seqNum("0");
        cmiDetails.set_timespent("");
        cmiDetails.set_objecttypeid(learningModel.getObjectId());
        cmiDetails.set_sitrurl(learningModel.getSiteURL());
        int objlastAttempt = getLatestAttempt(learningModel.getScoId(), learningModel.getUserID(),
                learningModel.getSiteID());
        LearnerSessionModel nsessionDetails = new LearnerSessionModel();
        nsessionDetails.setSiteID(learningModel.getSiteID());
        nsessionDetails.setUserID(learningModel.getUserID());
        nsessionDetails.setScoID(learningModel.getScoId());
        nsessionDetails.setAttemptNumber("" + objlastAttempt + 1);
        nsessionDetails
                .setSessionDateTime(getCurrentDateTime("yyyy-MM-dd HH:mm:ss"));

        if (learningModel.getIsDownloaded().equals("true")) {
            if (learningModel.getWresult().contains("disabled")) {
                if (learningModel.getContentExpire().equals("")) {
//                    ShowAlert.alertOK(DownloadList.this,
//                            R.string.alert, resultwmessage,
//                            R.string.alert_btntext_OK, false);
                }
            } else {
                insertCMI(cmiDetails, true);
                insertUserSession(nsessionDetails);
//                loadMyLearningData(root);
//                getListView().setSelection(position);
                Log.d("SetComplete click",
                        "Status updated to DB");
//                Toast.makeText(
//                        DownloadList.this,
//                        getString(R.string.status_update_success),
//                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isNetworkConnectionAvailable(context, -1)) {

                String paramsString = "ContentID="
                        + learningModel.getContentID() + "&UserID=" + learningModel.getUserID()
                        + "&ScoId=" + learningModel.getScoId();

                paramsString = paramsString.replace(" ", "%20");

                appController.setWebApiUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));

                InputStream inputStream = wap.callWebAPIMethod(appController.getWebApiUrl(), "MobileLMS",
                        "MobileSetStatusCompleted", appController.getAuthentication(), paramsString);

                try {
                    if (inputStream != null) {

                        String result = convertStreamToString(inputStream);
                        inputStream.close();
                        if (result.toLowerCase().contains(
                                "completed")) {
                            insertCMI(cmiDetails, true);
                            insertUserSession(nsessionDetails);
                            Log.d("HERE",
                                    "loadMyLearningData 8");

//                loadMyLearningData(root); uncomment here

                            Log.d("SetComplete click",
                                    "Status updated to DB");
//                            Toast.makeText(
//                                    context, context.
//                                            getString(R.string.status_update_success),
//                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(
//                                    context, context.getString(R.string.status_update_fail),
//                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                        Toast.makeText(
//                                context, context.
//                                        getString(R.string.status_update_fail),
//                                Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalStateException | IOException e) {
                    e.printStackTrace();
                    Log.d("SetComplete click",
                            "WebAPI Response error");
                }

            } else {
                //jhkhjkhj
                insertCMI(cmiDetails, true);
                insertUserSession(nsessionDetails);
                Log.d("HERE", "loadMyLearningData 9");

//                loadMyLearningData(root); uncomment here

                Log.d("SetComplete click",
                        "Status updated to DB");
//                Toast.makeText(context,
//                        context.getString(R.string.status_update_success),
//                        Toast.LENGTH_SHORT).show();
            }
        }


    }


    public void injectTinCanConfigurationValues(JSONObject jsonObj, String siteID) throws JSONException {

        String authKey = jsonObj.getString("lrsauthorization");
        String authPassword = jsonObj.getString("lrsauthorizationpassword");

        String base64lrsAuthKey = authKey + ":" + authPassword;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TBL_TINCAN + " WHERE siteid = " + siteID);

        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();
            contentValues.put("istincan", jsonObj.get("istincan").toString());
            contentValues.put("lrsendpoint", jsonObj.get("lrsendpoint").toString());
            contentValues.put("lrsauthorization", authKey);
            contentValues.put("lrsauthorizationpassword", authPassword);
            contentValues.put("enabletincansupportforco", jsonObj.get("enabletincansupportforco").toString());
            contentValues.put("enabletincansupportforao", jsonObj.get("enabletincansupportforao").toString());
            contentValues.put("enabletincansupportforlt", jsonObj.get("enabletincansupportforlt").toString());
            contentValues.put("base64lrsAuthKey", base64lrsAuthKey);
            contentValues.put("siteid", siteID);

            db.insert(TBL_TINCAN, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public JSONObject gettTinCanConfigurationValues(String siteID) throws JSONException {


        JSONObject jsonObject = new JSONObject();


        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from TINCAN where siteid = " + siteID;

        Cursor cursor = db.rawQuery(query, null);
        try {

            if (cursor != null) {

                while (cursor.moveToFirst()) {
                    jsonObject.put("istincan", cursor.getString(cursor.getColumnIndex("istincan")));
                    jsonObject.put("lrsendpoint", cursor.getString(cursor.getColumnIndex("lrsendpoint")));
                    jsonObject.put("lrsauthorization", cursor.getString(cursor.getColumnIndex("lrsauthorization")));
                    jsonObject.put("lrsauthorizationpassword", cursor.getString(cursor.getColumnIndex("lrsauthorizationpassword")));
                    jsonObject.put("enabletincansupportforco", cursor.getString(cursor.getColumnIndex("enabletincansupportforco")));
                    jsonObject.put("enabletincansupportforao", cursor.getString(cursor.getColumnIndex("enabletincansupportforao")));
                    jsonObject.put("enabletincansupportforlt", cursor.getString(cursor.getColumnIndex("enabletincansupportforlt")));
                    jsonObject.put("base64lrsAuthKey", cursor.getString(cursor.getColumnIndex("base64lrsAuthKey")));
                    jsonObject.put("siteid", cursor.getString(cursor.getColumnIndex("siteid")));
                    break;
                }

            }
            cursor.close();
            db.close();

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

        return jsonObject;
    }

    public boolean isSubscribedContent(MyLearningModel learningModel) {
        boolean isSubscribedContent = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String strSelQuery = "SELECT contentid FROM " + TBL_DOWNLOADDATA
                    + " WHERE userid ='" + learningModel.getUserID() + "' AND contentid ='"
                    + learningModel.getContentID() + "' AND siteid ='" + learningModel.getSiteID() + "'";
            Cursor cursor = db.rawQuery(strSelQuery, null);
            if (cursor.getCount() > 0) {
                isSubscribedContent = true;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            if (db.isOpen()) {
                db.close();
            }
            Log.d("isContentSubscribed", e.getMessage());

        }
        return isSubscribedContent;
    }

    public boolean saveNewlySubscribedContentMetadata(JSONObject jsonObject) throws JSONException {

        boolean isInserted = false;
        Log.d("saveNewlySubscribed DB", " " + jsonObject);
        SQLiteDatabase db = this.getWritableDatabase();

        JSONArray jsonTableAry = jsonObject.getJSONArray("table");
        // for deleting records in table for respective table


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            Log.d(TAG, "injectMyLearningData: " + jsonMyLearningColumnObj);

            MyLearningModel myLearningModel = new MyLearningModel();
            ContentValues contentValues = null;


            //sitename
            if (jsonMyLearningColumnObj.has("sitename")) {

                myLearningModel.setSiteName(jsonMyLearningColumnObj.get("sitename").toString());
            }
            // siteurl
            if (jsonMyLearningColumnObj.has("siteurl")) {

                myLearningModel.setSiteURL(jsonMyLearningColumnObj.get("siteurl").toString());

            }
            // siteid
            if (jsonMyLearningColumnObj.has("siteid")) {

                myLearningModel.setSiteID(jsonMyLearningColumnObj.get("siteid").toString());

            }
            // userid
            if (jsonMyLearningColumnObj.has("userid")) {

                myLearningModel.setUserID(jsonMyLearningColumnObj.get("userid").toString());

            }
            // coursename


            if (jsonMyLearningColumnObj.has("name")) {

                myLearningModel.setCourseName(jsonMyLearningColumnObj.get("name").toString());

            }

            // shortdes
            if (jsonMyLearningColumnObj.has("shortdescription")) {


                Spanned result = fromHtml(jsonMyLearningColumnObj.get("shortdescription").toString());

                myLearningModel.setShortDes(result.toString());

            }

            String authorName = "";
            if (jsonMyLearningColumnObj.has("contentauthordisplayname")) {
                authorName = jsonMyLearningColumnObj.getString("contentauthordisplayname");

            }

            if (authorName.length() != 0) {
                myLearningModel.setAuthor(authorName);
            } else {
                // author
                if (jsonMyLearningColumnObj.has("author")) {

                    myLearningModel.setAuthor(jsonMyLearningColumnObj.get("author").toString());

                }
            }


            // contentID
            if (jsonMyLearningColumnObj.has("contentid")) {

                myLearningModel.setContentID(jsonMyLearningColumnObj.get("contentid").toString());

            }
            // createddate
            if (jsonMyLearningColumnObj.has("createddate")) {

                myLearningModel.setCreatedDate(jsonMyLearningColumnObj.get("createddate").toString());

            }
            // displayName

            myLearningModel.setDisplayName(appUserModel.getDisplayName());
            // durationEndDate
            if (jsonMyLearningColumnObj.has("durationenddate")) {

                myLearningModel.setDurationEndDate(jsonMyLearningColumnObj.get("durationenddate").toString());

            }
            // objectID
            if (jsonMyLearningColumnObj.has("objectid")) {

                myLearningModel.setObjectId(jsonMyLearningColumnObj.get("objectid").toString());

            }
            // thumbnailimagepath
            if (jsonMyLearningColumnObj.has("thumbnailimagepath")) {

                String imageurl = jsonMyLearningColumnObj.getString("thumbnailimagepath");


                if (isValidString(imageurl)) {

                    myLearningModel.setThumbnailImagePath(imageurl);
                    String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + myLearningModel.getContentID() + "/" + imageurl;
                    myLearningModel.setImageData(imagePathSet);


                } else {
                    if (jsonMyLearningColumnObj.has("contenttypethumbnail")) {
                        String imageurlContentType = jsonMyLearningColumnObj.getString("contenttypethumbnail");
                        if (isValidString(imageurlContentType)) {
                            String imagePathSet = myLearningModel.getSiteURL() + "/content/sitefiles/Images/" + imageurlContentType;
                            myLearningModel.setImageData(imagePathSet);

                        }
                    }


                }

                // relatedcontentcount
                if (jsonMyLearningColumnObj.has("relatedconentcount")) {

                    myLearningModel.setRelatedContentCount(jsonMyLearningColumnObj.get("relatedconentcount").toString());

                }
                // isDownloaded
                if (jsonMyLearningColumnObj.has("isdownloaded")) {

                    myLearningModel.setIsDownloaded(jsonMyLearningColumnObj.get("isdownloaded").toString());

                }
                // courseattempts
                if (jsonMyLearningColumnObj.has("courseattempts")) {

                    myLearningModel.setCourseAttempts(jsonMyLearningColumnObj.get("courseattempts").toString());

                }
                // objecttypeid
                if (jsonMyLearningColumnObj.has("objecttypeid")) {

                    myLearningModel.setObjecttypeId(jsonMyLearningColumnObj.get("objecttypeid").toString());

                }
                // scoid
                if (jsonMyLearningColumnObj.has("scoid")) {

                    myLearningModel.setScoId(jsonMyLearningColumnObj.get("scoid").toString());

                }
                // startpage
                if (jsonMyLearningColumnObj.has("startpage")) {

                    myLearningModel.setStartPage(jsonMyLearningColumnObj.get("startpage").toString());

                }
                // status
                if (jsonMyLearningColumnObj.has("corelessonstatus")) {

                    myLearningModel.setStatus(jsonMyLearningColumnObj.get("corelessonstatus").toString());

                }
                // userName
                myLearningModel.setUserName(appUserModel.getUserName());
                // longdes
                if (jsonMyLearningColumnObj.has("longdescription")) {

                    Spanned result = fromHtml(jsonMyLearningColumnObj.get("longdescription").toString());

                    myLearningModel.setShortDes(result.toString());
                    myLearningModel.setLongDes(result.toString());

                }
                // typeofevent
                if (jsonMyLearningColumnObj.has("typeofevent")) {

                    int typeoFEvent = Integer.parseInt(jsonMyLearningColumnObj.get("typeofevent").toString());

                    myLearningModel.setTypeofevent(typeoFEvent);

                }

                // medianame
                if (jsonMyLearningColumnObj.has("medianame")) {
                    String medianame = "";

                    if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {
                        if (jsonMyLearningColumnObj.getString("medianame").equalsIgnoreCase("test")) {
                            medianame = "Assessment(Test)";

                        } else {
                            medianame = jsonMyLearningColumnObj.get("medianame").toString();
                        }
                    } else {
                        if (myLearningModel.getTypeofevent() == 2) {
                            medianame = "Event (Online)";


                        } else if (myLearningModel.getTypeofevent() == 1) {
                            medianame = "Event (Face to Face)";

                        }
                    }

                    myLearningModel.setMediaName(medianame);

                }       // ratingid
                if (jsonMyLearningColumnObj.has("ratingid")) {

                    myLearningModel.setRatingId(jsonMyLearningColumnObj.get("ratingid").toString());

                }
                // publishedDate
                if (jsonMyLearningColumnObj.has("publisheddate")) {

                    myLearningModel.setPublishedDate(jsonMyLearningColumnObj.get("publisheddate").toString());

                }
                // eventstarttime
                if (jsonMyLearningColumnObj.has("eventstartdatetime")) {

                    myLearningModel.setEventstartTime(jsonMyLearningColumnObj.get("eventstartdatetime").toString());

                }
                // eventendtime
                if (jsonMyLearningColumnObj.has("eventenddatetime")) {

                    myLearningModel.setEventendTime(jsonMyLearningColumnObj.get("eventenddatetime").toString());

                }

                // mediatypeid
                if (jsonMyLearningColumnObj.has("mediatypeid")) {

                    myLearningModel.setMediatypeId(jsonMyLearningColumnObj.get("mediatypeid").toString());

                }
                // dateassigned
                if (jsonMyLearningColumnObj.has("dateassigned")) {

                    myLearningModel.setDateAssigned(jsonMyLearningColumnObj.get("dateassigned").toString());

                }
                // keywords
                if (jsonMyLearningColumnObj.has("seokeywords")) {

                    myLearningModel.setKeywords(jsonMyLearningColumnObj.get("seokeywords").toString());

                }
                // eventcontentid
                if (jsonMyLearningColumnObj.has("eventcontentid")) {

                    myLearningModel.setEventContentid(jsonMyLearningColumnObj.get("eventcontentid").toString());

                }
                // eventAddedToCalender
                myLearningModel.setEventAddedToCalender(false);

//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                String checkfalseOrTrue = jsonMyLearningColumnObj.get("startdate").toString();
//                if (checkfalseOrTrue.equalsIgnoreCase("false")) {
//                    myLearningModel.setEventAddedToCalender(false);
//                } else {
//                    myLearningModel.setEventAddedToCalender(true);
//                }
//            }
                // isExpiry
                myLearningModel.setIsExpiry("false");
//            if (jsonMyLearningColumnObj.has("startdate")) {
//
//                myLearningModel.setIsExpiry(jsonMyLearningColumnObj.get("startdate").toString());
//
//            }
                // locationname
                if (jsonMyLearningColumnObj.has("locationname")) {

                    myLearningModel.setLocationName(jsonMyLearningColumnObj.get("locationname").toString());

                }
                // timezone
                if (jsonMyLearningColumnObj.has("timezone")) {

                    myLearningModel.setTimeZone(jsonMyLearningColumnObj.get("timezone").toString());

                }
                // participanturl
                if (jsonMyLearningColumnObj.has("participanturl")) {

                    myLearningModel.setParticipantUrl(jsonMyLearningColumnObj.get("participanturl").toString());

                }
                // password

                myLearningModel.setPassword(appUserModel.getPassword());

                // isListView
                if (jsonMyLearningColumnObj.has("bit5")) {

                    myLearningModel.setIsListView(jsonMyLearningColumnObj.get("bit5").toString());

                }

                // joinurl
                if (jsonMyLearningColumnObj.has("joinurl")) {

                    myLearningModel.setJoinurl(jsonMyLearningColumnObj.get("joinurl").toString());

                }

                // offlinepath
                if (jsonMyLearningColumnObj.has("objecttypeid") && jsonMyLearningColumnObj.has("startpage")) {
                    String objtId = jsonMyLearningColumnObj.get("objecttypeid").toString();
                    String startPage = jsonMyLearningColumnObj.get("startpage").toString();
                    String contentid = jsonMyLearningColumnObj.get("contentid").toString();
                    String downloadDestFolderPath = dbctx.getExternalFilesDir(null)
                            + "/Mydownloads/Contentdownloads" + "/" + contentid;

                    String finalDownloadedFilePath = downloadDestFolderPath + "/" + startPage;

                    myLearningModel.setOfflinepath(finalDownloadedFilePath);
                }
//


                // wresult
                if (jsonMyLearningColumnObj.has("wresult")) {

                    myLearningModel.setWresult(jsonMyLearningColumnObj.get("wresult").toString());

                }
                // wmessage
                if (jsonMyLearningColumnObj.has("wmessage")) {

                    myLearningModel.setWmessage(jsonMyLearningColumnObj.get("wmessage").toString());

                }

                // presenter
                if (jsonMyLearningColumnObj.has("presenter")) {

                    myLearningModel.setPresenter(jsonMyLearningColumnObj.get("presenter").toString());

                }

                //sitename
                if (jsonMyLearningColumnObj.has("progress")) {

                    myLearningModel.setProgress(jsonMyLearningColumnObj.get("progress").toString());
                    if (myLearningModel.getStatus().equalsIgnoreCase("Not Started")) {
                        ejectRecordsinCmi(myLearningModel);
                    }
                }

                getTrackScoIdsAndDeleteCMI(myLearningModel);

                injectMyLearningIntoTable(myLearningModel, true);

                isInserted = true;
            }

            db.close();
        }

        return isInserted;
    }

    public void getTrackScoIdsAndDeleteCMI(MyLearningModel learningModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        String selQuery = "SELECT ScoId,objecttypeid FROM TRACKOBJECTS WHERE userid="
                + learningModel.getUserID()
                + " AND trackscoid="
                + learningModel.getScoId()
                + " AND siteid=" + learningModel.getSiteID();
        String whereCLause = "";
        Cursor cursorTrackObjects = db.rawQuery(selQuery,
                null);
        if (cursorTrackObjects != null
                & cursorTrackObjects.getCount() > 0) {
            if (cursorTrackObjects.moveToFirst()) {
                do {
                    whereCLause = "WHERE userid ='"
                            + learningModel.getUserID()
                            + "' AND scoid='"
                            + cursorTrackObjects
                            .getString(0)
                            + "' AND siteid='" + learningModel.getSiteID()
                            + "'";

                    String strDelete = "DELETE FROM CMI " + whereCLause;

                    db.execSQL(strDelete);
                    String strDeleteUser = "DELETE FROM USERSESSION " + whereCLause;

                    db.execSQL(strDeleteUser);


                    if (cursorTrackObjects.getString(1)
                            .equals("9")
                            || cursorTrackObjects
                            .getString(1).equals(
                                    "8")) {
//						deleteRecords(TBL_STUDENTRESPONSES,
//								whereCLause);
                        String strDeleteUserRes = "DELETE FROM STUDENTRESPONSES " + whereCLause;

                        db.execSQL(strDeleteUserRes);
                    }

                } while (cursorTrackObjects.moveToNext());
            }
        }
//		cursorTrackObjects.close();
    }

    public void updateContenToCatalog(MyLearningModel myLearningModel) {
        String strUpdateML = "UPDATE " + TBL_CATALOGDATA + " SET isaddedtomylearning=1"
                + " WHERE siteid ='" + myLearningModel.getSiteID() + "' AND userid ='"
                + myLearningModel.getUserID() + "' AND scoid='" + myLearningModel.getScoId() + "'";
        try {
            executeQuery(strUpdateML);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



