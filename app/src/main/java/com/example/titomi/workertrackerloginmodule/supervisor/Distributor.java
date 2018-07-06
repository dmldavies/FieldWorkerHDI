package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class Distributor extends Entity implements Serializable {
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;
    protected static final long serialVersionUID = 1l;
}
