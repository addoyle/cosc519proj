package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.model.RunnableJob;

import java.util.List;

/**
 * Priority scheduler. Prioritizes jobs based on priority.
 */
public class Priority extends Scheduler {
    @Override
    protected RunnableJob getNextJob(List<? extends RunnableJob> readyQueue) {
        RunnableJob highestPriority = null;
        for (RunnableJob job : readyQueue) {
            if (highestPriority == null || job.getPriority() > highestPriority.getPriority()) {
                highestPriority = job;
            }
        }

        return highestPriority;
    }

    @Override
    public String getShortLabel() {
        return "Pri";
    }

    @Override
    public String getLabel() {
        return "Priority";
    }
}
