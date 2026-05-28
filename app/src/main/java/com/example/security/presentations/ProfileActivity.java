package com.example.security.presentations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.security.R;
import com.example.security.datas.apis.UserGet;
import com.example.security.datas.common.CheckInternet;
import com.example.security.domains.callbacks.MyResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail, tvLastName, tvFirstName, tvSureName, tvGender;
    private Button btnLogout;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        tvEmail = findViewById(R.id.tv_email);
        tvLastName = findViewById(R.id.tv_lastname);
        tvFirstName = findViewById(R.id.tv_firstname);
        tvSureName = findViewById(R.id.tv_surename);
        tvGender = findViewById(R.id.tv_gender);
        btnLogout = findViewById(R.id.btn_logout);

        loadUserData();

        btnLogout.setOnClickListener(v ->{
            startActivity(new Intent(this, LogInActivity.class));
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserData(){
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        this.token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Токен не найден, войдите снова", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogInActivity.class));
            finish();
            return;
        }

        CheckInternet checkInternet = new CheckInternet(this);

        UserGet userGet = new UserGet(
                token,
                checkInternet,
                new MyResponseCallback() {
                    @Override
                    public void onComplete(String result) {
                        runOnUiThread(() -> {
                            try {

                                Log.d("PROFILE", "Данные пользователя: " + result);

                                JSONObject userData = new JSONObject(result);

                                tvEmail.setText("Email: " + userData.optString("email"));
                                tvLastName.setText("Фамилия: " + userData.optString("lastname"));
                                tvFirstName.setText("Имя: " + userData.optString("firstname"));
                                tvSureName.setText("Отчество: " + userData.optString("surname"));

                                int gender = userData.optInt("gender", 0);
                                String genderText = (gender == 0) ? "Мужской" : "Женский";
                                tvGender.setText("Пол: " + genderText);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProfileActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Log.e("PROFILE", "Ошибка: " + error);
                            Toast.makeText(ProfileActivity.this, "Ошибка загрузки: " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                });
        userGet.execute();

    }
}