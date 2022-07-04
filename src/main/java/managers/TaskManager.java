package managers;

import tasks.*;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    Integer createTask(Task task);   //метод создания задач

    Integer createSubtask(Subtask subtask);  //метод создания подзадач

    Integer createEpic(Epic epic);   //метод создания эпиков

    List<Task> getAllTask();     //метод получения списка всех задач

    List<Subtask> getAllSubtask();     //метод получения списка всех подзадач

    List<Epic> getAllEpic();   //метод получения списка всех эпиков

    void deleteTasks();//метод удаления всех задач

    void deleteSubtasks(); //метод удаления всех подзадач

    void deleteEpics();   //метод удаления всех эпиков

    Task getTaskById(int id); //метод получения задачи по id

    Subtask getSubtaskById(int id);//метод получения подзадачи по id

    Epic getEpicById(int id); //метод получения эпика по id

    void updateTask(Task newTask, int id);  //метод обновления задачи

    void updateSubtask(Subtask newSubtask, int id); //метод обновления подзадачи

    void updateEpic(Epic newEpic, int id);  //метод обновления эпика

    void deleteByIdTask(Integer id);  //метод удаления задачи по id

    void deleteByIdSubtask(Integer id); //метод удаления подзадачи по id

    void deleteByIdEpic(Integer id); //метод удаления эпика по id

    List<Subtask> getSubtaskByEpic(Epic epic);//Метод для получения списка всех подзадач определённого эпика
    List<Task> getHistory();
    Set<Task> getPrioritizedTasks();
}
