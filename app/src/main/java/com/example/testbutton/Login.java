package com.example.testbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    private TextView regestar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.email_login);
        password = (EditText) findViewById(R.id.password_login);

        regestar = (TextView) findViewById(R.id.regist);

        mAuth = FirebaseAuth.getInstance();

        regestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegesteryActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onCLickLogining(View view) {
        String emailUser = email.getText().toString().trim();

        String passwordUser = password.getText().toString().trim();

        if(emailUser.isEmpty() || passwordUser.isEmpty()){
            return;
        }

        mAuth.signInWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Toast.makeText(Login.this, "ok mar", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }else{
                    Toast.makeText(Login.this, "error"+ task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
