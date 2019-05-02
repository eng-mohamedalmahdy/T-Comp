package com.sourcey.materiallogindemo.adapters;

public class Student {
    private String name;
    private double grade;

    public Student(String name, double grade) {

        this.name = name;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }
}
