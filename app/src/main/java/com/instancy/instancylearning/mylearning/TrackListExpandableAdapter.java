package com.instancy.instancylearning.mylearning;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.UnZip;
import com.instancy.instancylearning.helper.VolleySingleton;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.interfaces.DownloadInterface;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.synchtasks.WebAPIClient;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.showToast;

/**
 * Created by Upendranath on 7/18/2017 Working on InstancyLearning.
 */

public class TrackListExpandableAdapter extends BaseExpandableListAdapter {

    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    private LayoutInflater inflater;
    private Activity activity;
    private List<String> _blockNames; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<MyLearningModel>> _trackList;
    private Context _context;
    WebAPIClient webAPIClient;
    VollyService vollyService;
    IResult resultCallback = null;
    ExpandableListView expandableListView;
    boolean isDownloading = false;

    public TrackListExpandableAdapter(Context context, List<String> blockNames, HashMap<String, List<MyLearningModel>> trackList, ExpandableListView expandableListView) {
        this._context = context;
        this._blockNames = blockNames;
        this._trackList = trackList;
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(_context);
        webAPIClient = new WebAPIClient(_context);
        vollyService = new VollyService(resultCallback, _context);
        db = new DatabaseHandler(context);
        this.expandableListView = expandableListView;
    }

    public void refreshList(List<String> blockNames, HashMap<String, List<MyLearningModel>> trackList) {
        this._trackList = trackList;
        this._blockNames = blockNames;
//        this.searchList = new ArrayList<MyLearningModel>();
//        this.searchList.addAll(myLearningModel);
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return this._blockNames != null ? _blockNames.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this._trackList.get(this._blockNames.get(groupPosition)) != null ? this._trackList.get(this._blockNames.get(groupPosition))
                .size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._blockNames.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this._trackList.get(this._blockNames.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View groupView = convertView;
        if (groupView == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            groupView = inflater.inflate(R.layout.tracklistgroup_item, parent, false);
            TextView blockNameTxt = (TextView) groupView.findViewById(R.id.txt_trackgroup_name);
            blockNameTxt.setText(_blockNames.get(groupPosition));

            if (_blockNames.get(groupPosition).equalsIgnoreCase("")) {
                groupView.setVisibility(View.GONE);
                blockNameTxt.setVisibility(View.GONE);
            }
        }
        return groupView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MyLearningModel trackChildList = (MyLearningModel) getChild(groupPosition, childPosition);
        View childView = convertView;
        if (childView == null)
            inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        childView = inflater.inflate(R.layout.tracklistchilditem, parent, false);

        holder = new ViewHolder(childView);
        holder.parent = parent;
        holder.relativeLayout.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.getChildPosition = childPosition;
        holder.getGroupPosition = groupPosition;
        holder.myLearningDetalData = trackChildList;

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDisc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtTitle.setText(trackChildList.getCourseName());
        holder.txtCourseName.setText(trackChildList.getMediaName());
        holder.txtAuthor.setText("By " + trackChildList.getAuthor());
        holder.txtShortDisc.setText(trackChildList.getShortDes());

        if (trackChildList.getShortDes().isEmpty()) {
            holder.txtShortDisc.setVisibility(View.GONE);
        } else {
            holder.txtShortDisc.setVisibility(View.VISIBLE);
        }
        if (trackChildList.getObjecttypeId().equalsIgnoreCase("70")) {
            holder.circleProgressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.txtCourseStatus.setVisibility(View.GONE);
//            holder.btnPreview.setVisibility(View.GONE);

        } else {
            if (trackChildList.getObjecttypeId().equalsIgnoreCase("10") && trackChildList.getIsListView().equalsIgnoreCase("true") || trackChildList.getObjecttypeId().equalsIgnoreCase("28")) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.circleProgressBar.setVisibility(View.GONE);
            } else {

                File myFile = new File(trackChildList.getOfflinepath());

                if (myFile.exists()) {
                    holder.btnDownload.setTextColor(childView.getResources().getColor(R.color.colorStatusCompleted));
                    holder.btnDownload.setEnabled(false);

                } else {

                    holder.btnDownload.setTextColor(childView.getResources().getColor(R.color.colorBlack));
                    holder.btnDownload.setEnabled(true);
                }

                holder.btnDownload.setVisibility(View.VISIBLE);
            }
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.txtCourseStatus.setVisibility(View.VISIBLE);
            String courseStatus = "";
            if (trackChildList.getStatus().equalsIgnoreCase("Completed") || trackChildList.getStatus().toLowerCase().contains("passed") || trackChildList.getStatus().toLowerCase().contains("failed")) {
                String progressPercent = "100";
                String statusValue = trackChildList.getStatus();
                if (trackChildList.getStatus().equalsIgnoreCase("Completed")) {
                    statusValue = "Completed";

                } else if (trackChildList.getStatus().equalsIgnoreCase("failed")) {

                    statusValue = "Completed(failed)";
                } else if (trackChildList.getStatus().equalsIgnoreCase("passed")) {

                    statusValue = "Completed(passed)";

                }

                holder.progressBar.setProgressTintList(ColorStateList.valueOf(childView.getResources().getColor(R.color.colorStatusCompleted)));
                holder.progressBar.setProgress(Integer.parseInt(progressPercent));
                holder.txtCourseStatus.setTextColor(childView.getResources().getColor(R.color.colorStatusCompleted));
//                courseStatus = trackChildList.getStatus() + " (" + trackChildList.getProgress();
                courseStatus = statusValue + " (" + progressPercent;
            } else if (trackChildList.getStatus().equalsIgnoreCase("Not Started")) {

//                holder.progressBar.setBackgroundColor(vi.getResources().getColor(R.color.colorStatusNotStarted));
                holder.progressBar.setProgressTintList(ColorStateList.valueOf(childView.getResources().getColor(R.color.colorStatusNotStarted)));
                holder.progressBar.setProgress(0);
                holder.txtCourseStatus.setTextColor(childView.getResources().getColor(R.color.colorStatusNotStarted));
                courseStatus = trackChildList.getStatus() + "  (0";

            } else {
                String statusValue = "In Progress";

                if (trackChildList.getStatus().toLowerCase().equalsIgnoreCase("incomplete") || trackChildList.getStatus().equalsIgnoreCase("") || trackChildList.getStatus().toLowerCase().equalsIgnoreCase("in complete")) {
                    statusValue = "In Progress";
                }


                holder.progressBar.setProgressTintList(ColorStateList.valueOf(childView.getResources().getColor(R.color.colorStatusInProgress)));
                holder.progressBar.setProgress(Integer.parseInt(trackChildList.getProgress()));
                holder.txtCourseStatus.setTextColor(childView.getResources().getColor(R.color.colorStatusInProgress));
                courseStatus = statusValue + " (" + trackChildList.getProgress();

            }
            holder.txtCourseStatus.setText(courseStatus + "%)");
        }

        if (trackChildList.getShowStatus().equalsIgnoreCase("disabled")) {

            holder.btnDownload.setEnabled(false);
            childView.setBackgroundColor(childView.getResources().getColor(R.color.colorGray));
            holder.btnContextMenu.setEnabled(false);
            holder.imgThumb.setEnabled(false);
        } else if (trackChildList.getShowStatus().equalsIgnoreCase("hide")) {
            childView.setVisibility(View.GONE);
        } else {
            childView.setBackgroundColor(Color.WHITE);
            holder.btnDownload.setEnabled(true);
            holder.btnContextMenu.setEnabled(true);
            holder.imgThumb.setEnabled(true);
        }

        String imgUrl = trackChildList.getImageData();
        Glide.with(childView.getContext()).load(imgUrl)
                .thumbnail(0.5f)
                .crossFade()
                .placeholder(R.drawable.cellimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgThumb);
        childView.setTag("view");
        return childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    class ViewHolder {
        public int getChildPosition;
        public int getGroupPosition;
        public MyLearningModel myLearningDetalData;
        public ViewGroup parent;
        DownloadInterface downloadInterface;
        @Nullable
        @Bind(R.id.txt_title_name)
        TextView txtTitle;

        @Nullable
        @Bind(R.id.relative_layout)
        RelativeLayout relativeLayout;

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
        @Bind(R.id.txt_course_progress)
        TextView txtCourseStatus;

        @Nullable
        @Bind(R.id.course_progress_bar)
        ProgressBar progressBar;

        @Nullable
        @Bind(R.id.txt_author)
        TextView txtAuthor;

        @Nullable
        @Bind(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @Bind(R.id.btntxt_download)
        TextView btnDownload;

        @Nullable
        @Bind(R.id.circle_progress_track)
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
            };
        }

        @OnClick({R.id.btntxt_download, R.id.btn_contextmenu, R.id.imagethumb})
        public void actionsForMenu(View view) {

            if (view.getId() == R.id.btn_contextmenu) {

                GlobalMethods.contextMenuMethod(view, getChildPosition, btnContextMenu, myLearningDetalData, downloadInterface);

            } else if (view.getId() == R.id.imagethumb) {
                GlobalMethods.launchCourseViewFromGlobalClass(myLearningDetalData, view.getContext());
            } else {
//                ((ExpandableListView) parent).performItemClick(view, getChildPosition, getGroupPosition);
                if (isNetworkConnectionAvailable(_context, -1)) {
                    if (!isDownloading) {
                        downloadTheCourse(myLearningDetalData, view, getChildPosition, getGroupPosition);
                        btnDownload.setTextColor(view.getResources().getColor(R.color.colorStatusInProgress));
                        showToast(_context, "Download Started");

                    } else {
                        showToast(_context, "Download in progress");
                    }
                } else {
                    showToast(_context, "No Internet");
                }
            }
        }
    }

