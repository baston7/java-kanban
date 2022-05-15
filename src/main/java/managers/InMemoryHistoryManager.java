package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> historyList = new ArrayList<>();// Cписок для хранения истории просмотров

    @Override
    public void add(Task task) {
        if (task != null) {
            historyList.add(task);
            if (historyList.size() == 11) {
                historyList.remove(0);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
