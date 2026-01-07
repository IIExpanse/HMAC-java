package ru.yandex.practicum;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.context.web.WebContext;
import ru.yandex.practicum.exception.app.ServerInstantiationException;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Основной класс сервера для генерации и проверки HMAC-подписей.
 *
 * <p>Этот класс отвечает за инициализацию HTTP-сервера, настройку обработчиков
 * и запуск сервера на указанном порту. Использует встроенный HTTP-сервер из
 * пакета {@code com.sun.net.httpserver} для обработки входящих запросов.</p>
 *
 * <p>Конфигурация сервера (порт, секрет, алгоритм и др.) загружается через
 * {@link AppContext}, а маршруты и их обработчики регистрируются с помощью
 * {@link WebContext}.</p>
 *
 * <p>Сервер использует пул потоков на основе {@link Executors#newWorkStealingPool()},
 * что обеспечивает эффективную обработку запросов в асинхронном режиме.</p>
 */
public class ServerHMAC {
    private static final Logger log = Logger.getLogger(ServerHMAC.class.getName());

    public static void main(String[] args) {
        try {
            log.info("Starting server initialization..");
            HttpServer server = HttpServer.create(new InetSocketAddress(AppContext.getListenPort()), 0);
            WebContext.init(server);
            server.setExecutor(Executors.newWorkStealingPool());
            server.start();
            log.info("Server initialization finished.");
        } catch (Exception e) {
            throw new ServerInstantiationException("Failed to instantiate http server: " + e.getMessage(), e);
        }
    }
}