    public void downloadTheCourse(final MyLearningModel learningModel, final View view, final int position, final int Gposition) {

        String[] startPage = null;

        boolean isZipFile = false;

        String localizationFolder = "";

        if (learningModel.getStartPage().contains("/")) {
            startPage = learningModel.getStartPage().split("/");
            localizationFolder = "/" + startPage[0];
        } else {
            localizationFolder = "";
        }
        final String downloadDestFolderPath = view.getContext().getExternalFilesDir(null)
                + "/Mydownloads/Contentdownloads" + "/" + learningModel.getContentID() + localizationFolder;
        final String[] downloadSourcePath = {null};

        boolean success = (new File(downloadDestFolderPath)).mkdirs();

        switch (learningModel.getObjecttypeId()) {
            case "52":
                downloadSourcePath[0] = learningModel.getSiteURL() + "/content/sitefiles/"
                        + learningModel.getSiteID() + "/usercertificates/" + learningModel.getSiteID() + "/"
                        + learningModel.getContentID() + ".pdf";
                isZipFile = false;
                break;
            case "11":
            case "14":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getStartPage();
                isZipFile = false;
                break;
            case "8":
            case "9":
            case "10":
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                isZipFile = true;
                break;
            default:
                downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                        + learningModel.getContentID() + "/" + learningModel.getContentID()
                        + ".zip";
                isZipFile = true;
                break;
        }

        final boolean finalisZipFile = isZipFile;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode = 0;
                //code to do the HTTP request
                if (finalisZipFile) {

                    statusCode = webAPIClient.checkFileFoundOrNot(downloadSourcePath[0], appUserModel.getAuthHeaders());

                    if (statusCode != 200) {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/downloadfiles/"
                                + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], downloadDestFolderPath, learningModel, position, view, Gposition);

                    } else {
                        downloadSourcePath[0] = learningModel.getSiteURL() + "content/sitefiles/"
                                + learningModel.getContentID() + "/" + learningModel.getContentID() + ".zip";
                        downloadThin(downloadSourcePath[0], downloadDestFolderPath, learningModel, position, view, Gposition);

                    }
                } else {

                    downloadThin(downloadSourcePath[0], downloadDestFolderPath, learningModel, position, view, Gposition);
                }
