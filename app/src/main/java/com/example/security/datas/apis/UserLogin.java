package com.example.security.datas.apis;

import com.example.security.datas.common.CheckInternet;
import com.example.security.domains.apis.MyAsyncTask;
import com.example.security.domains.callbacks.MyResponseCallback;
import com.example.security.domains.models.User;
import com.google.gson.GsonBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class UserLogin extends MyAsyncTask {

    private User user;
    public UserLogin(User user,CheckInternet checkInternet, MyResponseCallback callback) {
        super(checkInternet, callback);
        this.user = user;
    }

    @Override
    protected String doInBackground(Void... voids){
        if (!checkInternet.isWiFiConnection() && !checkInternet.isMobileConnection())
            return "Error : no internet connection";

        String rawData = new GsonBuilder().create().toJson(this.user);

        try {
            Connection.Response response = Jsoup.connect("http://10.111.20.114:5000/api/user/login")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(Connection.Method.POST)
                    .header("Content-type", "application/json")
                    .requestBody(rawData)
                    .execute();

            return response.statusCode() == 200
                    ? response.body()
                    : "Error: " + response.statusCode() + ": " + response.body();
        } catch (IOException e){
            return "Error: " + e.getMessage();
        }
    }
}
