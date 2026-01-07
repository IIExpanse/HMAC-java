package ru.yandex.practicum.validator;

import com.sun.net.httpserver.Headers;
import ru.yandex.practicum.exception.web.HttpPayloadTooLarge;
import ru.yandex.practicum.model.ApiSettings;
import ru.yandex.practicum.exception.web.HttpBadRequestException;
import ru.yandex.practicum.exception.web.HttpMethodNotSupportedException;
import ru.yandex.practicum.exception.web.HttpUnsupportedMediaTypeException;

import static ru.yandex.practicum.constant.AppConstants.CONTENT_TYPE;

/**
 * Интерфейс валидатора HTTP-запросов.
 *
 * <p>Предоставляет методы для проверки корректности тела запроса, HTTP-метода,
 * типа содержимого (Content-Type) и размера тела. Валидация выполняется на основе
 * конфигурации API, заданной в {@link ApiSettings}.</p>
 *
 * <p>Интерфейс содержит статические методы для проверки общих аспектов запроса
 * и дефолтный метод {@link #validateBody(Object)}, предназначенный для кастомной
 * валидации содержимого тела (например, обязательных полей).</p>
 *
 * @param <T> тип объекта, представляющего тело запроса (DTO)
 */
public interface HttpRequestValidator<T> {
    default void validateBody(T body) {}

    static void checkIsSupportedMethod(ApiSettings config, String method) {
        if (!config.getSupportedMethods().contains(method)) {
            throw new HttpMethodNotSupportedException(
                    String.format("Http method %s is not supported", method)
            );
        }
    }

    static void checkIsSupportedMediaType(ApiSettings config, Headers headers) {
        if (!headers.containsKey(CONTENT_TYPE)) {
            throw new HttpBadRequestException(String.format("Required header %s is missing", CONTENT_TYPE));
        }
        if (!config.getSupportedMediaTypes().containsAll(headers.get(CONTENT_TYPE))) {
            throw new HttpUnsupportedMediaTypeException(String.format(
                    "Request contains unsupported media types. Supported types are %s", config.getSupportedMediaTypes())
            );
        }
    }

    static void checkSize(byte[] body, int maxSize) {
        if (body.length == 0) {
            throw new HttpBadRequestException("Request body is empty");
        }
        if (body.length > maxSize) {
            throw new HttpPayloadTooLarge(
                    String.format("Request body size exceeds max %d bytes", maxSize)
            );
        }
    }
}
