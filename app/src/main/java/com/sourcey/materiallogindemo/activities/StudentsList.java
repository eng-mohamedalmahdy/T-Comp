package com.sourcey.materiallogindemo.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.adapters.Student;
import com.sourcey.materiallogindemo.adapters.StudentsAdapter;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentsList extends AppCompatActivity {

    @BindView(R.id.students_list)
    NeverEmptyListView _studentsList;
    ArrayList<Student> students;
    DatabaseReference myRef;
    FirebaseDatabase database;
    ArrayAdapter<Student> adapter;
    Dialog updateGrade;
    String school;
    String year;
    String subject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);
        ButterKnife.bind(this);
        updateGrade = new Dialog(this);
        school = getIntent().getStringExtra("school");
        year = getIntent().getStringExtra("year");
        subject = getIntent().getStringExtra("subject");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("system").child("schools");
        final ProgressDialog progressDialog = new ProgressDialog(StudentsList.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        students = new ArrayList<>();
        adapter = new StudentsAdapter(this, students);
        progressDialog.setMessage("loading data...");
        progressDialog.show();


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                students = new ArrayList<>();

                dataSnapshot = dataSnapshot.child(school.trim()).child(year.trim());

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.child("id").getValue().toString();
                    String name = ds.child("name").getValue().toString();
                    double grade = Double.valueOf(ds.child(subject).getValue().toString());
                    Student student = new Student(id, name, grade);
                    students.add(student);
                }

                adapter = new StudentsAdapter(StudentsList.this, students);
                _studentsList.setAdapter(adapter);
                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(StudentsList.this, "error loading the data", Toast.LENGTH_SHORT).show();


            }
        });

        _studentsList.getListview().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopup(parent, position);
                return true;
            }
        });

    }

    public void showPopup(View v, int i) {
        updateGrade.setContentView(R.layout.update_grade_dialoge);
        final Student student = students.get(students.size() - 1 - i);

        TextView name = updateGrade.findViewById(R.id.student_name);
        name.setText(String.format("Last exam grade for student : %s", student.getName()));

        final EditText newGrade = updateGrade.findViewById(R.id.last_grade);
        final double currentGrade = student.getGrade();


        Button update = updateGrade.findViewById(R.id.update_grade);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newGrade.getText().toString().isEmpty()) {
                    newGrade.setError("cannot be empty");
                } else if (Double.valueOf(newGrade.getText().toString()) > 11 ||
                        Double.valueOf(newGrade.getText().toString()) < 0) {
                    newGrade.setError("Grade must be between 0 and 10");

                } else {
                    double updatedGrade = currentGrade + Double.valueOf(newGrade.getText().toString());
                    DatabaseReference gradesRef = database.getReference("system").
                            child("schools").
                            child(school).child(year.replaceAll("\"", "")).
                            child(student.getId()).
                            child(subject);
                    gradesRef.setValue(Double.toString(updatedGrade));
                    newGrade.setError(null);
                    updateGrade.dismiss();
                }
            }
        });
        updateGrade.getWindow().setGravity(Gravity.CENTER);
        updateGrade.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updateGrade.show();
    }

}
