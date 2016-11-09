package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.Job;

/**
 * Shortest job first, prioritizes the current shortest job
 */
public class ShortestJobFirst extends Scheduler {
    @Override
    protected Job getNextJob() {
        Job shortestJob = readyQueue.get(0);
        for (Job job : readyQueue) {
            if (job.getBurst() < shortestJob.getBurst()) {
                shortestJob = job;
            }
        }

        return shortestJob;
    }

    @Override
    public String getShortLabel() {
        return "SJF";
    }

    @Override
    public String getLabel() {
        return "Shortest Job First";
    }


}
