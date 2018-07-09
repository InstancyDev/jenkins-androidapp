package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class LrsDetails implements Serializable {

    public int Id;
    public String lrs = "";
    public String lrsUrl = "";
    public String url = "";
    public String method = "";
    public String data = "";
    public String auth = "";
    public String actor = "";
    public String callback = "";
    public String ignore404 = "";
    public String extraHeaders = "";
    public String isUpdated = "false";
    public int siteId;
    public int scoId;
    public int userId;
}
