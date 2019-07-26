package com.instancy.instancylearning.interfaces;

import com.instancy.instancylearning.progressreports.ProgressReportChildModel;
import com.instancy.instancylearning.progressreports.ProgressReportModel;

/**
 * Created by Upendranath on 7/31/2017 Working on InstancyLearning.
 */

public interface ProgressReportInterface {

    void viewCertificateLink(boolean fromChild, ProgressReportModel progressReportModel, ProgressReportChildModel progressReportChildModel);
}
