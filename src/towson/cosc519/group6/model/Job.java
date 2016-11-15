package towson.cosc519.group6.model;

/**
 * Represents a single process
 */
public class Job {
    private final int burst;
    private final int start;
    private final String label;

    public Job(String label, int burst, int start) {
        this.burst = burst;
        this.start = start;
        this.label = label;
    }

    public Job(final Job job) {
        this(job.label, job.burst, job.start);
    }

    public int getBurst() {
        return burst;
    }

    public int getStart() {
        return start;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + " (" + start + ", " + burst + ")";
    }
}
