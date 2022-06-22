package managers;

import customs.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private File file;

    public static void main(String[] args) {
        FileBackedTasksManager backedTasksManager = new FileBackedTasksManager(new File("book.csv"));
        Task task = new Task("Купить цветы", "Купить цветы девушке на др ");
        Task task1 = new Task("Починить авто", "Съездить в сервис");
        backedTasksManager.createTask(task);
        backedTasksManager.createTask(task1);
        //Создаем первый эпик и подзадачи для него
        Epic epic = new Epic("Бег", "выполнить цель по км за месяц ");
        backedTasksManager.createEpic(epic);
        Subtask subtask = new Subtask("1 и 2 недели бег", "пробежать в сумме 60 км", epic.getId());
        backedTasksManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("3  неделя бега", "пробежать в сумме 80 км", epic.getId());
        backedTasksManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("4  неделя бега", "пробежать в сумме 100 км", epic.getId());
        backedTasksManager.createSubtask(subtask3);
        //создаем второй эпик без подзадач
        Epic epic2 = new Epic("Подтягивания", "Выполнить 20 подтягиваний за раз");
        backedTasksManager.createEpic(epic2);
        backedTasksManager.getTaskById(task.getId());
        backedTasksManager.getTaskById(task1.getId());
        backedTasksManager.getEpicById(epic.getId());
        backedTasksManager.getEpicById(epic2.getId());
        backedTasksManager.getSubtaskById(subtask.getId());
        backedTasksManager.getSubtaskById(subtask3.getId());
        FileBackedTasksManager manager2 = loadFromFile(new File("book.csv"));
        List<Task> historyList = manager2.getHistory();
        List<Task> taskList = manager2.getAllTask();
        List<Subtask> subtaskList = manager2.getAllSubtask();
        List<Epic> epicList = manager2.getAllEpic();
    }

    public FileBackedTasksManager(File file) {
        if (file != null) {
            this.file = file;
        }
    }

    @Override
    public Integer createTask(Task task) {
        Integer taskID = super.createTask(task);
        save();
        return taskID;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer subtaskID = super.createSubtask(subtask);
        save();
        return subtaskID;
    }

    @Override
    public Integer createEpic(Epic epic) {
        Integer epicID = super.createEpic(epic);
        save();
        return epicID;
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        var task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        var task = super.getSubtaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        var task = super.getEpicById(id);
        save();
        return task;
    }

    @Override
    public void updateTask(Task newTask, int id) {
        super.updateTask(newTask, id);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int id) {
        super.updateSubtask(newSubtask, id);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic, int id) {
        super.updateEpic(newEpic, id);
        save();
    }

    @Override
    public void deleteByIdTask(Integer id) {
        super.deleteByIdTask(id);
        save();
    }

    @Override
    public void deleteByIdSubtask(Integer id) {
        super.deleteByIdSubtask(id);
        save();
    }

    @Override
    public void deleteByIdEpic(Integer id) {
        super.deleteByIdEpic(id);
        save();
    }

    private void save() {
        try (FileWriter filewriter = new FileWriter(file)) {
            filewriter.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTask()) {
                filewriter.write(task.toString());
            }
            for (Task task : getAllSubtask()) {
                filewriter.write(task.toString());
            }
            for (Task task : getAllEpic()) {
                filewriter.write(task.toString());
            }
            filewriter.write("\n");
            filewriter.write(toString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Error", e.getCause());
        }
    }

    public static Task fromString(String value) {
        if (value != null && !value.isBlank() && !value.isEmpty()) {
            String[] splitTasks = value.split(",");
            if (TaskType.valueOf(splitTasks[1]).equals(TaskType.TASK)) {
                Task task = new Task(splitTasks[2], splitTasks[4], Status.valueOf(splitTasks[3]),
                        Integer.parseInt(splitTasks[0]));
                return task;
            } else if (TaskType.valueOf(splitTasks[1]).equals(TaskType.SUBTASK)) {
                Subtask subtask = new Subtask(splitTasks[2], splitTasks[4], Status.valueOf(splitTasks[3]),
                        Integer.parseInt(splitTasks[0]), Integer.parseInt(splitTasks[5]));
                return subtask;
            } else if (TaskType.valueOf(splitTasks[1]).equals(TaskType.EPIC)) {
                Epic epic = new Epic(splitTasks[2], splitTasks[4], Status.valueOf(splitTasks[3]),
                        Integer.parseInt(splitTasks[0]));
                return epic;
            }
        }
        return null;
    }

    public static String toString(HistoryManager manager) {
        if (manager != null) {
            StringBuilder builder = new StringBuilder();
            if (manager.getHistory() != null) {
                for (Task task : manager.getHistory()) {
                    builder.append(task.getId()).append(",");
                }
                return builder.toString();
            }
        }
        return null;
    }

    public static List<Integer> fromLastString(String value) {
        List<Integer> idTasks = new ArrayList<>();
        if (value != null && !value.isBlank() && !value.isEmpty()) {
            String[] splitTasks = value.split(",");
            for (int i = 0; i < splitTasks.length; i++) {
                idTasks.add(Integer.parseInt(splitTasks[i]));
            }
            return idTasks;
        } else {
            return null;
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        String result = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                result += scanner.nextLine() + "\n";
            }
            String[] splitLine = result.split("\n");
            for (int i = 1; i < splitLine.length; i++) {
                if (i != splitLine.length - 1) {
                    manager.addRecoverTask(fromString(splitLine[i]));
                } else {
                    // добавляем для всех эпиков id подзадач, тк по условию в файле нет четкого порядка
                    if (!manager.getAllSubtask().isEmpty()) {
                        for (Subtask subtask : manager.getAllSubtask()) {
                            for (Epic epic : manager.getAllEpic()) {
                                if (epic.getId() == subtask.getEpicId()) {
                                    epic.getSubtaskIdList().add(subtask.getId());
                                }
                            }
                        }
                    }
                    // обрабатываем последнюю строку для истории просмотров задач
                    List<Integer> idTasks = fromLastString(splitLine[splitLine.length - 1]);
                    for (Integer id : idTasks) {
                        manager.getTaskById(id);
                        manager.getEpicById(id);
                        manager.getSubtaskById(id);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error", e.getCause());
        }
        return manager;
    }

}
