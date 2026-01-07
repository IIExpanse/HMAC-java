package ru.yandex.practicum.exception.app;

public class ConfigurationNotFoundException extends ServerInstantiationException {
    public ConfigurationNotFoundException(String message) {
        super(message);
    }
}
