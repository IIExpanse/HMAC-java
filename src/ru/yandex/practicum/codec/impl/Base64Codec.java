package ru.yandex.practicum.codec.impl;

import ru.yandex.practicum.codec.Codec;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Реализация кодировщика, использующая кодировку Base64 с URL-безопасным алфавитом.
 *
 * <p>Данный класс предоставляет методы для кодирования и декодирования массивов байтов
 * в формате Base64, пригодном для передачи в URL и файлах конфигурации, где недопустимы
 * символы, такие как '+' и '/'.</p>
 */
public class Base64Codec implements Codec {
    private static final Logger log = Logger.getLogger(Base64Codec.class.getName());

    /**
     * Кодирует переданный массив байтов в формате Base64 с использованием URL-безопасного алфавита.
     *
     * @param bytes массив байтов для кодирования
     * @return закодированный массив байтов в формате Base64
     */
    @Override
    public byte[] encode(byte[] bytes) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Encoding bytes with length: " + bytes.length);
        }
        return Base64.getUrlEncoder().encode(bytes);
    }

    /**
     * Декодирует массив байтов из формата Base64 обратно в исходный массив.
     *
     * @param bytes массив байтов в формате Base64 для декодирования
     * @return декодированный исходный массив байтов
     * @throws IllegalArgumentException если входные данные не являются корректным Base64
     */
    @Override
    public byte[] decode(byte[] bytes) {
        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("Decoding text with length: %d", bytes.length));
        }
        return Base64.getUrlDecoder().decode(bytes);
    }
}
