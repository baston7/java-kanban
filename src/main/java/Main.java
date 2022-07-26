import managers.HTTPTaskManager;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utilits.Managers;

import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException {
        KVServer server=new KVServer();
        server.start();
        HTTPTaskManager manager= Managers.getDefaultHTTP();

        Task task = new Task("Купить цветы", "Купить цветы девушке на др ", 44);
        Task task1 = new Task("Починить авто", "Съездить в сервис", 33);
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        Subtask subtask1 = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км",55, 3);
        Subtask subtask2 = new Subtask("3  неделя бега", "пробежать в сумме 80 км",50, 3);


        manager.createTask(task);
        manager.createTask(task1);
        manager.createEpic(epic);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.getTaskById(task.getId());
        manager.getSubtaskById(subtask1.getId());

        System.out.println("__________________________________________");

        //создаем новый менеджер и восстанавливаем его по ключу первого
        HTTPTaskManager recoverManager=Managers.getDefaultHTTP();
        recoverManager.recoverManager(manager.forTestGetApi());
        System.out.println("Все задачи: "+recoverManager.getAllTask());
        System.out.println("Все эпики: "+recoverManager.getAllEpic());
        System.out.println("Все подзадачи: "+recoverManager.getAllSubtask());
        System.out.println("История просмотра: "+recoverManager.getHistory());
        System.out.println("Задачи по времени: "+recoverManager.getPrioritizedTasks());

        server.stop();

    }
}