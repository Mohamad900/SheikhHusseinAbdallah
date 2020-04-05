package com.sheikh.hussein.abdallah.model;

import java.io.Serializable;


public class Settings implements Serializable {
    public int VersionCode;

    public Settings(){

    }

    public Settings(int versionCode){
        this.VersionCode=versionCode;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }
}