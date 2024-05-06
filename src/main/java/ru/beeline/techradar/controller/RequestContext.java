package ru.beeline.techradar.controller;

import java.util.List;
import java.util.Map;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

public class RequestContext {
    private static final ThreadLocal<Map<String, Object>> headersThreadLocal = new ThreadLocal<>();

    public static void setHeaders(Map<String, Object> headers) {
        headersThreadLocal.set(headers);
    }

    public static Map<String, Object> getHeaders() {
        return headersThreadLocal.get();
    }

    public static List<String> getRoles() {
        return (List<String>) getHeaders().get(USER_ROLES_HEADER);
    }
}
