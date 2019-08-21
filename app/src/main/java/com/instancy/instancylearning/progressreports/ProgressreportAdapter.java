package com.instancy.instancylearning.progressreports;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.PopupMenu;
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
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.mainactivities.PdfViewer_Activity;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;
import static com.instancy.instancylearning.utils.Utilities.isValidString;
import static com.instancy.instancylearning.utils.Utilities.showToast;

/**
 * Created by Upendranath on 5/29/2017. used tutorial
 * http://www.journaldev.com/9942/android-expandablelistview-example-tutorial
 * http://thedeveloperworldisyours.com/android/notifydatasetchanged/
 */

public class ProgressReportAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<ProgressReportModel> progressReportModellist;
    ExpandableListView expandableListView;
    private UiSettingsModel uiSettingsModel;
    Typeface iconFon;
    ProgressReportfragment progressReportfragment;
    AppUserModel appUserModel;

    public ProgressReportAdapter(Context context, List<ProgressReportModel> progressReportModellist, ExpandableListView expandableListView, ProgressReportfragment progressReportfragment) {
        this.context = context;
        this.progressReportModellist = progressReportModellist;
        this.expandableListView = expandableListView;
        uiSettingsModel = UiSettingsModel.getInstance();
        iconFon = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        this.progressReportfragment = progressReportfragment;
        appUserModel = AppUserModel.getInstance();
    }


    public void refreshList(List<ProgressReportModel> progressReportModellist) {
        this.progressReportModellist = progressReportModellist;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return this.progressReportModellist != null ? progressReportModellist.size() : 0;

    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.progressReportModellist.get(groupPosition).progressReportChildModelList != null ? progressReportModellist.get(groupPosition).progressReportChildModelList.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.progressReportModellist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return "";
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {

        final ProgressReportModel listTitle = (ProgressReportModel) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.progressreportscell, null);//
        }

        CardView cardView = (CardView) convertView
                .findViewById(R.id.card_view);
        TextView expIcon = (TextView) convertView
                .findViewById(R.id.expIcon);

        if (isExpanded) {
            expIcon.setText(convertView.getResources().getString(R.string.fa_icon_angle_down));
        } else {
            expIcon.setText(convertView.getResources().getString(R.string.fa_icon_angle_right));

        }

        if (listTitle.progressReportChildModelList != null && listTitle.progressReportChildModelList.size() > 0) {
            cardView.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.colorGray));
            expIcon.setVisibility(View.VISIBLE);
            FontManager.markAsIconContainer(convertView.findViewById(R.id.expIcon), iconFon);
        } else {

            cardView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
            expIcon.setVisibility(View.GONE);
        }

        final TextView txtContextmenu = (TextView) convertView
                .findViewById(R.id.txt_contextmenu);

        FontManager.markAsIconContainer(convertView.findViewById(R.id.txt_contextmenu), iconFon);


        TextView txtContentTitle = (TextView) convertView
                .findViewById(R.id.txt_contenttitle);


        TextView txtSiteName = (TextView) convertView
                .findViewById(R.id.txt_sitename);

        TextView txtContentType = (TextView) convertView
                .findViewById(R.id.txt_content);

        TextView txtDateStarted = (TextView) convertView
                .findViewById(R.id.txt_datestarted);

        TextView txtDateCompleted = (TextView) convertView
                .findViewById(R.id.txt_datecompleted);

        TextView txtStatus = (TextView) convertView
                .findViewById(R.id.txt_status);


        // Title Labels Localazation Start
        TextView txtTitle = (TextView) convertView
                .findViewById(R.id.txtTitle);

        TextView titleSiteName = (TextView) convertView
                .findViewById(R.id.titleSiteName);

        TextView titleContentType = (TextView) convertView
                .findViewById(R.id.titleContentType);

        TextView titleDateStarted = (TextView) convertView
                .findViewById(R.id.titleDateStarted);

        TextView titleDateCompleted = (TextView) convertView
                .findViewById(R.id.titleDateCompleted);

        TextView titleStatus = (TextView) convertView
                .findViewById(R.id.titleStatus);

        TextView titleScore = (TextView) convertView
                .findViewById(R.id.titleScore);

        TextView lblCredits = (TextView) convertView
                .findViewById(R.id.lblCredits);

        TextView txtCredits = (TextView) convertView
                .findViewById(R.id.txtCredits);

        txtTitle.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contenttitlelabel) + " :");
        titleSiteName.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_sitenamelabel) + " :");
        titleContentType.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contentstypeslabel) + " :");
        titleDateStarted.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_datestartedlabel) + " :");
        titleDateCompleted.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_datecompletedlabel) + " :");
        titleStatus.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_statuslabel) + " :");
        titleScore.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_scorelabel) + " :");

        // Title Labels Localazation End

        TextView txtScore = (TextView) convertView
                .findViewById(R.id.txt_score);
        txtContentTitle.setTypeface(null, Typeface.BOLD);

        txtContentTitle.setText(listTitle.contenttitle);
        txtSiteName.setText(listTitle.orgname);
        txtContentType.setText(listTitle.contenttype);
        txtDateStarted.setText(listTitle.datestarted);
        txtDateCompleted.setText(listTitle.datecompleted);
        txtStatus.setText(listTitle.status);
        txtScore.setText(listTitle.overScore);

        if (isValidString(listTitle.credits)) {

            txtCredits.setText(listTitle.credits);

        }
        if (context.getResources().getString(R.string.app_name).equalsIgnoreCase(context.getResources().getString(R.string.app_esperanza))) {
            txtCredits.setVisibility(View.GONE);
            lblCredits.setVisibility(View.GONE);
        }

        txtContentTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtSiteName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateStarted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtContextmenu.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (isValidString(listTitle.certificateAction)) {
            txtContextmenu.setVisibility(View.VISIBLE);
        } else {
            txtContextmenu.setVisibility(View.GONE);
        }

        txtContextmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressReportChildModel progressReportChildModelModel = null;
                progresReportContextMenuMethod(txtContextmenu, listTitle, progressReportChildModelModel, false);

            }
        });


        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {

        final ProgressReportChildModel progressReportModel = progressReportModellist.get(groupPosition).progressReportChildModelList.get(childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.progressreportschildcell, null);
        }

        CardView cardView = (CardView) convertView
                .findViewById(R.id.card_view);


        TextView txtContentTitle = (TextView) convertView
                .findViewById(R.id.txt_contenttitle);

        TextView txtSiteName = (TextView) convertView
                .findViewById(R.id.txt_sitename);

        TextView txtContentType = (TextView) convertView
                .findViewById(R.id.txt_content);


        TextView txtDateStarted = (TextView) convertView
                .findViewById(R.id.txt_datestarted);


        TextView txtDateCompleted = (TextView) convertView
                .findViewById(R.id.txt_datecompleted);

        TextView txtStatus = (TextView) convertView
                .findViewById(R.id.txt_status);


        TextView txtScore = (TextView) convertView
                .findViewById(R.id.txt_score);


        // Title Labels Localazation Start
        TextView txtTitle = (TextView) convertView
                .findViewById(R.id.txtTitle);

        TextView titleSiteName = (TextView) convertView
                .findViewById(R.id.titleSiteName);

        TextView titleContentType = (TextView) convertView
                .findViewById(R.id.titleContentType);

        TextView titleDateStarted = (TextView) convertView
                .findViewById(R.id.titleDateStarted);

        TextView titleDateCompleted = (TextView) convertView
                .findViewById(R.id.titleDateCompleted);

        TextView titleStatus = (TextView) convertView
                .findViewById(R.id.titleStatus);

        TextView titleScore = (TextView) convertView
                .findViewById(R.id.titleScore);

        TextView lblCredits = (TextView) convertView
                .findViewById(R.id.lblCredits);

        TextView txtCredits = (TextView) convertView
                .findViewById(R.id.txtCredits);

        txtTitle.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contenttitlelabel) + " :");
        titleSiteName.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_sitenamelabel) + " :");
        titleContentType.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_contentstypeslabel) + " :");
        titleDateStarted.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_datestartedlabel) + " :");
        titleDateCompleted.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_datecompletedlabel) + " :");
        titleStatus.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_statuslabel) + " :");
        titleScore.setText(getLocalizationValue(JsonLocalekeys.myprogressreport_label_scorelabel) + " :");

        // Title Labels Localazation End

        final TextView txtContextmenu = (TextView) convertView
                .findViewById(R.id.txt_contextmenu);

        FontManager.markAsIconContainer(convertView.findViewById(R.id.txt_contextmenu), iconFon);

        txtContentTitle.setText(progressReportModel.contenttitle);
        txtSiteName.setText(progressReportModel.orgname);
        txtContentType.setText(progressReportModel.contenttype);
        txtDateStarted.setText(progressReportModel.datestarted);
        txtDateCompleted.setText(progressReportModel.datecompleted);
        txtStatus.setText(progressReportModel.status);
        txtScore.setText(progressReportModel.overScore);
        if (isValidString(progressReportModel.credits)) {

            txtCredits.setText(progressReportModel.credits);
        }

        if (context.getResources().getString(R.string.app_name).equalsIgnoreCase(context.getResources().getString(R.string.app_esperanza))) {
            txtCredits.setVisibility(View.GONE);
            lblCredits.setVisibility(View.GONE);
        }

        FontManager.markAsIconContainer(convertView.findViewById(R.id.txt_contextmenu), iconFon);

        final View finalConvertView = convertView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long packedPos = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
                int flatPos = expandableListView.getFlatListPosition(packedPos);

