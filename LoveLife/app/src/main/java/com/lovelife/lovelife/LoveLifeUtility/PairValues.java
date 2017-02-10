package com.lovelife.lovelife.LoveLifeUtility;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by dx on 7/9/2015.
 */
public class PairValues {

    /**
     * method to provide key value pair in hash map.
     *
     * @param key
     * @param value
     */
    public Map<String, String> funcardPairValue(String key[], String value[]) {
        Map<String, String> theMaptheMap = new HashMap<String, String>();
        /**
         * created key value pairs in map
         */
        for (int i = 0; i < key.length; i++) {
            theMaptheMap.put(key[i],value[i]);
        }
        return theMaptheMap;
    }


}
