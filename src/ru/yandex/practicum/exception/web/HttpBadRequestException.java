package ru.yandex.practicum.exception.web;

public class HttpBadRequestException extends HttpRequestException {
    public HttpBadRequestException(String message) {
        super(message, 400);
    }
}
