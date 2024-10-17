package api;

public enum StatusCode {
    OK_WITH_BODY(200),
    OK_WITHOUT_BODY(201),
    NOT_FOUND(404),
    NOT_ACCEPTABLE(406),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
