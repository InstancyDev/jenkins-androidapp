package com.instancy.instancylearning.globalpackage;

import android.content.Context;
import android.util.Log;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CMIModel;
import com.instancy.instancylearning.models.LearnerSessionModel;
import com.instancy.instancylearning.models.StudentResponseModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.instancy.instancylearning.utils.Utilities;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Upendranath on 8/10/2017 Working on InstancyLearning.
 */

public class SynchData {


    Context _context;
    DatabaseHandler dbh;
    AppUserModel appUserModel;
    PreferencesManager preferencesManager;
    WebAPIClient webAPIClient;

    public SynchData(Context context) {
        this._context = context;
        dbh = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
        preferencesManager = PreferencesManager.getInstance();
//        appUserModel.setWebAPIUrl(preferencesManager.getStringValue(StaticValues.KEY_WEBAPIURL));
//        appUserModel.setUserIDValue(preferencesManager.getStringValue(StaticValues.KEY_USERID));
//        appUserModel.setSiteIDValue(preferencesManager.getStringValue(StaticValues.KEY_SITEID));
//        appUserModel.setUserName(preferencesManager.getStringValue(StaticValues.KEY_USERNAME));
//        appUserModel.setSiteURL(preferencesManager.getStringValue(StaticValues.KEY_SITEURL));
//        appUserModel.setAuthHeaders(preferencesManager.getStringValue(StaticValues.KEY_AUTHENTICATION));
        webAPIClient = new WebAPIClient(_context);
    }

