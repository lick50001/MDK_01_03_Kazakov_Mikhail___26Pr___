package com.example.security.datas.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {

    public ConnectivityManager Manager;

    public CheckInternet(Context context){
        Manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isWiFiConnection(){
        if (Manager == null) return false;

        NetworkInfo WiFiNetwork = Manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return WiFiNetwork != null && WiFiNetwork.isConnected();
    }

    public boolean isMobileConnection(){
        if (Manager == null) return false;

        NetworkInfo MobileNetwork = Manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return MobileNetwork != null && MobileNetwork.isConnected();
    }

}
