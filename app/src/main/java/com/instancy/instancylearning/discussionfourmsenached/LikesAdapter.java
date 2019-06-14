package com.instancy.instancylearning.discussionfourmsenached;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.askexpertenached.AskExpertUpVoters;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getFirstCaseWords;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class LikesAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<LikesModel> likesModelList = null;

    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = LikesAdapter.class.getSimpleName();


    public LikesAdapter(Activity activity, List<LikesModel> likesModelList) {
        this.activity = activity;
        this.likesModelList = likesModelList;

        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

    }

    public void refreshList(List<LikesModel> likesModelList) {
        this.likesModelList = likesModelList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return likesModelList != null ? likesModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return likesModelList.get(position);
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
        convertView = inflater.inflate(R.layout.askexpertupvoterscell, parent, false);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtUserName.setText(likesModelList.get(position).userName);
        holder.txtUserName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        String displayNameDrawable = getFirstCaseWords(likesModelList.get(position).userName);

        holder.txtUserJobTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtUserAddress.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (isValidString(likesModelList.get(position).jobTitle)) {

            holder.txtUserJobTitle.setVisibility(View.VISIBLE);
            holder.txtUserJobTitle.setText(likesModelList.get(position).jobTitle);
        } else {
            holder.txtUserJobTitle.setVisibility(View.GONE);
        }

        if (isValidString(likesModelList.get(position).userAddress)) {

            holder.txtUserAddress.setVisibility(View.VISIBLE);
            holder.txtUserAddress.setText(likesModelList.get(position).userAddress);
        } else {
            holder.txtUserAddress.setVisibility(View.GONE);
        }

        if (likesModelList.get(position).picture.length() > 2) {
            String imgUrl = appUserModel.getSiteURL() + likesModelList.get(position).picture;
            Glide.with(convertView.getContext()).load(imgUrl).placeholder(convertView.getResources().getDrawable(R.drawable.defaulttechguy)).into(holder.imgThumb);

        } else {

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT

            int color = generator.getColor(displayNameDrawable);

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(displayNameDrawable, color);

            holder.imgThumb.setBackground(drawable);

        }

        return convertView;
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
        @BindView(R.id.txtUserName)
        TextView txtUserName;

        @Nullable
        @BindView(R.id.txtUserJobTitle)
        TextView txtUserJobTitle;

        @Nullable
        @BindView(R.id.txtUserAddress)
        TextView txtUserAddress;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;


        @OnClick({R.id.txtUserName, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


