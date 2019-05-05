package com.sourcey.materiallogindemo.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.sourcey.materiallogindemo.Config.Config;
import com.sourcey.materiallogindemo.R;

import org.json.JSONException;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.defaults.view.TextButton;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static final int PAYPAL_REQUEST_CODE = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);


    TextView edtAmount;

    private Dialog fillForm;
    private FirebaseAuth mAuth;
    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    String id = "";
    String amount = null;

    @BindView(R.id.donate)
    TextView donate;

    Dialog donationDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance((FirebaseApp.initializeApp(this)));


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        donationDialog = new Dialog(this);
        fillForm = new Dialog(this);

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDonateDialog();
            }
        });
    }

    private void showDonateDialog() {


        donationDialog.setContentView(R.layout.donation_dialoge);
        edtAmount = donationDialog.findViewById(R.id.donated_ammount);
        Button donate = donationDialog.findViewById(R.id.donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
                donationDialog.dismiss();
            }
        });
        donationDialog.getWindow().setGravity(Gravity.CENTER);
        donationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        donationDialog.show();


    }

    public void login() {
        Log.d(TAG, "Login");


        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (email.trim().toLowerCase().endsWith("@tcompadmin.com")) {
                                startActivity(new Intent(LoginActivity.this, AdminOperations.class));

                            } else if (email.trim().toLowerCase().endsWith("@tcomptech.com")) {
                                final Intent add = new Intent(LoginActivity.this, YearSelection.class);
                                // Read from the database

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("system").child("teachers");

                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if (ds.child("email").getValue().toString().equals(user.getEmail())) {

                                                id = ds.getKey();
                                                add.putExtra("id", id);
                                                startActivity(add);

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });


                            } else {
                                Toast.makeText(LoginActivity.this, "invalid domain", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        progressDialog.dismiss();
        _loginButton.setEnabled(true);


    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void ShowVolPopup(View v) {
        TextView txtclose;
        WebView form;
        fillForm.setContentView(R.layout.activity_volunteer);
        txtclose = fillForm.findViewById(R.id.txtclose);
        form = fillForm.findViewById(R.id.google_form);
        form.setWebViewClient(new WebViewClient());
        form.getSettings().setJavaScriptEnabled(true);
        form.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSc0BFj1-ws0R-iM4SXYeqGdAqZzJz-qZkmvCxp70cMm-zwfiA/formResponse");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillForm.dismiss();
            }
        });
        fillForm.getWindow().setGravity(Gravity.CENTER);
        fillForm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fillForm.show();
    }


    private void processPayment() {
        amount = edtAmount.getText().toString();
        if (amount.isEmpty()) {
            edtAmount.setError("Cannot be empty");
        } else {
            edtAmount.setError(null);
            PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD",
                    "Donate", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(intent, PAYPAL_REQUEST_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();

    }
}

