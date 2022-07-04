package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int id;
    private Status status;
    private long duration;
    private LocalDateTime starTime;
    private LocalDateTime endTime=null;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, long duration) {
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public Task(String name, String description, long duration, LocalDateTime starTime) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.starTime = starTime;
        this.endTime=getEndTime();
    }

    public Task(String name, String description, Status status, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    //еще один конструктор для обновления задач со статусом и id, для удобства обновления
    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime starTime) {
        this.starTime = starTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (starTime == null) {
            return null;
        } else {
            return starTime.plus(Duration.ofMinutes(getDuration()));
        }
    }

    public LocalDateTime getStartTime() {
        return starTime;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (getDuration() != task.getDuration()) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(task.getDescription()) : task.getDescription() != null)
            return false;
        if (getStatus() != task.getStatus()) return false;
        if (starTime != null ? !starTime.equals(task.starTime) : task.starTime != null) return false;
        return getEndTime() != null ? getEndTime().equals(task.getEndTime()) : task.getEndTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getId();
        result = 31 * result + getStatus().hashCode();
        result = 31 * result + (int) (getDuration() ^ (getDuration() >>> 32));
        result = 31 * result + starTime.hashCode();
        result = 31 * result + getEndTime().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return id + "," + TaskType.TASK + "," + name + "," + status + "," + description+ ","+duration+ ","+starTime+ ","
                +endTime+"\n";

    }

    @Override
    public int compareTo(Task o) {
        if (this.getStartTime() != null && o.getStartTime() != null) {
            if (this.getStartTime().isBefore(o.getStartTime())) {
                return -1;
            } else if (this.getStartTime().isEqual(o.getStartTime())) {
                return 0;
            } else {
                return 1;
            }
        } else if (this.getStartTime() != null && o.getStartTime() == null) {
            return -1;
        } else if (this.getStartTime() == null && o.getStartTime() != null) {
            return 1;
        } else if (this.getStartTime() == null && o.getStartTime() == null) {
            if (getDuration() - o.getDuration() > 0) {
                return 1;
            } else if (getDuration() - o.getDuration() < 0) {
                return -1;
            }
        }
        return 0;
    }
}
