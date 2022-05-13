package managers;

import tasks.*;
import utilits.Managers;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();   // Хэшмап для хранения обычных задач
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();    //Хэшмап для хранения подзадач
    private HashMap<Integer, Epic> epicMap = new HashMap<>();    //Хэшмап для хранения эпиков
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 0;  //Переменная для получения id

    @Override
    public Integer createTask(Task task) {  //метод создания задач
        task.setId(++id);
        task.setStatus(Status.NEW);
        taskMap.put(task.getId(), task);
        return id;
    }

    @Override
    public Integer createSubtask(Subtask subtask) { //метод создания подзадач
        if (epicMap.containsKey(subtask.getEpicId())) {
            subtask.setId(++id);
            subtask.setStatus(Status.NEW);
            subtaskMap.put(subtask.getId(), subtask);
            Epic epic = epicMap.get(subtask.getEpicId());// достаем Эпик чтобы записать в него id подзадачи
            epic.addIdSubtaskList(id);
            if (epic.getStatus().equals(Status.DONE)) { // если мы его добавили в эпик со статусом "DONE", его нужно поменять
                epic.setStatus(Status.IN_PROGRESS);
            }
            return id;
        } else {
            return null;
        }
    }

    @Override
    public Integer createEpic(Epic epic) {   //метод создания эпиков
        epic.setId(++id);
        epic.setStatus(Status.NEW);
        epicMap.put(epic.getId(), epic);
        return id;
    }

    @Override
    public ArrayList<Task> getAllTask() {     //метод получения списка всех задач
        if (!taskMap.isEmpty()) {
            ArrayList<Task> tasks = new ArrayList<>();
            for (Task task : taskMap.values()) {
                tasks.add(task);
            }
            return tasks;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {     //метод получения списка всех подзадач
        if (!subtaskMap.isEmpty()) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            for (Subtask subtask : subtaskMap.values()) {
                subtasks.add(subtask);
            }
            return subtasks;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Epic> getAllEpic() {     //метод получения списка всех эпиков
        if (!epicMap.isEmpty()) {
            ArrayList<Epic> epics = new ArrayList<>();
            for (Epic epic : epicMap.values()) {
                epics.add(epic);
            }
            return epics;
        } else {
            return null;
        }
    }

    @Override
    public void deleteTucks() { //метод удаления всех задач
        taskMap.clear();
    }

    @Override
    public void deleteSubtasks() { //метод удаления всех подзадач
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {  //Удаляем все id из Эпиков
            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            subtaskIdList.clear();
        }
    }

    @Override
    public void deleteEpics() {  //метод удаления всех эпиков
        epicMap.clear();
        subtaskMap.clear(); //Удаляем все подзадачи тоже, тк они не могут существовать без Эпиков
    }

    @Override
    public Task getTaskById(int id) { //метод получения задачи по id
        if (taskMap.containsKey(id)) {
            //тк размер списка не должен превышать 10 элементов, то постоянно это отслеживаем и удаляем лишний элемент
            historyManager.add(taskMap.get(id));
            return taskMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {//метод получения подзадачи по id
        if (subtaskMap.containsKey(id)) {
            //тк размер списка не должен превышать 10 элементов, то постоянно это отслеживаем и удаляем лишний элемент
            historyManager.add(subtaskMap.get(id));
            return subtaskMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(int id) { //метод получения эпика по id
        if (epicMap.containsKey(id)) {
            //тк размер списка не должен превышать 10 элементов, то постоянно это отслеживаем и удаляем лишний элемент
            historyManager.add(epicMap.get(id));
            return epicMap.get(id);
        } else {
            return null;
        }
    }

    @Override
    public void updateTask(Task newTask, int id) {  //метод обновления задачи
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            task.setName(newTask.getName());
            task.setDescription(newTask.getDescription());
            task.setStatus(newTask.getStatus());
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int id) { //метод обновления подзадачи
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            subtask.setName(newSubtask.getName());
            subtask.setDescription(newSubtask.getDescription());
            subtask.setStatus(newSubtask.getStatus());
            Epic epic = epicMap.get(subtask.getEpicId()); // проверяем и обновляем статус Эпика, если статус подзадачи изменился
            epicUpdateStatus(epic);
        }
    }

    @Override
    public void updateEpic(Epic newEpic, int id) {  //метод обновления эпика
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            epic.setName(newEpic.getName());
            /* не задаем статус эпика,т.к он устанавливается "NEW" только при создании,
             а меняется в зависимости от статуса подзадач */
            epic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void deleteByIdTask(Integer id) { //метод удаления задачи по id
        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
        }
    }

    @Override
    public void deleteByIdSubtask(Integer id) { //метод удаления подзадачи по id
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id); //обновляем иформацию по соответствующему Эпику
            Epic epic = epicMap.get(subtask.getEpicId());
            epic.deleteIdSubtaskList(id);
            epicUpdateStatus(epic);
            subtaskMap.remove(id);//удаляем подзадачу
        }
    }

    @Override
    public void deleteByIdEpic(Integer id) { //метод удаления эпика по id
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);//удаляем связанные подзадачи
            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            for (Integer idSubtask : subtaskIdList) {
                subtaskMap.remove(idSubtask);
            }
            epicMap.remove(id); //удаляем эпик
        }
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {//Метод для получения списка всех подзадач определённого эпика
        if (epicMap.containsKey(epic.getId())) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            if (!subtaskIdList.isEmpty()) {
                for (Integer id : subtaskIdList) {
                    subtasks.add(subtaskMap.get(id));
                }
            }
            return subtasks;
        } else {
            return null;
        }
    }

    private void epicUpdateStatus(Epic epic) {
        ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
        if (subtaskIdList.size() == 0) { // если список пустой, то устанавливаем статус NEW на эпик
            epic.setStatus(Status.NEW);
        } else {
            int counter = 0; // счетчик учета выполненных подзадач
            int counter2 = 0; // счетчик учета новых подзадач
            for (int i = 0; i < subtaskIdList.size(); i++) {
                Subtask subtaskInEpic = subtaskMap.get(subtaskIdList.get(i));
                if (subtaskInEpic.getStatus().equals(Status.DONE)) {
                    counter += 1;
                } else if (subtaskInEpic.getStatus().equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else if (subtaskInEpic.getStatus().equals(Status.NEW)) {
                    counter2 += 1;
                }
            }
            if (counter == subtaskIdList.size()) {
                epic.setStatus(Status.DONE);
            } else if (counter2 == subtaskIdList.size()) {
                epic.setStatus(Status.NEW);
            }
        }
    }
}
