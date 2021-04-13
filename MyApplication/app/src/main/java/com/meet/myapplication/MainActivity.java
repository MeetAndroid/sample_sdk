package com.meet.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.spec.sample.LoginListener;
import com.spec.sample.SampleManager;
import com.spec.sample.SampleResponse;
import com.spec.sample.OIDCToken;
import com.spec.sample.TokenValidationListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final String apiKeyBiometric = "5166e6ac-9442-11eb-a8b3-0242ac130003";
    SampleManager mSampleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSampleManager = SampleManager.getInstance();
        mSampleManager.loginWithIdemeum(MainActivity.this, apiKeyBiometric
                , new LoginListener() {
                    @Override
                    public void onSuccess(boolean isSuccess, SampleResponse mSampleResponse, String error) {
                        if (isSuccess) {
                            validateToken(mSampleResponse.getToken());

                        } else
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateToken(OIDCToken token) {
        mSampleManager.getClaimsFromToken(this, token, new TokenValidationListener() {

            @Override
            public void onTokenValidation(boolean isSuccess, JSONObject mClaims, String error) {
                if (isSuccess) {
                    Toast.makeText(MainActivity.this, mClaims.toString(), Toast.LENGTH_SHORT).show();
//                    Intent mIntent = new Intent(MainActivity.this, HomeScreenActivity.class);
//                    mIntent.putExtra("response", mClaims.toString());
//                    startActivity(mIntent);
                } else {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}