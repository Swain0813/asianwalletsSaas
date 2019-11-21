package com.asianwallets.common.response;
import lombok.Data;

@Data
public class AuthenticationRequest {
    String institutionCode;
    String username;
    String password;
    String imei;

    public AuthenticationRequest(String institutionCode, String username, String password,  String imei) {
        this.institutionCode = institutionCode;
        this.username = username;
        this.password = password;
        this.imei = imei;
    }

    public AuthenticationRequest() {
        super();
    }

}
