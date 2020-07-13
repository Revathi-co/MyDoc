package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DoctorProfile extends AppCompatActivity {

    private TextView docInfo;
    private Button bookAppoint,book;
    private String DoctorId;
    private TextView Date;
    private ImageView doctorPic;
    String docName, docSpecialist, date;
    String userName;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    FirebaseFirestore firebaseFirestore;
    Doctors doctor;
    private static final String TAG = "DoctorProfile";
    private final String CHANNEL_ID ="Booking Request";
    private final String CHANNEL_NAME ="Booking Request";
    private final int NOTIFICATION_ID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Booking request notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        docInfo = findViewById(R.id.tvDoctorProfile);
        bookAppoint = findViewById(R.id.btnBookAppoint);
        Date = findViewById(R.id.tvDate);
        bookAppoint = findViewById(R.id.btnBookAppoint);
        book =findViewById(R.id.btnBook);
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
                                + "\nCertification: " + doctor.getCertifications() + "\nlocation: "+ doctor.getLocation() + "\n\n Fee: " + doctor.getFee());
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
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("Doctors");
        storageReference.child(DoctorId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

            }
        });

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        DoctorProfile.this,
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

                date= dayOfMonth+ "/"+ month+"/"+ year;
                Date.setText(date);
            }
        };

        bookAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        DoctorProfile.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year ,month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                bookAppoint.setVisibility(View.GONE);
                Date.setVisibility(View.VISIBLE);
                book.setVisibility(View.VISIBLE);

            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                String title = "Appointment Request";
                String message = ("Booking request sent to "+ docName + ", "+docSpecialist+" on "+date+"\n Location and Appointment" +
                        " time will be send as notification");
                sendNotification(title);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        DoctorProfile.this, CHANNEL_ID
                )
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                Intent intent = new Intent(DoctorProfile.this, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title",title);
                intent.putExtra("message",message);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(DoctorProfile.this);
                stackBuilder.addParentStack(NotificationActivity.class);

                stackBuilder.addNextIntent(intent);
                PendingIntent pendingIntent = PendingIntent.getActivity(DoctorProfile.this,
                        0, intent , PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.notify(001, builder.build());
            }
        });

    }

    private void sendNotification(final String title) {

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

                String message = ("Appointment request from "+ userName+" on date: "+ date+
                        "\n please send the location and Appointment timings");
                Map<String,Object> notificationMessage = new HashMap<>();
                notificationMessage.put("Title",title);
                notificationMessage.put("Message",message);
                notificationMessage.put("from",userId);
                firebaseFirestore.collection("Doctors/"+ DoctorId+ "/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(DoctorProfile.this, "Booking Request sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorProfile.this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
