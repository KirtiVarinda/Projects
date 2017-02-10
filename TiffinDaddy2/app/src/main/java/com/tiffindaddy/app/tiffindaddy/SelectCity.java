package com.tiffindaddy.app.tiffindaddy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tiffindaddy.app.tiffindaddy.TiffinAdapters.CartAdapter;
import com.tiffindaddy.app.tiffindaddy.TiffinAdapters.CityAdapter;

public class SelectCity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        String[] allCities=new String[ProductPage.cities.length];
        for(int t=0;t<ProductPage.cities.length;t++) {
            allCities[t]=ProductPage.cities[t].getCityName();
        }


        ListView listView=(ListView)findViewById(R.id.listView4);
        final CityAdapter cityAdapter = new CityAdapter(getApplicationContext(), R.layout.selectcity_row ,allCities,ProductPage.cities,this);
        listView.setAdapter(cityAdapter);


    }

    @Override
    public void onBackPressed() {


           this.finish();
            overridePendingTransition(0,0);

    }

}
