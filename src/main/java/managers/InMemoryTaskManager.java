package managers;

import tasks.*;
import utilits.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskMap = new HashMap<>();   // Хэшмап для хранения обычных задач
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();    //Хэшмап для хранения подзадач
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();    //Хэшмап для хранения эпиков
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> timesSet = new TreeSet<>();
    private int id = 0;  //Переменная для получения id

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    // метод для добавления задач в приватные поля при восстановлении из файла
    protected void addRecoverTask(Task task) {
        if (task != null && task.getId() != 0) {
            if (task instanceof Subtask) {
                subtaskMap.put(task.getId(), (Subtask) task);
                timesSet.add(task);
            } else if (task instanceof Epic) {
                epicMap.put(task.getId(), (Epic) task);
            } else {
                taskMap.put(task.getId(), task);
                timesSet.add(task);
            }
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return timesSet;
    }


    @Override
    public Integer createTask(Task task) {  //метод создания задач
        boolean check = checkTimesSet(task);
        if (check) {
            task.setId(generationId());
            task.setStatus(Status.NEW);
            task.setStartTime(task.getStartTime());
            task.setDuration(task.getDuration());
            timesSet.add(task);
            taskMap.put(task.getId(), task);
            return task.getId();
        }
        return null;
    }


    @Override
    public Integer createSubtask(Subtask subtask) { //метод создания подзадач
        if (epicMap.containsKey(subtask.getEpicId())) {
            boolean check = checkTimesSet(subtask);
            if (check) {
                subtask.setId(generationId());
                subtask.setStatus(Status.NEW);
                subtask.setDuration(subtask.getDuration());
                subtask.setStartTime(subtask.getStartTime());
                timesSet.add(subtask);
                subtaskMap.put(subtask.getId(), subtask);
                Epic epic = epicMap.get(subtask.getEpicId());// достаем Эпик чтобы записать в него id подзадачи
                epic.addIdSubtaskList(id);
                epicUpdateTimes(epic);
                if (epic.getStatus().equals(Status.DONE)) { // если мы его добавили в эпик со статусом "DONE", его нужно поменять
                    epic.setStatus(Status.IN_PROGRESS);
                }
                return subtask.getId();
            }
        }
        return null;
    }

    @Override
    public Integer createEpic(Epic epic) {   //метод создания эпиков
        epic.setId(generationId());
        epic.setStatus(Status.NEW);
        epicMap.put(epic.getId(), epic);
        return id;
    }

    @Override
    public List<Task> getAllTask() {     //метод получения списка всех задач
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Subtask> getAllSubtask() {     //метод получения списка всех подзадач
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<Epic> getAllEpic() {     //метод получения списка всех эпиков
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public void deleteTasks() { //метод удаления всех задач
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
            timesSet.remove(task);
        }
        taskMap.clear();
    }

    @Override
    public void deleteSubtasks() {//метод удаления всех подзадач
        for (Subtask subtask : subtaskMap.values()) {
            timesSet.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {  //Удаляем все id из Эпиков
            List<Integer> subtaskIdList = epic.getSubtaskIdList();
            subtaskIdList.clear();
            epicUpdateTimes(epic);
        }
    }

    @Override
    public void deleteEpics() {//метод удаления всех эпиков
        for (Integer id : epicMap.keySet()) {
            historyManager.remove(id);
        }
        epicMap.clear();
        for (Subtask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
            timesSet.remove(subtask);
        }
        subtaskMap.clear(); //Удаляем все подзадачи тоже, тк они не могут существовать без Эпиков
    }

    @Override
    public Task getTaskById(int id) { //метод получения задачи по id
        var task = taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {//метод получения подзадачи по id
        var task = subtaskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) { //метод получения эпика по id
        var task = epicMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task newTask, int id) {  //метод обновления задачи
        if (taskMap.containsKey(id)&& checkTimesSet(newTask)) {
            Task task = taskMap.get(id);
            task.setName(newTask.getName());
            task.setDescription(newTask.getDescription());
            task.setStatus(newTask.getStatus());
            task.setDuration(newTask.getDuration());
            task.setStartTime(newTask.getStartTime());
            timesSet.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int id) { //метод обновления подзадачи
        if (subtaskMap.containsKey(id)&&checkTimesSet(newSubtask)) {
            Subtask subtask = subtaskMap.get(id);
            subtask.setName(newSubtask.getName());
            subtask.setDescription(newSubtask.getDescription());
            if (newSubtask.getStatus() != null) {
                subtask.setStatus(newSubtask.getStatus());
            }
            subtask.setStartTime(newSubtask.getStartTime());
            subtask.setDuration(newSubtask.getDuration());
            timesSet.add(subtask);
                Epic epic = epicMap.get(subtask.getEpicId()); // проверяем и обновляем статус Эпика, если статус подзадачи изменился
                epicUpdateStatus(epic);
                epicUpdateTimes(epic);
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
            historyManager.remove(id);
            timesSet.remove(getTaskById(id));
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
            epicUpdateTimes(epic);
            timesSet.remove(subtask);
            historyManager.remove(id);
            subtaskMap.remove(id);//удаляем подзадачу
        }
    }

    @Override
    public void deleteByIdEpic(Integer id) { //метод удаления эпика по id
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);//удаляем связанные подзадачи
            ArrayList<Integer> subtaskIdList = epic.getSubtaskIdList();
            for (Integer idSubtask : subtaskIdList) {
                timesSet.remove(getSubtaskById(idSubtask));
                subtaskMap.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
            historyManager.remove(id);
            epicMap.remove(id); //удаляем эпик
        }
    }

    @Override
    public List<Subtask> getSubtaskByEpic(Epic epic) {//Метод для получения списка всех подзадач определённого эпика
        if (epicMap.containsKey(epic.getId())) {
            List<Subtask> subtasks = new ArrayList<>();
            List<Integer> subtaskIdList = epic.getSubtaskIdList();
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void epicUpdateStatus(Epic epic) {
        List<Integer> subtaskIdList = epic.getSubtaskIdList();
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

    private void epicUpdateTimes(Epic epic) {
        if (!epic.getSubtaskIdList().isEmpty()) {
            long durationEpic = 0;
            LocalDateTime starTimeEpic = null;
            for (Subtask subtask : getSubtaskByEpic(epic)) {
                if (subtask.getStartTime() != null) {
                    starTimeEpic = subtask.getStartTime();
                    durationEpic += subtask.getDuration();
                    if (subtask.getStartTime().isBefore(starTimeEpic)) {
                        starTimeEpic = subtask.getStartTime();
                    }
                } else if (subtask.getStartTime() == null) {
                    durationEpic += subtask.getDuration();
                }
                epic.setDuration(durationEpic);
            }
            if (starTimeEpic != null) {
                epic.setStartTime(starTimeEpic);
                epic.setEndTime(starTimeEpic.plus(Duration.ofMinutes(durationEpic)));
            }
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
        }
    }

    private boolean checkTimesSet(Task task) {
        if (task != null && task.getStartTime() != null) {
            for (Task task1 : timesSet) {
                if (task1.getStartTime() != null && task.getStartTime().isBefore(task1.getStartTime()) &&
                        task.getEndTime().isAfter(task1.getStartTime())) {
                    return false;
                } else if (task1.getStartTime() != null && task.getStartTime().isBefore(task1.getEndTime()) &&
                        task.getEndTime().isAfter(task1.getStartTime())) {
                    return false;
                }
            }
            return true;
        } else if (task != null && task.getStartTime() == null) {
            return true;
        }
        return false;
    }

    private Integer generationId() {
        return ++id;
    }
}
