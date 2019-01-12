package com.instancy.instancylearning.chatmessanger;

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
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColor;
import static com.instancy.instancylearning.utils.Utilities.getDrawableFromStringWithColorWithSize;
import static com.instancy.instancylearning.utils.Utilities.getFirstCaseWords;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class SendMessageAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ChatListModel> chatListModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    private String TAG = SendMessageAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<ChatListModel> searchList;


    public SendMessageAdapter(Activity activity, int resource, List<ChatListModel> chatListModelList) {
        this.activity = activity;
        this.chatListModelList = chatListModelList;
        this.searchList = new ArrayList<ChatListModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        appUserModel = AppUserModel.getInstance();

    }

    public void refreshList(List<ChatListModel> peopleListingModelList) {
        this.chatListModelList = peopleListingModelList;
        this.searchList = new ArrayList<ChatListModel>();
        this.searchList.addAll(peopleListingModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatListModelList != null ? chatListModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return chatListModelList.get(position);
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
        convertView = inflater.inflate(R.layout.sendmessageencell, parent, false);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtName.setText(chatListModelList.get(position).fullName);
//        holder.txtRole.setText(chatListModelList.get(position).connectionStatus);
        holder.txtRole.setText(chatListModelList.get(position).role);
        holder.txtPlace.setText(chatListModelList.get(position).country);

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtRole.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtPlace.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        String imagePath = chatListModelList.get(position).profPic;
        int unreadCount = chatListModelList.get(position).unReadCount;


        if (unreadCount == 0) {
            holder.unreadCountTxt.setVisibility(View.GONE);
        } else {
            holder.unreadCountTxt.setVisibility(View.VISIBLE);
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color 14/ 28400
//        int color1 = generator.getRandomColor();
// generate color based on a key (same key returns the same color), useful for list/grid views
            int color = generator.getColor(unreadCount);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("" + unreadCount, color);
            holder.unreadCountTxt.setBackground(drawable);
        }
        String displayNameDrawable = getFirstCaseWords(chatListModelList.get(position).fullName);

        if (imagePath.length() > 2) {
            String imgUrl = appUserModel.getSiteURL() + chatListModelList.get(position).profPic;

            Picasso.with(convertView.getContext()).load(imgUrl).placeholder(convertView.getResources().getDrawable(R.drawable.defaulttechguy)).into(holder.imgThumb);

        } else {

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color 14/ 28400
//        int color1 = generator.getRandomColor();
// generate color based on a key (same key returns the same color), useful for list/grid views
            int color = generator.getColor(displayNameDrawable);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(displayNameDrawable, color);
            holder.imgThumb.setBackground(drawable);
        }

        if (chatListModelList.get(position).connectionStatus == 1) {
            holder.statusImg.setBackground(getDrawableFromStringWithColorWithSize(convertView.getContext(), R.string.fa_icon_circle, "#008000"));
        } else {
            holder.statusImg.setBackground(getDrawableFromStringWithColorWithSize(convertView.getContext(), R.string.fa_icon_circle_o,"#008000"));
        }

        return convertView;
    }

    public void filter(String charText) {
//        charText = charText.toLowerCase(Locale.getDefault());
//        peopleListingModelList.clear();
//        if (charText.length() == 0) {
//            peopleListingModelList.addAll(searchList);
//        } else {
//            for (PeopleListingModel s : searchList) {
//                if (s.userDisplayname.toLowerCase(Locale.getDefault()).contains(charText) || s.chatUserStatus.toLowerCase(Locale.getDefault()).contains(charText)) {
//                    peopleListingModelList.add(s);
//                }
//            }
//        }
//        notifyDataSetChanged();
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
        @BindView(R.id.statusImg)
        ImageView statusImg;

        @Nullable
        @BindView(R.id.txtPeople)
        TextView txtName;

        @Nullable
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.txtRole)
        TextView txtRole;

        @Nullable
        @BindView(R.id.unreadCountTxt)
        ImageView unreadCountTxt;

        @Nullable
        @BindView(R.id.txtPlace)
        TextView txtPlace;

        @OnClick({R.id.card_view})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }
}


