package de.eidottermihi.rpicheck;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import de.eidottermihi.raspicheck.R;
import de.eidottermihi.rpicheck.ssh.beans.DiskUsageBean;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class DiskUsageChartActivity extends AppCompatActivity {
    private PieChart chart;

    Typeface tfRegular;
    Typeface tfLight;


    public static final int[] MATERIAL_COLORS2 = {
            rgb("#2ecc71"), rgb("##29b774"), rgb("#24a367"), rgb("#208e5a"), rgb("#1b7a4d"),
            rgb("#2ecc81"), rgb("#42d18d"), rgb("#57d69a"), rgb("#6cdba6"), rgb("#81e0b3")
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_usage_chart);

        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


        Bundle args = getIntent().getBundleExtra("BUNDLE");
        ArrayList<DiskUsageBean> object = (ArrayList<DiskUsageBean>) args.getSerializable("diskbean");

        String sFileSystem = object.get(1).getFileSystem();

        //Toast.makeText(getApplicationContext(), sFileSystem, Toast.LENGTH_SHORT).show();
        Log.d("DiskUsageChartActivity", "incoming value -1 =" + object.get(1).getMountedOn().toString());

        Log.d("DiskUsageChartActivity", "incoming value -2 =" + object.get(2).getMountedOn().toString());

        Log.d("DiskUsageChartActivity", "incoming value -3 =" + object.get(3).getMountedOn().toString());


        chart = findViewById(R.id.chart1);
        chart.setBackgroundColor(Color.WHITE);

        moveOffScreen();

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);

        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);

        chart.setMaxAngle(180f); // HALF CHART
        chart.setRotationAngle(180f);
        chart.setCenterTextOffset(0, -20);

        setData(object.size(), 100, object);

        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling


        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

    }

    private void moveOffScreen() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;

        int offset = (int)(height * 0.65); /* percent to move */

        RelativeLayout.LayoutParams rlParams =
                (RelativeLayout.LayoutParams) chart.getLayoutParams();
        rlParams.setMargins(0, 0, 0, -offset);
        chart.setLayoutParams(rlParams);
    }

    private void setData(int count, float range, ArrayList<DiskUsageBean> object) {

        Log.d("DiskUsageChartActivity", "object.size = " +count);

        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i <count; i++) {
            Log.d("DiskUsageChartActivity", "value idx(" + i + ") =" + object.get(i).getUsedPercent() + "." + object.get(i).getMountedOn());
            String sUsedSize = object.get(i).getUsedPercent();
            sUsedSize = sUsedSize.replace("%", "");
            Float fUsedSize = Float.parseFloat(sUsedSize) / range;
            values.add(new PieEntry(fUsedSize, object.get(i).getMountedOn()));
        }

        PieDataSet dataSet = new PieDataSet(values, "PI Disk Usage");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(MATERIAL_COLORS2);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        chart.setData(data);

        chart.invalidate();
    }


    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
}
