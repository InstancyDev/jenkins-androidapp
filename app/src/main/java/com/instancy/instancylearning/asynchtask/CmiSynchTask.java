package com.instancy.instancylearning.asynchtask;

import android.content.Context;
import android.os.AsyncTask;

import com.instancy.instancylearning.globalpackage.SynchData;
import com.instancy.instancylearning.interfaces.SynchCompleted;


/**
 * Created by Upendranath on 5/22/2017.
 */

public class CmiSynchTask extends AsyncTask<String, Integer, Void> {


    Context context;
    SynchData synchData;
    public SynchCompleted synchCompleted;
    int synch = 0;

    public CmiSynchTask(Context context) {
        this.context = context;
        synchData = new SynchData(context);
        synch = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(String... params) {
        synchData.SyncData();

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        Toast.makeText(context, "   Synch Completed   ", Toast.LENGTH_SHORT).show();
        if (synchCompleted != null) {
            synchCompleted.completedSynch();
        }
    }
}