    public void SyncData() {
        String bundlevalue1 = null;
        List<CMIModel> cmiList = new ArrayList<CMIModel>();
        Set<CMIModel> hs = new HashSet<>();

        List<CMIModel> cmiMylearningList = dbh.getAllCmiDownloadDataDetails();
//        cmiList.addAll(cmiMylearningList);
        hs.addAll(cmiMylearningList);
        List<CMIModel> cmitrackList = dbh.getAllCmiTrackListDetails();
        cmiList.addAll(cmitrackList);
//        hs.addAll(cmiMylearningList);
        List<CMIModel> cmiEventRelated = dbh.getAllCmiRelatedContentDetails();
//        cmiList.addAll(cmiEventRelated);
        hs.addAll(cmiMylearningList);
        cmiList.addAll(hs);

//        List<String> al = new ArrayList<>();
//// add elements to al, including duplicates
//        Set<String> hs = new HashSet<>();
//        hs.addAll(cmiList);
//        al.clear();
//        al.addAll(hs);

        for (CMIModel tempCmi : cmiList) {
            bundlevalue1 = String.valueOf(tempCmi.get_userId());
            StringBuilder sb = new StringBuilder();
            sb.append("<TrackedData><CMI>");
            sb.append("<ID>" + String.valueOf(tempCmi.get_Id()) + "</ID>");
            sb.append("<UserID>" + bundlevalue1 + "</UserID>");
            sb.append("<SCOID>" + String.valueOf(tempCmi.get_scoId())
                    + "</SCOID>");
            if (tempCmi.get_status().equals("") || tempCmi.get_status() == null
                    || tempCmi.get_status().equals("null")) {
                sb.append("<CoreLessonStatus></CoreLessonStatus>");

            } else {
                sb.append("<CoreLessonStatus>" + tempCmi.get_status()
                        + "</CoreLessonStatus>");
            }

            if (tempCmi.get_location().equals("")
                    || tempCmi.get_location() == null
                    || tempCmi.get_location().equals("null")) {
                sb.append("<CoreLessonLocation></CoreLessonLocation>");

            } else {
                sb.append("<CoreLessonLocation>" + tempCmi.get_location()
                        + "</CoreLessonLocation>");
            }
            if (tempCmi.get_suspenddata().equals("")
                    || tempCmi.get_suspenddata() == null
                    || tempCmi.get_suspenddata().equals("null")) {
                sb.append("<SuspendData></SuspendData>");

            } else {
                sb.append("<SuspendData>" + tempCmi.get_suspenddata()
                        + "</SuspendData>");
            }

            try {
                if (tempCmi.get_datecompleted().equals("")
                        || tempCmi.get_datecompleted() == null
                        || tempCmi.get_datecompleted().equals("null")) {
                    sb.append("<DateCompleted></DateCompleted>");

                } else {
//                    Calendar c = Calendar.getInstance();
//                    SimpleDateFormat nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssss");
//                    String presentDate = nowtime.format(c.getTime());
                    sb.append("<DateCompleted>" + tempCmi.get_datecompleted()
                            + "</DateCompleted>");
                }
            } catch (Exception ex) {
                sb.append("<DateCompleted></DateCompleted>");
            }

            sb.append("<NoOfAttempts>" + tempCmi.get_noofattempts()
                    + "</NoOfAttempts>");

            // need to send data  parentcontentid and parentscoid
            sb.append("<TrackScoID>" + String.valueOf(tempCmi.get_scoId()) + "</TrackScoID>");
            sb.append("<TrackContentID></TrackContentID>");


            // need to send parent obj type id
            sb.append("<TrackObjectTypeID>" + tempCmi.get_objecttypeid() + "</TrackObjectTypeID>");


            sb.append("<OrgUnitID>" + tempCmi.get_siteId() + "</OrgUnitID>");

            try {
                if (tempCmi.get_score().equals("")
                        || tempCmi.get_score() == null
                        || tempCmi.get_score().equals("null")) {
                    sb.append("<Score>0</Score>");
                } else {
                    sb.append("<Score>" + tempCmi.get_score() + "</Score>");
                }
            } catch (Exception ex) {
                sb.append("<Score>0</Score>");
            }
            sb.append("<ObjectTypeId>" + tempCmi.get_objecttypeid()
                    + "</ObjectTypeId>");


            try {
                if (tempCmi.get_seqNum().equals("")
                        || tempCmi.get_seqNum() == null
                        || tempCmi.get_seqNum().equals("null")) {
                    sb.append("<SequenceNumber>0</SequenceNumber>");
                } else {
                    sb.append("<SequenceNumber>" + tempCmi.get_seqNum()
                            + "</SequenceNumber>");
                }
            } catch (Exception ex) {
                sb.append("<SequenceNumber>0</SequenceNumber>");
            }

            try {
                if (tempCmi.get_attemptsleft().equals("")
                        || tempCmi.get_attemptsleft() == null
                        || tempCmi.get_attemptsleft().equals("null")) {
                    sb.append("<AttemptsLeft></AttemptsLeft>");
                } else {
                    sb.append("<AttemptsLeft>" + tempCmi.get_attemptsleft()
                            + "</AttemptsLeft>");
                }
            } catch (Exception ex) {
                sb.append("<AttemptsLeft></AttemptsLeft>");
            }
            try {
                if (tempCmi.get_coursemode().equals("")
                        || tempCmi.get_coursemode() == null
                        || tempCmi.get_coursemode().equals("null")) {
                    sb.append("<CoreLessonMode></CoreLessonMode>");
                } else {
                    sb.append("<CoreLessonMode>" + tempCmi.get_coursemode()
                            + "</CoreLessonMode>");
                }
            } catch (Exception ex) {
                sb.append("<CoreLessonMode></CoreLessonMode>");
            }
            try {
                if (tempCmi.get_scoremin().equals("")
                        || tempCmi.get_scoremin() == null
                        || tempCmi.get_scoremin().equals("null")) {
                    sb.append("<ScoreMin></ScoreMin>");
                } else {
                    sb.append("<ScoreMin>" + tempCmi.get_scoremin()
                            + "</ScoreMin>");
                }
            } catch (Exception ex) {
                sb.append("<ScoreMin></ScoreMin>");
            }
            try {
                if (tempCmi.get_scoremax().equals("")
                        || tempCmi.get_scoremax() == null
                        || tempCmi.get_scoremax().equals("null")) {
                    sb.append("<ScoreMax></ScoreMax>");
                } else {
                    sb.append("<ScoreMax>" + tempCmi.get_scoremax()
                            + "</ScoreMax>");
                }
            } catch (Exception ex) {
                sb.append("<ScoreMax></ScoreMax>");
            }
            try {
                if (tempCmi.get_qusseq().equals("")
                        || tempCmi.get_qusseq() == null
                        || tempCmi.get_qusseq().equals("null")) {
                    sb.append("<RandomQuestionNos></RandomQuestionNos>");
                } else {
                    sb.append("<RandomQuestionNos>" + tempCmi.get_qusseq()
                            + "</RandomQuestionNos>");
                }
            } catch (Exception ex) {
                sb.append("<RandomQuestionNos></RandomQuestionNos>");
            }
            try {
                if (tempCmi.get_textResponses().equals("")
                        || tempCmi.get_textResponses() == null
                        || tempCmi.get_textResponses().equals("null")) {
                    sb.append("<TextResponses></TextResponses>");
                } else {
                    sb.append("<TextResponses>" + tempCmi.get_textResponses()
                            + "</TextResponses>");
                }
            } catch (Exception ex) {
                sb.append("<TextResponses></TextResponses>");
            }
            try {
                if (tempCmi.get_pooledqusseq() == null
                        || tempCmi.get_pooledqusseq().equals("")
                        || tempCmi.get_pooledqusseq().equals("null")) {
                    sb.append("<PooledQuestionNos></PooledQuestionNos>");
                } else {
                    sb.append("<PooledQuestionNos>"
                            + tempCmi.get_pooledqusseq()
                            + "</PooledQuestionNos>");
                }
            } catch (Exception ex) {
                sb.append("<PooledQuestionNos></PooledQuestionNos>");
            }
            sb.append("</CMI>");

            List<LearnerSessionModel> sesList = dbh.getAllSessionDetails(
                    bundlevalue1, String.valueOf(tempCmi.get_siteId()),
                    String.valueOf(tempCmi.get_scoId()));
            if (sesList.size() > 0) {
                for (LearnerSessionModel tempSession : sesList) {
                    sb.append("<LearnerSession>");
//                    sb.append("<SessionID>"
//                            + String.valueOf(tempSession.getSessionID())
//                            + "</SessionID>");
                    sb.append("<SessionID></SessionID>");


                    sb.append("<UserID>" + bundlevalue1 + "</UserID>");
                    sb.append("<SCOID>" + String.valueOf(tempSession.getScoID())
                            + "</SCOID>");
                    sb.append("<AttemptNumber>"
                            + String.valueOf(tempSession.getAttemptNumber())
                            + "</AttemptNumber>");
                    sb.append("<SessionDateTime>"
                            + tempSession.getSessionDateTime()
                            + "</SessionDateTime>");
                    try {
                        if (tempSession.getTimeSpent().equals("")
                                || tempSession.getTimeSpent() == null
                                || tempSession.getTimeSpent().equals("null")) {
                            sb.append("<TimeSpent>0</TimeSpent>");

                        } else {
                            sb.append("<TimeSpent>" + tempSession.getTimeSpent()
                                    + "</TimeSpent>");
                        }
                    } catch (Exception ex) {
                        sb.append("<TimeSpent>0</TimeSpent>");
                    }

                    sb.append("</LearnerSession>");
                }
            } else {
                sb.append("<LearnerSession>");
                sb.append("</LearnerSession>");
            }
            try {
                List<StudentResponseModel> resList = dbh
                        .getAllResponseDetails(bundlevalue1,
                                String.valueOf(tempCmi.get_siteId()),
                                String.valueOf(tempCmi.get_scoId()));
                for (StudentResponseModel tempResponse : resList) {

                    String sampleStdntResponse = tempResponse
                            .get_studentresponses();
                    sampleStdntResponse = sampleStdntResponse.replace("\'",
                            "\\\'");
                    sampleStdntResponse = sampleStdntResponse.replace("\"",
                            "\\\"");
                    try {
                        sampleStdntResponse = sampleStdntResponse.replace(
                                "%23", "#");
                        sampleStdntResponse = sampleStdntResponse.replace(
                                "%5E", "^");
                        if (sampleStdntResponse.contains("#^#^")) {
                            sampleStdntResponse = sampleStdntResponse.replace(
                                    "#^#^", "@");
                        }
                        if (sampleStdntResponse.contains("##^^##^^")) {
                            sampleStdntResponse = sampleStdntResponse.replace(
                                    "##^^##^^", "&&**&&");
                            sampleStdntResponse = "[CDATA["
                                    + sampleStdntResponse + "]]";

                        }
                    } catch (Exception ex) {
                    }

                    sb.append("<StudentResponse>");
                    sb.append("<UserID>" + bundlevalue1 + "</UserID>");
                    sb.append("<SCOID>"
                            + String.valueOf(tempResponse.get_scoId())
                            + "</SCOID>");
                    sb.append("<QuestionID>"
                            + String.valueOf(tempResponse.get_questionid())
                            + "</QuestionID>");
                    sb.append("<AssessmentAttempt>"
                            + String.valueOf(tempResponse
                            .get_assessmentattempt())
                            + "</AssessmentAttempt>");
                    sb.append("<QuestionAttempt>"
                            + String.valueOf(tempResponse.get_questionattempt())
                            + "</QuestionAttempt>");
                    sb.append("<AttemptDate>" + tempResponse.get_attemptdate()
                            + "</AttemptDate>");
                    sb.append("<Response>" + sampleStdntResponse
                            + "</Response>");
                    sb.append("<Result>" + tempResponse.get_result()
                            + "</Result>");
                    sb.append("<OptionalNotes>"
                            + tempResponse.get_optionalNotes()
                            + "</OptionalNotes>");

                    try {
                        if (tempResponse.get_attachfilename().equals("")
                                || tempResponse.get_attachfilename() == null
                                || tempResponse.get_attachfilename().equals(
                                "null")) {
                            sb.append("<AttachFileName></AttachFileName>");

                        } else {
                            sb.append("<AttachFileName>"
                                    + tempResponse.get_attachfilename()
                                    + "</AttachFileName>");
                        }
                    } catch (Exception ex) {
                        sb.append("<AttachFileName></AttachFileName>");
                    }
                    try {
                        if (tempResponse.get_attachfileid().equals("")
                                || tempResponse.get_attachfileid() == null
                                || tempResponse.get_attachfileid().equals(
                                "null")) {
                            sb.append("<AttachFileId></AttachFileId>");

                        } else {
                            sb.append("<AttachFileId>"
                                    + tempResponse.get_attachfileid()
                                    + "</AttachFileId>");
                            File fil = new File(
                                    tempResponse.get_attachedfilepath());
//                            if (fil.exists()) {
//                                String temp = dbh
//                                        .uploadAttachedFile(tempResponse
//                                                .get_attachedfilepath());
//
//                                if (temp.contains("True")) {
//
//                                    fil.delete();
//                                }
//                            }
                        }
                    } catch (Exception ex) {

                    }
                    sb.append(bindXML("CapturedVidFileName",
                            tempResponse.get_capturedVidFileName(), ""));
                    sb.append(bindXML("CapturedVidId",
                            tempResponse.get_capturedVidId(),
                            tempResponse.get_capturedVidFilepath()));

                    sb.append(bindXML("CapturedImgFileName",
                            tempResponse.get_capturedImgFileName(), ""));
                    sb.append(bindXML("CapturedImgId",
                            tempResponse.get_capturedImgId(),
                            tempResponse.get_capturedImgFilepath()));

                    sb.append("</StudentResponse>");
                }
            } catch (Exception e) {
                Log.d("UpdateMessage:", e.getMessage());
            }
            sb.append("</TrackedData>");

            Log.d("SynchUpdateOffline", sb.toString());

            String requestURL = appUserModel.getWebAPIUrl()
                    + "/MobileLMS/MobileUpdateOfflineTrackedData"
                    + "?_studId=" + String.valueOf(tempCmi.get_userId())
                    + "&_scoId=" + String.valueOf(tempCmi.get_scoId())
                    + "&_siteURL=" + tempCmi.get_sitrurl()
                    + "&_siteID=" + tempCmi.get_siteId();
            InputStream inputStream = null;
            inputStream = webAPIClient.synchronousPostMethod(requestURL, appUserModel.getAuthHeaders(), sb.toString());
            if (inputStream != null) {

                String result = Utilities.convertStreamToString(inputStream);
                dbh.insertCMiIsViewd(tempCmi);
                Log.d("TAG", "SyncData: " + result);
            }
//            dbh.finishSynch(tempCmi);
//            dbh.sendOfflineUserPagenotes();
        }

        // if (errorMessage.equals("")) {
//          dbh.finishSynch();
        // }
    }

    private String bindXML(String tag, String value, String filePath) {
        StringBuilder sampleString = new StringBuilder();
        try {
            if (value == null || value.equals("") || value.equals("null")
                    || value.equals("undefined"))
                sampleString.append("<" + tag + "></" + tag + ">");
            else
                sampleString.append("<" + tag + ">" + value + "</" + tag + ">");
        } catch (Exception ex) {
            sampleString.append("<" + tag + "></" + tag + ">");
        }

//        if (filePath.length() != 0) {
//
//            File fil = new File(filePath);
//            if (fil.exists()) {
//                dbh.uploadAttachedFile(filePath);
//
//            }
//
//        }

        return sampleString.toString();
    }
}
