package com.example.security.presentations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.security.R;
import com.example.security.datas.apis.UserCreate;
import com.example.security.datas.common.CheckInternet;
import com.example.security.domains.callbacks.MyResponseCallback;
import com.example.security.domains.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class RegInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg_in);

        TextView btnOpenLogin = findViewById(R.id.btn_open_log_in);

        btnOpenLogin.setOnClickListener(v -> {
            Intent RegIn = new Intent(this, LogInActivity.class);
            startActivity(RegIn);
        });

        Button btnRegIn = findViewById(R.id.appCompatButtonRegIn);

        btnRegIn.setOnClickListener(v -> {
            TextView Email = findViewById(R.id.EmailRegIn);
            TextView Lastname = findViewById(R.id.LastnameRegIn);
            TextView Firstname = findViewById(R.id.FirstnameRegIn);
            TextView Surename = findViewById(R.id.SurenameRegIn);
            Spinner Sex = findViewById(R.id.SexRegIn);
            TextView Password = findViewById(R.id.PasswordRegIn);

            String email = Email.getText().toString();
            String lastname = Lastname.getText().toString();
            String firstname = Firstname.getText().toString();
            String surename = Surename.getText().toString();
            String password = Password.getText().toString();
            Integer sex = Sex.getSelectedItemPosition();

            if (email.isEmpty()){
                Toast.makeText(this, "Пожалуйста, введите свою почту", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!email.matches("^[aA-zZ.?&,]{2,20}@[aA-zZ]{2,20}.[aA-zZ]{2,3}$")){
                Toast.makeText(this, "Формат почты должен быть такой: xx@xx.xx", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (lastname.isEmpty()){
                Toast.makeText(this, "Пожалуйста, введите свою фамилию", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (firstname.isEmpty()){
                Toast.makeText(this, "Пожалуйста, введите своё имя", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (surename.isEmpty()){
                Toast.makeText(this, "Пожалуйста, введите своё отчество", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (sex == null){
                Toast.makeText(this, "Пожалуйста, выберите пол", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (password.isEmpty()){
                Toast.makeText(this, "Пожалуйста, придумайте пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Email.setText("");
                Lastname.setText("");
                Firstname.setText("");
                Surename.setText("");
                Sex.setSelection(0);
                Password.setText("");
            }

            requestUserCreate(email, lastname, firstname, surename, sex, password);

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void requestUserCreate(String email, String lastname, String firstname, String surname, Integer gender, String password){
        Context context = this;
        CheckInternet checkInternet = new CheckInternet(this);

        User User = new User();
        User.email = email;
        User.lastname = lastname;
        User.firstname = firstname;
        User.surname = surname;
        User.gender = gender;
        User.password = password;

        UserCreate RequestUserCreate = new UserCreate(
                User,
                checkInternet,
                new MyResponseCallback() {
                    @Override
                    public void onComplete(String result) {
                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            String token = jsonResponse.getString("token");

                            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                            prefs.edit().putString("auth_token", token).apply();

                            Toast.makeText(context, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Ошибка при обработке ответа", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("USER REGISTER", "Ошибка: " + error);
                        runOnUiThread(() -> {
                            Toast.makeText(context, "При регистрации произошла ошибка", Toast.LENGTH_LONG).show();
                        });
                    }
                });
        RequestUserCreate.execute();

    }
}