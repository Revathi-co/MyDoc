package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmgDoctorProfile extends AppCompatActivity {

    private TextView docInfo;
    private Button bookAppoint,book;
    private ImageView doctorPic;
    private TextView date;
    private String DoctorId;
    String docName, docSpecialist, dt;
    String userName;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    Doctors doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emg_doctor_profile);

        docInfo = findViewById(R.id.tvDoctorProfile);
        bookAppoint = findViewById(R.id.btnBookAppoint);
        bookAppoint = findViewById(R.id.btnBookAppoint);
        book =findViewById(R.id.btnBook);
        date = findViewById(R.id.tvDate);
        doctorPic = findViewById(R.id.doctorPic);

        DoctorId = getIntent().getStringExtra("doctorID");
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Doctors");
        doctor = new Doctors();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    doctor = ds.getValue(Doctors.class);
                    assert doctor != null;
                    if (doctor.getDoctorID().equals(DoctorId)) {
                        String doc = (doctor.getDoctorName() + "\n " + doctor.getSpecialization() + "\n\nExperience: "
                                + doctor.getExperience() + "\nRating: " + doctor.getRating() + "\nQualification: " + doctor.getQualification()
                                + "\nCertification: " + doctor.getCertifications() + "\nlocation: " + doctor.getLocation() + "\n\n Fee: " + doctor.getFee());
                        docInfo.setText(doc);
                        docName = doctor.getDoctorName();
                        docSpecialist = doctor.getSpecialization();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bookAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = getIntent().getStringExtra("Mode");
                if(mode.equals("Emergency visit")){
                    sendNotification("Emergency visit Request", "Emergency visit request from");
                }
                else{
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(
                            EmgDoctorProfile.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            onDateSetListener,
                            year ,month, day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    bookAppoint.setVisibility(View.GONE);
                    date.setVisibility(View.VISIBLE);
                    book.setVisibility(View.VISIBLE);
                }
            }
        });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EmgDoctorProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year ,month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            private static final String TAG = "EmgDoctorProfile" ;

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                Log.d(TAG,"onDateSet: date: "+ dayOfMonth +"/"+ month +"/"+ year);

                dt= dayOfMonth+ "/"+ month+"/"+ year;
                date.setText(dt);
            }
        };

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification("Door-step Treatment Request", "Door_step Treatment request on "+ dt +" from");
            }
        });

    }
    private void sendNotification(final String title, final String message) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid());
        final String userId = FirebaseAuth.getInstance().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                assert userProfile != null;
                userName= userProfile.getName();

                Map<String,Object> notificationMessage = new HashMap<>();
                notificationMessage.put("Title",title);
                notificationMessage.put("Message",(message+ userName +" please, Accept the request"));
                notificationMessage.put("from",userId);
                firebaseFirestore.collection("Doctors/"+ DoctorId+ "/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(EmgDoctorProfile.this, "Visit Request sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EmgDoctorProfile.this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
