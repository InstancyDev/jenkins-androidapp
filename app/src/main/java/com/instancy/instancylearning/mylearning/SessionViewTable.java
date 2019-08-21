package com.instancy.instancylearning.mylearning;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class SessionViewTable extends DatabaseHandler {


    AppUserModel appUserModel;
    private Context dbctx;
    DatabaseHandler databaseHandler;


    public SessionViewTable(Context context) {
        super(context);
        dbctx = context;
        databaseHandler = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
    }

    public void injectContentViewData(JSONObject jsonObject, String parentScoid) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("CourseList");

        ejectRecordsinTable(TBL_SESSIONVIEW);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);


            MyLearningModel myLearningModel = new MyLearningModel();


            myLearningModel.setAuthor(jsonMyLearningColumnObj.optString("AuthorDisplayName"));
            myLearningModel.setUserID(appUserModel.getUserIDValue());
            myLearningModel.setSiteID(appUserModel.getSiteIDValue());
            myLearningModel.setEventstartTime(jsonMyLearningColumnObj.optString("EventStartDateTime"));
            myLearningModel.setEventendTime(jsonMyLearningColumnObj.optString("EventEndDateTime"));
            myLearningModel.setCourseName(jsonMyLearningColumnObj.optString("Title"));
            myLearningModel.setTimeZone(jsonMyLearningColumnObj.optString("TimeZone"));
            myLearningModel.setAuthor(jsonMyLearningColumnObj.optString("AuthorDisplayName"));
            myLearningModel.setLocationName(jsonMyLearningColumnObj.optString("Location"));
            myLearningModel.setThumbnailImagePath(jsonMyLearningColumnObj.optString("ThumbnailImagePath"));
            myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.optString("ThumbnailIconPath"));
            myLearningModel.setContentType(jsonMyLearningColumnObj.optString("ContentType"));
            myLearningModel.setShortDes(jsonMyLearningColumnObj.optString("ShortDescription"));
            myLearningModel.setScoId(jsonMyLearningColumnObj.optString("ScoID"));
            myLearningModel.setContentID(jsonMyLearningColumnObj.optString("ContentID"));
            myLearningModel.setTrackScoid(parentScoid);

            myLearningModel.setSiteID(appUserModel.getSiteIDValue());

            injectContentViewDataIntoSqLites(myLearningModel);
        }

    }

    public void injectContentViewDataIntoSqLites(MyLearningModel myLearningModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = null;
        try {


            contentValues = new ContentValues();

            contentValues.put("eventname", myLearningModel.getCourseName());
            contentValues.put("startdate", myLearningModel.getEventstartTime());
            contentValues.put("enddate", myLearningModel.getEventendTime());
            contentValues.put("timezone", myLearningModel.getTimeZone());
            contentValues.put("thumbnailimage", myLearningModel.getThumbnailImagePath());
            contentValues.put("thumbnailicon", myLearningModel.getContentTypeImagePath());
            contentValues.put("instructors", myLearningModel.getAuthor());
            contentValues.put("location", myLearningModel.getLocationName());
            contentValues.put("contentid", myLearningModel.getContentID());
            contentValues.put("siteid", myLearningModel.getSiteID());
            contentValues.put("parentscoid", myLearningModel.getTrackScoid());
            contentValues.put("authorname", myLearningModel.getAuthor());
            contentValues.put("contenttype", myLearningModel.getContentType());
            contentValues.put("shortdesc", myLearningModel.getShortDes());
            contentValues.put("userid", myLearningModel.getUserID());


            db.insert(TBL_SESSIONVIEW, null, contentValues);

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<MyLearningModel> fetchContentViewData(String parentScoid) {
        List<MyLearningModel> myLearningModelList = null;
        MyLearningModel myLearningModel = new MyLearningModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuery = "SELECT * from " + TBL_SESSIONVIEW + " WHERE userid =" + appUserModel.getUserIDValue() + " AND siteid = " + appUserModel.getSiteIDValue() + " AND parentscoid = " + parentScoid;


        Log.d(TAG, "contentview: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                myLearningModelList = new ArrayList<MyLearningModel>();
                do {

                    myLearningModel = new MyLearningModel();

                    myLearningModel.setCourseName(cursor.getString(cursor
                            .getColumnIndex("eventname")));

                    myLearningModel.setEventstartTime(cursor.getString(cursor
                            .getColumnIndex("startdate")));

                    myLearningModel.setEventendTime(cursor.getString(cursor
                            .getColumnIndex("enddate")));

                    myLearningModel.setTimeZone(cursor.getString(cursor
                            .getColumnIndex("timezone")));

                    myLearningModel.setThumbnailImagePath(cursor.getString(cursor
                            .getColumnIndex("thumbnailimage")));

                    myLearningModel.setContentTypeImagePath(cursor.getString(cursor
                            .getColumnIndex("thumbnailicon")));

                    myLearningModel.setAuthor(cursor.getString(cursor
                            .getColumnIndex("authorname")));

                    myLearningModel.setLocationName(cursor.getString(cursor
                            .getColumnIndex("location")));

                    myLearningModel.setContentID(cursor.getString(cursor
                            .getColumnIndex("contentid")));

                    myLearningModel.setUserID(cursor.getString(cursor
                            .getColumnIndex("userid")));

                    myLearningModel.setSiteID(cursor.getString(cursor
                            .getColumnIndex("siteid")));


                    myLearningModel.setTrackScoid(cursor.getString(cursor
                            .getColumnIndex("parentscoid")));

                    myLearningModel.setContentType(cursor.getString(cursor
                            .getColumnIndex("contenttype")));

                    myLearningModel.setShortDes(cursor.getString(cursor
                            .getColumnIndex("shortdesc")));


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
}


