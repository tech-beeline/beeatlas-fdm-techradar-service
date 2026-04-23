package ru.beeline.techradar.exception;

public class ProductNfrLinkException extends RuntimeException {

    public static final String DEFAULT_MESSAGE =
            "Ошибка при обращении к product сервису, паттерн не создан";

    public ProductNfrLinkException() {
        super(DEFAULT_MESSAGE);
    }
}
