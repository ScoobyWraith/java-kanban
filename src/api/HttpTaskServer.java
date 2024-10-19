package api;

import api.adapters.DurationAdapter;
import api.adapters.LocalDateTimeAdapter;
import api.hadlers.EpicHandler;
import api.hadlers.HistoryHandler;
import api.hadlers.PrioritizedTasksHandler;
import api.hadlers.SubtaskHandler;
import api.hadlers.TaskHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer server;

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(Settings.HOST, Settings.PORT), 0);

        server.createContext(TaskHandler.handlePath, new TaskHandler(manager));
        server.createContext(SubtaskHandler.handlePath, new SubtaskHandler(manager));
        server.createContext(EpicHandler.handlePath, new EpicHandler(manager));
        server.createContext(HistoryHandler.handlePath, new HistoryHandler(manager));
        server.createContext(PrioritizedTasksHandler.handlePath, new PrioritizedTasksHandler(manager));
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.start();
    }

    public void start() {
        server.start();
        System.out.println("HttpTaskServer запущен...");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpTaskServer остановлен");
    }
}
