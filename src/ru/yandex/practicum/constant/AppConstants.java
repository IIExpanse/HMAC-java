package ru.yandex.practicum.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AppConstants {
    private AppConstants() {}

    public static final String DEFAULT_CONFIG_PATH = "ru/yandex/practicum/context/app/config.json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String HMAC = "Hmac";
    public static final Charset CHARSET = StandardCharsets.UTF_8;
}
