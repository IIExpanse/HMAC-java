package ru.yandex.practicum.service;

import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.schema.SignResponseDto;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.schema.VerifyResponseDto;

public interface HmacService {
    SignResponseDto sign(SignRequestDto signRequestDto);

    VerifyResponseDto verify(VerifyRequestDto verifyRequestDto);

    void init(String secret, String algorithm);
}
