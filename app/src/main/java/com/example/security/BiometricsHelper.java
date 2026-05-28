package com.example.security;

import android.content.Context;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricsHelper {
    Context context;
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.AuthenticationCallback callback;
    BiometricPrompt.PromptInfo promptInfo;

    public BiometricsHelper(MainActivity activity, BiometricPrompt.AuthenticationCallback callback){
        this.context = activity.getApplicationContext();
        this.callback = callback;
        this.executor = ContextCompat.getMainExecutor(activity);

        biometricPrompt = new BiometricPrompt(activity, this.executor, this.callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация по отпечатку")
                .setSubtitle("Приложите палец к сканеру")
                .setDescription("Подтвердите личность для входа")
                .setNegativeButoonText("Отмена")
                .build();
    }

    public void show(){
        if (isBiometricAvailable()){
            biometricPrompt.authenticate(promptInfo);
        }else
            Log.d("BiometricsHelper", "Биометрия недоступна на устройстве");
    }

    boolean isBiometricAvailable(){
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }
}
