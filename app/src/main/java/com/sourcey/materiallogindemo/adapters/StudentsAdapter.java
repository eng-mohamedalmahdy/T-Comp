package com.sourcey.materiallogindemo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcey.materiallogindemo.R;

import java.util.ArrayList;

public class StudentsAdapter extends ArrayAdapter<Student> {
    ArrayList<Student> students;

    public StudentsAdapter(@NonNull Context context, ArrayList<Student> students) {
        super(context, R.layout.student_data);
        this.students = students;
    }


    @Override
    public int getCount() {
        return students == null ? 0 : students.size() + 1;
    }

    @Override
    public Student getItem(int position) {
        if (students != null && position > 0)
            return students.get(position - 1);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        if (students == null && position > 0)
            return students.get(position).hashCode();
        else
            return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(getContext(), R.layout.student_data, null);
        TextView name = view.findViewById(R.id.student_name);
        name.setText(getItem(position).getName());

        TextView grade = view.findViewById(R.id.subject_grade);
        grade.setText(Double.toString(getItem(position).getGrade()));


        return view;
    }


}
