package ru.yandex.practicum.codec;

import java.util.Base64;

public interface Codec {
    byte[] encode(byte[] bytes);

    byte[] decode(byte[] text);

    /**
     * Проверяет, является ли переданный массив байтов корректным Base64-кодированным значением.
     * Метод возвращает {@code false} при ошибках декодирования.</p>
     *
     * @param bytes массив байтов для проверки; должен быть в формате Base64
     * @return {@code true}, если данные корректно декодируются как Base64, иначе {@code false}
     */
    static boolean isValidBase64(byte[] bytes) {
        try {
            Base64.getUrlDecoder().decode(bytes);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
