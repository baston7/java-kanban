import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIdList = new ArrayList<>(); /*список для хранения id подзадач. По рекомендации
                                                                    наставника храним не объекты подзадач, а их id
                                                                  */

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
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
        return "Epic{" +
                "subtaskIdList=" + subtaskIdList +
                "} " + super.toString();
    }
}
