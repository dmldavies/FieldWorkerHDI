package com.example.titomi.workertrackerloginmodule.apis.rest;

import com.squareup.otto.Bus;

/**
 * Created by Titomi on 2/20/2018.
 */

public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }

    public BusProvider(){}
}
