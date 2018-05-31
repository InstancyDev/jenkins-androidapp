package com.instancy.instancylearning.mycompetency;

import java.io.Serializable;

/**
 * Created by Upendranath on 6/8/2017 Working on InstancyLearning.
 */

public class SkillModel implements Serializable {


    public String skillName = "";
    public String skillID = "";
    public String prefCategoryID = "";
    public String jobRoleID = "";
    public String skillDescription = "";
    public String requiredProfValues = "";
    public String userPictureURL = "";
    public String managerPictureURL = "";
    public String contentAuthorPictureURL = "";
    public int requiredScore = 0;
    public String valueName = "";
    public double gapScore = 0.0;
    public int userScore = 100;
    public int managerScore = 100;
    public double contentAuthorScore = 0.0;

}