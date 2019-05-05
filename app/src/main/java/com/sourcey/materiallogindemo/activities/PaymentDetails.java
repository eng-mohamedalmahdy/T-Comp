package com.sourcey.materiallogindemo.activities;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.sourcey.materiallogindemo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView txtId ,txtAmount , txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        //Get intent

        Intent intent =getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response") , intent.getStringExtra("PaymentAmount"));

        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    private void showDetails(JSONObject response , String paymentAmount){
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            txtAmount.setText(response.getString(String.format("$%s",paymentAmount)));


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
