package com.example.diksh.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     ListView listview;
    static ArrayList<String> memorablelist =new ArrayList<>();
    static ArrayList<LatLng> location=new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview=(ListView)findViewById(R.id.listview);

        SharedPreferences sharedPreferences=this.getSharedPreferences("com.example.diksh.memorableplaces",Context.MODE_PRIVATE);

        ArrayList<String>latitudes=new ArrayList<>();
        ArrayList<String> longitudes=new ArrayList<>();
        memorablelist.clear();
        latitudes.clear();
        longitudes.clear();
        location.clear();



        try {
            memorablelist= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));




        } catch (IOException e) {
            e.printStackTrace();
        }
         if(memorablelist.size()>0&&latitudes.size()>0&&longitudes.size()>0){
            if(memorablelist.size()==latitudes.size()&&latitudes.size()==longitudes.size()){
                for (int i=0;i<latitudes.size();i++){
                    location.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));

                }
            }
         }
         else {

             memorablelist.add("Add a Memorable Place...");
             location.add(new LatLng(0, 0));


         }
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,memorablelist);



        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("placenumber",position);
                    startActivity(intent);











            }
        });
//
//        Intent ansintent=getIntent();
//        arrayAdapter.add(ansintent.getStringExtra("new added"));


    }
}
