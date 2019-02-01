package com.instancy.instancylearning.advancedfilters_mylearning;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.mcoy_jiang.videomanager.ui.McoyVideoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class FilterByAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FilterByModel> filterByModelList = null;
    ContentFilterByModel contentFilterByModel;
    AppUserModel appUserModel;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;
    boolean goInside;

    public FilterByAdapter(Activity activity, List<FilterByModel> filterByModelList, ContentFilterByModel contentFilterByModel) {
        this.activity = activity;
        this.filterByModelList = filterByModelList;

        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        this.contentFilterByModel = contentFilterByModel;
        this.goInside = goInside;
        notifyDataSetChanged();
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
        /* setup enter and exit animation */

    }

    public void refreshList(List<FilterByModel> filterByModelList) {
        this.filterByModelList = filterByModelList;
        this.filterByModelList = new ArrayList<FilterByModel>();
        this.filterByModelList.addAll(filterByModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filterByModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return filterByModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FilterByModel filterByModel = filterByModelList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.advfilterchxcell, null);
        }
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.chxCategoryDisplayName = (CheckBox) convertView.findViewById(R.id.chxCategoryDisplayName);
        holder.txtCategoryDisplayName = (TextView) convertView.findViewById(R.id.txtCategoryDisplayName);
        holder.txtArrow = (TextView) convertView.findViewById(R.id.txtArrow);
        holder.txtCategoryDisplayName.setText(filterByModel.categoryName);
        if (filterByModel.isContainsChild) {
            holder.txtArrow.setVisibility(View.VISIBLE);
        } else {
            holder.txtArrow.setVisibility(View.GONE);
        }

        if (contentFilterByModel.categoryID.equalsIgnoreCase("bytype")) {
            holder.txtArrow.setVisibility(View.GONE);
        }

        if (filterByModel.isSelected) {
            holder.chxCategoryDisplayName.setChecked(true);
        } else {
            holder.chxCategoryDisplayName.setChecked(false);
        }

        holder.chxCategoryDisplayName.setButtonTintList(ColorStateList.valueOf(Color.parseColor(uiSettingsModel.getAppButtonBgColor())));


        return convertView;
    }

    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }


        @Nullable
        @BindView(R.id.chxCategoryDisplayName)
        CheckBox chxCategoryDisplayName;


        @Nullable
        @BindView(R.id.cardView)
        CardView cardView;

        @Nullable
        @BindView(R.id.txtCategoryDisplayName)
        TextView txtCategoryDisplayName;

        @Nullable
        @BindView(R.id.txtArrow)
        TextView txtArrow;


        @OnClick({R.id.txtCategoryDisplayName, R.id.cardView, R.id.chxCategoryDisplayName})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }


}
