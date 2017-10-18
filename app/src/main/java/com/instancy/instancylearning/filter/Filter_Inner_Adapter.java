package com.instancy.instancylearning.filter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.NativeSetttingsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.List;

/**
 * Created by Upendranath on 10/10/2017 Working on Instancy-Playground-Android.
 */

public class Filter_Inner_Adapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<NativeSetttingsModel.FilterInnerModel> filterInnerModelList = null;
    private List<NativeSetttingsModel.FilterInnerModel> searchList = null;
    AppUserModel appUserModel;
    AppController appcontroller;
    PreferencesManager preferencesManager;
    UiSettingsModel uiSettingsModel;

    public Filter_Inner_Adapter() {
        super();

    }

    public Filter_Inner_Adapter(Activity activity, int resource, List<NativeSetttingsModel.FilterInnerModel> filterInnerModelList) {
        this.activity = activity;
        this.filterInnerModelList = filterInnerModelList;

        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();

        notifyDataSetChanged();
        preferencesManager = PreferencesManager.getInstance();
        appUserModel.getWebAPIUrl();
          /* setup enter and exit animation */
        appcontroller = AppController.getInstance();

    }

//    public void updateList(List<NativeSetttingsModel.FilterInnerModel> filterInnerModelLists) {
//        List<NativeSetttingsModel.FilterInnerModel> tempList = new ArrayList<NativeSetttingsModel.FilterInnerModel>();
//        tempList.addAll(filterInnerModelLists);
//        this.filterInnerModelList.clear();
//        this.filterInnerModelList.addAll(tempList);
//        this.notifyDataSetChanged();
//    }

    @Override
    public int getCount() {
        return filterInnerModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return filterInnerModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NativeSetttingsModel.FilterInnerModel filterInnerModel = filterInnerModelList.get(position);
        View vi = convertView;
        if (convertView == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.nativesettingsplaincell, null);
            TextView expandedListTextView = (TextView) vi.findViewById(R.id.settings_label);
            expandedListTextView.setText(filterInnerModel.name);


            if (filterInnerModel.isSelected) {
                expandedListTextView.setTextColor(vi.getResources().getColor(R.color.colorStatusCompleted));
                filterInnerModelList.get(position).isSelected = false;
            } else {
                expandedListTextView.setTextColor(vi.getResources().getColor(R.color.colorDarkGrey));
            }
        }
        return vi;
    }

}
