package com.instancy.instancylearning.catalog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
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
import android.widget.LinearLayout;
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
import com.dinuscxj.progressbar.CircleProgressBar;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.ApiConstants;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormat;
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
    boolean isEvent = false;


    public CatalogAdapter(Activity activity, int resource, List<MyLearningModel> myLearningModel, boolean isEvent) {
        this.activity = activity;
        this.myLearningModel = myLearningModel;
        this.searchList = new ArrayList<MyLearningModel>();
//        this.searchList.addAll(myLearningModel);
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        this.isEvent = isEvent;
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
        return myLearningModel != null ? myLearningModel.size() : 0;
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
//        vi = inflater.inflate(R.layout.catalogcellitem, null);
        vi = inflater.inflate(R.layout.catalogneventcellitem, null);
        holder = new ViewHolder(vi);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.myLearningDetalData = myLearningModel.get(position);
        holder.txtTitle.setText(myLearningModel.get(position).getCourseName());
        holder.txtCourseName.setText(myLearningModel.get(position).getMediaName());

        if (isEvent) {

            String fromDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventstartTime(), "yyyy-MM-dd hh:mm:ss");

//            String toDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventendTime(), "yyyy-MM-dd hh:mm:ss");

            holder.txtAuthor.setText(myLearningModel.get(position).getPresenter());
            holder.txtEventFromTo.setText(fromDate);
            holder.txtEventLocation.setText(myLearningModel.get(position).getLocationName());
            holder.txtTimeZone.setText(myLearningModel.get(position).getTimeZone());
            holder.txtTimeZone.setVisibility(View.GONE);
            if (myLearningModel.get(position).getTypeofevent() == 2) {
                holder.locationlayout.setVisibility(View.GONE);
            }
            holder.ratingBar.setVisibility(View.GONE);
            if (!myLearningModel.get(position).isCompletedEvent()) {
                holder.txtCourseName.setText(myLearningModel.get(position).getMediaName() + " | Available seats : " + myLearningModel.get(position).getAviliableSeats());
            }

        } else {
            holder.txtAuthor.setText(myLearningModel.get(position).getAuthor() + " ");
        }

        holder.txtShortDisc.setText(myLearningModel.get(position).getShortDes());

        if (myLearningModel.get(position).getSiteName().equalsIgnoreCase("")) {
            holder.consolidateLine.setVisibility(View.GONE);

        } else {
            holder.consolidateLine.setVisibility(View.VISIBLE);
        }

        if (myLearningModel.get(position).getViewType().equalsIgnoreCase("3")) {
            holder.txtPrice.setText("$" + myLearningModel.get(position).getPrice());
//            holder.txtPrice.setText(myLearningModel.get(position).getPrice() + " "+myLearningModel.get(position).getCurrency());
            holder.txtPrice.setVisibility(View.VISIBLE);
//            holder.txtPriceLabel.setVisibility(View.VISIBLE);
        } else {
            holder.txtPrice.setVisibility(View.GONE);
//            holder.txtPriceLabel.setVisibility(View.GONE);
            holder.txtPrice.setText("");
        }

        if (myLearningModel.get(position).getAddedToMylearning() == 1) {
            holder.txtPrice.setVisibility(View.GONE);

        }

        holder.txtSiteName.setText(" " + myLearningModel.get(position).getSiteName());
        float ratingValue = 0;
        try {
            ratingValue = Float.parseFloat(myLearningModel.get(position).getRatingId());
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            ratingValue = 0;
        }
        holder.ratingBar.setRating(ratingValue);

        holder.ratingBar.setIsIndicator(true);
        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(vi.getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);

        Drawable progress = holder.ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor(uiSettingsModel.getAppTextColor()));


        // apply colors

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtEventFromTo.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtEventLocation.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtTimeZone.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtAthrIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLocationIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtEvntIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        holder.btnDownload.setTag(position);


        String imgUrl = myLearningModel.get(position).getImageData();

        Picasso.with(vi.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);

