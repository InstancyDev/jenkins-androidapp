package com.instancy.instancylearning.asynchtask;

import android.content.Context;
import android.os.AsyncTask;

import com.instancy.instancylearning.globalpackage.SynchData;

/**
 * Created by Upendranath on 5/22/2017.
 */

public class CmiSynchTask extends AsyncTask<String, Integer, Void> {


    Context context;
    SynchData synchData;

    public CmiSynchTask(Context context) {
        this.context = context;
        synchData = new SynchData(context);
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

    }
}
