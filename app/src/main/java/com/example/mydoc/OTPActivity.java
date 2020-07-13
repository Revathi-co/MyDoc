package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText phoneNo, userOTP;
    Button sendOTP, verifyButton;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks nCallback;
    String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        phoneNo = (EditText)findViewById(R.id.etPhoneNo);
        userOTP = (EditText)findViewById(R.id.etOTP);
        sendOTP = (Button)findViewById(R.id.btnSendOTP);
        verifyButton =(Button)findViewById(R.id.btnVerify);
        firebaseAuth = FirebaseAuth.getInstance();
        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(v);
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify(v);
            }
        });

    }
    public void sendSMS(View v){
        String number = phoneNo.getText().toString();
        if(number.length()!=10){
            phoneNo.setError("please enter a valid phone");
            phoneNo.requestFocus();
            return;
        }
        nCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),"OTP Failed! please enter correct phone no.",Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(OTPActivity.this,OTPActivity.class));
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(getApplicationContext(),"code sent to the number",Toast.LENGTH_SHORT).show();

            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber("91"+number,60, TimeUnit.SECONDS,this,nCallback);

    }

    public void signInPhone(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User signed in successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OTPActivity.this, HomeActivity.class));
                }
            }
        });
    }
    public void verify(View v) {
        String input_code = userOTP.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,input_code);
        signInPhone(credential);

    }

}

