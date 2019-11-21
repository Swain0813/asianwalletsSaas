package com.asianwallets.common.response;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class AuthenticationResponse {
    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public AuthenticationResponse() {

    }

    String userId;
    String institutionId;

    String token;
    String username;
    String name;
    String publicKey;
    List<ResRole> role;
    Set<ResPermissions> permissions;

//    String role;
//    Set<String> permissions;
}

