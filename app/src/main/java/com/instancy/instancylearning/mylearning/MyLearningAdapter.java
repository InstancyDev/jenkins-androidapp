package com.instancy.instancylearning.mylearning;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.interfaces.MylearningInterface;
import com.instancy.instancylearning.interfaces.DownloadStart;
import com.instancy.instancylearning.interfaces.EventInterface;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.DETAIL_CLOSE_CODE;
import static com.instancy.instancylearning.utils.StaticValues.REVIEW_REFRESH;
import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormat;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class MyLearningAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<MyLearningModel> myLearningModel = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SideMenusModel sideMenusModel;
    //  SVProgressHUD svProgressHUD;
    PreferencesManager preferencesManager;
    private String TAG = MyLearningAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<MyLearningModel> searchList;
    AppController appcontroller;
    EventInterface eventInterface;
    boolean isReportEnabled = true;
    boolean isWaitListedContent = false;
    boolean isIconEnabled = false;

    public MyLearningAdapter(Activity activity, int resource, List<MyLearningModel> myLearningModel, EventInterface eventInterface, boolean isReportEnabled, SideMenusModel sideMenusModel, boolean isIconEnabled) {
        this.eventInterface = eventInterface;
        this.activity = activity;
        this.myLearningModel = myLearningModel;
        this.searchList = new ArrayList<MyLearningModel>();
        this.searchList.addAll(myLearningModel);
        this.resource = resource;
        this.isReportEnabled = isReportEnabled;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        // svProgressHUD = new SVProgressHUD(activity);
        this.sideMenusModel = sideMenusModel;
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
        /* setup enter and exit animation */
        appcontroller = AppController.getInstance();
        this.isIconEnabled = isIconEnabled;
    }

    public void refreshList(List<MyLearningModel> myLearningModel, boolean isWaitListedContent) {
        this.myLearningModel = myLearningModel;
        this.searchList = new ArrayList<MyLearningModel>();
        this.searchList.addAll(myLearningModel);
        this.isWaitListedContent = isWaitListedContent;
        this.notifyDataSetChanged();
    }

    public void applySortBy(final boolean isAscn, String configid) {

//        Collections.sort(myLearningModel, Collections.reverseOrder());

        switch (configid) {

            case "81":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.getCourseName().compareToIgnoreCase(obj2.getCourseName());

                        } else {
                            return obj2.getCourseName().compareToIgnoreCase(obj1.getCourseName());
                        }
                    }
                });
                break;
            case "211":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        if (isAscn) {
                            return obj1.getAuthor().compareToIgnoreCase(obj2.getAuthor());

                        } else {
                            return obj2.getAuthor().compareToIgnoreCase(obj1.getAuthor());
                        }

                    }
                });
                break;
            case "191":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {
                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.getStatusActual().compareToIgnoreCase(obj2.getStatusActual());

                        } else {
                            return obj2.getStatusActual().compareToIgnoreCase(obj1.getStatusActual());
                        }
                    }
                });
                break;
            case "181":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.getDateAssigned().compareToIgnoreCase(obj2.getDateAssigned());

                        } else {
                            return obj2.getDateAssigned().compareToIgnoreCase(obj1.getDateAssigned());
                        }
                    }
                });
                break;
            case "171":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.getDateAssigned().compareToIgnoreCase(obj2.getDateAssigned());

                        } else {
                            return obj2.getDateAssigned().compareToIgnoreCase(obj1.getDateAssigned());
                        }
                    }
                });
                break;
            case "default":
                break;

        }

        this.notifyDataSetChanged();
    }

    public void applyGroupBy(JSONObject groupBy) {

        String groupID = groupBy.optString("group").toLowerCase();
        String objectID = "";
        if (groupID.length() == 0)
            return;

        switch (groupID) {

            case "authors":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        return obj1.getAuthor().compareToIgnoreCase(obj2.getAuthor());

                    }
                });
                objectID = "authors";
                break;
            case "contenttypes":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        return obj1.getObjecttypeId().compareToIgnoreCase(obj2.getObjecttypeId());

                    }
                });
                objectID = "contenttypes";
                break;
            case "skills":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        return obj1.getObjecttypeId().compareToIgnoreCase(obj2.getObjecttypeId());

                    }
                });
                objectID = "skills";
                break;

            case "job":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        return obj1.getObjecttypeId().compareToIgnoreCase(obj2.getObjecttypeId());

                    }
                });
                objectID = "job";
                break;
            case "categories":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order
                        return obj1.getComponentId().compareToIgnoreCase(obj2.getComponentId());

                    }
                });
                objectID = "categories";
                break;
            case "default":
                break;

        }

        if (objectID.equalsIgnoreCase("authors")) {
            if (myLearningModel.size() > 0) {
                String groupName = "";

                for (int i = 0; i < myLearningModel.size(); i++) {
                    Log.d(TAG, "applyGroupBy: author name " + myLearningModel.get(i).getAuthor());

                    if (!myLearningModel.get(i).getAuthor().equalsIgnoreCase(groupName)) {
                        myLearningModel.get(i).setGroupName(myLearningModel.get(i).getAuthor());
                        groupName = myLearningModel.get(i).getAuthor();
                    }
                }

            }
//        } else if (objectID.equalsIgnoreCase("contenttypes")) {
        } else {
            if (myLearningModel.size() > 0) {
                String groupName = "";

                for (int i = 0; i < myLearningModel.size(); i++) {
                    Log.d(TAG, "applyGroupBy: author name " + myLearningModel.get(i).getMediaName());

                    if (!myLearningModel.get(i).getObjecttypeId().equalsIgnoreCase(groupName)) {
                        myLearningModel.get(i).setGroupName(myLearningModel.get(i).getMediaName());
                        groupName = myLearningModel.get(i).getObjecttypeId();
                    }
                }

            }


        }


        this.notifyDataSetChanged();
    }


