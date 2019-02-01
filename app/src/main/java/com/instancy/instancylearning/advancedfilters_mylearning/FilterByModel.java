package com.instancy.instancylearning.advancedfilters_mylearning;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class FilterByModel implements Serializable {

    public String parentID = "0";
    public String categoryName = "";
    public String categoryIcon = "";
    public String categoryID = "0";
    public boolean isSelected = false;
    public boolean isContainsChild = false;

}
