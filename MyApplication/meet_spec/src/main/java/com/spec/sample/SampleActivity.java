package com.spec.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.androidbrowserhelper.trusted.TwaLauncher;
import com.google.gson.Gson;

import java.net.URLDecoder;

public class SampleActivity extends Activity {
    static String KEY_AUTHORIZATION_STARTED = "KEY_AUTHORIZATION_STARTED";
    static String HANDLE_RESPONSE = "HANDLE_RESPONSE";
    static String KEY_TWA_OPENED = "KEY_TWA_OPENED";

    private boolean mAuthorizationStarted = false;
    private boolean twaOpened = false;
    private TwaLauncher mTWALauncher;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);

        // on first open, set authorization started as false
        if (savedInstanceBundle != null) {
            mAuthorizationStarted = savedInstanceBundle.getBoolean(KEY_AUTHORIZATION_STARTED, false);
        }

        Log.i("IDEMEUM_IDENTITY", "onCreate handleIntent");
        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("onSaveInstanceState", outState.toString());
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_AUTHORIZATION_STARTED, mAuthorizationStarted);
        outState.putBoolean(KEY_TWA_OPENED, twaOpened);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("IDEMEUM_IDENTITY", "onResume handleIntent");
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void handleIntent(Intent intent) {
        boolean openTwa = intent.getBooleanExtra(SampleManager.OPEN_TWA, false);
        boolean handleResponse = intent.getBooleanExtra(HANDLE_RESPONSE, false);
        Log.i("IDEMEUM_IDENTITY", "handleIntent, openTwa: " + openTwa);
        Log.i("IDEMEUM_IDENTITY", "handleIntent, handleResponse: " + handleResponse);
        Log.i("IDEMEUM_IDENTITY", "handleIntent, mAuthorizationStarted: " + mAuthorizationStarted);
        Log.i("IDEMEUM_IDENTITY", "handleIntent, twaOpened: " + twaOpened);


        // if open twa is true and authorization hasn't started, launch TWA
        if (openTwa && !mAuthorizationStarted) {
            // launch TWA
            String url = intent.getStringExtra(SampleManager.TWA_URL);
            mTWALauncher = new TwaLauncher(this, "com.android.chrome");
            mTWALauncher.launch(Uri.parse(url));
            Log.i("IDEMEUM_IDENTITY", "handleIntent, Launched TWA" + url);
            mAuthorizationStarted = true;
            return;
        }

        // if handleResponse requested, then this must come from redirect page
        if (handleResponse) {
            // handle the response from verification request
            Log.i("IDEMEUM_IDENTITY", "handleIntent, start handleResponse");
            handleResponse(this, intent);
            return;
        }

        // if openTwa is true, mAuthorizationStarted has started, but not twaOpened
        // this must be onResume when the WebView is open
        // so set twaOpened = true
        if (openTwa && mAuthorizationStarted && !twaOpened) {
            twaOpened = true;
            Log.i("IDEMEUM_IDENTITY", "handleIntent, openTwa && mAuthorizationStarted && !twaOpened -> set twaOpened true");
            return;
        }

        // if openTwa is true, mAuthorizationStarted has started, and twaOpened is true
        // this must be when the user canceled the WebView
        // so finish the activity
        if (openTwa && mAuthorizationStarted && twaOpened) {
            Log.i("IDEMEUM_IDENTITY", "handleIntent, openTwa && mAuthorizationStarted && twaOpened -> finish");
            finish();
        }
    }

    public void handleResponse(Context ctx, Intent intent) {


        Uri appLinkData = getIntent().getData();
        try {
            String response = URLDecoder.decode(appLinkData.getQueryParameter("response"), "UTF-8");

            byte[] data = Base64.decode(response, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            SampleResponse mSampleResponse = new Gson().fromJson(text, SampleResponse.class);

            if (mSampleResponse != null) {
//                Toast.makeText(this, URLDecoder.decode(appLinkData.toString(), "UTF-8"), Toast.LENGTH_SHORT).show();
                if (mSampleResponse.getStatus()) {
                    //status true
                    if (TextUtils.isEmpty(mSampleResponse.getToken().getIdToken()) || TextUtils.isEmpty(mSampleResponse.getToken().getAccessToken())) {
                        //token is null
                        SampleManager.getInstance().sendCallBack(false, null, "Token not received from backend!!");
                    } else
                        //token received
                        SampleManager.getInstance().sendCallBack(true, mSampleResponse, null);


                } else {
                    //status false
                    if (!TextUtils.isEmpty(mSampleResponse.getMessage()))
                        //error msg received
                        SampleManager.getInstance().sendCallBack(false, null, mSampleResponse.getMessage());
                    else
                        //error msg not received
                        SampleManager.getInstance().sendCallBack(false, null, "Unknown error!!");
                }

            } else {
                Log.e("App Link data", "null");
                SampleManager.getInstance().sendCallBack(false, null, "Operation cancelled!!");
            }
            this.finish();
        } catch (Exception e) {
            e.printStackTrace();
            SampleManager.getInstance().sendCallBack(false, null, e.getMessage());
            this.finish();

        }
        this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTWALauncher != null) {
            mTWALauncher.destroy();
        }
        SampleManager.getInstance().sendCallBack(false, null, "Operation cancelled!!");
    }
}
