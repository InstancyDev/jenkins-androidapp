package com.instancy.instancylearning.home;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.interfaces.RecyclerViewClickListener;
import com.instancy.instancylearning.models.CatalogCategoryButtonModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;

import java.util.List;

/**
 * Created by Upendranath on 11/24/2017.
 */

public class HomeButtonAdapter extends RecyclerView.Adapter<HomeButtonAdapter.ViewHolder> {

    List<SideMenusModel> categoryButtonModelList;
    private RecyclerViewClickListener mListener;

    UiSettingsModel uiSettingsModel;


    public HomeButtonAdapter(List<SideMenusModel> categoryButtonModelList1, RecyclerViewClickListener mListener) {
        this.categoryButtonModelList = categoryButtonModelList1;
        this.mListener = mListener;
        uiSettingsModel = UiSettingsModel.getInstance();    }

    public void reloadAllContent(List<SideMenusModel> dataset) {
        categoryButtonModelList.clear();
        categoryButtonModelList.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalog_button, parent, false);
        CardView cardView =(CardView)relativeLayout.findViewById(R.id.card_view);
//        // set the view's size, margins, paddings and layout parameters
        cardView.setCardBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));
        cardView.setRadius(14);
        ViewHolder vh = new ViewHolder(relativeLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView txtCategoryName = (TextView) holder.relativeLayout.findViewById(R.id.categoryname);

        txtCategoryName.setText(categoryButtonModelList.get(position).getDisplayName());

    }


    @Override
    public int getItemCount() {
        return categoryButtonModelList.isEmpty() ? 0 : categoryButtonModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout relativeLayout;

        public ViewHolder(RelativeLayout v) {
            super(v);
            relativeLayout = v;
        }
    }
}
