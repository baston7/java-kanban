package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, long duration, int epicId) {
        super(name, description, status, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long duration, int epicId) {
        super(name, description, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long duration, LocalDateTime starTime, int epicId) {
        super(name, description, duration, starTime);
        super.getEndTime();
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;

        Subtask subtask = (Subtask) o;

        return getEpicId() == subtask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return super.getId() + "," + TaskType.SUBTASK + "," + super.getName() + "," + super.getStatus() + "," +
                super.getDescription() + "," + epicId + "," + super.getDuration() + "," + super.getStartTime()
                + "," + super.getEndTime() + "\n";
    }
}
