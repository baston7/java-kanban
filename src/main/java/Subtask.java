import java.util.Objects;

public class Subtask extends Task {
    private int EpicId;

    public Subtask(String name, String description, int EpicId) {
        super(name, description);
        this.EpicId = EpicId;
    }

    //еще один конструктор со статусом и id для удобства обновления подзадач
    public Subtask(String name, String description, String status, int id, int epicId) {
        super(name, description, status, id);
        EpicId = epicId;
    }

    public int getEpicId() {
        return EpicId;
    }

    public void setEpicId(int epicId) {
        EpicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return EpicId == subtask.EpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), EpicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "EpicId=" + EpicId +
                "} " + super.toString();
    }
}
