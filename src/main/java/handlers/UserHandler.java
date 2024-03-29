package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.HTTPTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UserHandler implements HttpHandler {

    private final HTTPTaskManager httpTaskManager;

    public UserHandler(HTTPTaskManager httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
    }


    @Override
    public void handle(HttpExchange h) throws IOException {
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        try {
            String[] pathParts = h.getRequestURI().getPath().split("/");
            int pathPartsLength = pathParts.length;
            String type = "";
            if (pathPartsLength > 2) {
                type = pathParts[2];
            }
            // параметры запроса
            String query = h.getRequestURI().getQuery();
            Integer id = checkQuery(query);
            String method = h.getRequestMethod();

            if (h.getRequestURI().getPath().equals("/tasks/")) {
                String json = gson.toJson(httpTaskManager.getPrioritizedTasks());
                sendText(h, json);
                return;
            }

            if (type.equals("task")) {
                handleTask(h, method, query, id, gson);
            } else if (type.equals("subtask")) {
                handleSubTask(h, method, query, id, gson, pathParts);
            } else if (type.equals("epic")) {
                handleEpic(h, method, query, id, gson);
            } else if (type.equals("history") && query == null) {
                handleHistory(h,method,gson);
            }
        } finally {
            h.close();
        }
    }

    private void handleTask(HttpExchange h, String method, String query, Integer id, Gson gson) throws IOException {
        switch (method) {
            case "GET":
                if (query == null) {
                    String json = gson.toJson(httpTaskManager.getAllTask());
                    sendText(h, json);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                } else if (httpTaskManager.getTaskById(id) == null) {
                    System.out.println("Задача по указанному id отсутствует");
                    h.sendResponseHeaders(404, 0);
                    break;
                }
                String json = gson.toJson(httpTaskManager.getTaskById(id));
                sendText(h, json);
                break;

            case "POST":
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                Task task = gson.fromJson(value, Task.class);
                int idTask = task.getId();
                //примем что если пользователь указал id то это обновление задачи, если нет, то создание
                if (idTask == 0) {
                    httpTaskManager.createTask(task);
                    h.sendResponseHeaders(201, 0);
                    break;
                } else {
                    httpTaskManager.updateTask(task, idTask);
                    h.sendResponseHeaders(201, 0);
                    break;
                }

            case "DELETE":
                if (query == null) {
                    httpTaskManager.deleteTasks();
                    h.sendResponseHeaders(200, 0);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                httpTaskManager.deleteByIdTask(id);
                h.sendResponseHeaders(200, 0);
                break;

            default:
                System.out.println("сервер ждёт GET,POST,DELETE-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }

    }

    private void handleSubTask(HttpExchange h, String method, String query, Integer id, Gson gson, String[] pathParts)
            throws IOException {
        switch (method) {
            case "GET":
                if (query == null && pathParts[pathParts.length - 1].equals("subtask")) {
                    String json = gson.toJson(httpTaskManager.getAllSubtask());
                    sendText(h, json);
                    break;

                } else if (id != null && pathParts[pathParts.length - 1].equals("epic")) {
                    Epic epic = httpTaskManager.getEpicById(id);
                    if (epic == null) {
                        System.out.println("Эпика с таким id  не найдено");
                        h.sendResponseHeaders(404, 0);
                        break;
                    }
                    String json = gson.toJson(httpTaskManager.getSubtaskByEpic(epic));
                    sendText(h, json);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                } else if (httpTaskManager.getSubtaskById(id) == null) {
                    System.out.println("Задача по указанному id отсутствует");
                    h.sendResponseHeaders(404, 0);
                    break;
                }
                String json = gson.toJson(httpTaskManager.getSubtaskById(id));
                sendText(h, json);
                break;
            case "POST":
                String value = readText(h);
                JsonElement jsonElement = JsonParser.parseString(value);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                Subtask task = gson.fromJson(value, Subtask.class);
                int idSubTask = task.getId();
                //примем что если пользователь указал id то это обновление задачи, если нет, то создание
                if (idSubTask == 0) {
                    httpTaskManager.createSubtask(task);
                    h.sendResponseHeaders(201, 0);
                    break;
                } else {
                    httpTaskManager.updateSubtask(task, idSubTask);
                    h.sendResponseHeaders(201, 0);
                    break;
                }
            case "DELETE":
                if (query == null) {
                    httpTaskManager.deleteSubtasks();
                    h.sendResponseHeaders(200, 0);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                httpTaskManager.deleteByIdSubtask(id);
                h.sendResponseHeaders(200, 0);
                break;
            default:
                System.out.println("сервер ждёт GET,POST,DELETE-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleEpic(HttpExchange h, String method, String query, Integer id, Gson gson) throws IOException {
        switch (method) {
            case "GET":
                if (query == null) {
                    String json = gson.toJson(httpTaskManager.getAllEpic());
                    sendText(h, json);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                } else if (httpTaskManager.getEpicById(id) == null) {
                    System.out.println("Эпик по указанному id отсутствует");
                    h.sendResponseHeaders(404, 0);
                    break;
                }
                String json = gson.toJson(httpTaskManager.getEpicById(id));
                sendText(h, json);
                break;

            case "POST":
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                Epic task = gson.fromJson(value, Epic.class);
                int idTask = task.getId();
                //примем что если пользователь указал id то это обновление задачи, если нет, то создание
                if (idTask == 0) {
                    httpTaskManager.createEpic(task);
                    h.sendResponseHeaders(201, 0);
                    break;
                } else {
                    httpTaskManager.updateEpic(task, idTask);
                    h.sendResponseHeaders(201, 0);
                    break;
                }
            case "DELETE":
                if (query == null) {
                    httpTaskManager.deleteEpics();
                    h.sendResponseHeaders(200, 0);
                    break;
                } else if (id == null) {
                    System.out.println("id задачи не указан корректно");
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                httpTaskManager.deleteByIdEpic(id);
                h.sendResponseHeaders(200, 0);
                break;

            default:
                System.out.println("сервер ждёт GET,POST,DELETE-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
                break;
        }
    }

    private void handleHistory(HttpExchange h, String method,Gson gson) throws IOException {
        if ("GET".equals(method)) {
            String json = gson.toJson(httpTaskManager.getHistory());
            sendText(h, json);
        } else {
            System.out.println("сервер ждёт GET запрос, а получил: " + h.getRequestMethod());
            h.sendResponseHeaders(405, 0);
        }
    }



    private void sendText(HttpExchange h, String json) throws IOException {
        byte[] resp = json.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    // Проверяем переданный id на корректность
    private Integer checkQuery(String query) {
        if (query == null) {
            return null;
        }
        if (query.contains("=")) {
            String[] pathsQuery = query.split("=");
            if (!pathsQuery[0].equals("id")) {
                return null;
            }
            if (pathsQuery.length < 2) {
                return null;
            } else {
                return Integer.parseInt(pathsQuery[1]);
            }
        }
        return null;
    }
}

