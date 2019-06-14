package com.instancy.instancylearning.catalog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.interfaces.RecyclerViewClickListener;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.CatalogCategoryButtonModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Upendranath on 11/24/2017.
 */

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ViewHolder> {

    List<CatalogCategoryButtonModel> categoryButtonModelList;
    private RecyclerViewClickListener mListener;

    UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;


    public ButtonAdapter(List<CatalogCategoryButtonModel> categoryButtonModelList1, RecyclerViewClickListener mListener, AppUserModel appUserModel) {
        this.appUserModel = appUserModel;
        this.categoryButtonModelList = categoryButtonModelList1;
        this.mListener = mListener;
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    public void reloadAllContent(List<CatalogCategoryButtonModel> dataset) {
        categoryButtonModelList.clear();
        categoryButtonModelList.addAll(dataset);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catalog_button_withthumbfull, parent, false);
        CardView cardView = (CardView) relativeLayout.findViewById(R.id.card_view);
//        // set the view's size, margins, paddings and layout parameters
        cardView.setCardBackgroundColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        cardView.setRadius(14);
        ViewHolder vh = new ViewHolder(relativeLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TextView txtCategoryName = (TextView) holder.relativeLayout.findViewById(R.id.categoryname);
        ImageView imageView = (ImageView) holder.relativeLayout.findViewById(R.id.imagethumb);
        txtCategoryName.setText(categoryButtonModelList.get(position).getCategoryName());
        txtCategoryName.setTextColor(Color.parseColor(uiSettingsModel.getAppButtonBgColor()));
        txtCategoryName.setVisibility(View.VISIBLE);
        String categoryThumbnail = appUserModel.getSiteURL() + categoryButtonModelList.get(position).getCategoryIcon();

        Glide.with(holder.itemView.getContext()).load(categoryThumbnail).placeholder(R.drawable.cellimage).into(imageView);

        // Log.d("CAT", "onBindViewHolder: " + categoryThumbnail);
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
