package towson.cosc519.group6.model;

/**
 * Possible job statuses
 */
public enum JobStatus {
    WAITING("status-waiting"),
    RUNNING("status-running"),
    NONE(null);

    private final String cssClass;

    JobStatus(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }
}
