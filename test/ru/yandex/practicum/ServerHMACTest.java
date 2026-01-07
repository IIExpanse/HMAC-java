package ru.yandex.practicum;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.utils.TestHttpServer;
import ru.yandex.practicum.utils.TestUtils;

class ServerHMACTest {
    @BeforeAll
    static void init() {
        AppContext.setConfig(TestUtils.loadTestConfig(), true);
    }

    @Test
    void serverStarts() {
        Assertions.assertDoesNotThrow(() -> {
            TestHttpServer server = TestUtils.initializeNewHttpServer();
            server.stop();
        });
    }
}
