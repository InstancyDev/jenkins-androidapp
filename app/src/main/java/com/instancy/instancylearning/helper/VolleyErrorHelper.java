//package com.instancy.instancylearning.helper;
//
//import android.content.Context;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.NetworkError;
//import com.android.volley.NetworkResponse;
//import com.android.volley.NoConnectionError;
//import com.android.volley.ServerError;
//import com.android.volley.TimeoutError;
//import com.android.volley.VolleyError;
//import com.instancy.instancylearning.R;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Upendranath on 5/22/2017.
// */
//
//public class VolleyErrorHelper {
//
//    /**
//     * Returns appropriate message which is to be displayed to the user
//     * against the specified error object.
//     *
//     * @param error
//     * @param context
//     * @return
//     */
//    public static String getMessage(Object error, Context context) {
//        if (error instanceof TimeoutError) {
//            return context.getResources().getString(R.string.generic_server_down);
//        }
//        else if (isServerProblem(error)) {
//            return handleServerError(error, context);
//        }
//        else if (isNetworkProblem(error)) {
//            return context.getResources().getString(R.string.no_internet);
//        }
//        return context.getResources().getString(R.string.generic_error);
//    }
//
//    /**
//     * Determines whether the error is related to network
//     * @param error
//     * @return
//     */
//    private static boolean isNetworkProblem(Object error) {
//        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
//    }
//    /**
//     * Determines whether the error is related to server
//     * @param error
//     * @return
//     */
//    private static boolean isServerProblem(Object error) {
//        return (error instanceof ServerError) || (error instanceof AuthFailureError);
//    }
//    /**
//     * Handles the server error, tries to determine whether to show a stock message or to
//     * show a message retrieved from the server.
//     *
//     * @param err
//     * @param context
//     * @return
//     */
//    private static String handleServerError(Object err, Context context) {
//        VolleyError error = (VolleyError) err;
//
//        NetworkResponse response = error.networkResponse;
//
//        if (response != null) {
//            switch (response.statusCode) {
//                case 404:
//                case 422:
//                case 401:
//                    try {
//                        // server might return error like this { "error": "Some error occured" }
//                        // Use "Gson" to parse the result
//                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
//                                new TypeToken<Map<String, String>>() {
//                                }.getType());
//
//                        if (result != null && result.containsKey("error")) {
//                            return result.get("error");
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    // invalid request
//                    return error.getMessage();
//
//                default:
//                    return context.getResources().getString(R.string.generic_server_down);
//            }
//        }
//        return context.getResources().getString(R.string.generic_error);
//    }
//}
//public void saveCourseClose(String url, MyLearningModel learningModel) {
//        String prevStatus = "", submittime = "";
//        String subURL[] = url.split("/?");
//
//        Log.d("SubString: ", subURL[1]);
//        String surl = subURL[1];
//        String ss = "test-test1";
//        String urlScoId = "", lloc = "", status = "", susdata = "", quesdata = "", ltstatus = "", seqId = "", timespent = "", strRetake = "false", strScore = "";
//        String[] strSplitvalues = null;
//        try {
//        strSplitvalues = surl.split("=");
//        } catch (Exception e) {
//
//        }
//
//        if (learningModel.getObjecttypeId().equals("8") || learningModel.getObjecttypeId().equals("9")) {
//        try {
//        urlScoId = strSplitvalues[2].split("&")[0];
//        lloc = strSplitvalues[4].split("&")[0];
//        status = strSplitvalues[5].split("&")[0];
//        susdata = strSplitvalues[6].split("&")[0];
//        timespent = strSplitvalues[7].split("&")[0];
//        } catch (Exception ex) {
//        Log.d("Retake Issue:", surl);
//        }
//        try {
//
//        quesdata = strSplitvalues[8].split("&")[0];
//        if (surl.contains("&score="))
//        strScore = strSplitvalues[9].split("&")[0];
//
//        if (surl.contains("&retake=true"))
//        strRetake = strSplitvalues[10].split("&")[0];
//
//        } catch (Exception ex) {
//        Log.d("Retake Issue:", surl);
//        }
//        }
//        if (learningModel.getObjecttypeId().equals("10")) {
//        try {
//        urlScoId = strSplitvalues[2].split("&")[0];
//        lloc = strSplitvalues[5].split("&")[0];
//        status = strSplitvalues[6].split("&")[0];
//        susdata = strSplitvalues[7].split("&")[0];
//        timespent = strSplitvalues[8].split("&")[0];
//        quesdata = strSplitvalues[9].split("&")[0];
//        ltstatus = strSplitvalues[10].split("&")[0];
//        seqId = strSplitvalues[3].split("&")[0];
//        } catch (Exception e) {
//
//        }
//        try {
//        if (surl.contains("&score=")) {
//        strScore = surl.split("&score=")[1];
//        if (strScore.contains("&")) {
//        strScore = strScore.split("&")[0];
//        }
//        }
//        if (surl.contains("&retake=true"))
//        strRetake = "true";
//
//        } catch (Exception ex) {
//        Log.d("Retake ISSue:", surl);
//
//        }
//        }
//
//        CMIModel cmiDetails = new CMIModel();
//        cmiDetails.set_datecompleted("");
//        cmiDetails.set_siteId(learningModel.getSiteID());
//        cmiDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
//        cmiDetails.set_objecttypeid(learningModel.getObjecttypeId());
//        cmiDetails.set_scoId(Integer.parseInt(urlScoId));
//        cmiDetails.set_location(lloc);
//        cmiDetails.set_isupdate("false");
//        cmiDetails.set_startdate("");
//        cmiDetails.set_timespent(timespent);
//        if (learningModel.getObjecttypeId().equals("8")) {
//        cmiDetails.set_status(status);
//        cmiDetails.set_suspenddata(susdata.replaceAll(
//        "CourseDiscussionsPage.aspx?ContentID", ""));
//        cmiDetails.set_seqNum("0");
//        }
//        if (learningModel.getObjecttypeId().equals("10")) {
//        cmiDetails.set_status(ltstatus);
//        cmiDetails.set_suspenddata("");
//        cmiDetails.set_seqNum(seqId);
//        }
//
//        if (learningModel.getObjecttypeId().equals("9") || learningModel.getObjecttypeId().equals("8")) {
//        cmiDetails.set_suspenddata(susdata);
//        cmiDetails.set_seqNum("0");
//        cmiDetails.set_score(strScore);
//        prevStatus = cmiDetails.get_status();
//
//        if (!prevStatus.toLowerCase().equals("passed")) {
//        cmiDetails.set_status(status);
//        String strPresStatus = cmiDetails.get_status();
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals("passed")
//        || strPresStatus.toLowerCase().equals("failed")) {
//        cmiDetails.set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiDetails.set_datecompleted("");
//        }
//        } else {
//        cmiDetails.set_status("passed");
//        }
//        } else {
//
//        if (!prevStatus.toLowerCase().equals("completed")) {
//        String strPresStatus = cmiDetails.get_status();
//        if (strPresStatus.toLowerCase().equals("completed")) {
//        cmiDetails.set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiDetails.set_datecompleted("");
//        }
//        }
//        }
//        cmiDetails.set_submittime("");
//        cmiDetails.set_objecttypeid(learningModel.getObjecttypeId());
//        cmiDetails.set_sitrurl(learningModel.getSiteURL());
//        // cmiDetails.set_score("");
//
//        int seqNo = insertCMI(cmiDetails, true);
//        submittime = "";
//        int lastAttempt = getLatestAttempt(learningModel);
//
//        LearnerSessionModel sessionDetails = new LearnerSessionModel();
//        sessionDetails.setSiteID(learningModel.getSiteID());
//        sessionDetails.setUserID(learningModel.getUserID());
//        sessionDetails.setScoID(learningModel.getScoId());
//        sessionDetails.setAttemptNumber(lastAttempt);
//        sessionDetails.setTimeSpent(timespent);
//        // sessionDetails.set_sessiondatetime(dateStr);
//        insertUserSession(sessionDetails);
//
//        try {
//        if (learningModel.getObjecttypeId().equals("10")) {
//        String objScoDetails = GetObjectScoDetails(learningModel);
//
//        String[] objArray = objScoDetails.split("[$]");
//        int trackObjScoId = Integer.parseInt(objArray[0]);
//        String trackObjTypeId = objArray[1];
//        String objPrevStatus = GetPreviousStatus(trackObjScoId,
//        Integer.parseInt(learningModel.getUserID()), Integer.parseInt(learningModel.getSiteID()));
//        CMIModel cmiObjeDetails = new CMIModel();
//        cmiObjeDetails.set_datecompleted("");
//        cmiObjeDetails.set_siteId((learningModel.getSiteID()));
//        cmiObjeDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
//        cmiObjeDetails.set_scoId(trackObjScoId);
//        cmiObjeDetails.set_location(lloc);
//        cmiObjeDetails.set_isupdate("false");
//        cmiObjeDetails.set_startdate("");
//        cmiObjeDetails.set_timespent(timespent);
//        try {
//        if (!objPrevStatus.toLowerCase().equals("completed")
//        || !objPrevStatus.toLowerCase()
//        .equals("passed")) {
//        String strPresStatus = status;
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals(
//        "passed")
//        || strPresStatus.toLowerCase().equals(
//        "failed")) {
//        cmiObjeDetails
//        .set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiObjeDetails.set_datecompleted("");
//        }
//        }
//        } catch (Exception e) {
//        String strPresStatus = status;
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals("passed")
//        || strPresStatus.toLowerCase().equals("failed")) {
//        cmiObjeDetails
//        .set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiObjeDetails.set_datecompleted("");
//        }
//        }
//        cmiObjeDetails.set_status(status);
//        cmiObjeDetails.set_suspenddata(susdata);
//        cmiObjeDetails.set_objecttypeid(trackObjTypeId);
//        cmiObjeDetails.set_score(strScore);
//        cmiDetails.set_sitrurl(learningModel.getSiteURL());
//        int objSeqNo = insertCMI(cmiObjeDetails, true);
//
//        int objlastAttempt = getLatestAttempt(learningModel);
//        LearnerSessionModel objSession = new LearnerSessionModel();
//        objSession.setSiteID(learningModel.getSiteID());
//        objSession.setUserID(learningModel.getUserID());
//        objSession.setScoID(learningModel.getTrackScoid());
//        objSession.setAttemptNumber("");
//        objSession.setTimeSpent(timespent);
//        insertUserSession(objSession);
//        // Student Responses
//
//        try {
//        String strQuesData = quesdata;
//        // if (!strQuesData.equals("")) {
//        String[] quesArray = strQuesData.split("[$]");
//        Log.d("QuestionsCOunt: ",
//        String.valueOf(quesArray.length));
//        int assessmentAttempt = Getassessmentattempt(
//        learningModel, strRetake);
//        for (int i = 0; i < quesArray.length; i++) {
//        // 1@00:00:00@1@correct$2@00:00:00@1@correc
//        String[] resArray = quesArray[i].split("[@]");
//        if (resArray.length > 3) {
//        StudentResponseModel stdObjResDetails = new StudentResponseModel();
//        stdObjResDetails.set_scoId(trackObjScoId);
//        stdObjResDetails.set_siteId((learningModel.getSiteID());
//        stdObjResDetails.set_userId(Integer
//        .parseInt(learningModel.getUserID()));
//        if (trackObjTypeId.equals("8")) {
//        stdObjResDetails.set_questionid(Integer
//        .parseInt(resArray[0]) + 1);
//        } else {
//        stdObjResDetails.set_questionid(Integer
//        .parseInt(resArray[0]));
//        }
//        stdObjResDetails
//        .set_studentresponses(resArray[2]);
//        stdObjResDetails.set_result(resArray[3]);
//        stdObjResDetails
//        .set_assessmentattempt(assessmentAttempt);
//        String formattedDate = GetCurrentDateTime();
//        stdObjResDetails.set_attemptdate(formattedDate);
//        if (resArray.length > 4) {
//        String tempOptionalNotes = resArray[4];
//        if (tempOptionalNotes.contains("^notes^")) {
//        tempOptionalNotes = tempOptionalNotes
//        .replace("^notes^", "");
//        stdObjResDetails
//        .set_optionalNotes(tempOptionalNotes);
//        } else {
//        if (resArray.length > 5) {
//        stdObjResDetails
//        .set_attachfilename(resArray[4]);
//        stdObjResDetails
//        .set_attachfileid(resArray[5]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/Offline_Attachments/";
//        stdObjResDetails
//        .set_attachedfilepath(strManyDirectories
//        + resArray[5]);
//        }
//        }
//        if (resArray.length > 6) {
//
//        if (resArray[6].length() == 0
//        && resArray[6]
//        .equals("undefined")) {
//        stdObjResDetails
//        .set_capturedVidFileName("");
//        stdObjResDetails
//        .set_capturedVidId("");
//        stdObjResDetails
//        .set_capturedVidFilepath("");
//        }
//
//        stdObjResDetails
//        .set_capturedVidFileName(resArray[6]);
//        stdObjResDetails
//        .set_capturedVidId(resArray[7]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/mediaresource/mediacapture/";
//        stdObjResDetails
//        .set_capturedVidFilepath(strManyDirectories
//        + resArray[7]);
//        }
//        if (resArray.length > 8) {
//
//        if (resArray[8].length() == 0
//        && resArray[8]
//        .equals("undefined")) {
//        stdObjResDetails
//        .set_capturedImgFileName("");
//        stdObjResDetails
//        .set_capturedImgId("");
//        stdObjResDetails
//        .set_capturedImgFilepath("");
//        }
//
//        stdObjResDetails
//        .set_capturedImgFileName(resArray[8]);
//        stdObjResDetails
//        .set_capturedImgId(resArray[9]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/mediaresource/mediacapture/";
//        stdObjResDetails
//        .set_capturedImgFilepath(strManyDirectories
//        + resArray[9]);
//        }
//
//        }
//
//        insertStudentResponses(stdObjResDetails);
//        }
//        }
//        // }
//        } catch (Exception ex) {
//
//        }
//        }
//        } catch (Exception e) {
//        // Log.d("SpliteSrror",e.getMessage());
//        }
//
//        try {
//        String strQuesData = quesdata;
//        if (learningModel.getObjecttypeId().equals("9") || learningModel.getObjecttypeId().equals("8")) {
//        String[] quesArray = strQuesData.split("[$]");
//        Log.d("QuestionsCOunt: ", String.valueOf(quesArray.length));
//        int assessmentAttempt = Getassessmentattempt(learningModel, strRetake);
//        for (int i = 0; i < quesArray.length; i++) {
//        // 1@00:00:00@1@correct$2@00:00:00@1@correc
//        String[] resArray = quesArray[i].split("[@]");
//        if (resArray.length > 3) {
//        StudentResponseModel stdResDetails = new StudentResponseModel();
//        stdResDetails.set_scoId(Integer.parseInt(learningModel.getScoId()));
//        stdResDetails.set_siteId(learningModel.getSiteID());
//        stdResDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
//        if (learningModel.getObjecttypeId().equals("8")) {
//        stdResDetails.set_questionid(Integer
//        .parseInt(resArray[0]) + 1);
//        } else {
//        stdResDetails.set_questionid(Integer
//        .parseInt(resArray[0]));
//        }
//        stdResDetails.set_studentresponses(resArray[2]);
//        stdResDetails.set_result(resArray[3]);
//        stdResDetails
//        .set_assessmentattempt(assessmentAttempt);
//        stdResDetails.set_attemptdate(GetCurrentDateTime());
//        if (resArray.length > 4) {
//
//        String tempOptionalNotes = resArray[4];
//        if (tempOptionalNotes.contains("^notes^")) {
//        tempOptionalNotes = tempOptionalNotes
//        .replace("^notes^", "");
//        stdResDetails
//        .set_optionalNotes(tempOptionalNotes);
//        } else {
//        if (resArray.length > 5) {
//        stdResDetails
//        .set_attachfilename(resArray[4]);
//        stdResDetails
//        .set_attachfileid(resArray[5]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/Offline_Attachments/";
//        stdResDetails
//        .set_attachedfilepath(strManyDirectories
//        + resArray[5]);
//        }
//        }
//
//        if (resArray.length > 6) {
//
//        if (resArray[6].length() == 0
//        && resArray[6].equals("undefined")) {
//        stdResDetails
//        .set_capturedVidFileName("");
//        stdResDetails.set_capturedVidId("");
//        stdResDetails
//        .set_capturedVidFilepath("");
//        }
//
//        stdResDetails
//        .set_capturedVidFileName(resArray[6]);
//        stdResDetails
//        .set_capturedVidId(resArray[7]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/mediaresource/mediacapture/";
//        stdResDetails
//        .set_capturedVidFilepath(strManyDirectories
//        + resArray[7]);
//        }
//        if (resArray.length > 8) {
//
//        if (resArray[8].length() == 0
//        && resArray[8].equals("undefined")) {
//        stdResDetails
//        .set_capturedImgFileName("");
//        stdResDetails.set_capturedImgId("");
//        stdResDetails
//        .set_capturedImgFilepath("");
//        }
//
//        stdResDetails
//        .set_capturedImgFileName(resArray[8]);
//        stdResDetails
//        .set_capturedImgId(resArray[9]);
//        String strManyDirectories = bundlevalue
//        .substring(1, bundlevalue
//        .lastIndexOf("/"))
//        + "/mediaresource/mediacapture/";
//        stdResDetails
//        .set_capturedImgFilepath(strManyDirectories
//        + resArray[9]);
//        }
//        }
//        insertStudentResponses(stdResDetails);
//        }
//        }
//        }
//        } catch (Exception ex) {
//
//        }
////        dbh.deleteAttachedFile(userid, siteId, scoId,
////                bundlevalue.substring(1, bundlevalue.lastIndexOf("/")));
//
//        }

//second worked
//public void saveCourseClose(String url, MyLearningModel learningModel) {
//        String prevStatus = "", submittime = "";
//        String subURL[] = url.split("\\?");
//
//        Log.d("SubString: ", subURL[1]);
//        String surl = subURL[1];
//        String ss = "test-test1";
//        String urlScoId = "", lloc = "", status = "", susdata = "", quesdata = "", ltstatus = "", seqId = "", timespent = "", strRetake = "false", strScore = "";
//        String[] strSplitvalues = null;
//        try {
//        strSplitvalues = surl.split("=");
//        } catch (Exception e) {
//
//        }
//
//        if (learningModel.getObjecttypeId().equals("8") || learningModel.getObjecttypeId().equals("9")) {
//        try {
//        urlScoId = strSplitvalues[2].split("&")[0];
//        lloc = strSplitvalues[4].split("&")[0];
//        status = strSplitvalues[5].split("&")[0];
//        susdata = strSplitvalues[6].split("&")[0];
//        timespent = strSplitvalues[7].split("&")[0];
//        } catch (Exception ex) {
//        Log.d("Retake Issue:", surl);
//        }
//        try {
//
//        quesdata = strSplitvalues[8].split("&")[0];
//        if (surl.contains("&score="))
//        strScore = strSplitvalues[9].split("&")[0];
//
//        if (surl.contains("&retake=true"))
//        strRetake = strSplitvalues[10].split("&")[0];
//
//        } catch (Exception ex) {
//        Log.d("Retake Issue:", surl);
//        }
//        }
//        if (learningModel.getObjecttypeId().equals("10")) {
//        try {
//        urlScoId = strSplitvalues[2].split("&")[0];
//        lloc = strSplitvalues[5].split("&")[0];
//        status = strSplitvalues[6].split("&")[0];
//        susdata = strSplitvalues[7].split("&")[0];
//        timespent = strSplitvalues[8].split("&")[0];
//        quesdata = strSplitvalues[9].split("&")[0];
//        ltstatus = strSplitvalues[10].split("&")[0];
//        seqId = strSplitvalues[3].split("&")[0];
//        } catch (Exception e) {
//
//        }
//        try {
//        if (surl.contains("&score=")) {
//        strScore = surl.split("&score=")[1];
//        if (strScore.contains("&")) {
//        strScore = strScore.split("&")[0];
//        }
//        }
//        if (surl.contains("&retake=true"))
//        strRetake = "true";
//
//        } catch (Exception ex) {
//        Log.d("Retake ISSue:", surl);
//
//        }
//        }
//
//        CMIModel cmiDetails = new CMIModel();
//        cmiDetails.set_datecompleted("");
//        cmiDetails.set_siteId(learningModel.getSiteID());
//        cmiDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
//        cmiDetails.set_objecttypeid(learningModel.getObjecttypeId());
//        cmiDetails.set_scoId(Integer.parseInt(learningModel.getScoId()));
//        cmiDetails.set_location(lloc);
//        cmiDetails.set_isupdate("false");
//        cmiDetails.set_startdate("");
//        cmiDetails.set_timespent(timespent);
//        if (learningModel.getObjecttypeId().equals("8")) {
//        cmiDetails.set_status(status);
//        cmiDetails.set_suspenddata(susdata.replaceAll(
//        "CourseDiscussionsPage.aspx?ContentID", ""));
//        cmiDetails.set_seqNum("0");
//        }
//        if (learningModel.getObjecttypeId().equals("10")) {
//        cmiDetails.set_status(ltstatus);
//        cmiDetails.set_suspenddata("");
//        cmiDetails.set_seqNum(seqId);
//        }
//
//        if (learningModel.getObjecttypeId().equals("9") || learningModel.getObjecttypeId().equals("8")) {
//        cmiDetails.set_suspenddata(susdata);
//        cmiDetails.set_seqNum("0");
//        cmiDetails.set_score(strScore);
//        prevStatus = cmiDetails.get_status();
//
//        if (!prevStatus.toLowerCase().equals("passed")) {
//        cmiDetails.set_status(status);
//        String strPresStatus = cmiDetails.get_status();
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals("passed")
//        || strPresStatus.toLowerCase().equals("failed")) {
//        cmiDetails.set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiDetails.set_datecompleted("");
//        }
//        } else {
//        cmiDetails.set_status("passed");
//        }
//        } else {
//
//        if (!prevStatus.toLowerCase().equals("completed")) {
//        String strPresStatus = cmiDetails.get_status();
//        if (strPresStatus.toLowerCase().equals("completed")) {
//        cmiDetails.set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiDetails.set_datecompleted("");
//        }
//        }
//        }
//        cmiDetails.set_submittime("");
//        cmiDetails.set_objecttypeid(learningModel.getObjecttypeId());
//        cmiDetails.set_sitrurl(learningModel.getSiteURL());
//        // cmiDetails.set_score("");
//
//        int seqNo = insertCMI(cmiDetails, true);
//        submittime = "";
//        int lastAttempt = getLatestAttempt(learningModel);
//
//        LearnerSessionModel sessionDetails = new LearnerSessionModel();
//        sessionDetails.setSiteID(learningModel.getSiteID());
//        sessionDetails.setUserID(learningModel.getUserID());
//        sessionDetails.setScoID(learningModel.getScoId());
//        sessionDetails.setAttemptNumber("" + lastAttempt);
//        sessionDetails.setTimeSpent(timespent);
//        // sessionDetails.set_sessiondatetime(dateStr);
//        insertUserSession(sessionDetails);
//
//        try {
//        if (learningModel.getObjecttypeId().equals("10")) {
//        String objScoDetails = GetObjectScoDetails(learningModel);
//
//        String[] objArray = objScoDetails.split("[$]");
//        int trackObjScoId = Integer.parseInt(objArray[0]);
//        String trackObjTypeId = objArray[1];
//        String objPrevStatus = GetPreviousStatus(trackObjScoId,
//        Integer.parseInt(learningModel.getUserID()), Integer.parseInt(learningModel.getSiteID()));
//        CMIModel cmiObjeDetails = new CMIModel();
//        cmiObjeDetails.set_datecompleted("");
//        cmiObjeDetails.set_siteId((learningModel.getSiteID()));
//        cmiObjeDetails.set_userId(Integer.parseInt(learningModel.getUserID()));
//        cmiObjeDetails.set_scoId(trackObjScoId);
//        cmiObjeDetails.set_location(lloc);
//        cmiObjeDetails.set_isupdate("false");
//        cmiObjeDetails.set_startdate("");
//        cmiObjeDetails.set_timespent(timespent);
//        try {
//        if (!objPrevStatus.toLowerCase().equals("completed")
//        || !objPrevStatus.toLowerCase()
//        .equals("passed")) {
//        String strPresStatus = status;
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals(
//        "passed")
//        || strPresStatus.toLowerCase().equals(
//        "failed")) {
//        cmiObjeDetails
//        .set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiObjeDetails.set_datecompleted("");
//        }
//        }
//        } catch (Exception e) {
//        String strPresStatus = status;
//        if (strPresStatus.toLowerCase().equals("completed")
//        || strPresStatus.toLowerCase().equals("passed")
//        || strPresStatus.toLowerCase().equals("failed")) {
//        cmiObjeDetails
//        .set_datecompleted(GetCurrentDateTime());
//        } else {
//        cmiObjeDetails.set_datecompleted("");
//        }
//        }
//        cmiObjeDetails.set_status(status);
//        cmiObjeDetails.set_suspenddata(susdata);
//        cmiObjeDetails.set_objecttypeid(trackObjTypeId);
//        cmiObjeDetails.set_score(strScore);
//        cmiDetails.set_sitrurl(learningModel.getSiteURL());
//        int objSeqNo = insertCMI(cmiObjeDetails, true);
//
//        int objlastAttempt = getLatestAttempt(learningModel);
//        LearnerSessionModel objSession = new LearnerSessionModel();
//        objSession.setSiteID(learningModel.getSiteID());
//        objSession.setUserID(learningModel.getUserID());
//        objSession.setScoID(learningModel.getTrackScoid());
//        objSession.setAttemptNumber("");
//        objSession.setTimeSpent(timespent);
//        insertUserSession(objSession);
//        // Student Responses
//
//
//        }
//
//        } catch (Exception e) {
//        // Log.d("SpliteSrror",e.getMessage());
//        }
//
//
////        dbh.deleteAttachedFile(userid, siteId, scoId,
////                bundlevalue.substring(1, bundlevalue.lastIndexOf("/")));
//
//        }
