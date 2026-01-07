package ru.yandex.practicum.handler.impl;

import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.handler.HttpRequestHandler;
import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.schema.SignResponseDto;
import ru.yandex.practicum.service.impl.HmacServiceImpl;
import ru.yandex.practicum.validator.impl.SignEndpointValidator;

import java.util.logging.Logger;

public class SignEndpointHandler extends HttpRequestHandler<SignRequestDto, SignResponseDto> {
    private static final Logger log = Logger.getLogger(SignEndpointHandler.class.getName());

    public SignEndpointHandler() {
        super(AppContext.getBean(SignEndpointValidator.class), SignRequestDto.class,
                AppContext.getBean(HmacServiceImpl.class)::sign);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
