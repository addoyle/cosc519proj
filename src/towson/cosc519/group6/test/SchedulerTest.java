package towson.cosc519.group6.test;

import towson.cosc519.group6.Job;
import towson.cosc519.group6.schedulers.FirstComeFirstServe;
import towson.cosc519.group6.schedulers.RoundRobin;
import towson.cosc519.group6.schedulers.Scheduler;
import towson.cosc519.group6.schedulers.ShortestJobFirst;

public class SchedulerTest {
    public static void main(String[] args) {
//        Scheduler scheduler = new FirstComeFirstServe();
//        Scheduler scheduler = new ShortestJobFirst();
        Scheduler scheduler = new RoundRobin();

        scheduler.addJob(new Job("P1", 5, 2));
        scheduler.addJob(new Job("P2", 10, 0));
        scheduler.addJob(new Job("P3", 2, 3));
        scheduler.addJob(new Job("P4", 3, 2));

        scheduler.runJobs();

        for (Job job: scheduler.getJobs()) {
            System.out.println(job);
        }

        System.out.println("Average wait time: " + scheduler.getAverageWaitTime());
    }
}