//Getting the ID for our child
                long id = expandableListView.getExpandableListAdapter().getChildId(groupPosition, childPosition);

                ((ExpandableListView) parent).performItemClick(finalConvertView, flatPos, id);

            }
        });

        txtContentTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtSiteName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateStarted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        cardView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        txtContextmenu.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        txtContextmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressReportModel progressReportModelNull = null;
                progresReportContextMenuMethod(txtContextmenu, progressReportModelNull, progressReportModel, true);

            }
        });

        if (isValidString(progressReportModel.certificateAction)) {
            txtContextmenu.setVisibility(View.VISIBLE);
        } else {
            txtContextmenu.setVisibility(View.GONE);
        }
        txtContentTitle.setTextColor(convertView.getResources().getColor(R.color.colorDarkGrey));
        return convertView;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;

    }

    public void progresReportContextMenuMethod(final TextView txtContextmenu, final ProgressReportModel progressReportModel, final ProgressReportChildModel progressReportChildModel, final boolean isFromChild) {

        PopupMenu popup = new PopupMenu(txtContextmenu.getContext(), txtContextmenu);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.progressreport_menu, popup.getMenu());
        //registering popup with OnMenuItemClickListene
        Menu menu = popup.getMenu();

        menu.getItem(1).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewcertificateoption));

        String certificationAction = "";
        String objectID = "";
        if (isFromChild) {
            certificationAction = progressReportChildModel.certificateAction;
            objectID = progressReportChildModel.objectID;
        } else {
            certificationAction = progressReportModel.certificateAction;
            objectID = progressReportModel.objectID;
        }

        final String finalCertificationAction = certificationAction;

        final String finalObjectID = objectID;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.ctx_viewcertificate) {
                    if (finalCertificationAction.equalsIgnoreCase("notearned")) {
                        if (isNetworkConnectionAvailable(txtContextmenu.getContext(), -1)) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(txtContextmenu.getContext());
                            builder.setMessage(getLocalizationValue(JsonLocalekeys.mylearning_alertsubtitle_forviewcertificate)).setTitle(getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewcertificateoption))
                                    .setCancelable(false)
                                    .setPositiveButton(getLocalizationValue(JsonLocalekeys.mylearning_closebuttonaction_closebuttonalerttitle), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            dialog.dismiss();

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        String[] contentIDnStartPage = getSplitString(finalCertificationAction);

                        if (contentIDnStartPage != null && contentIDnStartPage.length > 0) {
                            certificateAPICallForGenerate(contentIDnStartPage[0], contentIDnStartPage[1], finalObjectID);

                        }

                    }

                }
                if (item.getItemId() == R.id.ctx_details) {

                    if (isNetworkConnectionAvailable(context, -1)) {


                    } else {

                        Toast.makeText(context, getLocalizationValue(JsonLocalekeys.network_alerttitle_nointernet), Toast.LENGTH_SHORT).show();
                    }

                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void certificateAPICallForGenerate(String certificateID, String certificatePage, String contentID) {

        String urlString = appUserModel.getWebAPIUrl() + "/MobileLMS/MobiledownloadHTMLasPDF?UserID=" + appUserModel.getUserIDValue() + "&CID=" + contentID + "&CertID=" + certificateID + "&CertPage=" + certificatePage + "&SiteID=" + appUserModel.getSiteIDValue() + "&siteURL=" + appUserModel.getSiteURL() + "&height=400&width=900";

        final StringRequest request = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {


                String replaceString = s.replaceAll("^\"|\"$", "");

                String encodedStr = appUserModel.getSiteURL() + replaceString.replaceAll(" ", "%20");
                MyLearningModel learningModel = new MyLearningModel();
                learningModel.setCourseName("Certificate");
                if (isValidString(s) && s.contains(".pdf")) {
                    Intent pdfIntent = new Intent(context, PdfViewer_Activity.class);
                    pdfIntent.putExtra("PDF_URL", encodedStr);
                    pdfIntent.putExtra("ISONLINE", "YES");
                    pdfIntent.putExtra("ISCERTIFICATE", true);
                    pdfIntent.putExtra("PDF_FILENAME", getLocalizationValue(JsonLocalekeys.mylearning_actionsheet_viewcertificateoption));
                    pdfIntent.putExtra("myLearningDetalData", learningModel);
                    context.startActivity(pdfIntent);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Toast.makeText(context, getLocalizationValue(JsonLocalekeys.error_alertsubtitle_somethingwentwrong) + volleyError, Toast.LENGTH_LONG).show();

            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(context);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, context);
    }

    public String[] getSplitString(String parmString) {
        String[] contenIDnStartAry = null;

        if (!isValidString(parmString))
            return contenIDnStartAry;

        if (parmString.contains(":,")) {
            contenIDnStartAry = parmString.split(":,");
        }

        return contenIDnStartAry;
    }

}
