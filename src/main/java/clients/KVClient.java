package clients;

import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVClient {
    private final String serverUrl;
    private long API_TOKEN;
    private final HttpClient client = HttpClient.newHttpClient();

    public KVClient(String serverUrl) {
        this.serverUrl = serverUrl;
        registration();
    }

    public long getAPI_TOKEN() {
        return API_TOKEN;
    }

    public void setAPI_TOKEN(long API_TOKEN) {
        this.API_TOKEN = API_TOKEN;
    }

    private void registration() {
        if (API_TOKEN != 0) {
            System.out.println("Ваш API_TOKEN = " + API_TOKEN);
            return;
        }
        URI registrationUrl = URI.create(serverUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(registrationUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            API_TOKEN = jsonElement.getAsLong();
            System.out.println("Ваш API_TOKEN = " + API_TOKEN);

        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + registrationUrl +
                    "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void put(String key, String json) {
        URI saveUrl = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + saveUrl +
                    "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }


    public String load(String key) {
        String load = null;
        URI loadUrl = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(loadUrl)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // передаем парсеру тело ответа в виде строки, содержащей данные в формате JSON
                JsonElement jsonElement = JsonParser.parseString(response.body());
                if (!jsonElement.isJsonArray()) { // проверяем, точно ли мы получили JSON-массив
                    System.out.println("Ответ от сервера не соответствует ожидаемому.");
                    return null;
                }
                // преобразуем результат разбора текста в JSON-объект
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                load = jsonArray.toString();

            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return load;
    }

}


