package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by NB VENKATESHWARULU on 11/28/2016.
 */
public class RideList {
    private String destination;
    private String pickup;
    private String drivername;
    private Date ridedate;

    public RideList() {

    }

    public RideList(String destination, String pickup, String drivername, Date ridedate) {
        this.destination = destination;
        this.pickup = pickup;
        this.drivername = drivername;
        this.ridedate = ridedate;
    }


    public Date getRidedate() {
        return ridedate;
    }

    public void setRidedate(Date ridedate) {
        this.ridedate = ridedate;
    }

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }



    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }









}
