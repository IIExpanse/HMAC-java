package ru.yandex.practicum.schema;

public class SignResponseDto {
    private String signature;

    public SignResponseDto() {
    }

    public SignResponseDto(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
