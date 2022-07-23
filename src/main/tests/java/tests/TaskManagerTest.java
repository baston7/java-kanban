package tests;

import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public T taskManager;
    abstract T createTaskManager();
    public KVServer server=createKVServer();


    public KVServer createKVServer() {
        try {
            return new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }return null;
    }
    @BeforeEach
    public void updateTaskManager() {
        server.start();
        taskManager = createTaskManager();
    }
    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 215,
                LocalDateTime.of(2022, 11, 22, 10, 22));

        int id = taskManager.createTask(task);
        int idTaskInMap = taskManager.getAllTask().get(0).getId();

        assertEquals(1, id, "Метод не возвращает id задачи");
        assertEquals(1, idTaskInMap, "Метод не добавил задачу в хэшмап для хранения задач");

    }


    @Test
    public void testCreateSubtask() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);

        int idSubtask = taskManager.createSubtask(new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км", 30,
                LocalDateTime.of(2022, 11, 22, 10, 22), epic.getId()));

        int idSubtaskInMap = taskManager.getAllSubtask().get(0).getId();
        int idSubtaskInEpic = taskManager.getAllEpic().get(0).getSubtaskIdList().get(0);
        int idEpicInSubtask = taskManager.getAllSubtask().get(0).getEpicId();

        assertEquals(1, idEpicInSubtask, "Метод не добавил id Epic в Subtask");
        assertEquals(2, idSubtask, "Метод не возвращает id подзадачи");
        assertEquals(2, idSubtaskInEpic, "Метод не добавил id подзадачи в Epic");
        assertEquals(2, idSubtaskInMap, "Метод не добавил подзадачу в хэшмап для хранения подзадач");

    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");

        int idEpic = taskManager.createEpic(epic);
        int idEpicInMap = taskManager.getAllEpic().get(0).getId();

        assertEquals(1, idEpic, "Метод не возвращает id эпика");
        assertEquals(1, idEpicInMap, "Метод не добавил эпик в хэшмап для хранения эпиков");
    }

    @Test
    public void testGetAllTask() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 45);
        Task task1 = new Task("Починить авто", "Съездить в сервис",
                60, LocalDateTime.of(2022, 5, 22, 10, 22));

        boolean empty = taskManager.getAllTask().isEmpty();
        assertTrue(empty, "При несозданных задачах метод должен возвращать пустой список");

        taskManager.createTask(task);
        taskManager.createTask(task1);

        int sizeList = taskManager.getAllTask().size();
        assertEquals(2, sizeList, "Отсутсвуют задачи в возвращаемом методом List ");
    }
    @Test
    public void testGetAllSubtask() {

        boolean empty = taskManager.getAllSubtask().isEmpty();
        assertTrue(empty, "При несозданных задачах метод должен возвращать пустой список");
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");

        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",
                60,
                epic.getId()));
        taskManager.createSubtask(new Subtask("3 и 4 недели бег", "пробежать в сумме 80 км",
                50,
                epic.getId()));

        int sizeList = taskManager.getAllSubtask().size();
        assertEquals(2, sizeList, "Отсутсвуют задачи в возвращаемом методом List ");
    }

    @Test
    public void testGetAllEpic() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        boolean empty = taskManager.getAllEpic().isEmpty();
        assertTrue(empty, "При несозданных эпиках метод должен возвращать пустой список");
        taskManager.createEpic(epic);
        int sizeList = taskManager.getAllEpic().size();
        assertEquals(1, sizeList, "Отсутсвует эпик в возвращаемом методом List ");

    }

    @Test
    public void testDeleteTasks() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 50);
        Task task1 = new Task("Починить авто", "Съездить в сервис", 56);

        taskManager.createTask(task);
        taskManager.createTask(task1);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());

        int sizeHistoryList = taskManager.getHistory().size();
        boolean empty = taskManager.getAllTask().isEmpty();

        assertFalse(empty, "При cозданных задачах список не должен быть пуст");
        assertEquals(2, sizeHistoryList, "Неверно отображается количество просмотренных задач");

        taskManager.deleteTasks();
        boolean empty2 = taskManager.getAllTask().isEmpty();
        int sizeHistoryList2 = taskManager.getHistory().size();

        assertTrue(empty2, "После вызова метода список задач должен быть пуст");
        assertEquals(0, sizeHistoryList2, "Количество просмотренных задач должно быть равно 0");
    }

    @Test
    public void testDeleteSubtasks() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        int id = taskManager.createSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",
                60,
                epic.getId()));
        int id2 = taskManager.createSubtask(new Subtask("3 и 4 недели бег", "пробежать в сумме 80 км",
                50,
                epic.getId()));

        taskManager.getSubtaskById(id);
        taskManager.getSubtaskById(id2);

        boolean empty = taskManager.getAllSubtask().isEmpty();
        int sizeHistoryList = taskManager.getHistory().size();

        assertFalse(empty, "При cозданных подзадачах список не должен быть пуст");
        assertEquals(2, sizeHistoryList, "Неверно отображается количество просмотренных подзадач");

        taskManager.deleteSubtasks();
        boolean empty2 = taskManager.getAllSubtask().isEmpty();
        int sizeHistoryList2 = taskManager.getHistory().size();

        assertTrue(empty2, "После вызова метода список подзадач должен быть пуст");
        assertEquals(0, sizeHistoryList2, "Количество просмотренных подзадач должно быть равно 0");
    }

    @Test
    public void testDeleteEpics() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");

        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", 55,
                epic.getId()));
        taskManager.createSubtask(new Subtask("3 и 4 недели бег", "пробежать в сумме 80 км", 45,
                epic.getId()));

        taskManager.getEpicById(epic.getId());
        boolean isEmpty = taskManager.getAllEpic().isEmpty();
        boolean isEmpty2 = taskManager.getAllSubtask().isEmpty();

        int sizeHistoryList = taskManager.getHistory().size();

        assertFalse(isEmpty, "При cозданных эпиках, их список не должен быть пуст");
        assertFalse(isEmpty2, "При cозданных эпиках c подзадачами список подзадач не должен быть пуст");
        assertEquals(1, sizeHistoryList, "Неверно отображается количество просмотренных эпиков");

        taskManager.deleteEpics();

        boolean isEmpty3 = taskManager.getAllEpic().isEmpty();
        boolean isEmpty4 = taskManager.getAllSubtask().isEmpty();

        int sizeHistoryList2 = taskManager.getHistory().size();

        assertTrue(isEmpty3, "После вызова метода список эпиков должен быть пуст");
        assertTrue(isEmpty4, "После вызова метода список подзадач должен быть пуст");
        assertEquals(0, sizeHistoryList2, "Количество просмотренных подзадач должно быть равно 0");

    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 55);
        Task task2 = taskManager.getTaskById(22);
        assertNull(task2, "При попытке получить несуществующую задачу метод должен возвращать null");
        taskManager.createTask(task);
        int sizeHistoryList = taskManager.getHistory().size();
        assertEquals(0, sizeHistoryList, "Cписок просмотренных задач должен быть пуст");
        Task task3 = taskManager.getTaskById(1);
        int sizeHistoryList2 = taskManager.getHistory().size();
        assertEquals(1, sizeHistoryList2, "Размер списка просмотренных задач должен соответствовать" +
                " количеству просмотренных задач");
        assertEquals(task, task3, "Ошибка при получении задачи");

    }

    @Test
    public void testGetSubtaskById() {
        Subtask subtask = taskManager.getSubtaskById(22);
        assertNull(subtask, "При попытке получить несуществующую подзадачу метод должен возвращать null");
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", 77,
                epic.getId());
        taskManager.createSubtask(subtask1);
        int sizeHistoryList = taskManager.getHistory().size();
        assertEquals(0, sizeHistoryList, "Cписок просмотренных задач должен быть пуст");
        Subtask subtask2 = taskManager.getSubtaskById(subtask1.getId());
        int sizeHistoryList2 = taskManager.getHistory().size();

        assertEquals(1, sizeHistoryList2, "Размер списка просмотренных задач должен соответствовать" +
                " количеству просмотренных задач");
        assertEquals(subtask1, subtask2, "Ошибка при получении подзадачи");
    }

    @Test
    public void testGetEpicById() {
        Epic epic = taskManager.getEpicById(22);
        assertNull(epic, "При попытке получить несуществующий эпик метод должен возвращать null");
        Epic epic2 = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic2);
        int sizeHistoryList = taskManager.getHistory().size();
        assertEquals(0, sizeHistoryList, "Cписок просмотренных задач должен быть пуст");
        Epic epic3 = taskManager.getEpicById(epic2.getId());
        int sizeHistoryList2 = taskManager.getHistory().size();

        assertEquals(1, sizeHistoryList2, "Размер списка просмотренных подзадач должен соответствовать" +
                " количеству просмотренных задач");
        assertEquals(epic2, epic3, "Ошибка при получении подзадачи");
    }


    @Test
    public void testUpdateTask() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 76);
        Task task2 = new Task("Оформить отпуск", "согласовать отпускную записку", Status.IN_PROGRESS);

        taskManager.createTask(task);
        taskManager.updateTask(task2, 44);

        assertEquals("Купить цветы", task.getName(), "Имя задачи не должно измениться");
        assertEquals("Купить цветы девушке на др ", task.getDescription(),
                "Описание задачи не должно измениться");
        assertEquals(Status.NEW, task.getStatus(), "Статус не должен измениться");

        taskManager.updateTask(task2, 1);

        assertEquals("Оформить отпуск", task.getName(), "Имя задачи должно измениться");
        assertEquals("согласовать отпускную записку", task.getDescription(),
                "Описание задачи должно измениться");
        assertEquals(Status.IN_PROGRESS, task.getStatus(), "Статус должен измениться");

    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("1  неделя бег", "пробежать в сумме 60 км",
                45, epic.getId());
        Subtask subtask2 = new Subtask("2 неделя  бег", "пробежать в сумме 80 км",
                40, epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("3 неделя  бег", "пробежать в сумме 100 км", Status.IN_PROGRESS,
                30, epic.getId());
        taskManager.updateSubtask(subtask3, 22);

        assertEquals("2 неделя  бег", subtask2.getName(), "Имя задачи не должно измениться");
        assertEquals("пробежать в сумме 80 км", subtask2.getDescription(),
                "Описание задачи не должно измениться");
        assertEquals(Status.NEW, subtask2.getStatus(), "Статус не должен измениться");

        taskManager.updateSubtask(subtask3, 3);

        assertEquals("3 неделя  бег", subtask2.getName(), "Имя задачи  должно измениться");
        assertEquals("пробежать в сумме 100 км", subtask2.getDescription(),
                "Описание задачи  должно измениться");
        assertEquals(Status.IN_PROGRESS, subtask2.getStatus(), "Статус  должен измениться");
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен измениться");
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц");
        taskManager.updateEpic(epic, 22);
        boolean isEmpty = taskManager.getAllEpic().isEmpty();
        assertTrue(isEmpty, "Cписок эпиков должен быть пуст");
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Плавание", "Проплыть 100 км");

        taskManager.updateEpic(epic2, 22);

        assertEquals("Бег", epic.getName(), "Имя эпика не должно измениться");
        assertEquals("выполнить цель по км за месяц", epic.getDescription(),
                "Описание эпика не должно измениться");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика не должен измениться");

        taskManager.updateEpic(epic2, 1);

        assertEquals("Плавание", epic.getName(), "Имя эпика должно измениться");
        assertEquals("Проплыть 100 км", epic.getDescription(), "Описание эпика  должно измениться");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика не должен измениться");
    }

    @Test
    public void testDeleteByIdTask() {
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 44);
        taskManager.createTask(task);
        taskManager.deleteByIdTask(22);
        int size = taskManager.getAllTask().size();
        assertEquals(1, size, "Cписок задач не должен быть пуст");
        taskManager.deleteByIdTask(1);
        int size2 = taskManager.getAllTask().size();
        assertEquals(0, size2, "Cписок задач должен быть пуст");
    }

    @Test
    public void testDeleteByIdSubtask() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("2 неделя  бег", "пробежать в сумме 80 км",
                77, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteByIdSubtask(33);
        int size = taskManager.getAllSubtask().size();
        assertEquals(1, size, "Cписок подзадач должен быть без изменений");
        Subtask subtask3 = new Subtask("3 неделя  бег", "пробежать в сумме 100 км",
                30, epic.getId()
        );
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3, 2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Cтатус эпика должен быть IN_PROGRESS");
        assertEquals(2, epic.getSubtaskIdList().get(0), "Эпик должен хранить id подзадачи");

        taskManager.deleteByIdSubtask(2);
        int size2 = taskManager.getAllSubtask().size();

        assertEquals(0, size2, "Cписок подзадач должен быть пуст");
        assertEquals(Status.NEW, epic.getStatus(), "Cтатус эпика должен быть NEW");

        assertTrue(epic.getSubtaskIdList().isEmpty(), "Эпик не должен хранить id подзадачи");

    }

    @Test
    public void testDeleteByIdEpic() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("2 неделя  бег", "пробежать в сумме 80 км",
                66, epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteByIdEpic(33);

        int size = taskManager.getAllEpic().size();
        int size2 = taskManager.getAllSubtask().size();

        assertEquals(1, size2, "Cписок подзадач должен быть без изменений");
        assertEquals(1, size, "Cписок эпиков должен быть без изменений");

        taskManager.deleteByIdEpic(1);

        int size3 = taskManager.getAllEpic().size();
        int size4 = taskManager.getAllSubtask().size();

        assertEquals(0, size4, "Cписок подзадач должен быть пуст");
        assertEquals(0, size3, "Cписок эпиков должен быть пуст");

    }

    @Test
    public void testGetSubtaskByEpic() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("2 неделя  бег", "пробежать в сумме 80 км",
                90, epic.getId());
        boolean isEmpty = taskManager.getSubtaskByEpic(epic).isEmpty();
        assertTrue(isEmpty, "Cписок подзадач должен быть пуст");
        taskManager.createSubtask(subtask);
        Subtask subtask2 = taskManager.getSubtaskByEpic(epic).get(0);

        assertEquals("2 неделя  бег", subtask2.getName(), "Имя не совпадает");
        assertEquals("пробежать в сумме 80 км", subtask2.getDescription(),
                "Описание подзадачи не совпадает");
        assertEquals(1, subtask2.getEpicId(), "id эпика не совпадает");
    }


    @Test
    public void testGetHistory() {
        List<Task> myHistoryList = new ArrayList<>();
        taskManager.getHistory();
        boolean isEmpty = taskManager.getHistory().isEmpty();
        assertTrue(isEmpty, "История просмотров задач должна быть пустая");

        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 99);
        Task task1 = new Task("Починить авто", "Съездить в сервис", 88);

        taskManager.createTask(task);
        taskManager.createTask(task1);

        //Создаем первый эпик и подзадачи для него
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",
                88, epic.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("3  неделя бега", "пробежать в сумме 80 км",
                49, epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("4  неделя бега", "пробежать в сумме 100 км",
                55, epic.getId());
        taskManager.createSubtask(subtask3);
        //создаем второй эпик без подзадач
        Epic epic2 = new Epic("Подтягивания", "Выполнить 20 подтягиваний за раз");
        taskManager.createEpic(epic2);
        taskManager.getTaskById(task.getId());
        myHistoryList.add(task);
        taskManager.getTaskById(task1.getId());
        myHistoryList.add(task1);
        taskManager.getEpicById(epic.getId());
        myHistoryList.add(epic);
        taskManager.getEpicById(epic.getId());
        myHistoryList.remove(epic);
        myHistoryList.add(epic);
        taskManager.getEpicById(epic2.getId());
        myHistoryList.add(epic2);
        taskManager.getTaskById(task.getId());
        myHistoryList.remove(task);
        myHistoryList.add(task);
        taskManager.getTaskById(task.getId());
        myHistoryList.remove(task);
        myHistoryList.add(task);
        taskManager.getSubtaskById(subtask.getId());
        myHistoryList.add(subtask);
        taskManager.getEpicById(epic.getId());
        myHistoryList.remove(epic);
        myHistoryList.add(epic);
        taskManager.getTaskById(task.getId());
        myHistoryList.remove(task);
        myHistoryList.add(task);
        taskManager.getEpicById(epic2.getId());
        myHistoryList.remove(epic2);
        taskManager.deleteByIdEpic(3);

        myHistoryList.remove(epic);
        myHistoryList.remove(subtask);
        myHistoryList.remove(subtask2);
        myHistoryList.remove(subtask3);

        myHistoryList.add(epic2);
        List<Task> list = taskManager.getHistory();
        assertEquals(myHistoryList, list, "Неверно отображается история просмотра задач");

    }

    @Test
    public void testTimes() {
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        taskManager.createSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",
                60, LocalDateTime.of(2022, 5, 22, 10, 22),
                epic.getId()));
        taskManager.createSubtask(new Subtask("3 и 4 недели бег", "пробежать в сумме 80 км",
                50, LocalDateTime.of(2022, 5, 22, 10, 22),
                epic.getId()));

        taskManager.getSubtaskById(2);
        taskManager.getSubtaskById(3);

        int sizeTimeSet = taskManager.getPrioritizedTasks().size();
        int sizeHistoryList = taskManager.getHistory().size();

        boolean empty = taskManager.getAllSubtask().isEmpty();

        assertEquals(1, sizeTimeSet, "Неверно отображается количество подзадач во времени");
        assertFalse(empty, "При cозданных подзадачах список не должен быть пуст");
        assertEquals(1, sizeHistoryList, "Неверно отображается количество просмотренных подзадач");

        taskManager.deleteSubtasks();
        boolean empty2 = taskManager.getAllSubtask().isEmpty();
        int sizeHistoryList2 = taskManager.getHistory().size();

        assertTrue(empty2, "После вызова метода список подзадач должен быть пуст");
        assertEquals(0, sizeHistoryList2, "Количество просмотренных подзадач должно быть равно 0");
    }

}

