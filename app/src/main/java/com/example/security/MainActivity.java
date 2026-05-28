package com.example.security;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Context context;
    BiometricsHelper biometricsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        Button btnLogin = new BiometricsHelper(this, biometricsHelper.callback);

        btnLogin.setOnClickListener(v -> {
            biometricsHelper.show();
        });
    }

    BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Toast.makeText(context, "Авторизация пройдена", Toast.LENGTH_SHORT)
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }
    }
}