package com.instancy.instancylearning.myskills;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class MySkillModel implements Serializable {

    public String skillName = "";
    public int siteID = 374;
    public int userId = 0;
    public JSONArray skilCount = null;
    public String skillcontentviewlink = "disabled";
    public List<SkillCountModel> skillCountModelList = null;

}