//    public static Set<Integer> findDuplicates(int[] input) {
//        Set<Integer> duplicates = new HashSet<Integer>();
//        for (int i = 0; i < input.length; i++) {
//            for (int j = 1; j < input.length; j++) {
//                if (input[i] == input[j] && i != j) {
// duplicate element found duplicates.add(input[i]); break; } } } return duplicates; }


    public void filterByObjTypeId(JSONObject filterBy) {

        String contentType = filterBy.optString("contentype");

        if (contentType.length() == 0)
            return;

        myLearningModel.clear();
        if (contentType.length() == 0) {
            myLearningModel.addAll(searchList);
        } else {
            for (MyLearningModel s : searchList) {
                Log.d(TAG, "filterByCategoryId: " + s.getObjecttypeId());
                if (s.getObjecttypeId().equalsIgnoreCase(contentType)) {
                    myLearningModel.add(s);
                }
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return myLearningModel.size();
    }

    @Override
    public Object getItem(int position) {
        return myLearningModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {

        final ViewHolder holder;
        View vi = convertView;
        if (convertView == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.mylearning_cell, null);
        holder = new ViewHolder(vi);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.myLearningDetalData = myLearningModel.get(position);
        holder.txtTitle.setText(myLearningModel.get(position).getCourseName());
        holder.txtCourseName.setText(myLearningModel.get(position).getMediaName());

        if (myLearningModel.get(position).getGroupName().length() > 0) {
            holder.txtgroupName.setVisibility(View.VISIBLE);
            holder.txtgroupName.setText(myLearningModel.get(position).getGroupName());
        } else {
            holder.txtgroupName.setVisibility(View.GONE);
        }

        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
            holder.txtAuthor.setText(myLearningModel.get(position).getPresenter() + " ");
            String fromDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventstartTime(), "yyyy-MM-dd hh:mm:ss");
//            String toDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventendTime(), "yyyy-MM-dd hh:mm:ss");
            holder.txtEventFromTo.setText(fromDate);
            holder.txtEventLocation.setText(myLearningModel.get(position).getLocationName());
            if (myLearningModel.get(position).getTypeofevent() == 2) {
                holder.locationlayout.setVisibility(View.GONE);
            }
//            holder.txtTimeZone.setText(myLearningModel.get(position).getTimeZone());
            holder.txtTimeZone.setVisibility(View.GONE);
        } else {
            holder.txtAuthor.setText(myLearningModel.get(position).getAuthor() + " ");
        }
        holder.txtShortDisc.setText(myLearningModel.get(position).getShortDes());

        if (myLearningModel.get(position).getSiteName().equalsIgnoreCase("")) {
            holder.consolidateLine.setVisibility(View.GONE);

        } else {
            holder.consolidateLine.setVisibility(View.VISIBLE);
        }
        holder.txtSiteName.setText(myLearningModel.get(position).getSiteName());
        float ratingValue = 0;
        try {
            ratingValue = Float.parseFloat(myLearningModel.get(position).getRatingId());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            ratingValue = 0;
        }
        holder.ratingBar.setRating(ratingValue);

        if (vi.getResources().getString(R.string.app_name).equalsIgnoreCase(vi.getResources().getString(R.string.crop_life))) {
            holder.ratingBar.setVisibility(View.GONE);
        }
        float ratingRequired = 0;
        try {
            ratingRequired = Float.parseFloat(uiSettingsModel.getMinimimRatingRequiredToShowRating());
        } catch (NumberFormatException exce) {
            ratingRequired = 0;
        }

        if (myLearningModel.get(position).getTotalratings() >= uiSettingsModel.getNumberOfRatingsRequiredToShowRating() && ratingValue >= ratingRequired) {
            holder.txtWriteReview.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.VISIBLE);
        }

        if (!myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70") && myLearningModel.get(position).getStatusActual().toLowerCase().contains("completed") || myLearningModel.get(position).getStatusActual().equalsIgnoreCase("passed") || myLearningModel.get(position).getStatusActual().equalsIgnoreCase("failed")) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.txtWriteReview.setVisibility(View.VISIBLE);
        } else if (myLearningModel.get(position).getStatusActual().toLowerCase().equalsIgnoreCase("completed") && myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {

            holder.ratingBar.setVisibility(View.GONE);
            holder.txtWriteReview.setVisibility(View.GONE);

        } else if (myLearningModel.get(position).getStatusActual().equalsIgnoreCase("attended") && myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {

            holder.ratingBar.setVisibility(View.GONE);
            holder.txtWriteReview.setVisibility(View.VISIBLE);

        } else {
            holder.ratingBar.setIsIndicator(true);
        }

        // apply colors

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.btnDownload.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtWriteReview.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        holder.txtWriteReview.setText(getLocalizationValue(JsonLocalekeys.details_button_writeareviewbutton));
        holder.txtEnrollShedule.setText(getLocalizationValue(JsonLocalekeys.details_label_enroll));
        holder.txtEnrollShedule.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        holder.txtEventFromTo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtEventLocation.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTimeZone.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtAthrIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLocationIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtEvntIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (vi.getResources().getString(R.string.app_name).equalsIgnoreCase(vi.getResources().getString(R.string.app_medmentor))) {
            if (isValidString(myLearningModel.get(position).getCredits())) {
                holder.txtCredits.setText(myLearningModel.get(position).getDecimal2());
                holder.lbCredits.setText(" CME");

            }
            holder.creditsLayout.setVisibility(View.VISIBLE);
        } else {
            holder.creditsLayout.setVisibility(View.GONE);
        }
        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(vi.getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);

        Drawable progress = holder.ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor(uiSettingsModel.getAppTextColor()));

//        initVolleyCallback(myLearningModel.get(position), position);

        if (myLearningModel.get(position).getShortDes().isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }
        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {

            holder.circleProgressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.txtAthrIcon.setVisibility(View.VISIBLE);

            if (isValidString(myLearningModel.get(position).getLocationName())) {
                holder.eventLayout.setVisibility(View.VISIBLE);
            } else {
                holder.eventLayout.setVisibility(View.GONE);
            }

        } else {
            holder.eventLayout.setVisibility(View.GONE);
//            holder.txtAthrIcon.setVisibility(View.GONE);
        }
        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("688") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("27")) {
            // 102 commented for some XAPI content
            holder.btnDownload.setVisibility(View.GONE);
            holder.circleProgressBar.setVisibility(View.GONE);

//            holder.progressBar.setVisibility(View.GONE);

        } else {

            if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
                holder.progressBar.setVisibility(View.GONE);
                holder.btnDownload.setVisibility(View.GONE);
                holder.circleProgressBar.setVisibility(View.GONE);
            } else {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.btnDownload.setVisibility(View.VISIBLE);
                holder.circleProgressBar.setVisibility(View.GONE);
            }
            if (uiSettingsModel.getMyLearningContentDownloadType().equalsIgnoreCase("0")) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.circleProgressBar.setVisibility(View.GONE);
            } else {
                File myFile = new File(myLearningModel.get(position).getOfflinepath());

                if (myFile.exists()) {
                    holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
                    holder.btnDownload.setEnabled(false);

                } else {
                    holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorBlack));
                    holder.btnDownload.setEnabled(true);
                }
            }
