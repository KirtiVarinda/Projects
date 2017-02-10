package com.tiffindaddy.app.tiffindaddy.JsonParsing;

import android.content.Context;
import android.os.Message;
import android.util.JsonWriter;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.CartData;
import com.tiffindaddy.app.tiffindaddy.ProductPage;
import com.tiffindaddy.app.tiffindaddy.SharedData.MySharedData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avnish on 2/22/2016.
 */
public class WriteJsonData {

    static MySharedData mySharedData;
    static String couponCode = "", discountType = "", discount = "";

    public static String writeJson2(CartData[] cartDatasBreakfastToday, CartData[] cartDatasBreakfastTomorrow, CartData[] cartDatasLunchToday, CartData[] cartDatasLunchTomorrow, CartData[] cartDatasDinnerToday, CartData[] cartDatasDinnerTomorrow, String paymentMode, String uid, Context context) {
        mySharedData = new MySharedData(context);

        if (ProductPage.couponData != null) {
            couponCode = ProductPage.couponData.getCouponCode();
            discountType = ProductPage.couponData.getCouponType();
            discount = ProductPage.couponData.getDiscount();
        }


        int count = 0;

        if (cartDatasBreakfastToday.length > 0) {
            count++;
        }
        if (cartDatasBreakfastTomorrow.length > 0) {
            count++;
        }
        if (cartDatasLunchToday.length > 0) {
            count++;

        }
        if (cartDatasLunchTomorrow.length > 0) {
            count++;

        }
        if (cartDatasDinnerToday.length > 0) {
            count++;

        }
        if (cartDatasDinnerTomorrow.length > 0) {
            count++;

        }


        ArrayList<CartData[]> list = new ArrayList<CartData[]>();

        list.add(cartDatasBreakfastToday);
        list.add(cartDatasBreakfastTomorrow);
        list.add(cartDatasLunchToday);
        list.add(cartDatasLunchTomorrow);
        list.add(cartDatasDinnerToday);
        list.add(cartDatasDinnerTomorrow);


        JSONObject object = new JSONObject();
        try {
            // Add the id to the json
            object.put("uid", uid);
            object.put("device_token", mySharedData.getGeneralSaveSession(MySharedData.SESSION_TOKEN));
            object.put("payment_mode", paymentMode);


            // Create a json array
            JSONArray jsonArray2 = new JSONArray();

            JSONObject[] outerArray = new JSONObject[count];
            int index1 = 0;
            for (int index = 0; index < list.size(); index++) {
                CartData[] cartData = list.get(index);
                if (cartData.length > 0) {
                    outerArray[index1] = new JSONObject();

                    JSONArray array1 = new JSONArray();
                    JSONObject object1 = new JSONObject();

                    object1.put("coupon_code", couponCode);
                    object1.put("discount", discount);
                    object1.put("discount_type", discountType);

                    object1.put("tiffin_category", cartData[0].getTiffinData().getCategoryType());

                    String[] changeDate=cartData[0].getSelectedDate().split("/");

                    object1.put("delivery_date", changeDate[2]+"-"+changeDate[1]+"-"+changeDate[0]);
                    object1.put("time_slot", cartData[0].getTimeSlot());
                    object1.put("address", cartData[0].getAddress());
                    object1.put("special_notes", cartData[0].getSpecialNote());
                    object1.put("city_id_fk", mySharedData.getGeneralSaveSession(MySharedData.SESSION_CITY_ID));


                    for (int i = 0; i < cartData.length; i++) {
                        JSONObject object3 = new JSONObject();

                        object3.put("tiffin_id", cartData[i].getTiffinData().getTiffinId());
                        object3.put("price", cartData[i].getTiffinData().getTiffinPrice());
                        object3.put("quantity", cartData[i].getTiffinData().getQuantityPurchased());


                        String[] categories = cartData[i].getTiffinData().getCategoryName().split("_");
                        if (categories.length > 1) {
                            object3.put("type", "2");
                        } else {
                            object3.put("type", "1");
                        }


                        array1.put(object3);
                    }
                    object1.put("items", array1);
                    outerArray[index1] = object1;
                    index1++;

                }


            }


            for (int k = 0; k < outerArray.length; k++) {
                jsonArray2.put(outerArray[k]);
            }


            object.put("orders", jsonArray2);

            //
            // jsonArray.put(object);
            // object.put("Test",jsonArray);


        } catch (JSONException e) {
            // Handle impossible error
            e.printStackTrace();
        }

        return object.toString();
    }


}
