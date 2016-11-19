package towson.cosc519.group6.schedulers;

import towson.cosc519.group6.model.Job;
import towson.cosc519.group6.model.RunnableJob;
import towson.cosc519.group6.model.SchedulerOutput;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Base class of schedulers. A schedulers takes a list of processes and determines which process should be run
 * based on its own set of rules.
 */
public abstract class Scheduler {
    public final static Set<Class<? extends Scheduler>> SCHEDULERS = getSchedulers();

    /**
     * All schedulers are singletons, thus protected constructor
     */
    protected Scheduler() {
    }

    /**
     * Get the next scheduled job
     *
     * @return Next job to run
     */
    protected abstract RunnableJob getNextJob(List<? extends RunnableJob> readyQueue);

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
     * Run the jobs!
     */
    public SchedulerOutput runJobs(final List<? extends Job> jobs) {
        // No jobs, nothing to do
        if (jobs.isEmpty()) {
            return new SchedulerOutput();
        }

        int clock = 0;
        int totalWait = 0;

        List<RunnableJob> runnableJobs = new LinkedList<>();
        List<RunnableJob> readyQueue = new ArrayList<>();

        // Populate runnable jobs
        for (Job job : jobs) {
            runnableJobs.add(new RunnableJob(job));
        }

        do {
            // Add in jobs to be started
            for (RunnableJob job : runnableJobs) {
                if (job.getStart() == clock) {
                    addToReadyQueue(readyQueue, job);
                }
            }

            RunnableJob runningJob = null;
            if (!readyQueue.isEmpty()) {
                // Update the currently running job
                runningJob = getNextJob(readyQueue);
                runningJob.doBurst();

                // Remove the job from the ready queue if completed
                if (runningJob.isDone()) {
                    removeFromReadyQueue(readyQueue, runningJob);
                }
            }

            // Update the wait time for all other jobs
            for (RunnableJob job : readyQueue) {
                if (job != runningJob) {
                    job.doWait();
                    totalWait++;
                }
            }

            // Clock cycle complete!
            clock++;
        } while (!readyQueue.isEmpty());

        // Return output
        return new SchedulerOutput(runnableJobs, clock, totalWait);
    }

    protected boolean addToReadyQueue(List<RunnableJob> readyQueue, RunnableJob job) {
        return readyQueue.add(job);
    }

    protected boolean removeFromReadyQueue(List<RunnableJob> readyQueue, RunnableJob job) {
        return readyQueue.remove(job);
    }

    /**
     * Grabs all {@link Scheduler}s and returns them in a {@link Set}
     *
     * @return Set of Schedulers
     */
    @SuppressWarnings("unchecked")
    private static Set<Class<? extends Scheduler>> getSchedulers() {
        Set<Class<? extends Scheduler>> schedulers = new LinkedHashSet<>();

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
