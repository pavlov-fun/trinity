package fun.pavlov.pojo;

public class Task {

    private Long uid;

    private String body;

    private TaskStatus status;

    public Task() {
    }

    public Task(Long uid, String body) {
        this.status = TaskStatus.NEW;
        this.uid = uid;
        this.body = body;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Task{uid=%s, body=%s, status=%s}", getUid(), getBody(), getStatus());
    }

    public Task corrupt() {
        this.status = TaskStatus.CORRUPTED;
        System.out.println(this.toString());
        return this;
    }

    public Task execute() {
        this.status = TaskStatus.EXECUTED;
        System.out.println(this.toString());
        return this;
    }

    public enum TaskStatus {
        NEW, EXECUTED, CORRUPTED
    }

}
