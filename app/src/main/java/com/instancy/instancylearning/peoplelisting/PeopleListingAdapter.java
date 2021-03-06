package com.instancy.instancylearning.peoplelisting;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AllUserInfoModel;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.instancy.instancylearning.utils.StaticValues;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getFirstCaseWords;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class PeopleListingAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<PeopleListingModel> peopleListingModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = PeopleListingAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<PeopleListingModel> searchList;


    public PeopleListingAdapter(Activity activity, int resource, List<PeopleListingModel> peopleListingModelList) {
        this.activity = activity;
        this.peopleListingModelList = peopleListingModelList;
        this.searchList = new ArrayList<PeopleListingModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();


    }

    public void refreshList(List<PeopleListingModel> peopleListingModelList) {
        this.peopleListingModelList = peopleListingModelList;
        this.searchList = new ArrayList<PeopleListingModel>();
        this.searchList.addAll(peopleListingModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return peopleListingModelList != null ? peopleListingModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return peopleListingModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.peoplescell, parent, false);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtName.setText(peopleListingModelList.get(position).userDisplayname);
        holder.txtPlace.setText(peopleListingModelList.get(position).mainOfficeAddress);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtPlace.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtStatus.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        String mainOfficeAddress = peopleListingModelList.get(position).mainOfficeAddress;
        if (mainOfficeAddress.length() > 4) {
            holder.txtPlace.setText(mainOfficeAddress);
            holder.txtPlace.setVisibility(View.VISIBLE);
        } else {
            holder.txtPlace.setVisibility(View.INVISIBLE);
        }

        String connectionState = peopleListingModelList.get(position).connectionState;

        if (connectionState.length() > 4) {
            holder.txtStatus.setText(connectionState);
            holder.txtStatus.setVisibility(View.VISIBLE);
        } else {
            holder.txtStatus.setVisibility(View.INVISIBLE);
        }

        String imagePath = peopleListingModelList.get(position).memberProfileImage;

        String displayNameDrawable = getFirstCaseWords(peopleListingModelList.get(position).userDisplayname);

        if (imagePath.length() > 2) {
            String imgUrl = peopleListingModelList.get(position).siteURL + peopleListingModelList.get(position).memberProfileImage;

            Glide.with(convertView.getContext()).load(imgUrl).placeholder(convertView.getResources().getDrawable(R.drawable.defaulttechguy)).into(holder.imgThumb);

        } else {

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
//        int color1 = generator.getRandomColor();
// generate color based on a key (same key returns the same color), useful for list/grid views
            int color = generator.getColor(displayNameDrawable);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(displayNameDrawable, color);

            holder.imgThumb.setBackground(drawable);

        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        peopleListingModelList.clear();
        if (charText.length() == 0) {
            peopleListingModelList.addAll(searchList);
        } else {
            for (PeopleListingModel s : searchList) {
                if (s.userDisplayname.toLowerCase(Locale.getDefault()).contains(charText) || s.mainOfficeAddress.toLowerCase(Locale.getDefault()).contains(charText)) {
                    peopleListingModelList.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder {

        public int getPosition;
        public ViewGroup parent;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txtPeople)
        TextView txtName;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtPlace)
        TextView txtPlace;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtStatus)
        TextView txtStatus;


        @OnClick({R.id.btn_contextmenu, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


