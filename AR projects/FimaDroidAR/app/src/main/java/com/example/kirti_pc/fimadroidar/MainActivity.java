package com.example.kirti_pc.fimadroidar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import gl.GL1Renderer;
import gl.GLFactory;
import system.ArActivity;
import system.DefaultARSetup;
import util.Vec;
import worldData.World;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button b = new Button(this);
        b.setText("Click me to Start the AR Action");
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ArActivity.startWithSetup(MainActivity.this, new DefaultARSetup(){

                    @Override
                    public void addObjectsTo(GL1Renderer renderer, World world,
                                             GLFactory objectFactory) {
                        world.add(objectFactory.newSolarSystem(new Vec(10, 0, 0)));
                    }

                });
            }
        });

        setContentView(b);
    }
}
