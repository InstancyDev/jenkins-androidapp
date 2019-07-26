package com.instancy.instancylearning.catalog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.instancy.instancylearning.askexpertenached.AskExpertAnswerModelDg;
import com.instancy.instancylearning.askexpertenached.AskExpertCategoriesModelDigi;
import com.instancy.instancylearning.askexpertenached.AskExpertCommentModel;
import com.instancy.instancylearning.askexpertenached.AskExpertFilterMappingModel;
import com.instancy.instancylearning.askexpertenached.AskExpertQuestionModelDg;
import com.instancy.instancylearning.askexpertenached.AskExpertSkillsModelDg;
import com.instancy.instancylearning.askexpertenached.AskExpertSortModel;
import com.instancy.instancylearning.askexpertenached.AskExpertUpVoters;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.convertDateToSortDateFormat;
import static com.instancy.instancylearning.utils.Utilities.fromHtmlToString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ContentViewTable extends DatabaseHandler {


    AppUserModel appUserModel;
    private Context dbctx;
    DatabaseHandler databaseHandler;


    public ContentViewTable(Context context) {
        super(context);
        dbctx = context;
        databaseHandler = new DatabaseHandler(context);
        appUserModel = AppUserModel.getInstance();
    }

    public void injectContentViewData(JSONObject jsonObject, String parentScoid) throws JSONException {

        JSONArray jsonTableAry = jsonObject.getJSONArray("table5");

        ejectRecordsinTable(TBL_CONTENTVIEW);

        for (int i = 0; i < jsonTableAry.length(); i++) {
            JSONObject jsonMyLearningColumnObj = jsonTableAry.getJSONObject(i);


            MyLearningModel myLearningModel = new MyLearningModel();

//            db.execSQL("CREATE TABLE IF NOT EXISTS "
//                    + TBL_CONTENTVIEW
//                    + "(id INTEGER PRIMARY KEY AUTOINCREMENT, coursename TEXT, authorname TEXT, contenttype TEXT, shortdesc TEXT, thumbnailimage TEXT, thumbnailicon TEXT, scoid TEXT, siteid TEXT, userid TEXT,medianame TEXT,parentscoid TEXT)");


            myLearningModel.setAuthor(jsonMyLearningColumnObj.optString("author"));
            myLearningModel.setUserID(appUserModel.getUserIDValue());
            myLearningModel.setCourseName(jsonMyLearningColumnObj.optString("name"));
            myLearningModel.setContentType(jsonMyLearningColumnObj.optString("objecttypeid"));
            myLearningModel.setShortDes(jsonMyLearningColumnObj.optString("shortdescription"));
            myLearningModel.setThumbnailImagePath(jsonMyLearningColumnObj.optString("thumbnailimagepath"));
            myLearningModel.setContentTypeImagePath(jsonMyLearningColumnObj.optString("contenttypethumbnail"));
            myLearningModel.setMediaName(jsonMyLearningColumnObj.optString("medianame"));
            myLearningModel.setScoId(jsonMyLearningColumnObj.optString("scoid"));
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

            contentValues.put("coursename", myLearningModel.getCourseName());
            contentValues.put("authorname", myLearningModel.getAuthor());
            contentValues.put("contenttype", myLearningModel.getContentType());
            contentValues.put("shortdesc", myLearningModel.getShortDes());
            contentValues.put("thumbnailimage", myLearningModel.getThumbnailImagePath());
            contentValues.put("thumbnailicon", myLearningModel.getContentTypeImagePath());
            contentValues.put("scoid", myLearningModel.getScoId());
            contentValues.put("siteid", myLearningModel.getSiteID());
            contentValues.put("userid", myLearningModel.getUserID());
            contentValues.put("medianame", myLearningModel.getMediaName());
            contentValues.put("parentscoid", myLearningModel.getTrackScoid());

            db.insert(TBL_CONTENTVIEW, null, contentValues);

        } catch (SQLiteException exception) {

            exception.printStackTrace();
        }

    }

    public List<MyLearningModel> fetchContentViewData(String parentScoid) {
        List<MyLearningModel> myLearningModelList = null;
        MyLearningModel myLearningModel = new MyLearningModel();
        SQLiteDatabase db = this.getWritableDatabase();

        String strSelQuery = "SELECT * from " + TBL_CONTENTVIEW + " WHERE userid =" + appUserModel.getUserIDValue() + " AND siteid = " + appUserModel.getSiteIDValue() + " AND parentscoid = " + parentScoid;


        Log.d(TAG, "contentview: " + strSelQuery);
        try {
            Cursor cursor = null;
            cursor = db.rawQuery(strSelQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                myLearningModelList = new ArrayList<MyLearningModel>();
                do {

                    myLearningModel = new MyLearningModel();

                    myLearningModel.setCourseName(cursor.getString(cursor
                            .getColumnIndex("coursename")));

                    myLearningModel.setAuthor(cursor.getString(cursor
                            .getColumnIndex("authorname")));

                    myLearningModel.setContentType(cursor.getString(cursor
                            .getColumnIndex("contenttype")));

                    myLearningModel.setShortDes(cursor.getString(cursor
                            .getColumnIndex("shortdesc")));

                    myLearningModel.setThumbnailImagePath(cursor.getString(cursor
                            .getColumnIndex("thumbnailimage")));

                    myLearningModel.setContentTypeImagePath(cursor.getString(cursor
                            .getColumnIndex("thumbnailicon")));

                    myLearningModel.setScoId(cursor.getString(cursor
                            .getColumnIndex("scoid")));

                    myLearningModel.setSiteID(cursor.getString(cursor
                            .getColumnIndex("siteid")));

                    myLearningModel.setUserID(cursor.getString(cursor
                            .getColumnIndex("userid")));

                    myLearningModel.setMediaName(cursor.getString(cursor
                            .getColumnIndex("medianame")));

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


