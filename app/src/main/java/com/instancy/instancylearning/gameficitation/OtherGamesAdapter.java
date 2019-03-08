package com.instancy.instancylearning.gameficitation;

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

import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;

import com.instancy.instancylearning.globalpackage.AppController;

import com.instancy.instancylearning.localization.JsonLocalization;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.OtherGameModel;
import com.instancy.instancylearning.utils.JsonLocalekeys;
import com.instancy.instancylearning.utils.PreferencesManager;


import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isValidString;


/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class OtherGamesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<OtherGameModel> otherGameModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = OtherGamesAdapter.class.getSimpleName();

    AppController appcontroller;

    public OtherGamesAdapter(Context context, int resource, List<OtherGameModel> otherGameModelList) {
        this.context = context;
        this.otherGameModelList = otherGameModelList;

        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(context);
        db = new DatabaseHandler(context);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<OtherGameModel> otherGameModelList) {
        this.otherGameModelList = otherGameModelList;

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return otherGameModelList != null ? otherGameModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return otherGameModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.othergamecell, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));
        holder.txtName.setText(" " + otherGameModelList.get(position).name);
        holder.txtCredits.setText(" " + otherGameModelList.get(position).decimal2);

        holder.lbName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCredits.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.lbCertificate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtCertificate.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.txtCertificate.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));

        if (isValidString(otherGameModelList.get(position).certificatepreviewpath)) {
            holder.txtCertificate.setText(otherGameModelList.get(position).certificatePage);
            holder.txtCertificate.setText(JsonLocalization.getInstance().getStringForKey(JsonLocalekeys.view_certificate,context));
        } else {
            holder.txtCertificate.setText("NA");
        }

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
        @BindView(R.id.lbName)
        TextView lbName;

        @Nullable
        @BindView(R.id.txtName)
        TextView txtName;

        @Nullable
        @BindView(R.id.lbCredits)
        TextView lbCredits;

        @Nullable
        @BindView(R.id.txtCredits)
        TextView txtCredits;

        @Nullable
        @BindView(R.id.lbCertificate)
        TextView lbCertificate;

        @Nullable
        @BindView(R.id.txtCertificate)
        TextView txtCertificate;


        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;


        @OnClick({R.id.txtCertificate})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }


}


