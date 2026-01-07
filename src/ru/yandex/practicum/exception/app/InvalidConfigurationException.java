package ru.yandex.practicum.exception.app;

public class InvalidConfigurationException extends ServerInstantiationException {
    public InvalidConfigurationException(String message) {
        super(message);
    }
}
