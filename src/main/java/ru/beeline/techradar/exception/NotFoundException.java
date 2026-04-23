/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
