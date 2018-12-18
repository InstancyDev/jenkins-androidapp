package com.instancy.instancylearning.discussionfourmsenached;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.Spanned;
import android.util.Log;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.convertDateToSortDateFormat;
import static com.instancy.instancylearning.utils.Utilities.convertDateToSortDateFormatUpdated;
import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class DiscussionFourmsDbTables extends DatabaseHandler {


    AppUserModel appUserModel;
    private Context dbctx;
    DatabaseHandler databaseHandler;

    public DiscussionFourmsDbTables(Context context) {
        super(context);
        dbctx = context;
        databaseHandler = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
    }

    public void injectDiscussionFourmDataIntoTable(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("forumList");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_FORUMS_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionForumModelDg discussionForumModel = new DiscussionForumModelDg();

            discussionForumModel.forumID = jsonMyLearningColumnObj.optInt("ForumID");
            discussionForumModel.name = jsonMyLearningColumnObj.optString("Name");

            Spanned result = fromHtml(jsonMyLearningColumnObj.optString("Description"));
            discussionForumModel.description = result.toString();

            discussionForumModel.parentForumID = jsonMyLearningColumnObj.optInt("ParentForumID");
            discussionForumModel.displayOrder = jsonMyLearningColumnObj.optInt("DisplayOrder");

            String formattedDate = formatDate(jsonMyLearningColumnObj.optString("CreatedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

            Log.d(TAG, "injectEventCatalog: " + formattedDate);


            discussionForumModel.createdDate = convertDateToSortDateFormatUpdated(formattedDate);

            discussionForumModel.siteID = jsonMyLearningColumnObj.optInt("SiteID");
            discussionForumModel.createdUserID = jsonMyLearningColumnObj.optInt("CreatedUserID");

            discussionForumModel.active = jsonMyLearningColumnObj.optBoolean("Active");
            discussionForumModel.requiresSubscription = jsonMyLearningColumnObj.optBoolean("RequiresSubscription");
            discussionForumModel.createNewTopic = jsonMyLearningColumnObj.optBoolean("CreateNewTopic");
            discussionForumModel.attachFile = jsonMyLearningColumnObj.optBoolean("AttachFile");
            discussionForumModel.likePosts = jsonMyLearningColumnObj.optBoolean("LikePosts");
            discussionForumModel.sendEmail = jsonMyLearningColumnObj.optBoolean("SendEmail");
            discussionForumModel.moderation = jsonMyLearningColumnObj.optBoolean("Moderation");
            discussionForumModel.isPrivate = jsonMyLearningColumnObj.optBoolean("IsPrivate");

            String authorName = jsonMyLearningColumnObj.optString("Author");
            if (isValidString(authorName)) {
                discussionForumModel.author = authorName;
            }

            String categoryIDs = jsonMyLearningColumnObj.optString("CategoryIDs");
            if (isValidString(categoryIDs)) {
                discussionForumModel.categoryIDs = categoryIDs;
            }

            discussionForumModel.noOfTopics = jsonMyLearningColumnObj.optInt("NoOfTopics");
            discussionForumModel.totalPosts = jsonMyLearningColumnObj.optInt("TotalPosts");
            discussionForumModel.existing = jsonMyLearningColumnObj.optInt("Existing");
            discussionForumModel.totalLikes = jsonMyLearningColumnObj.optInt("TotalLikes");
            discussionForumModel.dfProfileImage = jsonMyLearningColumnObj.optString("DFProfileImage");
            discussionForumModel.dfUpdateTime = jsonMyLearningColumnObj.optString("DFUpdateTime");
            discussionForumModel.dfChangeUpdateTime = jsonMyLearningColumnObj.optString("DFChangeUpdateTime");
            discussionForumModel.forumThumbnailPath = jsonMyLearningColumnObj.optString("ForumThumbnailPath");
            discussionForumModel.descriptionWithLimit = jsonMyLearningColumnObj.optString("DescriptionWithLimit");
            discussionForumModel.moderatorID = jsonMyLearningColumnObj.optInt("ModeratorID");
            discussionForumModel.updatedAuthor = jsonMyLearningColumnObj.optString("UpdatedAuthor");

            String updatedDate = formatDate(jsonMyLearningColumnObj.optString("UpdatedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

            discussionForumModel.updatedDate = convertDateToSortDateFormatUpdated(updatedDate);

            discussionForumModel.moderatorName = jsonMyLearningColumnObj.optString("ModeratorName");

            String moderatorName = jsonMyLearningColumnObj.optString("ModeratorName");
            if (isValidString(moderatorName)) {
                discussionForumModel.moderatorName = moderatorName;
            }

            discussionForumModel.allowShare = jsonMyLearningColumnObj.optBoolean("AllowShare");
            discussionForumModel.descriptionWithoutLimit = jsonMyLearningColumnObj.optString("DescriptionWithoutLimit");

            injectDiscussionForums(discussionForumModel);
        }

    }

    public void injectDiscussionForums(DiscussionForumModelDg discussionForumModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("forumID", discussionForumModel.forumID);
            contentValues.put("name", discussionForumModel.name);
            contentValues.put("description", discussionForumModel.description);
            contentValues.put("parentForumID", discussionForumModel.parentForumID);
            contentValues.put("displayOrder", discussionForumModel.displayOrder);
            contentValues.put("siteID", discussionForumModel.siteID);
            contentValues.put("createdUserID", discussionForumModel.createdUserID);
            contentValues.put("createdDate", discussionForumModel.createdDate);
            contentValues.put("active", discussionForumModel.active);
            contentValues.put("requiresSubscription", discussionForumModel.requiresSubscription);
            contentValues.put("createNewTopic", discussionForumModel.createNewTopic);
            contentValues.put("attachFile", discussionForumModel.attachFile);
            contentValues.put("likePosts", discussionForumModel.likePosts);
            contentValues.put("sendEmail", discussionForumModel.sendEmail);
            contentValues.put("moderation", discussionForumModel.moderation);
            contentValues.put("isPrivate", discussionForumModel.isPrivate);
            contentValues.put("noOfTopics", discussionForumModel.noOfTopics);
            contentValues.put("totalPosts", discussionForumModel.totalPosts);
            contentValues.put("existing", discussionForumModel.existing);
            contentValues.put("totalLikes", discussionForumModel.totalLikes);
            contentValues.put("dfProfileImage", discussionForumModel.dfProfileImage);
            contentValues.put("dfUpdateTime", discussionForumModel.dfUpdateTime);
            contentValues.put("dfChangeUpdateTime", discussionForumModel.dfChangeUpdateTime);
            contentValues.put("forumThumbnailPath", discussionForumModel.forumThumbnailPath);
            contentValues.put("descriptionWithLimit", discussionForumModel.descriptionWithLimit);
            contentValues.put("moderatorID", discussionForumModel.moderatorID);
            contentValues.put("updatedAuthor", discussionForumModel.updatedAuthor);
            contentValues.put("updatedDate", discussionForumModel.updatedDate);
            contentValues.put("moderatorName", discussionForumModel.moderatorName);
            contentValues.put("allowShare", discussionForumModel.allowShare);
            contentValues.put("descriptionWithoutLimit", discussionForumModel.descriptionWithoutLimit);
            contentValues.put("author", discussionForumModel.author);
            contentValues.put("categoryIDs", discussionForumModel.categoryIDs);

            db.insert(TBL_FORUMS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<DiscussionForumModelDg> fetchDiscussionForums(String siteId, boolean isPrivateForum) {

        List<DiscussionForumModelDg> discussionForumModelList = null;
        DiscussionForumModelDg discussionForumModel = new DiscussionForumModelDg();
        SQLiteDatabase db = this.getWritableDatabase();
        String strSelQuerys = "";
        if (isPrivateForum) {
            strSelQuerys = "SELECT DF.*, UI.profileimagepath from " + TBL_FORUMS_DIGI + " DF LEFT OUTER JOIN " + TBL_ALLUSERSINFO + " UI ON DF.createduserid = UI.userid WHERE DF.siteid = " + siteId + " ORDER BY createddate DESC";

        } else {

            strSelQuerys = "SELECT DF.*, UI.profileimagepath from " + TBL_FORUMS_DIGI + " DF LEFT OUTER JOIN " + TBL_ALLUSERSINFO + " UI ON DF.createduserid = UI.userid WHERE DF.siteid = " + siteId + " AND isPrivate=0 ORDER BY createddate DESC";
        }

        Log.d(TAG, "fetchCatalogModel: " + strSelQuerys);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuerys, null);

            if (cursor != null && cursor.moveToFirst()) {
                discussionForumModelList = new ArrayList<DiscussionForumModelDg>();
                do {

                    discussionForumModel = new DiscussionForumModelDg();

                    discussionForumModel.forumID = cursor.getInt(cursor
                            .getColumnIndex("forumID"));

                    discussionForumModel.name = cursor.getString(cursor.getColumnIndex("name"));

                    discussionForumModel.description = cursor.getString(cursor
                            .getColumnIndex("description"));

                    discussionForumModel.parentForumID = cursor.getInt(cursor
                            .getColumnIndex("parentForumID"));

                    discussionForumModel.displayOrder = cursor.getInt(cursor
                            .getColumnIndex("displayOrder"));

                    discussionForumModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    discussionForumModel.createdUserID = cursor.getInt(cursor
                            .getColumnIndex("createdUserID"));

                    discussionForumModel.createdDate = cursor.getString(cursor
                            .getColumnIndex("createdDate"));

                    discussionForumModel.active = cursor.getInt(cursor
                            .getColumnIndex("active")) > 0;

                    discussionForumModel.requiresSubscription = cursor.getInt(cursor
                            .getColumnIndex("requiresSubscription")) > 0;

                    discussionForumModel.createNewTopic = cursor.getInt(cursor
                            .getColumnIndex("createNewTopic")) > 0;

                    discussionForumModel.attachFile = cursor.getInt(cursor
                            .getColumnIndex("attachFile")) > 0;

                    discussionForumModel.likePosts = cursor.getInt(cursor
                            .getColumnIndex("likePosts")) > 0;

                    discussionForumModel.sendEmail = cursor.getInt(cursor
                            .getColumnIndex("sendEmail")) > 0;

                    discussionForumModel.moderation = cursor.getInt(cursor
                            .getColumnIndex("moderation")) > 0;

                    discussionForumModel.isPrivate = cursor.getInt(cursor
                            .getColumnIndex("isPrivate")) > 0;

                    discussionForumModel.author = cursor.getString(cursor
                            .getColumnIndex("author"));


                    discussionForumModel.noOfTopics = cursor.getInt(cursor
                            .getColumnIndex("noOfTopics"));


                    discussionForumModel.totalPosts = cursor.getInt(cursor
                            .getColumnIndex("totalPosts"));


                    discussionForumModel.existing = cursor.getInt(cursor
                            .getColumnIndex("existing"));


                    discussionForumModel.totalLikes = cursor.getInt(cursor
                            .getColumnIndex("totalLikes"));


                    discussionForumModel.dfProfileImage = cursor.getString(cursor
                            .getColumnIndex("dfProfileImage"));


                    discussionForumModel.dfUpdateTime = cursor.getString(cursor
                            .getColumnIndex("dfUpdateTime"));


                    discussionForumModel.dfChangeUpdateTime = cursor.getString(cursor
                            .getColumnIndex("dfChangeUpdateTime"));


                    discussionForumModel.forumThumbnailPath = cursor.getString(cursor
                            .getColumnIndex("forumThumbnailPath"));


                    discussionForumModel.descriptionWithLimit = cursor.getString(cursor
                            .getColumnIndex("descriptionWithLimit"));


                    discussionForumModel.moderatorID = cursor.getInt(cursor
                            .getColumnIndex("moderatorID"));


                    discussionForumModel.updatedAuthor = cursor.getString(cursor
                            .getColumnIndex("updatedAuthor"));

                    discussionForumModel.updatedDate = cursor.getString(cursor
                            .getColumnIndex("updatedDate"));


                    discussionForumModel.moderatorName = cursor.getString(cursor
                            .getColumnIndex("moderatorName"));


                    discussionForumModel.allowShare = cursor.getInt(cursor
                            .getColumnIndex("allowShare")) > 0;

                    discussionForumModel.descriptionWithoutLimit = cursor.getString(cursor
                            .getColumnIndex("descriptionWithoutLimit"));

                    discussionForumModel.categoryIDs = cursor.getString(cursor
                            .getColumnIndex("categoryIDs"));

                    discussionForumModel.categoriesIDArray = getArrayListFromString(discussionForumModel.categoryIDs);

                    discussionForumModelList.add(discussionForumModel);
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
        return discussionForumModelList;
    }

    public List<String> getArrayListFromString(String questionCategoriesString) {

        List<String> questionCategoriesArray = new ArrayList<>();

        if (questionCategoriesString.length() <= 0)
            return questionCategoriesArray;

        questionCategoriesArray = Arrays.asList(questionCategoriesString.split(","));

        return questionCategoriesArray;

    }

    public void injectDiscussionTopics(JSONObject jsonObject, DiscussionForumModelDg discussionForumModel) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("TopicList");
        // for deleting records in table for respective table

//        ejectRecordsinTable(TBL_FORUMTOPICS_DIGI);

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            String strDelete = "DELETE FROM " + TBL_FORUMTOPICS_DIGI + " WHERE  siteID ='"
                    + appUserModel.getSiteIDValue() + "' AND forumId  ='" + discussionForumModel.forumID + "'";

            db.execSQL(strDelete);

        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionTopicModelDg discussionTopicModel = new DiscussionTopicModelDg();

            discussionTopicModel.contentID = jsonMyLearningColumnObj.optString("ContentID");

            discussionTopicModel.createdDate = formatDate(jsonMyLearningColumnObj.optString("CreatedDate"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

            discussionTopicModel.name = jsonMyLearningColumnObj.optString("Name");

            discussionTopicModel.createdUserID = jsonMyLearningColumnObj.optInt("CreatedUserID", -1);

            discussionTopicModel.noOfReplies = jsonMyLearningColumnObj.optInt("NoOfReplies");

            Spanned result = fromHtml(jsonMyLearningColumnObj.optString("LongDescription"));

            discussionTopicModel.longDescription = result.toString();

            discussionTopicModel.noOfViews = jsonMyLearningColumnObj.optInt("NoOfViews");

            discussionTopicModel.latestReplyBy = jsonMyLearningColumnObj.optString("LatestReplyBy");

            discussionTopicModel.author = jsonMyLearningColumnObj.optString("Author");

            discussionTopicModel.uploadFileName = jsonMyLearningColumnObj.optString("UploadFileName");

            discussionTopicModel.updatedTime = jsonMyLearningColumnObj.optString("UpdatedTime");

            discussionTopicModel.createdTime = jsonMyLearningColumnObj.optString("CreatedTime");

            discussionTopicModel.modifiedUserName = jsonMyLearningColumnObj.optString("ModifiedUserName");

            discussionTopicModel.uploadedImageName = jsonMyLearningColumnObj.optString("UploadedImageName");

            discussionTopicModel.likes = jsonMyLearningColumnObj.optInt("Likes");

            discussionTopicModel.likeState = jsonMyLearningColumnObj.optBoolean("likeState");

            discussionTopicModel.isPin = jsonMyLearningColumnObj.optBoolean("IsPin");

            discussionTopicModel.pinID = jsonMyLearningColumnObj.optInt("PinID");

            discussionTopicModel.topicUserProfile = jsonMyLearningColumnObj.optString("TopicUserProfile");

            discussionTopicModel.siteId = Integer.parseInt(appUserModel.getSiteIDValue());

            discussionTopicModel.forumId = discussionForumModel.forumID;

            injectDiscussionTopicsIntoTable(discussionTopicModel, discussionForumModel);

        }

    }

    public void injectDiscussionTopicsIntoTable(DiscussionTopicModelDg discussionTopicModel, DiscussionForumModelDg discussionForumModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
//            String strDelete = "DELETE FROM " + TBL_FORUMTOPICS_DIGI + " WHERE  siteID ='"
//                    + appUserModel.getSiteIDValue() + "' AND forumid ='" + discussionForumModel.forumID + "' AND topicid  ='" + discussionTopicModel.topicid + "'";


        } catch (SQLiteException sqlEx) {

            sqlEx.printStackTrace();
        }

        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();


            //      "(ID INTEGER PRIMARY KEY AUTOINCREMENT,contentID TEXT,forumId INTEGER,name TEXT,createdDate INTEGER,createdUserID INTEGER,noOfReplies INTEGER,noOfViews INTEGER,createdDate TEXT,longDescription TEXT,latestReplyBy TEXT, author TEXT, uploadFileName TEXT, updatedTime TEXT,createdTime TEXT,modifiedUserName TEXT,uploadedImageName TEXT,likes INTEGER,likeState BOOLEAN,topicUserProfile TEXT, isPin BOOLEAN, pinID INTEGER,commentsCount INTEGER)"


            contentValues.put("contentID", discussionTopicModel.contentID);
            contentValues.put("forumId", discussionTopicModel.forumId);
            contentValues.put("name", discussionTopicModel.name);
            contentValues.put("createdDate", discussionTopicModel.createdDate);
            contentValues.put("createdUserID", discussionTopicModel.createdUserID);
            contentValues.put("noOfReplies", discussionTopicModel.noOfReplies);
            contentValues.put("noOfViews", discussionTopicModel.noOfViews);
            contentValues.put("longDescription", discussionTopicModel.longDescription);
            contentValues.put("latestReplyBy", discussionTopicModel.latestReplyBy);
            contentValues.put("author", discussionTopicModel.author);
            contentValues.put("uploadFileName", discussionTopicModel.uploadFileName);
            contentValues.put("updatedTime", discussionTopicModel.updatedTime);
            contentValues.put("createdTime", discussionTopicModel.createdTime);
            contentValues.put("modifiedUserName", discussionTopicModel.modifiedUserName);
            contentValues.put("uploadedImageName", discussionTopicModel.uploadedImageName);
            contentValues.put("likes", discussionTopicModel.likes);
            contentValues.put("modifiedUserName", discussionTopicModel.modifiedUserName);
            contentValues.put("likeState", discussionTopicModel.likeState);
            contentValues.put("topicUserProfile", discussionTopicModel.topicUserProfile);
            contentValues.put("isPin", discussionTopicModel.isPin);
            contentValues.put("pinID", discussionTopicModel.pinID);
            contentValues.put("commentsCount", discussionTopicModel.commentsCount);
            contentValues.put("siteId", discussionTopicModel.siteId);
            contentValues.put("userId", discussionTopicModel.userId);

            db.insert(TBL_FORUMTOPICS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<DiscussionTopicModelDg> fetchDiscussionTopicList(String siteID, int fourmID) {
        List<DiscussionTopicModelDg> discussionTopicModelList = null;
        DiscussionTopicModelDg discussionTopicModel = new DiscussionTopicModelDg();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSqlQuery = "SELECT FT.*, UI.profileimagepath, UI.displayname from " + TBL_FORUMTOPICS_DIGI + " FT LEFT OUTER JOIN " + TBL_ALLUSERSINFO + " UI ON FT.createdUserID = UI.userid WHERE FT.forumId = '" + fourmID + "' AND FT.siteId = " + siteID + " ORDER BY FT.createddate DESC";

        Log.d(TAG, "fetchCatalogModel: " + strSqlQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSqlQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                discussionTopicModelList = new ArrayList<DiscussionTopicModelDg>();
                do {

                    discussionTopicModel = new DiscussionTopicModelDg();

                    discussionTopicModel.contentID = cursor.getString(cursor
                            .getColumnIndex("contentID"));

                    discussionTopicModel.forumId = cursor.getInt(cursor
                            .getColumnIndex("forumId"));

                    discussionTopicModel.name = cursor.getString(cursor
                            .getColumnIndex("name"));

                    discussionTopicModel.createdUserID = cursor.getInt(cursor
                            .getColumnIndex("createdUserID"));

                    discussionTopicModel.noOfReplies = cursor.getInt(cursor
                            .getColumnIndex("noOfReplies"));

                    discussionTopicModel.noOfViews = cursor.getInt(cursor
                            .getColumnIndex("noOfViews"));

                    discussionTopicModel.createdDate = cursor.getString(cursor
                            .getColumnIndex("createdDate"));

                    discussionTopicModel.longDescription = cursor.getString(cursor
                            .getColumnIndex("longDescription"));

                    discussionTopicModel.latestReplyBy = cursor.getString(cursor
                            .getColumnIndex("latestReplyBy"));

                    discussionTopicModel.uploadFileName = cursor.getString(cursor
                            .getColumnIndex("uploadFileName"));

                    discussionTopicModel.contentID = cursor.getString(cursor
                            .getColumnIndex("contentID"));

                    discussionTopicModel.author = cursor.getString(cursor
                            .getColumnIndex("author"));

                    discussionTopicModel.updatedTime = cursor.getString(cursor
                            .getColumnIndex("updatedTime"));

                    discussionTopicModel.createdTime = cursor.getString(cursor
                            .getColumnIndex("createdTime"));

                    discussionTopicModel.modifiedUserName = cursor.getString(cursor
                            .getColumnIndex("modifiedUserName"));

                    discussionTopicModel.uploadedImageName = cursor.getString(cursor
                            .getColumnIndex("uploadedImageName"));

                    discussionTopicModel.likes = cursor.getInt(cursor
                            .getColumnIndex("likes"));

                    discussionTopicModel.likeState = cursor.getInt(cursor
                            .getColumnIndex("likeState")) > 0;

                    discussionTopicModel.topicUserProfile = cursor.getString(cursor
                            .getColumnIndex("topicUserProfile"));

                    discussionTopicModel.isPin = cursor.getInt(cursor
                            .getColumnIndex("isPin")) > 0;

                    discussionTopicModel.pinID = cursor.getInt(cursor
                            .getColumnIndex("pinID"));

                    discussionTopicModel.commentsCount = cursor.getInt(cursor
                            .getColumnIndex("commentsCount"));

                    discussionTopicModel.siteId = cursor.getInt(cursor
                            .getColumnIndex("siteId"));

                    discussionTopicModel.userId = cursor.getInt(cursor
                            .getColumnIndex("userId"));


                    discussionTopicModelList.add(discussionTopicModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchDiscuss db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return discussionTopicModelList;
    }

    // discussion comments

    public void injectDiscussionComments(String responseStr, DiscussionTopicModelDg discussionTopicModel) throws JSONException {


        JSONArray jsonTableAry = new JSONArray(responseStr);

//        SQLiteDatabase db = this.getWritableDatabase();
//
//        try {
//            String strDelete = "DELETE FROM " + TBL_TOPICCOMMENTS_DIGI + " WHERE  siteID ='"
//                    + appUserModel.getSiteIDValue() + "' AND topicID  ='" + discussionTopicModel.contentID + "'";
//            db.execSQL(strDelete);
//
//        } catch (SQLiteException sqlEx) {
//
//            sqlEx.printStackTrace();
//        }
        ejectRecordsinTable(TBL_TOPICCOMMENTS_DIGI);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionCommentsModelDg discussionCommentsModel = new DiscussionCommentsModelDg();

            discussionCommentsModel.commentID = jsonColumnObj.optInt("commentid");

            discussionCommentsModel.postedDate = formatDate(jsonColumnObj.optString("posteddate"), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

            discussionCommentsModel.topicID = jsonColumnObj.optString("topicid");

            discussionCommentsModel.postedBy = jsonColumnObj.optInt("postedby", -1);

            Spanned result = fromHtml(jsonColumnObj.optString("message"));

            discussionCommentsModel.message = result.toString();

            discussionCommentsModel.forumID = jsonColumnObj.optInt("forumid");

            discussionCommentsModel.siteID = jsonColumnObj.optInt("siteid");

            discussionCommentsModel.replyID = jsonColumnObj.optString("ReplyID");

            discussionCommentsModel.commentedBy = jsonColumnObj.optString("CommentedBy");

            discussionCommentsModel.commentedFromDays = jsonColumnObj.optString("CommentedFromDays");

            discussionCommentsModel.commentFileUploadPath = jsonColumnObj.optString("CommentFileUploadPath");

            discussionCommentsModel.commentFileUploadName = jsonColumnObj.optString("CommentFileUploadName");

            discussionCommentsModel.commentVideoUploadName = jsonColumnObj.optString("CommentVideoUploadName");

            discussionCommentsModel.commentAudioUploadName = jsonColumnObj.optString("CommentAudioUploadName");

            discussionCommentsModel.commentApplicationUploadName = jsonColumnObj.optString("CommentApplicationUploadName");

            discussionCommentsModel.likeState = jsonColumnObj.optBoolean("likeState");

            discussionCommentsModel.commentRepliesCount = jsonColumnObj.optInt("CommentRepliesCount");

            discussionCommentsModel.commentLikes = jsonColumnObj.optInt("CommentLikes");

            discussionCommentsModel.commentUserProfile = jsonColumnObj.optString("CommentUserProfile");

            discussionCommentsModel.siteID = Integer.parseInt(appUserModel.getSiteIDValue());

            discussionCommentsModel.userID = Integer.parseInt(appUserModel.getUserIDValue());

            injectDiscussionCommetnsData(discussionCommentsModel, discussionTopicModel);
        }

    }

    public void injectDiscussionCommetnsData(DiscussionCommentsModelDg discussionCommentsModel, DiscussionTopicModelDg discussionTopicModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = null;
        try {

            contentValues = new ContentValues();


            contentValues.put("commentID", discussionCommentsModel.commentID);
            contentValues.put("topicID", discussionCommentsModel.topicID);
            contentValues.put("forumID", discussionCommentsModel.forumID);
            contentValues.put("message", discussionCommentsModel.message);
            contentValues.put("postedDate", discussionCommentsModel.postedDate);
            contentValues.put("postedBy", discussionCommentsModel.postedBy);
            contentValues.put("siteID", discussionCommentsModel.siteID);
            contentValues.put("replyID", discussionCommentsModel.replyID);
            contentValues.put("commentedBy", discussionCommentsModel.commentedBy);

            contentValues.put("commentedFromDays", discussionCommentsModel.commentedFromDays);
            contentValues.put("commentFileUploadPath", discussionCommentsModel.commentFileUploadPath);
            contentValues.put("commentFileUploadName", discussionCommentsModel.commentFileUploadName);
            contentValues.put("commentVideoUploadName", discussionCommentsModel.commentVideoUploadName);
            contentValues.put("commentAudioUploadName", discussionCommentsModel.commentAudioUploadName);
            contentValues.put("commentApplicationUploadName", discussionCommentsModel.commentApplicationUploadName);
            contentValues.put("likeState", discussionCommentsModel.likeState);
            contentValues.put("commentLikes", discussionCommentsModel.commentLikes);
            contentValues.put("commentRepliesCount", discussionCommentsModel.commentRepliesCount);

            contentValues.put("commentUserProfile", discussionCommentsModel.commentUserProfile);
            contentValues.put("userID", discussionCommentsModel.userID);

            db.insert(TBL_TOPICCOMMENTS_DIGI, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<DiscussionCommentsModelDg> fetchDiscussionComments(String siteID, DiscussionTopicModelDg topicModel) {
        List<DiscussionCommentsModelDg> discussionTopicModelList = null;
        DiscussionCommentsModelDg discussionCommentsModel = new DiscussionCommentsModelDg();
        SQLiteDatabase db = this.getWritableDatabase();


        String strSelQuery = "SELECT TC.*, UI.profileimagepath, UI.displayname from " + TBL_TOPICCOMMENTS_DIGI + " TC LEFT OUTER JOIN " + TBL_ALLUSERSINFO + " UI ON TC.postedby = UI.userid WHERE TC.forumID = " + topicModel.forumId + " AND TC.topicID = '" + topicModel.contentID + "' AND TC.siteid = " + siteID + " ORDER BY TC.commentID DESC";

        Log.d(TAG, "fetchDiscussionCommentsModelList: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                discussionTopicModelList = new ArrayList<DiscussionCommentsModelDg>();
                do {
                    discussionCommentsModel = new DiscussionCommentsModelDg();

                    discussionCommentsModel.commentID = cursor.getInt(cursor
                            .getColumnIndex("commentID"));

                    discussionCommentsModel.topicID = cursor.getString(cursor
                            .getColumnIndex("topicID"));

                    discussionCommentsModel.forumID = cursor.getInt(cursor
                            .getColumnIndex("forumID"));

                    discussionCommentsModel.message = cursor.getString(cursor
                            .getColumnIndex("message"));

                    discussionCommentsModel.postedDate = cursor.getString(cursor
                            .getColumnIndex("postedDate"));

                    discussionCommentsModel.postedBy = cursor.getInt(cursor
                            .getColumnIndex("postedBy"));

                    discussionCommentsModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    discussionCommentsModel.userID = cursor.getInt(cursor
                            .getColumnIndex("userID"));


                    discussionCommentsModel.replyID = cursor.getString(cursor
                            .getColumnIndex("replyID"));

                    discussionCommentsModel.commentedBy = cursor.getString(cursor
                            .getColumnIndex("commentedBy"));

                    discussionCommentsModel.commentedFromDays = cursor.getString(cursor
                            .getColumnIndex("commentedFromDays"));

                    discussionCommentsModel.commentFileUploadPath = cursor.getString(cursor
                            .getColumnIndex("commentFileUploadPath"));

                    discussionCommentsModel.commentFileUploadName = cursor.getString(cursor
                            .getColumnIndex("commentFileUploadName"));


                    discussionCommentsModel.commentVideoUploadName = cursor.getString(cursor
                            .getColumnIndex("commentVideoUploadName"));


                    discussionCommentsModel.commentAudioUploadName = cursor.getString(cursor
                            .getColumnIndex("commentAudioUploadName"));

                    discussionCommentsModel.commentApplicationUploadName = cursor.getString(cursor
                            .getColumnIndex("commentApplicationUploadName"));

                    discussionCommentsModel.likeState = cursor.getInt(cursor
                            .getColumnIndex("likeState")) > 0;

                    discussionCommentsModel.commentLikes = cursor.getInt(cursor
                            .getColumnIndex("commentLikes"));

                    discussionCommentsModel.commentRepliesCount = cursor.getInt(cursor
                            .getColumnIndex("commentRepliesCount"));

                    discussionCommentsModel.commentUserProfile = cursor.getString(cursor
                            .getColumnIndex("commentUserProfile"));

                    discussionTopicModelList.add(discussionCommentsModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchTopicList db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return discussionTopicModelList;
    }

    public void injectDiscussionFourmReplies(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_TOPICREPLY);


        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionReplyModelDg discussionReplyModel = new DiscussionReplyModelDg();

            discussionReplyModel.replyID = jsonMyLearningColumnObj.optInt("ReplyID");

            discussionReplyModel.commentID = jsonMyLearningColumnObj.optInt("CommentID");

            Spanned result = fromHtml(jsonMyLearningColumnObj.optString("Message"));
            discussionReplyModel.message = result.toString();

            String formattedDate = formatDate(jsonMyLearningColumnObj.optString("PostedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss");

            Log.d(TAG, "injectEventCatalog: " + formattedDate);
            discussionReplyModel.postedDate = formattedDate;
            discussionReplyModel.postedBy = jsonMyLearningColumnObj.optInt("PostedBy");
            discussionReplyModel.replyBy = jsonMyLearningColumnObj.optString("ReplyBy");
            discussionReplyModel.picture = jsonMyLearningColumnObj.optString("Picture");
            discussionReplyModel.likeState = jsonMyLearningColumnObj.optBoolean("likeState");
            discussionReplyModel.replyProfile = jsonMyLearningColumnObj.optString("ReplyProfile");
            discussionReplyModel.dtPostedOnDate = jsonMyLearningColumnObj.optString("dtPostedDate");
            discussionReplyModel.topicID = jsonMyLearningColumnObj.optString("TopicID");
            discussionReplyModel.forumID = jsonMyLearningColumnObj.optInt("ForumID");
            discussionReplyModel.userID = Integer.parseInt(appUserModel.getUserIDValue());
            discussionReplyModel.siteID = Integer.parseInt(appUserModel.getSiteIDValue());


            injectDiscussionReplies(discussionReplyModel);
        }

    }

    public void injectDiscussionReplies(DiscussionReplyModelDg discussionReplyModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("siteID", discussionReplyModel.siteID);
            contentValues.put("userID", discussionReplyModel.userID);
            contentValues.put("topicID", discussionReplyModel.topicID);
            contentValues.put("replyID", discussionReplyModel.replyID);
            contentValues.put("commentID", discussionReplyModel.commentID);
            contentValues.put("forumID", discussionReplyModel.forumID);
            contentValues.put("message", discussionReplyModel.message);
            contentValues.put("postedDate", discussionReplyModel.postedDate);
            contentValues.put("postedBy", discussionReplyModel.postedBy);
            contentValues.put("replyBy", discussionReplyModel.replyBy);
            contentValues.put("picture", discussionReplyModel.picture);
            contentValues.put("likeState", discussionReplyModel.likeState);
            contentValues.put("replyProfile", discussionReplyModel.replyProfile);
            contentValues.put("dtPostedOnDate", discussionReplyModel.dtPostedOnDate);


            db.insert(TBL_TOPICREPLY, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }


    public List<DiscussionReplyModelDg> fetchDiscussionReplies(String siteID, DiscussionCommentsModelDg commentsModel) {
        List<DiscussionReplyModelDg> discussionReplyModelList = null;
        DiscussionReplyModelDg discussionCommentsModel = new DiscussionReplyModelDg();
        SQLiteDatabase db = this.getWritableDatabase();


//        String strSelQuery = "SELECT TC.*, UI.profileimagepath, UI.displayname from " + TBL_TOPICREPLY + " TC LEFT OUTER JOIN " + TBL_ALLUSERSINFO + " UI ON TC.postedby = UI.userid WHERE TC.forumID = " + commentsModel.commentID + " AND TC.topicID = '" + commentsModel.topicID + "' AND TC.siteid = " + siteID + " ORDER BY TC.commentID DESC";

        String strSelQuery = "SELECT * from FORUMTOPICREPLY where siteID =" + commentsModel.siteID + " AND userID =" + commentsModel.userID + " AND commentID = " + commentsModel.commentID + " AND topicID = '" + commentsModel.topicID + "'";

        Log.d(TAG, "fetchDiscussionCommentsModelList: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                discussionReplyModelList = new ArrayList<DiscussionReplyModelDg>();
                do {
                    discussionCommentsModel = new DiscussionReplyModelDg();

                    discussionCommentsModel.commentID = cursor.getInt(cursor
                            .getColumnIndex("commentID"));

                    discussionCommentsModel.topicID = cursor.getString(cursor
                            .getColumnIndex("topicID"));

                    discussionCommentsModel.forumID = cursor.getInt(cursor
                            .getColumnIndex("forumID"));

                    discussionCommentsModel.message = cursor.getString(cursor
                            .getColumnIndex("message"));

                    discussionCommentsModel.postedDate = cursor.getString(cursor
                            .getColumnIndex("postedDate"));

                    discussionCommentsModel.postedBy = cursor.getInt(cursor
                            .getColumnIndex("postedBy"));

                    discussionCommentsModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    discussionCommentsModel.userID = cursor.getInt(cursor
                            .getColumnIndex("userID"));


                    discussionCommentsModel.replyID = cursor.getInt(cursor
                            .getColumnIndex("replyID"));

                    discussionCommentsModel.replyBy = cursor.getString(cursor
                            .getColumnIndex("replyBy"));

                    discussionCommentsModel.dtPostedOnDate = cursor.getString(cursor
                            .getColumnIndex("dtPostedOnDate"));

                    discussionCommentsModel.replyProfile = cursor.getString(cursor
                            .getColumnIndex("replyProfile"));

                    discussionCommentsModel.likeState = cursor.getInt(cursor
                            .getColumnIndex("likeState")) > 0;


                    discussionReplyModelList.add(discussionCommentsModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchTopicList db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return discussionReplyModelList;
    }


    public void injectDiscussionCategoriesResponse(String responseStr) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseStr);

        JSONArray jsonTableAry = jsonObject.getJSONArray("Table1");

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_FORUM_CATEGORIES);


        for (int i = 0; i < jsonTableAry.length(); i++) {

            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            DiscussionCategoriesModel discussionCategoriesModel = new DiscussionCategoriesModel();
            discussionCategoriesModel.id = jsonMyLearningColumnObj.optInt("id");
            discussionCategoriesModel.categoryName = jsonMyLearningColumnObj.optString("CategoryName");
            Spanned result = fromHtml(jsonMyLearningColumnObj.optString("fullName"));
            discussionCategoriesModel.fullName = result.toString();
            discussionCategoriesModel.categoryID = jsonMyLearningColumnObj.optInt("CategoryID");
            discussionCategoriesModel.parentId = jsonMyLearningColumnObj.optString("parentId");
            discussionCategoriesModel.iconpath = jsonMyLearningColumnObj.optString("Iconpath");
            discussionCategoriesModel.agreementDocId = jsonMyLearningColumnObj.optString("AgreementDocId");
            discussionCategoriesModel.contentCount = jsonMyLearningColumnObj.optInt("ContentCount");
            discussionCategoriesModel.refParentID = jsonMyLearningColumnObj.optInt("RefParentID");
            discussionCategoriesModel.displayOrder = jsonMyLearningColumnObj.optInt("DisplayOrder");
            discussionCategoriesModel.userId = Integer.parseInt(appUserModel.getUserIDValue());
            discussionCategoriesModel.siteId = Integer.parseInt(appUserModel.getSiteIDValue());

            injectDiscussionCategories(discussionCategoriesModel);
        }

    }

    public void injectDiscussionCategories(DiscussionCategoriesModel discussionCategoriesModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("siteID", discussionCategoriesModel.siteId);
            contentValues.put("userID", discussionCategoriesModel.userId);
            contentValues.put("ids", discussionCategoriesModel.id);
            contentValues.put("categoryName", discussionCategoriesModel.categoryName);
            contentValues.put("fullName", discussionCategoriesModel.fullName);
            contentValues.put("categoryID", discussionCategoriesModel.categoryID);
            contentValues.put("parentId", discussionCategoriesModel.parentId);
            contentValues.put("iconpath", discussionCategoriesModel.iconpath);
            contentValues.put("agreementDocId", discussionCategoriesModel.agreementDocId);
            contentValues.put("contentCount", discussionCategoriesModel.contentCount);
            contentValues.put("refParentID", discussionCategoriesModel.refParentID);
            contentValues.put("displayOrder", discussionCategoriesModel.displayOrder);


            db.insert(TBL_FORUM_CATEGORIES, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }


    public List<DiscussionCategoriesModel> fetchDiscussionCategories(String siteID) {
        List<DiscussionCategoriesModel> discussionCategoriesModellist = null;

        SQLiteDatabase db = this.getWritableDatabase();


        String strSelQuery = "SELECT * from TBLFORUMCATEGORIES where siteID =" + siteID + " AND userID =" + appUserModel.getUserIDValue();

        Log.d(TAG, "fetchDiscussionCategories: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                discussionCategoriesModellist = new ArrayList<DiscussionCategoriesModel>();
                do {
                    DiscussionCategoriesModel discussionCategoriesModel = new DiscussionCategoriesModel();

                    discussionCategoriesModel.siteId = cursor.getInt(cursor
                            .getColumnIndex("siteID"));

                    discussionCategoriesModel.userId = cursor.getInt(cursor
                            .getColumnIndex("userID"));

                    discussionCategoriesModel.fullName = cursor.getString(cursor
                            .getColumnIndex("fullName"));

                    discussionCategoriesModel.id = cursor.getInt(cursor
                            .getColumnIndex("ids"));

                    discussionCategoriesModel.categoryName = cursor.getString(cursor
                            .getColumnIndex("categoryName"));

                    discussionCategoriesModel.categoryID = cursor.getInt(cursor
                            .getColumnIndex("categoryID"));

                    discussionCategoriesModel.parentId = cursor.getString(cursor
                            .getColumnIndex("parentId"));

                    discussionCategoriesModel.iconpath = cursor.getString(cursor
                            .getColumnIndex("iconpath"));

                    discussionCategoriesModel.agreementDocId = cursor.getString(cursor
                            .getColumnIndex("agreementDocId"));


                    discussionCategoriesModel.contentCount = cursor.getInt(cursor
                            .getColumnIndex("contentCount"));

                    discussionCategoriesModel.refParentID = cursor.getInt(cursor
                            .getColumnIndex("refParentID"));

                    discussionCategoriesModel.displayOrder = cursor.getInt(cursor
                            .getColumnIndex("displayOrder"));

                    discussionCategoriesModellist.add(discussionCategoriesModel);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (db.isOpen()) {
                db.close();
            }
            Log.d("fetchTopicList db",
                    e.getMessage() != null ? e.getMessage()
                            : "Error getting menus");

        }

        return discussionCategoriesModellist;
    }


}


