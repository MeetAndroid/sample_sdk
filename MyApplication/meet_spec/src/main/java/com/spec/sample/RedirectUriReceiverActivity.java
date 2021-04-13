package com.spec.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RedirectUriReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handling the redirect this way ensures that the WebView closes after redirect
        startActivity(SampleManager.getInstance().createResponseHandlingIntent(this, getIntent().getData()));
        finish();
    }


}