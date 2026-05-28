package com.example.security;

import android.content.Context;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricsHelper {

    private final BiometricPrompt biometricPrompt;
    private final BiometricPrompt.PromptInfo promptInfo;
    private final Context context;

    public BiometricsHelper(Context context, BiometricPrompt.AuthenticationCallback callback) {
        this.context = context;

        Executor executor = ContextCompat.getMainExecutor(context);
        if (!(context instanceof androidx.appcompat.app.AppCompatActivity)) {
            throw new IllegalArgumentException("Context must be an AppCompatActivity");
        }

        biometricPrompt = new BiometricPrompt((androidx.appcompat.app.AppCompatActivity) context, executor, callback);

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Авторизация по отпечатку")
                .setSubtitle("Приложите палец к сканеру")
                .setDescription("Подтвердите личность для входа")
                .setNegativeButtonText("Отмена")
                .build();
    }

    public void show() {
        if (isBiometricAvailable()) {
            biometricPrompt.authenticate(promptInfo);
        }else {
            Toast.makeText(context, "Биометрия недоступна или не настроена", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS;
    }
}