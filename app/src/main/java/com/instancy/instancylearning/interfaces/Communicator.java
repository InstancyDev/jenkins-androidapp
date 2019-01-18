package com.instancy.instancylearning.interfaces;
import org.json.JSONArray;


/**
 * Created by Upendranath on 11/30/2017.
 */

public interface Communicator {

    void messageRecieved(JSONArray messageReceived);

    void userOnline(int typeUpdate, JSONArray objReceived);

}
