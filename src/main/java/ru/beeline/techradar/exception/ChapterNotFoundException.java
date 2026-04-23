package ru.beeline.techradar.exception;

public class ChapterNotFoundException extends RuntimeException {
    public ChapterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

