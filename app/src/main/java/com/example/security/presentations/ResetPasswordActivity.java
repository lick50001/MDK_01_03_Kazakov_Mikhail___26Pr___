package com.example.security.presentations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.security.R;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        TextView btnBack = findViewById(R.id.btn_open_back);

        btnBack.setOnClickListener(v -> {
            Intent Back = new Intent(this, LogInActivity.class);
            startActivity(Back);
        });

        Button btnReset = findViewById(R.id.btn_reset_password);

        btnReset.setOnClickListener(v -> {
            TextView Email = findViewById(R.id.et_email_reset);
            TextView PasswordNew = findViewById(R.id.et_password_new);
            TextView PasswordReset = findViewById(R.id.et_password_reset);

            String email = Email.getText().toString();
            String passwordNew = PasswordNew.getText().toString();
            String passwordReset = PasswordReset.getText().toString();

            if (email.isEmpty()){
                Toast.makeText(this, "Укажите вашу почту", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!email.matches("^[aA-zZ.?&,]{2,20}@[aA-zZ]{2,20}.[aA-zZ]{2,3}$")){
                Toast.makeText(this, "Формат почты должен быть такой: xx@xx.xx", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (passwordNew.isEmpty()){
                Toast.makeText(this, "Вы не указали новый пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (passwordReset.isEmpty()){
                Toast.makeText(this, "Повторите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!passwordReset.equals(passwordNew)){
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Email.setText("");
                PasswordNew.setText("");
                PasswordReset.setText("");
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}