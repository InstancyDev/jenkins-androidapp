package com.instancy.instancylearning.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Upendranath on 5/29/2017.
 */

public class NativeSetttingsModel {

    public static HashMap<String, List<String>> getData(Boolean isLogin) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        if (!isLogin) {
            List<String> cricket = new ArrayList<String>();
            cricket.add("Reset URL");
            cricket.add("Site URL");
            expandableListDetail.put("SITE SETTINGS", cricket);
        } else {
            List<String> football = new ArrayList<String>();

            football.add("Assigned Content Target Reminder");
            football.add("Content Assignment");
            football.add("New Content items Available in the Catalog_fragment");
            football.add("Content items Unassigned");

            List<String> basketball = new ArrayList<String>();
            basketball.add("Enable Auto Download of Content");
            expandableListDetail.put("NOTIFICATION", football);
            expandableListDetail.put("DOWNLOAD", basketball);
        }


        return expandableListDetail;
    }

}
