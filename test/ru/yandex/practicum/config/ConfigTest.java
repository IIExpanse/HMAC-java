package ru.yandex.practicum.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.exception.app.InvalidConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    private static final Gson gson = new Gson();

    @Test
    void testInvalidHmacAlg() {
        String config = changeJson(getDefaultConfig(), "hmacAlg", "");
        Throwable e = assertThrows(InvalidConfigurationException.class, () -> AppContext.setConfig(config, true));
        assertEquals("hmacAlg must not be empty", e.getMessage());
    }

    @Test
    void testInvalidSecret() {
        String config1 = changeJson(getDefaultConfig(), "secret", "");
        Throwable e = assertThrows(InvalidConfigurationException.class, () -> AppContext.setConfig(config1, true));
        assertEquals("secret must not be empty", e.getMessage());

        String config2 = changeJson(getDefaultConfig(), "secret", "@@@");
        e = assertThrows(InvalidConfigurationException.class, () -> AppContext.setConfig(config2, true));
        assertEquals("secret must be a valid base64 encoded string", e.getMessage());
    }

    @Test
    void testInvalidListenPort() {
        String config = changeJson(getDefaultConfig(), "listenPort", "-1");
        Throwable e = assertThrows(InvalidConfigurationException.class, () -> AppContext.setConfig(config, true));
        assertEquals("listenPort cannot be negative", e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void testInvalidMaxMsgSizeBytes(int maxMsgSizeBytes) {
        String config = changeJson(getDefaultConfig(), "maxMsgSizeBytes", String.valueOf(maxMsgSizeBytes));
        Throwable e = assertThrows(InvalidConfigurationException.class, () -> AppContext.setConfig(config, true));
        assertEquals("maxMsgSizeBytes must greater than zero", e.getMessage());
    }

    private String changeJson(String config, String field, String value) {
        JsonElement json = JsonParser.parseString(config);
        JsonObject obj = json.getAsJsonObject();
        obj.addProperty(field, value);
        return gson.toJson(obj);
    }

    private String getDefaultConfig() {
        return """
                {
                  "hmacAlg": "SHA256",
                  "secret": "dGVzdC1zZWNyZXQ=",
                  "listenPort": 8080,
                  "maxMsgSizeBytes": 1048576
                }
                """;
    }
}
