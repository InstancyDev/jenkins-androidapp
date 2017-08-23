package com.instancy.instancylearning.globalpackage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.mainactivities.AdvancedWebCourseLaunch;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.mylearning.MyLearningDetail_Activity;
import com.instancy.instancylearning.mylearning.Reports_Activity;
import com.instancy.instancylearning.mylearning.TrackList_Activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.COURSE_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.Utilities.replace;

/**
 * Created by Upendranath on 7/7/2017 Working on InstancyLearning.
 */

public class GlobalMethods {


    private static DatabaseHandler databaseH;

    public static void launchCourseViewFromGlobalClass(MyLearningModel myLearningModel, Context context) {


        File myFile = new File(myLearningModel.getOfflinepath());

        String offlinePath = "";

        if (myFile.exists()) {
            databaseH = new DatabaseHandler(context);

            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {

   // LRS need to be implemented

                databaseH.preFunctionalityBeforeNonLRSOfflineContentPathCreation(myLearningModel, context);
//
                offlinePath = databaseH.generateOfflinePathForCourseView(myLearningModel, context);

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {


            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {

                offlinePath = "file://"
                        + context.getExternalFilesDir(null)
                        + "/Mydownloads/Content/LaunchCourse.html?contentpath=file://"
                        + myLearningModel.getOfflinepath();

            } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("52")) {


            } else {

                offlinePath = "file://" + myLearningModel.getOfflinepath();

            }

             String  offlinePathEncode = offlinePath.replaceAll(" ", "%20");

            if (offlinePathEncode.endsWith(".pdf")) {
                Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                pdfIntent.putExtra("PDF_URL", offlinePathEncode);
                pdfIntent.putExtra("ISONLINE", "NO");
                pdfIntent.putExtra("PDF_FILENAME", myLearningModel.getCourseName());
                context.startActivity(pdfIntent);

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
                try {

                    offlinePathEncode = URLDecoder.decode(offlinePath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                offlinePathEncode = offlinePathEncode.replace("file://", "");

                try {

                    offlinePathEncode = URLDecoder.decode(offlinePathEncode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
                try {
                    offlinePathEncode = URLDecoder.decode(offlinePathEncode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

                try {
                    offlinePathEncode = URLDecoder.decode(offlinePathEncode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

                try {

                    offlinePathEncode = URLDecoder.decode(offlinePathEncode, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

            }
        } else {



            if (myLearningModel.getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.getIsListView().equalsIgnoreCase("true")) {
                Intent intentDetail = new Intent(context, TrackList_Activity.class);
                intentDetail.putExtra("myLearningDetalData", myLearningModel);
                intentDetail.putExtra("ISTRACKLIST", true);
                ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
//                context.startActivity(intentDetail);
            } else {
                String urlForView = "";
                if (myLearningModel.getObjecttypeId().equalsIgnoreCase("8") || myLearningModel.getObjecttypeId().equalsIgnoreCase("9") || myLearningModel.getObjecttypeId().equalsIgnoreCase("10")) {
                    urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?URL=/Content/SiteFiles/"
                            + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&trackinguserid=" + myLearningModel.getUserID();
                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("11") || myLearningModel.getObjecttypeId().equalsIgnoreCase("14") || myLearningModel.getObjecttypeId().equalsIgnoreCase("21") || myLearningModel.getObjecttypeId().equalsIgnoreCase("36")) //14 21 36
                {
                    urlForView = myLearningModel.getSiteURL() + "/Content/Sitefiles/" + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage();
                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("28")) {

                    urlForView = myLearningModel.getStartPage();
                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("26")) {

                    urlForView = myLearningModel.getSiteURL() + "/remote/AJAXLaunchPage.aspx?URL=/Content/SiteFiles/"
                            + myLearningModel.getContentID() + "/" + myLearningModel.getStartPage() + "?nativeappURL=true" + "&CourseName=" + myLearningModel.getCourseName() + "&ContentID=" + myLearningModel.getContentID() + "&ObjectTypeID=" + myLearningModel.getObjecttypeId() + "&CanTrack=Yes&SCOID=" + myLearningModel.getScoId() + "&eventkey=&eventtype=" + "& trackinguserid=" + myLearningModel.getUserID();

                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("27")) {


                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("102")) {


                } else if (myLearningModel.getObjecttypeId().equalsIgnoreCase("688")) {


                } else {

                }
                String encodedStr = replace(urlForView);
                Log.d("DBG", "launchCourseView: " + encodedStr);
                if (encodedStr.endsWith(".pdf")) {
                    Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                    pdfIntent.putExtra("PDF_URL", encodedStr);
                    pdfIntent.putExtra("ISONLINE", "YES");
                    pdfIntent.putExtra("PDF_FILENAME", myLearningModel.getCourseName());
                    context.startActivity(pdfIntent);
                } else if (encodedStr.toLowerCase().contains(".ppt")
                        || encodedStr.toLowerCase().contains(".pptx")) {

                    encodedStr = encodedStr.replace("file://", "");

                    try {

                        encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    File file = new File(encodedStr);
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

                } else if (encodedStr.toLowerCase().contains(".doc")
                        || encodedStr.toLowerCase().contains(".docx")) {

                    encodedStr = encodedStr.replace("file://", "");
                    try {

                        encodedStr = URLDecoder.decode(encodedStr, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    File file = new File(encodedStr);
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

                } else if (offlinePath.toLowerCase().contains(".xlsx")
                        || offlinePath.toLowerCase().contains(".xls")) {
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

                } else if (offlinePath.toLowerCase().contains(".visio")
                        || offlinePath.toLowerCase().contains(".vsd")) {
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

                } else if (offlinePath.toLowerCase().contains(".txt")) {

                    try {

                        offlinePath = URLDecoder.decode(offlinePath, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    File file = new File(offlinePath);
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
//                context.startActivity(iWeb);
//                context.startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    ((Activity) context).startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                }
            }

        }
    }

    public static void relatedContentView(MyLearningModel myLearningModel, Context context) {
        Integer relatedCount = Integer.parseInt(myLearningModel.getRelatedContentCount());
        if (relatedCount > 0) {
            Intent intentDetail = new Intent(context, TrackList_Activity.class);
            intentDetail.putExtra("myLearningDetalData", myLearningModel);
            intentDetail.putExtra("ISTRACKLIST", false);
//            context.startActivity(intentDetail);
            ((Activity) context).startActivityForResult(intentDetail, COURSE_CLOSE_CODE);
        }
    }

    public static void contextMenuMethod(final View v, final int position, ImageButton btnselected, final MyLearningModel myLearningDetalData, final DownloadInterface downloadInterface) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.mylearning_contextmenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);
        menu.getItem(8).setVisible(false);
        menu.getItem(9).setVisible(false);
        menu.getItem(10).setVisible(false);

        File myFile = new File(myLearningDetalData.getOfflinepath());

        if (myFile.exists()) {

            menu.getItem(10).setVisible(true);

        } else {

            menu.getItem(10).setVisible(false);
        }

        if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("11") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("14") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("36") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("28") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("20") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("21") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("52")) {

            if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("11") && (myLearningDetalData.getMediatypeId().equalsIgnoreCase("3") || myLearningDetalData.getMediatypeId().equalsIgnoreCase("4"))) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
            } else {

                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
            }
            if (!myLearningDetalData.getStatus().contains("completed")) {
                menu.getItem(7).setVisible(true);

            }

        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {

            Integer relatedCount = Integer.parseInt(myLearningDetalData.getRelatedContentCount());
            if (relatedCount > 0) {
                menu.getItem(8).setVisible(true);
            }
        } else if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("688")) {

            menu.getItem(1).setVisible(false);

        } else {
            menu.getItem(2).setVisible(true);
            menu.getItem(5).setVisible(true);
            menu.getItem(1).setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("detail")) {
                    Intent intentDetail = new Intent(v.getContext(), MyLearningDetail_Activity.class);
                    intentDetail.putExtra("myLearningDetalData", myLearningDetalData);
//                    v.getContext().startActivity(intentDetail);
//                context.startActivity(iWeb);
//                context.startActivityForResult(iWeb, COURSE_CLOSE_CODE);
                    ((Activity) v.getContext()).startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);

                }
                if (item.getTitle().toString().equalsIgnoreCase("VIew")) {
                    GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, v.getContext());
                }
                if (item.getTitle().toString().equalsIgnoreCase("Report")) {

                    Intent intentReports = new Intent(v.getContext(), Reports_Activity.class);
                    intentReports.putExtra("myLearningDetalData", myLearningDetalData);
                    v.getContext().startActivity(intentReports);

                }
                if (item.getTitle().toString().equalsIgnoreCase("Download")) {
                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Related Content")) {
                    relatedContentView(myLearningDetalData, v.getContext());
                    Toast.makeText(v.getContext(), "You Clicked : " + item.getTitle() + " on position " + position, Toast.LENGTH_SHORT).show();

                }

                if (item.getTitle().toString().equalsIgnoreCase("Delete")) {
                    deleteDownloadedFile(v, myLearningDetalData, downloadInterface);

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
            }
        } else {

        }
    }


}
