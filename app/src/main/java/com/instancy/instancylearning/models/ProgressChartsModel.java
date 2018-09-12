package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class ProgressChartsModel {

    public static class ContentStatusModel implements Serializable {

        public String status = "";
        public int contentCount = 0;
    }

    public static class ContentTypesModel implements Serializable {

        public String contentType = "";
        public int contentCount = 0;
    }

    public static class AverageScore implements Serializable {

        public String contentType = "";
        public int contentCount = 0;
    }

}
