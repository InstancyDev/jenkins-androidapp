package com.instancy.instancylearning.catalog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.MyLearningModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ContentViewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<MyLearningModel> myLearningModel = null;

    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SideMenusModel sideMenusModel;
    PreferencesManager preferencesManager;
    private String TAG = ContentViewAdapter.class.getSimpleName();

    private List<MyLearningModel> searchList;

    public ContentViewAdapter(Activity activity, List<MyLearningModel> myLearningModel) {
        this.activity = activity;
        this.myLearningModel = myLearningModel;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        this.sideMenusModel = sideMenusModel;
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
    }

    public void refreshList(List<MyLearningModel> myLearningModel) {
        this.myLearningModel = myLearningModel;
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
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {

        final ViewHolder holder;
        View vi = convertView;
        if (convertView == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        vi = inflater.inflate(R.layout.contentviewcell, null);
        holder = new ViewHolder(vi);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));


        holder.txtCourseName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbAuthor.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDescription.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtContentType.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtCourseName.setText(myLearningModel.get(position).getCourseName());
        holder.txtAuthor.setText(myLearningModel.get(position).getAuthor());
        holder.txtContentType.setText(myLearningModel.get(position).getMediaName());
        holder.txtDescription.setText(myLearningModel.get(position).getShortDes());

        if (myLearningModel.get(position).getShortDes().isEmpty()) {
            holder.txtDescription.setVisibility(View.GONE);
            holder.lbDescription.setVisibility(View.GONE);
        } else {
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.lbDescription.setVisibility(View.VISIBLE);
        }

        String imgUrl = appUserModel.getSiteURL() + myLearningModel.get(position).getThumbnailImagePath();

        Glide.with(activity).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);

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


    class ViewHolder {
        public int getPosition;

        public ViewGroup parent;


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;


        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txtCourseName)
        TextView txtCourseName;

        @Nullable
        @BindView(R.id.lbAuthor)
        TextView lbAuthor;

        @Nullable
        @BindView(R.id.txtAuthor)
        TextView txtAuthor;

        @Nullable
        @BindView(R.id.lbDescription)
        TextView lbDescription;

        @Nullable
        @BindView(R.id.txtDescription)
        TextView txtDescription;

        @Nullable
        @BindView(R.id.lbContentType)
        TextView lbContentType;

        @Nullable
        @BindView(R.id.txtContentType)
        TextView txtContentType;


        public ViewHolder(final View view) {
            ButterKnife.bind(this, view);
//            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
//            FontManager.markAsIconContainer(view.findViewById(R.id.btntxt_download), iconFont);


        }
    }

    private String getLocalizationValue(String key) {
        Log.d(TAG, "getLocalizationValue: archived" + JsonLocalization.getInstance().getStringForKey(key, activity));
        return JsonLocalization.getInstance().getStringForKey(key, activity);
    }
}


