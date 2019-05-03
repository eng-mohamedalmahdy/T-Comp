package com.sourcey.materiallogindemo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.adapters.SimpleArrayListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;


public class NewStudentActivity extends AppCompatActivity {

    TextView _studentName;
    TextView _studentSchoolYear;
    SearchableSpinner _studentSchool;
    ArrayList<String> data;


    private FirebaseDatabase database;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);
        database = FirebaseDatabase.getInstance();
        Button _addStudent = findViewById(R.id.add_student);
        _studentSchoolYear = findViewById(R.id.student_school_year);
        _studentSchool = findViewById(R.id.student_school);
        _studentName = findViewById(R.id.student_name);
        data = new ArrayList<>();
        usersReference = database.getReference("system").child("schoolsData");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    data.add(ds.child("schoolName").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(NewStudentActivity.this, "request canceled", Toast.LENGTH_SHORT).show();

            }
        });

        _studentSchool.setAdapter(new SimpleArrayListAdapter(this, data));


        _addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String school = _studentSchool.getSelectedItem().toString();
                String year = _studentSchoolYear.getText().toString();
                if (!validate()) return;
                if (Integer.valueOf(year) < 1 || Integer.valueOf(year) > 3) {
                    Toast.makeText(NewStudentActivity.this, "invalid school year", Toast.LENGTH_SHORT).show();
                } else {
                    usersReference = database.getReference("system").child("schools").child(school).child("year " + year);
                    HashMap<String, String> newStudent = new HashMap<>();
                    String id = usersReference.push().getKey();
                    String name = _studentName.getText().toString();
                    newStudent.put("id", id);
                    newStudent.put("name", name);
                    newStudent.put("arabic", "0");
                    newStudent.put("english", "0");
                    newStudent.put("math", "0");
                    newStudent.put("science", "0");
                    newStudent.put("social", "0");
                    newStudent.put("art", "0");
                    newStudent.put("ie", "0");
                    newStudent.put("talents", "0");
                    usersReference.child(id).setValue(newStudent);
                    finish();
                }
            }
        });

    }



    private boolean validate() {
        boolean valid = true;
        if (_studentName.getText().toString().isEmpty()) {
            _studentName.setError("can't be empty");
            valid = false;
        } else {
            _studentName.setError(null);
        }
        if (_studentSchoolYear.getText().toString().isEmpty()) {
            _studentSchoolYear.setError("can't be empty");
            valid = false;
        } else {
            _studentSchoolYear.setError(null);
        }

        return valid;
    }
}
