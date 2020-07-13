package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class DoorStepNotification extends AppCompatActivity {

    EditText address;
    TextView title, msg;
    Button send;
    String Title, message, docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_step_notification);

        address = findViewById(R.id.etAddress);
        title = findViewById(R.id.tvTitle);
        msg = findViewById(R.id.tvMsg);
        send = findViewById(R.id.btnSend);

        Title = getIntent().getStringExtra("title");
        message = getIntent().getStringExtra("msg");
        docId = getIntent().getStringExtra("docId");

        title.setText(Title);
        msg.setText(message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendNotification();
            }
        });


    }

    private void sendNotification() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid());
        final String userId = FirebaseAuth.getInstance().getUid();
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                assert userProfile != null;
                String userName = userProfile.getName();
                String adr = address.getText().toString();

                Map<String, Object> notificationMessage = new HashMap<>();
                notificationMessage.put("Title", "Address Notification");
                notificationMessage.put("Message"," Request from "+userName+" requested to visit the following location for treatment:\n\t\t"+adr);
                notificationMessage.put("from", userId);
                firebaseFirestore.collection("Doctors/" + docId + "/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(DoorStepNotification.this, "Notification sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoorStepNotification.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
