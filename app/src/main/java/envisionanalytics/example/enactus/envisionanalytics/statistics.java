package envisionanalytics.example.enactus.envisionanalytics;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class statistics extends Fragment {

    LineChart chart;
    int time=24*60*60*1000;
    LineDataSet setComp1;
    List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    DecimalFormat mFormat = new DecimalFormat("###,###,##0");
    int counts[] = new int[]{0,0,0,0,0,0};
    SharedPreferences sp;
    String company;

    public statistics() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Statistics");
        chart = (LineChart) getView().findViewById(R.id.linechart);
        sp = this.getActivity().getSharedPreferences("envisionanalytics.example.enactus.envisionanalytics",Context.MODE_PRIVATE);
        company = sp.getString("company","");

        final String quarters_dates[];
        final String search_dates[];
        quarters_dates = new String[6];
        search_dates = new String[6];

        SimpleDateFormat df = new SimpleDateFormat("ddMMMM", Locale.US);
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(new Date());
        String formattedDate2 = df2.format(new Date());
        for(int i=0;i<=5;i++) {
            try {
                Date mydate = df.parse(formattedDate);
                Date newDate = new Date(mydate.getTime() - time*(5-i));
                String date = df.format(newDate).substring(0,5);
                quarters_dates[i]=date;

                Date mydate2 = df2.parse(formattedDate2);
                Date newDate2 = new Date(mydate2.getTime() - time*(5-i));
                String date2 = df2.format(newDate2);
                search_dates[i]=date2;
                System.out.println(search_dates[i]);

            } catch (Exception e) {
            }
        }



        chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.enableGridDashedLine(10f, 10f, 0f);
        }

        YAxis yAxis;
        {
            yAxis = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f, 10f, 0f);
            yAxis.setAxisMinimum(0);
        }
        chart.animateX(1000);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters_dates[(int) value];
            }

            public int getDecimalDigits() {  return 0; }
        };

        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        addData();

        FirebaseDatabase.getInstance().getReference().child("companies").child(company).child("aftercall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    counts = new int[6];
                    for (int i = 0; i < 6; i++) {
                        counts[i] = (int) dataSnapshot.child(search_dates[i]).getChildrenCount();
                    }
                    addData();
                }
                catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    public void addData()
    {
        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(0f,counts[0]));
        entries.add(new Entry(1f,counts[1]));
        entries.add(new Entry(2f,counts[2]));
        entries.add(new Entry(3f,counts[3]));
        entries.add(new Entry(4f,counts[4]));
        entries.add(new Entry(5f,counts[5]));

        setComp1 = new LineDataSet(entries, "No of Calls");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setDrawFilled(true);
        setComp1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this.getActivity(), R.drawable.fade_red);
            setComp1.setFillDrawable(drawable);
        } else {
            setComp1.setFillColor(Color.BLACK);
        }

        setComp1.setLineWidth(1f);
        setComp1.setDrawCircleHole(false);
        setComp1.setCircleRadius(5f);
        setComp1.setCircleColor(R.drawable.dotcol);
        setComp1.setValueTextSize(15f);
        setComp1.setColor(R.drawable.linecol);
        setComp1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                return mFormat.format(value);//return your text
            }
        });


        dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

}
