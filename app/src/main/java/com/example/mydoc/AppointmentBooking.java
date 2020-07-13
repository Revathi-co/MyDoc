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

public class AppointmentBooking extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner, appoint;
    private ListView listView;
    private  String specialization, item;
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
        setContentView(R.layout.activity_appointment_booking);

        doctor = new Doctors();
        listView = findViewById(R.id.listDoctor);
        search = findViewById(R.id.btnAppointSearch);

        spinner = findViewById(R.id.etSpecialist);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Specialization, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(AppointmentBooking.this);


        appoint = findViewById(R.id.apSpinner);
        final ArrayAdapter<CharSequence> ap = ArrayAdapter.createFromResource(this, R.array.Appoint, R.layout.support_simple_spinner_dropdown_item);
        ap.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        appoint.setAdapter(ap);
        appoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                            if(item.equals("Emergency Appointment")){
                                if ((doctor.getSpecialization().equals(specialization) && doctor.getStatus().equals("online"))) {
                                    doctorsList.add(doctor.getDoctorName()+ "\n " + doctor.getSpecialization() + "\nExperience: "
                                            + doctor.getExperience()
                                            + "\nRating:" + doctor.getRating() + "\n\n Fee: " + doctor.getFee());
                                    docID.add(doctor.getDoctorID());

                                }
                            }else {

                                if ((doctor.getSpecialization().equals(specialization))) {
                                    doctorsList.add(doctor.getDoctorName()+ "\n " + doctor.getSpecialization() + "\nExperience: "
                                            + doctor.getExperience()
                                            + "\nRating:" + doctor.getRating() + "\n\n Fee: " + doctor.getFee());
                                    docID.add(doctor.getDoctorID());

                                }
                            }
                        }
                        final CustomAdapter customAdapter = new CustomAdapter(AppointmentBooking.this,doctorsList,docID);
                        listView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
// custom adapter for listView
    public class CustomAdapter extends BaseAdapter{
        Context c;
        ArrayList<String> doctorsList;
        ArrayList<String> docId;
        LayoutInflater layoutInflater = null;

        CustomAdapter(Context c, ArrayList<String> doctorsList, ArrayList<String> docId) {
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
                   Intent intent = new Intent(AppointmentBooking.this, DoctorProfile.class);
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
