package towson.cosc519.group6.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import towson.cosc519.group6.model.JobStatus;
import towson.cosc519.group6.model.RunnableJob;
import towson.cosc519.group6.model.SchedulerOutput;
import towson.cosc519.group6.schedulers.Scheduler;
import towson.cosc519.group6.ui.GanttChart.ExtraData;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.*;
import static javafx.scene.layout.AnchorPane.*;
import static towson.cosc519.group6.model.JobStatus.NONE;

/**
 * Static class with some common utilities
 */
public class Utils {
    // Static only class
    private Utils() {}

    /**
     * Update a chart with a set of data
     *
     * @param chart    Chart to update
     * @param data     Data to update chart with
     */
    public static void updateChart(GanttChart chart, SchedulerOutput data) {
        ObservableList<String> categories = observableList(data.getJobs().stream().map(RunnableJob::getLabel).collect(toList()));
        reverse(categories);

        chart = resetChart(chart, data.getTotalClock(), categories);

        for (RunnableJob job : data.getJobs()) {
            Series<Number, String> series = new Series<Number, String>();

            System.out.println(job.toString());

            ExtraData extraData = null;
            JobStatus curStatus = NONE;
            int start = job.getStart();
            for (JobStatus status : job.getStatusList()) {
                if (status != curStatus){
                    if (extraData != null) {
                        series.getData().add(new Data<>(start, job.getLabel(), extraData));
                        start += (int) extraData.getLength();
                    }
                    extraData = new ExtraData(1, status.getCssClass());
                    curStatus = status;
                } else {
                    extraData.setLength(extraData.getLength() + 1);
                }
            }

            // add the last one
            series.getData().add(new Data<>(start, job.getLabel(), extraData));

            chart.getData().add(series);
        }

        chart.requestLayout();
    }

    /**
     * Add an icon to a button, label, etc.
     *
     * @param node    Element to add an icon to
     * @param icon    Icon to add
     */
    public static void iconify(Labeled node, FontAwesome icon) {
        Label iconLabel = new Label(icon.getIcon());
        iconLabel.getStyleClass().add("i");
        node.setGraphic(iconLabel);
    }

    /**
     * Find the first ancestor which matches a specific class
     *
     * @param successor       Element to find ancestor of
     * @param ancestorType    Ancestor type to find
     * @param <N>             Ancestor type
     * @return The first node which matches the ancestor type
     */
    @SuppressWarnings("unchecked")
    public static <N extends Node> N findFirstAncestor(Node successor, Class<N> ancestorType) {
        // Keep bubbling up the parents until either null or a match is hit
        Node cur = successor;
        while (cur.getParent() != null) {
            if (ancestorType.isAssignableFrom(cur.getClass())) {
                return (N) cur;
            }

            cur = cur.getParent();
        }

        return null;
    }

    /**
     * Finds the first descendant which matches a specific class
     *
     * @param ancestor          Parent element
     * @param descendantType    Descendant type to find
     * @param <N>               Descendent type
     * @return The first node found that matches the descendant type
     * @return
     */
    public static <N extends Node> N findFirstSuccessor(Parent ancestor, Class<N> descendantType) {
       return findFirstSuccessor(ancestor, ancestor, descendantType);
    }

    /**
     * The recursive piece of {@link Utils#findFirstSuccessor(Parent, Class)}
     *
     * @param ancestor          Parent element
     * @param cur               Current element
     * @param descendantType    Descendant type to find
     * @param <N>               Descendant type to find
     * @return The first node found that matches the descendant type
     */
    @SuppressWarnings("unchecked")
    private static <N extends Node> N findFirstSuccessor(Parent ancestor, Node cur, Class<N> descendantType) {
        // Only return if this node matches the descendant type and is not the original ancestor node
        if (ancestor != cur && descendantType.isAssignableFrom(cur.getClass())) {
            return (N) cur;
        }

        // Loop through children
        if (Parent.class.isAssignableFrom(cur.getClass())) {
            for (Node child : ((Parent) cur).getChildrenUnmodifiable()) {
                Node node = findFirstSuccessor(ancestor, child, descendantType);

                // Return the first matching child that matches
                if (node != null) {
                    return (N) node;
                }
            }

            // Returns null if no children match
            return null;
        }

        // Returns null if the current node is not a parent and does not match the descendant type
        return null;
    }

    private static GanttChart resetChart(GanttChart chart, int upperBound, ObservableList<String> categories) {
        return resetChart(chart.getTitle(), findFirstAncestor(chart, AnchorPane.class), upperBound, categories);
    }

    private static GanttChart resetChart(String title, AnchorPane anchor) {
        return resetChart(title, anchor, 10, emptyObservableList());
    }

    private static GanttChart resetChart(String title, AnchorPane anchor, int upperBound, ObservableList<String> categories) {
        anchor.getChildren().clear();

        GanttChart chart = initializeChart(upperBound, categories);
        chart.setTitle(title);

        anchor.getChildren().add(chart);

        setRightAnchor(chart, 0.0);
        setBottomAnchor(chart, 0.0);
        setLeftAnchor(chart, 0.0);
        setTopAnchor(chart, 0.0);

        return chart;
    }

    /**
     * Create a new tab
     *
     * @param scheduler    Scheduler for that particular tab
     * @return New tab
     */
    public static Tab createTab(Scheduler scheduler) {
        AnchorPane anchor = new AnchorPane();
        resetChart(scheduler.getLabel(), anchor);
        return new Tab(scheduler.getShortLabel(), anchor);
    }

    /**
     * Initialize a chart
     * @return
     */
    public static GanttChart initializeChart(int upperBound, ObservableList<String> categories){
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        GanttChart chart = new GanttChart(xAxis, yAxis);

        xAxis.setLabel("Clock");
        xAxis.setTickLabelFill(Color.BLACK);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(upperBound);
        xAxis.setTickUnit(1);
        xAxis.setAutoRanging(false);

        yAxis.setLabel("Process");
        yAxis.setTickLabelFill(Color.BLACK);
        yAxis.setAutoRanging(false);
        yAxis.setCategories(categories);

        chart.setLegendVisible(true);
        chart.setLegendSide(Side.RIGHT);

        chart.setBlockHeight(3);

        return chart;
    }
}
