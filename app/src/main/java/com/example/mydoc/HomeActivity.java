package com.example.mydoc;

import  android.content.Intent;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private TextView dailyTips, bookAppointment, onlineCon, emergencyBook;
    private Button viewNext;
    private ArrayList<HealthTips> healthTips;
    private  int index;
    ViewFlipper v_flipper;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();

        manageConnections();

        //Image scrolling
        int image[] = {R.drawable.pic1,R.drawable.pic2,R.drawable.pic3,R.drawable.pic4,R.drawable.pic5};
        v_flipper = findViewById(R.id.v_flipper);

        for (int i=0; i<image.length; i++){
            flipperImage(image[i]);
        }

        //Appointment booking
        bookAppointment = findViewById(R.id.tvPersonalAppoint);
        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AppointmentBooking.class));
            }
        });

        //Online Consultation
        onlineCon = findViewById(R.id.tvOnlineCon);
        onlineCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, OnlineConsultation.class));
            }
        });

        //Emergency booking
        emergencyBook = findViewById(R.id.tvDoorStep);
        emergencyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, EmergencyVisit.class));
            }
        });

        //generation of health tips
        dailyTips = findViewById(R.id.tvHealthTip);
        viewNext = findViewById(R.id.btnViewNext);

        Resources res = getResources();
        String[] allTips = res.getStringArray(R.array.Health_tips);
        String[] allIssues = res.getStringArray(R.array.Issue);
        healthTips = new ArrayList<>();
        addToTipsList(allTips,allIssues);

        final  int tipSize = healthTips.size();
        index = getRandomTip(tipSize);
        dailyTips.setText(healthTips.get(index).toString());

        viewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = getRandomTip(tipSize-1);
                dailyTips.setText(healthTips.get(index).toString());
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_details, R.id.nav_UploadPR, R.id.nav_notify, R.id.nav_Logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(HomeActivity.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.bringToFront();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                       startActivity(new Intent(HomeActivity.this,HomeActivity.class));
                       break;
                    case R.id.nav_details:
                        startActivity(new Intent(HomeActivity.this,UserDetailsActivity.class));
                        break;
                    case R.id.nav_UploadPR:
                        Toast.makeText(HomeActivity.this, "upload the past history", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_notify:
                        startActivity(new Intent(HomeActivity.this,NotificationActivity.class));
                        break;
                    case R.id.nav_Logout:

                       String current_id = firebaseAuth.getCurrentUser().getUid();

                        Map<String, Object> tokenRemove = new HashMap<>();
                        tokenRemove.put("token_id", FieldValue.delete());

                      firebaseFirestore.collection("Users").document(current_id).set(tokenRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(HomeActivity.this, MainActivity.class));

                           }
                      });
                                break;
                }

                drawer.closeDrawers();

                return false;
            }
        });


    }

    private void manageConnections() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid());
        final DatabaseReference connection = FirebaseDatabase.getInstance().getReference(".info/connected");
        connection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = dataSnapshot.getValue(Boolean.class);
                 if(flag){
                     DatabaseReference ref = reference.child("Status");
                     ref.setValue("online");
                     ref.onDisconnect().setValue("offline");
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Methods for image scrolling
    public void flipperImage(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(5000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this,android.R.anim.slide_out_right);

    }


    // Methods for generating health tips
    public  void addToTipsList(String[] allTips, String[] allIssues){
        for(int i=0; i<allTips.length; i++){
            String tips = allTips[i];
            String issue = allIssues[i];
            HealthTips newHealthTips = new HealthTips(tips,issue);
            healthTips.add(newHealthTips);
        }
    }

    public int getRandomTip(int length){
        return (int)(Math.random()* length)+1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