//              File extStore = Environment.getExternalStorageDirectory();
        }


        if (myLearningModel.get(position).getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
            holder.txtEnrollShedule.setVisibility(View.VISIBLE);
            holder.authorLayout.setVisibility(View.GONE);
            holder.eventLayout.setVisibility(View.GONE);
            holder.locationlayout.setVisibility(View.GONE);
        }
//        else {
//
//            holder.txtEnrollShedule.setVisibility(View.GONE);
//        }

        else if (myLearningModel.get(position).getEventScheduleType() == 2 && myLearningModel.get(position).isEnrollFutureInstance()) {
            holder.txtEnrollShedule.setVisibility(View.VISIBLE);
            holder.authorLayout.setVisibility(View.GONE);
            holder.eventLayout.setVisibility(View.GONE);
            holder.locationlayout.setVisibility(View.GONE);
        } else if (isWaitListedContent && isValidString(myLearningModel.get(position).getReSheduleEvent())) {
            holder.txtEnrollShedule.setVisibility(View.VISIBLE);
            holder.authorLayout.setVisibility(View.GONE);
            holder.eventLayout.setVisibility(View.GONE);
            holder.locationlayout.setVisibility(View.GONE);

        } else {

            holder.txtEnrollShedule.setVisibility(View.GONE);
        }


        holder.txtCourseStatus.setVisibility(View.VISIBLE);