//        initVolleyCallback(myLearningModel.get(position), position);

        if (myLearningModel.get(position).getShortDes().isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }

         if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("70")) {
            holder.circleProgressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
            holder.txtPrice.setVisibility(View.GONE);
            holder.eventLayout.setVisibility(View.VISIBLE);
            holder.txtAthrIcon.setVisibility(View.VISIBLE);

            holder.txtAuthor.setText(myLearningModel.get(position).getPresenter() + " ");
            String fromDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventstartTime(), "yyyy-MM-dd hh:mm:ss");
//            String toDate = convertToEventDisplayDateFormat(myLearningModel.get(position).getEventendTime(), "yyyy-MM-dd hh:mm:ss");
            holder.txtEventFromTo.setText(fromDate);
            holder.txtEventLocation.setText(myLearningModel.get(position).getLocationName());
            if (myLearningModel.get(position).getTypeofevent() == 2) {
                holder.locationlayout.setVisibility(View.GONE);
            }

             if (myLearningModel.get(position).getLocationName().length()==0) {
                 holder.locationlayout.setVisibility(View.GONE);
             }
//            holder.txtTimeZone.setText(myLearningModel.get(position).getTimeZone());
            holder.txtTimeZone.setVisibility(View.GONE);

        } else {


            holder.txtAuthor.setText(myLearningModel.get(position).getAuthor() + " ");
            holder.eventLayout.setVisibility(View.GONE);
            holder.txtAthrIcon.setVisibility(View.GONE);
            if (myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("10") && myLearningModel.get(position).getIsListView().equalsIgnoreCase("true") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("28") || myLearningModel.get(position).getObjecttypeId().equalsIgnoreCase("688") || uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
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
                    holder.btnDownload.setVisibility(View.GONE);

                }

                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
                    holder.btnDownload.setVisibility(View.GONE);
                }
                if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("2")) {
                    holder.btnDownload.setVisibility(View.VISIBLE);
//                    if (myLearningModel.get(position).getAddedToMylearning() == 0) {
//                        holder.btnDownload.setVisibility(View.GONE);
//                    }
                }

                holder.circleProgressBar.setVisibility(View.GONE);

//              File extStore = Environment.getExternalStorageDirectory();

            }

            // do something for phones running an SDK before lollipop


        }
        if (uiSettingsModel.getCatalogContentDownloadType().equalsIgnoreCase("0")) {
            holder.btnDownload.setVisibility(View.GONE);
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
            case "434":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.getPublishedDate().compareToIgnoreCase(obj2.getPublishedDate());

                        } else {
                            return obj2.getPublishedDate().compareToIgnoreCase(obj1.getPublishedDate());
                        }
                    }
                });
                break;
            case "221":
                Collections.sort(myLearningModel, new Comparator<MyLearningModel>() {

                    @Override
                    public int compare(MyLearningModel obj1, MyLearningModel obj2) {
                        // ## Ascending order

                        if (isAscn) {
                            return obj1.getObjecttypeId().compareToIgnoreCase(obj2.getObjecttypeId());

                        } else {
                            return obj2.getObjecttypeId().compareToIgnoreCase(obj1.getObjecttypeId());
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


    class ViewHolder {
        public int getPosition;
        public MyLearningModel myLearningDetalData;
        public ViewGroup parent;
        public DownloadInterface downloadInterface;
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
        @BindView(R.id.btn_price)
        TextView txtPrice;

        // added for events

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
        @BindView(R.id.eventlayout)
        LinearLayout eventLayout;

        @Nullable
        @BindView(R.id.locationlayout)
        LinearLayout locationlayout;


        @Nullable
        @BindView(R.id.circle_progress)
        CircleProgressBar circleProgressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btntxt_download), iconFont);
            FontManager.markAsIconContainer(view.findViewById(R.id.txtathricon), iconFont);
            FontManager.markAsIconContainer(view.findViewById(R.id.txteventicon), iconFont);
            FontManager.markAsIconContainer(view.findViewById(R.id.txtlocationicon), iconFont);
            downloadInterface = new DownloadInterface() {
                @Override
                public void deletedTheContent(int updateProgress) {
                    notifyDataSetChanged();
                }

                @Override
                public void cancelEnrollment(boolean cancelIt) {

                }

            };
        }

        @OnClick({R.id.btntxt_download, R.id.btn_contextmenu, R.id.imagethumb, R.id.card_view})
        public void actionsForMenu(View view) {


            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
}