//                int statusCode = vollyService.checkResponseCode(downloadSourcePath[0]);

            }
        });
        thread.start();

    }

    public void downloadThin(String downloadStruri, final String downloadPath, final MyLearningModel learningModel, final int position, final View view, final int gposition) {

        downloadStruri = downloadStruri.replace(" ", "%20");
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        Uri downloadUri = Uri.parse(downloadStruri);
        String extensionStr = "";
        switch (learningModel.getObjecttypeId()) {
            case "52":
            case "11":
            case "14":

                String[] startPage = null;
                if (learningModel.getStartPage().contains("/")) {
                    startPage = learningModel.getStartPage().split("/");
                    extensionStr = startPage[1];
                } else {
                    extensionStr = learningModel.getStartPage();
                }
                break;
            case "8":
            case "9":
            case "10":
                extensionStr = learningModel.getContentID() + ".zip";
                break;
            default:
                extensionStr = learningModel.getContentID() + ".zip";
                break;
        }

        final String finalDownloadedFilePath = downloadPath + "/" + extensionStr;

        final int id = 1;
        final NotificationManager mNotifyManager =
                (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(_context);
        mBuilder.setContentTitle("" + learningModel.getCourseName())
                .setContentText("" + learningModel.getAuthor())
                .setSmallIcon(R.mipmap.ic_launcher);

        final Uri destinationUri = Uri.parse(finalDownloadedFilePath);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new com.thin.downloadmanager.DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Log.d("TAG", "onDownloadComplete: ");
                        isDownloading = false;
                        if (finalDownloadedFilePath.contains(".zip")) {
                            String zipFile = finalDownloadedFilePath;
                            String unzipLocation = downloadPath;
                            UnZip d = new UnZip(zipFile,
                                    unzipLocation);
                            File zipfile = new File(zipFile);
                            zipfile.delete();
                        }

                        if (!learningModel.getStatus().equalsIgnoreCase("Not Started")) {
                            callMetaDataService(learningModel);

                        }
                        notifyDataSetChanged();
                        mBuilder.setContentText("Download completed")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        Log.d("TAG", "onDownloadFailed: " + +errorCode);
                        Toast.makeText(_context, "Download failed " + errorMessage, Toast.LENGTH_SHORT).show();
                        isDownloading = false;
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                        isDownloading = true;
//                        if (view != null) {
//                            updateStatus(position, progress, view, gposition);
//                        }


                        mBuilder.setProgress(100, progress, false);

                        // Displays the progress bar for the first time.
                        mNotifyManager.notify(id, mBuilder.build());

                    }

                });
        int downloadId = downloadManager.add(downloadRequest);
    }

    public void callMetaDataService(final MyLearningModel learningModel) {
        String paramsString = "_studid=" + learningModel.getUserID() + "&_scoid=" + learningModel.getScoId() + "&_SiteURL=" + learningModel.getSiteURL() + "&_contentId=" + learningModel.getContentID() + "&_trackId=";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, appUserModel.getWebAPIUrl() + "/MobileLMS/MobileGetContentTrackedData?" + paramsString, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        db.injectCMIDataInto(response, learningModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
        VolleySingleton.getInstance(_context).addToRequestQueue(jsonObjReq);

    }


    private void updateStatus(int index, int Status, View view, int Gposition) {

// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.

//        int positions = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
        int firstPosition = expandableListView.getFirstVisiblePosition() - expandableListView.getFlatListPosition(index); // This is the same as child #0
        int wantedChild = index - expandableListView.getFlatListPosition(firstPosition);
// Say, first visible position is 8, you want position 10, wantedChild will now be 2
// So that means your view is child #2 in the ViewGroup:

        View wantedView = expandableListView.getChildAt(wantedChild + 1);

        if (wantedView != null) {

            TextView txtBtnDownload = (TextView) wantedView.findViewById(R.id.btntxt_download);
            CircleProgressBar circleProgressBar = (CircleProgressBar) wantedView.findViewById(R.id.circle_progress_track);
            circleProgressBar.setVisibility(View.VISIBLE);
            txtBtnDownload.setVisibility(View.GONE);
            circleProgressBar.setProgress(Status);
            // Enabled Button View
            if (Status >= 100) {
                txtBtnDownload.setTextColor(view.getResources().getColor(R.color.colorStatusCompleted));
                txtBtnDownload.setVisibility(View.VISIBLE);
                circleProgressBar.setVisibility(View.GONE);
                txtBtnDownload.setEnabled(false);
            }
        }
    }

}
