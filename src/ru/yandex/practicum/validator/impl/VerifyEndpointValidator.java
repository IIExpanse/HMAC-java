package ru.yandex.practicum.validator.impl;

import ru.yandex.practicum.codec.Codec;
import ru.yandex.practicum.constant.AppConstants;
import ru.yandex.practicum.exception.web.HttpBadRequestException;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.validator.HttpRequestValidator;

public class VerifyEndpointValidator implements HttpRequestValidator<VerifyRequestDto> {
    @Override
    public void validateBody(VerifyRequestDto body) {
        if (body.getMsg() == null || body.getSignature() == null) {
            throw new HttpBadRequestException("msg or signature field is missing or null");
        }
        if (!Codec.isValidBase64(body.getSignature().getBytes(AppConstants.CHARSET))) {
            throw new HttpBadRequestException("signature is not a valid base64 encoded string");
        }
    }
}
