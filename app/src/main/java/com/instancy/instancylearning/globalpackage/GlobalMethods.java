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
import android.support.v4.content.FileProvider;
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
import com.instancy.instancylearning.interfaces.MylearningInterface;
import com.instancy.instancylearning.interfaces.DownloadStart;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.AdvancedWebCourseLaunch;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.GlobalSearchResultModelNew;
import com.instancy.instancylearning.models.LearnerSessionModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.EventTrackList_Activity;
import com.instancy.instancylearning.mylearning.MyLearningDetailActivity1;
import com.instancy.instancylearning.mylearning.Reports_Activity;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.convertStringToLong;
import static com.instancy.instancylearning.utils.Utilities.formatDate;

import static com.instancy.instancylearning.utils.Utilities.isCourseEndDateCompleted;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.replace;
import static com.instancy.instancylearning.utils.Utilities.returnEventCompleted;
import static com.instancy.instancylearning.utils.Utilities.showToast;

/**
 * Created by Upendranath on 7/7/2017 Working on InstancyLearning.
 */

public class GlobalMethods {

    static String TAG = GlobalMethods.class.getSimpleName();

    private static DatabaseHandler databaseH;


    public static void launchCourseViewFromGlobalClass(MyLearningModel myLearningModel, Context context, boolean isIconEnabled) {
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
                builder.setMessage(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.mylearning_alertsubtitle_selectedcontenthasbeenexpired, context))
                        .setCancelable(false)
                        .setPositiveButton(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.commoncomponent_alertbutton_okbutton, context), new DialogInterface.OnClickListener() {
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

        if (myFile.exists() && !myLearningModel.getObjecttypeId().equalsIgnoreCase("70")) {

            databaseH = new DatabaseHandler(context);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = databaseH.gettTinCanConfigurationValues(myLearningModel.getSiteID());
                Log.d(TAG, "TIN CAN OPTIONS: " + jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String lrsEndPoint = "";
//            String lrsActor = "{ \"mbox\":[\"mailto:" + userLoginId + "\"], \"name\":[\"" + userName + "\"] }";
            String lrsActor = "{\"name\":[\"" + userName + "\"],\"account\":[{\"accountServiceHomePage\":\"" + myLearningModel.getSiteURL() + "\",\"accountName\":\"" + myLearningModel.getPassword() + "|" + userLoginId + "\"}],\"objectType\":\"Agent\"}&activity_id=" + myLearningModel.getActivityId() +
                    "&grouping=" + myLearningModel.getActivityId();
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
//                if (isTinCan.toLowerCase().equalsIgnoreCase("true")) {
//                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") && enabletincanSupportforco.toLowerCase().equalsIgnoreCase("true")) {
//
//                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";
//
//                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("9") && enabletincanSupportforao.toLowerCase().equalsIgnoreCase("true")) {
//                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";
//
//                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && enabletincanSupportforlt.toLowerCase().equalsIgnoreCase("true")) {
//                        offlinePath = offlinePath + "?&endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + lrsActor + "&cid=0&nativeappURL=true&IsInstancyContent=true";
//                    } else {
//                        databaseH.preFunctionalityBeforeNonLRSOfflineContentPathCreation(myLearningModel, context);
////
//                        offlinePath = databaseH.generateOfflinePathForCourseView(myLearningModel, context);
//                    }
//                } else {

                databaseH.preFunctionalityBeforeNonLRSOfflineContentPathCreation(myLearningModel, context);
//
                offlinePath = databaseH.generateOfflinePathForCourseView(myLearningModel, context);

//                }

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {

                String encodedString = lrsActor;

//                try {
//                    encodedString = URLEncoder.encode(lrsActor, "utf-8").replace("+", "%20").replace("%3A", ":");
////                            encodedString = URLEncoder.encode(lrsActor, "UTF-8");
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
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
                if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started") || myLearningModel.getStatusActual().equalsIgnoreCase("")) {

                    CMIModel model = new CMIModel();
                    model.set_datecompleted("");
                    model.set_siteId(myLearningModel.getSiteID());
                    model.set_userId(Integer.parseInt(myLearningModel.getUserID()));
                    model.set_startdate(GetCurrentDateTime());
                    model.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
                    model.set_isupdate("false");
                    model.set_status("incomplete");
                    model.set_seqNum("0");
                    model.set_timespent("");
                    model.setPercentageCompleted("50");
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

                File file = new File(offlinePathEncode);
                Uri docUri = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                String mime = context.getContentResolver().getType(docUri);
                Intent intent3 = new Intent();

                intent3.setDataAndType(docUri, mime);
                intent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent3.setAction(android.content.Intent.ACTION_VIEW);


                try {
                    context.startActivity(intent3);
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
                Uri docUri = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                String mime = context.getContentResolver().getType(docUri);
                Intent intent3 = new Intent();

                intent3.setDataAndType(docUri, mime);
                intent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent3.setAction(android.content.Intent.ACTION_VIEW);
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

                offlinePathEncode = offlinePathEncode.replace("file://", "");

                File file = new File(offlinePathEncode);
                Uri docUri = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                String mime = context.getContentResolver().getType(docUri);
                Intent intent3 = new Intent();

                intent3.setDataAndType(docUri, mime);
                intent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent3.setAction(android.content.Intent.ACTION_VIEW);
                try {
                    context.startActivity(intent3);

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
                Uri docUri = FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                String mime = context.getContentResolver().getType(docUri);
                Intent intent3 = new Intent();

                intent3.setDataAndType(docUri, mime);
                intent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent3.setAction(android.content.Intent.ACTION_VIEW);
                try {
                    context.startActivity(intent3);

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

            /// ONLINEVIEW
        } else {

            boolean isAngularLaunch = true;

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
                Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
                intentDetail.putExtra("myLearningDetalData", myLearningModel);
                intentDetail.putExtra("ISICONENABLED", isIconEnabled);
                intentDetail.putExtra("ISTRACKLIST", true);
                ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
            } else {
                if (isNetworkConnectionAvailable(context, -1)) {
                    String urlForView = "";

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {


                        if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started") || myLearningModel.getStatusActual().equalsIgnoreCase("")) {

                            CMIModel model = new CMIModel();
                            model.set_datecompleted("");
                            model.set_siteId(myLearningModel.getSiteID());
                            model.set_userId(Integer.parseInt(myLearningModel.getUserID()));
                            model.set_startdate(GetCurrentDateTime());
                            model.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
                            model.set_isupdate("false");
                            model.set_status("incomplete");
                            model.set_seqNum("0");
                            model.setPercentageCompleted("50");
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
//                    String lrsActor = "{ \"mbox\":[\"mailto:" + userLoginId + "\"], \"name\":[\"" + userName + "\"] }";
                    String lrsActor = "{\"name\":[\"" + userName + "\"],\"account\":[{\"accountServiceHomePage\":\"" + myLearningModel.getSiteURL() + "\",\"accountName\":\"" + myLearningModel.getPassword() + "|" + userLoginId + "\"}],\"objectType\":\"Agent\"}&activity_id=" + myLearningModel.getActivityId() +
                            "&grouping=" + myLearningModel.getActivityId();
                    String lrsAuthorizationKey = "";
                    String enabletincanSupportforco = "";
                    String enabletincanSupportforao = "";
                    String enabletincanSupportforlt = "";
                    String isTinCan = "";
                    String autKey = "";
                    try {

                        if (jsonObject.length() != 0) {
                            if (isAngularLaunch) {
                                String lrsString = jsonObject.getString("lrsendpoint");
                                if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("102"))
                                    lrsEndPoint = lrsString.replace("/", "~");
                                else
                                    lrsEndPoint = lrsString;
                            } else {
                                lrsEndPoint = jsonObject.getString("lrsendpoint");
                            }

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


                        if (isAngularLaunch) {

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/ContentName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/ContentID/" + myLearningModel.getContentID() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + myLearningModel.getStartPage() + "?nativeappURL=true";

                        } else { // normal Launch

                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?URL=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&trackinguserid=" + myLearningModel.getUserID();
                        }

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

                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") && isValidString(myLearningModel.getJwvideokey())) {

//                            urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();

//                            urlForView = "http://content.jwplatform.com/players/" + myLearningModel.getJwvideokey() + "-" + myLearningModel.getCloudmediaplayerkey() + ".html"; // commented on 20th June 2019

                            String jwstartpage = myLearningModel.getStartPage();
                            jwstartpage = jwstartpage.replaceAll("/", "~");

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/ContentName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/ContentID/" + myLearningModel.getContentID() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + jwstartpage + "?nativeappURL=true/JWVideoParentID/" + myLearningModel.getTrackOrRelatedContentID();

                            //                        jwstartpage = jwstartpage.replacingOccurrences(of: "/", with: "~")
//
//                        urlForView = "\(objectData.siteURL)ajaxcourse/ContentName/" + "\(objectData.courseName)" + "/ScoID/" + "\(objectData.scoid)" + "/ContentTypeId/"  + "\(objectData.objectTypeID)" + "/ContentID/" + "\(objectData.contentID)" + "/AllowCourseTracking/true/trackuserid/" + "\(objectData.userID)" + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + "\(objectData.folderpath)" + "~" + "\(jwstartpage)" + "?nativeappURL=true" + "/JWVideoParentID/" + "\(objectData.parentContentID)"

                        } else {
                            urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();
                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                        urlForView = myLearningModel.getStartPage();
                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) { // scorm content

                        if (isAngularLaunch) {

                            String startPage = myLearningModel.getStartPage().replace("/", "~");

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + startPage + "?nativeappurl=true";

                        } else {// normal launch
                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?path=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();
                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {
                        if (isAngularLaunch) {
                            urlForView = myLearningModel.getSiteURL() + "/ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~" + myLearningModel.getStartPage() + "?nativeappurl=true";

                        } else {// normal launch
                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?Path=" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&CanTrack=Yes" + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();

                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
                        String encodedString = "";
//                        try {
//////                            encodedString = URLEncoder.encode(lrsActor, "utf-8").replace("+", "%20").replace("%3A", ":");
//                            encodedString = lrsActor.replace(" ", "%20");
//                            encodedString = URLEncoder.encode(lrsActor, "UTF-8");
////
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }

                        encodedString = lrsActor.replace(" ", "%20");
                        urlForView = myLearningModel.getSiteURL() + "Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + encodedString + /*&registration=*/ "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=YES" + "&nativeappURL=true";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {
                        String cerName = myLearningModel.getContentID() + "_Certificate";
                        urlForView = myLearningModel.getSiteURL() + "/content/sitefiles/" + myLearningModel.getSiteID() + "/UserCertificates/" + myLearningModel.getUserID() + "/" + cerName + ".pdf";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("20")) {

                        urlForView = myLearningModel.getSiteURL() + "/content/PublishFiles/" + myLearningModel.getFolderPath() + "/glossary_english.html";

                    } else {


                    }
                    String encodedStr = "";
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {
                        encodedStr = replace(urlForView);
                    } else {
//                        encodedStr = replace(urlForView.toLowerCase());// commented for digimedica
                        encodedStr = replace(urlForView);
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

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);

                    } else if (encodedStr.toLowerCase().contains(".doc")
                            || encodedStr.toLowerCase().contains(".docx")) {

                        encodedStr = encodedStr.replace("file://", "");


//                        String  src="http://docs.google.com/gview?embedded=true&url=http://ccidahra.com/wp-content/uploads/2016/03/sample.ppt";

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);


                    } else if (encodedStr.toLowerCase().contains(".xlsx")
                            || encodedStr.toLowerCase().contains(".xls")) {
                        encodedStr = encodedStr.replace("file://", "");


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

                        relatedContentView(myLearningModel, context, isIconEnabled);

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

    public static void relatedContentView(MyLearningModel myLearningModel, Context context, boolean isIconEnabled) {
        Integer relatedCount = Integer.parseInt(myLearningModel.getRelatedContentCount());
        if (relatedCount > 0) {
//            Intent intentDetail = new Intent(context, TrackList_Activity.class);
            Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
            intentDetail.putExtra("myLearningDetalData", myLearningModel);
            intentDetail.putExtra("ISTRACKLIST", false);
            intentDetail.putExtra("ISICONENABLED", isIconEnabled);
            ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
        } else {
            if (myLearningModel.getAddedToMylearning() != 1) {
                //   Toast.makeText(context, " No Content found for this event ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void myLearningContextMenuMethod(final View v, final int position, ImageButton btnselected, final MyLearningModel myLearningDetalData, final MylearningInterface mylearningInterface, final SetCompleteListner setcompleteLitner, final String typeFrom, boolean isReportEnabled, final DownloadStart downloadStart, UiSettingsModel uiSettingsModel, final SideMenusModel sideMenusModel, final boolean isIconEnabled) {

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
        menu.getItem(6).setVisible(false);// addtocalendar
        menu.getItem(7).setVisible(false);
        menu.getItem(8).setVisible(false); // related content
        menu.getItem(9).setVisible(false); // cancel
        menu.getItem(10).setVisible(false);
        menu.getItem(11).setVisible(false);
        menu.getItem(12).setVisible(false);
        menu.getItem(13).setVisible(false);
        menu.getItem(14).setVisible(true);//reshedule
        menu.getItem(15).setVisible(false);//certificateaction
        menu.getItem(16).setVisible(false);//qrCodeACtion


        menu.getItem(0).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_playoption, v.getContext()));
        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewoption, v.getContext()));
        menu.getItem(2).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_detailsoption, v.getContext()));

        menu.getItem(3).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_joinoption, v.getContext()));
        menu.getItem(4).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_downloadoption, v.getContext()));
        menu.getItem(5).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_reportoption, v.getContext()));

        menu.getItem(6).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_addtocalendaroption, v.getContext()));
        ;
        menu.getItem(7).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_setcompleteoption, v.getContext()));
        ;
        menu.getItem(8).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_relatedcontentoption, v.getContext()));
        ; // related content
        menu.getItem(9).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_cancelenrollmentoption, v.getContext()));// cancel
        menu.getItem(10).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_deleteoption, v.getContext()));
        menu.getItem(11).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_archiveoption, v.getContext()));
        menu.getItem(12).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_unarchiveoption, v.getContext()));
        menu.getItem(13).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_removefrommylearning, v.getContext()));

        menu.getItem(14).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionbutton_rescheduleactionbutton, v.getContext()));

        menu.getItem(15).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewcertificateoption, v.getContext()));

        menu.getItem(16).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewqrcode, v.getContext()));


        if (myLearningDetalData.isArchived()) {
            menu.getItem(11).setVisible(false);
            menu.getItem(12).setVisible(true);
        } else {
            menu.getItem(11).setVisible(true);
            menu.getItem(12).setVisible(false);
        }

        if (isValidString(myLearningDetalData.getReSheduleEvent())) {
            menu.getItem(14).setVisible(true);
        } else {
            menu.getItem(14).setVisible(false);
        }

        if (isValidString(myLearningDetalData.getCertificateAction())) {
            menu.getItem(15).setVisible(true);
        } else {
            menu.getItem(15).setVisible(false);
        }

        if (v.getContext().getResources().getString(R.string.app_name).equalsIgnoreCase(v.getContext().getResources().getString(R.string.app_esperanza))) {
            menu.getItem(11).setVisible(false);
            menu.getItem(12).setVisible(false);
        }

        if (myLearningDetalData.isRemoveFromMylearning()) { //Commented fo Playground apps
            menu.getItem(13).setVisible(true);
        } else {
            menu.getItem(13).setVisible(false);
        }


        final File myFile = new File(myLearningDetalData.getOfflinepath());

        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("10") && myLearningDetalData.getIsListView().equalsIgnoreCase("true") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("28") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("688") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("36") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("102") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("27") || myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {
            menu.getItem(4).setVisible(false);
        } else {
            menu.getItem(4).setVisible(true);
        }
        if (uiSettingsModel.getMyLearningContentDownloadType().equalsIgnoreCase("0")) {


        }

        if (myFile.exists() && !myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {

            menu.getItem(10).setVisible(true);
            menu.getItem(4).setVisible(false);

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
            if (myLearningDetalData.getStatusActual().contains("completed") || myLearningDetalData.getStatusActual().contains("Completed")) {

                menu.getItem(7).setVisible(false);

            } else {
                menu.getItem(7).setVisible(true);

            }

        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {
            menu.getItem(10).setVisible(false);
            Integer relatedCount = Integer.parseInt(myLearningDetalData.getRelatedContentCount());
            if (relatedCount > 0) {
                menu.getItem(8).setVisible(true);
            }

//            if (!myLearningDetalData.getStatusActual().toLowerCase().contains("completed")) {
//
//                menu.getItem(6).setVisible(true);
//
//            }

            // returnEventCompleted

            if (!returnEventCompleted(myLearningDetalData.getEventstartTime())) {
//                if (myLearningDetalData.getEventScheduleType() < 1 && myLearningDetalData.isCancelEventEnabled()) {
//                    menu.getItem(9).setVisible(true);
//                }
                if (myLearningDetalData.isCancelEventEnabled()) {
                    menu.getItem(9).setVisible(true);
                }
                menu.getItem(6).setVisible(true);
// for schedule events
                if (myLearningDetalData.getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
                    menu.getItem(6).setVisible(false);
                    menu.getItem(9).setVisible(false);
                }
            }

            if (!returnEventCompleted(myLearningDetalData.getEventendTime())) {

                if (myLearningDetalData.getTypeofevent() == 2) {
                    menu.getItem(3).setVisible(true);
                } else if (myLearningDetalData.getTypeofevent() == 1) {
                    menu.getItem(3).setVisible(false);
                }

//                menu.getItem(3).setVisible(true);
            }

            if (isValidString(myLearningDetalData.getQRImageName()) && isValidString(myLearningDetalData.getQrCodeImagePath())) {
                menu.getItem(16).setVisible(true);
            }

        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("688")) {

            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(2).setVisible(true);
            if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("27")) {
                menu.getItem(5).setVisible(false);
            } else {
                menu.getItem(5).setVisible(true);
            }

            menu.getItem(1).setVisible(true);
        }

        if (typeFrom.equalsIgnoreCase("event") || typeFrom.equalsIgnoreCase("track")) {
            menu.getItem(2).setVisible(false);
            menu.getItem(11).setVisible(false);
            menu.getItem(12).setVisible(false);
            //  menu.getItem(5).setVisible(false);// uncomment for esperanza
        }

        if (!isReportEnabled) {

            menu.getItem(5).setVisible(false);

        }

        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && myLearningDetalData.getBit4()) {
            menu.getItem(13).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(11).setVisible(false);
            menu.getItem(12).setVisible(false);
            menu.getItem(9).setVisible(false);
            menu.getItem(6).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.ctx_detail:
                        Intent intentDetail = new Intent(v.getContext(), MyLearningDetailActivity1.class);
                        intentDetail.putExtra("IFROMCATALOG", false);
                        intentDetail.putExtra("ISICONENABLED", isIconEnabled);
                        intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
                        intentDetail.putExtra("typeFrom", typeFrom);
                        intentDetail.putExtra("sideMenusModel", sideMenusModel);
//                        intentDetail.putExtra("reschdule", true);
                        ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);
                        break;
                    case R.id.ctx_view:
                        if (isValidString(myLearningDetalData.getViewprerequisitecontentstatus())) {
                            String alertMessage = getLocalizationValue(JsonLocalekeys.prerequistesalerttitle6_alerttitle6, v.getContext());
                            alertMessage = alertMessage + "  " + myLearningDetalData.getViewprerequisitecontentstatus();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage(alertMessage).setTitle(getLocalizationValue(JsonLocalekeys.details_alerttitle_stringalert, v.getContext()))
                                    .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.events_alertbutton_okbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext(), isIconEnabled);
                        }

                        break;
                    case R.id.ctx_report:
                        Intent intentReports = new Intent(v.getContext(), Reports_Activity.class);
                        intentReports.putExtra("myLearningDetalData", myLearningDetalData);
                        intentReports.putExtra("typeFrom", typeFrom);
                        v.getContext().startActivity(intentReports);
                        break;
                    case R.id.ctx_download:
                        if (isNetworkConnectionAvailable(v.getContext(), -1)) {
                            downloadStart.downloadTheContent();
                        } else {
                            showToast(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()));
                        }
                        break;
                    case R.id.ctx_relatedcontent:
                        if (isValidString(myLearningDetalData.getViewprerequisitecontentstatus())) {
                            String alertMessage = getLocalizationValue(JsonLocalekeys.prerequistesalerttitle6_alerttitle6, v.getContext());
                            alertMessage = alertMessage + "  " + myLearningDetalData.getViewprerequisitecontentstatus();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage(alertMessage).setTitle(getLocalizationValue(JsonLocalekeys.details_alerttitle_stringalert, v.getContext()))
                                    .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.events_alertbutton_okbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            relatedContentView(myLearningDetalData, v.getContext(), isIconEnabled);
                        }
                        break;
                    case R.id.ctx_delete:
                        if (isNetworkConnectionAvailable(v.getContext(), -1)) {

                            deleteDownloadedFile(v, myLearningDetalData, mylearningInterface);
                        } else {

                            Toast.makeText(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.ctx_complete:
                        databaseH = new DatabaseHandler(v.getContext());
//                  databaseH.setCompleteMethods(v.getContext(), myLearningDetalData);
                        File myFile = new File(myLearningDetalData.getOfflinepath());
                        if (isNetworkConnectionAvailable(v.getContext(), -1) || myFile.exists()) {
                            new SetCourseCompleteSynchTask(v.getContext(), databaseH, myLearningDetalData, setcompleteLitner).execute();
                        } else {

                            Toast.makeText(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.ctx_play:
                        if (isValidString(myLearningDetalData.getViewprerequisitecontentstatus())) {
                            String alertMessage = getLocalizationValue(JsonLocalekeys.prerequistesalerttitle6_alerttitle6, v.getContext());
                            alertMessage = alertMessage + "  " + myLearningDetalData.getViewprerequisitecontentstatus();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage(alertMessage).setTitle(getLocalizationValue(JsonLocalekeys.details_alerttitle_stringalert, v.getContext()))
                                    .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.events_alertbutton_okbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        } else {
                            GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext(), isIconEnabled);
                        }
                        break;
                    case R.id.ctx_join:
                        String joinUrl = myLearningDetalData.getJoinurl();

                        if (joinUrl.length() > 0) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myLearningDetalData.getJoinurl()));
                            v.getContext().startActivity(browserIntent);
                        } else {
                            Toast.makeText(v.getContext(), "No Url Found", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.ctx_addtocalender:
                        mylearningInterface.cancelEnrollment(false);
                        break;
                    case R.id.ctx_cancelenrollment:
                        if (myLearningDetalData.isBadCancellationEnabled()) {

                            mylearningInterface.badCancelEnrollment(true);
                            // bad cancel
                        } else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setMessage(getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_doyouwanttocancelenrolledevent, v.getContext())).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_alerttitle_stringareyousure, v.getContext()))
                                    .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.mylearning_alertbutton_cancelbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(getLocalizationValue(JsonLocalekeys.mylearning_alertbutton_yesbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.dismiss();
                                    mylearningInterface.cancelEnrollment(true);

                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        break;
                    case R.id.ctx_addtoarchive:
                        mylearningInterface.addToArchive(true);
                        break;
                    case R.id.ctx_removearchive:
                        mylearningInterface.addToArchive(false);
                        break;
                    case R.id.ctx_removefrommylearning:
                        if (isNetworkConnectionAvailable(v.getContext(), -1)) {
                            final AlertDialog.Builder builders = new AlertDialog.Builder(v.getContext());
                            builders.setMessage(getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_removethecontentitem, v.getContext())).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_alerttitle_stringareyousure, v.getContext()))
                                    .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.mylearning_alertbutton_cancelbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(getLocalizationValue(JsonLocalekeys.mylearning_alertbutton_yesbutton, v.getContext()), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.dismiss();
                                    mylearningInterface.removeFromMylearning(true);

                                }
                            });
                            AlertDialog alerts = builders.create();
                            alerts.show();
                        } else {
                            showToast(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()));
                        }
                        break;
                    case R.id.ctx_reshedule:
                        mylearningInterface.resheduleTheEvent(true);
                        break;
                    case R.id.ctx_certificateaction:
                        if (myLearningDetalData.getCertificateAction().equalsIgnoreCase("notearned")) {
                            if (isNetworkConnectionAvailable(v.getContext(), -1)) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setMessage(getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_forviewcertificate, v.getContext())).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewcertificateoption, v.getContext()))
                                        .setCancelable(false)
                                        .setPositiveButton(getLocalizationValue(JsonLocalekeys.mylearning_closebuttonaction_closebuttonalerttitle, v.getContext()), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                dialog.dismiss();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                showToast(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()));
                            }
                        } else {
                            mylearningInterface.viewCertificateLink(true);

                        }
                        break;
                    case R.id.ctx_qrcode:
                        if (isNetworkConnectionAvailable(v.getContext(), -1)) {
                            Intent iWeb = new Intent(v.getContext(), AdvancedWebCourseLaunch.class);
                            iWeb.putExtra("COURSE_URL", myLearningDetalData.getQrCodeImagePath());
                            iWeb.putExtra("myLearningDetalData", myLearningDetalData);
                            iWeb.putExtra("QRCODE", true);
                            Log.d(TAG, "actionsforDetail: " + myLearningDetalData.getOfflineQrCodeImagePath());
                            ((Activity) v.getContext()).startActivity(iWeb);
                        } else {

                            File file = new File(myLearningDetalData.getOfflineQrCodeImagePath());
                            if (file.exists()) {
                                String offlinePath = "file://" + myLearningDetalData.getQrCodeImagePath();
                                Intent iWeb = new Intent(v.getContext(), AdvancedWebCourseLaunch.class);
                                iWeb.putExtra("COURSE_URL", offlinePath);
                                iWeb.putExtra("myLearningDetalData", myLearningDetalData);
                                iWeb.putExtra("QRCODE", false);

                            } else {
                                Toast.makeText(v.getContext(), getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet, v.getContext()), Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public static void deleteDownloadedFile(View v, MyLearningModel myLearningModel, MylearningInterface mylearningInterface) {

        File myFile = new File(myLearningModel.getOfflinepath());
        String parentFilePath = myFile.getParent();
        File parentFile = new File(parentFilePath);
        if (myFile.exists()) {
            deleteDirectory(parentFile);
            if (mylearningInterface != null) {
                mylearningInterface.deletedTheContent(1);
                Toast.makeText(v.getContext(), getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_youhavesuccessfullydeleteddownloadedcontent, v.getContext()), Toast.LENGTH_LONG).show();
                databaseH = new DatabaseHandler(v.getContext());
                databaseH.ejectRecordsinCmi(myLearningModel);
                databaseH.ejectRecordsinStudentResponse(myLearningModel);
                databaseH.ejectRecordsinTrackObjDb(myLearningModel);
            }
        } else {

        }
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }


//    public static boolean deleteDirectory(String parentFile) {
//        File path = new File(parentFile);
//        if (path.exists()) {
//            File[] files = path.listFiles();
//            if (files == null) {
//                return true;
//            }
//            for (int i = 0; i < files.length; i++) {
//                if (files[i].isDirectory()) {
//                    files[i].delete();
//                } else {
//                    files[i].delete();
//                }
//            }
//        }
//        return (path.delete());
//    }


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

    public static void addEventToDeviceCalendar(MyLearningModel myLearningModel, Context context) {

        if (myLearningModel.getEventScheduleType() == 1)
            return;

        long startMillis = convertStringToLong(myLearningModel.getEventstartTime());
        long endMillis = convertStringToLong(myLearningModel.getEventendTime());

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, myLearningModel.getShortDes());
        intent.putExtra(CalendarContract.Events.TITLE, myLearningModel.getCourseName());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, myLearningModel.getLocationName());
        context.startActivity(intent);
    }


    public static void launchCoursePreviewViewFromGlobalClass(MyLearningModel myLearningModel, Context context, boolean isIconEnabled) {


        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
            Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
            intentDetail.putExtra("myLearningDetalData", myLearningModel);
            intentDetail.putExtra("ISTRACKLIST", true);
            intentDetail.putExtra("ISICONENABLED", isIconEnabled);
            ((Activity) context).startActivity(intentDetail);
//                context.startActivity(intentDetail);
        } else {
            if (isNetworkConnectionAvailable(context, -1)) {
                String urlForView = "";

//                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {
//
//                    urlForView = myLearningModel.getSiteURL() + "/Content/Sitefiles/" + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage();
//
//                }

                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

                    // urlForView = myLearningModel.getSiteURL() + "/PublicModules/AJAXPreview.aspx?Path=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=NO";

                    urlForView = myLearningModel.getSiteURL() + "ajaxcourse/ContentName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/ContentID/" + myLearningModel.getContentID() + "/AllowCourseTracking/false/trackuserid/" + myLearningModel.getUserID() + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + myLearningModel.getStartPage() + "?nativeappURL=true";
//
                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11")) {

                    //   urlForView = myLearningModel.getSiteURL() + "/PublicModules/AJAXPreview.aspx?Path=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "&ContentID=" + myLearningModel.getContentID();

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") && isValidString(myLearningModel.getJwvideokey())) {

//                            urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();

                        urlForView = "http://content.jwplatform.com/players/" + myLearningModel.getJwvideokey() + "-" + myLearningModel.getCloudmediaplayerkey() + ".html";

                        //                        jwstartpage = jwstartpage.replacingOccurrences(of: "/", with: "~")
//
//                        urlForView = "\(objectData.siteURL)ajaxcourse/ContentName/" + "\(objectData.courseName)" + "/ScoID/" + "\(objectData.scoid)" + "/ContentTypeId/"  + "\(objectData.objectTypeID)" + "/ContentID/" + "\(objectData.contentID)" + "/AllowCourseTracking/true/trackuserid/" + "\(objectData.userID)" + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + "\(objectData.folderpath)" + "~" + "\(jwstartpage)" + "?nativeappURL=true" + "/JWVideoParentID/" + "\(objectData.parentContentID)"


                    } else {
                        urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();
                    }


                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36")) //14 21 36
                {
                    urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                    urlForView = myLearningModel.getStartPage();
                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {

//                    urlForView = myLearningModel.getSiteURL() + "//PublicModules/AJAXPreview.aspx?path=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=NO&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=";

                    String startPage = myLearningModel.getStartPage().replace("/", "~");

                    urlForView = myLearningModel.getSiteURL() + "ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/false/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + startPage + "?nativeappurl=true";

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {
//                    urlForView = myLearningModel.getSiteURL() + "//PublicModules/AJAXPreview.aspx?Path=" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&CanTrack=NO" + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=";

                    urlForView = myLearningModel.getSiteURL() + "/ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/false/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~" + myLearningModel.getStartPage() + "?nativeappurl=true";


                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
                    String encodedString = "";

                    urlForView = myLearningModel.getSiteURL() + "Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?endpoint=" + "" + "&auth=" + "" + "&actor=" + encodedString + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=NO" + "&nativeappURL=true";

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {
                    String cerName = myLearningModel.getContentID() + "_Certificate";
                    urlForView = myLearningModel.getSiteURL() + "/content/sitefiles/" + myLearningModel.getSiteID() + "/UserCertificates/" + myLearningModel.getUserID() + "/" + cerName + ".pdf";

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("20")) {

                    urlForView = myLearningModel.getSiteURL() + "/content/PublishFiles/" + myLearningModel.getFolderPath() + "/glossary_english.html";

                } else {


                }
                String encodedStr = "";
                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {
                    encodedStr = replace(urlForView);
                } else {
                    encodedStr = replace(urlForView);
                }
                Log.d("DBG", "launchCourseView: " + encodedStr);
                if (encodedStr.endsWith(".pdf")) {
                    Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                    pdfIntent.putExtra("PDF_URL", encodedStr);
                    pdfIntent.putExtra("ISONLINE", "YES");
                    pdfIntent.putExtra("PDF_FILENAME", myLearningModel.getCourseName());
                    pdfIntent.putExtra("myLearningDetalData", myLearningModel);
//                        context.startActivity(pdfIntent);
                    ((Activity) context).startActivity(pdfIntent);

                } else if (encodedStr.toLowerCase().contains(".ppt") || encodedStr.toLowerCase().contains(".pptx")) {

                    encodedStr = encodedStr.replace("file://", "");

                    String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                    Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                    iWeb.putExtra("COURSE_URL", src);
                    iWeb.putExtra("myLearningDetalData", myLearningModel);
                    ((Activity) context).startActivity(iWeb);

                } else if (encodedStr.toLowerCase().contains(".doc")
                        || encodedStr.toLowerCase().contains(".docx")) {

                    encodedStr = encodedStr.replace("file://", "");

                    String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                    Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                    iWeb.putExtra("COURSE_URL", src);
                    iWeb.putExtra("myLearningDetalData", myLearningModel);
                    ((Activity) context).startActivity(iWeb);


                } else if (encodedStr.toLowerCase().contains(".xlsx")
                        || encodedStr.toLowerCase().contains(".xls")) {
                    encodedStr = encodedStr.replace("file://", "");
                    String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                    Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                    iWeb.putExtra("COURSE_URL", src);
                    iWeb.putExtra("myLearningDetalData", myLearningModel);
                    ((Activity) context).startActivity(iWeb);
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

                    relatedContentView(myLearningModel, context, false);

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                } else {

                    Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                    iWeb.putExtra("COURSE_URL", encodedStr);
                    iWeb.putExtra("ISCLOSE", true);
                    iWeb.putExtra("myLearningDetalData", myLearningModel);

                    ((Activity) context).startActivity(iWeb);
                }
            } else {

                showToast(context, "The content has not been downloaded for offline View. Please download it when you are in online");
            }
        }
    }


    public static void launchCourseForGlobalSearch(MyLearningModel myLearningModel, Context context) {

        databaseH = new DatabaseHandler(context);
        PreferencesManager.initializeInstance(context);
        String userLoginId = PreferencesManager.getInstance().getStringValue(StaticValues.KEY_USERLOGINID);
        String userName = PreferencesManager.getInstance().getStringValue(StaticValues.KEY_USERNAME);

        String offlinePath = "";

        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
            Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
            intentDetail.putExtra("myLearningDetalData", myLearningModel);
            intentDetail.putExtra("ISTRACKLIST", true);
            intentDetail.putExtra("ISICONENABLED", false);
            ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
        } else

        {
            boolean isAngularLaunch = true;

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
                Intent intentDetail = new Intent(context, EventTrackList_Activity.class);
                intentDetail.putExtra("myLearningDetalData", myLearningModel);
                intentDetail.putExtra("ISICONENABLED", false);
                intentDetail.putExtra("ISTRACKLIST", true);
                ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
            } else {
                if (isNetworkConnectionAvailable(context, -1)) {
                    String urlForView = "";

                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {


                        if (myLearningModel.getStatusActual().equalsIgnoreCase("Not Started") || myLearningModel.getStatusActual().equalsIgnoreCase("")) {

                            CMIModel model = new CMIModel();
                            model.set_datecompleted("");
                            model.set_siteId(myLearningModel.getSiteID());
                            model.set_userId(Integer.parseInt(myLearningModel.getUserID()));
                            model.set_startdate(GetCurrentDateTime());
                            model.set_scoId(Integer.parseInt(myLearningModel.getScoId()));
                            model.set_isupdate("false");
                            model.set_status("incomplete");
                            model.set_seqNum("0");
                            model.setPercentageCompleted("50");
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
//                    String lrsActor = "{ \"mbox\":[\"mailto:" + userLoginId + "\"], \"name\":[\"" + userName + "\"] }";
                    String lrsActor = "{\"name\":[\"" + userName + "\"],\"account\":[{\"accountServiceHomePage\":\"" + myLearningModel.getSiteURL() + "\",\"accountName\":\"" + myLearningModel.getPassword() + "|" + userLoginId + "\"}],\"objectType\":\"Agent\"}&activity_id=" + myLearningModel.getActivityId() +
                            "&grouping=" + myLearningModel.getActivityId();
                    String lrsAuthorizationKey = "";
                    String enabletincanSupportforco = "";
                    String enabletincanSupportforao = "";
                    String enabletincanSupportforlt = "";
                    String isTinCan = "";
                    String autKey = "";
                    try {

                        if (jsonObject.length() != 0) {
                            if (isAngularLaunch) {
                                String lrsString = jsonObject.getString("lrsendpoint");
                                if (!myLearningModel.getObjecttypeId().equalsIgnoreCase("102"))
                                    lrsEndPoint = lrsString.replace("/", "~");
                                else
                                    lrsEndPoint = lrsString;
                            } else {
                                lrsEndPoint = jsonObject.getString("lrsendpoint");
                            }

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


                        if (isAngularLaunch) {

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/ContentName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/ContentID/" + myLearningModel.getContentID() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + myLearningModel.getStartPage() + "?nativeappURL=true";

                        } else { // normal Launch

                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?URL=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&trackinguserid=" + myLearningModel.getUserID();
                        }

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

                        if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") && isValidString(myLearningModel.getJwvideokey())) {

//                            urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();

//                            urlForView = "http://content.jwplatform.com/players/" + myLearningModel.getJwvideokey() + "-" + myLearningModel.getCloudmediaplayerkey() + ".html"; // commented on 20th June 2019

                            String jwstartpage = myLearningModel.getStartPage();
                            jwstartpage = jwstartpage.replaceAll("/", "~");

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/ContentName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/ContentID/" + myLearningModel.getContentID() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + jwstartpage + "?nativeappURL=true/JWVideoParentID/" + myLearningModel.getTrackOrRelatedContentID();

                            //                        jwstartpage = jwstartpage.replacingOccurrences(of: "/", with: "~")
//
//                        urlForView = "\(objectData.siteURL)ajaxcourse/ContentName/" + "\(objectData.courseName)" + "/ScoID/" + "\(objectData.scoid)" + "/ContentTypeId/"  + "\(objectData.objectTypeID)" + "/ContentID/" + "\(objectData.contentID)" + "/AllowCourseTracking/true/trackuserid/" + "\(objectData.userID)" + "/ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + "\(objectData.folderpath)" + "~" + "\(jwstartpage)" + "?nativeappURL=true" + "/JWVideoParentID/" + "\(objectData.parentContentID)"

                        } else {
                            urlForView = myLearningModel.getSiteURL() + "/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage();
                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                        urlForView = myLearningModel.getStartPage();
                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) { // scorm content

                        if (isAngularLaunch) {

                            String startPage = myLearningModel.getStartPage().replace("/", "~");

                            urlForView = myLearningModel.getSiteURL() + "ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ScoID/" + myLearningModel.getScoId() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~Content~PublishFiles~" + myLearningModel.getFolderPath() + "~" + startPage + "?nativeappurl=true";

                        } else {// normal launch
                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?path=/Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();
                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {
                        if (isAngularLaunch) {
                            urlForView = myLearningModel.getSiteURL() + "/ajaxcourse/CourseName/" + myLearningModel.getCourseName() + "/ContentID/" + myLearningModel.getContentID() + "/ContentTypeId/" + myLearningModel.getObjecttypeId() + "/AllowCourseTracking/true/trackuserid/" + myLearningModel.getUserID() + "/eventkey//eventtype//ismobilecontentview/true/ContentPath/~" + myLearningModel.getStartPage() + "?nativeappurl=true";

                        } else {// normal launch
                            urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?Path=" + myLearningModel.getStartPage() + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&CanTrack=Yes" + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "&trackinguserid=" + myLearningModel.getUserID();

                        }

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {
                        String encodedString = "";
//                        try {
//////                            encodedString = URLEncoder.encode(lrsActor, "utf-8").replace("+", "%20").replace("%3A", ":");
//                            encodedString = lrsActor.replace(" ", "%20");
//                            encodedString = URLEncoder.encode(lrsActor, "UTF-8");
////
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }

                        encodedString = lrsActor.replace(" ", "%20");
                        urlForView = myLearningModel.getSiteURL() + "Content/PublishFiles/" + myLearningModel.getFolderPath() + "/" + myLearningModel.getStartPage() + "?endpoint=" + lrsEndPoint + "&auth=" + lrsAuthorizationKey + "&actor=" + encodedString + /*&registration=*/ "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=YES" + "&nativeappURL=true";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {
                        String cerName = myLearningModel.getContentID() + "_Certificate";
                        urlForView = myLearningModel.getSiteURL() + "/content/sitefiles/" + myLearningModel.getSiteID() + "/UserCertificates/" + myLearningModel.getUserID() + "/" + cerName + ".pdf";

                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                    } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("20")) {

                        urlForView = myLearningModel.getSiteURL() + "/content/PublishFiles/" + myLearningModel.getFolderPath() + "/glossary_english.html";

                    } else {


                    }
                    String encodedStr = "";
                    if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {
                        encodedStr = replace(urlForView);
                    } else {
//                        encodedStr = replace(urlForView.toLowerCase());// commented for digimedica
                        encodedStr = replace(urlForView);
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

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);

                    } else if (encodedStr.toLowerCase().contains(".doc")
                            || encodedStr.toLowerCase().contains(".docx")) {

                        encodedStr = encodedStr.replace("file://", "");


//                        String  src="http://docs.google.com/gview?embedded=true&url=http://ccidahra.com/wp-content/uploads/2016/03/sample.ppt";

                        String src = "http://docs.google.com/gview?embedded=true&url=" + encodedStr;
                        Intent iWeb = new Intent(context, AdvancedWebCourseLaunch.class);
                        iWeb.putExtra("COURSE_URL", src);
                        iWeb.putExtra("myLearningDetalData", myLearningModel);
                        ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);


                    } else if (encodedStr.toLowerCase().contains(".xlsx")
                            || encodedStr.toLowerCase().contains(".xls")) {
                        encodedStr = encodedStr.replace("file://", "");


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

                        relatedContentView(myLearningModel, context, false);

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


    public static MyLearningModel convertGlobalModelToMylearningModel(GlobalSearchResultModelNew globalSearchResultModelNew, AppUserModel appUserModel) {
        MyLearningModel myLearningModel = new MyLearningModel();

        if (globalSearchResultModelNew == null)
            return null;

        myLearningModel.setUserID(globalSearchResultModelNew.userID);
        myLearningModel.setUserName(globalSearchResultModelNew.userID);
        myLearningModel.setSiteID("" + globalSearchResultModelNew.siteid);
        myLearningModel.setSiteURL(globalSearchResultModelNew.siteurl);
        myLearningModel.setSiteName(globalSearchResultModelNew.sitename);
        myLearningModel.setContentID(globalSearchResultModelNew.contentid);
        myLearningModel.setObjectId(globalSearchResultModelNew.objectid);
        myLearningModel.setCourseName(globalSearchResultModelNew.name);
        myLearningModel.setAuthor(globalSearchResultModelNew.authordisplayname);
        myLearningModel.setPresenter(globalSearchResultModelNew.presenter);
        myLearningModel.setShortDes(globalSearchResultModelNew.shortdescription);
        myLearningModel.setLongDes(globalSearchResultModelNew.longdescription);

        myLearningModel.setMediaName(globalSearchResultModelNew.medianame);
        myLearningModel.setCreatedDate(globalSearchResultModelNew.createddate);
        myLearningModel.setStartPage(globalSearchResultModelNew.startpage);
        myLearningModel.setAviliableSeats(globalSearchResultModelNew.availableseats);
        myLearningModel.setObjecttypeId("" + globalSearchResultModelNew.objecttypeid);
        myLearningModel.setLocationName(globalSearchResultModelNew.location);
        myLearningModel.setScoId("" + globalSearchResultModelNew.scoid);
        myLearningModel.setParticipantUrl(globalSearchResultModelNew.participanturl);
        myLearningModel.setStatusActual(globalSearchResultModelNew.status);
        myLearningModel.setPassword(appUserModel.getPassword());
        myLearningModel.setDisplayName(globalSearchResultModelNew.name);
        myLearningModel.setIsListView("false");
        myLearningModel.setIsDownloaded("false");
        myLearningModel.setCourseAttempts("0");
        myLearningModel.setAddedToMylearning(globalSearchResultModelNew.isaddedtomylearning);
        myLearningModel.setEventContentid(globalSearchResultModelNew.contentid);
        myLearningModel.setRelatedContentCount("" + globalSearchResultModelNew.relatedconentcount);
        if (globalSearchResultModelNew.duration.equalsIgnoreCase("0")) {
            myLearningModel.setDurationEndDate("");
        } else {
            myLearningModel.setDurationEndDate(globalSearchResultModelNew.duration);
        }
        myLearningModel.setRatingId("" + globalSearchResultModelNew.ratingid);
        myLearningModel.setIsExpiry("false");
        myLearningModel.setMediatypeId(globalSearchResultModelNew.mediatypeid);
        myLearningModel.setDateAssigned(globalSearchResultModelNew.createddate);
        myLearningModel.setKeywords(globalSearchResultModelNew.keywords);
        myLearningModel.setDownloadURL(globalSearchResultModelNew.downloadfile);
        myLearningModel.setOfflinepath("");
        myLearningModel.setPresenter(globalSearchResultModelNew.presenter);
        myLearningModel.setEventAddedToCalender(false);
        myLearningModel.setTimeZone(globalSearchResultModelNew.timezone);
        myLearningModel.setJoinurl("");
        myLearningModel.setTypeofevent(globalSearchResultModelNew.typeofevent);
        myLearningModel.setViewType("" + globalSearchResultModelNew.viewtype);
        myLearningModel.setProgress("inprogress");

        myLearningModel.setMemberShipLevel(globalSearchResultModelNew.membershiplevel);

        myLearningModel.setMembershipname(globalSearchResultModelNew.membershipname);

        myLearningModel.setFolderPath(globalSearchResultModelNew.folderpath);

        myLearningModel.setJwvideokey(globalSearchResultModelNew.jwvideokey);

        myLearningModel.setCloudmediaplayerkey(globalSearchResultModelNew.cloudmediaplayerkey);

        myLearningModel.setPublishedDate(formatDate(globalSearchResultModelNew.publisheddate, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));

        myLearningModel.setEventstartTime(globalSearchResultModelNew.eventstartdatedisplay);
        myLearningModel.setEventendTime(globalSearchResultModelNew.eventenddatedisplay);

        myLearningModel.setEventstartUtcTime(globalSearchResultModelNew.eventstartdatetime);

        myLearningModel.setEventendUtcTime(globalSearchResultModelNew.eventenddatetime);

        myLearningModel.setBadCancellationEnabled(globalSearchResultModelNew.isBadCancellationEnabled);

        myLearningModel.setImageData(globalSearchResultModelNew.siteurl + globalSearchResultModelNew.thumbnailimagepath);

        myLearningModel.setViewprerequisitecontentstatus(globalSearchResultModelNew.viewprerequisitecontentstatus);

        myLearningModel.setContentTypeImagePath(globalSearchResultModelNew.iconpath);

        return myLearningModel;
    }

    public static SideMenusModel convertGlobalModelToSideMenuModel(GlobalSearchResultModelNew globalSearchResultModelNew) {
        SideMenusModel sideMenusModel = new SideMenusModel();

        if (globalSearchResultModelNew == null)
            return null;

        sideMenusModel.setMenuId(globalSearchResultModelNew.menuID);
        sideMenusModel.setDisplayName(globalSearchResultModelNew.componentName);
        sideMenusModel.setDisplayOrder(0);
        sideMenusModel.setImage("");
        sideMenusModel.setDataFound(true);
        sideMenusModel.setIsOfflineMenu("");
        sideMenusModel.setIsEnabled("");
        sideMenusModel.setContextTitle(globalSearchResultModelNew.componentName);
        sideMenusModel.setContextMenuId("" + globalSearchResultModelNew.contextMenuId);
        sideMenusModel.setRepositoryId("" + globalSearchResultModelNew.componentInstanceID);
        sideMenusModel.setLandingPageType("");
        sideMenusModel.setCategoryStyle("");
        sideMenusModel.setComponentId("" + globalSearchResultModelNew.componentid);
        sideMenusModel.setConditions("");
        sideMenusModel.setParentMenuId("" + globalSearchResultModelNew.menuID);
        sideMenusModel.setParameterStrings("");
        sideMenusModel.setSiteID(globalSearchResultModelNew.siteid);
        return sideMenusModel;
    }

    private static String getLocalizationValue(String key, Context context) {
        return JsonLocalization.getInstance().getStringForKey(key, context);
    }
}
