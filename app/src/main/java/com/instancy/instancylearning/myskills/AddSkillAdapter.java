package com.instancy.instancylearning.myskills;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
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

public class AddSkillAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AddSkillModel> addSkillModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AddSkillAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AddSkillModel> searchList;
    AppController appcontroller;

    public AddSkillAdapter(Activity activity, int resource, List<AddSkillModel> addSkillModelList) {
        this.activity = activity;
        this.addSkillModelList = addSkillModelList;
        this.searchList = new ArrayList<AddSkillModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appcontroller = AppController.getInstance();
        this.searchList.addAll(addSkillModelList);

    }

    public void refreshList(List<AddSkillModel> addSkillModelList) {
        this.addSkillModelList = addSkillModelList;
        this.searchList = new ArrayList<AddSkillModel>();
        this.searchList.addAll(addSkillModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return addSkillModelList != null ? addSkillModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return addSkillModelList.get(position);
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
        convertView = inflater.inflate(R.layout.addskillcell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtUserName.setText(addSkillModelList.get(position).preferrencetitle);


        holder.txtUserName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAddress.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));


        holder.txtAddress.setVisibility(View.GONE);

        convertView.setTag(holder);
        return convertView;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        addSkillModelList.clear();
        if (charText.length() == 0) {
            addSkillModelList.addAll(searchList);
        } else {
            for (AddSkillModel s : searchList) {
                if (s.preferrencetitle.toLowerCase(Locale.getDefault()).contains(charText) || s.description.toLowerCase(Locale.getDefault()).contains(charText)) {
                    addSkillModelList.add(s);
                }
            }
        }
        notifyDataSetChanged();
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
        @BindView(R.id.txtUserName)
        TextView txtUserName;

        @Nullable
        @BindView(R.id.txtAddress)
        TextView txtAddress;


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;


        @OnClick({R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }

}


