package com.instancy.instancylearning.advancedfilters_mylearning;

import android.content.ContentValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class ContentFilterByModel implements Serializable {


    public String categoryDisplayName = "";
    public String categoryName = "";
    public String categoryIcon = "";
    public String categoryID = "";
    public boolean goInside = false;
    public String selectedInnerSkills = "";

    public String selectedSkillsNameString = "";
    public String selectedSkillsCatIdString = "";

    public List<String> selectedSkillNamesArry = new ArrayList<>();
    public List<String> selectedSkillIdsArry = new ArrayList<>();

    public List<String> selectedChildSkillNamesArry = new ArrayList<>();

    public List<String> selectedChildSkillIdsArry = new ArrayList<>();
    public int categorySelectedID = -1;

    public String categorySelectedStartDate = "";
    public String categorySelectedEndDate = "";
}
