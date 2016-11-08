package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.Job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Base class of schedulers. A schedulers takes a list of processes and determines which process should be run
 * based on its own set of rules.
 */
public abstract class Scheduler {
    public final static Set<Class<? extends Scheduler>> SCHEDULERS = getSchedulers();

    // List of jobs waiting to be executed
    protected final List<Job> readyQueue;

    // Total list of jobs that have been added
    private final List<Job> jobs;

    // Clock counter
    private int clock = 0;

    private int totalWaitTime = 0;

    /**
     * Constructor
     */
    public Scheduler() {
        readyQueue = new LinkedList<>();
        jobs = new LinkedList<>();
    }

    /**
     * Get the next scheduled job
     *
     * @return Next job to run
     */
    protected abstract Job getNextJob();

    /**
     * Get the abbreviated label, used for tab text
     *
     * @return Abbreviated label
     */
    public abstract String getShortLabel();

    /**
     * Get the text label of the scheduler
     *
     * @return Scheduler label
     */
    public abstract String getLabel();

    /**
     * Add a job to the job list
     *
     * @param job    Job to add
     */
    public void addJob(Job job) {
        jobs.add(job);
    }

    /**
     * Run the jobs!
     */
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
        return (float) getTotalWaitTime() / (float) jobs.size();
    }

    public List<Job> getJobs() {
        return jobs;
    }

    /**
     * Grabs all {@link Scheduler}s and returns them in a {@link Set}
     *
     * @return Set of Schedulers
     */
    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Scheduler>> getSchedulers() {
        Set<Class<? extends Scheduler>> schedulers = new HashSet<>();

        // Get the path to the packages folder
        String pkg = Scheduler.class.getPackage().getName();
        String path = pkg.replace('.', '/');
        URL resource = ClassLoader.getSystemResource(path);
        if (resource == null) {
            throw new RuntimeException("No resource for " + path);
        }

        // Read through the list of files in the package folder
        String[] files = new File(resource.getPath()).list();
        if (files != null) {
            for (String fn : files) {

                // Only look at .class files
                if (fn.endsWith(".class")) {
                    try {
                        // Get the class based on the name
                        Class<?> sClass = Class.forName(pkg + '.' + fn.substring(0, fn.length() - 6));

                        // Only add it if it's a subclass of Scheduler
                        if (sClass != Scheduler.class && Scheduler.class.isAssignableFrom(sClass)) {
                            schedulers.add((Class<? extends Scheduler>) sClass);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return Collections.unmodifiableSet(schedulers);
    }
}
