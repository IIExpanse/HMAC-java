package ru.yandex.practicum.exception.app;

public class BeanInitializationException extends ServerInstantiationException {
    public BeanInitializationException(String message) {
        super(message);
    }
}
