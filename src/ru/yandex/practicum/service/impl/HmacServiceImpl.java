package ru.yandex.practicum.service.impl;

import ru.yandex.practicum.codec.Codec;
import ru.yandex.practicum.codec.impl.Base64Codec;
import ru.yandex.practicum.constant.AppConstants;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.exception.app.InternalServerException;
import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.schema.SignResponseDto;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.schema.VerifyResponseDto;
import ru.yandex.practicum.service.HmacService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Реализация сервиса для создания и проверки HMAC-подписей.
 *
 * <p>Сервис использует алгоритм HMAC (Hash-based Message Authentication Code) для генерации
 * цифровой подписи сообщения на основе секретного ключа. Поддерживает кодирование ключа
 * и подписи в формате Base64.</p>
 *
 * <p>Сервис требует инициализации перед использованием — установки секретного ключа и
 * алгоритма хеширования. После инициализации экземпляр готов к работе.</p>
 *
 * <p>Для кодирования/декодирования данных используется {@link Codec}, получаемый из
 * контекста приложения ({@link AppContext}).</p>
 */
public class HmacServiceImpl implements HmacService {
    private byte[] secret;
    private String algorithm;
    private boolean isInitialized;
    private final Codec codec;

    public HmacServiceImpl() {
        this.codec = AppContext.getBean(Base64Codec.class);
    }

    /**
     * Генерирует подпись для переданного сообщения.
     *
     * <p>Использует внутренний метод {@link #calculateSignature(String)} для вычисления HMAC.
     * Возвращает объект {@link SignResponseDto}, содержащий подпись в виде строки.</p>
     *
     * @param signRequestDto объект с полем {@code msg}, содержащим сообщение для подписи
     * @return объект {@link SignResponseDto}, содержащий вычисленную подпись
     */
    @Override
    public SignResponseDto sign(SignRequestDto signRequestDto) {
        return new SignResponseDto(calculateSignature(signRequestDto.getMsg()));
    }

    /**
     * Проверяет, соответствует ли переданная подпись сообщению.
     *
     * <p>Вычисляет HMAC для переданного сообщения и сравнивает его с предоставленной подписью
     * с помощью безопасного сравнения, чтобы избежать атак по времени.</p>
     *
     * @param verifyRequestDto объект, содержащий сообщение и ожидаемую подпись
     * @return объект {@link VerifyResponseDto} с результатом проверки в виде строки "true" или "false"
     */
    @Override
    public VerifyResponseDto verify(VerifyRequestDto verifyRequestDto) {
        String signature = calculateSignature(verifyRequestDto.getMsg());
        return new VerifyResponseDto(
                Boolean.toString(MessageDigest.isEqual(signature.getBytes(), verifyRequestDto.getSignature().getBytes()))
        );
    }

    @Override
    public void init(String secret, String algorithm) {
        if (isInitialized) {
            throw new InternalServerException("HmacService is already initialized");
        }
        this.secret = secret.getBytes(AppConstants.CHARSET);
        this.algorithm = AppConstants.HMAC + algorithm;
        isInitialized = true;
    }

    private String calculateSignature(String message) {
        if (!isInitialized) {
            throw new InternalServerException("HmacService is not initialized");
        }
        try {
            byte[] key = codec.decode(this.secret);
            Mac mac = Mac.getInstance(this.algorithm);
            mac.init(new SecretKeySpec(key, this.algorithm));
            byte[] sig = mac.doFinal(message.getBytes());
            return new String(codec.encode(sig), AppConstants.CHARSET);

        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerException("HMAC algorithm not found: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new InternalServerException("Invalid key: " + e.getMessage());
        }
    }
}
