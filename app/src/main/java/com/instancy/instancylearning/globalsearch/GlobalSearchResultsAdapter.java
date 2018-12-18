package com.instancy.instancylearning.globalsearch;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;

import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GlobalSearchResultModel;
import com.instancy.instancylearning.models.GlobalSearchResultModelNew;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.convertToEventDisplayDateFormat;
import static com.instancy.instancylearning.utils.Utilities.getFirstCaseWords;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchResultsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail;
    UiSettingsModel uiSettingsModel;
    ExpandableListView expandableListView;
    AppUserModel appUserModel;

    public GlobalSearchResultsAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail, ExpandableListView expandableListView) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        uiSettingsModel = UiSettingsModel.getInstance();
        this.expandableListView = expandableListView;
        appUserModel = AppUserModel.getInstance();
    }

    public void refreshList(List<String> expandableListTitle, HashMap<String, List<GlobalSearchResultModelNew>> expandableListDetail) {

        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return this.expandableListDetail.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

//        int sizeOfChild = this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
//                .size() > 5 ? 5 : this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
//                .size();

        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
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

        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.globalgroup, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.groupheader);

        listTitleTextView.setText(listTitle);

        return convertView;

    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, final View convertView, final ViewGroup parent) {
        final GlobalSearchResultModelNew expandedListText = (GlobalSearchResultModelNew) getChild(groupPosition, childPosition);

        LayoutInflater inflater;
        View vi = convertView;
        final ViewHolder holder;
        if (vi == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.globalsearchresultitem_en, null);
            holder = new ViewHolder(vi);
            holder.childPosition = childPosition;
            holder.groupPosition = groupPosition;
            holder.parent = parent;
            holder.view = vi;
            vi.setTag(holder);

        } else {

            holder = (ViewHolder) vi.getTag();
        }

        holder.cellView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtTitle.setText(expandedListText.name);

        if (isValidString(expandedListText.authordisplayname)) {
            holder.txtAuthor.setText("Author Name: " + expandedListText.authordisplayname);
        } else {
            holder.txtAuthor.setText(expandedListText.sitename);
        }


        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCreatedOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStartDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtEndDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAskedby.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtdateCreated.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtFotor.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        holder.txtFotor.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        holder.txtTitle.setFocusable(false);

        holder.ratingBar.setRating(expandedListText.ratingid);

        final View finalVi = vi;
        holder.btnContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((GlobalSearchResultsActivity) finalVi.getContext()).globalSearchContextMenu(finalVi, childPosition, holder.btnContextMenu, expandedListText);

            }
        });

        holder.txtFotor.setVisibility(View.GONE);
        if (childPosition == getChildrenCount(groupPosition) - 1) {

            holder.txtFotor.setVisibility(View.VISIBLE);
            if (holder.txtFotor != null) {

                holder.txtFotor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long packedPos = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
                        int flatPos = expandableListView.getFlatListPosition(packedPos);

//Getting the ID for our child
                        long id = expandableListView.getExpandableListAdapter().getChildId(groupPosition, childPosition);

                        ((ExpandableListView) parent).performItemClick(finalVi, flatPos, id);
                    }
                });
            }

        }
        String imgUrl = appUserModel.getSiteURL() + "/Content/SiteFiles/Images/" + expandedListText.contenttypethumbnail;


        switch (expandedListText.contextMenuId) {
            case 1:// mylearning
                holder.txtStartDate.setVisibility(View.GONE);
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtdateCreated.setText("Date Created :" + expandedListText.createddate);
                holder.txtAskedby.setText("Content type: " + expandedListText.contenttype);
                break;
            case 2://catalog
                holder.txtStartDate.setVisibility(View.GONE);
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtdateCreated.setText("Date Created :" + expandedListText.createddate);
                holder.txtAskedby.setText("Content type: " + expandedListText.contenttype);
                break;
            case 8:// events
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtdateCreated.setVisibility(View.GONE);
                holder.txtdateCreated.setText("Date Created :" + expandedListText.createddate);
                holder.txtAskedby.setText("Content type: " + expandedListText.contenttype);

                holder.txtEndDate.setText("End date:" + expandedListText.eventenddatedisplay);
                String fromStartDate = convertToEventDisplayDateFormat(expandedListText.eventstartdatedisplay, "yyyy-MM-dd hh:mm:ss");

                String fromEndDate = convertToEventDisplayDateFormat(expandedListText.eventenddatedisplay, "yyyy-MM-dd hh:mm:ss");

                holder.txtStartDate.setText("Start date:" + fromStartDate);

                holder.txtEndDate.setText("End date:" + fromEndDate);

                holder.txtAuthor.setText("Facilitator :" + expandedListText.authordisplayname);
//                holder.txtAskedby.setText("Avaliable Seats :" + expandedListText.availableseats);
                break;
            case 4://Discussion forum
                holder.ratingBar.setVisibility(View.GONE);
                holder.txtStartDate.setVisibility(View.GONE);
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtAskedby.setVisibility(View.GONE);
                if (expandedListText.objecttypeid == 17) { // topic
                    holder.txtAuthor.setText("Posted By : " + expandedListText.authordisplayname);
                    holder.txtdateCreated.setText("Date created: " + expandedListText.createddate);
                } else {  // forum

                    holder.txtAuthor.setText("Moderator :" + expandedListText.authordisplayname);
                    holder.txtdateCreated.setText("Date created: " + expandedListText.createddate);
                }
                break;
            case 5:// askexpert
                holder.ratingBar.setVisibility(View.GONE);
                holder.txtAuthor.setVisibility(View.GONE);
                holder.txtStartDate.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtAskedby.setText("Asked by: " + expandedListText.authordisplayname + " ");
                holder.txtdateCreated.setText("Date created: " + expandedListText.createddate + " ");
                break;
            case 10:// people listing
                holder.ratingBar.setVisibility(View.GONE);
                holder.txtStartDate.setVisibility(View.GONE);
                holder.txtEndDate.setVisibility(View.GONE);
                holder.txtCreatedOn.setVisibility(View.GONE);
                holder.txtdateCreated.setVisibility(View.GONE);
//                imgUrl = appUserModel.getSiteURL() + "/Content/SiteFiles/374/ProfileImages/" + expandedListText.contenttypethumbnail;
                break;
            default:
                holder.ratingBar.setVisibility(View.GONE);

        }


