package com.instancy.instancylearning.interfaces;

import com.instancy.instancylearning.models.MyLearningModel;

/**
 * Created by Upendranath on 7/31/2017 Working on InstancyLearning.
 */

public interface EventInterface {


    void cancelEnrollment(MyLearningModel learningModel, boolean isCancelEnrollment);

    void archiveAndUnarchive(boolean isArchived);
}
