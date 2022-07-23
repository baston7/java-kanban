package tests;

import managers.HTTPTaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilits.Managers;

import java.time.LocalDateTime;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 44);
    Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
    Subtask subtask = new Subtask("1 и 2 недели бег",
            "пробежать в сумме 60 км",
            60, LocalDateTime.of(2022, 5, 22, 10, 22),
            epic.getId());
    Subtask subtask2 = new Subtask("3 недели бег",
            "пробежать в сумме 80 км",
            60, LocalDateTime.of(2022, 5, 25, 10, 22),
            epic.getId());

    @Override
    HTTPTaskManager createTaskManager() {
        return Managers.getDefaultHTTP();

    }

    @Test
    public void testSavedAndRecoverTasks() {
        //пустой список задач
        boolean isEmpty = taskManager.getAllTask().isEmpty();
        boolean isEmpty2 = taskManager.getHistory().isEmpty();

        assertTrue(isEmpty, "Cписок задач должен быть пуст");
        assertTrue(isEmpty2, "Cписок истории должен быть пуст");

        //пробуем получить задачу
        taskManager.getTaskById(2);
        HTTPTaskManager httpTaskManager = Managers.getDefaultHTTP();
        httpTaskManager.recoverManager(taskManager.forTestGetApi());

        boolean isEmpty3 = httpTaskManager.getAllTask().isEmpty();
        boolean isEmpty4 = httpTaskManager.getHistory().isEmpty();

        assertTrue(isEmpty3, "Cписок задач должен быть пуст");
        assertTrue(isEmpty4, "Cписок истории должен быть пуст");

        //cоздаем эпик и вызываем его для заполнения истории
        taskManager.createEpic(epic);
        taskManager.getEpicById(epic.getId());
        HTTPTaskManager httpTaskManager2 = Managers.getDefaultHTTP();


        boolean isEmpty5 = httpTaskManager2.getAllEpic().isEmpty();
        boolean isEmpty6 = httpTaskManager2.getHistory().isEmpty();
        assertTrue(isEmpty5, "Cписок задач должен быть пуст");
        assertTrue(isEmpty6, "Cписок истории должен быть пуст");

        //восстанавливаем менеджер и проверяем состояние
        httpTaskManager2.recoverManager(taskManager.forTestGetApi());
        assertEquals(1, httpTaskManager2.getAllEpic().size(), "Эпик не добавлен в новый менеджер");
        assertEquals(1, httpTaskManager2.getHistory().size(), "История просмотра задач не обновлена");

        //Создадим задачу, эпик с 2 подзадачами и выполним проверку для них
        HTTPTaskManager httpTaskManager3 = Managers.getDefaultHTTP();
        httpTaskManager3.createTask(task);
        httpTaskManager3.createEpic(epic);
        subtask.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        httpTaskManager3.createSubtask(subtask);
        httpTaskManager3.createSubtask(subtask2);
        httpTaskManager3.getSubtaskById(subtask.getId());
        httpTaskManager3.getTaskById(task.getId());

        HTTPTaskManager httpTaskManager4 = Managers.getDefaultHTTP();
        httpTaskManager4.recoverManager(httpTaskManager3.forTestGetApi());

        assertEquals(1, httpTaskManager4.getAllTask().size(), "Задача не добавлена в новый менеджер");
        assertEquals(1, httpTaskManager4.getAllEpic().size(), "Эпик не добавлен в новый менеджер");
        assertEquals(2, httpTaskManager4.getAllSubtask().size(), "Подзадачи не добавлены");
        assertEquals(2, httpTaskManager4.getHistory().size(), "История просмотра задач не обновлена");

    }


    @Test
    public void testRecoverTasksTimes() {
        //пустой список задач
        boolean isEmpty = taskManager.getPrioritizedTasks().isEmpty();

        assertTrue(isEmpty, "Cписок задач по времени  должен быть пуст");


        //пробуем получить задачу
        taskManager.getTaskById(2);
        HTTPTaskManager httpTaskManager = Managers.getDefaultHTTP();
        httpTaskManager.recoverManager(taskManager.forTestGetApi());

        boolean isEmpty3 = httpTaskManager.getPrioritizedTasks().isEmpty();

        assertTrue(isEmpty3, "Cписок задач по времени  должен быть пуст");

        //создаем эпик и проверяем заполнение задач по времени
        taskManager.createEpic(epic);
        HTTPTaskManager httpTaskManager2 = Managers.getDefaultHTTP();


        boolean isEmpty5 = httpTaskManager2.getPrioritizedTasks().isEmpty();
        assertTrue(isEmpty5, "Cписок задач по времени  должен быть пуст");

        //восстанавливаем менеджер и проверяем состояние
        httpTaskManager2.recoverManager(taskManager.forTestGetApi());
        assertEquals(0, httpTaskManager2.getPrioritizedTasks().size(),
                "Список задач по времени должен быть пуст");

        //Создадим задачу, эпик с 2 подзадачами и выполним проверку для них
        HTTPTaskManager httpTaskManager3 = Managers.getDefaultHTTP();
        httpTaskManager3.createTask(task);
        httpTaskManager3.createEpic(epic);
        subtask.setEpicId(epic.getId());
        subtask2.setEpicId(epic.getId());
        httpTaskManager3.createSubtask(subtask);
        httpTaskManager3.createSubtask(subtask2);
        httpTaskManager3.getSubtaskById(subtask.getId());
        httpTaskManager3.getTaskById(task.getId());

        HTTPTaskManager httpTaskManager4 = Managers.getDefaultHTTP();
        httpTaskManager4.recoverManager(httpTaskManager3.forTestGetApi());

        assertEquals(3, httpTaskManager4.getPrioritizedTasks().size(),
                "Список задач некорректно отображает их количество");
        TreeSet<Task> set = (TreeSet<Task>) httpTaskManager4.getPrioritizedTasks();

        assertEquals(subtask, set.first(), "Неверная сортировка задач по времени");
        assertEquals(task, set.last(), "Неверная сортировка задач по времени");
    }
}


