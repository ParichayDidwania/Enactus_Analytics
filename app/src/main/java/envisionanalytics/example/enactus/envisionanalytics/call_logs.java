package envisionanalytics.example.enactus.envisionanalytics;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class call_logs extends Fragment {

    ListView l;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> durations = new ArrayList<>();
    ArrayList<String> responses = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();
    CustomAdapter customAdapter;

    ArrayList<String> names1 = new ArrayList<>();
    ArrayList<String> phones1 = new ArrayList<>();
    ArrayList<String> durations1 = new ArrayList<>();
    ArrayList<String> responses1 = new ArrayList<>();
    ArrayList<String> dates1 = new ArrayList<>();
    ArrayList<String> times1 = new ArrayList<>();

    ArrayList<String> check_dates = new ArrayList<>();

    SharedPreferences sp;
    String company;

    public call_logs() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Call Logs");
        sp = this.getActivity().getSharedPreferences("envisionanalytics.example.enactus.envisionanalytics",Context.MODE_PRIVATE);
        company = sp.getString("company","");

        try{
            names = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("names","NAME")));
            phones = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("phones","0123456789")));
            durations = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("durations","5")));
            responses = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("responses","positive")));
            dates = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("dates","01-January-2018")));
            times = ((ArrayList<String>) ObjectSerializer.deserialize(sp.getString("times","00:00:00")));


        }catch (Exception e)
        {
        }


        l = (ListView)getView().findViewById(R.id.list);
        customAdapter = new CustomAdapter();

        l.setAdapter(customAdapter);

        FirebaseDatabase.getInstance().getReference().child("companies").child(company).child("aftercall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    names = new ArrayList<>();
                    phones = new ArrayList<>();
                    durations = new ArrayList<>();
                    responses = new ArrayList<>();
                    dates = new ArrayList<>();
                    times = new ArrayList<>();
                    check_dates = new ArrayList<>();


                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        check_dates.add(dataSnapshot1.getKey());
                        System.out.println("*************************\n"+dataSnapshot1.getKey());
                    }

                    Collections.sort(check_dates, new StringDateComparator());

                        for (int i = check_dates.size() - 1; i >= 0; i--) {

                                names1 = new ArrayList<>();
                                phones1 = new ArrayList<>();
                                durations1 = new ArrayList<>();
                                responses1 = new ArrayList<>();
                                dates1 = new ArrayList<>();
                                times1 = new ArrayList<>();


                                DataSnapshot dataSnapshot1 = dataSnapshot.child(check_dates.get(i));

                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    dates1.add(dataSnapshot1.getKey());
                                    names1.add(dataSnapshot2.child("name").getValue().toString());
                                    phones1.add(dataSnapshot2.getKey());
                                    durations1.add(dataSnapshot2.child("duration").getValue().toString());
                                    times1.add(dataSnapshot2.child("time").getValue().toString());
                                    responses1.add(dataSnapshot2.child("response").getValue().toString());
                                }
                                concurrentSort(times1, times1, names1, dates1, phones1, durations1, responses1);
                                Collections.reverse(times1);
                                Collections.reverse(names1);
                                Collections.reverse(dates1);
                                Collections.reverse(phones1);
                                Collections.reverse(durations1);
                                Collections.reverse(responses1);
                                times.addAll(times1);
                                names.addAll(names1);
                                responses.addAll(responses1);
                                durations.addAll(durations1);
                                dates.addAll(dates1);
                                phones.addAll(phones1);
                            }

                    if(names.size()==0)
                    {
                        names.add("NAMES");
                        phones.add("0123456789");
                        durations.add("5");
                        responses.add("positive");
                        dates.add("01-January-2018");
                        times.add("00:00:00");
                        customAdapter.notifyDataSetChanged();
                    }
                    saveData();
                    System.out.println("Length of names2: "+names.size());
                    customAdapter.notifyDataSetChanged();
                }catch (Exception e)
                {

                }
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
        return inflater.inflate(R.layout.fragment_call_logs, container, false);
    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.customlayout,null);
            TextView name = (TextView)view.findViewById(R.id.textView6);
            TextView phone = (TextView)view.findViewById(R.id.textView7);
            TextView duration = (TextView)view.findViewById(R.id.textView8);
            TextView response = (TextView)view.findViewById(R.id.textView10);
            TextView date = (TextView)view.findViewById(R.id.textView9);

            name.setText(name.getText()+" "+names.get(i));
            phone.setText(phone.getText()+" "+phones.get(i));
            duration.setText(duration.getText()+" "+durations.get(i));
            response.setText(response.getText()+" "+responses.get(i));
            date.setText(date.getText()+" "+dates.get(i)+" at "+times.get(i));

            return view;
        }
    }

    public static <T extends Comparable<T>> void concurrentSort(
            final List<T> key, List<?>... lists){
        // Create a List of indices
        List<Integer> indices = new ArrayList<Integer>();
        for(int i = 0; i < key.size(); i++)
            indices.add(i);

        // Sort the indices list based on the key
        Collections.sort(indices, new Comparator<Integer>(){
            @Override public int compare(Integer i, Integer j) {
                return key.get(i).compareTo(key.get(j));
            }
        });

        // Create a mapping that allows sorting of the List by N swaps.
        // Only swaps can be used since we do not know the type of the lists
        Map<Integer,Integer> swapMap = new HashMap<Integer, Integer>(indices.size());
        List<Integer> swapFrom = new ArrayList<Integer>(indices.size()),
                swapTo   = new ArrayList<Integer>(indices.size());
        for(int i = 0; i < key.size(); i++){
            int k = indices.get(i);
            while(i != k && swapMap.containsKey(k))
                k = swapMap.get(k);

            swapFrom.add(i);
            swapTo.add(k);
            swapMap.put(i, k);
        }

        // use the swap order to sort each list by swapping elements
        for(List<?> list : lists)
            for(int i = 0; i < list.size(); i++)
                Collections.swap(list, swapFrom.get(i), swapTo.get(i));
    }

    public void saveData()
    {
        try {
            sp.edit().putString("names",ObjectSerializer.serialize(names)).apply();
            sp.edit().putString("phones",ObjectSerializer.serialize(phones)).apply();
            sp.edit().putString("durations",ObjectSerializer.serialize(durations)).apply();
            sp.edit().putString("responses",ObjectSerializer.serialize(responses)).apply();
            sp.edit().putString("dates",ObjectSerializer.serialize(dates)).apply();
            sp.edit().putString("times",ObjectSerializer.serialize(times)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class StringDateComparator implements Comparator<String>
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        public int compare(String lhs, String rhs)
        {
            try {
                return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
            } catch (ParseException e) {
                return 1;
            }
        }
    }

}
