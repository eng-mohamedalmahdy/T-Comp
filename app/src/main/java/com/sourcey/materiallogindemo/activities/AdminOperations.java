package com.sourcey.materiallogindemo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sourcey.materiallogindemo.R;

public class AdminOperations extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_operations);

        final Button _addTeacher = (Button) findViewById(R.id.add_teacher);
        Button _addStudent = (Button) findViewById(R.id.add_student);
        Button _addSchool = (Button) findViewById(R.id.add_school);
        _addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent add = new Intent(AdminOperations.this, SignupActivity.class);
                add.putExtra("type", "teacher");
                startActivity(add);

            }
        });

        _addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AdminOperations.this, NewStudentActivity.class);
                add.putExtra("type", "school");
                startActivity(add);

            }
        });

        _addSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(AdminOperations.this, AddSchoolActivity.class);
                startActivity(add);
            }
        });
    }
}
