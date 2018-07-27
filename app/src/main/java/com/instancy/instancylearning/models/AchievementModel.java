package com.instancy.instancylearning.models;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class AchievementModel implements Serializable {

    public String userBadges = "";
    public String userLevel = "";
    public String userPoints = "";

    public boolean showLevelSection = false;
    public boolean showPointSection = false;
    public boolean showBadgeSection = false;

}
