package com.instancy.instancylearning.events;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mylearning.MyLearningScheduleChildModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.fromHtmlForYourNExt;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringMethod;
import static com.instancy.instancylearning.utils.Utilities.isValidString;


public class PrerequisiteExpandableAdapter extends BaseExpandableListAdapter {

    private LinkedHashMap<String, List<PrerequisiteModel>> prerequisiteExpandableList;
    private Context _context;
    private MyLearningModel myLearningModel;
    private UiSettingsModel uiSettingsModel;
    private AlertDialog alertDialog;
    AppUserModel appUserModel;

    ExpandableListView expandableListView;

    public PrerequisiteExpandableAdapter(Context context, LinkedHashMap<String, List<PrerequisiteModel>> prerequisiteExpandableList, MyLearningModel myLearningModel, ExpandableListView expandableListView) {
        this._context = context;
        this.prerequisiteExpandableList = prerequisiteExpandableList;
        this.myLearningModel = myLearningModel;
        uiSettingsModel = UiSettingsModel.getInstance();
        this.expandableListView = expandableListView;
        appUserModel = AppUserModel.getInstance();
    }

    public void refreshList(LinkedHashMap<String, List<PrerequisiteModel>> prerequisiteExpandableList) {
        this.prerequisiteExpandableList = prerequisiteExpandableList;
        this.notifyDataSetChanged();
        if (prerequisiteExpandableList != null && prerequisiteExpandableList.size() > 0) {
            for (int i = 0; i < prerequisiteExpandableList.size(); i++)
                expandableListView.expandGroup(i);
        }
    }

    @Override
    public int getGroupCount() {
        return prerequisiteExpandableList != null ? prerequisiteExpandableList.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String key = getKey(groupPosition);
        return prerequisiteExpandableList != null ? prerequisiteExpandableList.get(key).size() : 0;
    }

    @Override
    public Object getGroup(int i) {
        return getKey(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getChildData(groupPosition, childPosition);
    }

    private PrerequisiteModel getChildData(int groupPosition, int childPosition) {
        String key = getKey(groupPosition);
        List<PrerequisiteModel> list = prerequisiteExpandableList.get(key);
        return list.size() > 0 ? list.get(childPosition) : null;
    }

    private String getKey(int keyPosition) {
        int counter = 0;
        Iterator<String> keyIterator = prerequisiteExpandableList.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (counter++ == keyPosition) {
                return key;
            }
        }
        // will not be the case ...
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupName = getKey(groupPosition);

        PrerequisiteModel prerequisiteModel = (PrerequisiteModel) getChild(groupPosition, 0);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.prerequisitegroup, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.txtTitle);

        TextView txtDescription = (TextView) convertView
                .findViewById(R.id.txtDescription);

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(groupName);

        if (isValidString(prerequisiteModel.prereqGrouplabel)) {
            txtDescription.setText(prerequisiteModel.prereqGrouplabel);
            txtDescription.setVisibility(View.VISIBLE);
        } else {
            txtDescription.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        final PrerequisiteModel prerequisiteModel = (PrerequisiteModel) getChild(groupPosition, childPosition);

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.prerequisitechild, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.parent = parent;
        holder.getChildPosition = childPosition;
        holder.getGroupPosition = groupPosition;
        holder.parent = parent;
        holder.view = convertView;

        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCreatedOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbAddedtoMylearning.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtShortDesc.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtSelectedDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        holder.lbSelectedDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtCourseName.setText(prerequisiteModel.Title);
        holder.txtCreatedOn.setText(prerequisiteModel.CreatedOn);
        holder.txtAuthor.setText(prerequisiteModel.AuthorDisplayName);
        holder.lbAddedtoMylearning.setText("");
        holder.txtContentType.setText(prerequisiteModel.ContentType);

        if (prerequisiteModel.Prerequisites.equalsIgnoreCase("1") || prerequisiteModel.Prerequisites.equalsIgnoreCase("2") || prerequisiteModel.Prerequisites.equalsIgnoreCase("3")) {
            if (prerequisiteModel.ContentTypeId.equalsIgnoreCase("70") && prerequisiteModel.EventScheduleType.equalsIgnoreCase("1")) {

                if (isValidString(prerequisiteModel.EventStartDateTime) && isValidString(prerequisiteModel.EventEndDateTime)) {
                    holder.selectedDateLayout.setVisibility(View.VISIBLE);
                    holder.txtSelectedDate.setText(prerequisiteModel.EventStartDateTime + " - " + prerequisiteModel.EventEndDateTime);
                    holder.lbSelectedDate.setText(getLocalizationValue(JsonLocalekeys.prerequis_label_selectedtimelabel));
                }else {
                    holder.selectedDateLayout.setVisibility(View.GONE);
                }

            }else {
                holder.selectedDateLayout.setVisibility(View.GONE);
            }

        }else {
            holder.selectedDateLayout.setVisibility(View.GONE);
        }

//        if (isValidString(prerequisiteModel.ShortDescription)) {
//            holder.txtShortDesc.setVisibility(View.VISIBLE);
//            holder.txtShortDesc.setText(prerequisiteModel.ShortDescription);
//
//            String[] newAry = prerequisiteModel.ShortDescription.split("\\} -->");
//
//            if (newAry != null && newAry.length > 1) {
//                holder.txtShortDesc.setText(Html.fromHtml(newAry[1]));
//            } else {
//                holder.txtShortDesc.setText(fromHtmlForYourNExt(prerequisiteModel.ShortDescription));
//            }
//
//
//        } else {
        holder.txtShortDesc.setVisibility(View.GONE);
//        }

        String imgUrl = "";

        if (!prerequisiteModel.ThumbnailImagePath.contains("http")) {
            imgUrl = appUserModel.getSiteURL() + prerequisiteModel.ThumbnailImagePath.trim();
        } else {
            imgUrl = prerequisiteModel.ThumbnailImagePath.trim();
        }

        String iconUrl = appUserModel.getSiteURL() + prerequisiteModel.ThumbnailIconPath.trim();

        Glide.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imageThumb);

