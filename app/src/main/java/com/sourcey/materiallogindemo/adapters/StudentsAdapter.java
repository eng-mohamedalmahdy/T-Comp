package com.sourcey.materiallogindemo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourcey.materiallogindemo.R;

import java.util.ArrayList;

import top.defaults.view.TextButton;

public class StudentsAdapter extends ArrayAdapter<Student> {
    ArrayList<Student> students;

    public StudentsAdapter(@NonNull Context context, ArrayList<Student> students) {
        super(context, R.layout.student_data,students);
        this.students = students;
    }


    @Override
    public Student getItem(int position) {
        return super.getItem(getCount() - position - 1);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_data, parent, false);
        }
        TextView name = convertView.findViewById(R.id.student_name);
        name.setText(getItem(position).getName());

        TextView grade = convertView.findViewById(R.id.subject_grade);
        grade.setText(Double.toString(getItem(position).getGrade()));

        return convertView;
    }


}
