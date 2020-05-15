package com.demo.demos.FindU.SearchByWiFi.core.SQLite;

public class DataBaseItem {

    private String mMac;
    private String mFirm;

    public DataBaseItem(){
        super();
    }
    public DataBaseItem(String mac, String firm){
        this.mMac = mac;
        this.mFirm = firm;
    }

    public String getmMac() {
        return mMac;
    }

    public void setmMac(String mMac) {
        this.mMac = mMac;
    }

    public String getmFirm() {
        return mFirm;
    }

    public void setmFirm(String mFirm) {
        this.mFirm = mFirm;
    }
}
