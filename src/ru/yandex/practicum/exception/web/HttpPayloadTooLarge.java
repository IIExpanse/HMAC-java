package ru.yandex.practicum.exception.web;

public class HttpPayloadTooLarge extends HttpRequestException {
    public HttpPayloadTooLarge(String message) {
        super(message, 413);
    }
}
