package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfile extends AppCompatActivity {

    private static final String TAG = "UpdateProfile";

    private  FirebaseDatabase firebaseDatabase;
    private  FirebaseAuth firebaseAuth;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private EditText newName, newAge, newDOB, newPhone, newGender, newEmail;
       private Button update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        newName = findViewById(R.id.etRName);
        newAge = findViewById(R.id.etAge);
        newDOB = findViewById(R.id.etDOB);
        newGender = findViewById(R.id.etGender);
        newPhone = findViewById(R.id.etPhoneNo);
        update = findViewById(R.id.btnUpdate);
        newEmail = findViewById(R.id.etEmail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                newName.setText(userProfile.getName());
                newAge.setText(userProfile.getAge());
                newDOB.setText(userProfile.getDob());
                newGender.setText(userProfile.getGender());
                newEmail.setText(userProfile.getEmail());
                newPhone.setText(userProfile.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        newDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UpdateProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year ,month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d(TAG,"onDateSet: date: "+ dayOfMonth +"/"+ month +"/"+ year);

                String date= dayOfMonth+ "/"+ month+"/"+ year;
                newDOB.setText(date);
            }
        };

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newName.getText().toString();
                String age = newAge.getText().toString();
                String gender = newGender.getText().toString();
                String dob = newDOB.getText().toString();
                String email = newEmail.getText().toString();
                String phone = newPhone.getText().toString();

                UserProfile userProfile = new UserProfile(name,age,email,gender,phone,dob);
                databaseReference.setValue(userProfile);
                finish();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
