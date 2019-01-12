package com.instancy.instancylearning.advancedfilters_mylearning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class AllFilterModel implements Serializable {

    public String categoryName = "";
    public String categoryIcon = "";
    public int categoryID = 0;
    public String categorySelectedData = "";
    public int categorySelectedID = -1;
    public List<String> groupArrayList = new ArrayList<>();
    public boolean isGroup = false;
}
