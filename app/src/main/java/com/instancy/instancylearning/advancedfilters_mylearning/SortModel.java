package com.instancy.instancylearning.advancedfilters_mylearning;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class SortModel implements Serializable {


    public int categoryID = 0;
    public int componentID = 0;
    public String localID = "en-us";
    public String optionDisplayText = "";
    public String optionIdValue = "";
    public boolean isSelected = false;
    public int ratingValue = 0;


//    ID : 8
//    SiteID : 374
//    ComponentID : 3
//    LocalID : ""+preferencesManager.getLocalizationStringValue(getResources().getString(R.string.locale_name))+""
//    OptionText : "Title A-Z"
//    OptionValue : "MC.Name asc"
//    EnableColumn : null

}
