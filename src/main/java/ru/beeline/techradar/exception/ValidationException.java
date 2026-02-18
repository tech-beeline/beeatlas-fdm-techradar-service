/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
