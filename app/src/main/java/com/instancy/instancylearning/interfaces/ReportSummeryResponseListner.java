package com.instancy.instancylearning.interfaces;

import com.instancy.instancylearning.progressreports.ProgressReportChildModel;
import com.instancy.instancylearning.progressreports.ProgressReportModel;


/**
 * Created by Upendranath on 7/26/2017 Working on InstancyLearning.
 */

public interface ReportSummeryResponseListner {

    void statusUpdateFromServer(boolean isFromGroup, ProgressReportModel progressReportModel, ProgressReportChildModel progressReportChildModel);

}
