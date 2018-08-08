package com.instancy.instancylearning.globalsearch;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.GlobalMethods;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.GlobalSearchCategoryModel;
import com.instancy.instancylearning.models.GlobalSearchResultModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class GlobalSearchResultsAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<GlobalSearchResultModel>> expandableListDetail;
    UiSettingsModel uiSettingsModel;
    ExpandableListView expandableListView;
    AppUserModel appUserModel;

    public GlobalSearchResultsAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<GlobalSearchResultModel>> expandableListDetail, ExpandableListView expandableListView) {
        this.context = context;
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = expandableListTitle;
        uiSettingsModel = UiSettingsModel.getInstance();
        this.expandableListView = expandableListView;
        appUserModel = AppUserModel.getInstance();
    }

    public void refreshList(List<String> expandableListTitle, HashMap<String, List<GlobalSearchResultModel>> expandableListDetail) {

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
        final GlobalSearchResultModel expandedListText = (GlobalSearchResultModel) getChild(groupPosition, childPosition);

        LayoutInflater inflater;
        View vi = convertView;
        final ViewHolder holder;
        if (vi == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.globalsearchresultitem, null);
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
        holder.txtTitle.setText(expandedListText.namePreFix + "" + expandedListText.title);
        holder.txtCourse.setText(expandedListText.contentType);
        holder.txtShortDesc.setText(expandedListText.shortDescription);
        holder.txtLongDesc.setText(expandedListText.longDescription);
        holder.txtAuthor.setText(expandedListText.authorName);

        holder.txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCourse.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtLongDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtFotor.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonTextColor()));
        holder.txtFotor.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        holder.txtTitle.setFocusable(false);


        if (expandedListText.longDescription.length() == 0) {
            holder.txtLongDesc.setVisibility(View.GONE);
        }
        if (expandedListText.shortDescription.length() == 0) {
            holder.txtShortDesc.setVisibility(View.GONE);
        }

        if (expandedListText.ratingID.length() == 0) {
            holder.ratingBar.setVisibility(View.GONE);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
            try {
                int ratingInt = Integer.parseInt(expandedListText.ratingID);
                holder.ratingBar.setRating(ratingInt);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }

        if (expandedListText.contextMenuId == 10) {
            holder.txtAvaSeats.setVisibility(View.GONE);
            holder.txtSiteName.setVisibility(View.GONE);
            holder.txtSiteLine.setVisibility(View.GONE);
            holder.txtAutLine.setVisibility(View.GONE);

        }

        String imgUrl = appUserModel.getSiteURL() + expandedListText.thumbnailImagePath;

        Picasso.with(vi.getContext()).load(imgUrl).placeholder(vi.getResources().getDrawable(R.drawable.cellimage)).into(holder.imageThumb);

        final View finalVi = vi;
        holder.btnContextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.globalSearchContextMenu(finalVi,childPosition,holder.btnContextMenu,"");

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
        @BindView(R.id.txt_title_name)
        TextView txtTitle;

        @Nullable
        @BindView(R.id.txt_coursename)
        TextView txtCourse;

        @Nullable
        @BindView(R.id.txtShortDesc)
        TextView txtShortDesc;

        @Nullable
        @BindView(R.id.txtLongDesc)
        TextView txtLongDesc;

        @Nullable
        @BindView(R.id.txt_author)
        TextView txtAuthor;


        @Nullable
        @BindView(R.id.cellview)
        LinearLayout cellView;


        @Nullable
        @BindView(R.id.rat_adapt_ratingbar)
        RatingBar ratingBar;


        @Nullable
        @BindView(R.id.footortxt)
        TextView txtFotor;

        @Nullable
        @BindView(R.id.txt_avaliableseats)
        TextView txtAvaSeats;

        @Nullable
        @BindView(R.id.txt_sitename)
        TextView txtSiteName;

        @Nullable
        @BindView(R.id.txt_lineatcoursename)
        TextView txtAutLine;

        @Nullable
        @BindView(R.id.txt_linesitename)
        TextView txtSiteLine;

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

