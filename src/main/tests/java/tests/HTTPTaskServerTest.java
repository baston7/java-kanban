package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskServerTest {
    //сначала создаем KVServer, запускаем его и только потом создаем и запускаем наш сервер для обработки запросов
    KVServer dataServer = createKVServer();
    HttpTaskServer httpServer;
    HTTPTaskManager manager;

    private final Gson gson = new GsonBuilder()
            .create();

    public KVServer createKVServer() {
        try {
            return new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpTaskServer createHttpTaskServer() {
        try {
            return new HttpTaskServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @BeforeEach
    public void updateTaskManager() throws IOException {
        dataServer.start();
        httpServer = createHttpTaskServer();
        httpServer.start();
        manager = httpServer.getHttpTaskManager();

    }

    @AfterEach
    public void stopServer() {
        httpServer.stop();
        dataServer.stop();
    }

    //по рекомендации наставника на каждый запрос создаем новый клиент

    @Test
    public void testCreateTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(new Task("Купить цветы", "Купить цветы девушке на др ", 215,
                LocalDateTime.of(2022, 11, 22, 10, 22)));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllTask().get(0).getId(), "Задача не создана");

    }

    @Test
    public void testCreateEpicAndSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllEpic().get(0).getId(), "Эпик не создан");
        assertEquals(0, manager.getAllEpic().get(0).getSubtaskIdList().size(), "Список подзадач не пуст");
        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(2, manager.getAllSubtask().get(0).getId(), "Подзадача не создана");
        assertEquals(1, manager.getAllEpic().get(0).getSubtaskIdList().size(), "Эпик не привязан к подзадаче");

    }

    @Test
    public void testGetAllTask() throws IOException, InterruptedException {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 45);
        Task task1 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 22, 10, 22));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        String json2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        int sizeList = manager.getAllTask().size();
        assertEquals(2, sizeList, "Отсутствуют задачи в возвращаемом методом List ");
        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(response3.body(), type);

        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(manager.getAllTask(), tasksList, "Сервер некорректно вернул список задач");
    }

    @Test
    public void testGetAllSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        int sizeList = manager.getAllSubtask().size();

        assertEquals(1, sizeList, "Отсутсвуют задачи в возвращаемом методом List ");

        Type type = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksList = gson.fromJson(response3.body(), type);

        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(manager.getAllSubtask(), tasksList, "Сервер некорректно вернул список подзадач");
    }

    @Test
    public void testGetAllEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        int sizeList = manager.getAllEpic().size();

        assertEquals(1, sizeList, "Отсутсвует эпик в возвращаемом методом List ");

        Type type = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> tasksList = gson.fromJson(response2.body(), type);

        assertEquals(200, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(manager.getAllEpic(), tasksList, "Сервер некорректно вернул список эпиков");
    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 45);
        Task task1 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 22, 10, 22));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        String json2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        int sizeList = manager.getAllTask().size();
        assertEquals(2, sizeList, "Отсутсвуют задачи в возвращаемом методом List ");

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        int sizeList2 = manager.getAllTask().size();

        assertEquals(0, sizeList2, "Задачи не удалены из менеджера");
        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
    }

    @Test
    public void testDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());
        int sizeList = manager.getAllSubtask().size();

        assertEquals(1, sizeList, "Отсутсвуют задачи в возвращаемом методом List ");

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        int sizeList2 = manager.getAllSubtask().size();

        assertEquals(0, sizeList2, "Задачи не удалены из менеджера");
        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        int sizeList = manager.getAllSubtask().size();
        int sizeList2 = manager.getAllEpic().size();

        assertEquals(1, sizeList, "Отсутствуют задачи в возвращаемом методом List ");
        assertEquals(1, sizeList2, "Отсутствуют эпики в возвращаемом методом List ");

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        int sizeList3 = manager.getAllSubtask().size();
        int sizeList4 = manager.getAllEpic().size();

        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(0, sizeList3, "Список подзадач не пуст");
        assertEquals(0, sizeList4, "Список эпитков не пуст");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(new Task("Купить цветы", "Купить цветы девушке на др ", 215,
                LocalDateTime.of(2022, 11, 22, 10, 22)));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        //пробуем получить задачу с неcуществующим id
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task/?id=22");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить задачу с некорректным id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/task/?i222");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить задачу с корректным id

        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client4.send(request4, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response4.body(), Task.class);
        Task task2 = manager.getAllTask().get(0);

        assertEquals(200, response4.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(task2, task, "Сервер неверно вернул задачу");
        assertEquals(1, manager.getHistory().size(), "История просмотра задач пустая");

    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        //пробуем получить задачу с неcуществующим id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/?id=22");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить задачу с некорректным id
        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/subtask/?i222");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client4.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response4.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить задачу с корректным id

        HttpClient client5 = HttpClient.newHttpClient();
        URI url5 = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client5.send(request5, HttpResponse.BodyHandlers.ofString());
        Subtask task = gson.fromJson(response5.body(), Subtask.class);
        Subtask task2 = manager.getAllSubtask().get(0);

        assertEquals(200, response5.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(task2, task, "Сервер неверно вернул задачу");
        assertEquals(1, manager.getHistory().size(), "История просмотра задач пустая");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //пробуем получить эпик с неcуществующим id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/epic/?id=22");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить эпик с некорректным id
        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/epic/?i222");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response4 = client4.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response4.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить эпик с корректным id

        HttpClient client5 = HttpClient.newHttpClient();
        URI url5 = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client5.send(request5, HttpResponse.BodyHandlers.ofString());
        Epic task = gson.fromJson(response5.body(), Epic.class);
        Epic task2 = manager.getAllEpic().get(0);

        assertEquals(200, response5.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(task2, task, "Сервер неверно вернул задачу");
        assertEquals(1, manager.getHistory().size(), "История просмотра задач пустая");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 76);
        Task task2 = new Task("Оформить отпуск", "согласовать отпускную записку", Status.IN_PROGRESS);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        task2.setId(1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task/");
        String json2 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllTask().size(), "Количество задач не совпадает");
        assertEquals("Оформить отпуск", manager.getAllTask().get(0).getName(), "Задача не обновлена");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("3 и 4 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 25, 10, 22),
                1);
        subtask2.setId(2);

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        String json3 = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllSubtask().size(), "Количество задач не совпадает");
        assertEquals("3 и 4 недели бег", manager.getAllSubtask().get(0).getName(), "Задача не обновлена");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epic2 = new Epic("Прыжки", "прыгнуть 100раз ");
        epic2.setId(1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/epic/");
        String json2 = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllEpic().size(), "Количество эпиков не совпадает");
        assertEquals("Прыжки", manager.getAllEpic().get(0).getName(), "Эпик не обновлен");
    }

    @Test
    public void testDeleteByIdTask() throws IOException, InterruptedException {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 76);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //пробуем удалить задачу с некорректным id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/task/?i222");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем удалить задачу с корректным id
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(0, manager.getAllTask().size(), "Задача не удалена");

    }

    @Test
    public void testDeleteByIdSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        //пробуем удалить задачу с некорректным id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/?i222");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllSubtask().size(), "Список подзадач не должен быть пуст");

        //пробуем удалить задачу с корректным id
        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        HttpResponse<String> response2 = client4.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(0, manager.getAllSubtask().size(), "Задача не удалена");

    }

    @Test
    public void testDeleteByIdEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        //пробуем удалить задачу с некорректным id
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/epic/?i222");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(1, manager.getAllEpic().size(), "Список эпиков не должен быть пуст");

        //пробуем удалить задачу с корректным id
        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        HttpResponse<String> response2 = client4.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(0, manager.getAllEpic().size(), "Эпик не удален");

    }

    @Test
    public void testGetSubtaskByEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        //пробуем получить подзадачу с некорректным id эпика
        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/epic/?i222");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");

        //пробуем получить подзадачу с корректным id эпика
        HttpClient client4 = HttpClient.newHttpClient();
        URI url4 = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response2 = client4.send(request4, HttpResponse.BodyHandlers.ofString());

        Type type = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasksList2 = manager.getAllSubtask();
        List<Subtask> tasksList = gson.fromJson(response2.body(), type);


        assertEquals(200, response2.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(tasksList.get(0), tasksList2.get(0), "Список подзадач не получен корректно");

    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22),
                1);

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/");
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        Subtask subtask2 = new Subtask("3 и 4 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 25, 10, 22),
                1);

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/subtask/");
        String json3 = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(body3).build();
        client3.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, manager.getHistory().size(), "Список истории должен быть пуст");

        // добавим в просмотры задачу
        HttpClient client5 = HttpClient.newHttpClient();
        URI url5 = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client5.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getHistory().size(), "Список истории не должен быть пуст");

        // Добавим ту же задачу в просмотры
        HttpClient client6 = HttpClient.newHttpClient();
        URI url6 = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();
        client6.send(request6, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, manager.getHistory().size(), "Список истории не должен измениться");

        // Добавим другую задачу в просмотры
        HttpClient client7 = HttpClient.newHttpClient();
        URI url7 = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        client7.send(request7, HttpResponse.BodyHandlers.ofString());
    }


    @Test
    public void testTimes() throws IOException, InterruptedException {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 45);
        Task task1 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 22, 10, 22));
        Task task2 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 24, 10, 22));
        Task task3 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 24, 9, 42));
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpClient client1 = HttpClient.newHttpClient();
        String json1 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        client1.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        String json2 = gson.toJson(task1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        client2.send(request2, HttpResponse.BodyHandlers.ofString());

        HttpClient client3 = HttpClient.newHttpClient();
        URI url3 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client3.send(request3, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<TreeSet<Task>>() {
        }.getType();
        TreeSet<Task> tasksList = gson.fromJson(response3.body(), type);

        assertEquals(200, response3.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(3, tasksList.size(), "Список сортированных задач не соответствует ожидаемому");
        assertEquals(manager.getTaskById(1), tasksList.last(), "Последняя задача не совпадает с ожидаемой");
        assertEquals(manager.getTaskById(3), tasksList.first(), "Первая задача не совпадает с ожидаемой");

        //создадим задачу с пересечением во времени
        HttpClient client4 = HttpClient.newHttpClient();
        String json4 = gson.toJson(task3);
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json4);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        client4.send(request4, HttpResponse.BodyHandlers.ofString());

        HttpClient client5 = HttpClient.newHttpClient();
        URI url5 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client5.send(request5, HttpResponse.BodyHandlers.ofString());
        Type type2 = new TypeToken<TreeSet<Task>>() {
        }.getType();
        TreeSet<Task> tasksList2 = gson.fromJson(response5.body(), type2);

        assertEquals(200, response5.statusCode(), "Код ответа от сервера не совпадает с ожидаемым");
        assertEquals(3, tasksList2.size(), "Список сортированных задач не соответствует ожидаемому");
        assertEquals(manager.getTaskById(1), tasksList2.last(), "Последняя задача не совпадает с ожидаемой");
        assertEquals(manager.getTaskById(3), tasksList2.first(), "Первая задача не совпадает с ожидаемой");
    }
}