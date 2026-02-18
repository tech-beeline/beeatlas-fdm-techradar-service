/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.exception;


public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}