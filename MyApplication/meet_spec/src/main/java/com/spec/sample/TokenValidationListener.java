package com.spec.sample;

import org.json.JSONObject;

public interface TokenValidationListener {
    void onTokenValidation(boolean isSuccess, JSONObject mClaims, String error);
}
