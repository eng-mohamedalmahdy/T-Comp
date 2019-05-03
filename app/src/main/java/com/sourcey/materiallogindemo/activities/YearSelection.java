package com.sourcey.materiallogindemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.materiallogindemo.R;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YearSelection extends AppCompatActivity {
    private String years;
    private String school;
    private String subject;
    String[] dataArray;


    @BindView(R.id.listview)
    NeverEmptyListView _yearsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_selection);
        ButterKnife.bind(this);
        String id = getIntent().getStringExtra("id");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("system").child("teachers").child(id);
        dataArray = new String[10];
        _yearsList.setHolderClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearSelection.super.onBackPressed();
            }
        });

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                years = dataSnapshot.child("years").getValue().toString();
                school = dataSnapshot.child("school").getValue().toString();
                subject = dataSnapshot.child("subject").getValue().toString();
                dataArray = years.split(",");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(YearSelection.this, R.layout.simple_list_item, R.id.text1, dataArray);
                _yearsList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {


            }
        });


        _yearsList.getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(YearSelection.this, StudentsList.class);
                i.putExtra("year", dataArray[position]);
                i.putExtra("school", school);
                i.putExtra("subject", subject);
                startActivity(i);
            }
        });


    }
}