//        String courseStatus = "";
//        int progressPercentage = 0;
//        String statusFromModel = myLearningModel.get(position).getStatusActual();
//        String statusDisplay = myLearningModel.get(position).getStatusDisplay();
//
//        Log.d(TAG, "getView: statusFromModel" + statusFromModel);

//        if (statusFromModel.equalsIgnoreCase("Completed") || (statusFromModel.toLowerCase().contains("passed") || statusFromModel.toLowerCase().contains("failed")) || statusFromModel.equalsIgnoreCase("completed")) {
//            if (statusFromModel.equalsIgnoreCase("Completed")) {
//                statusFromModel = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel);
//            } else if (statusFromModel.equalsIgnoreCase("failed")) {
//
//                statusFromModel = vi.getContext().getString(R.string.status_completed_failed);
//            } else if (statusFromModel.equalsIgnoreCase("passed")) {
//                statusFromModel = getLocalizationValue(JsonLocalekeys.status_completed_passed);
//            }
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusCompleted)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
//            courseStatus = statusFromModel;
//            progressPercentage = 100;
//        } else if (statusFromModel.equalsIgnoreCase("Not Started")) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusNotStarted)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
//            courseStatus = getLocalizationValue(JsonLocalekeys.mylearning_label_notstartedlabel);
//            progressPercentage = 0;
//        } else if (statusFromModel.equalsIgnoreCase("incomplete") || (statusFromModel.toLowerCase().contains("inprogress")) || (statusFromModel.toLowerCase().contains("in progress"))) {
//
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusInProgress)));
//            String status = "";
//            if (statusFromModel.equalsIgnoreCase("incomplete")) {
//
//                status = getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel);
//            } else if (statusFromModel.length() == 0) {
//
//                status = getLocalizationValue(JsonLocalekeys.mylearning_label_inprogresslabel);
//            } else {
//                status = statusFromModel;
//
//            }
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusInProgress));
//            courseStatus = status;
//            progressPercentage = 50;
//        } else if (statusFromModel.equalsIgnoreCase("pending review") || (statusFromModel.toLowerCase().contains("pendingreview")) || (statusFromModel.toLowerCase().contains("grade"))) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
//            courseStatus = getLocalizationValue(JsonLocalekeys.mylearning_label_pendingreviewlabel);
//            ;
//            progressPercentage = 100;
//        } else if (statusFromModel.equalsIgnoreCase("Registered") || (statusFromModel.toLowerCase().contains("registered"))) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
//            courseStatus = getLocalizationValue(JsonLocalekeys.mylearning_label_registerlabel);
//
//            if (myLearningModel.get(position).getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
//                courseStatus = getLocalizationValue(JsonLocalekeys.details_label_tobeshedule);
//                holder.txtCourseStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
//            }
//            progressPercentage = -1;
//        } else if (statusFromModel.toLowerCase().contains("attended")) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
//            courseStatus = getLocalizationValue(JsonLocalekeys.mylearning_label_attendedlabel);
//            progressPercentage = -1;
//        } else if (statusFromModel.toLowerCase().contains("Expired")) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
//            courseStatus = getLocalizationValue(JsonLocalekeys.mylearning_label_expiredlabel);
//            ;
//            progressPercentage = -1;
//        } else if (statusFromModel.toLowerCase().contains("waitlisted")) {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
//            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
//            courseStatus = statusFromModel;
//            progressPercentage = -1;
//        } else {
//            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
//            courseStatus = statusFromModel;
//            progressPercentage = 0;
//        }
//        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
//            holder.txtCourseStatus.setText(courseStatus);
//        } else {
//            if (isValidString(myLearningModel.get(position).getPercentCompleted())) {
//                holder.txtCourseStatus.setText(courseStatus + "(" + myLearningModel.get(position).getPercentCompleted() + "%)");
//                try {
//                    holder.progressBar.setProgress(Integer.parseInt(myLearningModel.get(position).getPercentCompleted()));
//                } catch (NumberFormatException ex) {
//                    ex.printStackTrace();
//                    holder.progressBar.setProgress(progressPercentage);
//                }
//
//            } else {
//                holder.txtCourseStatus.setText(courseStatus + "(" + progressPercentage + "%)");
//                holder.progressBar.setProgress(progressPercentage);
//            }
//
//        }

        int progressPercentage = 0;
        String statusFromModel = myLearningModel.get(position).getStatusActual();
        String statusDisplay = myLearningModel.get(position).getStatusDisplay();

        Log.d(TAG, "getView: statusFromModel" + statusFromModel);

        if (statusFromModel.equalsIgnoreCase("Completed") || (statusFromModel.toLowerCase().contains("passed") || statusFromModel.toLowerCase().contains("failed")) || statusFromModel.equalsIgnoreCase("completed")) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusCompleted)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
            File myFile = new File(myLearningModel.get(position).getOfflinepath());
            if (myFile.exists()) {
                if (statusFromModel.toLowerCase().equalsIgnoreCase("completed")) {
                    statusDisplay = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel);
                } else if (statusFromModel.equalsIgnoreCase("failed")) {
                    statusDisplay = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel_failed);
                } else if (statusFromModel.equalsIgnoreCase("passed")) {
                    statusDisplay = getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel_passed);
                }

            }
            progressPercentage = 100;
        } else if (statusFromModel.equalsIgnoreCase("Not Started") || statusFromModel.length() == 0) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusNotStarted)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
            progressPercentage = 0;
        } else if (statusFromModel.equalsIgnoreCase("incomplete") || (statusFromModel.toLowerCase().contains("inprogress")) || (statusFromModel.toLowerCase().contains("in progress"))) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusInProgress)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusInProgress));
            progressPercentage = 50;
        } else if (statusFromModel.equalsIgnoreCase("pending review") || (statusFromModel.toLowerCase().contains("pendingreview")) || (statusFromModel.toLowerCase().contains("grade"))) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
            progressPercentage = 100;
        } else if (statusFromModel.equalsIgnoreCase("Registered") || (statusFromModel.toLowerCase().contains("registered"))) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
            if (myLearningModel.get(position).getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
                holder.txtCourseStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
            }
            progressPercentage = -1;
        } else if (statusFromModel.toLowerCase().contains("attended") || statusFromModel.toLowerCase().contains("Expired") || statusFromModel.toLowerCase().contains("waitlisted")) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
            progressPercentage = -1;
        } else {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
            progressPercentage = 0;
        }

        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
            if (myLearningModel.get(position).getEventScheduleType() == 1 && uiSettingsModel.isEnableMultipleInstancesforEvent()) {
                holder.txtCourseStatus.setText(getLocalizationValue(JsonLocalekeys.details_label_tobeshedule));
            } else if (myLearningModel.get(position).getBit4()) {
                holder.txtCourseStatus.setText(getLocalizationValue(JsonLocalekeys.mylearningcanceled_labletitle_canceledlbltitle));
                holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
                holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
                progressPercentage = 100;
            } else {
                holder.txtCourseStatus.setText(statusDisplay);
            }
        } else {
            if (isValidString(myLearningModel.get(position).getPercentCompleted())) {

                if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true")) {
                    holder.txtCourseStatus.setText(statusDisplay + "(" + myLearningModel.get(position).getPercentCompleted() + "%)");
                } else {
                    holder.txtCourseStatus.setText(statusDisplay);
                }

                try {
                    if (isValidString(myLearningModel.get(position).getPercentCompleted()) && myLearningModel.get(position).getPercentCompleted().contains(".")) {
                        float i = Float.valueOf(myLearningModel.get(position).getPercentCompleted());
                        int progressValue = (int) i;
                        holder.progressBar.setProgress(progressValue);
                        progressPercentage = progressValue;
                    } else {
                        holder.progressBar.setProgress(Integer.parseInt(myLearningModel.get(position).getPercentCompleted()));
                        progressPercentage = Integer.parseInt(myLearningModel.get(position).getPercentCompleted());
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    holder.progressBar.setProgress(progressPercentage);
                }

            } else {

                if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true")) {
                    holder.txtCourseStatus.setText(statusDisplay + "(" + progressPercentage + "%)");
                } else {
                    holder.txtCourseStatus.setText(statusDisplay);
                }

                holder.progressBar.setProgress(progressPercentage);
            }
        }

        String imgUrl = myLearningModel.get(position).getImageData();


        Glide.with(vi.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);

        String thumbUrl = myLearningModel.get(position).getSiteURL() + "/Content/SiteFiles/ContentTypeIcons/" + myLearningModel.get(position).getContentTypeImagePath();

        if (isIconEnabled)
            holder.fabbtnthumb.setVisibility(View.VISIBLE);
        else
            holder.fabbtnthumb.setVisibility(View.GONE);

        if (isIconEnabled)
            Glide.with(vi.getContext()).
                    load(thumbUrl.trim()).
                    into(holder.fabbtnthumb);

        holder.fabbtnthumb.setBackgroundTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorWhite)));

