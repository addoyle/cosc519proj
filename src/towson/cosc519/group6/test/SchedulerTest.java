package towson.cosc519.group6.test;

import towson.cosc519.group6.model.Job;
import towson.cosc519.group6.model.RunnableJob;
import towson.cosc519.group6.model.SchedulerOutput;
import towson.cosc519.group6.schedulers.RoundRobin;
import towson.cosc519.group6.schedulers.Scheduler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SchedulerTest {
    public static void main(String[] args) {
//        Scheduler scheduler = new FirstComeFirstServe();
//        Scheduler scheduler = new ShortestJobFirst();
        Scheduler scheduler = new RoundRobin();

        List<Job> jobs = Arrays.asList(
                new Job("P1", 5, 2, 3),
                new Job("P2", 10, 0, 2),
                new Job("P3", 2, 3, 4),
                new Job("P4", 3, 2, 1)
        );

        SchedulerOutput output = scheduler.runJobs(jobs);

        for (RunnableJob job: output.getJobs()) {
            System.out.println(job);
        }

        System.out.println("Average wait time: " + output.getAverageWaitTime());
    }
}
