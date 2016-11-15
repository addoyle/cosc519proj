package towson.cosc519.group6.model;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Represents the output of jobs that were ran. Different schedulers will presumably have different outcomes.
 */
public class SchedulerOutput {
    private final List<RunnableJob> jobs;
    private final int totalClock;
    private final int totalWaitTime;

    public SchedulerOutput() {
        jobs = emptyList();
        totalClock = 0;
        totalWaitTime = 0;
    }

    public SchedulerOutput(List<RunnableJob> jobs, int totalClock, int totalWaitTime) {
        this.jobs = jobs;
        this.totalClock = totalClock;
        this.totalWaitTime = totalWaitTime;
    }

    public List<RunnableJob> getJobs() {
        return jobs;
    }

    public int getTotalClock() {
        return totalClock;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public float getAverageWaitTime() {
        return (float) getTotalWaitTime() / (float) jobs.size();
    }
}
