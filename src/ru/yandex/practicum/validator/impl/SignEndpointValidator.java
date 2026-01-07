package ru.yandex.practicum.validator.impl;

import ru.yandex.practicum.exception.web.HttpBadRequestException;
import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.validator.HttpRequestValidator;

public class SignEndpointValidator implements HttpRequestValidator<SignRequestDto> {
    @Override
    public void validateBody(SignRequestDto body) {
        if (body.getMsg() == null || body.getMsg().isBlank()) {
            throw new HttpBadRequestException("msg field cannot be empty");
        }
    }
}
