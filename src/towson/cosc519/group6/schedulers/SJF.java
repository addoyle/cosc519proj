package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.Job;

/**
 * First come first serve scheduler. Schedules the first job in the queue as the current job.
 */
public class SJF extends Scheduler {
    @Override
    protected Job getNextJob() {
        return readyQueue.get(0);
    }



}
