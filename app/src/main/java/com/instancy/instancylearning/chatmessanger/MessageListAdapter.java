package com.instancy.instancylearning.chatmessanger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Upendranath on 1/17/2018.
 */

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<BaseMessage> mMessageList;
    AppUserModel appUserModel;
    UiSettingsModel uiSettingsModel;

    public MessageListAdapter(Context context, List<BaseMessage> messageList) {
        this.mContext = context;
        this.mMessageList = messageList;
        appUserModel = AppUserModel.getInstance();
        uiSettingsModel = UiSettingsModel.getInstance();
    }

    public void reloadAllContent(List<BaseMessage> dataset) {
        mMessageList.clear();
        mMessageList.addAll(dataset);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);

        if (message.itsMe) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = (BaseMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
//            messageText.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppHeaderColor()));

            Drawable tvBackground = (Drawable) messageText.getBackground();
            tvBackground.setColorFilter(Color.parseColor(uiSettingsModel.getAppHeaderColor()), PorterDuff.Mode.SRC_ATOP);
        }

        void bind(BaseMessage message) {
            messageText.setText(message.messageChat);

            // Format the stored timestamp into a readable String using method.
//            timeText.setText(DateUtils.formatDateTime(message.fromDate));
            timeText.setText("Today");

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(BaseMessage message) {
            messageText.setText(message.messageChat);

            // Format the stored timestamp into a readable String using method.
//            timeText.setText(DateUtils.formatDateTime(message.getCreatedAt()));

            nameText.setText(message.fromUserName);

            // Insert the profile image from the URL into the ImageView.
//            Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
            String profileIma = appUserModel.getSiteURL() + message.profilePic;

            Picasso.with(itemView.getContext()).load(profileIma).placeholder(itemView.getResources().getDrawable(R.drawable.defaulttechguy)).into(profileImage);
        }
    }


}