package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import towson.cosc519.group6.model.JobStatus;
import towson.cosc519.group6.model.RunnableJob;
import towson.cosc519.group6.model.SchedulerOutput;
import towson.cosc519.group6.schedulers.Scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.BorderPane.setAlignment;

/**
 * Static class with some common utilities
 */
public class Utils {
    // Static only class
    private Utils() {}

    public static void updateChart(GanttChart chart, SchedulerOutput data) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        CategoryAxis yAxis = (CategoryAxis) chart.getYAxis();

        // Update axes
        xAxis.setUpperBound(data.getTotalClock());
        yAxis.setCategories(FXCollections.observableList(data.getJobs().stream().map(RunnableJob::getLabel).collect(Collectors.toList())));

        for (RunnableJob job : data.getJobs()) {
            GanttChart.Series<Number, String> series = new GanttChart.Series<Number, String>();
            int i = 0;
            for (JobStatus status : job.getStatusList()) {
                if(status == JobStatus.RUNNING){
                    series.getData().add(new GanttChart.Data<Number, String>(i, job.getLabel(), new GanttChart.ExtraData(1, "status-blue")));
                    i++;
                }else{
                    i++;
                }

            }
            chart.getData().add(series);
        }

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
     * Finds the first descendant which matches a specific class
     *
     * @param ancestor          Parent element
     * @param descendantType    Descendant type to find
     * @return The first node found that matches the descendant type
     */
    public static <N extends Node> N findFirst(Parent ancestor, Class<N> descendantType) {
       return findFirst(ancestor, ancestor, descendantType);
    }

    /**
     * The recursive piece of {@link Utils#findFirst(Parent, Class)}
     *
     * @param ancestor          Parent element
     * @param cur               Current element
     * @param descendantType    Descendant type to find
     * @param <N>               Descendant type to find
     * @return The first node found that matches the descendant type
     */
    @SuppressWarnings("unchecked")
    private static <N extends Node> N findFirst(Parent ancestor, Node cur, Class<N> descendantType) {
        // Only return if this node matches the descendant type and is not the original ancestor node
        if (ancestor != cur && descendantType.isAssignableFrom(cur.getClass())) {
            return (N) cur;
        }

        // Loop through children
        if (Parent.class.isAssignableFrom(cur.getClass())) {
            for (Node child : ((Parent) cur).getChildrenUnmodifiable()) {
                Node node = findFirst(ancestor, child, descendantType);

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

    /**
     * Create a new tab
     *
     * @param scheduler    Scheduler for that particular tab
     * @return New tab
     */
    public static Tab createTab(Scheduler scheduler) {
        AnchorPane anchor = new AnchorPane();
        BorderPane tabContent = new BorderPane();

        Label title = new Label(scheduler.getLabel());
        title.setFont(new Font(24.0));
        setAlignment(title, Pos.CENTER);
        tabContent.setTop(title);

        tabContent.setCenter(initializeChart());

        anchor.getChildren().add(tabContent);

        setRightAnchor(tabContent, 0.0);
        setBottomAnchor(tabContent, 0.0);
        setLeftAnchor(tabContent, 0.0);
        setTopAnchor(tabContent, 0.0);

        return new Tab(scheduler.getShortLabel(), anchor);
    }

    public static Node initializeChart(){
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        GanttChart chart = new GanttChart(xAxis, yAxis);

        xAxis.setLabel("Clock");
        xAxis.setTickLabelFill(Color.BLACK);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(10);
        xAxis.setAutoRanging(false);

        yAxis.setLabel("Process");
        yAxis.setTickLabelFill(Color.BLACK);
        yAxis.setAutoRanging(false);

        chart.setBlockHeight(50);
        return chart;
    }
}
