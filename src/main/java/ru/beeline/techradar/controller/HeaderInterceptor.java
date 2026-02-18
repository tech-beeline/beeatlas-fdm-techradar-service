/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.beeline.techradar.exception.ForbiddenException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.beeline.techradar.utils.Constant.*;


public class HeaderInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(HeaderInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean getAllPattern =  request.getRequestURI().contains("/api/v1/pattern") && ("GET".equalsIgnoreCase(request.getMethod()));
        try {
            if (request.getRequestURI().contains("/swagger")
                    || (request.getRequestURI().contains("/v2/api-docs"))) {
                return true;
            }
            if (request.getRequestURI().contains("/actuator/prometheus")
                    || getAllPattern
                    || request.getRequestURI().contains("/swagger")
                    || request.getRequestURI().contains("/error")
                    || request.getRequestURI().contains("/api-docs")
                    || request.getRequestURI().contains("/api/v1/rings")
                    || request.getRequestURI().contains("/api/v1/sectors")
                    || request.getRequestURI().contains("/api/v1/category")
                    || request.getRequestURI().contains("/api/v1/processes")
                    || (request.getRequestURI().contains("/api/v1/tech")
                    && !request.getRequestURI().contains("/version")
                    && !request.getRequestURI().contains("/subscribed")
                    && !request.getRequestURI().contains("/subscribed"))
            ) {
                Map<String, Object> headers = new HashMap<>();
                RequestContext.setHeaders(headers);
                String userRolesHeaderValue = request.getHeader(USER_ROLES_HEADER);
                if (userRolesHeaderValue == null) {
                    userRolesHeaderValue = "";
                }
                List<String> userRolesList = Arrays.asList(userRolesHeaderValue.split(","));
                headers.put(USER_ROLES_HEADER, userRolesList);
                return true;
            }
            Map<String, Object> headers = new HashMap<>();
            RequestContext.setHeaders(headers);
            logger.info(USER_ROLES_HEADER + toList(request.getHeader(USER_ROLES_HEADER)));
            headers.put(USER_ROLES_HEADER, toList(request.getHeader(USER_ROLES_HEADER).toString()));
            logger.info(USER_ID_HEADER + request.getHeader(USER_ID_HEADER));
            headers.put(USER_ID_HEADER, request.getHeader(USER_ID_HEADER).toString());
            logger.info(USER_PERMISSION_HEADER + toList(request.getHeader(USER_PERMISSION_HEADER)));
            headers.put(USER_PERMISSION_HEADER, toList(request.getHeader(USER_PERMISSION_HEADER).toString()));
            logger.info(USER_PRODUCTS_IDS_HEADER + toList(request.getHeader(USER_PRODUCTS_IDS_HEADER)));
            headers.put(USER_PRODUCTS_IDS_HEADER, toList(request.getHeader(USER_PRODUCTS_IDS_HEADER).toString()));
            RequestContext.setHeaders(headers);
            logger.info("Set headers complete");
            return true;
        } catch (Exception e) {
            throw new ForbiddenException("403 Forbidden.");
        }
    }

    private List<String> toList(String value) {
        return Arrays.stream(value.split(","))
                .map(str -> str.substring(0))
                .map(str -> str.replaceAll("\"", ""))
                .map(str -> str.replaceAll("]", ""))
                .map(str -> str.replaceAll("\\[", ""))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}