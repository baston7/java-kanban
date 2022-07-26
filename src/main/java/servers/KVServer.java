package servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    private static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                sendHeaders(h,"Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа",
                        403);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    sendHeaders(h,"Key для загрузки пустой. key указывается в пути: /load/{key}", 400);
                    return;
                }
                if (!data.containsKey(key)) {
                    sendHeaders(h, "Значение по указанному ключу отсутствует", 404);
                    return;
                }
                sendText(h, data.get(key));
                sendHeaders(h,String.format("Значение для ключа  %s  успешно отправлено!",key), 200);
            } else {
                sendHeaders(h,String.format("/load ждёт GET-запрос, а получил: %s",h.getRequestMethod()), 405);
            }
        } finally {
            h.close();
        }
    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                sendHeaders(h,"Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа",
                        403);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().split("/")[2];
                if (key.isEmpty()) {
                    sendHeaders(h,"Key для сохранения пустой. key указывается в пути: /load/{key}", 400);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    sendHeaders(h,"Value для сохранения пустой. value указывается в теле запроса", 400);
                    return;
                }
                data.put(key, value);
                sendHeaders(h,String.format("Значение для ключа  %s  успешно обновлено!",key), 200);
            } else {
                sendHeaders(h,String.format("/save ждёт POST-запрос, а получил: %s",h.getRequestMethod()), 405);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                sendHeaders(h,String.format("/register ждёт GET-запрос, а получил %s",h.getRequestMethod()), 405);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        System.out.println("Cервер на порту " + PORT + " остановлен");
        server.stop(0);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
    private void sendHeaders(HttpExchange h, String message, int code) throws IOException {
        System.out.println(message);
        h.sendResponseHeaders(code, 0);
    }
}