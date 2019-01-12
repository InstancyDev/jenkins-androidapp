package com.instancy.instancylearning.myskills;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.instancy.instancylearning.R;
import com.instancy.instancylearning.catalogfragment.CatalogFragmentActivity;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.helper.IResult;
import com.instancy.instancylearning.helper.VollyService;
import com.instancy.instancylearning.models.AppUserModel;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.mycompetency.CompetencyCategoryModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.Utilities.isNetworkConnectionAvailable;


public class MySkillAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<MySkillModel> skillModelList;
    Typeface iconFon;
    String jobTagId = "";
    AppUserModel appUserModel = null;
    SideMenusModel sideMenusModel = null;
    UiSettingsModel uiSettingsModel;
    List<String> scoreList;
    VollyService vollyService;
    IResult resultCallback = null;
    JSONArray requiredScore = null;

    ExpandableListView expandableListView;

    public void refreshList(List<MySkillModel> skillModelList, JSONArray requiredScore) {
        this.skillModelList = skillModelList;
        this.notifyDataSetChanged();
        iconFon = FontManager.getTypeface(ctx, FontManager.FONTAWESOME);
        this.requiredScore = requiredScore;

    }

    // Up
    public MySkillAdapter(Context ctx, List<MySkillModel> mainMenuList, String jobTagId, AppUserModel appUserModel, SideMenusModel sideMenusModel, ExpandableListView expandableListView) {

        this.ctx = ctx;
        this.appUserModel = appUserModel;
        this.sideMenusModel = sideMenusModel;
        this.jobTagId = jobTagId;
        uiSettingsModel = UiSettingsModel.getInstance();
        scoreList = new ArrayList<>();
        this.expandableListView = expandableListView;
        vollyService = new VollyService(resultCallback, ctx);
    }

    @Override
    public int getGroupCount() {
        if (skillModelList != null) {
            return skillModelList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int parentPosition) {
        if (skillModelList != null) {
            if (skillModelList.get(parentPosition).skillCountModelList != null) {
                return skillModelList.get(parentPosition).skillCountModelList.size();
            } else {
                return 0;
            }

        } else {
            return 0;
        }
    }

    @Override
    public MySkillModel getGroup(int parentPosition) {
        if (skillModelList != null) {
            return skillModelList.get(parentPosition);
        } else {
            return null;
        }
    }

    @Override
    public SideMenusModel getChild(int parentPosition, int childPosition) {
        if (skillModelList != null && skillModelList.get(parentPosition).skillCountModelList != null) {

            return null;
//            }
        } else {
            return null;
        }
    }

    @Override
    public long getGroupId(int parentPosition) {
        if (skillModelList != null) {
            return parentPosition;
        } else {
            return 0;
        }
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        if (skillModelList != null) {
            return childPosition;
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parentPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView) {
        MySkillModel mainMenu = getGroup(parentPosition);

        View pView = convertView;

        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.competencyjobcell, parentView, false);
//            pView.setBackgroundColor(ctx.getResources().getColor(R.color.colorGray));
        }
        TextView expTxtIcon = (TextView) pView.findViewById(R.id.expIcon);
        TextView txtTitle = (TextView) pView.findViewById(R.id.jobrolename);
        txtTitle.setText(mainMenu.skillName);
        txtTitle.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
        expTxtIcon.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        if (isExpanded) {
            expTxtIcon.setVisibility(View.VISIBLE);
            expTxtIcon.setText(pView.getResources().getString(R.string.fa_icon_angle_up));
        } else {
            expTxtIcon.setVisibility(View.VISIBLE);
            expTxtIcon.setText(pView.getResources().getString(R.string.fa_icon_angle_down));
        }

        FontManager.markAsIconContainer(pView.findViewById(R.id.fontawasomeIcon), iconFon);
        FontManager.markAsIconContainer(pView.findViewById(R.id.expIcon), iconFon);


        return pView;
    }

    @Override
    public View getChildView(final int parentPosition, final int childPosition,
                             boolean isLastChild, View cView, ViewGroup parentView) {

        final ViewHolder holder;
        final SkillCountModel childSkillModel = skillModelList.get(parentPosition).skillCountModelList.get(childPosition);
        if (cView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = inflater.inflate(R.layout.myskillcell, parentView, false);
            holder = new ViewHolder(cView);
            cView.setTag(holder);
        } else {
            holder = (ViewHolder) cView.getTag();
        }
        holder.parent = parentView;
        holder.getPosition = childPosition;
        holder.getGroupPosition = parentPosition;

        holder.txtSkill.setText(childSkillModel.porefCatName);

        holder.spnrScore.setAdapter(holder.getAdapter(childPosition));

        setSpinText(holder.spnrScore, childSkillModel.expertLevel);


        holder.spnrScore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                skillSpinnerIsSelected(holder.spnrScore, parentPosition, childPosition);
                return false;
            }
        });

        return cView;
    }

    public void skillSpinnerIsSelected(Spinner spnrScore, final int groupPosition, final int childPosition) {

        spnrScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int spnrPosition,
                                       long id) {

                Log.d("TAG", "skillCategorySkillOptionHasChanged: scoreList =" + scoreList.get(spnrPosition) + "  groupPosition= " + groupPosition + " childPosition =" + childPosition);

                try {
                    SkillCountModel childSkillModel = skillModelList.get(groupPosition).skillCountModelList.get(childPosition);
                    submitSkillData(scoreList.get(spnrPosition), childSkillModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    public int idFromString(String expertLevelStr) {
        int expertLevel = 0;

        if (scoreList != null && scoreList.size() != 0 && requiredScore != null) {
            for (int i = 0; i < requiredScore.length(); i++) {
                JSONObject columnObj = null;
                try {
                    columnObj = requiredScore.getJSONObject(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    if (expertLevelStr.equalsIgnoreCase(columnObj.getString("LabelDescription"))) {

                        expertLevel = columnObj.getInt("LabelID");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return expertLevel;
    }

    public void submitSkillData(String skillString, SkillCountModel childSkillModel) throws JSONException {


        int intPrefId = idFromString(skillString);

        JSONObject parameters = new JSONObject();

        parameters.put("PreferrenceID", childSkillModel.prefCatID);
        parameters.put("ExpertLevel", intPrefId);
        parameters.put("UserID", appUserModel.getUserIDValue());


        String parameterString = parameters.toString();

        Log.d("CMP", "onResponse: " + parameterString);

        sendNewSaveprogressitemToServer(parameterString);
    }


    public void sendNewSaveprogressitemToServer(final String postData) {
        String apiURL = "";

        apiURL = appUserModel.getWebAPIUrl() + "/MySkills/Saveprogressitem";

        final StringRequest request = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.d("CMP", "onResponse: " + s);

                if (s != null && s.length() > 0) {
                    try {

                        if (s.contains("true")) {

                        }

                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + s + "\"");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ctx, "Some error occurred -> " + volleyError, Toast.LENGTH_LONG).show();

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/json";

            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                String base64EncodedCredentials = Base64.encodeToString(appUserModel.getAuthHeaders().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

        };

        RequestQueue rQueue = Volley.newRequestQueue(ctx);
        rQueue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void setSpinText(Spinner spin, int expertLevelId) {


        if (requiredScore != null && requiredScore.length() > 0) {
            scoreList = new ArrayList<>();

            for (int i = 0; i < requiredScore.length(); i++) {
                JSONObject columnObj = null;
                try {
                    columnObj = requiredScore.getJSONObject(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    scoreList.add(columnObj.getString("LabelDescription"));
                    Log.d("CTX", "getAdapter: " + requiredScore.getString(i));

                    if (expertLevelId == columnObj.getInt("LabelID")) {
                        spin.setSelection(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class ViewHolder {

        private ArrayAdapter<String> spinnerAdapter;
        CompetencyCategoryModel categoryModel;
        public int getPosition, getGroupPosition;
        public ViewGroup parent;

        private int selected;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

        public ArrayAdapter<String> getAdapter(int position) {

            if (requiredScore != null && requiredScore.length() > 0) {
                scoreList = new ArrayList<>();

                for (int i = 0; i < requiredScore.length(); i++) {
                    JSONObject columnObj = null;
                    try {
                        columnObj = requiredScore.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        scoreList.add(columnObj.getString("LabelDescription"));
                        Log.d("CTX", "getAdapter: " + requiredScore.getString(i));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                spinnerAdapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, scoreList);

            }

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
        @BindView(R.id.txtSkill)
        TextView txtSkill;

        @Nullable
        @BindView(R.id.txtself)
        TextView txtself;


        @Nullable
        @BindView(R.id.lbSkill)
        TextView lbSkill;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;


        @Nullable
        @BindView(R.id.spnrScore)
        Spinner spnrScore;


        @OnClick({R.id.btn_contextmenu})
        public void actionsForMenu(View view) {
//            SkillCountModel childSkillModel = skillModelList.get(getGroupPosition).skillCountModelList.get(getPosition);
//            mySkillContextMenu(view, getPosition, childSkillModel);

            long packedPos = ExpandableListView.getPackedPositionForChild(getGroupPosition, getPosition);
            int flatPos = expandableListView.getFlatListPosition(packedPos);

//Getting the ID for our child
            long id = expandableListView.getExpandableListAdapter().getChildId(getGroupPosition, getPosition);

            ((ExpandableListView) parent).performItemClick(view, flatPos, id);
        }

    }

}
