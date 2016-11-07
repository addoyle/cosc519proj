package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.Job;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class of schedulers. A schedulers takes a list of processes and determines which process should be run
 * based on its own set of rules.
 */
public abstract class Scheduler {
    // List of jobs waiting to be executed
    protected final List<Job> readyQueue;

    // Total list of jobs that have been added
    private final List<Job> jobs;

    // Clock counter
    private int clock = 0;

    private int totalWaitTime = 0;

    protected abstract Job getNextJob();

    public Scheduler() {
        readyQueue = new LinkedList<>();
        jobs = new LinkedList<>();
    }

    public void addJob(Job job) {
        jobs.add(job);
    }

    public void runJobs() {
        clock = 0;

        do {
            // Add in jobs to be started
            for (Job job : jobs) {
                if (job.getStart() == clock) {
                    readyQueue.add(job);
                }
            }

            // Update the currently running job
            Job runningJob = getNextJob();
            runningJob.doBurst();

            // Remove the job from the ready queue if completed
            if (runningJob.isDone()) {
                readyQueue.remove(runningJob);
            }

            // Update the wait time for all other jobs
            for (Job job : readyQueue) {
                if (job != runningJob) {
                    job.doWait();
                    totalWaitTime++;
                }
            }

            // Clock cycle complete!
            clock++;
        } while (!readyQueue.isEmpty());
    }

    public List<Job> getReadyQueue() {
        return readyQueue;
    }

    public int getClock() {
        return clock;
    }

    public int getTotalWaitTime() {
        return totalWaitTime;
    }

    public float getAverageWaitTime() {
        return (float) totalWaitTime / (float) jobs.size();
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
