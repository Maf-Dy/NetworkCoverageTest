package com.mafdy.networkcoveragetest.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Legend {

    @SerializedName("RSRP")
    @Expose
    private List<RSRP> rSRP = null;
    @SerializedName("RSRQ")
    @Expose
    private List<RSRQ> rSRQ = null;
    @SerializedName("SINR")
    @Expose
    private List<SINR> sINR = null;

    public List<RSRP> getRSRP() {
        return rSRP;
    }

    public void setRSRP(List<RSRP> rSRP) {
        this.rSRP = rSRP;
    }

    public List<RSRQ> getRSRQ() {
        return rSRQ;
    }

    public void setRSRQ(List<RSRQ> rSRQ) {
        this.rSRQ = rSRQ;
    }

    public List<SINR> getSINR() {
        return sINR;
    }

    public void setSINR(List<SINR> sINR) {
        this.sINR = sINR;
    }



}
