package ru.yandex.practicum.context.web;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.model.ApiSettings;
import ru.yandex.practicum.handler.HandlerWrapper;

import java.util.logging.Logger;

/**
 * Класс для инициализации веб-маршрутов приложения.
 *
 * <p>Отвечает за настройку контекстов HTTP-сервера на основе перечисления {@link ApiSettings}.
 * Для каждого API-эндпоинта создаётся соответствующий обработчик, оборачиваемый в {@link HandlerWrapper}
 * для дополнительной обработки запросов (например, валидации, логирования и т.п.).</p>
 */
public class WebContext {
    private static final Logger log = Logger.getLogger(WebContext.class.getName());

    private WebContext() {}

    public static void init(HttpServer httpServer) {
        for (ApiSettings settings : ApiSettings.values()) {
            log.info("Initializing handler for path: " + settings.getPath());
            HttpHandler handler = (HttpHandler) AppContext.getBean(settings.getHandlerClass());
            httpServer.createContext(settings.getPath(), new HandlerWrapper(settings, handler));
        }
    }
}
