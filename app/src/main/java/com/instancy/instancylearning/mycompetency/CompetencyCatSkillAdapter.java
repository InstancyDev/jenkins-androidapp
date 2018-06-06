package com.instancy.instancylearning.mycompetency;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

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
import com.instancy.instancylearning.utils.StaticValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.instancy.instancylearning.utils.StaticValues.MAIN_MENU_POSITION;

public class CompetencyCatSkillAdapter extends BaseExpandableListAdapter {
    Context ctx;
    private List<CompetencyCategoryModel> mainMenuList;
    private HashMap<Integer, List<CompetencyCategoryModel>> subMenuList;
    private List<SkillModel> skillModelList;


    UiSettingsModel uiSettingsModel;

    public void refreshList(List<CompetencyCategoryModel> mainMenuList, List<SkillModel> skillModelList) {
        this.mainMenuList = mainMenuList;
        this.skillModelList = skillModelList;
        this.notifyDataSetChanged();
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

        TextView txtTitle = (TextView) pView.findViewById(R.id.jobrolename);
        txtTitle.setText(mainMenu.prefCategoryTitle);
        return pView;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parentView) {
//        SideMenusModel childMenu = getChild(parentPosition, childPosition);

        View cView = convertView;
        if (cView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cView = inflater.inflate(R.layout.competencyskillpiechartcell, parentView, false);
        }

        TextView txtTitle = (TextView) cView.findViewById(R.id.jobrolename);

        TextView jobDescription = (TextView) cView.findViewById(R.id.jobdescription);


        PieChart mChart = (PieChart) cView.findViewById(R.id.piechart);


        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterText("Pie chart");

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(ctx.getResources().getColor(R.color.piechartcircle));

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(255);

        mChart.setHoleRadius(44f);
        mChart.setTransparentCircleRadius(54f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener

        setData(2, 100, mChart);


        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);

        mChart.setEntryLabelTextSize(12f);
        txtTitle.setText(skillModelList.get(childPosition).skillName);

        jobDescription.setText(skillModelList.get(childPosition).skillDescription);


        return cView;
    }

    private void setData(int count, float range, PieChart pieChart) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        String[] mParties = new String[]{
                " A", " B", " C"
        };

        for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length],
                    ctx.getResources().getDrawable(R.drawable.edit_round_drawable)));
        }


        PieDataSet dataSet = new PieDataSet(entries, "");
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


//    @Override
//    public boolean onChildClick(ExpandableListView parent, View v,
//                                int groupPosition, int childPosition, long id) {
//
//        int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
//        parent.setItemChecked(index, true);
//
//
//        return true;
//    }

}
