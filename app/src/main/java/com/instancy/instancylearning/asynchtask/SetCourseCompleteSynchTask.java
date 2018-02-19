package com.instancy.instancylearning.asynchtask;

import android.content.Context;
import android.os.AsyncTask;

import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.mylearning.MyLearningFragment;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class SetCourseCompleteSynchTask extends AsyncTask<String, Integer, Void> {


    Context context;
    DatabaseHandler databaseHandler;
    MyLearningModel learningModel;
    SetCompleteListner setCompleteListner;


    public SetCourseCompleteSynchTask(Context context, DatabaseHandler databaseHandler, MyLearningModel learningModel,SetCompleteListner setCompleteListner) {
        this.context = context;
        this.databaseHandler = databaseHandler;
        this.learningModel = learningModel;
        this.setCompleteListner = setCompleteListner;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
        databaseHandler.setCompleteMethods(context, learningModel);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        databaseHandler.close();
        setCompleteListner.completedStatus();
    }
}
