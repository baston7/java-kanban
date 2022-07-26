package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import handlers.UserHandler;
import managers.HTTPTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilits.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;


public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final HTTPTaskManager httpTaskManager;


    public HttpTaskServer() throws IOException {
        this.httpTaskManager = Managers.getDefaultHTTP();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task/", new UserHandler(httpTaskManager));
        server.createContext("/tasks/subtask/", new UserHandler(httpTaskManager));
        server.createContext("/tasks/epic/", new UserHandler(httpTaskManager));
        server.createContext("/tasks/history/", new UserHandler(httpTaskManager));
        server.createContext("/tasks/", new UserHandler(httpTaskManager));
    }

    public HTTPTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }



    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Cервер на порту " + PORT + " остановлен");
        server.stop(0);
    }
}
