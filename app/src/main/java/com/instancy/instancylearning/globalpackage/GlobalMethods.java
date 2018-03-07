package com.instancy.instancylearning.globalpackage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.asynchtask.SetCourseCompleteSynchTask;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.mainactivities.AdvancedWebCourseLaunch;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.LearnerSessionModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.mylearning.EventTrackList_Activity;
import com.instancy.instancylearning.mylearning.MyLearningDetail_Activity;
import com.instancy.instancylearning.mylearning.MyLearningFragment;
import com.instancy.instancylearning.mylearning.Reports_Activity;
import com.instancy.instancylearning.mylearning.TrackList_Activity;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.isCourseEndDateCompleted;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.replace;
import static com.instancy.instancylearning.utils.Utilities.showToast;

/**
 * Created by Upendranath on 7/7/2017 Working on InstancyLearning.
 */

public class GlobalMethods {

    static String TAG = GlobalMethods.class.getSimpleName();

    private static DatabaseHandler databaseH;
    PreferencesManager preferencesManager;

    public static void launchCourseViewFromGlobalClass(MyLearningModel myLearningModel, Context context) {
        databaseH = new DatabaseHandler(context);
        File myFile = new File(myLearningModel.getOfflinepath());
        PreferencesManager.initializeInstance(context);
        String userLoginId = PreferencesManager.getInstance().getStringValue(StaticValues.KEY_USERLOGINID);
        String userName = PreferencesManager.getInstance().getStringValue(StaticValues.KEY_USERNAME);
        String offlinePath = "";

        String endDuarationDate = myLearningModel.getDurationEndDate();

//        String endDuarationDate = "2018-04-15 21:00:00";

        if (isValidString(endDuarationDate)) {

            boolean isCompleted = isCourseEndDateCompleted(endDuarationDate);

            if (isCompleted) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.mycatalog_enddurationdate))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                dialog.dismiss();
                                // remove event from android calander


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                return;
            }

        }

        if (myFile.exists()) {

            databaseH = new DatabaseHandler(context);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = databaseH.gettTinCanConfigurationValues(myLearningModel.getSiteID());
                Log.d(TAG, "TIN CAN OPTIONS: " + jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String lrsEndPoint = "";
            String lrsActor = "{ \"mbox\":[\"mailto:" + userLoginId + "\"], \"name\":[\"" + userName + "\"] }";
            String lrsAuthorizationKey = "";
            String enabletincanSupportforco = "";
            String enabletincanSupportforao = "";
            String enabletincanSupportforlt = "";
            String isTinCan = "";
            String autKey = "";
            try {

                if (jsonObject.length() != 0) {
                    lrsEndPoint = jsonObject.getString("lrsendpoint");
                    autKey = jsonObject.getString("base64lrsAuthKey");
                    enabletincanSupportforco = jsonObject.getString("enabletincansupportforco");
                    enabletincanSupportforao = jsonObject.getString("enabletincansupportforao");
                    enabletincanSupportforlt = jsonObject.getString("enabletincansupportforlt");
                    isTinCan = jsonObject.getString("istincan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            byte[] encrpt = new byte[0];
            try {
                encrpt = autKey.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String base64 = Base64.encodeToString(encrpt, Base64.NO_WRAP);
            lrsAuthorizationKey = "Basic " + base64;
            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                offlinePath = "file://" + myLearningModel.getOfflinepath();
                // LRS is implemented completed disabled for some resons
                if (isTinCan.toLowerCase().equalsIgnoreCase("true")) {
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") && enabletincanSupportforco.toLowerCase().equalsIgnoreCase("true")) {

                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("9") && enabletincanSupportforao.toLowerCase().equalsIgnoreCase("true")) {
                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && enabletincanSupportforlt.toLowerCase().equalsIgnoreCase("true")) {
                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";
                    } else {
                        databaseH.preFunctionalityBeforeNonLRSOfflineContentPathCreation(myLearningModel, context);
//
                        offlinePath = databaseH.generateOfflinePathForCourseView(myLearningModel, context);
                    }
                } else {

                    databaseH.preFunctionalityBeforeNonLRSOfflineContentPathCreation(myLearningModel, context);
//
                    offlinePath = databaseH.generateOfflinePathForCourseView(myLearningModel, context);

                }

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {

                String encodedString = "";

                try {
                    encodedString = URLEncoder.encode(lrsActor, "utf-8").replace("+", "%20").replace("%3A", ":");
//                            encodedString = URLEncoder.encode(lrsActor, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                offlinePath = "file://" + myLearningModel.getOfflinepath();
                offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + encodedString + "&cid=0&nativeappURL=true&IsInstancyContent=true";

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {

                offlinePath = "file://"
                        + context.getExternalFilesDir(null)
                        + "/Mydownloads/Content/LaunchCourse.html?contentpath=file://"
                        + myLearningModel.getOfflinepath();


            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {

                String cerName = myLearningModel.getContentID() + "_Certificate";

                offlinePath = offlinePath + "/" + myLearningModel.getUserID() + "/" + cerName + ".pdf";

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                offlinePath = "file://" + myLearningModel.getOfflinepath();
                if (myLearningModel.getStatus().equalsIgnoreCase("Not Started") || myLearningModel.getStatus().equalsIgnoreCase("")) {

                    CMIModel model = new CMIModel();
                    model.set_datecompleted("");
                    model.set_siteId(myLearningModel.getSiteID());
                    model.set_userId(Integer.parseInt(myLearningModel.getUserID()));
                    model.set_startdate(GetCurrentDateTime());
                    model.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
                    model.set_isupdate("false");
                    model.set_status("In Progress");
                    model.set_seqNum("0");
                    model.set_timespent("");
                    model.set_objecttypeid(myLearningModel.getObjecttypeId());
                    model.set_sitrurl(myLearningModel.getSiteURL());
                    databaseH.injectIntoCMITable(model, "false");

                    int attempts = databaseH.getLatestAttempt(myLearningModel);

                    LearnerSessionModel learnerSessionModel = new LearnerSessionModel();

                    learnerSessionModel.setSiteID(myLearningModel.getSiteID());
                    learnerSessionModel.setUserID(myLearningModel.getUserID());
                    learnerSessionModel.setScoID(myLearningModel.getScoId());
                    learnerSessionModel.setAttemptNumber("" + (attempts + 1));
                    learnerSessionModel.setSessionDateTime(GetCurrentDateTime());

                    databaseH.insertUserSession(learnerSessionModel);


                }

            } else {

                offlinePath = "file://" + myLearningModel.getOfflinepath();

            }

            String offlinePathEncode = offlinePath.replaceAll(" ", "%20");

            if (offlinePathEncode.endsWith(".pdf")) {
                Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                pdfIntent.putExtra("PDF_URL", offlinePathEncode);
                pdfIntent.putExtra("ISONLINE", "NO");
                pdfIntent.putExtra("myLearningDetalData", myLearningModel);
                pdfIntent.putExtra("PDF_FILENAME", myLearningModel.getCourseName());
//                context.startActivity(pdfIntent);
                ((Activity) context).startActivityForResult(pdfIntent, COURSE_CLOSE_CODE);

            } else if (offlinePathEncode.toLowerCase().contains(".ppt")
                    || offlinePathEncode.toLowerCase().contains(".pptx")) {

                offlinePathEncode = offlinePathEncode.replace("file://", "");

                try {

                    offlinePathEncode = URLDecoder.decode(offlinePath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                File file = new File(offlinePathEncode);
                Intent intent2 = new Intent();
                intent2.setAction(android.content.Intent.ACTION_VIEW);
                intent2.setDataAndType(
                        Uri.fromFile(file),
                        "application/mspowerpoint,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation");
                try {
                    context.startActivity(intent2);

                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_pp),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

            } else if (offlinePathEncode.toLowerCase().contains(".doc")
                    || offlinePathEncode.toLowerCase().contains(".docx")) {

                offlinePathEncode = offlinePathEncode.replace("file://", "");

                File file = new File(offlinePathEncode);
                Intent intent3 = new Intent();
                intent3.setAction(android.content.Intent.ACTION_VIEW);
                intent3.setDataAndType(
                        Uri.fromFile(file),
                        "application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                try {
                    context.startActivity(intent3);

                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_word),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            } else if (offlinePathEncode.toLowerCase().contains(".xlsx")
                    || offlinePathEncode.toLowerCase().contains(".xls")) {
                offlinePathEncode = offlinePathEncode.replace(" file://", "");

                File file = new File(offlinePathEncode);
                Intent intent4 = new Intent();
                intent4.setAction(android.content.Intent.ACTION_VIEW);
                intent4.setDataAndType(
                        Uri.fromFile(file),
                        "application/excel,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                try {
                    context.startActivity(intent4);

                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_excel),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }
            } else if (offlinePathEncode.toLowerCase().contains(".mpp")) {
                offlinePathEncode = offlinePathEncode.replace("file://", "");

                File file = new File(offlinePathEncode);
                Intent intent4 = new Intent();
                intent4.setAction(android.content.Intent.ACTION_VIEW);
                intent4.setDataAndType(
                        Uri.fromFile(file),
                        "application/vnd.ms-project, application/msproj, application/msproject, application/x-msproject, application/x-ms-project, application/x-dos_ms_project, application/mpp, zz-application/zz-winassoc-mpp");

                try {
                    context.startActivity(intent4);
                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_mpp),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();


                }


            } else if (offlinePathEncode.toLowerCase().contains(".visio")
                    || offlinePathEncode.toLowerCase().contains(".vsd")) {
                offlinePathEncode = offlinePathEncode.replace("file://", "");

                File file = new File(offlinePathEncode);
                Intent intent4 = new Intent();
                intent4.setAction(android.content.Intent.ACTION_VIEW);
                intent4.setDataAndType(
                        Uri.fromFile(file),
                        "application/visio, application/x-visio, application/vnd.visio, application/visio.drawing, application/vsd, application/x-vsd, image/x-vsd, zz-application/zz-winassoc-vsd");

                try {
                    context.startActivity(intent4);
                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_mpp),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();


                }

            } else if (offlinePathEncode.toLowerCase().contains(".txt")) {
                offlinePathEncode = offlinePathEncode.replace("file://", "");

                File file = new File(offlinePathEncode);
                Intent intent4 = new Intent();
                intent4.setAction(android.content.Intent.ACTION_VIEW);
                intent4.setDataAndType(Uri.fromFile(file), "text/plain");
                try {
                    context.startActivity(intent4);

                } catch (ActivityNotFoundException e) {

                    Toast toast = Toast.makeText(context,
                            context.getString(R.string.toast_no_application_txt),
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else {
                databaseH.insertCmiIsUpdate(myLearningModel);
                Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                iWeb.putExtra("COURSE_URL", offlinePathEncode);
                iWeb.putExtra("myLearningDetalData", myLearningModel);
                ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);

//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(offlinePathEncode));
//                ((Activity) context).startActivity(i);

            }

            /// ONLINE
        } else {

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
                Intent intentDetail = new Intent(context, TrackList_Activity.class);
                intentDetail.putExtra("myLearningDetalData", myLearningModel);
                intentDetail.putExtra("ISTRACKLIST", true);
                ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
//                context.startActivity(intentDetail);
            } else {
                if (isNetworkConnectionAvailable(context, -1)) {
                    String urlForView = "";

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {


                        if (myLearningModel.getStatus().equalsIgnoreCase("Not Started") || myLearningModel.getStatus().equalsIgnoreCase("")) {

                            CMIModel model = new CMIModel();
                            model.set_datecompleted("");
                            model.set_siteId(myLearningModel.getSiteID());
                            model.set_userId(Integer.parseInt(myLearningModel.getUserID()));
                            model.set_startdate(GetCurrentDateTime());
                            model.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
                            model.set_isupdate("false");
                            model.set_status("In Progress");
                            model.set_seqNum("0");
                            model.set_timespent("");
                            model.set_objecttypeid(myLearningModel.getObjecttypeId());
                            model.set_contentId(myLearningModel.getContentID());
                            model.set_sitrurl(myLearningModel.getSiteURL());
                            databaseH = new DatabaseHandler(context);
                            databaseH.injectIntoCMITable(model, "false");

                            int attempts = databaseH.getLatestAttempt(myLearningModel);

                            LearnerSessionModel learnerSessionModel = new LearnerSessionModel();

                            learnerSessionModel.setSiteID(myLearningModel.getSiteID());
                            learnerSessionModel.setUserID(myLearningModel.getUserID());
                            learnerSessionModel.setScoID(myLearningModel.getScoId());
                            learnerSessionModel.setAttemptNumber("" + (attempts + 1));
                            learnerSessionModel.setSessionDateTime(GetCurrentDateTime());

                            databaseH.insertUserSession(learnerSessionModel);

                        }

                    }
                    databaseH = new DatabaseHandler(context);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = databaseH.gettTinCanConfigurationValues(myLearningModel.getSiteID());
                        Log.d(TAG, "TIN CAN OPTIONS: " + jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String lrsEndPoint = "";
                    String lrsActor = "{ \"mbox\":[\"mailto:" + userLoginId + "\"], \"name\":[\"" + userName + "\"] }";
                    String lrsAuthorizationKey = "";
                    String enabletincanSupportforco = "";
                    String enabletincanSupportforao = "";
                    String enabletincanSupportforlt = "";
                    String isTinCan = "";
                    String autKey = "";
                    try {

                        if (jsonObject.length() != 0) {
                            lrsEndPoint = jsonObject.getString("lrsendpoint");
                            autKey = jsonObject.getString("base64lrsAuthKey");
                            enabletincanSupportforco = jsonObject.getString("enabletincansupportforco");
                            enabletincanSupportforao = jsonObject.getString("enabletincansupportforao");
                            enabletincanSupportforlt = jsonObject.getString("enabletincansupportforlt");
                            isTinCan = jsonObject.getString("istincan");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    byte[] encrpt = new byte[0];
                    try {
                        encrpt = autKey.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String base64 = Base64.encodeToString(encrpt, Base64.NO_WRAP);
                    lrsAuthorizationKey = "Basic " + base64;
//                      String basicNewKey=base64.replace("\n","");
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {


                        urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?URL=/Content/SiteFiles/"
                                + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&trackinguserid=" + myLearningModel.getUserID();

                        if (isTinCan.toLowerCase().equalsIgnoreCase("true")) {

                            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") && enabletincanSupportforco.toLowerCase().equalsIgnoreCase("true")) {

                                urlForView = urlForView + "&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor;

                            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("9") && enabletincanSupportforao.toLowerCase().equalsIgnoreCase("true")) {
                                urlForView = urlForView + "&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor;

                            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && enabletincanSupportforlt.toLowerCase().equalsIgnoreCase("true")) {
                                urlForView = urlForView + "&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor;

                            }
                        }
                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36")) //14 21 36
                    {
                        urlForView = myLearningModel.getSiteURL() + "/Content/Sitefiles/" + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage();
                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                        urlForView = myLearningModel.getStartPage();
                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {

                        urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?path=/Content/SiteFiles/"
                                + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();


                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {
                        urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?Path=" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&CanTrack=Yes" + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
                        String encodedString = "";

                        try {
                            encodedString = URLEncoder.encode(lrsActor, "utf-8").replace("+", "%20").replace("%3A", ":");
//                            encodedString = URLEncoder.encode(lrsActor, "UTF-8");

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        urlForView = myLearningModel.getSiteURL() + "/Content/SiteFiles/" + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage() + "?endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + encodedString + "&registration=&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=YES" + "&nativeappURL=true";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {
                        String cerName = myLearningModel.getContentID() + "_Certificate";
                        urlForView = myLearningModel.getSiteURL() + "/content/sitefiles/" + myLearningModel.getSiteID() + "/UserCertificates/" + myLearningModel.getUserID() + "/" + cerName + ".pdf";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("20")) {

                        urlForView = myLearningModel.getSiteURL() + "/content/sitefiles/" + myLearningModel.getContentID() + "/glossary_english.html";

                    } else {


                    }
                    String encodedStr = "";
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {
                        encodedStr = replace(urlForView);
                    } else {
                        encodedStr = replace(urlForView.toLowerCase());
                    }


                    Log.d("DBG", "launchCourseView: " + encodedStr);
                    if (encodedStr.endsWith(".pdf")) {
                        Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                        pdfIntent.putExtra("PDF_URL", encodedStr);
                        pdfIntent.putExtra("ISONLINE", "YES");
                        pdfIntent.putExtra("PDF_FILENAME", myLearningModel.getCourseName());
                        pdfIntent.putExtra("myLearningDetalData", myLearningModel);
//                        context.startActivity(pdfIntent);
                        ((Activity) context).startActivityForResult(pdfIntent, COURSE_CLOSE_CODE);

                    } else if (encodedStr.toLowerCase().contains(".ppt")
                            || encodedStr.toLowerCase().contains(".pptx")) {

                        encodedStr = encodedStr.replace("file://", "");

//                        try {
//
//                            encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        File file = new File(encodedStr);
//                        Intent intent2 = new Intent();
//                        intent2.setAction(android.content.Intent.ACTION_VIEW);
//                        intent2.setDataAndType(
//                                Uri.fromFile(file),
//                                "application/mspowerpoint,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation");
//                        try {
//                            context.startActivity(intent2);
//
//                        } catch (ActivityNotFoundException e) {
//
//                            Toast toast = Toast.makeText(context,
//                                    context.getString(R.string.toast_no_application_pp),
//                                    Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//
//                        }

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);

                    } else if (encodedStr.toLowerCase().contains(".doc")
                            || encodedStr.toLowerCase().contains(".docx")) {

                        encodedStr = encodedStr.replace("file://", "");
//                        try {
//
//                            encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        File file = new File(encodedStr);
//
//                        Intent intent3 = new Intent();
//                        intent3.setAction(android.content.Intent.ACTION_VIEW);
//                        intent3.setDataAndType(
//                                Uri.fromFile(file),
//                                "application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//                        try {
//                            context.startActivity(intent3);
//
//                        } catch (ActivityNotFoundException e) {
//
//                            Toast toast = Toast.makeText(context,
//                                    context.getString(R.string.toast_no_application_word),
//                                    Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//                        }

//                        String doc = "<iframe src='http://docs.google.com/viewer?url=" + encodedStr + "'" +
//                                "width = '100%' height = '100%'" +
//                                "style = 'border: none;' ></iframe > ";

//                        String  src="http://docs.google.com/gview?embedded=true&url=http://ccidahra.com/wp-content/uploads/2016/03/sample.ppt";

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);


                    } else if (encodedStr.toLowerCase().contains(".xlsx")
                            || encodedStr.toLowerCase().contains(".xls")) {
                        encodedStr = encodedStr.replace("file://", "");

//                        try {
//
//                            offlinePath = URLDecoder.decode(offlinePath, "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                        File file = new File(offlinePath);
//                        Intent intent4 = new Intent();
//                        intent4.setAction(android.content.Intent.ACTION_VIEW);
//                        intent4.setDataAndType(
//                                Uri.fromFile(file),
//                                "application/excel,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//                        try {
//                            context.startActivity(intent4);
//
//                        } catch (ActivityNotFoundException e) {
//
//                            Toast toast = Toast.makeText(context,
//                                    context.getString(R.string.toast_no_application_excel),
//                                    Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            toast.show();
//
//                        }

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    } else if (offlinePath.toLowerCase().contains(".mpp")) {
                        offlinePath = offlinePath.replace("file://", "");
                        try {
                            offlinePath = URLDecoder.decode(offlinePath, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        File file = new File(offlinePath);
                        Intent intent4 = new Intent();
                        intent4.setAction(android.content.Intent.ACTION_VIEW);
                        intent4.setDataAndType(
                                Uri.fromFile(file),
                                "application/vnd.ms-project, application/msproj, application/msproject, application/x-msproject, application/x-ms-project, application/x-dos_ms_project, application/mpp, zz-application/zz-winassoc-mpp");

                        try {
                            context.startActivity(intent4);
                        } catch (ActivityNotFoundException e) {

                            Toast toast = Toast.makeText(context,
                                    context.getString(R.string.toast_no_application_mpp),
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    } else if (encodedStr.toLowerCase().contains(".visio")
                            || encodedStr.toLowerCase().contains(".vsd")) {
                        encodedStr = encodedStr.replace("file://", "");

                        try {
                            encodedStr = URLDecoder.decode(offlinePath, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        File file = new File(encodedStr);
                        Intent intent4 = new Intent();
                        intent4.setAction(android.content.Intent.ACTION_VIEW);
                        intent4.setDataAndType(
                                Uri.fromFile(file),
                                "application/visio, application/x-visio, application/vnd.visio, application/visio.drawing, application/vsd, application/x-vsd, image/x-vsd, zz-application/zz-winassoc-vsd");

                        try {
                            context.startActivity(intent4);
                        } catch (ActivityNotFoundException e) {

                            Toast toast = Toast.makeText(context,
                                    context.getString(R.string.toast_no_application_mpp),
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();


                        }

                    } else if (encodedStr.toLowerCase().contains(".txt")) {

                        try {

                            encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        File file = new File(encodedStr);
                        Intent intent4 = new Intent();
                        intent4.setAction(android.content.Intent.ACTION_VIEW);
                        intent4.setDataAndType(Uri.fromFile(file), "text/plain");
                        try {
                            context.startActivity(intent4);

                        } catch (ActivityNotFoundException e) {

                            Toast toast = Toast.makeText(context,
                                    context.getString(R.string.toast_no_application_txt),
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

                        relatedContentView(myLearningModel, context);

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                    } else {

                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", encodedStr);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);

                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    }
                } else {

                    showToast(context, "The content has not been downloaded for offline View. Please download it when you are in online");
                }
            }
        }


    }

    private static String GetCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }

    public static void relatedContentView(MyLearningModel myLearningModel, Context context) {
        Integer relatedCount = Integer.parseInt(myLearningModel.getRelatedContentCount());
        if (relatedCount > 0) {
//            Intent intentDetail = new Intent(context, TrackList_Activity.class);
            Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
            intentDetail.putExtra("myLearningDetalData", myLearningModel);
            intentDetail.putExtra("ISTRACKLIST", false);
            ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
        } else {

            Toast.makeText(context, "No Content found for this event", Toast.LENGTH_SHORT).show();

        }
    }

    public static void myLearningContextMenuMethod(final View v, final int position, ImageButton btnselected, final MyLearningModel myLearningDetalData, final DownloadInterface downloadInterface, final SetCompleteListner setcompleteLitner) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.mylearning_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);// reports
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);
        menu.getItem(8).setVisible(false); // related content
        menu.getItem(9).setVisible(false); // cancel
        menu.getItem(10).setVisible(false);

        File myFile = new File(myLearningDetalData.getOfflinepath());

        if (myFile.exists() && !myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {

            menu.getItem(10).setVisible(true);

        } else {

            menu.getItem(10).setVisible(false);
        }

        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("11") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("14") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("36") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("28") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("20") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("21") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("52")) {

            if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("11") && (myLearningDetalData.getMediatypeId().equalsIgnoreCase("3") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("4"))) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
            } else {

                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
            }
            if (myLearningDetalData.getStatus().contains("completed") || myLearningDetalData.getStatus().contains("Completed")) {

                menu.getItem(7).setVisible(false);

            } else {
                menu.getItem(7).setVisible(true);

            }

        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {
            menu.getItem(10).setVisible(false);
            Integer relatedCount = Integer.parseInt(myLearningDetalData.getRelatedContentCount());
            if (relatedCount > 0) {
                menu.getItem(8).setVisible(true);
                menu.getItem(5).setVisible(true);
            }

            if (!myLearningDetalData.getStatus().toLowerCase().contains("completed")) {

                menu.getItem(6).setVisible(true);

            }

            if (myLearningDetalData.getTypeofevent() == 2) {
                menu.getItem(3).setVisible(true);
            } else if (myLearningDetalData.getTypeofevent() == 1) {
                menu.getItem(3).setVisible(false);
            }
            menu.getItem(9).setVisible(true);
        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("688")) {

            menu.getItem(1).setVisible(false);

        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(5).setVisible(true); // uncomment for report
//
            menu.getItem(1).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("details")) {
                    Intent intentDetail = new Intent(v.getContext(), MyLearningDetail_Activity.class);
                    intentDetail.putExtra("IFROMCATALOG", false);
                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
//                    v.getContext().startActivity(intentDetail);
//                context.startActivity(iWeb);
//                context.startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);

                }
                if (item.getTitle().toString().equalsIgnoreCase("View")) {
                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                }
                if (item.getTitle().toString().equalsIgnoreCase("Report")) {

                    Intent intentReports = new Intent(v.getContext(), Reports_Activity.class);
                    intentReports.putExtra("myLearningDetalData", myLearningDetalData);
                    v.getContext().startActivity(intentReports);

                }
                if (item.getTitle().toString().equalsIgnoreCase("Download")) {
//                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Related Content")) {
                    relatedContentView(myLearningDetalData, v.getContext());

                }

                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);

                }

                if (item.getTitle().toString().equalsIgnoreCase("Set Complete")) {
                    Log.d("GLB", "onMenuItemClick: Set Complete ");
                    databaseH = new DatabaseHandler(v.getContext());
//                  databaseH.setCompleteMethods(v.getContext(), myLearningDetalData);
                    new SetCourseCompleteSynchTask(v.getContext(), databaseH, myLearningDetalData, setcompleteLitner).execute();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Play")) {
//                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);
                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                }

                if (item.getTitle().toString().equalsIgnoreCase("Join")) {

                    String joinUrl = myLearningDetalData.getJoinurl();

                    if (joinUrl.length() > 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myLearningDetalData.getJoinurl()));
                        v.getContext().startActivity(browserIntent);
                    } else {
                        Toast.makeText(v.getContext(), "No Url Found", Toast.LENGTH_SHORT).show();
                    }

                }

                if (item.getTitle().toString().equalsIgnoreCase(v.getResources().getString(R.string.btn_txt_add_to_calendar))) {

                    downloadInterface.cancelEnrollment(false);
                }
                if (item.getTitle().toString().equalsIgnoreCase(v.getResources().getString(R.string.btn_txt_cancel_enrolment))) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(v.getResources().getString(R.string.canceleventmessage)).setTitle(v.getResources().getString(R.string.eventalert))
                            .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.dismiss();
                            downloadInterface.cancelEnrollment(true);

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public static void deleteDownloadedFile(View v, MyLearningModel myLearningModel, DownloadInterface downloadInterface) {

        File myFile = new File(myLearningModel.getOfflinepath());
        if (myFile.exists()) {
            myFile.delete();
            if (downloadInterface != null) {
                downloadInterface.deletedTheContent(1);
                Toast.makeText(v.getContext(), "successfully deleted", Toast.LENGTH_SHORT).show();
                databaseH = new DatabaseHandler(v.getContext());
                databaseH.ejectRecordsinCmi(myLearningModel);
                databaseH.ejectRecordsinStudentResponse(myLearningModel);
                databaseH.ejectRecordsinTrackObjDb(myLearningModel);
            }
        } else {

        }
    }


    public void downloadJwContentOffline(Context context, MyLearningModel learningModel) {

//jwvideos.xml
        String path = context.getExternalFilesDir(null)
                + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + "/" + "jwvideos.xml";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("Files", "FileName:" + files[i].getName());
        }

    }

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }


    public static Bitmap createBitmapFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static void addToDeviceCalendar(MyLearningModel myLearningModel, Context context) {

        long startMillis = 0;
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startMillis);
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, myLearningModel.getShortDes());
        intent.putExtra(CalendarContract.Events.TITLE, myLearningModel.getCourseName());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, myLearningModel.getLocationName());
        context.startActivity(intent);

    }
//    public static void catalogContextMenuMethod(final View v, final int position, ImageButton btnselected, final MyLearningModel myLearningDetalData, UiSettingsModel uiSettingsModel, final AppUserModel userModel) {
//
//        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
//        //Inflating the Popup using xml file
//        popup.getMenuInflater().inflate(R.menu.catalog_contextmenu, popup.getMenu());
//        //registering popup with OnMenuItemClickListene
//        databaseH = new DatabaseHandler(v.getContext());
//        Menu menu = popup.getMenu();
//
//        menu.getItem(0).setVisible(false);//view
//        menu.getItem(1).setVisible(false);//add
//        menu.getItem(2).setVisible(false);//buy
//        menu.getItem(3).setVisible(false);//detail
//        menu.getItem(4).setVisible(false);//delete
//
////        boolean subscribedContent = databaseH.isSubscribedContent(myLearningDetalData);
//
//        if (myLearningDetalData.getAddedToMylearning() == 1) {
//            menu.getItem(0).setVisible(true);
//            menu.getItem(1).setVisible(false);
//            menu.getItem(2).setVisible(false);
//            menu.getItem(3).setVisible(true);
//
//            if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
//
//                File myFile = new File(myLearningDetalData.getOfflinepath());
//
//                if (myFile.exists()) {
//
//                    menu.getItem(4).setVisible(true);
//
//                } else {
//
//                    menu.getItem(4).setVisible(false);
//                }
//            }
//
//        } else {
//            if (myLearningDetalData.getViewType().equalsIgnoreCase("1")) {
//                menu.getItem(0).setVisible(false);
//                menu.getItem(1).setVisible(true);
//                menu.getItem(2).setVisible(false);
//                menu.getItem(3).setVisible(true);
//
//                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
//
//                    File myFile = new File(myLearningDetalData.getOfflinepath());
//
//                    if (myFile.exists()) {
//
//                        menu.getItem(4).setVisible(true);
//
//                    } else {
//
//                        menu.getItem(4).setVisible(false);
//                    }
//                }
//            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("2")) {
//                menu.getItem(0).setVisible(false);
//                menu.getItem(1).setVisible(true);
//                menu.getItem(3).setVisible(true);
//                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("1") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
//
//                    File myFile = new File(myLearningDetalData.getOfflinepath());
//
//                    if (myFile.exists()) {
//
//                        menu.getItem(4).setVisible(true);
//
//                    } else {
//
//                        menu.getItem(4).setVisible(false);
//                    }
//
//                }
//            } else if (myLearningDetalData.getViewType().equalsIgnoreCase("3")) {
//                menu.getItem(0).setVisible(false);
//                menu.getItem(2).setVisible(true);
//                menu.getItem(3).setVisible(true);
//                menu.getItem(1).setVisible(false);
//            }
//        }
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//
//                if (item.getTitle().toString().equalsIgnoreCase("Details")) {
//                    Intent intentDetail = new Intent(v.getContext(), MyLearningDetail_Activity.class);
//                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
////                    v.getContext().startActivity(intentDetail);
//                    v.getContext().startActivity(intentDetail);
////                context.startActivityForResult(iWeb, COURSE_CLOSE_CODE);
////                    ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);
//
//                }
//                if (item.getTitle().toString().equalsIgnoreCase("View")) {
////                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
//                }
//
//                if (item.getTitle().toString().equalsIgnoreCase("Download")) {
////                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();
//                }
//
//                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
////                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);
//
//                }
//                if (item.getTitle().toString().equalsIgnoreCase("Add")) {
//
//                }
//                return true;
//            }
//        });
//        popup.show();//showing popup menu
//
//    }

}
