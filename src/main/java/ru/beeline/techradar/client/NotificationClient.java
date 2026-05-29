/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static ru.beeline.techradar.utils.Constant.USER_ID_HEADER;


@Slf4j
@Service
public class NotificationClient {
    RestTemplate restTemplate;
    private final String notificationServerUrl;

    public NotificationClient(@Value("${integration.notification-server-url}") String notificationServerUrl,
                              RestTemplate restTemplate) {
        this.notificationServerUrl = notificationServerUrl;
        this.restTemplate = restTemplate;
    }

    public List<Integer> getSubscribes(String entityType, String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(USER_ID_HEADER, userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = notificationServerUrl + "/api/v1/subscribe/" + entityType;

            ResponseEntity<List<Integer>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Integer>>() {
                    });

            return response.getBody();
        } catch (Exception e) {
            log.error("Error occurred while trying to get all entity subscriptions: ", e);
            return new ArrayList<>();
        }
    }
}
