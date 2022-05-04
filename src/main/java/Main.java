import manager.TaskManager;
import tasks.*;

import java.util.ArrayList;

public class Main {  // метод для тестирования программы
    public static void main(String[] args) {
        //Создаем задачи
        TaskManager taskManager = new TaskManager();
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ");
        Task task1 = new Task("Починить авто", "Съездить в сервис");
        taskManager.createTask(task);
        taskManager.createTask(task1);
        //Создаем первый эпик и подзадачи для него
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("3 и 4  недели бег", "пробежать в сумме 100 км", epic.getId());
        taskManager.createSubtask(subtask2);
        //создаем второй эпик и подзадачи для него
        Epic epic2 = new Epic("Подтягивания", "Выполнить 20 подтягиваний за раз");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Найти турник", "Найти и купить в магазине турник", epic2.getId());
        taskManager.createSubtask(subtask3);
        //Выводим в консоль
        System.out.println(task);
        System.out.println(task1);
        System.out.println(epic);
        System.out.println(epic2);
        System.out.println(subtask);
        System.out.println(subtask2);
        System.out.println(subtask3);
        //Обновляем две подзадачи и одну задачу
        Subtask subtask4 = new Subtask("1 и 2 недели бег", "пробежать в сумме 70 км",
                "IN_PROGRESS", subtask.getId(), epic.getId());
        taskManager.updateSubtask(subtask4, subtask.getId());
        Subtask subtask5 = new Subtask("Найти турник", "Найти и купить в магазине турник", "DONE",
                subtask3.getId(), epic2.getId());
        taskManager.updateSubtask(subtask5, subtask3.getId());
        Task task2 = new Task("Купить цветы", "Купить цветы девушке на др ",
                "IN_PROGRESS", task.getId());
        taskManager.updateTask(task2, task.getId());
        //Выводим в консоль
        System.out.println(task);
        System.out.println(subtask);
        System.out.println(subtask3);
        System.out.println(epic);
        System.out.println(epic2);
        //Удаляем одну из подзадач и выводим в консоль соответствующий эпик
        taskManager.deleteByIdSubtask(subtask2.getId());
        System.out.println(epic);
        //Получаем списки и выводим их в консоль для всех подзадач и подзадач для эпика
        ArrayList<Subtask> list = taskManager.getAllSubtask();
        System.out.println(list);
        ArrayList<Subtask> list2 = taskManager.getSubtaskByEpic(epic2);
        System.out.println(list2);
    }
}
