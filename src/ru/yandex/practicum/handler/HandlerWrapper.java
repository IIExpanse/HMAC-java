package ru.yandex.practicum.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.exception.app.ServerInstantiationException;
import ru.yandex.practicum.exception.web.HttpRequestException;
import ru.yandex.practicum.model.ApiSettings;
import ru.yandex.practicum.validator.HttpRequestValidator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Обёртка над HTTP-обработчиком, обеспечивающая предварительную валидацию запроса.
 *
 * <p>Класс реализует {@link HttpHandler} и выступает в роли промежуточного звена между
 * HTTP-сервером и реальным обработчиком запросов. Перед передачей управления целевому
 * обработчику выполняет проверку корректности HTTP-метода и типа содержимого (Content-Type),
 * опираясь на настройки из {@link ApiSettings}.</p>
 *
 * <p>В случае ошибки валидации или обработки запроса отправляется соответствующий
 * HTTP-ответ с кодом состояния и сообщением об ошибке. Соединение всегда закрывается
 * в блоке {@code finally}.</p>
 */
public class HandlerWrapper implements HttpHandler {
    private static final Logger log = Logger.getLogger(HandlerWrapper.class.getName());

    private final ApiSettings apiSettings;
    private final HttpHandler handler;

    public HandlerWrapper(ApiSettings apiSettings, HttpHandler handler) {
        this.apiSettings = apiSettings;
        this.handler = handler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            HttpRequestValidator.checkIsSupportedMethod(apiSettings, exchange.getRequestMethod());
            HttpRequestValidator.checkIsSupportedMediaType(apiSettings, exchange.getRequestHeaders());
            handler.handle(exchange);

        } catch (HttpRequestException e) {
            handleErrorResponse(exchange, e.getCode(), e.getMessage());

        } catch (ServerInstantiationException e) {
            throw e;

        } catch (Exception e) {
            handleErrorResponse(exchange, 500, e.getMessage());

        } finally {
            exchange.close();
        }
    }

    private void handleErrorResponse(HttpExchange exchange, int code, String message) throws IOException {
        log.info(message);
        byte[] body = message.getBytes();
        OutputStream os = exchange.getResponseBody();
        exchange.sendResponseHeaders(code, body.length);
        os.write(body);
        os.flush();
    }
}
