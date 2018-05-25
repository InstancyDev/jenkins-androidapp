package com.instancy.instancylearning.mylearning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.interfaces.EventInterface;
import com.instancy.instancylearning.interfaces.SetCompleteListner;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.ReviewRatingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.sidemenumodule.SideMenu;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;
import static com.instancy.instancylearning.utils.StaticValues.REVIEW_REFRESH;
import static com.instancy.instancylearning.utils.Utilities.convertDateToDayFormat;
import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = MyLearningAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<MyLearningModel> searchList;
    AppController appcontroller;
    EventInterface eventInterface;


    public MyLearningAdapter(Activity activity, int resource, List<MyLearningModel> myLearningModel, EventInterface eventInterface) {
        this.eventInterface = eventInterface;
        this.activity = activity;
        this.myLearningModel = myLearningModel;
        this.searchList = new ArrayList<MyLearningModel>();
        this.searchList.addAll(myLearningModel);
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
          /* setup enter and exit animation */
        appcontroller = AppController.getInstance();
    }

    public void refreshList(List<MyLearningModel> myLearningModel) {
        this.myLearningModel = myLearningModel;
        this.searchList = new ArrayList<MyLearningModel>();
        this.searchList.addAll(myLearningModel);
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
                            return obj1.getStatus().compareToIgnoreCase(obj2.getStatus());

                        } else {
                            return obj2.getStatus().compareToIgnoreCase(obj1.getStatus());
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


//    public String returnGroupID(String groupName) {
//
//        String groupID = "";
//
//        switch (groupName) {
//            case "Authors":
//                groupID = "authors";
//                break;
//            case "Job":
//                groupID = "job";
//                break;
//            case "Categories":
//                groupID = "categories";
//                break;
//            case "Skills":
//                groupID = "skills";
//                break;
//            case "ContentTypes":
//                groupID = "contenttypes";
//                break;
//
//        }
//
//        return groupID;
//    }


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
        } else {
            holder.txtAuthor.setText(myLearningModel.get(position).getAuthor() + " ");
        }


//        holder.btnContextMenu.getDrawable().setTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppTextColor())));

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
            holder.ratingBar.setVisibility(View.INVISIBLE);
        }

        // apply colors

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.btnDownload.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


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
        }
        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("688") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("36") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("102") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("27")) {
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


//              File extStore = Environment.getExternalStorageDirectory();
            File myFile = new File(myLearningModel.get(position).getOfflinepath());

            if (myFile.exists()) {
                holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
                holder.btnDownload.setEnabled(false);

            } else {
                holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorBlack));
                holder.btnDownload.setEnabled(true);

            }
        }

        holder.txtCourseStatus.setVisibility(View.VISIBLE);
        String courseStatus = "";
//            int progressPercentage = 1;
//
//            try {
//                progressPercentage = Integer.parseInt(myLearningModel.get(position).getProgress());
//            } catch (NumberFormatException ex) {
//                progressPercentage = 0;
//                ex.printStackTrace();
//            }

        String statusFromModel = myLearningModel.get(position).getStatus();

        Log.d(TAG, "getView: statusFromModel " + statusFromModel);

        if (statusFromModel.equalsIgnoreCase("Completed") || (statusFromModel.toLowerCase().contains("passed") || statusFromModel.toLowerCase().contains("failed")) || statusFromModel.equalsIgnoreCase("completed")) {

            if (statusFromModel.equalsIgnoreCase("Completed")) {
                statusFromModel = "Completed";

            } else if (statusFromModel.equalsIgnoreCase("failed")) {

                statusFromModel = "Completed(failed)";
            } else if (statusFromModel.equalsIgnoreCase("passed")) {

                statusFromModel = "Completed(passed)";

            }

            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusCompleted)));
            holder.progressBar.setProgress(100);
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
            courseStatus = statusFromModel + " (" + 100;
        } else if (statusFromModel.equalsIgnoreCase("Not Started")) {

            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusNotStarted)));
            holder.progressBar.setProgress(1);
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
            courseStatus = statusFromModel + "  (0";

        } else if (statusFromModel.equalsIgnoreCase("incomplete") || (statusFromModel.toLowerCase().contains("inprogress")) || (statusFromModel.toLowerCase().contains("in progress"))) {

            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusInProgress)));
            String status = "";

            if (statusFromModel.equalsIgnoreCase("incomplete")) {
                status = "In Progress ";
            } else if (statusFromModel.length() == 0) {
                status = "In Progress ";

            } else {
                status = statusFromModel;

            }
            holder.progressBar.setProgress(50);
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusInProgress));
            courseStatus = status + " (" + 50;

        } else if (statusFromModel.equalsIgnoreCase("pending review") || (statusFromModel.toLowerCase().contains("pendingreview")) || (statusFromModel.toLowerCase().contains("grade")) ) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
            String status = "Pending Review";

            holder.progressBar.setProgress(100);
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
            courseStatus = status + "(" + 100;
        } else if (statusFromModel.equalsIgnoreCase("Registered") || (statusFromModel.toLowerCase().contains("registered"))) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
            String status = "";

            status = statusFromModel;

            holder.progressBar.setProgress(100);
            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorGray));
            courseStatus = status;
        } else if (statusFromModel.toLowerCase().contains("attended") || (statusFromModel.toLowerCase().contains("registered"))) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = statusFromModel;


            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
            courseStatus = status;
        } else if (statusFromModel.toLowerCase().contains("Expired")) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorStatusOther)));
            String status = "";

            status = statusFromModel;

            holder.txtCourseStatus.setTextColor(vi.getResources().getColor(R.color.colorStatusOther));
            courseStatus = status;
        } else {

            holder.progressBar.setProgressTintList(ColorStateList.valueOf(vi.getResources().getColor(R.color.colorGray)));
            holder.progressBar.setProgress(0);
            String status = "";
            status = statusFromModel;
            courseStatus = status + "(" + 0;

        }
        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
            holder.txtCourseStatus.setText(courseStatus);
        } else {

            holder.txtCourseStatus.setText(courseStatus + "%)");
        }
        String imgUrl = myLearningModel.get(position).getImageData();

        Picasso.with(vi.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);

