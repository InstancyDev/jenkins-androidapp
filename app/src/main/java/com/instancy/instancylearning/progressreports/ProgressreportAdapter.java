package com.instancy.instancylearning.progressreports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
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

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.List;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;

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
            convertView = layoutInflater.inflate(R.layout.progressreportscell, null);//lbczzcw
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

        txtContentTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtSiteName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateStarted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtDateCompleted.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtScore.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        txtContextmenu.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

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

        final TextView txtContextmenu = (TextView) convertView
                .findViewById(R.id.txt_contextmenu);

        txtContentTitle.setText(progressReportModel.contenttitle);
        txtSiteName.setText(progressReportModel.orgname);
        txtContentType.setText(progressReportModel.contenttype);
        txtDateStarted.setText(progressReportModel.datestarted);
        txtDateCompleted.setText(progressReportModel.datecompleted);
        txtStatus.setText(progressReportModel.status);
        txtScore.setText(progressReportModel.overScore);

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


//        TextView sortAwasomeIcon = (TextView) convertView
//                .findViewById(R.id.sortawasome);
//
//        FontManager.markAsIconContainer(convertView.findViewById(R.id.sortawasome), iconFon);
//        if (groupPosition == 0) {
//            sortAwasomeIcon.setVisibility(View.VISIBLE);
//        } else {
//            sortAwasomeIcon.setVisibility(View.GONE);
//        }
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

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("View Certificate")) {


                }

                if (item.getTitle().toString().equalsIgnoreCase("Details")) {


                    if (isNetworkConnectionAvailable(context, -1)) {

                        if (isFromChild) {
                            (progressReportfragment).getMobileGetMobileContentMetaData(appUserModel.getSiteURL(), progressReportChildModel.objectID);
                        } else {
                            (progressReportfragment).getMobileGetMobileContentMetaData(appUserModel.getSiteURL(), progressReportModel.objectID);

                        }

                    } else {

                        Toast.makeText(context, context.getString(R.string.alert_headtext_no_internet), Toast.LENGTH_SHORT).show();
                    }


                }

                return true;
            }
        });
        popup.show();//showing popup menu
    }

}
