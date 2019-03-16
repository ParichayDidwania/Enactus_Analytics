package envisionanalytics.example.enactus.envisionanalytics;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class response extends Fragment {

    SharedPreferences sp;
    String company;

    private int positive=0;
    private int negative=0;
    float pos_percentage;
    float neg_percentage;

    private float ydata[] = {25f,75f};
    private String xdata[] = {"Positive","Negative"};

    PieChart pieChart;
    TextView t;
    ArrayList<PieEntry> y_data = new ArrayList<PieEntry>();
    PieDataSet pieDataSet = new PieDataSet(y_data,"");
    ArrayList<Integer> colors = new ArrayList<>();



    public response() {
        // Required empty public constructor
    }

    public void addData()
    {
        ArrayList<PieEntry> y_data = new ArrayList<PieEntry>();

        for(int i=0;i<ydata.length;i++)
        {
            y_data.add(new PieEntry(ydata[i],xdata[i]));
        }
        pieDataSet = new PieDataSet(y_data,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(20f);
        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(14f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        sp = this.getActivity().getSharedPreferences("envisionanalytics.example.enactus.envisionanalytics", Context.MODE_PRIVATE);
        company = sp.getString("company","");

        t = (TextView)getView().findViewById(R.id.textView5);
        t.setText(Integer.toString(sp.getInt("calls",0)));

        pos_percentage = sp.getFloat("pos_per",25);
        neg_percentage = sp.getFloat("neg_per",75);
        ydata = new float[2];
        ydata[0]=pos_percentage;
        ydata[1]=neg_percentage;

        getActivity().setTitle("Response");

        pieChart = (PieChart)getView().findViewById(R.id.pie);
        pieChart.setHoleRadius(20f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setUsePercentValues(true);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        addData();

        FirebaseDatabase.getInstance().getReference().child("companies").child("company1").child("aftercall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    positive = 0;
                    negative = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                            if (dataSnapshot2.child("response").getValue().toString().equals("positive")) {
                                positive++;

                            } else {
                                negative++;

                            }
                        }
                    }

                    calculate();
                }catch (Exception e){}

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void calculate()
    {
        sp.edit().putInt("calls",positive+negative).apply();
        t.setText(Integer.toString(positive+negative));

        pos_percentage = positive*100/(positive+negative);
        neg_percentage = negative*100/ (positive+negative);
        sp.edit().putFloat("pos_per",pos_percentage).apply();
        sp.edit().putFloat("neg_per",neg_percentage).apply();

        ydata = new float[2];
        ydata[0]=pos_percentage;
        ydata[1]=neg_percentage;

        addData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_response, container, false);


    }



}
