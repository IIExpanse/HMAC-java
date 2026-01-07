package ru.yandex.practicum.exception.app;

public class ServerInstantiationException extends RuntimeException {
    public ServerInstantiationException(String message) {
        super(message);
    }

    public ServerInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
