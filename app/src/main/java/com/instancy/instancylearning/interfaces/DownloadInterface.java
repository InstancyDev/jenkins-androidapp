package com.instancy.instancylearning.interfaces;

/**
 * Created by Upendranath on 7/31/2017 Working on InstancyLearning.
 */

public interface DownloadInterface {

    void deletedTheContent(int updateProgress);

    void cancelEnrollment(boolean cancelIt);

    void addToArchive(boolean added);

    void removeFromMylearning(boolean isRemoved);

    void resheduleTheEvent(boolean isReshedule);

    void badCancelEnrollment(boolean cancelIt);

    void viewCertificateLink(boolean viewIt);
}
