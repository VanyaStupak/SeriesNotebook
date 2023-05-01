package com.example.seriesnotebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {
    private TextView signUpTextView;
    private TextView wrongInfoTextView;
    private Button signInButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.button_sign_in);
        signUpTextView = findViewById(R.id.tv_sign_up);
        emailEditText = findViewById(R.id.edit_text_sign_in_email);
        passwordEditText = findViewById(R.id.edit_text_sign_in_password);
        wrongInfoTextView = findViewById(R.id.tv_wrong_sign_in);

        //Кнопка реєстрації
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Кнопка входу в акаунт
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(emailEditText.getText().toString().trim().isEmpty() & passwordEditText.getText().toString().trim().isEmpty())){
                    firebaseAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }else{
                                        wrongInfoTextView.setText("Wrong password or email");
                                    }
                                }
                            });
                }
            }
        });
    }
}
