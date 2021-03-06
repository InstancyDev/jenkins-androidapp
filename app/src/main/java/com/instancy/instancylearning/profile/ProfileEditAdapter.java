package com.instancy.instancylearning.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.databaseutils.DatabaseHandler;
import com.instancy.instancylearning.globalpackage.AppController;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.AskExpertQuestionModel;
import com.instancy.instancylearning.models.ProfileConfigsModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.utils.CustomSpinner;
import com.instancy.instancylearning.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.blankj.utilcode.util.Utils.getContext;
import static com.instancy.instancylearning.utils.Utilities.getMonthFromint;

/**
 * Created by Upendranath on 6/20/2017 Working on InstancyLearning.
 */

public class ProfileEditAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ProfileConfigsModel> profileConfigsModelList = null;
    private int resource;
    private UiSettingsModel uiSettingsModel;
    AppUserModel appUserModel;
    SVProgressHUD svProgressHUD;
    DatabaseHandler db;
    PreferencesManager preferencesManager;
    private String TAG = ProfileEditAdapter.class.getSimpleName();
    private int MY_SOCKET_TIMEOUT_MS = 5000;
    private List<ProfileConfigsModel> searchList;

    ArrayAdapter titleAdapter;

    List<String> countriesList;


    public ProfileEditAdapter(Activity activity, int resource, List<ProfileConfigsModel> profileConfigsModelList) {
        this.activity = activity;

        this.profileConfigsModelList = profileConfigsModelList;
        this.searchList = new ArrayList<ProfileConfigsModel>();
        this.resource = resource;
        this.notifyDataSetChanged();
        uiSettingsModel = UiSettingsModel.getInstance();
        appUserModel = AppUserModel.getInstance();
        svProgressHUD = new SVProgressHUD(activity);
        db = new DatabaseHandler(activity);
        preferencesManager = PreferencesManager.getInstance();
        appUserModel = AppUserModel.getInstance();
        countriesList = new ArrayList<>();
    }

    public void refreshList(List<ProfileConfigsModel> profileConfigsModelList) {
        this.profileConfigsModelList = profileConfigsModelList;
        this.searchList = new ArrayList<ProfileConfigsModel>();
        this.searchList.addAll(profileConfigsModelList);

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return profileConfigsModelList != null ? profileConfigsModelList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return profileConfigsModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.personaleditcell, null);
            holder = new ViewHolder(convertView);
            holder.parent = parent;
            holder.getPosition = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.labelField.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.txtAstrek.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.labelField.setText(profileConfigsModelList.get(position).attributedisplaytext);
        holder.edit_field.setText(profileConfigsModelList.get(position).valueName);
        holder.edit_field.setMaxLines(returnLines(profileConfigsModelList.get(position).names));

        holder.edit_field.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        holder.edit_field.setHintTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (profileConfigsModelList.get(position).iseditable.contains("false")) {

            holder.edit_field.setEnabled(false);
            holder.spnrCountries.setEnabled(false);
            holder.txtDobClick.setEnabled(false);
        }

        if (returnHide(profileConfigsModelList.get(position).names) == 1) {
            holder.edit_field.setText(profileConfigsModelList.get(position).valueName);
            holder.edit_field.setFilters(new InputFilter[]{new InputFilter.LengthFilter(profileConfigsModelList.get(position).maxLenth)});
            holder.txtDobClick.setVisibility(View.GONE);
            holder.edit_field.setVisibility(View.VISIBLE);
            holder.spnrCountries.setVisibility(View.GONE);

        } else if (returnHide(profileConfigsModelList.get(position).names) == 3) {

            holder.txtDobClick.setVisibility(View.GONE);
            holder.edit_field.setVisibility(View.GONE);
            holder.spnrCountries.setVisibility(View.VISIBLE);

            // attaching data adapter to spinner

            holder.spnrCountries.setAdapter(holder.getAdapter(position));

            setSpinText(holder.spnrCountries, profileConfigsModelList.get(position).valueName);

//            profileConfigsModelList.get(position).valueName = holder.getText();

            holder.spnrCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spnrPosition,
                                           long id) {

                    if (profileConfigsModelList.get(position).attributeconfigid.equalsIgnoreCase("1030")) {

                        if (holder.getText().toLowerCase().contains("fe")) {

                            holder.setSelected(spnrPosition);
                            profileConfigsModelList.get(position).valueName = "1";
                        } else {
                            holder.setSelected(spnrPosition);
                            profileConfigsModelList.get(position).valueName = "0";
                        }

                    } else {

                        holder.setSelected(spnrPosition);
                        profileConfigsModelList.get(position).valueName = holder.getText();
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });

