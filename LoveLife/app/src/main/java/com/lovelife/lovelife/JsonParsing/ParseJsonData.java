package com.lovelife.lovelife.JsonParsing;


import com.lovelife.lovelife.BeanClasses.BeanForRestaurantDetail;
import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.BeanClasses.UserLoginBeanClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dx on 2/1/2016.
 */
public class ParseJsonData {

    public static String STATUS_SUCCESS = "success";
    public static String STATUS_FAIL = "fail";
    private static String STATUS = "status";
    private static String MESSAGE = "message";


    public static String LOGIN_STATUS_SUCCESS = "login_success";
    public static String REGISTERATION_STATUS_SUCCESS = "register_success";
    public static String FB_LOGIN_STATUS_SUCCESS = "fb_login_success";
    public static String FB_REGISTERATION_STATUS_SUCCESS = "fb_register_success";

    /**
     * Method to check the status of the json coming from server.
     *
     * @param json json String from which status will be get.
     * @return the status value.
     */
    public static String checkStatus(String json) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.optString(STATUS);


    }


    /**
     * Method to check the Message of the json coming from server.
     *
     * @param json json String from which status will be get.
     * @return the message value.
     */
    public static String getMessage(String json) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.optString(MESSAGE);


    }


    /**
     * Method to get the values from key in json
     *
     * @param json json String from which status will be get.
     * @return the message value.
     */
    public static String getValueWithKey(String json, String key) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.optString(key);


    }


    /**
     * parse user login data json and return array of bean
     *
     * @param response
     */
    public static UserLoginBeanClass parseUserLoginJson(String response) {


        UserLoginBeanClass userLoginData = new UserLoginBeanClass();

        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject(response);
            userLoginData.setUserId(jsonObj.optString("id"));
            userLoginData.setUserName(jsonObj.optString("name"));
            userLoginData.setUserEmail(jsonObj.optString("email"));
            userLoginData.setUserMobile(jsonObj.optString("mobile"));
            userLoginData.setUserToken(jsonObj.optString("remember_token"));
            userLoginData.setFbToken(jsonObj.optString("fb_token"));
            userLoginData.setProfilePic(jsonObj.optString("profile_pic"));
            userLoginData.setEmailConfirmStatus(jsonObj.optString("confirm"));
            userLoginData.setIsConnectedToPartner(jsonObj.optString("connection"));
            userLoginData.setUserGender(jsonObj.optString("gender"));
            userLoginData.setUserDOB(jsonObj.optString("dob"));



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userLoginData;
    }


    public static ConnectedPartnerBean setConnectedPartnerJson(String response,String key) {

        ConnectedPartnerBean connectedPartnerBean = new ConnectedPartnerBean();

        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject(response);
            String partnerData = jsonObj.optString(key);
            jsonObj = new JSONObject(partnerData);

            connectedPartnerBean.setPartnerID(jsonObj.optString("id"));
            connectedPartnerBean.setPartnerConnectionID(jsonObj.optString("conn_id"));
            connectedPartnerBean.setPartnerStatus(jsonObj.optString("status"));
            connectedPartnerBean.setPartnerDisplayName(jsonObj.optString("display_name"));
            connectedPartnerBean.setPartnerEmail(jsonObj.optString("email"));
            connectedPartnerBean.setPartnerGender(jsonObj.optString("gender"));
            connectedPartnerBean.setPartnerDob(jsonObj.optString("dob"));
            connectedPartnerBean.setPartnerFavouriteMovie(jsonObj.optString("favorite_movie"));
            connectedPartnerBean.setPartnerFavouriteFood(jsonObj.optString("favorite_food"));
            connectedPartnerBean.setPartnerFavouriteWeather(jsonObj.optString("favorite_weather"));
            connectedPartnerBean.setPartnerFavouriteFlower(jsonObj.optString("favorite_flower"));
            connectedPartnerBean.setPartnerFavouriteColor(jsonObj.optString("favorite_colour"));
            connectedPartnerBean.setPartnerFavouriteSport(jsonObj.optString("favorite_sport"));
            connectedPartnerBean.setPartnerFavouriteVacation(jsonObj.optString("favorite_vacations"));
            connectedPartnerBean.setPartnerFavouriteCar(jsonObj.optString("favorite_car"));
            connectedPartnerBean.setPartnerProfilePic(jsonObj.optString("profile_pic"));


        } catch (JSONException e) {
            e.printStackTrace();
        }






     /*   JSONObject jsonObj = null;

        ConnectedPartnerBean connectedPartnerBean = new ConnectedPartnerBean();
        try {
            JSONObject jsonRootObject = new JSONObject(response);
            *//**Get the instance of JSONArray that contains JSONObjects*//*
            JSONArray partnerData = jsonRootObject.optJSONArray("partner");


            for (int i = 0; i < partnerData.length(); i++) {
                JSONObject partnerDetail = partnerData.getJSONObject(i);
                connectedPartnerBean.setPartnerID(partnerDetail.optString("id"));
                connectedPartnerBean.setPartnerConnectionID(partnerDetail.optString("conn_id"));
                connectedPartnerBean.setPartnerStatus(partnerDetail.optString("status"));
                connectedPartnerBean.setPartnerDisplayName(partnerDetail.optString("display_name"));
                connectedPartnerBean.setPartnerEmail(partnerDetail.optString("email"));
                connectedPartnerBean.setPartnerGender(partnerDetail.optString("gender"));
                connectedPartnerBean.setPartnerDob(partnerDetail.optString("dob"));
                connectedPartnerBean.setPartnerFavouriteMovie(partnerDetail.optString("favorite_movie"));
                connectedPartnerBean.setPartnerFavouriteFood(partnerDetail.optString("favorite_food"));
                connectedPartnerBean.setPartnerFavouriteWeather(partnerDetail.optString("favorite_weather"));
                connectedPartnerBean.setPartnerFavouriteFlower(partnerDetail.optString("favorite_flower"));
                connectedPartnerBean.setPartnerFavouriteColor(partnerDetail.optString("favorite_colour"));
                connectedPartnerBean.setPartnerFavouriteSport(partnerDetail.optString("favorite_sport"));
                connectedPartnerBean.setPartnerFavouriteVacation(partnerDetail.optString("favorite_vacations"));
                connectedPartnerBean.setPartnerFavouriteCar(partnerDetail.optString("favorite_car"));
                connectedPartnerBean.setPartnerProfilePic(partnerDetail.optString("profile"));

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
*/


        return connectedPartnerBean;
    }


    /**
     * Method to get all the cities for restaurant api
     *
     * @param response
     */
    public static ArrayList<String> parseRestaurantCities(String response) {
        ArrayList<String> allCities = new ArrayList<String>();
        //String[] allCities = null;

        JSONArray citiesArray;
        try {
            JSONObject jsonObj = new JSONObject(response);
            String cities = jsonObj.optString("cities");
            citiesArray = new JSONArray(cities);
//            allCities = new String[citiesArray.length()];
            for (int i = 0; i < citiesArray.length(); i++) {
                allCities.add(citiesArray.getString(i));
//                allCities[i]=citiesArray.getString(i);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return allCities;
    }


    /**
     * Method to get all the cities for restaurant api
     *
     * @param response
     */
    public static ConnectedPartnerBean requestsParsing(String response) {
        ConnectedPartnerBean connectedPartnerBean = new ConnectedPartnerBean();

        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject obj = jsonObj.getJSONObject("partner");
            String partnerEmail = obj.optString("partner_email");
            //   String connectionID = obj.optString("connection_id");
            //   connectedPartnerBean.setPartnerEmail(partnerEmail);
            //  connectedPartnerBean.setConnectionID(connectionID);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return connectedPartnerBean;
    }


    /**
     * parse restaurent data from the response of API
     *
     * @param response response get by URL
     */
    static BeanForRestaurantDetail beanForRestaurantDetail[];

    public static BeanForRestaurantDetail[] parseRestaurantData(String response) {


        JSONObject jsonObj = null;
        try {
            BeanForRestaurantDetail restaurantBean;
            jsonObj = new JSONObject(response);
            JSONArray restaurants = jsonObj.optJSONArray("restaurants");
            beanForRestaurantDetail = new BeanForRestaurantDetail[restaurants.length()];
            for (int i = 0; i < restaurants.length(); i++) {
                restaurantBean = new BeanForRestaurantDetail();
                JSONObject restaurantsObject = restaurants.getJSONObject(i);

                restaurantBean.setRestaurantMobileReserveURL(restaurantsObject.getString("mobile_reserve_url"));
                restaurantBean.setRestaurantphone(restaurantsObject.getString("phone"));
                restaurantBean.setRestaurantState(restaurantsObject.getString("state"));
                restaurantBean.setRestaurantLang(restaurantsObject.getString("lng"));
                restaurantBean.setRestaurantLat(restaurantsObject.getString("lat"));
                restaurantBean.setRestaurantCountry(restaurantsObject.getString("country"));
                restaurantBean.setRestaurantCity(restaurantsObject.getString("city"));
                restaurantBean.setRestaurantID(restaurantsObject.getString("id"));
                restaurantBean.setRestaurantPrice(restaurantsObject.getString("price"));
                restaurantBean.setRestaurantArea(restaurantsObject.getString("area"));
                restaurantBean.setRestaurantImageURL(restaurantsObject.getString("image_url"));
                restaurantBean.setRestaurantAddr(restaurantsObject.getString("address"));
                restaurantBean.setRestaurantPCode(restaurantsObject.getString("postal_code"));
                restaurantBean.setRestaurantName(restaurantsObject.getString("name"));
                restaurantBean.setRestaurantReserveURL(restaurantsObject.getString("reserve_url"));
                beanForRestaurantDetail[i] = restaurantBean;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return beanForRestaurantDetail;
    }


    /**
     * parse requests from people
     *
     * @param response response get by URL
     */
    static BeanRequestsFromPeople beanRequestsFromPeople[];

    public static BeanRequestsFromPeople[] parseRequestsFromPeople(String response) {


        JSONObject jsonObj = null;
        try {
            BeanRequestsFromPeople requestsBean;
            jsonObj = new JSONObject(response);
            JSONArray requests = jsonObj.optJSONArray("request");
            beanRequestsFromPeople = new BeanRequestsFromPeople[requests.length()];
            for (int i = 0; i < requests.length(); i++) {
                requestsBean = new BeanRequestsFromPeople();
                JSONObject restaurantsObject = requests.getJSONObject(i);

                requestsBean.setRequest_email(restaurantsObject.getString("request_email"));
                requestsBean.setRequest_name(restaurantsObject.getString("name"));
                requestsBean.setRequest_pic(restaurantsObject.getString("profile_pic"));

                beanRequestsFromPeople[i] = requestsBean;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return beanRequestsFromPeople;
    }


    /**
     * parse user login data json and return array of bean
     *
     * @param response
     */
    public static String[] parseUserProfileDetail(String response) {
        String[] profileDetail = new String[14];
        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject(response);
            profileDetail[0] = jsonObj.optString("display_name");
            profileDetail[1] = jsonObj.optString("email");
            profileDetail[2] = jsonObj.optString("mobile");
            profileDetail[3] = jsonObj.optString("gender");
            profileDetail[4] = jsonObj.optString("dob");
            profileDetail[5] = jsonObj.optString("height");
            profileDetail[6] = jsonObj.optString("weight");
            profileDetail[7] = jsonObj.optString("city");
            profileDetail[8] = jsonObj.optString("state");
            profileDetail[9] = jsonObj.optString("country");
            profileDetail[10] = jsonObj.optString("zip");
            profileDetail[11] = jsonObj.optString("favorite_movie");
            profileDetail[12] = jsonObj.optString("favorite_food");
            profileDetail[13] = jsonObj.optString("favorite_weather");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profileDetail;
    }


}
