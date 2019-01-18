package com.instancy.instancylearning.advancedfilters_mylearning;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.NativeSetttingsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class ContentFilterAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ContentFilterByModel> contentFilterByModelList = null;

    AppUserModel appUserModel;
    AppController appcontroller;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    public ContentFilterAdapter(Activity activity, List<ContentFilterByModel> contentFilterByModelList) {
        this.activity = activity;
        this.contentFilterByModelList = contentFilterByModelList;

        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();

        notifyDataSetChanged();
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
        /* setup enter and exit animation */
        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<ContentFilterByModel> contentFilterByModelList) {
        this.contentFilterByModelList = contentFilterByModelList;
        this.contentFilterByModelList = new ArrayList<ContentFilterByModel>();
        this.contentFilterByModelList.addAll(contentFilterByModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contentFilterByModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentFilterByModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContentFilterByModel contentFilterByModel = contentFilterByModelList.get(position);
        View vi = convertView;
        if (convertView == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.advcontentypecell, null);
        }
        TextView txtCategoryDisplayName = (TextView) vi.findViewById(R.id.txtCategoryDisplayName);
        TextView txtCategorySelectedSkills = (TextView) vi.findViewById(R.id.txtCategorySelectedSkills);
        TextView txtArrow = (TextView) vi.findViewById(R.id.txtArrow);
        txtCategoryDisplayName.setText(contentFilterByModel.categoryDisplayName);

        txtCategorySelectedSkills.setText(contentFilterByModel.selectedSkillsNameString);
        txtCategorySelectedSkills.setVisibility(View.GONE);

        return vi;
    }

}
