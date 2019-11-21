package com.asianwallets.common.utils;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class HTTPUtil {

//    @Autowired
//    public RestTemplate restTemplate;

    private static final String API_KEY_ID = "KeyId";
    private static final String SIGNATURE = "sign";

    public String postRequest(String url, String message, HttpHeaders headers) {

        HttpEntity<String> entity = new HttpEntity<>(message, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        response.getBody();
        return response.getBody();
    }

    public HttpHeaders generateJsonHeaders(String sign, String apiKeyId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(API_KEY_ID, apiKeyId);
        headers.add(SIGNATURE, sign);
        return headers;
    }


}
