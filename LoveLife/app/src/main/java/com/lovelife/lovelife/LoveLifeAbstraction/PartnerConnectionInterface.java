package com.lovelife.lovelife.LoveLifeAbstraction;

import com.lovelife.lovelife.BeanClasses.BeanRequestsFromPeople;
import com.lovelife.lovelife.BeanClasses.ConnectedPartnerBean;

/**
 * Created by Avnish on 1/10/2017.
 */
public interface PartnerConnectionInterface {
    /**
     * function to handle new registration and nothing is happened
     */
    void noEventOccur();

    /**
     * function to handle pending requests and has been sent to partner
     */
    void requestIsPending(String partnerEmail);

    /**
     * function to handle received requests.
     */
    void requestsReceived(BeanRequestsFromPeople[] beanRequestsFromPeoples);

    void onPartnerConnected(ConnectedPartnerBean connectedPartnerBean);

    void onNetworkError();

    void sessionExpired();


}