//        holder.btnContextMenu.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));

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
//                    int ratingInt = Math.round(rating);
//                    svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
//                    String paramsString = appUserModel.getWebAPIUrl() +
//                            ApiConstants.UPDATERATINGURL + "UserID=" + appUserModel.getUserIDValue() +
//                            "&ContentID=" + myLearningModel.get(position).getContentID()
//                            + "&Title=" +
//                            "&Description=From%20Android%20Native%20App" +
//                            "&RatingID=" + ratingInt;
//                    if (isNetworkConnectionAvailable(activity, -1)) {
//                        try {
//
//                            Log.d(TAG, "getJsonObjResponseVolley: " + paramsString);
//                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, paramsString, null, new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        Log.d("logr  response =", "response " + response.get("table1"));
//                                        JSONArray jsonArray = response.getJSONArray("table1");
//                                        String status = jsonArray.getJSONObject(0).get("status").toString();
//                                        String rating = jsonArray.getJSONObject(0).get("rating").toString();
//                                        if (status.contains("Success")) {
//                                            db.updateContentRatingToLocalDB(myLearningModel.get(position), rating);
//                                            Toast.makeText(
//                                                    activity,
//                                                    activity.getString(R.string.rating_update_success),
//                                                    Toast.LENGTH_SHORT)
//                                                    .show();
//                                            myLearningModel.get(position).setRatingId(rating);
////                                        notifyDataSetChanged();
//                                        } else {
//                                            Toast.makeText(
//                                                    activity,
//                                                    activity.getString(R.string.rating_update_fail),
//                                                    Toast.LENGTH_SHORT)
//                                                    .show();
//                                            holder.ratingBar.setRating(oldRating);
//                                        }
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                        holder.ratingBar.setRating(oldRating);
//                                    }
//                                    svProgressHUD.dismiss();
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    holder.ratingBar.setRating(oldRating);
//                                    svProgressHUD.dismiss();
//                                    Toast.makeText(
//                                            activity,
//                                            activity.getString(R.string.rating_update_fail),
//                                            Toast.LENGTH_SHORT)
//                                            .show();
//                                }
//                            }) {
//                                @Override
//                                public Map<String, String> getHeaders() throws AuthFailureError {
//                                    final Map<String, String> headers = new HashMap<>();
//                                    String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
//                                    headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                                    return headers;
//                                }
//                            };
////                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
////                                0,
////                                -1,
////                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                            VolleySingleton.getInstance(activity).addToRequestQueue(jsonObjReq);
//
//                        } catch (Exception e) {
//
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                        Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });

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
                })
                        .text("Click to download the content").clickToHide(true)
                        .show();

                ViewTooltip
                        .on(holder.btnContextMenu)
                        .autoHide(true, 5000)
                        .corner(30)
                        .position(ViewTooltip.Position.BOTTOM).clickToHide(true)
                        .text("Click for more options").onHide(new ViewTooltip.ListenerHide() {
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
                        .text("Click on image to view").onHide(new ViewTooltip.ListenerHide() {
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
                if (s.getCourseName().toLowerCase(Locale.getDefault()).contains(charText) || s.getAuthor().toLowerCase(Locale.getDefault()).contains(charText) || s.getMediaName().toLowerCase(Locale.getDefault()).contains(charText)) {
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
        public DownloadInterface downloadInterface;
        public SetCompleteListner setCompleteListner;


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

        @Nullable
        @BindView(R.id.circle_progress)
        CircleProgressBar circleProgressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btntxt_download), iconFont);
            downloadInterface = new DownloadInterface() {
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
            };

            setCompleteListner = new SetCompleteListner() {
                @Override
                public void completedStatus() {
                    myLearningDetalData.setStatus("Completed");
                    myLearningDetalData.setProgress("100");
                    notifyDataSetChanged();
                }
            };
        }

        @OnClick({R.id.btntxt_download, R.id.btn_contextmenu, R.id.imagethumb, R.id.txt_title_name})
        public void actionsForMenu(View view) {

            if (view.getId() == R.id.btn_contextmenu) {

                GlobalMethods.myLearningContextMenuMethod(view, getPosition, btnContextMenu, myLearningDetalData, downloadInterface, setCompleteListner, "");
            } else {
                if (!myLearningDetalData.getObjecttypeId().equalsIgnoreCase("70")) {
                    ((ListView) parent).performItemClick(view, getPosition, 0);
                }
            }
        }
    }

    public void getUserRatingsOfTheContent(final int position, final float rating) throws
            JSONException {

        JSONObject parameters = new JSONObject();
        parameters.put("ContentID", myLearningModel.get(position).getContentID());
        parameters.put("Locale", "en-us");
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
                svProgressHUD.dismiss();
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

                svProgressHUD.dismiss();
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
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

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


}


