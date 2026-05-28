package com.example.security.domains.callbacks;

public interface MyResponseCallback {

    void onComplete(String result);

    void onError(String error);

}
