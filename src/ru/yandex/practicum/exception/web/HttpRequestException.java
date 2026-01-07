package ru.yandex.practicum.exception.web;

public class HttpRequestException extends RuntimeException {
    protected final int code;

    public HttpRequestException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
