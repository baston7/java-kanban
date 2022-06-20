package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    //Список для хранения id подзадач. По рекомендации наставника храним не объекты подзадач, а их id
    private ArrayList<Integer> subtaskIdList = new ArrayList<>();

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIdList, epic.subtaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList);
    }

    @Override
    public String toString() {
        return  super.getId() +","+ TaskType.EPIC+","+super.getName()+","+super.getStatus()+","+
                super.getDescription()+"\n";
    }
}
