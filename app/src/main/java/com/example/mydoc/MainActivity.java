package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText Name,Password;
    private TextView ForgotPassword, NewRegistry, InvalidInfo;
    private Button Login;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;
    private int counter= 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Name = (EditText)findViewById(R.id.etName);
        Password = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);
        ForgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        NewRegistry = (TextView)findViewById(R.id.tvRegister);
        InvalidInfo = (TextView)findViewById(R.id.tvInvalidMsg);
        InvalidInfo.setText(" ");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user!=null) {
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
        NewRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            }
        });
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordActivity.class));
            }
        });


    }
    private void validate(String userName, String userPassword){
        progressDialog.setMessage("please wait....");
        progressDialog.show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String token_id = FirebaseInstanceId.getInstance().getToken();
                    String current_id = firebaseAuth.getCurrentUser().getUid();

                    Map<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put("token_id", token_id);

                    firebaseFirestore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();
                            checkEmailVerification();
                        }
                    });

                } else {
                    counter--;
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                    InvalidInfo.setText("Incorrect Username or Password ");
                    if(counter == 0) {
                        Login.setEnabled(false);
                    }
                }
            }
        });
    }
    private  void checkEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        boolean emailFlag = firebaseUser.isEmailVerified();
        if(emailFlag){
            finish();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }else{
            Toast.makeText(this, "verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

}
