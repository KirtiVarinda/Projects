package com.lovelife.lovelife.LoveLifeUtility;

import android.content.Context;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;
import com.lovelife.lovelife.JsonParsing.ParseJsonData;
import com.lovelife.lovelife.LoveLifeAbstraction.PartnerConnectionInterface;
import com.lovelife.lovelife.NetworkManager.ServerSync;
import com.lovelife.lovelife.R;
import com.lovelife.lovelife.SharedData.MySharedData;
import com.lovelife.lovelife.StringResources.ManagePartnerResource;

import org.json.JSONException;

import java.util.Map;

/**
 * Created by dx on 1/10/2017.
 */
public class ManagePartnerConnection {

    private static String GET_REQUEST;
    private Context context;
    PartnerConnectionInterface partnerConnectionInterface;

    public ManagePartnerConnection(Context context, PartnerConnectionInterface partnerConnectionInterface) {
        /** initialize url for getting requests */
        this.context = context;
        this.partnerConnectionInterface = partnerConnectionInterface;
        GET_REQUEST = context.getResources().getString(R.string.serverDomain) + context.getResources().getString(R.string.getRequests);
    }


    public void loadRequests() {
        MySharedData mySharedData = new MySharedData(context);
        String emailAfterLogin = mySharedData.getGeneralSaveSession(MySharedData.USEREMAIL);
        String userIDAfterLogin = mySharedData.getGeneralSaveSession(MySharedData.USERID);
        String tokenafetrLogin = mySharedData.getGeneralSaveSession(MySharedData.TOKEN);

        final Map<String, String> postData = getKeyPairValueInHashMap(userIDAfterLogin, tokenafetrLogin, emailAfterLogin);
        /** get request from server */

        try {
            /**
             * Make Connection and Pass URL to get response
             */
            getRequests(postData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Method that create key value pair for sending dta in post
     *
     * @return
     */
    private Map<String, String> getKeyPairValueInHashMap(String userID, String token, String email) {

        /**
         * make key and values array for all the strings.
         * index of both should be same.
         */
        String key[] = {ManagePartnerResource.EMAIL, ManagePartnerResource.TOKEN, ManagePartnerResource.USER_ID};      // set keys
        String value[] = {email, token, userID};                //set values


        /**
         * Get key value pairs .
         * KeyValues will be in hash map.
         *
         */
        PairValues pairValues = new PairValues();
        return pairValues.funcardPairValue(key, value);
    }

    /**
     * get all connection request from server
     *
     * @throws JSONException
     */
    public void getRequests(Map<String, String> postData) throws JSONException {

        /** Get Cities From Restaurant Server */
        ServerSync serverSync = new ServerSync();
        String response = serverSync.SyncServer(postData, GET_REQUEST);


        System.out.println("request response connection= " + response);

        if (response.equals(ServerSync.DEFAULT_RESPONSE_VALUE) || response.equals(ServerSync.HTTP_ERROR)) {/** If network error occurs */

            /**
             * if there is a network problem while registering user on server.
             * them shows the network error.
             */
            //  showError();
            partnerConnectionInterface.onNetworkError();


        } else {

            String no_requests = ParseJsonData.checkStatus(response);

            if (no_requests.equals("no_request")) {

                partnerConnectionInterface.noEventOccur();

            } else if(no_requests.equals("fail")){ // if user session expire or login on another device then logout
                String message = ParseJsonData.getMessage(response);
                if(message.equals("session_expire")){
                    partnerConnectionInterface.sessionExpired();
                }

            }else {

                String connected = ParseJsonData.getValueWithKey(response, "connected");
                final String partner = ParseJsonData.getValueWithKey(response, "partner");

                if (connected.equals("yes")) {

                    ConnectedPartnerBean connectedPartnerBean = ParseJsonData.setConnectedPartnerJson(response,"partnerDetail");
                    partnerConnectionInterface.onPartnerConnected(connectedPartnerBean);

                } else if (connected.equals("pending")) {

                    partnerConnectionInterface.requestIsPending(partner);

                } else {

                    String status = ParseJsonData.checkStatus(response);
                    if (status.equals("yes")) {

                        BeanRequestsFromPeople[] beanRequestsFromPeoples = ParseJsonData.parseRequestsFromPeople(response);
                        partnerConnectionInterface.requestsReceived(beanRequestsFromPeoples);

                    } else if (status.equals("no")) {

                        partnerConnectionInterface.noEventOccur();
                    }


                }

            }


        }
    }


}
