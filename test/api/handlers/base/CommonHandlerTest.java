package api.handlers.base;

import api.HttpTaskServer;
import api.Settings;
import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public abstract class CommonHandlerTest {
    private static File tmpFile;
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected HttpClient client;
    protected Gson gson;

    public CommonHandlerTest() throws IOException {
        tmpFile = File.createTempFile("tmp-api-manager", ".csv");
        manager = Managers.getFileBackedManager(tmpFile);
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        if (tmpFile != null) {
            Files.deleteIfExists(tmpFile.toPath());
        }
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    protected String getPathForEndpoint(String endpoint) {
        return "http://" + Settings.HOST + ":" + Settings.PORT + endpoint;
    }

    protected HttpResponse<String> get(HttpClient client, URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> delete(HttpClient client, URI url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> post(HttpClient client, URI url, String data) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected abstract String getEndpoint();
}
