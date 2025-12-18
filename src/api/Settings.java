package api;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Settings {
    public static final int PORT = 8080;
    public static final String HOST = "localhost";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final String CONTENT_TYPE = "application/json; charset=utf-8";
}
