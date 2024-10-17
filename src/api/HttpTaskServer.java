package api;

import api.hadlers.TaskHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress(Settings.HOST, Settings.PORT), 0);

        server.createContext(TaskHandler.handlePath, new TaskHandler(manager));
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
