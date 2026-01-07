package ru.yandex.practicum.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.practicum.exception.web.DeserializationException;

/**
 * Утилитарный класс для преобразования объектов в формат JSON и обратно.
 */
public class JsonObjectMapper {
    private JsonObjectMapper() {}

    private static final Gson GSON = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return GSON.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new DeserializationException("Error while parsing json: " + e.getMessage());
        }
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        } else {
            return GSON.toJson(object);
        }
    }
}