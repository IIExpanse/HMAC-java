package ru.yandex.practicum.exception.web;

public class HttpUnsupportedMediaTypeException extends HttpRequestException {
    public HttpUnsupportedMediaTypeException(String message) {
        super(message, 415);
    }
}
