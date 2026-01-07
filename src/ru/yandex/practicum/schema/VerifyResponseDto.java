package ru.yandex.practicum.schema;

public class VerifyResponseDto {
    private String ok;

    public VerifyResponseDto() {
    }

    public VerifyResponseDto(String ok) {
        this.ok = ok;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }
}