//            titleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, degreeTitleList);
//
//            spinnerDgreType.setAdapter(titleAdapter);

        } else {
            holder.txtDobClick.setText(profileConfigsModelList.get(position).valueName);
            holder.edit_field.setVisibility(View.GONE);
            holder.txtDobClick.setVisibility(View.VISIBLE);
            holder.spnrCountries.setVisibility(View.GONE);
            holder.txtDobClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (returnHide(profileConfigsModelList.get(position).names) == 2) {
                        int mYear, mMonth, mDay;
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {


//                                        profileConfigsModelList.get(position).valueName = "" + year + "-" + getMonthFromint(monthOfYear + 1) + "-" + dayOfMonth;
                                        profileConfigsModelList.get(position).valueName = monthOfYear + 1 + "/" + dayOfMonth + "/" + year;

                                        holder.txtDobClick.setText(profileConfigsModelList.get(position).valueName);

                                    }
                                }, mYear, mMonth, mDay);
                        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); completed dates
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();

                    } else {

                        Toast.makeText(activity, "country list api", Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }
        if (profileConfigsModelList.get(position).isrequired.contains("true")) {
            holder.txtAstrek.setText("*");
            holder.txtAstrek.setTextColor(convertView.getResources().getColor(R.color.colorRed));
        } else {
            holder.txtAstrek.setText("");
        }

        holder.edit_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    final EditText Caption = (EditText) v;

                    profileConfigsModelList.get(position).valueName = Caption.getText().toString();

                }
            }
        });

        return convertView;
    }

    class ViewHolder {

        private ArrayAdapter<String> spinnerAdapter;

        public int getPosition;
        public ViewGroup parent;

        private int selected;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);


        }

        public ArrayAdapter<String> getAdapter(int position) {
            countriesList = db.fetchCountriesName(appUserModel.getSiteIDValue(), profileConfigsModelList.get(position).attributeconfigid);
            spinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, countriesList);
            return spinnerAdapter;
        }


        public String getText() {
            return (String) spinnerAdapter.getItem(selected);
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }


        @Nullable
        @BindView(R.id.labelField)
        TextView labelField;

        @Nullable
        @BindView(R.id.astrekd)
        TextView txtAstrek;

        @Nullable
        @BindView(R.id.editField)
        EditText edit_field;

        @Nullable
        @BindView(R.id.txtdobclick)
        TextView txtDobClick;


        @Nullable
        @BindView(R.id.spnrCountries)
        Spinner spnrCountries;


    }

    public int returnLines(String linesRequired) {
        int returnValue = 3;
        switch (linesRequired) {
            case "SingleLineTextField":
                returnValue = 3;
                break;
            case "EmailTextField":
                returnValue = 3;
                break;
            case "MultiLineTextField":
                returnValue = 100;
                break;
        }
        return returnValue;
    }


    public void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().toLowerCase().equalsIgnoreCase(text)) {
                spin.setSelection(i);
            }
        }

    }

    public int returnHide(String whichKeyboard) {
        int returnValue = 1;
        switch (whichKeyboard) {
            case "SingleLineTextField":
            case "EmailTextField":
            case "MultiLineTextField":
                returnValue = 1;
                break;
            case "DatePicker":
                returnValue = 2;
                break;
            case "DropDownList":
            case "OptionButtonList":
                returnValue = 3;
                break;

        }
        return returnValue;
    }

}


