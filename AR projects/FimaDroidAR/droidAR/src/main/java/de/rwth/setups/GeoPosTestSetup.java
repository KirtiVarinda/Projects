package de.rwth.setups;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import commands.Command;
import geo.GeoObj;
import gl.Color;
import gl.GL1Renderer;
import gl.GLFactory;
import gui.GuiSetup;
import system.DefaultARSetup;
import system.SimpleLocationManager;
import worldData.World;

public class GeoPosTestSetup extends DefaultARSetup {
    public static boolean check = false;
    int duration = Toast.LENGTH_SHORT;


    @Override
    public void addObjectsTo(GL1Renderer renderer, World world,
                             GLFactory objectFactory) {
System.out.println("qwqwqw");



            if (!(SimpleLocationManager.location == null)) {

                System.out.println("locationnnnn-===" + SimpleLocationManager.location.getLongitude() + "okk" + SimpleLocationManager.location.getLatitude());
                GeoObj o = new GeoObj(SimpleLocationManager.location.getLatitude(), SimpleLocationManager.location.getLongitude());
                // Set the object on the world.

                o.setComp(GLFactory.getInstance().newSquare(Color.green()));
                world.add(o);
                Log.d("Insert: ", "Inserting ..");
                DatabaseHandler db = new DatabaseHandler(this.getActivity());
                // Add data into database
                db.addContact(new DBLatLang(SimpleLocationManager.location.getLatitude(), SimpleLocationManager.location.getLongitude(), "diamond", "hlo"));

                // show data from database
                DBLatLang l=new DBLatLang();
                Log.d("Reading: ", "Reading all Data..");
                List<DBLatLang> latlang = db.getAllContacts();

              //  double[] latArrayVar = new double[50];
              //  double[] lanArrayVar = new double[50];
                System.out.println("checkkk11"+check);
                int i = 0;
                for (DBLatLang ll : latlang) {
                    double latitude =ll.get_lat();
                    double longitude = ll.get_lang();
                    System.out.println("latitude"+latitude+"longitude"+longitude);
                    GeoObj ob = new GeoObj(latitude, longitude);

                    // Set the object on the world.


                    ob.setComp(GLFactory.getInstance().newSquare(Color.green()));
                    world.add(ob);
//                    String log = "Id: " + ll.getID() + " ,Latitude: " + ll.get_lat() + " ,Longitude: " +
//                            ll.get_lang() + " ,Object: " + ll.get_object() + " ,Time: " +
//                            ll.get_time();
                    // Writing Contacts to log
//                    Log.d("Name::: ", log+ll.get_lat());
//                    try {
//                        latArrayVar[i] = ll.get_lat();
//                        lanArrayVar[i] = ll.get_lang();
//                        System.out.println("hloo plewwwqqqzz"+latArrayVar[i]+"okkk"+lanArrayVar[i]);
//                        i=i+1;
//
//                    }catch(Exception e){
//                        e.printStackTrace();
//                        System.out.println("exceptionnnnns"+e);
//                    }
                }

                check = true;
                System.out.println("checkkk"+check);
                Toast toast1 = Toast.makeText(this.getActivity(), "Done check", duration);
                toast1.show();
            //    db.deleteAllContact();


        }
    }


    @Override
    public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {

        guiSetup.setRightViewAllignBottom();
        guiSetup.addImangeButtonToRightView(android.R.drawable.arrow_up_float,
                new Command() {
                    @Override
                    public boolean execute() {
                        camera.changeZPositionBuffered(+ZDELTA);
                        return false;
                    }
                });
        guiSetup.addImangeButtonToRightView(android.R.drawable.arrow_down_float,
                new Command() {
                    @Override
                    public boolean execute() {
                        camera.changeZPositionBuffered(-ZDELTA);
                        return false;
                    }
                });

        guiSetup.setBottomMinimumHeight(20);
        guiSetup.setBottomViewCentered();

        // addMapView(activity, guiSetup);

        guiSetup.addButtonToBottomView(new Command() {

            @Override
            public boolean execute() {

                System.out.println("gooooood");
                check = false;

//				GeoObj o = new GeoObj();
//				o.setComp(GLFactory.getInstance().newDiamond(Color.green()));
//				world.add(o);
                return false;
            }
        }, "Add Me");

    }
}



