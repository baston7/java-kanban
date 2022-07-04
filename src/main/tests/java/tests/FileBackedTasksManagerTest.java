package tests;

import managers.FileBackedTasksManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("book2.csv"));
    }

    @Test
    public void testSavedAndRecoverTasks() {
        boolean isEmpty = taskManager.getAllTask().isEmpty();
        boolean isEmpty2 = taskManager.getHistory().isEmpty();
        assertTrue(isEmpty, "Cписок задач должен быть пуст");
        assertTrue(isEmpty2, "Cписок истории должен быть пуст");
        taskManager.getTaskById(88);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.
                loadFromFile(new File("book2.csv"));
        boolean isEmpty3 = fileBackedTasksManager.getAllTask().isEmpty();
        boolean isEmpty4 = fileBackedTasksManager.getHistory().isEmpty();
        assertTrue(isEmpty3, "Cписок задач должен быть пуст");
        assertTrue(isEmpty4, "Cписок истории должен быть пуст");
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц");
        taskManager.createEpic(epic);
        boolean isEmpty5 = taskManager.getAllEpic().isEmpty();
        boolean isEmpty6 = taskManager.getHistory().isEmpty();
        assertFalse(isEmpty5, "Cписок эпиков не должен быть пуст");
        assertTrue(isEmpty6, "Cписок истории должен быть пуст");
        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(
                new File("book2.csv"));
        Epic epic2 = fileBackedTasksManager2.getAllEpic().get(0);
        assertEquals(epic, epic2, "Эпик не найден в файле");
        int size = fileBackedTasksManager2.getHistory().size();
        assertEquals(0, size, "Cписок истории должен быть пуст");
        taskManager.getEpicById(1);
        FileBackedTasksManager fileBackedTasksManager3 = FileBackedTasksManager.loadFromFile(
                new File("book2.csv"));
        int size2 = fileBackedTasksManager3.getHistory().size();
        assertEquals(1, size2, "Cписок истории не должен быть пуст");
    }

    @Test
    public void testRecoverTasksTimes() {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(new File("book.csv"));
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 44);
        Task task1 = new Task("Починить авто", "Съездить в сервис", 33);
        backedTasksManager.createTask(task);
        backedTasksManager.createTask(task1);
        //Создаем первый эпик и подзадачи для него
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        backedTasksManager.createEpic(epic);
        Integer id1 = backedTasksManager.createSubtask(new Subtask("1 и 2 недели бег",
                "пробежать в сумме 60 км",
                60, LocalDateTime.of(2022, 5, 22, 10, 22),
                epic.getId()));
        Integer id2 = backedTasksManager.createSubtask(new Subtask("3 недели бег",
                "пробежать в сумме 80 км",
                60, LocalDateTime.of(2022, 5, 25, 10, 22),
                epic.getId()));
        Integer id3 = backedTasksManager.createSubtask(new Subtask("4 недели бег",
                "пробежать в сумме 100 км",
                60, LocalDateTime.of(2022, 5, 25, 10, 34),
                epic.getId()));
        //создаем второй эпик без подзадач
        Epic epic2 = new Epic("Подтягивания", "Выполнить 20 подтягиваний за раз");
        backedTasksManager.createEpic(epic2);
        backedTasksManager.getTaskById(task.getId());
        backedTasksManager.getTaskById(task1.getId());
        backedTasksManager.getEpicById(epic.getId());
        backedTasksManager.getEpicById(epic2.getId());
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File("book.csv"));
        List<Task> historyList = manager2.getHistory();
        List<Task> taskList = manager2.getAllTask();
        List<Subtask> subtaskList = manager2.getAllSubtask();
        List<Epic> epicList = manager2.getAllEpic();
        Set<Task> setTimes = manager2.getPrioritizedTasks();
        assertEquals(4, historyList.size(), "Ошибка в истории просмотра задач");
        assertEquals(2, taskList.size(), "Менеджер неверно уложил задачи в лист");
        assertEquals(2, subtaskList.size(), "Менеджер неверно уложил подзадачи в лист");
        assertEquals(2, epicList.size(), "Менеджер неверно уложил эпики в лист");
        assertEquals(4, setTimes.size(), "Не совпадает размер списка задач по времени с ожидаемым");
    }
}
