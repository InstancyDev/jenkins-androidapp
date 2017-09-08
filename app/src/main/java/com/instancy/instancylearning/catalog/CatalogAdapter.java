package com.instancy.instancylearning.catalog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class CatalogAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<MyLearningModel> myLearningModel = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = CatalogAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<MyLearningModel> searchList;
    AppController appcontroller;


    public CatalogAdapter(Activity activity, int resource, List<MyLearningModel> myLearningModel) {
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final ViewHolder holder;
        View vi = convertView;
        if (convertView == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(R.layout.catalog_cell, null);
        holder = new ViewHolder(vi);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.myLearningDetalData = myLearningModel.get(position);
        holder.txtTitle.setText(myLearningModel.get(position).getCourseName());
        holder.txtCourseName.setText(myLearningModel.get(position).getMediaName());
        holder.txtAuthor.setText("By " + myLearningModel.get(position).getAuthor());
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

        // apply colors

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.btnDownload.setTag(position);
//        initVolleyCallback(myLearningModel.get(position), position);

        if (myLearningModel.get(position).getShortDes().isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }
        if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {

            holder.circleProgressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
//            holder.btnPreview.setVisibility(View.GONE);

        } else {
            if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("688")) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.circleProgressBar.setVisibility(View.GONE);

            } else {
                holder.btnDownload.setVisibility(View.VISIBLE);
                holder.btnDownload.setVisibility(View.GONE);
                holder.circleProgressBar.setVisibility(View.GONE);

//              File extStore = Environment.getExternalStorageDirectory();
                File myFile = new File(myLearningModel.get(position).getOfflinepath());

                if (myFile.exists()) {
                    holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
                    holder.btnDownload.setEnabled(false);

                } else {
                    holder.btnDownload.setTextColor(vi.getResources().getColor(R.color.colorBlack));
                    holder.btnDownload.setEnabled(true);
                    holder.btnDownload.setVisibility(View.GONE);

                }
            }

            String imgUrl = myLearningModel.get(position).getImageData();
            Glide.with(vi.getContext()).load(imgUrl)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.cellimage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgThumb);
            final float oldRating = ratingValue;
            holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    // TODO Auto-generated method stub

                    if (fromUser) {
                        int ratingInt = Math.round(rating);
                        svProgressHUD.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
                        String paramsString = appUserModel.getWebAPIUrl() +
                                ApiConstants.UPDATERATINGURL + "UserID=" + appUserModel.getUserIDValue() +
                                "&ContentID=" + myLearningModel.get(position).getContentID()
                                + "&Title=" +
                                "&Description=From%20Android%20Native%20App" +
                                "&RatingID=" + ratingInt;
                        if (isNetworkConnectionAvailable(activity, -1)) {
                            try {

                                Log.d(TAG, "getJsonObjResponseVolley: " + paramsString);
                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, paramsString, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Log.d("logr  response =", "response " + response.get("table1"));
                                            JSONArray jsonArray = response.getJSONArray("table1");
                                            String status = jsonArray.getJSONObject(0).get("status").toString();
                                            String rating = jsonArray.getJSONObject(0).get("rating").toString();
                                            if (status.contains("Success")) {
                                                db.updateContentRatingToLocalDB(myLearningModel.get(position), rating);
                                                Toast.makeText(
                                                        activity,
                                                        activity.getString(R.string.rating_update_success),
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                myLearningModel.get(position).setRatingId(rating);
//                                        notifyDataSetChanged();
                                            } else {
                                                Toast.makeText(
                                                        activity,
                                                        activity.getString(R.string.rating_update_fail),
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                holder.ratingBar.setRating(oldRating);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            holder.ratingBar.setRating(oldRating);
                                        }
                                        svProgressHUD.dismiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        holder.ratingBar.setRating(oldRating);
                                        svProgressHUD.dismiss();
                                        Toast.makeText(
                                                activity,
                                                activity.getString(R.string.rating_update_fail),
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        final Map<String, String> headers = new HashMap<>();
                                        String base64EncodedCredentials = Base64.encodeToString(String.format(appUserModel.getAuthHeaders()).getBytes(), Base64.NO_WRAP);
                                        headers.put("Authorization", "Basic " + base64EncodedCredentials);
                                        return headers;
                                    }
                                };
//                        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                                0,
//                                -1,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleySingleton.getInstance(activity).addToRequestQueue(jsonObjReq);

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(activity, "No internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            // do something for phones running an SDK before lollipop


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
                if (s.getCourseName().toLowerCase(Locale.getDefault()).contains(charText) || s.getAuthor().toLowerCase(Locale.getDefault()).contains(charText)) {
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
        @Nullable
        @Bind(R.id.txt_title_name)
        TextView txtTitle;

        @Nullable
        @Bind(R.id.card_view)
        CardView card_view;

        @Nullable
        @Bind(R.id.txtShortDesc)
        TextView txtShortDisc;

        @Nullable
        @Bind(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @Bind(R.id.txt_coursename)
        TextView txtCourseName;

        @Nullable
        @Bind(R.id.rat_adapt_ratingbar)
        RatingBar ratingBar;


        @Nullable
        @Bind(R.id.txt_author)
        TextView txtAuthor;

        @Nullable
        @Bind(R.id.txt_site_name)
        TextView txtSiteName;

        @Nullable
        @Bind(R.id.consolidateline)
        View consolidateLine;

        @Nullable
        @Bind(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @Bind(R.id.btntxt_download)
        TextView btnDownload;

        @Nullable
        @Bind(R.id.circle_progress)
        CircleProgressBar circleProgressBar;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btntxt_download), iconFont);
            downloadInterface = new DownloadInterface() {
                @Override
                public void deletedTheContent(int updateProgress) {
                    notifyDataSetChanged();
                    deleteMetaDataFromDB(myLearningDetalData);
                }
            };
        }

        @OnClick({R.id.btntxt_download, R.id.btn_contextmenu, R.id.imagethumb})
        public void actionsForMenu(View view) {

            if (view.getId() == R.id.btn_contextmenu) {

                GlobalMethods.contextMenuMethod(view, getPosition, btnContextMenu, myLearningDetalData, downloadInterface);
            } else {
                ((ListView) parent).performItemClick(view, getPosition, 0);
            }

        }
    }

    private void deleteMetaDataFromDB(MyLearningModel learningModel) {

    }


}


