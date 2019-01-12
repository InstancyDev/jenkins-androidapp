package com.instancy.instancylearning.localization;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.discussionfourmsenached.DiscussionCategoriesModel;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class LocalizationSelectionAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private Activity activity;
    private List<LocalizationSelectionModel> localizationSelectionModelList = null;
    private UiSettingsModel uiSettingsModel;
    private List<DiscussionCategoriesModel> searchList;
    AppUserModel appUserModel;

    public LocalizationSelectionAdapter(Activity activity, List<LocalizationSelectionModel> localizationSelectionModelList) {

        this.localizationSelectionModelList = localizationSelectionModelList;
        this.searchList = new ArrayList<DiscussionCategoriesModel>();
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        this.activity = activity;
        appUserModel = AppUserModel.getInstance();

    }

    public void refreshList(List<LocalizationSelectionModel> localizationSelectionModelList) {
        this.localizationSelectionModelList = localizationSelectionModelList;
        this.searchList = new ArrayList<DiscussionCategoriesModel>();

        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return localizationSelectionModelList != null ? localizationSelectionModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return localizationSelectionModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.locaecell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.cardView.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtLanguageName.setText(localizationSelectionModelList.get(position).languageName);
        holder.txtDescription.setText(localizationSelectionModelList.get(position).languageDescription);

        holder.txtLanguageName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        String thumbUrl = appUserModel.getSiteURL() + "Content/SiteFiles/FlagIcons/United_States_of_America.png";
        //+ localizationSelectionModelList.get(position).countryFlag;

        Picasso.with(activity).
                load(thumbUrl).
                placeholder(R.drawable.cellimage).
                into(holder.imgFlag);

        convertView.setTag(holder);
        return convertView;
    }


    static class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.txtLanguageName)
        TextView txtLanguageName;

        @Nullable
        @BindView(R.id.txtDescription)
        TextView txtDescription;

        @Nullable
        @BindView(R.id.imgFlag)
        ImageView imgFlag;

        @Nullable
        @BindView(R.id.cellview)
        RelativeLayout cellView;

        @Nullable
        @BindView(R.id.card_view)
        CardView cardView;


        @OnClick({R.id.cellview})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }
    }
}


