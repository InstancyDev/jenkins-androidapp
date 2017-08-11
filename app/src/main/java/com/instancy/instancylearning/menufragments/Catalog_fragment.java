package com.instancy.instancylearning.menufragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.mainactivities.Settings_activity;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.StaticValues;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.instancy.instancylearning.utils.StaticValues.CONTEXT_TITLE;
import static com.instancy.instancylearning.utils.Utilities.tintMenuIcon;

/**
 * Created by Upendranath on 5/19/2017.
 */

public class Catalog_fragment extends Fragment {


    @Bind(R.id.lable_catalog)
    TextView frqagmentName;

    public Catalog_fragment() {


    }
    UiSettingsModel uiSettingsModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiSettingsModel = UiSettingsModel.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        ButterKnife.bind(this, rootView);
        initilizeView();
        return rootView;
    }

    public void initilizeView() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Here custom catalog");
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        String contextTitle = "";
        if (bundle != null) {
            contextTitle = bundle.getString(CONTEXT_TITLE);
            frqagmentName.setText(contextTitle);
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(uiSettingsModel.getAppHeaderColor())));
        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>My Learning</font>"));
//        actionBar.setTitle(Html.fromHtml("<font color='" + uiSettingsModel.getHeaderTextColor() + "'>" + contextTitle + "</font>"));

        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.catalog_menu, menu);
        MenuItem item = menu.findItem(R.id.catalog_search);

        if (item != null) {
            item.setIcon(R.drawable.ic_search_black_24dp);
            tintMenuIcon(getActivity(), item, R.color.colorWhite);
            item.setTitle("Search");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String link = bundle.getString("url");

        }
    }

    private ActionBar getActionBar() {

        return ((AppCompatActivity) getActivity()).getSupportActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.catalog_search) {
            Intent intentSettings = new Intent(getActivity(), Settings_activity.class);
            intentSettings.putExtra(StaticValues.KEY_ISLOGIN, true);
            startActivity(intentSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
