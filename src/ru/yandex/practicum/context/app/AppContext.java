package ru.yandex.practicum.context.app;

import ru.yandex.practicum.codec.Codec;
import ru.yandex.practicum.exception.app.BeanInitializationException;
import ru.yandex.practicum.exception.app.ConfigurationNotFoundException;
import ru.yandex.practicum.exception.app.InvalidConfigurationException;
import ru.yandex.practicum.exception.web.DeserializationException;
import ru.yandex.practicum.mapper.JsonObjectMapper;
import ru.yandex.practicum.service.HmacService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.yandex.practicum.constant.AppConstants.DEFAULT_CONFIG_PATH;

/**
 * Контекст приложения, управляющий жизненным циклом компонентов (бины) и конфигурацией.
 * Инкапсулирует логику работы с конфигурацией для ограничения к ней прямого доступа.
 *
 * <p>Класс предоставляет централизованный доступ к экземплярам компонентов приложения
 * и загружает конфигурацию из файла по умолчанию. Поддерживает инициализацию бинов
 * с последующей настройкой (например, для {@link HmacService}).</p>
 *
 * <p>Реализует паттерн Singleton и использует внутреннее хранилище для кэширования
 * созданных бинов по их имени класса.</p>
 */
public class AppContext {
    private static final Map<String, Object> CONTEXT = new HashMap<>();

    private AppContext() {
    }

    public static <T> T getBean(Class<T> clazz) {
        if (CONTEXT.containsKey(clazz.getSimpleName())) {
            return clazz.cast(CONTEXT.get(clazz.getSimpleName()));
        }
        T bean;
        try {
            bean = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInitializationException(
                    String.format("Failed to initialize bean %s with error: %s", clazz.getSimpleName(), e.getMessage()));
        }

        postInitializeBean(bean);
        CONTEXT.put(clazz.getSimpleName(), bean);
        return bean;
    }

    public static int getListenPort() {
        return AppConfigProvider.getConfig().getListenPort();
    }

    public static int getMaxSizeBytes() {
        return AppConfigProvider.getConfig().getMaxMsgSizeBytes();
    }

    public static void setConfig(String config, boolean debug) {
        AppConfigProvider.setConfig(config, debug);
    }

    private static <T> void postInitializeBean(T bean) {
        if (bean instanceof HmacService hmacService) {
            hmacService.init(AppConfigProvider.getConfig().getSecret(), AppConfigProvider.getConfig().getHmacAlg());
        }
    }

    private static class AppConfigProvider {
        private static final Logger log = Logger.getLogger(AppConfigProvider.class.getName());

        private AppConfigProvider() {}

        private static AppConfig appConfig;
        private static boolean lock;

        private static AppConfig getConfig() {
            if (appConfig == null) {
                log.info("Loading config from file at path: " + DEFAULT_CONFIG_PATH);
                setConfig(loadConfig(), false);
                log.info("Successfully loaded config.");
            }
            return appConfig;
        }

        private static void setConfig(String config, boolean debug) {
            if (appConfig != null && lock) {
                throw new IllegalStateException("Config is already set");
            }
            lock = !debug;
            appConfig = parseConfig(config);
        }

        private static AppConfig parseConfig(String text) {
            try {
                AppConfig appConfig = JsonObjectMapper.fromJson(text, AppConfig.class);
                validateConfig(appConfig);
                return appConfig;
            }  catch (DeserializationException e) {
                throw new InvalidConfigurationException("Failed to parse config file: " + e.getMessage());
            }
        }

        private static String loadConfig() {
            InputStream is = AppContext.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH);
            if (is == null) {
                throw new ConfigurationNotFoundException("Failed to find config file with name: " + DEFAULT_CONFIG_PATH);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                return br.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new InvalidConfigurationException("Error while reading config file: " + e.getMessage());
            }
        }

        private static void validateConfig(AppConfig appConfig) {
            if (appConfig.getListenPort() < 0) {
                throw new InvalidConfigurationException("listenPort cannot be negative");
            }
            if (appConfig.getMaxMsgSizeBytes() <= 0) {
                throw new InvalidConfigurationException("maxMsgSizeBytes must greater than zero");
            }
            if (appConfig.getHmacAlg() == null || appConfig.getHmacAlg().isBlank()) {
                throw new InvalidConfigurationException("hmacAlg must not be empty");
            }
            if (appConfig.getSecret() == null || appConfig.getSecret().isBlank()) {
                throw new InvalidConfigurationException("secret must not be empty");
            }
            if (!Codec.isValidBase64(appConfig.getSecret().getBytes())) {
                throw new InvalidConfigurationException("secret must be a valid base64 encoded string");
            }
        }

        /**
         * Здесь бы подошел record, но GSON 2.8.7 из прекода не умеет работать с ним (пытается использовать сеттеры).
         */
        private static class AppConfig {
            private String hmacAlg;
            private String secret;
            private int listenPort;
            private int maxMsgSizeBytes;

            public String getHmacAlg() {
                return hmacAlg;
            }

            public void setHmacAlg(String hmacAlg) {
                this.hmacAlg = hmacAlg;
            }

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public int getListenPort() {
                return listenPort;
            }

            public void setListenPort(int listenPort) {
                this.listenPort = listenPort;
            }

            public int getMaxMsgSizeBytes() {
                return maxMsgSizeBytes;
            }

            public void setMaxMsgSizeBytes(int maxMsgSizeBytes) {
                this.maxMsgSizeBytes = maxMsgSizeBytes;
            }
        }
    }
}