//        if (expandedListText.contextMenuId == 10) {
//            if (expandedListText.authordisplayname != null) {
//
//                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
//
//                String displayNameDrawable = getFirstCaseWords(expandedListText.authordisplayname);
//                int color = generator.getColor(displayNameDrawable);
//
//                TextDrawable drawable = TextDrawable.builder()
//                        .buildRect(displayNameDrawable, color);
//
//                holder.imageThumb.setBackground(drawable);
//
//            } else {
//                holder.imageThumb.setBackground(vi.getResources().getDrawable(R.drawable.cellimage));
//
//            }
//        } else {
        Picasso.with(vi.getContext()).load(imgUrl).placeholder(vi.getResources().getDrawable(R.drawable.cellimage)).into(holder.imageThumb);

//        }
        return vi;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return super.getChildType(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;

    }

    class ViewHolder {
        public int childPosition;
        public int groupPosition;
        public ViewGroup parent;
        public View view;
        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imageThumb;


        @Nullable
        @BindView(R.id.cellview)
        LinearLayout cellView;

        @Nullable
        @BindView(R.id.txt_title_name)
        TextView txtTitle;

        @Nullable
        @BindView(R.id.txtAuthor)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.txtCreatedOn)
        TextView txtCreatedOn;

        @Nullable
        @BindView(R.id.txtStartDate)
        TextView txtStartDate;

        @Nullable
        @BindView(R.id.txtEndDate)
        TextView txtEndDate;


        @Nullable
        @BindView(R.id.txtAskedby)
        TextView txtAskedby;

        @Nullable
        @BindView(R.id.txtdateCreated)
        TextView txtdateCreated;


        @Nullable
        @BindView(R.id.ratingBar)
        RatingBar ratingBar;


        @Nullable
        @BindView(R.id.footortxt)
        TextView txtFotor;


        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }


//        @OnCheckedChanged(R.id.chxBox)
//        void onGenderSelected(CheckBox button, boolean checked) {
//            //do your stuff.
//            Log.d("CHX", "onGenderSelected: " + childPosition);
//            ((ExpandableListView) parent).performItemClick(view, childPosition, groupPosition);
//
//        }

//        @OnClick({R.id.footortxt})
//        public void actionsForMenu(View view) {
//
//            Toast.makeText(context, "clicked " + groupPosition, Toast.LENGTH_SHORT).show();
//
//        }

    }

}

