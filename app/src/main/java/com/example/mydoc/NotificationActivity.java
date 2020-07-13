package com.example.mydoc;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class NotificationActivity extends AppCompatActivity {

    ListView notificationView;
    ArrayList<String> notificationTitle;
    ArrayList<String> notificationMsg;
    ArrayList<String> docId;
    TextView nullNotification;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationView = findViewById(R.id.lvNotification);
        nullNotification = findViewById(R.id.tvNullNotification);

        notificationTitle = new ArrayList<>();
        notificationMsg = new ArrayList<>();
        docId = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String current_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(current_id).collection("Notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                assert queryDocumentSnapshots != null;
                for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                    notificationTitle.add(snapshot.getString("Title"));
                    notificationMsg.add(snapshot.getString("Message"));
                    docId.add(snapshot.getString("from"));

                    if (notificationTitle.isEmpty()) {
                        notificationView.setVisibility(View.GONE);
                        nullNotification.setVisibility(View.VISIBLE);

                    } else {
                        CustomAdapter customAdapter = new CustomAdapter(NotificationActivity.this, notificationTitle, notificationMsg, docId);
                        notificationView.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                    }

                }
            }
        });

    }

    class CustomAdapter extends BaseAdapter {
        Context c;
        ArrayList<String> notificationTitle;
        ArrayList<String> notificationMsg;
        ArrayList<String> docId;
        LayoutInflater layoutInflater;

        CustomAdapter(Context c, ArrayList<String> notificationTitle, ArrayList<String> notificationsMsg, ArrayList<String> docId) {
            this.c = c;
            this.notificationTitle = notificationTitle;
            this.notificationMsg = notificationsMsg;
            this.docId = docId;
            this.layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return notificationTitle.size();
        }

        @Override
        public Object getItem(int position) {
            return notificationTitle.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.notify, null);
            }
            TextView notifyTitle = convertView.findViewById(R.id.tvTitle);
            TextView notifyView =  convertView.findViewById(R.id.tvNotification);
            notifyTitle.setText(notificationTitle.get(position));
            notifyView.setText(notificationMsg.get(position));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(notificationTitle.get(position).equals("Door-step Treatment Request")&& notificationTitle.get(position).equals("Emergency visit Request")){
                        Intent intent = new Intent(NotificationActivity.this, DoorStepNotification.class);
                        intent.putExtra("docId",docId.get(position));
                        intent.putExtra("title",notificationTitle.get(position));
                        intent.putExtra("msg",notificationMsg.get(position));
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }
    }
}



