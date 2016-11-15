package towson.cosc519.group6.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper class of a job that is running or has ran
 */
public class RunnableJob {
    private int remainingBurst;
    private int wait;
    private final List<JobStatus> statusList;
    private final Job job;

    public RunnableJob(Job job) {
        this.job = job;
        remainingBurst = job.getBurst();
        wait = 0;
        statusList = new LinkedList<>();
    }

    public int doWait() {
        statusList.add(JobStatus.WAITING);
        return ++wait;
    }

    public int doBurst() {
        statusList.add(JobStatus.RUNNING);
        return --remainingBurst;
    }

    public boolean isDone() {
        return remainingBurst <= 0;
    }

    public void addStatus(JobStatus status) {
        statusList.add(status);
    }

    public int getRemainingBurst() {
        return remainingBurst;
    }

    public void setRemainingBurst(int remainingBurst) {
        this.remainingBurst = remainingBurst;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    public int getStart() {
        return job.getStart();
    }

    public int getBurst() {
        return job.getBurst();
    }

    public String getLabel() {
        return job.getLabel();
    }

    public Job getJob() {
        return job;
    }

    public List<JobStatus> getStatusList() {
        return statusList;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < job.getStart(); i++) {
            out += ' ';
        }
        for (JobStatus status : statusList) {
            if (status == JobStatus.WAITING) {
                out += '-';
            } else if (status == JobStatus.RUNNING) {
                out += 'o';
            }
        }

        return super.toString()  + "\t" + out;
    }
}
