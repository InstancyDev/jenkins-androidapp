package com.instancy.instancylearning.mylearning;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.ReviewContentModel;
import com.instancy.instancylearning.models.ReviewRatingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class RatingsAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ReviewRatingModel> ratingModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = RatingsAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<ReviewRatingModel> searchList;


    public RatingsAdapter(Activity activity, int resource, List<ReviewRatingModel> ratingModelList) {
        this.activity = activity;
        this.ratingModelList = ratingModelList;
        this.searchList = new ArrayList<ReviewRatingModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();


    }

    public void refreshList(List<ReviewRatingModel> ratingModelList) {
        this.ratingModelList = ratingModelList;
        this.searchList = new ArrayList<ReviewRatingModel>();
        this.searchList.addAll(ratingModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ratingModelList != null ? ratingModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return ratingModelList.get(position);
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
        convertView = inflater.inflate(R.layout.ratingscell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtReview.setMaxLines(100);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtReview.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtDate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        String imgUrl = appUserModel.getSiteURL() + ratingModelList.get(position).picture;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        holder.txtName.setText(ratingModelList.get(position).userName);
        holder.txtDate.setText(ratingModelList.get(position).reviewDate);
        holder.txtReview.setText(ratingModelList.get(position).description);
        holder.ratingBar.setRating(ratingModelList.get(position).rating);

        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(convertView.getContext().getResources().getColor(R.color.colorRating), PorterDuff.Mode.SRC_ATOP);


        convertView.setTag(holder);
        return convertView;
    }


    static class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            Typeface iconFont = FontManager.getTypeface(view.getContext(), FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(view.findViewById(R.id.btn_attachment), iconFont);
        }


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txtreview)
        TextView txtReview;

        @Nullable
        @BindView(R.id.txtName)
        TextView txtName;

        @Nullable
        @BindView(R.id.txt_date)
        TextView txtDate;


        @Nullable
        @BindView(R.id.rat_adapt_ratingbar)
        RatingBar ratingBar;


//        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.btn_attachment})
//        public void actionsForMenu(View view) {
//
//            ((ListView) parent).performItemClick(view, getPosition, 0);
//
//        }

    }
}


