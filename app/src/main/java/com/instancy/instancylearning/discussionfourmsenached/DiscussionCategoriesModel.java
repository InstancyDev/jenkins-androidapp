package com.instancy.instancylearning.discussionfourmsenached;

import java.io.Serializable;

/**
 * Created by Upendranath on 12/12/2017.
 */

public class DiscussionCategoriesModel implements Serializable {

    public int id = 0;
    public String categoryName = "";
    public String fullName = "";
    public int categoryID = 0;
    public String parentId = "";
    public String iconpath = "";
    public String agreementDocId = "";
    public int contentCount = 0;
    public int refParentID = 0;
    public int displayOrder = 0;
    public int userId = -1;
    public int siteId = 374;
    public boolean isSelected = false;
}
