package ru.yandex.practicum.model;

import ru.yandex.practicum.handler.impl.SignEndpointHandler;
import ru.yandex.practicum.handler.impl.VerifyEndpointHandler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Перечисление, определяющее настройки доступных API-эндпоинтов приложения.
 *
 * <p>Каждый элемент перечисления представляет собой конфигурацию конкретного HTTP-пути,
 * включая допустимые методы, поддерживаемые типы содержимого (Content-Type),
 * и соответствующий обработчик запросов.</p>
 *
 * <p>Используется классом {@link ru.yandex.practicum.context.web.WebContext} для регистрации
 * обработчиков в HTTP-сервере, а также {@link ru.yandex.practicum.handler.HandlerWrapper}
 * для валидации запросов на соответствие настройкам.</p>
 */
public enum ApiSettings {
    SIGN_ENDPOINT(
            "/sign",
            List.of(HttpMethod.POST),
            List.of(MediaType.APPLICATION_JSON),
            SignEndpointHandler.class
    ),
    VERIFY_ENDPOINT(
            "/verify",
            List.of(HttpMethod.POST),
            List.of(MediaType.APPLICATION_JSON),
            VerifyEndpointHandler.class
    );

    private final String path;
    private final Set<String> supportedMethods;
    private final Set<String> supportedMediaTypes;
    private final Class<?> handlerClass;

    ApiSettings(
            String path,
            List<HttpMethod> supportedMethods,
            List<MediaType> supportedMediaTypes,
            Class<?> handlerClass
    ) {
        this.path = path;
        this.supportedMethods = supportedMethods.stream()
                .map(Enum::name)
                .collect(Collectors.toUnmodifiableSet());
        this.supportedMediaTypes = supportedMediaTypes.stream()
                .map(MediaType::getValue)
                .collect(Collectors.toUnmodifiableSet());
        this.handlerClass = handlerClass;
    }

    public String getPath() {
        return path;
    }

    public Set<String> getSupportedMethods() {
        return supportedMethods;
    }

    public Set<String> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    public Class<?> getHandlerClass() {
        return handlerClass;
    }
}
