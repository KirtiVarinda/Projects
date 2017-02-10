package de.rwth.setups;

/**
 * Created by Kirti-PC on 10/13/2016.
 */
public class DBLatLang {

    //private variables
    int _id;
    double _lat;
    double _lang;
    String _object;
    String _time;

    // Empty constructor
    public DBLatLang() {

    }

    // constructor
    public DBLatLang(int id, double lats, double langs,String obj,String times){
        this._id = id;
        this._lat = lats;
        this._lang = langs;
        this._object = obj;
        this._time = times;
    }

    // constructor
    public DBLatLang(double lats, double langs, String obj, String times) {
        this._lat = lats;
        this._lang = langs;
        this._object = obj;
        this._time = times;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_lang() {
        return _lang;
    }

    public void set_lang(double _lang) {
        this._lang = _lang;
    }

    public String get_object() {
        return _object;
    }

    public void set_object(String _object) {
        this._object = _object;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }
}