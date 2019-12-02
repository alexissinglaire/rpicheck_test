package de.eidottermihi.rpicheck;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

import de.eidottermihi.raspicheck.R;
import de.eidottermihi.rpicheck.listviewitems.ChartItem;
import de.eidottermihi.rpicheck.listviewitems.LineChartItem;
import de.eidottermihi.rpicheck.listviewitems.PieChartItem;
import de.eidottermihi.rpicheck.model.model_diskusage;
import de.eidottermihi.rpicheck.ssh.beans.DiskUsageBean;

import static java.security.AccessController.getContext;

public class DiskUsageListviewActivity extends AppCompatActivity {

    Typeface tfRegular;
    Typeface tfLight;

    ArrayList<Float> lfUsedPercent;
    ArrayList<String> lsUsedPerceentTitle;

    ArrayList<model_diskusage> arrayList;

    ChartDataAdapter2 mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_disk_usage_listview);

        setTitle("Disk Usage Information");

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code
                mAdapter.notifyDataSetChanged();

                pullToRefresh.setRefreshing(false);
            }
        });


        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        lfUsedPercent = new ArrayList<>();
        lsUsedPerceentTitle = new ArrayList<>();
        arrayList = new ArrayList<>();

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        ArrayList<DiskUsageBean> object = (ArrayList<DiskUsageBean>) args.getSerializable("diskbean");
        //Log.d("DiskUsageListview", "object value " + object.get(0).getMountedOn());
        //Log.d("DiskUsageListview", "object value " + object.get(0).getUsedPercent());

        ListView lv = findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();



        //lfUsedPercent.clear();
        //lsUsedPerceentTitle.clear();
        arrayList.clear();

        for (int i = 0; i < object.size() ; i++) {
            Log.d("DiskUsageListview", "object value " + object.get(i).getMountedOn());
            Log.d("DiskUsageListview", "object value " + object.get(i).getUsedPercent());
            String sUsedSize = object.get(i).getUsedPercent();
            sUsedSize = sUsedSize.replace("%", "");
            Float fUsedSize = Float.parseFloat(sUsedSize);
            String sMountedOn = object.get(i).getMountedOn();
            //lfUsedPercent.add(fUsedSize);
            //lsUsedPerceentTitle.add(object.get(i).getMountedOn());
            arrayList.add(new model_diskusage(fUsedSize, sMountedOn));

            //list.add(new PieChartItem(generateDataPie(fUsedSize, 100,  object.get(i).getMountedOn()), getApplicationContext()));



        }



        //ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);

        //lv.setAdapter(cda);

        mAdapter = new ChartDataAdapter2(this, R.layout.row_chartpie, arrayList);
        lv.setAdapter(mAdapter);


    }


    private class ChartDataAdapter2 extends BaseAdapter {
        private Context context;
        private int layout;
        //List<Float> fUsedPercent;
        //List<String> sTitleUsedPercent;
        List<model_diskusage> recordList;
        private ArrayList<model_diskusage> arrayList;

        //public ChartDataAdapter2 (Context context, int layout, List<Float> fValue, List<String> sTitle) {
        public ChartDataAdapter2 (Context context, int layout, List<model_diskusage> recordList) {
            this.context = context;
            this.layout = layout;
            this.recordList = recordList;
            this.arrayList = new ArrayList<model_diskusage>();
            this.arrayList.addAll(recordList);

        }

        @Override
        public int getCount() {
            return recordList.size();
        }

        @Override
        public Object getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder{

            PieChart chartPie;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ChartDataAdapter2.ViewHolder holder = new ChartDataAdapter2.ViewHolder();
            Typeface tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
            Typeface tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


            if (row==null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_chartpie, null);
                holder.chartPie = row.findViewById(R.id.chartMulti);

                holder.chartPie.setUsePercentValues(true);
                holder.chartPie.getDescription().setEnabled(false);
                holder.chartPie.setExtraOffsets(5, 10, 5, 5);

                holder.chartPie.setDragDecelerationFrictionCoef(0.95f);

                holder.chartPie.setCenterTextTypeface(tfLight);
                holder.chartPie.setCenterText(generateCenterSpannableText());

                holder.chartPie.setDrawHoleEnabled(true);
                holder.chartPie.setHoleColor(Color.WHITE);

                holder.chartPie.setTransparentCircleColor(Color.WHITE);
                holder.chartPie.setTransparentCircleAlpha(110);

                holder.chartPie.setHoleRadius(40f);  //58f
                holder.chartPie.setTransparentCircleRadius(43f);  //61f

                holder.chartPie.setDrawCenterText(true);

                holder.chartPie.setRotationAngle(0);

                holder.chartPie.setRotationEnabled(true);
                holder.chartPie.setHighlightPerTapEnabled(true);
                holder.chartPie.animateY(1400, Easing.EaseInOutQuad);

                Legend l = holder.chartPie.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.VERTICAL);
                l.setDrawInside(false);
                l.setXEntrySpace(7f);
                l.setYEntrySpace(0f);
                l.setYOffset(0f);
                l.setTextSize(15f);
                l.setTextColor(Color.LTGRAY);

                // entry label styling
                holder.chartPie.setEntryLabelColor(Color.parseColor("#e94922"));
                holder.chartPie.setEntryLabelTypeface(tfRegular);
                holder.chartPie.setEntryLabelTextSize(15f);

                row.setTag(holder);

            } else {
                holder = (ChartDataAdapter2.ViewHolder) row.getTag();
            }


            model_diskusage model = recordList.get(position);


            //form char PIE
            ArrayList<PieEntry> entries = new ArrayList<>();

            //float fValue1 = (float) (fUsedPercent.get(position) * 10) + 10 / 2;
            //float fValue2 = (float) ((10-fValue1 * 10) + 10 / 2);

            float fValue1 = (float)model.getUsed_percent();
            float fValue2 = 100 - fValue1;
            String sTitle = model.getDisk_title();

            Log.d("DiskUsageListvie", "float value 1 = " + fValue1);
            Log.d("DiskUsageListvie", "float value 2 = " + fValue2);



            if (fValue1 > 0) {
                entries.add(new PieEntry(fValue1, "used", null));
                entries.add(new PieEntry(fValue2, "available", null));
            } else {
                entries.add(new PieEntry(100, "available", null));
            }

            PieDataSet dataSet = new PieDataSet(entries, "Disk Usage - " + sTitle);
            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);
            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(holder.chartPie));
            data.setValueTextSize(15f);
            data.setValueTextColor(Color.RED);
            data.setValueTypeface(tfLight);
            holder.chartPie.setData(data);

            // undo all highlights
            holder.chartPie.highlightValues(null);

            holder.chartPie.invalidate();

            return row;

        }



        private SpannableString generateCenterSpannableText() {

            SpannableString s = new SpannableString("Disk Usage\npowered by MPAndroidChart");
            s.setSpan(new RelativeSizeSpan(1.7f), 0, 10, 0);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 10, s.length() - 11, 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 10, s.length() - 11, 0);
            //s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
            //s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
            //s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
            //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
            //s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
            //s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
            return s;
        }




    }






    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions

            return getItem(position).getView(position, convertView, getContext());
            //View rowView  = convertView;




            //return rowView ;
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
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


    private PieData generateDataPie(Float fUsedSize,  float range, String sTitle) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        //for (int i = 0; i < 4; i++) {
        //    entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        //}
        Log.d("DiskUsageListview", "inside generateDataPie. fUsedSize = " + fUsedSize);

        if (fUsedSize > 0) {
            entries.add(new PieEntry((float) ((fUsedSize * range) + range / 2),
                    sTitle,
                    getResources().getDrawable(R.drawable.star)));

            entries.add(new PieEntry((float) (((100 - fUsedSize) * range) + range / 2),
                    sTitle,
                    getResources().getDrawable(R.drawable.star)));
        } else {
            entries.add(new PieEntry((float) (((100 - 0 ) * range) + range / 2),
                    sTitle,
                    getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet d = new PieDataSet(entries, "Raspi Pi Usage - " + sTitle);

        // space between slices
       //d.setValueFormatter(new PercentFormatter());
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setValueTextColor(Color.RED);
        d.setValueTypeface(tfLight);


        // added


        return new PieData(d);
    }


}
