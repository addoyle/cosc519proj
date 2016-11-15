package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.model.RunnableJob;

import java.util.List;

/**
 * Shortest job first, prioritizes the current shortest job
 */
public class ShortestJobFirst extends Scheduler {
    @Override
    protected RunnableJob getNextJob(List<? extends RunnableJob> readyQueue) {
        RunnableJob shortestJob = readyQueue.get(0);
        for (RunnableJob job : readyQueue) {
            if (job.getRemainingBurst() < shortestJob.getRemainingBurst()) {
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
