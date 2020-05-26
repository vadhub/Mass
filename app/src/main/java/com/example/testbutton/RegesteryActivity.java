package com.example.testbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegesteryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText login;
    private EditText password;
    private TextView haveAccount;
    private TextView nameUser;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestery);

        db = FirebaseFirestore.getInstance();

        nameUser = (EditText) findViewById(R.id.nameUs);
        login = (EditText) findViewById(R.id.email);
        password =(EditText) findViewById(R.id.password);
        haveAccount = (TextView) findViewById(R.id.haveAccount);

        mAuth = FirebaseAuth.getInstance();

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegesteryActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void saveData(){
        sharedPreferences = getSharedPreferences("limit_times", MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong("limit_time", 10000);
        ed.commit();
    }

    private long loadData(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getLong("limit_time", 0);
    }

    public void onCLickRegestery(View view) {
        final String email = login.getText().toString().trim();

        String passwordUser = password.getText().toString().trim();

        if(email.isEmpty() || passwordUser.isEmpty()){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
//regester with userID
                    db.collection("users").document(mAuth.getUid()).set(new User(nameUser.getText().toString(), email, System.currentTimeMillis(), ""));
                    Intent intent = new Intent(RegesteryActivity.this, MainActivity.class);
                    Toast.makeText(RegesteryActivity.this, "Successful", Toast.LENGTH_LONG).show();
                    saveData();
                    System.out.println("==============+++"+ loadData());
                    startActivity(intent);
                }else{
                    Toast.makeText(RegesteryActivity.this, "error"+ task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });



    }
}
