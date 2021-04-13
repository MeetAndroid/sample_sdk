package com.spec.sample;

import com.google.gson.annotations.SerializedName;

public class SampleResponse {
    private boolean status;
    @SerializedName("oidc")
    private OIDCToken token;
    private String message;

    public boolean getStatus() {
        return status;
    }

    public OIDCToken getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
