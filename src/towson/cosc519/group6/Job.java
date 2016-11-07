package towson.cosc519.group6;

import java.util.LinkedList;
import java.util.List;

import static towson.cosc519.group6.JobStatus.RUNNING;
import static towson.cosc519.group6.JobStatus.WAITING;

/**
 * Represents a single process
 */
public class Job {
    private final int totalBurst;
    private final int start;
    private int burst;
    private int wait;
    private final List<JobStatus> statusList;
    private final String label;

    public Job(String label, int totalBurst, int start) {
        this.totalBurst = totalBurst;
        this.start = start;
        burst = totalBurst;
        wait = 0;
        statusList = new LinkedList<>();
        this.label = label;
    }

    public int doWait() {
        statusList.add(WAITING);
        return ++wait;
    }

    public int doBurst() {
        statusList.add(RUNNING);
        return --burst;
    }

    public boolean isDone() {
        return burst <= 0;
    }

    public void addStatus(JobStatus status) {
        statusList.add(status);
    }

    public int getTotalBurst() {
        return totalBurst;
    }

    public int getBurst() {
        return burst;
    }

    public void setBurst(int burst) {
        this.burst = burst;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    public int getStart() {
        return start;
    }

    public List<JobStatus> getStatusList() {
        return statusList;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < start; i++) {
            out += ' ';
        }
        for (JobStatus status : statusList) {
            if (status == WAITING) {
                out += '-';
            } else if (status == RUNNING) {
                out += 'o';
            }
        }

        return label + " (" + start + ", " + totalBurst + ")\n" + out;
    }
}
