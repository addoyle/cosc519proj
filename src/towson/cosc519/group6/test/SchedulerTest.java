package towson.cosc519.group6.test;

import towson.cosc519.group6.Job;
import towson.cosc519.group6.schedulers.FirstComeFirstServe;
import towson.cosc519.group6.schedulers.Scheduler;

public class SchedulerTest {
    public static void main(String[] args) {
        Scheduler scheduler = new FirstComeFirstServe();

        scheduler.addJob(new Job("P1", 5, 0));
        scheduler.addJob(new Job("P2", 10, 0));
        scheduler.addJob(new Job("P3", 2, 2));
        scheduler.addJob(new Job("P4", 3, 6));

        scheduler.runJobs();

        for (Job job: scheduler.getJobs()) {
            System.out.println(job);
        }

        System.out.println("Average wait time: " + scheduler.getAverageWaitTime());
    }
}
