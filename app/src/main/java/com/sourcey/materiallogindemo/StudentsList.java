package com.sourcey.materiallogindemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.util.ArrayList;

import butterknife.BindView;

public class StudentsList extends AppCompatActivity {

    @BindView(R.id.students_list)
    NeverEmptyListView _studentsList;
    ArrayList<String> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        String school = getIntent().getStringExtra("school");
        String year = getIntent().getStringExtra("year");
        String subject = getIntent().getStringExtra("subject");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("system").child("schools").child(school).child(year);
        students = new ArrayList<>();

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, students);
        _studentsList.setAdapter(adapter);

    }
}
