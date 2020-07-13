package com.example.mydoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlineConsultation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private ListView listView;
    private  String specialization;
    private Button search;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Doctors doctor;
    DatabaseReference reference;
    ArrayList<String> doctorsList;
    ArrayList<String> docID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_consultation);

        doctor = new Doctors();
        listView = findViewById(R.id.listDoctor);
        search = findViewById(R.id.btnOnlineConSearch);


        spinner = findViewById(R.id.etSpecialist);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Specialization, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(OnlineConsultation.this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();
                reference = firebaseDatabase.getReference("Doctors");
                doctorsList = new ArrayList<>();
                docID = new ArrayList<>();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {

                            doctor = ds.getValue(Doctors.class);
                            assert doctor != null;
                            if ((doctor.getSpecialization().equals(specialization.toString()))) {
                                doctorsList.add(doctor.getDoctorName().toString() + "\n " + doctor.getSpecialization().toString() + "\nExperience: "
                                        + doctor.getExperience().toString() + "\nRating:" + doctor.getRating().toString() + "\n\n Fee: " + doctor.getFee());
                                docID.add(doctor.getDoctorID());

                            }
                        }
                        final CustomAdapter customAdapter = new CustomAdapter(OnlineConsultation.this,doctorsList,docID);
                        listView.setAdapter(customAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Toast.makeText(OnlineConsultation.this,"hi", Toast.LENGTH_SHORT).show();


                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public class CustomAdapter extends BaseAdapter{
        Context c;
        ArrayList<String> doctorsList;
        ArrayList<String> docId;
        LayoutInflater layoutInflater = null;

        public CustomAdapter(Context c, ArrayList<String> doctorsList, ArrayList<String> docId) {
            this.c = c;
            this.doctorsList = doctorsList;
            this.docId = docId;
            this.layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return doctorsList.size();
        }

        @Override
        public Object getItem(int position) {
            return doctorsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = layoutInflater.inflate(R.layout.doctor_list, null);
            }

            TextView doctorsInfo = (TextView) convertView.findViewById(R.id.tvDoctor);
            Button docView = (Button) convertView.findViewById(R.id.btnDoctorView);

            doctorsInfo.setText(doctorsList.get(position).toString());
            final String doc = docId.get(position);

            docView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OnlineConsultation.this, DoctorProfile.class);
                    intent.putExtra("doctorID",doc);
                    startActivity(intent);
                }
            });


            return convertView;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        specialization = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), specialization, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
