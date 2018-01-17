package com.instancy.instancylearning.chatmessanger;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class BaseMessage implements Serializable {

    public String title = "";
    public String location = "";
    public String companyName = "";
    public String fromDate = "";
    public String toDate = "";
    public String userID = "1";
    public String displayNo = "";
    public String description = "";
    public boolean tillDate = true;
    public String difference = "";

}
