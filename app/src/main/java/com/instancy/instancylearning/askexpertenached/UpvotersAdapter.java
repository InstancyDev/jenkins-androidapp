package com.instancy.instancylearning.askexpertenached;

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
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.PeopleListingModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.models.UpvotersModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getFirstCaseWords;
import static com.instancy.instancylearning.utils.Utilities.isValidString;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class UpvotersAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertUpVoters> upvotersModelList = null;

    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = UpvotersAdapter.class.getSimpleName();


    public UpvotersAdapter(Activity activity, List<AskExpertUpVoters> upvotersModelList) {
        this.activity = activity;
        this.upvotersModelList = upvotersModelList;

        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

    }

    public void refreshList(List<AskExpertUpVoters> upvotersModelList) {
        this.upvotersModelList = upvotersModelList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return upvotersModelList != null ? upvotersModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return upvotersModelList.get(position);
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

        holder.txtUserName.setText(upvotersModelList.get(position).userName);
        holder.txtUserName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtUserJobTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (isValidString(upvotersModelList.get(position).jobTitle)) {
            holder.txtUserJobTitle.setText(upvotersModelList.get(position).jobTitle);
        }

        String displayNameDrawable = getFirstCaseWords(upvotersModelList.get(position).userName);

        if (upvotersModelList.get(position).picture.length() > 2) {
            String imgUrl = appUserModel.getSiteURL() + upvotersModelList.get(position).picture;
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
        @BindView(R.id.card_view)
        CardView card_view;


        @OnClick({R.id.txtUserName, R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


