package com.instancy.instancylearning.myskills;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.Spanned;
import android.util.Log;

import com.google.gson.JsonArray;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionCommentsModelDg;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionForumModelDg;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionReplyModelDg;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionTopicModelDg;
import com.instancy.instancylearning.models.AppUserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.formatDate;
import static com.instancy.instancylearning.utils.Utilities.fromHtml;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class MySkillDbTables extends DatabaseHandler {


    AppUserModel appUserModel;
    private Context dbctx;
    DatabaseHandler databaseHandler;

    public MySkillDbTables(Context context) {
        super(context);
        dbctx = context;
        databaseHandler = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
    }

    public void injectMySkillData(String responseStr) throws JSONException {

        JSONArray jsonTableAry = new JSONArray(responseStr);

        // for deleting records in table for respective table
        ejectRecordsinTable(TBL_MYSKILLS);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);

            MySkillModel mySkillModel = new MySkillModel();

            mySkillModel.skillName = jsonMyLearningColumnObj.optString("skillname");
            mySkillModel.skilCount = jsonMyLearningColumnObj.optJSONArray("Skilcount");
            mySkillModel.siteID = Integer.parseInt(appUserModel.getSiteIDValue());
            mySkillModel.userId = Integer.parseInt(appUserModel.getUserIDValue());

            injectDiscussionForums(mySkillModel);
        }

    }

    public void injectDiscussionForums(MySkillModel mySkillModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("siteid", mySkillModel.siteID);
            contentValues.put("userid", mySkillModel.userId);
            contentValues.put("skillcountObj", "" + mySkillModel.skilCount);
            contentValues.put("skillname", mySkillModel.skillName);

            db.insert(TBL_MYSKILLS, null, contentValues);
        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<MySkillModel> fetchMySkillsData(String siteId) {

        List<MySkillModel> mySkillModelList = null;
        MySkillModel mySkillModel = new MySkillModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuery = "SELECT * from " + TBL_MYSKILLS + " where siteID =" + siteId;

        Log.d(TAG, "fetchCatalogModel: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                mySkillModelList = new ArrayList<MySkillModel>();
                do {

                    mySkillModel = new MySkillModel();

                    mySkillModel.siteID = cursor.getInt(cursor
                            .getColumnIndex("siteid"));

                    mySkillModel.userId = cursor.getInt(cursor.getColumnIndex("userid"));

                    mySkillModel.skillName = cursor.getString(cursor
                            .getColumnIndex("skillname"));

                    String jsonString = (cursor.getString(cursor.getColumnIndex("skillcountObj")));

                    if (jsonString.length() > 0) {
                        mySkillModel.skilCount = new JSONArray(jsonString);
                    }


                    mySkillModel.skillCountModelList = generateSkillCount(mySkillModel.skilCount);

                    mySkillModelList.add(mySkillModel);
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
        return mySkillModelList;
    }


    public List<SkillCountModel> generateSkillCount(JSONArray jsonArray) throws JSONException {
        List<SkillCountModel> skillCountModelList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonArray.getJSONObject(i);

            SkillCountModel skillCountModel = new SkillCountModel();

            skillCountModel.prefCatDescription = jsonMyLearningColumnObj.optString("PrefCatDescription");
            skillCountModel.prefCatID = jsonMyLearningColumnObj.optInt("PrefCatID");
            skillCountModel.porefCatName = jsonMyLearningColumnObj.optString("PrefCatName");
            skillCountModel.expertLevel = jsonMyLearningColumnObj.optInt("ExpertLevel");

            skillCountModelList.add(skillCountModel);
        }


        return skillCountModelList;
    }


}


