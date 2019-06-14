package com.instancy.instancylearning.askexpertenached;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.Spanned;
import android.util.Log;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.AskExpertCategoriesModel;
import com.instancy.instancylearning.models.AskExpertCategoriesModelMapping;
import com.instancy.instancylearning.models.AskExpertSkillsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.convertDateToSortDateFormat;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.fromHtmlToString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class AskExpertDbTables extends DatabaseHandler {


    AppUserModel appUserModel;
    private Context dbctx;
    DatabaseHandler databaseHandler;


    public AskExpertDbTables(Context context) {
        super(context);
        dbctx = context;
        databaseHandler = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
    }

    public void injectAsktheExpertQuestionDataIntoTable(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);


        JSONArray jsonTableAry = jsonObject.getJSONArray("QuestionList");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKQUESTIONS_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);


            AskExpertQuestionModelDg askExpertsQuestionModel = new AskExpertQuestionModelDg();


            askExpertsQuestionModel.questionID = jsonMyLearningColumnObj.optInt("QuestionID");
            askExpertsQuestionModel.userID = jsonMyLearningColumnObj.optInt("UserID");
            askExpertsQuestionModel.userName = jsonMyLearningColumnObj.optString("UserName");

            askExpertsQuestionModel.userQuestion = fromHtmlToString(jsonMyLearningColumnObj.get("UserQuestion").toString());

            askExpertsQuestionModel.postedDate = jsonMyLearningColumnObj.optString("PostedDate");
            askExpertsQuestionModel.createdDate = jsonMyLearningColumnObj.optString("CreatedDate");
            askExpertsQuestionModel.totalAnswers = jsonMyLearningColumnObj.optInt("Answers");
            askExpertsQuestionModel.questionCategories = jsonMyLearningColumnObj.optString("QuestionCategories");

            askExpertsQuestionModel.userQuestionDescription = fromHtmlToString(jsonMyLearningColumnObj.get("UserQuestionDescription").toString());

            askExpertsQuestionModel.userQuestionImage = jsonMyLearningColumnObj.optString("UserQuestionImage");
            askExpertsQuestionModel.lastActivatedDate = jsonMyLearningColumnObj.optString("LastActivatedDate");
            askExpertsQuestionModel.totalViews = jsonMyLearningColumnObj.optInt("Views");
            askExpertsQuestionModel.objectID = jsonMyLearningColumnObj.optString("ObjectID");
            askExpertsQuestionModel.userImage = jsonMyLearningColumnObj.optString("UserImage");
            askExpertsQuestionModel.actionsLink = jsonMyLearningColumnObj.optString("ActionsLink");
            askExpertsQuestionModel.userQuestionImagePath = jsonMyLearningColumnObj.optString("UserQuestionImagePath");
            askExpertsQuestionModel.answerBtnWithLink = jsonMyLearningColumnObj.optString("AnswerBtnWithLink");


            injectAsktheExpertQuestionDataIntoSqLites(askExpertsQuestionModel);
        }

    }

    public void injectAsktheExpertQuestionDataIntoSqLites(AskExpertQuestionModelDg askExpertQuestionModelDg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("siteID", askExpertQuestionModelDg.siteID);
            contentValues.put("questionID", askExpertQuestionModelDg.questionID);
            contentValues.put("createduserID", askExpertQuestionModelDg.userID);
            contentValues.put("userID", appUserModel.getUserIDValue());
            contentValues.put("userName", askExpertQuestionModelDg.userName);
            contentValues.put("userQuestion", askExpertQuestionModelDg.userQuestion);
            contentValues.put("postedDate", askExpertQuestionModelDg.postedDate);
            contentValues.put("createdDate", askExpertQuestionModelDg.createdDate);
            contentValues.put("totalAnswers", askExpertQuestionModelDg.totalAnswers);
            contentValues.put("questionCategories", askExpertQuestionModelDg.questionCategories);
            contentValues.put("userQuestionImage", askExpertQuestionModelDg.userQuestionImage);
            contentValues.put("totalViews", askExpertQuestionModelDg.totalViews);
            contentValues.put("objectID", askExpertQuestionModelDg.objectID);
            contentValues.put("userImage", askExpertQuestionModelDg.userImage);
            contentValues.put("actionsLink", askExpertQuestionModelDg.actionsLink);
            contentValues.put("userQuestionImagePath", askExpertQuestionModelDg.userQuestionImagePath);
            contentValues.put("userQuestionDescription", askExpertQuestionModelDg.userQuestionDescription);
            contentValues.put("answerBtnWithLink", askExpertQuestionModelDg.answerBtnWithLink);

            db.insert(TBL_ASKQUESTIONS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<AskExpertQuestionModelDg> fetchAskExpertsQuestions(String categoryID) {
        List<AskExpertQuestionModelDg> askExpertQuestionModelDgList = null;
        AskExpertQuestionModelDg askExpertQuestionModelDg = new AskExpertQuestionModelDg();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "";
        if (categoryID.length() == 0) {
            strSelQuerys = "SELECT * FROM " + TBL_ASKQUESTIONS_DIGI + " WHERE USERID = " + appUserModel.getUserIDValue() + " AND SITEID = " + appUserModel.getSiteIDValue() + " ORDER BY QUESTIONID DESC";
        } else {
            strSelQuerys = "SELECT * FROM " + TBL_ASKQUESTIONS_DIGI + " AQ LEFT OUTER JOIN " + TBL_ASKQUESTIONCATEGORYMAPPING_DIGI + " AQC ON AQ.QUESTIONID = AQC.QUESTIONID WHERE AQ.USERID = " + appUserModel.getUserIDValue() + " AND AQ.SITEID = " + appUserModel.getSiteIDValue() + " AND AQC.CATEGORYID = " + categoryID + " ORDER BY AQ.QUESTIONID DESC";
        }

        Log.d(TAG, "fetchCatalogModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertQuestionModelDgList = new ArrayList<AskExpertQuestionModelDg>();
                do {

                    askExpertQuestionModelDg = new AskExpertQuestionModelDg();

                    askExpertQuestionModelDg.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    askExpertQuestionModelDg.userID = cursor.getInt(cursor.getColumnIndex("userID"));

                    askExpertQuestionModelDg.createduserID = cursor.getInt(cursor
                            .getColumnIndex("createduserID"));


                    askExpertQuestionModelDg.userName = cursor.getString(cursor
                            .getColumnIndex("userName"));

                    askExpertQuestionModelDg.userQuestion = cursor.getString(cursor
                            .getColumnIndex("userQuestion"));

                    askExpertQuestionModelDg.postedDate = cursor.getString(cursor
                            .getColumnIndex("postedDate"));

                    askExpertQuestionModelDg.createdDate = cursor.getString(cursor
                            .getColumnIndex("createdDate"));

                    askExpertQuestionModelDg.totalAnswers = cursor.getInt(cursor
                            .getColumnIndex("totalAnswers"));

                    askExpertQuestionModelDg.questionCategories = cursor.getString(cursor
                            .getColumnIndex("questionCategories"));

                    askExpertQuestionModelDg.userQuestionImage = cursor.getString(cursor
                            .getColumnIndex("userQuestionImage"));

                    askExpertQuestionModelDg.questionID = cursor.getInt(cursor
                            .getColumnIndex("questionID"));

                    askExpertQuestionModelDg.totalViews = cursor.getInt(cursor
                            .getColumnIndex("totalViews"));

                    askExpertQuestionModelDg.objectID = cursor.getString(cursor
                            .getColumnIndex("objectID"));

                    askExpertQuestionModelDg.userImage = cursor.getString(cursor
                            .getColumnIndex("userImage"));

                    askExpertQuestionModelDg.actionsLink = cursor.getString(cursor
                            .getColumnIndex("actionsLink"));

                    askExpertQuestionModelDg.userQuestionImagePath = cursor.getString(cursor
                            .getColumnIndex("userQuestionImagePath"));

                    askExpertQuestionModelDg.userQuestionDescription = cursor.getString(cursor
                            .getColumnIndex("userQuestionDescription"));

                    askExpertQuestionModelDg.answerBtnWithLink = cursor.getString(cursor
                            .getColumnIndex("answerBtnWithLink"));

                    askExpertQuestionModelDg.lastActivatedDateFormat = convertDateToSortDateFormat(askExpertQuestionModelDg.postedDate);

                    askExpertQuestionModelDg.questionCategoriesArray = getArrayListFromString(askExpertQuestionModelDg.questionCategories);
                    askExpertQuestionModelDgList.add(askExpertQuestionModelDg);
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
        return askExpertQuestionModelDgList;
    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }

    public void injectAsktheExpertsSkills(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKQUESTIONSKILLS_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertSkillsModelDg askExpertSkillsModel = new AskExpertSkillsModelDg();
            //questionid
            if (jsonMyLearningColumnObj.has("OrgUnitID")) {

                askExpertSkillsModel.orgUnitID = jsonMyLearningColumnObj.getString("OrgUnitID");
            }
            // response
            if (jsonMyLearningColumnObj.has("PreferrenceID")) {

                askExpertSkillsModel.preferrenceID = jsonMyLearningColumnObj.get("PreferrenceID").toString();

            }
            // responseid
            if (jsonMyLearningColumnObj.has("PreferrenceTitle")) {

                askExpertSkillsModel.preferrenceTitle = jsonMyLearningColumnObj.get("PreferrenceTitle").toString();

            }
            // respondeduserid
            if (jsonMyLearningColumnObj.has("ShortSkillName")) {

                askExpertSkillsModel.shortSkillName = jsonMyLearningColumnObj.get("ShortSkillName").toString();

            }

            askExpertSkillsModel.siteID = appUserModel.getSiteIDValue();

            injectAsktheExpertSkillsDataIntoSqLite(askExpertSkillsModel);
        }

    }


    public void injectAsktheExpertSkillsDataIntoSqLite(AskExpertSkillsModelDg askExpertSkillsModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("orgunitid", askExpertSkillsModel.orgUnitID);
            contentValues.put("preferrenceid", askExpertSkillsModel.preferrenceID);
            contentValues.put("preferrencetitle", askExpertSkillsModel.preferrenceTitle);
            contentValues.put("shortskillname", askExpertSkillsModel.shortSkillName);
            contentValues.put("siteid", askExpertSkillsModel.siteID);

            db.insert(TBL_ASKQUESTIONSKILLS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public List<AskExpertSkillsModelDg> fetchAskExpertSkillsList() {
        List<AskExpertSkillsModelDg> askExpertQuestionModelList = null;
        AskExpertSkillsModelDg askExpertSkillsModel = new AskExpertSkillsModelDg();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "SELECT * FROM " + TBL_ASKQUESTIONSKILLS_DIGI + " WHERE siteid  = " + appUserModel.getSiteIDValue();

        Log.d(TAG, "fetchCatalogModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertQuestionModelList = new ArrayList<AskExpertSkillsModelDg>();
                do {

                    askExpertSkillsModel = new AskExpertSkillsModelDg();

                    askExpertSkillsModel.orgUnitID = cursor.getString(cursor
                            .getColumnIndex("orgunitid"));

                    askExpertSkillsModel.preferrenceID = cursor.getString(cursor
                            .getColumnIndex("preferrenceid"));

                    askExpertSkillsModel.preferrenceTitle = cursor.getString(cursor
                            .getColumnIndex("preferrencetitle"));

                    askExpertSkillsModel.shortSkillName = cursor.getString(cursor
                            .getColumnIndex("shortskillname"));

                    askExpertSkillsModel.siteID = cursor.getString(cursor
                            .getColumnIndex("siteid"));

                    askExpertQuestionModelList.add(askExpertSkillsModel);
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

        return askExpertQuestionModelList;
    }

    public void injectAsktheExpertSkillFilters(String responseStr) throws JSONException {


        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKQUESTIONCATEGORIES_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
//            {"SkillID":"42","PreferrenceTitle":"Demonstrating self-control"}
            AskExpertCategoriesModelDigi askExpertCategoriesModel = new AskExpertCategoriesModelDigi();

            if (jsonMyLearningColumnObj.has("PreferrenceTitle")) {

                askExpertCategoriesModel.category = jsonMyLearningColumnObj.getString("PreferrenceTitle");
            }
            // response
            if (jsonMyLearningColumnObj.has("SkillID")) {

                askExpertCategoriesModel.categoryID = jsonMyLearningColumnObj.get("SkillID").toString();

            }

            askExpertCategoriesModel.siteID = appUserModel.getSiteIDValue();

            injectAsktheFilterSkillsDataIntoSqLite(askExpertCategoriesModel);
        }

    }


    public void injectAsktheFilterSkillsDataIntoSqLite(AskExpertCategoriesModelDigi askExpertCategoriesModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();
            contentValues.put("category", askExpertCategoriesModel.category);
            contentValues.put("categoryid", askExpertCategoriesModel.categoryID);
            contentValues.put("siteid", askExpertCategoriesModel.siteID);

            db.insert(TBL_ASKQUESTIONCATEGORIES_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<AskExpertCategoriesModelDigi> fetchAskFilterSkillsList() {
        List<AskExpertCategoriesModelDigi> askExpertQuestionModelList = null;
        AskExpertCategoriesModelDigi askExpertCategoriesModel = new AskExpertCategoriesModelDigi();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "SELECT * FROM " + TBL_ASKQUESTIONCATEGORIES_DIGI + " WHERE siteid  = " + appUserModel.getSiteIDValue();

        Log.d(TAG, "fetchCatalogModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertQuestionModelList = new ArrayList<AskExpertCategoriesModelDigi>();
                do {

                    askExpertCategoriesModel = new AskExpertCategoriesModelDigi();

                    askExpertCategoriesModel.category = cursor.getString(cursor
                            .getColumnIndex("category"));

                    askExpertCategoriesModel.categoryID = cursor.getString(cursor
                            .getColumnIndex("categoryid"));

                    askExpertCategoriesModel.siteID = cursor.getString(cursor
                            .getColumnIndex("siteid"));


                    askExpertQuestionModelList.add(askExpertCategoriesModel);
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

        return askExpertQuestionModelList;
    }

    public void injectAsktheExpertMappingQuestion(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table1");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKQUESTIONCATEGORYMAPPING_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertFilterMappingModel askExpertCategoriesModelMapping = new AskExpertFilterMappingModel();

            if (jsonMyLearningColumnObj.has("QuestionID")) {

                askExpertCategoriesModelMapping.questionID = jsonMyLearningColumnObj.getString("QuestionID");
            }
            // response
            if (jsonMyLearningColumnObj.has("SkillID")) {

                askExpertCategoriesModelMapping.categoryID = jsonMyLearningColumnObj.get("SkillID").toString();

            }

            askExpertCategoriesModelMapping.siteID = appUserModel.getSiteIDValue();

            injectAsktheFilterMapDataIntoSqLite(askExpertCategoriesModelMapping);
        }

    }


    public void injectAsktheFilterMapDataIntoSqLite(AskExpertFilterMappingModel askExpertCategoriesModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();
            contentValues.put("questionid", askExpertCategoriesModel.questionID);
            contentValues.put("categoryid", askExpertCategoriesModel.categoryID);
            contentValues.put("siteid", askExpertCategoriesModel.siteID);

            db.insert(TBL_ASKQUESTIONCATEGORYMAPPING_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

// injectAnswers Into SQlite

    public void injectAsktheExpertsAnswers(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table1");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKRESPONSES_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonAnswerColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertAnswerModelDg askExpertAnswerModel = new AskExpertAnswerModelDg();

            askExpertAnswerModel.questionID = jsonAnswerColumnObj.optInt("QuestionID");
            askExpertAnswerModel.responseID = jsonAnswerColumnObj.optInt("ResponseID");
            askExpertAnswerModel.response = fromHtmlToString(jsonAnswerColumnObj.optString("Response"));
            askExpertAnswerModel.respondedUserId = jsonAnswerColumnObj.optInt("RespondedUserID");
            askExpertAnswerModel.respondedUserName = jsonAnswerColumnObj.optString("RespondedUserName");
            askExpertAnswerModel.respondedDate = jsonAnswerColumnObj.optString("RespondedDate");
            askExpertAnswerModel.respondeDate = jsonAnswerColumnObj.optString("ResponseDate");
            askExpertAnswerModel.userResponseImage = jsonAnswerColumnObj.optString("UserResponseImage");
            askExpertAnswerModel.picture = jsonAnswerColumnObj.optString("Picture");
            askExpertAnswerModel.commentCount = jsonAnswerColumnObj.optInt("CommentCount");
            askExpertAnswerModel.upvotesCount = jsonAnswerColumnObj.optInt("UpvotesCount");
            askExpertAnswerModel.isLiked = jsonAnswerColumnObj.optBoolean("IsLiked");
            askExpertAnswerModel.userResponseImagePath = jsonAnswerColumnObj.optString("UserResponseImagePath");
            askExpertAnswerModel.daysAgo = jsonAnswerColumnObj.optString("Days");
            askExpertAnswerModel.responseUpVoters = jsonAnswerColumnObj.optString("ResponseUpVoters");
            askExpertAnswerModel.siteID = Integer.parseInt(appUserModel.getSiteIDValue());
            askExpertAnswerModel.commentAction = jsonAnswerColumnObj.optBoolean("CommentAction");
            askExpertAnswerModel.commentAction = jsonAnswerColumnObj.optBoolean("CommentAction");
            askExpertAnswerModel.isLikedStr = jsonAnswerColumnObj.optString("IsLiked");
            injectAsktheExpersAnswersDataIntoSqLite(askExpertAnswerModel);
        }

    }


    public void injectAsktheExpersAnswersDataIntoSqLite(AskExpertAnswerModelDg askExpertAnswerModelDg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("questionid", askExpertAnswerModelDg.questionID);
            contentValues.put("responseid", askExpertAnswerModelDg.responseID);
            contentValues.put("commentCount", askExpertAnswerModelDg.commentCount);
            contentValues.put("upvotesCount", askExpertAnswerModelDg.upvotesCount);
            contentValues.put("isLiked", askExpertAnswerModelDg.isLiked);
            contentValues.put("commentAction", askExpertAnswerModelDg.commentAction);
            contentValues.put("respondedUserId", askExpertAnswerModelDg.respondedUserId);
            contentValues.put("siteid", askExpertAnswerModelDg.siteID);
            contentValues.put("userId", askExpertAnswerModelDg.userID);
            contentValues.put("response", askExpertAnswerModelDg.response);
            contentValues.put("respondedDate", askExpertAnswerModelDg.respondedDate);
            contentValues.put("respondedUserName", askExpertAnswerModelDg.respondedUserName);
            contentValues.put("respondeDate", askExpertAnswerModelDg.respondeDate);
            contentValues.put("userResponseImage", askExpertAnswerModelDg.userResponseImage);
            contentValues.put("picture", askExpertAnswerModelDg.picture);
            contentValues.put("userResponseImagePath", askExpertAnswerModelDg.userResponseImagePath);
            contentValues.put("daysAgo", askExpertAnswerModelDg.daysAgo);
            contentValues.put("responseUpVoters", askExpertAnswerModelDg.responseUpVoters);
            contentValues.put("isLikedStr", askExpertAnswerModelDg.isLikedStr);

            db.insert(TBL_ASKRESPONSES_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public List<AskExpertAnswerModelDg> fetchAnswersForQuestion(String questionID, int totalViews) {
        List<AskExpertAnswerModelDg> askExpertAnswerModelList = null;
        AskExpertAnswerModelDg askExpertAnswerModel = new AskExpertAnswerModelDg();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "SELECT * FROM " + TBL_ASKRESPONSES_DIGI + " WHERE siteid  = " + appUserModel.getSiteIDValue() + " AND questionid  = " + questionID;

        Log.d(TAG, "fetchAnswerModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertAnswerModelList = new ArrayList<AskExpertAnswerModelDg>();
                do {

                    askExpertAnswerModel = new AskExpertAnswerModelDg();

                    askExpertAnswerModel.questionID = cursor.getInt(cursor
                            .getColumnIndex("questionid"));

                    askExpertAnswerModel.responseID = cursor.getInt(cursor
                            .getColumnIndex("responseid"));

                    askExpertAnswerModel.commentCount = cursor.getInt(cursor
                            .getColumnIndex("commentCount"));

                    askExpertAnswerModel.upvotesCount = cursor.getInt(cursor
                            .getColumnIndex("upvotesCount"));

                    askExpertAnswerModel.isLiked = cursor.getInt(cursor
                            .getColumnIndex("isLiked")) > 0;

                    askExpertAnswerModel.commentAction = cursor.getInt(cursor
                            .getColumnIndex("commentAction")) > 0;

                    askExpertAnswerModel.respondedUserId = cursor.getInt(cursor
                            .getColumnIndex("respondedUserId"));

                    askExpertAnswerModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteid"));

                    askExpertAnswerModel.userID = cursor.getInt(cursor
                            .getColumnIndex("userId"));

                    askExpertAnswerModel.response = cursor.getString(cursor
                            .getColumnIndex("response"));

                    askExpertAnswerModel.respondedDate = cursor.getString(cursor
                            .getColumnIndex("respondedDate"));

                    askExpertAnswerModel.respondedUserName = cursor.getString(cursor
                            .getColumnIndex("respondedUserName"));

                    askExpertAnswerModel.respondedUserName = cursor.getString(cursor
                            .getColumnIndex("respondedUserName"));

                    askExpertAnswerModel.respondeDate = cursor.getString(cursor
                            .getColumnIndex("respondeDate"));

                    askExpertAnswerModel.userResponseImage = cursor.getString(cursor
                            .getColumnIndex("userResponseImage"));


                    askExpertAnswerModel.picture = cursor.getString(cursor
                            .getColumnIndex("picture"));

                    askExpertAnswerModel.userResponseImagePath = cursor.getString(cursor
                            .getColumnIndex("userResponseImagePath"));


                    askExpertAnswerModel.daysAgo = cursor.getString(cursor
                            .getColumnIndex("daysAgo"));


                    askExpertAnswerModel.responseUpVoters = cursor.getString(cursor
                            .getColumnIndex("responseUpVoters"));

                    askExpertAnswerModel.isLikedStr = cursor.getString(cursor
                            .getColumnIndex("isLikedStr"));
                    askExpertAnswerModel.viewsCount = totalViews;

                    askExpertAnswerModelList.add(askExpertAnswerModel);

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

        return askExpertAnswerModelList;
    }

    public void deleteAnswerFromLocalDB(AskExpertAnswerModelDg askExpertAnswerModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_ASKRESPONSES_DIGI + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND responseid  ='" + askExpertAnswerModel.responseID + "'";
            db.execSQL(strDelete);

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }


    // unjectUpvotersList Into SQlite

    public void injectUpVoters(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("UpVotesUsers");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKUPVOTERS_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonAnswerColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertUpVoters askExpertUpVoters = new AskExpertUpVoters();

            askExpertUpVoters.likeID = jsonAnswerColumnObj.optInt("LikeID");
            askExpertUpVoters.isLiked = jsonAnswerColumnObj.optBoolean("IsLiked");
            askExpertUpVoters.userID = jsonAnswerColumnObj.optInt("UserID");
            askExpertUpVoters.objectId = jsonAnswerColumnObj.optInt("ObjectID");
            askExpertUpVoters.picture = jsonAnswerColumnObj.optString("Picture");
            askExpertUpVoters.userName = jsonAnswerColumnObj.optString("UserName");
            askExpertUpVoters.jobTitle = jsonAnswerColumnObj.optString("JobTitle");

            insertUpvotersIntoColoumn(askExpertUpVoters);
        }

    }

    public void insertUpvotersIntoColoumn(AskExpertUpVoters askExpertUpVoters) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();

            contentValues.put("objectId", askExpertUpVoters.objectId);
            contentValues.put("siteID", appUserModel.getSiteIDValue());
            contentValues.put("likeID", askExpertUpVoters.isLiked);
            contentValues.put("userID", askExpertUpVoters.userID);
            contentValues.put("isLiked", askExpertUpVoters.isLiked);
            contentValues.put("jobTitle", askExpertUpVoters.jobTitle);
            contentValues.put("picture", askExpertUpVoters.picture);
            contentValues.put("userName", askExpertUpVoters.userName);

            db.insert(TBL_ASKUPVOTERS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public List<AskExpertUpVoters> fetchUpvotersist(int responseID) {
        List<AskExpertUpVoters> askExpertUpVotersList = null;

        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuerys = "SELECT * FROM " + TBL_ASKUPVOTERS_DIGI + " WHERE siteid  = " + appUserModel.getSiteIDValue() + "  AND objectID =" + responseID + " AND isLiked = 1";

        Log.d(TAG, "fetchAnswerModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertUpVotersList = new ArrayList<AskExpertUpVoters>();
                do {

                    AskExpertUpVoters askExpertUpVoters = new AskExpertUpVoters();


                    askExpertUpVoters.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    askExpertUpVoters.likeID = cursor.getInt(cursor
                            .getColumnIndex("likeID"));

                    askExpertUpVoters.userID = cursor.getInt(cursor
                            .getColumnIndex("userID"));

                    askExpertUpVoters.jobTitle = cursor.getString(cursor
                            .getColumnIndex("jobTitle"));

                    askExpertUpVoters.isLiked = cursor.getInt(cursor
                            .getColumnIndex("isLiked")) > 0;

                    askExpertUpVoters.picture = cursor.getString(cursor
                            .getColumnIndex("picture"));

                    askExpertUpVoters.userName = cursor.getString(cursor
                            .getColumnIndex("userName"));

                    askExpertUpVoters.siteID = cursor.getInt(cursor
                            .getColumnIndex("objectId"));

                    askExpertUpVotersList.add(askExpertUpVoters);

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

        return askExpertUpVotersList;
    }


    // injectComments Into SQlite

    public void injectComments(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        // for deleting records in table for respective table


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonAnswerColumnObj = jsonTableAry.getJSONObject(i);

            AskExpertCommentModel askExpertCommentModel = new AskExpertCommentModel();

            askExpertCommentModel.commentID = jsonAnswerColumnObj.optInt("CommentID");
            askExpertCommentModel.commentDescription = fromHtmlToString(jsonAnswerColumnObj.optString("CommentDescription"));

            askExpertCommentModel.commentImage = jsonAnswerColumnObj.optString("CommentImage");
            askExpertCommentModel.commentDate = jsonAnswerColumnObj.optString("CommentDate");
            askExpertCommentModel.commentUserID = jsonAnswerColumnObj.optInt("CommentUserID");
            askExpertCommentModel.commentQuestionID = jsonAnswerColumnObj.optInt("CommentQuestionID");
            askExpertCommentModel.commentResponseID = jsonAnswerColumnObj.optInt("CommentResponseID");
            askExpertCommentModel.imagePath = jsonAnswerColumnObj.optString("Picture");
            askExpertCommentModel.commentedUserName = jsonAnswerColumnObj.optString("CommentedUserName");
            askExpertCommentModel.commentedDate = jsonAnswerColumnObj.optString("CommentedDate");
            askExpertCommentModel.isLiked = jsonAnswerColumnObj.optBoolean("IsLiked");
            askExpertCommentModel.usercCmntImagePath = jsonAnswerColumnObj.optString("UserCommentImagePath");
            askExpertCommentModel.commentAction = jsonAnswerColumnObj.optString("CommentAction");

            insertCommentsIntoColoumn(askExpertCommentModel);
        }

    }

    public void ejectRecordsFromCommentTable(int commentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String strDelete = "DELETE FROM " + TBL_ASKCOMMENTS_DIGI + " WHERE commentID = " + commentID + " AND siteID = " + appUserModel.getSiteIDValue();
            db.execSQL(strDelete);


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void insertCommentsIntoColoumn(AskExpertCommentModel askExpertCommentModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ejectRecordsFromCommentTable(askExpertCommentModel.commentID);
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("questionID", askExpertCommentModel.questionID);
            contentValues.put("siteID", appUserModel.getSiteIDValue());
            contentValues.put("userID", appUserModel.getUserIDValue());
            contentValues.put("commentAction", askExpertCommentModel.commentAction);
            contentValues.put("commentDate", askExpertCommentModel.commentDate);
            contentValues.put("commentDescription", askExpertCommentModel.commentDescription);
            contentValues.put("commentID", askExpertCommentModel.commentID);
            contentValues.put("commentImage", askExpertCommentModel.commentImage);
            contentValues.put("commentQuestionID", askExpertCommentModel.commentQuestionID);
            contentValues.put("commentResponseID", askExpertCommentModel.commentResponseID);
            contentValues.put("commentUserID", askExpertCommentModel.commentUserID);
            contentValues.put("commentedDate", askExpertCommentModel.commentedDate);
            contentValues.put("commentedUserName", askExpertCommentModel.commentedUserName);
            contentValues.put("isLiked", askExpertCommentModel.isLiked);
            contentValues.put("userImage", askExpertCommentModel.imagePath);
            contentValues.put("userCommentImagePath", askExpertCommentModel.usercCmntImagePath);

            db.insert(TBL_ASKCOMMENTS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }
    }

    public List<AskExpertCommentModel> fetchCommentsList(int responseID) {
        List<AskExpertCommentModel> askExpertCommentModelList = null;

        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "SELECT * FROM " + TBL_ASKCOMMENTS_DIGI + " WHERE siteid  = " + appUserModel.getSiteIDValue() + "  AND commentResponseID =" + responseID;
        Log.d(TAG, "fetchAnswerModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertCommentModelList = new ArrayList<AskExpertCommentModel>();
                do {

                    AskExpertCommentModel askExpertCommentModel = new AskExpertCommentModel();


                    askExpertCommentModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("questionID"));

                    askExpertCommentModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    askExpertCommentModel.userID = cursor.getInt(cursor
                            .getColumnIndex("userID"));

                    askExpertCommentModel.commentAction = cursor.getString(cursor
                            .getColumnIndex("commentAction"));

                    askExpertCommentModel.isLiked = cursor.getInt(cursor
                            .getColumnIndex("isLiked")) > 0;

                    askExpertCommentModel.commentDate = cursor.getString(cursor
                            .getColumnIndex("commentDate"));

                    askExpertCommentModel.commentDescription = cursor.getString(cursor
                            .getColumnIndex("commentDescription"));

                    askExpertCommentModel.commentImage = cursor.getString(cursor
                            .getColumnIndex("commentImage"));

                    askExpertCommentModel.commentQuestionID = cursor.getInt(cursor
                            .getColumnIndex("commentQuestionID"));

                    askExpertCommentModel.commentResponseID = cursor.getInt(cursor
                            .getColumnIndex("commentResponseID"));

                    askExpertCommentModel.commentUserID = cursor.getInt(cursor
                            .getColumnIndex("commentUserID"));

                    askExpertCommentModel.commentedDate = cursor.getString(cursor
                            .getColumnIndex("commentedDate"));

                    askExpertCommentModel.commentedUserName = cursor.getString(cursor
                            .getColumnIndex("commentedUserName"));

                    askExpertCommentModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("userImage"));

                    askExpertCommentModel.usercCmntImagePath = cursor.getString(cursor
                            .getColumnIndex("userCommentImagePath"));

                    askExpertCommentModel.commentID = cursor.getInt(cursor
                            .getColumnIndex("commentID"));

                    askExpertCommentModelList.add(askExpertCommentModel);

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

        return askExpertCommentModelList;
    }

    public void deleteCommentFromLocalDB(AskExpertCommentModel askExpertCommentModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_ASKCOMMENTS_DIGI + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND commentID  ='" + askExpertCommentModel.commentID + "'";
            db.execSQL(strDelete);

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

    }

    public void injectAsktheExpertSortOptions(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_ASKSORT);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);
            AskExpertSortModel askExpertSortModel = new AskExpertSortModel();

            //  {"ID":462,"SiteID":374,"ComponentID":161,"LocalID":"en-us","OptionText":"Recently Added","OptionValue":"LastActiveDate Desc","EnableColumn":null}

            askExpertSortModel.siteID = jsonMyLearningColumnObj.optInt("SiteID");
            askExpertSortModel.componentID = jsonMyLearningColumnObj.optInt("ComponentID");
            askExpertSortModel.localID = jsonMyLearningColumnObj.optString("LocalID");
            askExpertSortModel.optionText = jsonMyLearningColumnObj.optString("OptionText");
            askExpertSortModel.optionValue = jsonMyLearningColumnObj.optString("OptionValue");
            askExpertSortModel.enableColumn = jsonMyLearningColumnObj.optString("EnableColumn");
            askExpertSortModel.sortID = jsonMyLearningColumnObj.optInt("ID");

            injectAsktheExpertSortOptionsIntoSQLite(askExpertSortModel);
        }

    }


    public void injectAsktheExpertSortOptionsIntoSQLite(AskExpertSortModel askExpertSortModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {

            //    db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_ASKSORT + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, siteID INTEGER, ComponentID INTEGER, LocalID TEXT, OptionText TEXT, OptionValue TEXT, LastActiveDate TEXT,EnableColumn TEXT)");


            contentValues = new ContentValues();
            contentValues.put("LocalID", askExpertSortModel.localID);
            contentValues.put("ComponentID", askExpertSortModel.componentID);
            contentValues.put("siteID", askExpertSortModel.siteID);
            contentValues.put("OptionText", askExpertSortModel.optionText);
            contentValues.put("OptionValue", askExpertSortModel.optionValue);
            contentValues.put("sortID", askExpertSortModel.sortID);
            contentValues.put("EnableColumn", askExpertSortModel.siteID);


            db.insert(TBL_ASKSORT, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<AskExpertSortModel> fetchAskSortModelList() {
        List<AskExpertSortModel> askExpertSortModelList = null;
        AskExpertSortModel askExpertSortModel = new AskExpertSortModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuerys = "SELECT * FROM " + TBL_ASKSORT + " WHERE siteid  = " + appUserModel.getSiteIDValue();

        Log.d(TAG, "fetchCatalogModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                askExpertSortModelList = new ArrayList<AskExpertSortModel>();
                do {

                    askExpertSortModel = new AskExpertSortModel();

                    askExpertSortModel.localID = cursor.getString(cursor
                            .getColumnIndex("LocalID"));

                    askExpertSortModel.componentID = cursor.getInt(cursor
                            .getColumnIndex("ComponentID"));

                    askExpertSortModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    askExpertSortModel.optionText = cursor.getString(cursor
                            .getColumnIndex("OptionText"));

                    askExpertSortModel.optionValue = cursor.getString(cursor
                            .getColumnIndex("OptionValue"));

                    askExpertSortModel.sortID = cursor.getInt(cursor
                            .getColumnIndex("sortID"));

                    askExpertSortModel.enableColumn = cursor.getString(cursor
                            .getColumnIndex("EnableColumn"));

                    askExpertSortModelList.add(askExpertSortModel);
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

        return askExpertSortModelList;
    }


}


