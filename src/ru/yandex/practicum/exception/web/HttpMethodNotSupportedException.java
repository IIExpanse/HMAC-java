package ru.yandex.practicum.exception.web;

public class HttpMethodNotSupportedException extends HttpRequestException {
    public HttpMethodNotSupportedException(String message) {
        super(message, 405);
    }
}
