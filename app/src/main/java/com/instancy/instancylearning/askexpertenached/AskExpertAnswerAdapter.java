package com.instancy.instancylearning.askexpertenached;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertAnswerModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.PreferencesManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.globalpackage.GlobalMethods.createBitmapFromView;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class AskExpertAnswerAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<AskExpertAnswerModel> askExpertAnswerModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = AskExpertAnswerAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<AskExpertAnswerModel> searchList;
    AppController appcontroller;


    public AskExpertAnswerAdapter(Activity activity, int resource, List<AskExpertAnswerModel> askExpertAnswerModelList) {
        this.activity = activity;
        this.askExpertAnswerModelList = askExpertAnswerModelList;
        this.searchList = new ArrayList<AskExpertAnswerModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();

        appcontroller = AppController.getInstance();

    }

    public void refreshList(List<AskExpertAnswerModel> discussionTopicModelList) {
        this.askExpertAnswerModelList = discussionTopicModelList;
        this.searchList = new ArrayList<AskExpertAnswerModel>();
        this.searchList.addAll(askExpertAnswerModelList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return askExpertAnswerModelList != null ? askExpertAnswerModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return askExpertAnswerModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.askexpertsanswerscell_en, null);
        holder = new ViewHolder(convertView);
        holder.parent = parent;
        holder.getPosition = position;
        holder.card_view.setBackgroundColor(Color.parseColor(uiSettingsModel.getAppBGColor()));

        holder.txtName.setText(askExpertAnswerModelList.get(position).respondedusername);
        holder.txtAnsweredOn.setText("Answered on: " + askExpertAnswerModelList.get(position).responsedate);
        holder.txtMessage.setText(askExpertAnswerModelList.get(position).response + " ");

        holder.txtName.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAnsweredOn.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtMessage.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (askExpertAnswerModelList.get(position).userID.equalsIgnoreCase(askExpertAnswerModelList.get(position).respondeduserid)) {
            holder.btnContextMenu.setVisibility(View.VISIBLE);
        } else {
            holder.btnContextMenu.setVisibility(View.INVISIBLE);
        }

        assert holder.txtUpvote != null;
        assert holder.txtUpvote != null;
        holder.txtUpvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_up), null, null, null);

        assert holder.txtDownnvote != null;
        holder.txtDownnvote.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_thumbs_o_down), null, null, null);

        assert holder.txtComment != null;
        holder.txtComment.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_comments), null, null, null);

//        assert holder.txtComments != null;
//        holder.txtComments.setCompoundDrawablesWithIntrinsicBounds(getDrawableFromString(convertView.getContext(), R.string.fa_icon_question), null, null, null);


        String imgUrl = appUserModel.getSiteURL() + askExpertAnswerModelList.get(position).imageData;
        Picasso.with(convertView.getContext()).load(imgUrl).placeholder(R.drawable.user_placeholder).into(holder.imgThumb);

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        askExpertAnswerModelList.clear();
        if (charText.length() == 0) {
            askExpertAnswerModelList.addAll(searchList);
        } else {
            for (AskExpertAnswerModel s : searchList) {
                if (s.response.toLowerCase(Locale.getDefault()).contains(charText) || s.respondedusername.toLowerCase(Locale.getDefault()).contains(charText)) {
                    askExpertAnswerModelList.add(s);
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
        @BindView(R.id.card_view)
        CardView card_view;

        @Nullable
        @BindView(R.id.imagethumb)
        ImageView imgThumb;

        @Nullable
        @BindView(R.id.txt_name)
        TextView txtName;

        @Nullable
        @BindView(R.id.txtansweredon)
        TextView txtAnsweredOn;

        @Nullable
        @BindView(R.id.txtmessage)
        TextView txtMessage;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.txtUpvote)
        TextView txtUpvote;

        @Nullable
        @BindView(R.id.txtDownnvote)
        TextView txtDownnvote;

        @Nullable
        @BindView(R.id.txtComment)
        TextView txtComment;

        @Nullable
        @BindView(R.id.txtComments)
        TextView txtComments;

        @Nullable
        @BindView(R.id.txtTotalUpvoters)
        TextView txtTotalUpvoters;


        @OnClick({R.id.btn_contextmenu, R.id.card_view, R.id.txtTotalUpvoters, R.id.txtComment, R.id.txtComments})
        public void actionsForMenu(View view) {

            ((ListView) parent).performItemClick(view, getPosition, 0);

        }

    }

    @SuppressLint("ResourceAsColor")
    public Drawable getDrawableFromString(Context context, int resourceID) {

        Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        View customNav = LayoutInflater.from(context).inflate(R.layout.iconimage, null);
        TextView iconText = (TextView) customNav.findViewById(R.id.imageicon);
        iconText.setTextColor(R.color.colorDarkGrey);
        iconText.setText(resourceID);
        FontManager.markAsIconContainer(customNav.findViewById(R.id.imageicon), iconFont);
        Drawable d = new BitmapDrawable(context.getResources(), createBitmapFromView(context, customNav));

        return d;
    }

}


