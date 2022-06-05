import managers.TaskManager;
import tasks.*;
import utilits.Managers;

public class Main {  // метод для тестирования программы
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Купить цветы", "Купить цветы девушке на др ");
        Task task1 = new Task("Починить авто", "Съездить в сервис");
        taskManager.createTask(task);
        taskManager.createTask(task1);
        //Создаем первый эпик и подзадачи для него
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("3  неделя бега", "пробежать в сумме 80 км", epic.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("4  неделя бега", "пробежать в сумме 100 км", epic.getId());
        taskManager.createSubtask(subtask3);
        //создаем второй эпик без подзадач
        Epic epic2 = new Epic("Подтягивания", "Выполнить 20 подтягиваний за раз");
        taskManager.createEpic(epic2);
        //тесты
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.deleteByIdEpic(epic.getId());
        System.out.println(taskManager.getHistory());
    }
}