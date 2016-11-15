package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.model.Job;

/**
 * First come first serve scheduler. Schedules the first job in the queue as the current job.
 */
public class FirstComeFirstServe extends Scheduler {
    @Override
    protected Job getNextJob() {
        return readyQueue.get(0);
    }

    @Override
    public String getShortLabel() {
        return "FCFS";
    }

    @Override
    public String getLabel() {
        return "First Come First Serve";
    }
}
