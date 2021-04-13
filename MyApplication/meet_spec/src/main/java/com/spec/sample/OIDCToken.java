package com.spec.sample;

import org.json.JSONException;
import org.json.JSONObject;

public class OIDCToken {

    private String accessToken;
    private String idToken;
    private long expires_in;

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public long getExpires_in() {
        return expires_in;
    }

    @Override
    public String toString() {
        return "OIDC{" +
                "accessToken='" + accessToken + '\'' +
                ", idToken='" + idToken + '\'' +
                ", expires_in='" + expires_in + '\'' +
                '}';
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("accessToken", accessToken);
        jo.put("idToken", idToken);
        jo.put("expires_in", expires_in);

        return jo;
    }
}
