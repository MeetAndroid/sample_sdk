package com.spec.sample;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SampleManager {

    public static String OPEN_TWA = "OPEN_TWA";
    public static String TWA_URL = "TWA_URL";

    public static String ApiKeyID;
    public static String JS_URL = "https://ciam.idemeum.com/api/consumer/authorize?clientId=%s";

    private static SampleManager sInstance;

    LoginListener mLoginListener;
    boolean mCallbackSent = false;

    public static synchronized SampleManager getInstance() {
        if (sInstance == null) {
            sInstance = new SampleManager();
        }

        return sInstance;
    }

    public SampleManager() {

    }

    public void loginWithIdemeum(Context context, String apiKey, LoginListener mLoginListener) {
        ApiKeyID = apiKey;
        this.mLoginListener = mLoginListener;
        mCallbackSent = false;
        Intent identityActivity = new Intent(context, SampleActivity.class);
        identityActivity.putExtra(OPEN_TWA, true);
        identityActivity.putExtra(TWA_URL, String.format(JS_URL, ApiKeyID));
        context.startActivity(identityActivity);
    }

    public void sendCallBack(boolean isSuccess, SampleResponse mSampleResponse, String errorMsg) {
        if (this.mLoginListener != null) {
            if (!mCallbackSent) {
                mCallbackSent = true;
                mLoginListener.onSuccess(isSuccess, mSampleResponse, errorMsg);
            }
        }
    }

    public Intent createResponseHandlingIntent(Context context, Uri responseUri) {
        Log.i("createResponseIntent", responseUri.toString());

        // Creating an intent of the IdentityActivity to handle the uri from redirect
        Intent intent = new Intent(context, SampleActivity.class);
        intent.setData(responseUri);
        intent.putExtra(SampleManager.OPEN_TWA, false);
        intent.putExtra(SampleActivity.HANDLE_RESPONSE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public void getClaimsFromToken(Context context, OIDCToken oidcToken, final TokenValidationListener mOnTokenValidationListener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://ciam.idemeum.com/api/consumer/token/validation";

        if (!isInternetOn(context)) {
            mOnTokenValidationListener.onTokenValidation(false, null,
                    "Internet Connection not available");
        }


        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, oidcToken.toJSON(),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mOnTokenValidationListener.onTokenValidation(true, response,
                                    "Internet Connection not available");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mOnTokenValidationListener.onTokenValidation(false, null,
                                    error.toString());
                        }
                    });
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static boolean isInternetOn(Context application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }
}
