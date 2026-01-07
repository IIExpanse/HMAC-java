package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.schema.SignResponseDto;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.schema.VerifyResponseDto;
import ru.yandex.practicum.utils.TestUtils;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HmacServiceImplTest {
    private HmacServiceImpl hmacService;

    @BeforeAll
    static void init() {
        AppContext.setConfig(TestUtils.loadTestConfig(), true);
    }

    @BeforeEach
    void setUp() {
        hmacService = AppContext.getBean(HmacServiceImpl.class);
    }

    @Test
    void testSignSuccessful() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");

        SignResponseDto signResponseDto = hmacService.sign(signRequestDto);
        assertFalse(signResponseDto.getSignature().isEmpty());
    }

    @Test
    void testVerifySuccessful() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");
        SignResponseDto signResponseDto = hmacService.sign(signRequestDto);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg(signRequestDto.getMsg());
        verifyRequestDto.setSignature(signResponseDto.getSignature());

        VerifyResponseDto verifyResponseDto = hmacService.verify(verifyRequestDto);
        assertTrue(Boolean.parseBoolean(verifyResponseDto.getOk()));
    }

    @Test
    void testVerifyConstantTime() {
        String s = "a".repeat(1_000_000);
        Duration durationFullCompare = getCompareDurationForString(s);

        char[] chars = s.toCharArray();
        chars[0] = 'b';
        Duration durationQuickCompare = getCompareDurationForString(new String(chars));

        assertTrue(durationQuickCompare.multipliedBy(10).compareTo(durationFullCompare) > 0);
    }

    private Duration getCompareDurationForString(String s) {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg(s);
        SignResponseDto signResponseDto = hmacService.sign(signRequestDto);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg(signRequestDto.getMsg());
        verifyRequestDto.setSignature(signResponseDto.getSignature());

        Instant start = Instant.now();
        hmacService.verify(verifyRequestDto);
        Instant end = Instant.now();
        return Duration.between(start, end);
    }
}