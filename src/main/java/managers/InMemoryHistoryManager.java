package managers;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private static ArrayList<Task> historyList = new ArrayList<>();// Cписок для хранения истории просмотров

    @Override
    public void add(Task task) {
        historyList.add(task);
        if (historyList.size() == 11) {
            historyList.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
