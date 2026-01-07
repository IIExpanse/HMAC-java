package ru.yandex.practicum.utils;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.context.web.WebContext;
import ru.yandex.practicum.exception.app.ServerInstantiationException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TestUtils {
    private static final String CONFIG_PATH = "ru/yandex/practicum/context/app/test-config.json";

    private TestUtils() {}

    public static TestHttpServer initializeNewHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(AppContext.getListenPort()), 0);
            WebContext.init(server);
            server.setExecutor(Executors.newWorkStealingPool());
            server.start();
            return new TestHttpServer(server);
        } catch (Exception e) {
            throw new ServerInstantiationException("Failed to instantiate test http server: " + e.getMessage(), e);
        }
    }

    public static String loadTestConfig() {
        InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
        if (is == null) {
            throw new RuntimeException(String.format("Config file '%s' not found", CONFIG_PATH));
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().reduce("", (a, b) -> a + b);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test config: " + e.getMessage(), e);
        }
    }
}
