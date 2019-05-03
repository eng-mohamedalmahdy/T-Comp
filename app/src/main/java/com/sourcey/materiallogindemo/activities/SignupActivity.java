package com.sourcey.materiallogindemo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.okdroid.checkablechipview.CheckableChipView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.adapters.SimpleArrayListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private String s;
    @BindView(R.id.input_name)
    EditText _nameText;
    @BindView(R.id.input_address)
    EditText _addressText;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_mobile)
    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;

    EditText _subjectName;


    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @BindView(R.id.year1)
    CheckableChipView year1;


    @BindView(R.id.year2)
    CheckableChipView year2;

    @BindView(R.id.year3)
    CheckableChipView year3;


    @BindView(R.id.teacher_school)
    SearchableSpinner _schools;


    @BindView(R.id.domain)
    TextView domain;

    private ArrayList<String> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        _subjectName = (EditText) findViewById(R.id.subject_name);
        s = getIntent().getStringExtra("type");
        if (s.equals("student")) {
            android.support.design.widget.TextInputLayout contaier = (android.support.design.widget.TextInputLayout) findViewById(R.id.container);
            _subjectName.setVisibility(View.GONE);
            contaier.setVisibility(View.GONE);
        }

        ButterKnife.bind(this);
        if (s.equals("teacher")) {
            domain.setText("@tcomptech.com");
        } else if (s.equals("student")) {
            domain.setText("@tcompstu.com");
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

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
                Toast.makeText(SignupActivity.this, "request canceled", Toast.LENGTH_SHORT).show();

            }
        });

        _schools.setAdapter(new SimpleArrayListAdapter(this, data));

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String address = _addressText.getText().toString();
        final String email = _emailText.getText().toString() + domain.getText().toString();
        final String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            HashMap<String, String> userData = new HashMap<>();

                            userData.put("name", name);
                            userData.put("address", address);
                            userData.put("mobile", mobile);
                            userData.put("email", email);

                            if ("teacher".equals(s)) {
                                usersReference = database.getReference("system").child("teachers");
                                String subject = _subjectName.getText().toString();
                                String years = "";
                                if (year1.isChecked()) {
                                    years += year1.getText().toString() + ",";
                                }
                                if (year2.isChecked()) {
                                    years += year2.getText().toString() + ",";
                                }
                                if (year3.isChecked()) {
                                    years += year3.getText().toString() + ",";
                                }
                                userData.put("years", years);
                                userData.put("subject", subject);
                                userData.put("school", _schools.getSelectedItem().toString());

                            } else if ("student".equals(s)) {
                                usersReference = database.getReference("system").child("externalStudents");

                            }
                            String id = usersReference.push().getKey();
                            usersReference.child(id).setValue(userData);
                            progressDialog.dismiss();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        _signupButton.setEnabled(true);
                        progressDialog.dismiss();

                        // ...
                    }
                });


    }


    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString() + domain.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() != 11) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


        return valid;
    }
}