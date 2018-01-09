package com.instancy.instancylearning.peoplelisting;

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

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AllUserInfoModel;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.DiscussionForumModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class PeopleListingAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AllUserInfoModel> allUserInfoModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = PeopleListingAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AllUserInfoModel> searchList;


    public PeopleListingAdapter(Activity activity, int resource, List<AllUserInfoModel> allUserInfoModelList) {
        this.activity = activity;
        this.allUserInfoModelList = allUserInfoModelList;
        this.searchList = new ArrayList<AllUserInfoModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();


    }

    public void refreshList(List<AllUserInfoModel> allUserInfoModelList) {
        this.allUserInfoModelList = allUserInfoModelList;
        this.searchList = new ArrayList<AllUserInfoModel>();
        this.searchList.addAll(allUserInfoModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allUserInfoModelList != null ? allUserInfoModelList.size() : 100;
    }

    @Override
    public Object getItem(int position) {
        return allUserInfoModelList.get(position);
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
        convertView = inflater.inflate(R.layout.peoplescell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

//        holder.txtName.setText(allUserInfoModelList.get(position).displayName);
//        holder.txtPlace.setText(allUserInfoModelList.get(position).displayName);


        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtPlace.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


//            String imgUrl = discussionTopicModels.get(position).imagedata;
//            Picasso.with(vi.getContext()).load(imgUrl).placeholder(R.drawable.cellimage).into(holder.imgThumb);


        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        allUserInfoModelList.clear();
        if (charText.length() == 0) {
            allUserInfoModelList.addAll(searchList);
        } else {
            for (AllUserInfoModel s : searchList) {
                if (s.displayName.toLowerCase(Locale.getDefault()).contains(charText) || s.displayName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    allUserInfoModelList.add(s);
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

        @OnClick({R.id.btn_contextmenu, R.id.card_view})
        public void actionsForMenu(View view) {

//            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


