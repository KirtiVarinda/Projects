package com.tiffindaddy.app.tiffindaddy.Manager;

import com.tiffindaddy.app.tiffindaddy.BeanClasses.TiffinData;

/**
 * Created by dx on 2/19/2016.
 */
public class MergeData {


    public static TiffinData[] mergeTwoBeans(TiffinData[] tiffinData, TiffinData[] addons) {

        boolean firstCheck=true;
        int index = 0;
        int totalLength = tiffinData.length + addons.length;

        TiffinData[] mergedData = new TiffinData[totalLength];

        for (index = 0; index < tiffinData.length; index++) {

            mergedData[index] = tiffinData[index];
        }

        for (int i = 0; i < addons.length; i++) {
            if(firstCheck){

                addons[i].setCategoryName(addons[i].getCategoryName()+"_first");
                firstCheck=false;
            }

            mergedData[index] = addons[i];
            index++;
        }


        return mergedData;

    }


}
