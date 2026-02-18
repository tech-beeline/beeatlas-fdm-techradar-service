/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.exception;


public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}