//        holder.fabbtnthumb.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));

        if (myLearningModel.get(position).getIsRequired() == 1) {
            holder.lbRequired.setVisibility(View.VISIBLE);
        } else {
            holder.lbRequired.setVisibility(View.INVISIBLE);
        }

        final float oldRating = ratingValue;


        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // TODO Auto-generated method stub

                if (fromUser) {
                    try {
                        getUserRatingsOfTheContent(position, rating);

                        new CountDownTimer(500, 1000) {
                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                holder.ratingBar.setRating(oldRating);
                            }
                        }.start();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (isWaitListedContent) {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        }

        // do something for phones running an SDK before lollipop

// uncomment for crop life

        if (vi.getResources().getString(R.string.app_name).equalsIgnoreCase(vi.getResources().getString(R.string.crop_life))) {

            String isViewd = preferencesManager.getStringValue(StaticValues.KEY_HIDE_ANNOTATION);
            if (position == 0 && isViewd.equalsIgnoreCase("false")) {

                ViewTooltip
                        .on(holder.btnDownload)
                        .autoHide(true, 5000)
                        .corner(30)
                        .position(ViewTooltip.Position.LEFT).onHide(new ViewTooltip.ListenerHide() {
                    @Override
                    public void onHide(View view) {
                        appcontroller.setAlreadyViewd(true);
                        preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
                    }
                }).text(getLocalizationValue(JsonLocalekeys.mylearning_label_clicktodownloadlabel)).clickToHide(true)
                        .show();

                ViewTooltip
                        .on(holder.btnContextMenu)
                        .autoHide(true, 5000)
                        .corner(30)
                        .position(ViewTooltip.Position.BOTTOM).clickToHide(true)
                        .text(" " + getLocalizationValue(JsonLocalekeys.mylearning_label_clickformoreopetionlabel)).onHide(new ViewTooltip.ListenerHide() {
                    @Override
                    public void onHide(View view) {
                        appcontroller.setAlreadyViewd(true);
                        preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
                    }
                })
                        .show();

                ViewTooltip
                        .on(holder.imgThumb)
                        .autoHide(true, 5000)
                        .corner(30)
                        .position(ViewTooltip.Position.BOTTOM)
                        .clickToHide(true)
                        .animation(new ViewTooltip.FadeTooltipAnimation(500))
                        .text(" " + getLocalizationValue(JsonLocalekeys.mylearning_label_clickonimagelabel)).onHide(new ViewTooltip.ListenerHide() {
                    @Override
                    public void onHide(View view) {
                        appcontroller.setAlreadyViewd(true);
                        preferencesManager.setStringValue("true", StaticValues.KEY_HIDE_ANNOTATION);
                    }
                })
                        .show();

            }
        }


        return vi;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        myLearningModel.clear();
        if (charText.length() == 0) {
            myLearningModel.addAll(searchList);
        } else {
            for (MyLearningModel s : searchList) {
                if (s.getCourseName().toLowerCase(Locale.getDefault()).contains(charText) || s.getAuthor().toLowerCase(Locale.getDefault()).contains(charText) || s.getMediaName().toLowerCase(Locale.getDefault()).contains(charText) || s.getShortDes().toLowerCase(Locale.getDefault()).contains(charText) || s.getKeywords().toLowerCase(Locale.getDefault()).contains(charText) || s.getPresenter().toLowerCase(Locale.getDefault()).contains(charText)) {
                    myLearningModel.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }


    class ViewHolder {
        public int getPosition;
        public MyLearningModel myLearningDetalData;
        public ViewGroup parent;
        public MylearningInterface mylearningInterface;
        public SetCompleteListner setCompleteListner;
        public DownloadStart downloadStart;

        @Nullable
        @BindView(R.id.txtgroupName)
        TextView txtgroupName;

        @Nullable
        @BindView(R.id.txt_title_name)
        TextView txtTitle;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtShortDesc)
        TextView txtShortDisc;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txt_coursename)
        TextView txtCourseName;

        @Nullable
        @BindView(R.id.rat_adapt_ratingbar)
        RatingBar ratingBar;

        @Nullable
        @BindView(R.id.txt_course_progress)
        TextView txtCourseStatus;

        @Nullable
        @BindView(R.id.course_progress_bar)
        ProgressBar progressBar;

        @Nullable
        @BindView(R.id.txt_author)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.txt_site_name)
        TextView txtSiteName;

        @Nullable
        @BindView(R.id.consolidateline)
        View consolidateLine;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.btntxt_download)
        TextView btnDownload;

        // event labels

        @Nullable
        @BindView(R.id.txteventicon)
        TextView txtEvntIcon;

        @Nullable
        @BindView(R.id.txtathricon)
        TextView txtAthrIcon;

        @Nullable
        @BindView(R.id.txtlocationicon)
        TextView txtLocationIcon;

        @Nullable
        @BindView(R.id.txt_eventfromtotime)
        TextView txtEventFromTo;

        @Nullable
        @BindView(R.id.txt_timezone)
        TextView txtTimeZone;

        @Nullable
        @BindView(R.id.txt_eventlocation)
        TextView txtEventLocation;

        @Nullable
        @BindView(R.id.txtWriteReview)
        TextView txtWriteReview;

        @Nullable
        @BindView(R.id.txtEnrollShedule)
        TextView txtEnrollShedule;

        @Nullable
        @BindView(R.id.eventlayout)
        LinearLayout eventLayout;

        @Nullable
        @BindView(R.id.locationlayout)
        LinearLayout locationlayout;

        @Nullable
        @BindView(R.id.author_site_layout)
        LinearLayout authorLayout;

        @Nullable
        @BindView(R.id.fabbtnthumb)
        FloatingActionButton fabbtnthumb;

        @Nullable
        @BindView(R.id.lbRequired)
        TextView lbRequired;

        // credits layout

        @Nullable
        @BindView(R.id.creditsLayout)
        LinearLayout creditsLayout;

        @Nullable
        @BindView(R.id.lbCredits)
        TextView lbCredits;

        @Nullable
        @BindView(R.id.txtCredits)
        TextView txtCredits;

        @Nullable
        @BindView(R.id.circle_progress)
        CircleProgressBar circleProgressBar;

        public ViewHolder(final View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btntxt_download), iconFont);

            FontManager.markAsIconContainer(view.findViewById(R.id.txtathricon), iconFont);
            FontManager.markAsIconContainer(view.findViewById(R.id.txteventicon), iconFont);
            FontManager.markAsIconContainer(view.findViewById(R.id.txtlocationicon), iconFont);


            mylearningInterface = new MylearningInterface() {
                @Override
                public void deletedTheContent(int updateProgress) {
                    notifyDataSetChanged();

                }

                @Override
                public void cancelEnrollment(boolean isCancel) {
                    Log.d(TAG, "cancelEnrollment:  in adapter method ");
                    eventInterface.cancelEnrollment(myLearningDetalData, isCancel);
                    notifyDataSetChanged();
                }

                @Override
                public void addToArchive(boolean added) {
                    Log.d(TAG, "addToArchive:  in adapter method ");
                    try {
                        addToArchiveApiCall(getPosition, added);
                    } catch (JSONException e) {
                        e.printStackTrace();//
                    }
                }

                @Override
                public void removeFromMylearning(boolean isRemoved) {

                    Toast.makeText(activity, "Removed ", Toast.LENGTH_SHORT).show();

                    eventInterface.removedFromMylearning(myLearningDetalData);
                }

                @Override
                public void resheduleTheEvent(boolean isReshedule) {

                    if (myLearningDetalData.getEventScheduleType() == 2 && myLearningDetalData.isEnrollFutureInstance()) {
                        dialogForFutureInstances(myLearningDetalData);
                    } else {
                        gotoEventSheduleTab(myLearningDetalData);
                    }
                }

                @Override
                public void badCancelEnrollment(boolean cancelIt) {

                    eventInterface.badCancelEnrollment(myLearningDetalData, cancelIt);
                    notifyDataSetChanged();

                }

                @Override
                public void viewCertificateLink(boolean viewIt) {

                    eventInterface.viewCertificateLink(myLearningDetalData);
                }
            };

            setCompleteListner = new SetCompleteListner() {
                @Override
                public void completedStatus() {
                    myLearningDetalData.setStatusActual("Completed");
                    myLearningDetalData.setProgress("100");
                    myLearningDetalData.setPercentCompleted("100");
                    myLearningDetalData.setStatusDisplay(" " + getLocalizationValue(JsonLocalekeys.mylearning_label_completedlabel));
                    notifyDataSetChanged();
                }
            };

            downloadStart = new DownloadStart() {
                @Override
                public void downloadTheContent() {
                    btnDownload.performClick();
                }
            };
        }

        @OnClick({R.id.btntxt_download, R.id.btn_contextmenu, R.id.imagethumb, R.id.txt_title_name, R.id.fabbtnthumb, R.id.txtEnrollShedule, R.id.txtWriteReview})
        public void actionsForMenu(View view) {

            if (view.getId() == R.id.btn_contextmenu) {
                GlobalMethods.myLearningContextMenuMethod(view, getPosition, btnContextMenu, myLearningDetalData, mylearningInterface, setCompleteListner, "", isReportEnabled, downloadStart, uiSettingsModel, sideMenusModel, isIconEnabled);
            } else if (view.getId() == R.id.txtWriteReview) {
                try {

                    if (isNetworkConnectionAvailable(activity, -1)) {

                        float ratingValue = 0;
                        try {
                            ratingValue = Float.parseFloat(myLearningModel.get(getPosition).getRatingId());
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            ratingValue = 0;
                        }
                        getUserRatingsOfTheContent(getPosition, ratingValue);
                    } else {

                        Toast.makeText(activity, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70") && uiSettingsModel.isEnableMultipleInstancesforEvent() && myLearningDetalData.getEventScheduleType() == 1) {
                    ((ListView) parent).performItemClick(view, getPosition, 0);
                } else {
                    ((ListView) parent).performItemClick(view, getPosition, 0);
                }
            }
        }
    }

    public void dialogForFutureInstances(final MyLearningModel learningModel) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getLocalizationValue(JsonLocalekeys.mylearning_enrollalertsubttitle_subtitle)).setTitle(getLocalizationValue(JsonLocalekeys.details_label_enroll))
                .setCancelable(false).setNegativeButton(getLocalizationValue(JsonLocalekeys.mylearning_alertbutton_cancelbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).setPositiveButton(getLocalizationValue(JsonLocalekeys.details_label_enroll), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do things
                dialog.dismiss();
                gotoEventSheduleTab(learningModel);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    public void gotoEventSheduleTab(MyLearningModel learningModel) {

        Intent intentDetail = new Intent(activity, MyLearningDetailActivity1.class);
//                    myLearningDetalData.setContentID(myLearningDetalData.getReSheduleEvent());
        intentDetail.putExtra("IFROMCATALOG", false);
        intentDetail.putExtra("sideMenusModel", sideMenusModel);
        intentDetail.putExtra("myLearningDetalData", learningModel);
        intentDetail.putExtra("typeFrom", "tab");
        intentDetail.putExtra("reschdule", true);
        activity.startActivityForResult(intentDetail, DETAIL_CLOSE_CODE);

    }


    public void getUserRatingsOfTheContent(final int position, final float rating) throws JSONException {

        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", myLearningModel.get(position).getContentID());
        parameters.put("Locale", preferencesManager.getLocalizationStringValue(activity.getResources().getString(R.string.locale_name)));
        parameters.put("metadata", "0");
        parameters.put("intUserID", appUserModel.getUserIDValue());
        parameters.put("CartID", "");
        parameters.put("iCMS", "0");
        parameters.put("ComponentID", "3");
        parameters.put("SiteID", appUserModel.getSiteIDValue());
        parameters.put("DetailsCompID", "107");
        parameters.put("DetailsCompInsID", "3291");
        parameters.put("ERitems", "false");
        parameters.put("SkippedRows", 0);
        parameters.put("NoofRows", 3);

        final String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/GetUserRatings";

        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //  svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
//                initilizeRatingsListView();
                if (s != null) {
                    try {
                        mapReviewRating(s, position, rating);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                //    svProgressHUD.dismiss();
            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return parameterString.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(activity);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void mapReviewRating(String response, int position, float rating) throws
            JSONException {

        JSONObject jsonObject = new JSONObject(response);
        JSONObject editObj = null;
        if (jsonObject.has("EditRating")) {

            boolean isEditReview = false;
            if (jsonObject.isNull("EditRating")) {

                isEditReview = false;

            } else {

                isEditReview = true;
                editObj = jsonObject.getJSONObject("EditRating");
            }

            Intent intentReview = new Intent(activity, WriteReviewAcitiviy.class);
            intentReview.putExtra("myLearningDetalData", myLearningModel.get(position));
            intentReview.putExtra("isEditReview", isEditReview);
            if (isEditReview) {
                intentReview.putExtra("editObj", (Serializable) editObj.toString());
            }

            intentReview.putExtra("ratednow", rating);
            intentReview.putExtra("from", true);
            activity.startActivityForResult(intentReview, REVIEW_REFRESH);
        }
    }

    public void addToArchiveApiCall(int position, boolean isArchived) throws
            JSONException {
        int archV = 0;
        if (isArchived) {
            archV = 1;
        } else {
            archV = 0;
        }

        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", myLearningModel.get(position).getContentID());
        parameters.put("IsArchived", archV);
        parameters.put("UserID", appUserModel.getUserIDValue());

        final String parameterString = parameters.toString();

        String urlString = appUserModel.getWebAPIUrl() + "/catalog/UpdateMyLearningArchive";

        final int finalArchV = archV;
        final StringRequest request = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                // svProgressHUD.dismiss();
                Log.d(TAG, "onResponse: " + s);
                if (finalArchV == 1) {
                    Toast.makeText(activity, " " + getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_archivedsuccesfully), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activity, " " + getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_unarchivedsuccesfully), Toast.LENGTH_SHORT).show();

                }
                eventInterface.archiveAndUnarchive(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                // svProgressHUD.dismiss();
            }
        })

        {
            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() {
                return parameterString.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json");
//                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(activity);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private String getLocalizationValue(String key) {
        Log.d(TAG, "getLocalizationValue: archived" + JsonLocalization.getInstance().getStringForKey(key, activity));
        return JsonLocalization.getInstance().getStringForKey(key, activity);
    }
}


