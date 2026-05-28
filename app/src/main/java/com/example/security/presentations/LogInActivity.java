package com.example.security.presentations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.security.BiometricsHelper;
import com.example.security.R;
import com.example.security.datas.apis.UserLogin;
import com.example.security.datas.common.CheckInternet;
import com.example.security.domains.callbacks.MyResponseCallback;
import com.example.security.domains.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {

    private BiometricsHelper biometricsHelper;
    private TextView etEmail, etPassword;
    private Button btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogIn = findViewById(R.id.btn_log_in);

        TextView btnOpenSingIn = findViewById(R.id.btn_open_sign_in);
        btnOpenSingIn.setOnClickListener(v -> {
            Intent SingIn = new Intent(this, RegInActivity.class);
            startActivity(SingIn);
        });

        TextView btnResetPassword = findViewById(R.id.btn_open_reset_password);
        btnResetPassword.setOnClickListener(v -> {
            Intent Reset = new Intent(this, ResetPasswordActivity.class);
            startActivity(Reset);
        });

        // Проверяем, можно ли использовать биометрию
        checkForBiometricLogin();

        btnLogIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(this, "Не указана почта пользователя", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!email.matches("^[aA-zZ.?&,]{2,20}@[aA-zZ]{2,20}.[aA-zZ]{2,3}$")){
                Toast.makeText(this, "Формат почты должен быть такой: xx@xx.xx", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (password.isEmpty()){
                Toast.makeText(this, "Не указан пароль пользователя", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                requestUserLogin(email, password);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkForBiometricLogin() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean useBiometric = prefs.getBoolean("use_biometric", false);
        String token = prefs.getString("auth_token", null);

        if (useBiometric && token != null && !token.isEmpty()) {
            // Показываем биометрический вход
            showBiometricLogin();
        }
    }

    private void showBiometricLogin() {
        biometricsHelper = new BiometricsHelper(this, biometricCallback);
        biometricsHelper.show();
    }

    private final BiometricPrompt.AuthenticationCallback biometricCallback =
            new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
                        finish();
                    });
                }

                @Override
                public void onAuthenticationFailed() {
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Отпечаток не распознан. Войдите с паролем.", Toast.LENGTH_SHORT).show();
                        showLoginForm();
                    });
                }

                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    runOnUiThread(() -> {
                        Toast.makeText(LogInActivity.this, "Ошибка: " + errString, Toast.LENGTH_SHORT).show();
                        showLoginForm();
                    });
                }
            };

    private void showLoginForm() {
        etEmail.setVisibility(View.VISIBLE);
        etPassword.setVisibility(View.VISIBLE);
        btnLogIn.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_open_sign_in).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_open_reset_password).setVisibility(View.VISIBLE);
    }

    public void requestUserLogin(String email, String password) {
        Context context = this;
        CheckInternet checkInternet = new CheckInternet(this);

        User User = new User();
        User.email = email;
        User.password = password;

        UserLogin RequestUserLogin = new UserLogin(
                User,
                checkInternet,
                new MyResponseCallback() {
                    @Override
                    public void onComplete(String result) {
                        Log.d("USER LOGIN", result);
                        try {
                            JSONObject json = new JSONObject(result);
                            String token = json.getString("token");

                            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                            prefs.edit().putString("auth_token", token).apply();

                            // После успешного входа спрашиваем про биометрию
                            askForBiometricConsent();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("USER LOGIN", error);
                        runOnUiThread(() -> {
                            Toast.makeText(context, "При авторизации возникли ошибки", Toast.LENGTH_SHORT).show();
                            etEmail.setText("");
                            etPassword.setText("");
                        });
                    }
                });
        RequestUserLogin.execute();
    }

    private void askForBiometricConsent() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Быстрый вход")
                .setMessage("Хотите использовать отпечаток пальца для быстрого входа в следующий раз?")
                .setPositiveButton("Да", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("use_biometric", true).apply();
                    Toast.makeText(this, "Биометрия сохранена", Toast.LENGTH_SHORT).show();

                    // Переходим в профиль
                    startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
                    finish();
                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("use_biometric", false).apply();

                    // Переходим в профиль
                    startActivity(new Intent(LogInActivity.this, ProfileActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}