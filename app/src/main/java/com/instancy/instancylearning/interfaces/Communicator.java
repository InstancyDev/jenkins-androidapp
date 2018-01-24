package com.instancy.instancylearning.interfaces;

import android.content.ContentValues;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Upendranath on 11/30/2017.
 */

public interface Communicator {

    public void messageRecieved(JSONArray messageReceived);

}
