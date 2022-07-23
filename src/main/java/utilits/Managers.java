package utilits;

import managers.*;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HTTPTaskManager getDefaultHTTP() {
        return new HTTPTaskManager("http://localhost:8078");
    }
}