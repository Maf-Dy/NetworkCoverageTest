package com.mafdy.networkcoveragetest.model;

import com.google.gson.annotations.SerializedName;

public class NetworkReadings {

    @SerializedName("RSRP")
    private int RSRP;
    @SerializedName("RSRQ")
    private int RSRQ;
    @SerializedName("SINR")
    private int SINR;


    public int getSINR() {
        return SINR;
    }

    public void setSINR(int SINR) {
        this.SINR = SINR;
    }

    public int getRSRQ() {
        return RSRQ;
    }

    public void setRSRQ(int RSRQ) {
        this.RSRQ = RSRQ;
    }

    public int getRSRP() {
        return RSRP;
    }

    public void setRSRP(int RSRP) {
        this.RSRP = RSRP;
    }
}
