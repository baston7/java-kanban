package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import clients.KVClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVClient client;
    private final Gson gson;

    private final String urlKVServer;

    public HTTPTaskManager(String urlKVServer) {
        this.urlKVServer = urlKVServer;
        this.client = new KVClient(urlKVServer);
        this.gson = new GsonBuilder()
                .create();

    }

    public String getUrlKVServer() {
        return urlKVServer;
    }

    //для тестов создаем класс чтобы было удобно восстанавливать менеджер
    public long forTestGetApi() {
        return client.getAPI_TOKEN();
    }

    @Override
    //не сохраняем задачи по времени, тк мы сами это восстановим вызвав методы.(рекомендация наставника)
    protected void save() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        if (!getAllTask().isEmpty()) {
            List<Task> tasks = getAllTask();
            client.put("tasks", gson.toJson(tasks));

        }
        if (!getAllSubtask().isEmpty()) {
            String subtasksJson = gson.toJson(getAllSubtask());
            client.put("subtasks", subtasksJson);

        }
        if (!getAllEpic().isEmpty()) {
            String epicsJson = gson.toJson(getAllEpic());
            client.put("epics", epicsJson);

        }
        if (!getHistory().isEmpty()) {
            //будем укладывать id задач, а потом восстанавливать по ним задачи
            List<Integer> idTasksHistory = new ArrayList<>();
            List<Task> tasks = getHistory();
            for (Task task : tasks) {
                idTasksHistory.add(task.getId());
            }
            String historyJson = gson.toJson(idTasksHistory);
            client.put("history", historyJson);

        }
    }


    public void recoverManager(long API_TOKEN) {
        //необходимо восстановить токен тк, подразумевается что менеджер новый
        client.setAPI_TOKEN(API_TOKEN);

        String tasks = client.load("tasks");
        if (tasks != null) {
            Type type = new TypeToken<List<Task>>() {
            }.getType();
            List<Task> tasksList = gson.fromJson(tasks, type);
            //восстанавливаем задачи,
            for (Task task : tasksList) {
                addRecoverTask(task);
            }

        }

        String subtasks = client.load("subtasks");
        if (subtasks != null) {

            Type type2 = new TypeToken<List<Subtask>>() {
            }.getType();
            List<Subtask> subtasksList = gson.fromJson(subtasks, type2);
            for (Subtask subtask : subtasksList) {
                addRecoverTask(subtask);
            }
        }

        String epics = client.load("epics");
        if (epics != null) {
            Type type3 = new TypeToken<List<Epic>>() {
            }.getType();
            List<Epic> epicsList = gson.fromJson(epics, type3);
            for (Epic epic : epicsList) {
                addRecoverTask(epic);
            }
        }

        String historyID = client.load("history");
        if (historyID != null) {
            Type type4 = new TypeToken<List<Integer>>() {
            }.getType();
            List<Integer> historyIdList = gson.fromJson(historyID, type4);
            //восстанавливаем историю просмотров
            for (Integer id : historyIdList) {
                getTaskById(id);
                getEpicById(id);
                getSubtaskById(id);
            }
        }
    }
}
