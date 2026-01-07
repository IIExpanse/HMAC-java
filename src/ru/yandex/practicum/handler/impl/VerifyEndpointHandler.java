package ru.yandex.practicum.handler.impl;

import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.handler.HttpRequestHandler;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.schema.VerifyResponseDto;
import ru.yandex.practicum.service.impl.HmacServiceImpl;
import ru.yandex.practicum.validator.impl.VerifyEndpointValidator;

import java.util.logging.Logger;

public class VerifyEndpointHandler extends HttpRequestHandler<VerifyRequestDto, VerifyResponseDto> {
    private static final Logger log = Logger.getLogger(VerifyEndpointHandler.class.getName());

    public VerifyEndpointHandler() {
        super(AppContext.getBean(VerifyEndpointValidator.class), VerifyRequestDto.class,
                AppContext.getBean(HmacServiceImpl.class)::verify);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
