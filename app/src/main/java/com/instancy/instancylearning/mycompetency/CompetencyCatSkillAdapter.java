package com.instancy.instancylearning.mycompetency;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.instancy.instancylearning.R;
import com.instancy.instancylearning.helper.FontManager;
import com.instancy.instancylearning.models.SideMenusModel;
import com.instancy.instancylearning.models.UiSettingsModel;
import com.instancy.instancylearning.nativesignup.NativeSignupAdapter;
import com.instancy.instancylearning.utils.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;

public class CompetencyCatSkillAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<CompetencyCategoryModel> mainMenuList;
    private HashMap<Integer, List<CompetencyCategoryModel>> subMenuList;
    private List<SkillModel> skillModelList;
    Typeface iconFon;

    UiSettingsModel uiSettingsModel;
    List<String> scoreList;

    public void refreshList(List<CompetencyCategoryModel> mainMenuList, List<SkillModel> skillModelList) {
        this.mainMenuList = mainMenuList;
        this.skillModelList = skillModelList;
        this.notifyDataSetChanged();
        iconFon = FontManager.getTypeface(ctx, FontManager.FONTAWESOME);
    }

    public HashMap<Integer, List<CompetencyCategoryModel>> getSubMenuList() {
        return subMenuList;
    }


    public CompetencyCatSkillAdapter(Context ctx, int resource,
                                     List<CompetencyCategoryModel> mainMenuList) {

        this.ctx = ctx;
        this.subMenuList = subMenuList;
        this.mainMenuList = mainMenuList;

        uiSettingsModel = UiSettingsModel.getInstance();
        scoreList = new ArrayList<>();

    }

    @Override
    public int getGroupCount() {
        if (mainMenuList != null) {
            return mainMenuList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getChildrenCount(int parentPosition) {
        if (mainMenuList != null && skillModelList != null) {
//            List<SideMenusModel> subMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
            if (skillModelList != null) {
                return skillModelList.size();
            } else {
                return 0;
            }

        } else {
            return 0;
        }
    }

    @Override
    public CompetencyCategoryModel getGroup(int parentPosition) {
        if (mainMenuList != null) {
            return mainMenuList.get(parentPosition);
        } else {
            return null;
        }
    }

    @Override
    public SideMenusModel getChild(int parentPosition, int childPosition) {
        if (mainMenuList != null && subMenuList != null) {
//            List<SideMenusModel> childMenus = subMenuList.get(mainMenuList.get(parentPosition).getMenuId());
//            if (childMenus != null) {
//                return childMenus.get(childPosition);
//            } else {
            return null;
//            }
        } else {
            return null;
        }
    }

    @Override
    public long getGroupId(int parentPosition) {
        if (mainMenuList != null) {
            return parentPosition;
        } else {
            return 0;
        }
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        if (subMenuList != null) {
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
        CompetencyCategoryModel mainMenu = getGroup(parentPosition);

        View pView = convertView;
        if (pView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pView = inflater.inflate(R.layout.competencyjobcell, parentView, false);
        }
        TextView expTxtIcon = (TextView) pView.findViewById(R.id.expIcon);
        TextView txtTitle = (TextView) pView.findViewById(R.id.jobrolename);
        txtTitle.setText(mainMenu.prefCategoryTitle);

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
    public View getChildView(int parentPosition, final int childPosition,
                             boolean isLastChild, View cView, ViewGroup parentView) {

        final ViewHolder holder;

        if (cView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = inflater.inflate(R.layout.competencyskillpiechartcell, parentView, false);
            holder = new ViewHolder(cView);
            cView.setTag(holder);
        } else {
            holder = (ViewHolder) cView.getTag();
        }
        holder.parent = parentView;
        holder.getPosition = childPosition;
//        holder.txtJobRole.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));
//        holder.txtJobRole.setTextColor(Color.parseColor(uiSettingsModel.getAppTextColor()));

        holder.mChart.setUsePercentValues(true);
        holder.mChart.getDescription().setEnabled(false);
        holder.mChart.setExtraOffsets(5, 10, 5, 5);
        holder.mChart.setDragDecelerationFrictionCoef(0.95f);
        holder.mChart.setCenterText("" + skillModelList.get(childPosition).requiredProficiency);
        holder.mChart.setDrawHoleEnabled(true);
        holder.mChart.setHoleColor(ctx.getResources().getColor(R.color.piechartcircle));
        holder.mChart.setTransparentCircleColor(Color.WHITE);
        holder.mChart.setTransparentCircleAlpha(255);
        holder.mChart.setHoleRadius(44f);
        holder.mChart.setTransparentCircleRadius(54f);
        holder.mChart.setDrawCenterText(true);
        holder.mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        holder.mChart.setRotationEnabled(true);
        holder.mChart.setHighlightPerTapEnabled(true);
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener

        setData(2, 5, holder.mChart, skillModelList.get(childPosition));

        // mChart.spin(2000, 0, 360);

        Legend l = holder.mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        holder.mChart.setEntryLabelColor(Color.WHITE);

        holder.mChart.setEntryLabelTextSize(12f);
        holder.txtJobRole.setText(skillModelList.get(childPosition).skillName);

        holder.txtjobDescription.setText(skillModelList.get(childPosition).skillDescription);

        holder.txtAvgScore.setText("" + skillModelList.get(childPosition).weightedAverage);
        holder.txtContentScore.setText("" + skillModelList.get(childPosition).contentAuthorScore);
        holder.txtManagerScore.setText("" + skillModelList.get(childPosition).managerScore);

        holder.spnrScore.setAdapter(holder.getAdapter(childPosition));

        setSpinText(holder.spnrScore, skillModelList.get(childPosition).valueName);

//            profileConfigsModelList.get(position).valueName = holder.getText();

        holder.spnrScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spnrPosition,
                                       long id) {

                skillModelList.get(childPosition).valueName = scoreList.get(spnrPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        return cView;
    }

    private void setData(int count, float range, PieChart pieChart, SkillModel skillModel) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        String[] mParties = new String[]{
                "Gap " + skillModel.gapScore, "Average " + skillModel.weightedAverage,
        };

//        for (int i = 0; i < count; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
//                    mParties[i]));
//        }

        for (int i = 0; i < count; i++) {
            if (i == 0) {
                entries.add(new PieEntry((float) (skillModel.gapScore),
                        mParties[i]));
            } else {
                entries.add(new PieEntry((float) (skillModel.weightedAverage),
                        mParties[i]));

            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "" + skillModel.requiredProficiency);
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(ctx.getResources().getColor(R.color.piechartred));

        colors.add(ctx.getResources().getColor(R.color.piechartgreen));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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

            JSONArray requiredScore = skillModelList.get(position).requiredProfArys;

            if (requiredScore != null && requiredScore.length() > 0) {
                scoreList = new ArrayList<>();
                for (int i = 0; i < requiredScore.length(); i++) {

                    try {
                        scoreList.add(requiredScore.getString(i));
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
        @BindView(R.id.jobrolename)
        TextView txtJobRole;

        @Nullable
        @BindView(R.id.jobdescription)
        TextView txtjobDescription;

        @Nullable
        @BindView(R.id.txtMgScore)
        TextView txtManagerScore;

        @Nullable
        @BindView(R.id.txtCntScore)
        TextView txtContentScore;

        @Nullable
        @BindView(R.id.txtAvgScore)
        TextView txtAvgScore;

        @Nullable
        @BindView(R.id.btn_contextmenu)
        ImageButton btnContextMenu;

        @Nullable
        @BindView(R.id.piechart)
        PieChart mChart;


        @Nullable
        @BindView(R.id.spnrScore)
        Spinner spnrScore;


        @OnClick({R.id.btn_contextmenu})
        public void actionsForMenu(View view) {

            mycompetencyContextMenuMethod(view, btnContextMenu, getPosition);
        }

    }

    public static void mycompetencyContextMenuMethod(final View v, ImageButton btnselected, final int position) {

        PopupMenu popup = new PopupMenu(v.getContext(), btnselected);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.mycompetencymenu, popup.getMenu());
        //registering popup with OnMenuItemClickListene

        Menu menu = popup.getMenu();

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle().toString().equalsIgnoreCase("View Content")) {

                    Toast.makeText(v.getContext(), "clicked " + position, Toast.LENGTH_SHORT).show();
                }

                if (item.getTitle().toString().equalsIgnoreCase("Save")) {

                    Toast.makeText(v.getContext(), "clicked Save " + position, Toast.LENGTH_SHORT).show();

                }


                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }

}
