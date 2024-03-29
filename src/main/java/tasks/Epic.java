package tasks;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    //Список для хранения id подзадач. По рекомендации наставника храним не объекты подзадач, а их id
    private ArrayList<Integer> subtaskIdList = new ArrayList<>();
    private LocalDateTime endTimeEpic;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    public void deleteIdSubtaskList(Integer id) {
        subtaskIdList.remove(id);
        if (subtaskIdList.isEmpty()) {
            setStatus(Status.NEW);
        }
    }

    public void addIdSubtaskList(Integer id) {
        subtaskIdList.add(id);
    }

    @Override
    public void setEndTime(LocalDateTime endTimeEpic) {
        this.endTimeEpic = endTimeEpic;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTimeEpic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Epic)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Epic epic = (Epic) o;

        if (getSubtaskIdList() != null ? !getSubtaskIdList().equals(epic.getSubtaskIdList()) :
                epic.getSubtaskIdList() != null)
            return false;
        return endTimeEpic != null ? endTimeEpic.equals(epic.endTimeEpic) : epic.endTimeEpic == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getSubtaskIdList().hashCode();
        result = 31 * result + endTimeEpic.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return super.getId() + "," + TaskType.EPIC + "," + super.getName() + "," + super.getStatus() + "," +
                super.getDescription() + "," + super.getDuration() + "," + super.getStartTime() + "," + endTimeEpic +
                "\n";
    }
}
