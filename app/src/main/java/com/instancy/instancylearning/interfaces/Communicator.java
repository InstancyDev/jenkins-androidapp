package com.instancy.instancylearning.interfaces;
import org.json.JSONArray;


/**
 * Created by Upendranath on 11/30/2017.
 */

public interface Communicator {

    public void messageRecieved(JSONArray messageReceived);

    public void userOnline(boolean isSingle, JSONArray objReceived);

}
