package com.blog.afaq.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TurnstileService {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void verify(String token) {

        String url = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                request,
                Map.class
        );

        Map result = response.getBody();

        if (result == null || !(Boolean) result.get("success")) {
            throw new RuntimeException("Invalid captcha");
        }
    }
}