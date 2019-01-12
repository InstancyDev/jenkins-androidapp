package com.instancy.instancylearning.normalfilters;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class AdvancedFilterModel implements Serializable {


    public int cellIdentifier = 0;

    public int parentID = 0;
    public String categoryName = "";
    public String categoryIcon = "";
    public int categoryID = 0;
    public int column1 = 0;
    public int displayOrder = 1;
    public int contentCount = 0;
    public String subCategoryIcon = "";
    public boolean isSubFiltersExists = false;
    public int keyIdent = 0;

    //exclusive for sort by
    public int attributeConfigID = 0;
    public String aliasName = "";
    public String displayText = "";
    public int showDefault = 0;


    //exclusive for type by and source
    public int choiceValue = 0;
    public String choiceText = "";

    //Exclusive for group by
    public String name = "";

    //Exclusive for Sort type
    public String sortTypeName = "";
    public String sortTypeID = "";

    //Exclusive jobrole
    public String jobroleID = "";
    public String jobroleName = "";
    public String jobroleParentID = "";




}
