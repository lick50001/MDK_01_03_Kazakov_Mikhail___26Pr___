package com.example.security.datas.apis;

import android.util.Log;

import com.example.security.datas.common.CheckInternet;
import com.example.security.domains.apis.MyAsyncTask;
import com.example.security.domains.callbacks.MyResponseCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class UserGet extends MyAsyncTask {

    private String token;
    public UserGet(String token, CheckInternet checkInternet, MyResponseCallback callback) {
        super(checkInternet, callback);
        this.token = token;
    }

    @Override
    protected String doInBackground(Void... voids){
        if (!checkInternet.isWiFiConnection() && !checkInternet.isMobileConnection())
            return "Error : no internet connection";

        Log.d("UserGet", "Token: " + token);
        Log.d("UserGet", "URL: http://10.111.20.114:5000/api/user/get");

        try {
            Connection.Response response = Jsoup.connect("http://10.111.20.114:5000/api/user/get")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(Connection.Method.GET)
                    .header("token",  token)
                    .header("Accept", "application/json")
                    .execute();

            Log.d("UserGet", "Response code: " + response.statusCode());

            return response.statusCode() == 200
                    ? response.body()
                    : "Error: " + response.body();
        } catch (IOException e){
            return "Error: " + e.getMessage();
        }
    }
}