        Glide.with(convertView.getContext()).load(iconUrl).placeholder(R.drawable.cellimage).into(holder.fabbtnthumb);

        holder.fabbtnthumb.setBackgroundTintList(ColorStateList.valueOf(convertView.getResources().getColor(R.color.colorWhite)));

        holder.chxBoxPreq.setOnCheckedChangeListener(null);
        if (prerequisiteModel.Ischecked && prerequisiteModel.IsLearnerContent && groupPosition > 0) {
            holder.chxBoxPreq.setChecked(true);
            holder.chxBoxPreq.setEnabled(false);
            holder.lbAddedtoMylearning.setText(getLocalizationValue(JsonLocalekeys.prerequistesalerttitle2_alerttitle2));
            holder.lbAddedtoMylearning.setVisibility(View.VISIBLE);
        } else {
            holder.lbAddedtoMylearning.setVisibility(View.GONE);
            holder.chxBoxPreq.setChecked(prerequisiteModel.isItemChecked);
            holder.chxBoxPreq.setEnabled(true);
        }

        holder.chxBoxPreq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                long packedPos = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
                int flatPos = expandableListView.getFlatListPosition(packedPos);
                long id = expandableListView.getExpandableListAdapter().getChildId(groupPosition, childPosition);

                ((ExpandableListView) parent).performItemClick(holder.chxBoxPreq, flatPos, id);

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private String getLocalizationValue(String key) {
        return JsonLocalization.getInstance().getStringForKey(key, _context);
    }

    class ViewHolder {
        public int getChildPosition;
        public int getGroupPosition;
        public MyLearningModel myLearningDetalData;
        public ViewGroup parent;
        public View view;

        @Nullable
        @BindView(R.id.txtCourseName)
        TextView txtCourseName;

        @Nullable
        @BindView(R.id.txtCreatedOn)
        TextView txtCreatedOn;

        @Nullable
        @BindView(R.id.txtAuthor)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.lbAddedtoMylearning)
        TextView lbAddedtoMylearning;

        @Nullable
        @BindView(R.id.txtContentType)
        TextView txtContentType;

        @Nullable
        @BindView(R.id.lbSelectedDate)
        TextView lbSelectedDate;

        @Nullable
        @BindView(R.id.txtSelectedDate)
        TextView txtSelectedDate;

        @Nullable
        @BindView(R.id.selectedDateLayout)
        LinearLayout selectedDateLayout;

        @Nullable
        @BindView(R.id.txtShortDesc)
        TextView txtShortDesc;

        @Nullable
        @BindView(R.id.imageThumb)
        ImageView imageThumb;

        @Nullable
        @BindView(R.id.chxBoxPreq)
        CheckBox chxBoxPreq;

        @Nullable
        @BindView(R.id.fabbtnthumb)
        FloatingActionButton fabbtnthumb;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton contextMenu;


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @OnClick({R.id.btn_contextmenu})
        public void actionsForMenu(View view) {

            if (view.getId() == R.id.btn_contextmenu) {

                long packedPos = ExpandableListView.getPackedPositionForChild(getGroupPosition, getChildPosition);
                int flatPos = expandableListView.getFlatListPosition(packedPos);
                long id = expandableListView.getExpandableListAdapter().getChildId(getGroupPosition, getChildPosition);

                ((ExpandableListView) parent).performItemClick(view, flatPos, id);

            }
//            else if (view.getId() == R.id.chxBoxPreq) {
//                CheckBox checkBox = (CheckBox) view.findViewById(R.id.chxBoxPreq);
//                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                        long packedPos = ExpandableListView.getPackedPositionForChild(getGroupPosition, getChildPosition);
//                        int flatPos = expandableListView.getFlatListPosition(packedPos);
//                        long id = expandableListView.getExpandableListAdapter().getChildId(getGroupPosition, getChildPosition);
//
//                        ((ExpandableListView) parent).performItemClick(chxBoxPreq, flatPos, id);
//
//                    }
//                });
//
//            }
        }
    }


}
