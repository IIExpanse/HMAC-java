package ru.yandex.practicum.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.constant.AppConstants;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.exception.app.InternalServerException;
import ru.yandex.practicum.mapper.JsonObjectMapper;
import ru.yandex.practicum.validator.HttpRequestValidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Абстрактный обработчик HTTP-запросов, реализующий базовую логику обработки.
 *
 * <p>Класс предоставляет шаблонную реализацию метода {@link #handle(HttpExchange)},
 * который выполняет следующие шаги:</p>
 * <ol>
 *   <li>Чтение тела запроса с ограничением по размеру.</li>
 *   <li>Валидацию размера и содержимого тела запроса.</li>
 *   <li>Десериализацию JSON в объект заданного типа.</li>
 *   <li>Вызов бизнес-логики через переданную функцию.</li>
 *   <li>Сериализацию результата в JSON и отправку клиенту.</li>
 * </ol>
 *
 * <p>Класс является параметризованным:</p>
 * <ul>
 *   <li>{@code R} — тип входного DTO (объекта, передаваемого в запросе).</li>
 *   <li>{@code O} — тип результата, возвращаемого бизнес-методом.</li>
 * </ul>
 *
 * <p>Для работы требуется реализовать метод {@link #getLogger()}, чтобы обеспечить
 * логирование операций обработки запроса логгером конкретного класса.</p>
 *
 * @param <R> тип входного объекта (DTO), получаемого из тела запроса
 * @param <O> тип результата, возвращаемого бизнес-методом
 */
public abstract class HttpRequestHandler<R, O> implements HttpHandler {
    private final HttpRequestValidator<R> validator;
    private final Class<R> clazz;
    private final Function<R, O> serviceMethod;

    protected HttpRequestHandler(HttpRequestValidator<R> validator, Class<R> clazz, Function<R, O> serviceMethod) {
        this.validator = validator;
        this.clazz = clazz;
        this.serviceMethod = serviceMethod;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            getLogger().info("Started processing request for path: " + exchange.getRequestURI());
            InputStream is = exchange.getRequestBody();
            byte[] requestBody = is.readNBytes(AppContext.getMaxSizeBytes() + 1);
            HttpRequestValidator.checkSize(requestBody, AppContext.getMaxSizeBytes());

            R dto = JsonObjectMapper.fromJson(new String(requestBody, AppConstants.CHARSET), clazz);
            validator.validateBody(dto);

            String jsonResponse = JsonObjectMapper.toJson(serviceMethod.apply(dto));
            byte[] bytesResponse = jsonResponse.getBytes(AppConstants.CHARSET);
            exchange.sendResponseHeaders(200, bytesResponse.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytesResponse);
            os.flush();
            getLogger().info("Finished processing request for path: " + exchange.getRequestURI());
        } catch (IOException e) {
            throw new InternalServerException("Exception during http exchange: " + e.getMessage());
        }
    }

    protected abstract Logger getLogger();
}
