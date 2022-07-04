package tests;

import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;


import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private TaskManager taskManager;
    private final Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void createTaskManager() {
        taskManager = new InMemoryTaskManager();
        taskManager.createEpic(epic);
        subtask1 = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",55, epic.getId());
        subtask2 = new Subtask("3  неделя бега", "пробежать в сумме 80 км",50, epic.getId());
    }

    // Пустой список подзадач.
    @Test
    public void testEpicWithEmptySubtasks() {
        int sizeEpicMap = taskManager.getAllEpic().size();
        int sizeSubtaskMap = taskManager.getAllSubtask().size();
        Status status = taskManager.getAllEpic().get(0).getStatus();
        int sizeSubtaskIdList = taskManager.getAllEpic().get(0).getSubtaskIdList().size();
        assertEquals(1, sizeEpicMap, "эпик отсутствует в списке созданных эпиков,либо он там не один");
        assertEquals(Status.NEW, status, "статус эпика должен быть NEW");
        assertEquals(0, sizeSubtaskIdList, "список id подзадач в эпике должен быть пустым");
        assertEquals(0, sizeSubtaskMap, "список подзадач в менеджере должен быть пустым");
    }

    // Все подзадачи со статусом NEW.
    @Test
    public void testEpicWithSubtasksNewNew() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        int sizeSubtaskMap = taskManager.getAllSubtask().size();
        Status status = taskManager.getAllEpic().get(0).getStatus();
        int sizeSubtaskIdList = taskManager.getAllEpic().get(0).getSubtaskIdList().size();
        assertEquals(Status.NEW, status, "статус эпика должен быть NEW");
        assertEquals(2, sizeSubtaskIdList, "список id подзадач в эпике должен состоять из двух подзадач");
        assertEquals(2, sizeSubtaskMap, "список подзадач в менеджере должен состоять из двух подзадач");


    }

    //Все подзадачи со статусом DONE.
    @Test
    public void testEpicWithSubtasksDoneDone() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        int sizeSubtaskMap = taskManager.getAllSubtask().size();
        int sizeSubtaskIdList = taskManager.getAllEpic().get(0).getSubtaskIdList().size();
        taskManager.updateSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", Status.DONE,
                2, 1), 2);
        taskManager.updateSubtask(new Subtask("3  неделя бега", "пробежать в сумме 80 км", Status.DONE,
                3, 1), 3);
        Status status = taskManager.getAllEpic().get(0).getStatus();
        assertEquals(Status.DONE, status, "статус эпика должен быть DONE");
        assertEquals(2, sizeSubtaskIdList, "список id подзадач в эпике должен состоять из двух подзадач");
        assertEquals(2, sizeSubtaskMap, "список подзадач в менеджере должен состоять из двух подзадач");

    }

    //Подзадачи со статусами NEW и DONE.
    @Test
    public void testEpicWithSubtasksNewDone() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        int sizeSubtaskMap = taskManager.getAllSubtask().size();
        int sizeSubtaskIdList = taskManager.getAllEpic().get(0).getSubtaskIdList().size();
        taskManager.updateSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", Status.NEW,
                2, 1), 2);
        taskManager.updateSubtask(new Subtask("3  неделя бега", "пробежать в сумме 80 км", Status.DONE,
                3, 1), 3);
        Status status = taskManager.getAllEpic().get(0).getStatus();
        assertEquals(Status.NEW, status, "статус эпика должен быть NEW");
        assertEquals(2, sizeSubtaskIdList, "список id подзадач в эпике должен состоять из двух подзадач");
        assertEquals(2, sizeSubtaskMap, "список подзадач в менеджере должен состоять из двух подзадач");
    }

    // Подзадачи со статусом IN_PROGRESS.
    @Test
    public void testEpicWithSubtasksIpIp() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        int sizeSubtaskMap = taskManager.getAllSubtask().size();
        int sizeSubtaskIdList = taskManager.getAllEpic().get(0).getSubtaskIdList().size();
        taskManager.updateSubtask(new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",
                Status.IN_PROGRESS,
                2, 1), 2);
        taskManager.updateSubtask(new Subtask("3  неделя бега", "пробежать в сумме 80 км",
                Status.IN_PROGRESS,
                3, 1), 3);
        Status status = taskManager.getAllEpic().get(0).getStatus();
        assertEquals(Status.IN_PROGRESS, status, "статус эпика должен быть IN_PROGRESS");
        assertEquals(2, sizeSubtaskIdList, "список id подзадач в эпике должен состоять из двух подзадач");
        assertEquals(2, sizeSubtaskMap, "список подзадач в менеджере должен состоять из двух подзадач");
    }

}