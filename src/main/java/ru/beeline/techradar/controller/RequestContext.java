package ru.beeline.techradar.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

public class RequestContext {
    private static final ThreadLocal<Map<String, Object>> headersThreadLocal = new ThreadLocal<>();

    public static void setHeaders(Map<String, Object> headers) {
        headersThreadLocal.set(headers);
    }

    public static Map<String, Object> getHeaders() {
        Map<String, Object> headers = headersThreadLocal.get();
        if (headers == null) {
            headers = new HashMap<>();
            headersThreadLocal.set(headers);
        }
        return headers;
    }
    public static List<String> getRoles() {
        List<String> roles = (List<String>) getHeaders().get(USER_ROLES_HEADER);
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles;
    }
}
