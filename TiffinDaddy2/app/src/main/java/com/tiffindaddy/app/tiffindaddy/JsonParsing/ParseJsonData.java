package com.tiffindaddy.app.tiffindaddy.JsonParsing;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.Cities;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinCategories;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.UserAddresses;
import com.tiffindaddy.app.tiffindaddy.BeanClasses.UserLoginData;
import com.tiffindaddy.app.tiffindaddy.Manager.TimeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dx on 2/1/2016.
 */
public class ParseJsonData {

    /**
     * variables for categories parsing
     */
    TiffinCategories tempBean;
    TiffinCategories tiffinCategories[];
    TiffinData tiffinData[];
    TiffinData tempTiffinBean;
    Cities cities[];

    public String checkStatus(String json, String key) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.optString(key);


    }


    /**
     * parse tiffin categories json and return array of bean
     *
     * @param response
     */
    public Cities[] parseCitiesData(String response) {

        try {

            JSONObject jsonRootObject = new JSONObject(response);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            /**
             * initialize the beansClass array of object.
             */
            cities = new Cities[jsonArray.length()];
            Cities citiesTemp;
            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                citiesTemp = new Cities();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                citiesTemp.setCityName(jsonObject.getString("city"));
                citiesTemp.setCityId(jsonObject.getString("id"));
                cities[i] = citiesTemp;


            }


        } catch (JSONException e) {

            e.printStackTrace();
        }

        return cities;
    }

    /**
     * parse tiffin categories json and return array of bean
     *
     * @param response
     */
    public TiffinCategories[] parseCategoryJson(String response) {

        try {

            JSONObject jsonRootObject = new JSONObject(response);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            /**
             * initialize the beansClass array of object.
             */
            tiffinCategories = new TiffinCategories[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempBean = new TiffinCategories();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                tempBean.setCategoryId(jsonObject.getString("category_id"));
                tempBean.setCategoryName(jsonObject.getString("category_name"));
                tempBean.setCategoryStartTime(jsonObject.getString("category_start_time"));
                tempBean.setCategoryEndTime(jsonObject.getString("category_end_time"));
                tiffinCategories[i] = tempBean;


                System.out.println("category_id " + jsonObject.getString("category_id"));
                System.out.println("category_name " + jsonObject.getString("category_name"));
                System.out.println("category_start_time " + jsonObject.getString("category_start_time"));
                System.out.println("category_end_time " + jsonObject.getString("category_end_time"));


                System.out.println("===================================");

            }


        } catch (JSONException e) {

            e.printStackTrace();
        }

        return tiffinCategories;
    }

    /**
     * parse tiffin data json and return array of bean
     *
     * @param response
     */
    public TiffinData[] parseTiffinJson(String response, String categoryId, String categoryName, String timeSlot) {

        try {

            JSONObject jsonRootObject = new JSONObject(response);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            /**
             * initialize the beansClass array of object.
             */
            tiffinData = new TiffinData[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                tempTiffinBean = new TiffinData();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                tempTiffinBean.setTiffinId(jsonObject.getString("tiffin_id"));
                tempTiffinBean.setTiffinName(jsonObject.getString("tiffin_name"));
                tempTiffinBean.setTiffinDescription(jsonObject.getString("tiffin_description"));
                tempTiffinBean.setTiffinPrice(jsonObject.getString("tiffin_price"));
                tempTiffinBean.setTiffinImage(jsonObject.getString("tiffin_image"));
                tempTiffinBean.setQuantityPurchased(0);
                tempTiffinBean.setCategoryType(categoryId);
                tempTiffinBean.setCategoryName(categoryName);
                tempTiffinBean.setDeliverySlot(timeSlot);
                tiffinData[i] = tempTiffinBean;


                System.out.println(jsonObject.getString("tiffin_name"));
                System.out.println(jsonObject.getString("tiffin_description"));
                System.out.println(jsonObject.getString("tiffin_price"));
                System.out.println(jsonObject.getString("tiffin_image"));

                System.out.println("===================================");

            }


        } catch (JSONException e) {

            e.printStackTrace();
        }

        return tiffinData;
    }


    /**
     * parseaddress json and return array of bean
     *
     * @param response
     */

    UserAddresses userAddresses[];

    public UserAddresses[] parseUserAddressJson(String response) {

        try {

            JSONObject jsonRootObject = new JSONObject(response);
            /**Get the instance of JSONArray that contains JSONObjects*/
            JSONArray jsonArray = jsonRootObject.optJSONArray("result");

            /**
             * initialize the beansClass array of object.
             */
            userAddresses = new UserAddresses[jsonArray.length()];

            /**Iterate the jsonArray and print the info of JSONObjects*/
            for (int i = 0; i < jsonArray.length(); i++) {
                UserAddresses uAddress = new UserAddresses();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                uAddress.setAddressTtle(jsonObject.getString("address_title"));
                uAddress.setAddress(jsonObject.getString("address"));
                uAddress.setCityName(jsonObject.getString("city_name"));

                userAddresses[i] = uAddress;


                System.out.println(jsonObject.getString("address_title"));
                System.out.println(jsonObject.getString("address"));
                System.out.println(jsonObject.getString("city_name"));

                System.out.println("===================================");

            }


        } catch (JSONException e) {

            e.printStackTrace();
        }

        return userAddresses;
    }


    /**
     * parse user login data json and return array of bean
     *
     * @param response
     */
    public UserLoginData parseUserLoginJson(String response) {


        UserLoginData userLoginData = new UserLoginData();

        JSONObject jsonObj;

        try {
            jsonObj = new JSONObject(response);
            userLoginData.setUserID(jsonObj.optString("uid"));
            userLoginData.setUserName(jsonObj.optString("name"));
            userLoginData.setUserEmail(jsonObj.optString("email"));
            userLoginData.setUserPhone(jsonObj.optString("phone"));
            userLoginData.setToken(jsonObj.optString("device_token"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userLoginData;
    }


}
