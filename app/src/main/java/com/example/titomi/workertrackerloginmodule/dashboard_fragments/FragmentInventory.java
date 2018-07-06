package com.example.titomi.workertrackerloginmodule.dashboard_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.shared_pref_manager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Titomi on 2/8/2018.
 */

public class FragmentInventory extends Fragment implements OnChartValueSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    protected RectF mOnValueSelectedRectF = new RectF();
    View view;
    Context cxt;
    BarChart barInventoryChart;
    ArrayList<BarEntry> yVals = new ArrayList<>();
    float itemQuantity = 0;
    float itemQuantitySold = 0;
    float itemBalance = 0;
    private User loggedInUser;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshLayout;

    public FragmentInventory() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        cxt = getActivity();
        view = inflater.inflate(R.layout.inventory_fragment, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }

        pb = view.findViewById(R.id.progressBar);
        barInventoryChart= view.findViewById(R.id.barChartInvt);
        refreshLayout = view.findViewById(R.id.swipe_to_refresh_inventory);
        refreshLayout.setOnRefreshListener(this);

        barInventoryChart.getDescription().setEnabled(false);
        barInventoryChart.setOnChartValueSelectedListener(this);
        loadInventory();
        return view;
    }

    private void loadInventory() {

        String url = "";
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                url = getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?view=user_stock_details&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
            case User.NURSE:
                url = getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?view=user_stock_details&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
        }
        new InventoryNetwork().execute(url);

    }

    private void loadChart(JSONObject obj) throws JSONException {

        itemBalance = obj.getInt("balance");
        itemQuantitySold = obj.getInt("sold");
        itemQuantity = obj.getInt("total");
        SharedPrefManager pref = new SharedPrefManager(cxt);

        pref.setSavedInventoryBalance((int)itemBalance);

        yVals.clear();

        BarEntry balanceEntry = new BarEntry(0,itemBalance);
        BarEntry quantityEntry = new BarEntry(2, itemQuantity);
        BarEntry quantitySoldEntry = new BarEntry(1, itemQuantitySold);
        yVals.add(0,  balanceEntry);
        yVals.add(1,  quantitySoldEntry);
        yVals.add(2, quantityEntry);


        /*yVals.add(new BarEntry(1, itemQuantity));
        yVals.add(new BarEntry(2, itemQuantitySold));*/

        BarDataSet dataSet = new BarDataSet(yVals, "Inventory");


//        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        try {

//            dataSet.setColors(getResources().getColor(R.color.balanceColor), getResources().getColor(R.color.quantitySoldColor), getResources().getColor(R.color.quantityColor));

            dataSet.setColors(ColorTemplate.createColors(new int[]{Color.RED, Color.BLUE, Color.GREEN}));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        }
        dataSet.setDrawValues(true);

        BarData set = new BarData(dataSet);

        barInventoryChart.setData(set);

        barInventoryChart.invalidate();
        barInventoryChart.setPinchZoom(false);
        barInventoryChart.animateY(500);
        barInventoryChart.getData().setHighlightEnabled(true);

        Legend legend = barInventoryChart.getLegend();
        LegendEntry le = new LegendEntry();
//        legend.setCustom();

    }

    @Override
    public void onRefresh() {
        loadInventory();

    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        barInventoryChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = barInventoryChart.getPosition(e, YAxis.AxisDependency.LEFT);

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }

    private class InventoryNetwork extends android.os.AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pb.setVisibility(View.VISIBLE);
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pb.setVisibility(View.GONE);

            refreshLayout.setRefreshing(false);
            if (s == null) {

                return;
            }

            try {
                JSONObject obj = new JSONObject(s);
                loadChart(obj);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }

    }

}
