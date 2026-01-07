package ru.yandex.practicum.http;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.constant.AppConstants;
import ru.yandex.practicum.context.app.AppContext;
import ru.yandex.practicum.mapper.JsonObjectMapper;
import ru.yandex.practicum.schema.SignRequestDto;
import ru.yandex.practicum.schema.SignResponseDto;
import ru.yandex.practicum.schema.VerifyRequestDto;
import ru.yandex.practicum.schema.VerifyResponseDto;
import ru.yandex.practicum.utils.TestHttpServer;
import ru.yandex.practicum.utils.TestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class HttpIntegrationTest {
    private static TestHttpServer server;
    private static HttpClient client;
    private static String url;

    @BeforeAll
    static void setUp() {
        AppContext.setConfig(TestUtils.loadTestConfig(), true);
        server = TestUtils.initializeNewHttpServer();
        client = HttpClient.newHttpClient();
        url = "http://localhost" + ":" + server.getPort();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void testSuccessful() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        assertEquals(200, signResponse.statusCode());

        SignResponseDto signResponseDto = JsonObjectMapper.fromJson(signResponse.body(), SignResponseDto.class);
        assertNotNull(signResponseDto.getSignature());
        assertFalse(signResponseDto.getSignature().isBlank());

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg(signRequestDto.getMsg());
        verifyRequestDto.setSignature(signResponseDto.getSignature());
        HttpRequest verifyRequest = buildPostRequest(getVerifyUri(), JsonObjectMapper.toJson(verifyRequestDto));
        HttpResponse<String> verifyResponse = sendRequest(verifyRequest);
        assertEquals(200, verifyResponse.statusCode());

        VerifyResponseDto verifyResponseDto = JsonObjectMapper.fromJson(verifyResponse.body(), VerifyResponseDto.class);
        assertTrue(Boolean.parseBoolean(verifyResponseDto.getOk()));
    }

    @Test
    void testVerifyFailedWithChangedMessage() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        assertEquals(200, signResponse.statusCode());
        SignResponseDto signResponseDto = JsonObjectMapper.fromJson(signResponse.body(), SignResponseDto.class);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg("messsage");
        verifyRequestDto.setSignature(signResponseDto.getSignature());

        HttpRequest verifyRequest = buildPostRequest(getVerifyUri(), JsonObjectMapper.toJson(verifyRequestDto));
        HttpResponse<String> verifyResponse = sendRequest(verifyRequest);
        assertEquals(200, verifyResponse.statusCode());

        VerifyResponseDto verifyResponseDto = JsonObjectMapper.fromJson(verifyResponse.body(), VerifyResponseDto.class);
        assertFalse(Boolean.parseBoolean(verifyResponseDto.getOk()));
    }

    @Test
    void testVerifyFailedWithChangedSignature() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        assertEquals(200, signResponse.statusCode());
        SignResponseDto signResponseDto = JsonObjectMapper.fromJson(signResponse.body(), SignResponseDto.class);

        byte[] changedSignature = signResponseDto.getSignature().getBytes();
        changedSignature[0] = (byte) (changedSignature[0] + 1);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg(signRequestDto.getMsg());
        verifyRequestDto.setSignature(new String(changedSignature, AppConstants.CHARSET));

        HttpRequest verifyRequest = buildPostRequest(getVerifyUri(), JsonObjectMapper.toJson(verifyRequestDto));
        HttpResponse<String> verifyResponse = sendRequest(verifyRequest);
        assertEquals(200, verifyResponse.statusCode());

        VerifyResponseDto verifyResponseDto = JsonObjectMapper.fromJson(verifyResponse.body(), VerifyResponseDto.class);
        assertFalse(Boolean.parseBoolean(verifyResponseDto.getOk()));
    }

    @Test
    void test400ResponseCodeWithInvalidSignatureFormat() {
        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setMsg("message");
        verifyRequestDto.setSignature("@@@");

        HttpRequest verifyRequest = buildPostRequest(getVerifyUri(), JsonObjectMapper.toJson(verifyRequestDto));
        HttpResponse<String> verifyResponse = sendRequest(verifyRequest);
        assertEquals(400, verifyResponse.statusCode());
        assertEquals("signature is not a valid base64 encoded string", verifyResponse.body());
    }

    @Test
    void test400ResponseCodeWithInvalidMessage() {
        SignRequestDto signRequestDto = new SignRequestDto();

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        assertEquals(400, signResponse.statusCode());
        assertEquals("msg field cannot be empty", signResponse.body());

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto();
        verifyRequestDto.setSignature("aaa");

        HttpRequest verifyRequest = buildPostRequest(getVerifyUri(), JsonObjectMapper.toJson(verifyRequestDto));
        HttpResponse<String> verifyResponse = sendRequest(verifyRequest);
        assertEquals(400, verifyResponse.statusCode());
        assertEquals("msg or signature field is missing or null", verifyResponse.body());

    }

    @Test
    void test413ResponseCodeWithTooLargeMessage() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("a".repeat(AppContext.getMaxSizeBytes() + 1));

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        assertEquals(413, signResponse.statusCode());

        Pattern pattern = Pattern.compile("Request body size exceeds max \\d+ bytes");
        assertTrue(pattern.matcher(signResponse.body()).matches());
    }

    @Test
    void testSignatureIsDeterministic() {
        SignRequestDto signRequestDto = new SignRequestDto();
        signRequestDto.setMsg("message");

        HttpRequest signRequest = buildPostRequest(getSignUri(), JsonObjectMapper.toJson(signRequestDto));
        HttpResponse<String> signResponse = sendRequest(signRequest);
        String signature = JsonObjectMapper.fromJson(signResponse.body(), SignResponseDto.class).getSignature();

        for (int i = 0; i < 100; i++) {
            signResponse = sendRequest(signRequest);
            assertEquals(signature, JsonObjectMapper.fromJson(signResponse.body(), SignResponseDto.class).getSignature());
        }
    }

    private HttpRequest buildPostRequest(String uri, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new AssertionError(e);
        }
    }

    private String getSignUri() {
        return url + "/sign";
    }

    private String getVerifyUri() {
        return url + "/verify";
    }
}