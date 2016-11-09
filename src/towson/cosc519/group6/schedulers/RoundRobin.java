package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.Job;

/**
 * Round Robin, no priority, cycles through all jobs and runs one burst per job until completion
 */
public class RoundRobin extends Scheduler {
    private int i = 0;

    @Override
    protected Job getNextJob() {
        Job job = readyQueue.get(i);
        i = (i + 1) % readyQueue.size();
        
        return job;
    }

    @Override
    protected boolean removeFromReadyQueue(Job job) {
        int index = readyQueue.indexOf(job);
        if (index < i) {
            i--;
        }

        readyQueue.remove(job);

        return true;
    }

    @Override
    public String getShortLabel() {
        return "RR";
    }

    @Override
    public String getLabel() {
        return "Round Robin";
    }
}
