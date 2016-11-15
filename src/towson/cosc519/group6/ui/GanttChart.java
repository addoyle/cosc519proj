package towson.cosc519.group6.ui;

import java.util.Iterator;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Created by bjoynes on 11/9/2016.
 */

//http://stackoverflow.com/questions/27975898/gantt-chart-from-scratch


public class GanttChart extends XYChart<Number, String> {
    private double blockHeight = 5;


    /**
     * Implements extra data
     */
    public static class ganttData {

        public long length;
        public String styleClass;


        public ganttData(long lengthMs, String styleClass) {
            super();
            this.length = lengthMs;
            this.styleClass = styleClass;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public void setStyleClass(String styleClass) {
            this.styleClass = styleClass;
        }
    }


    /**
     * Most methods implemented from the Bubble Chart
     */

    public GanttChart(@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<String> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<Number, String>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<Number> xAxis, @NamedArg("yAxis") Axis<String> yAxis, @NamedArg("data") ObservableList<Series<Number, String>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X should be Number and Y should be Category");
        }
        setData(data);
    }

    @Override
    protected void dataItemAdded(Series<Number, String> series, int itemIndex, Data<Number, String> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);

    }

    private Node createContainer(Series<Number, String> series, int i, Data<Number, String> item, int itemIndex) {
        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add( getStyleClass( item.getExtraValue()));

        return container;
    }

    private static String getStyleClass( Object obj) {
        return ((ganttData) obj).getStyleClass();
    }

    private static double getLength( Object obj) {
        return ((ganttData) obj).getLength();
    }

    @Override
    protected void dataItemRemoved(Data<Number, String> item, Series<Number, String> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override
    protected void dataItemChanged(Data<Number, String> item) {

    }

    @Override
    protected void seriesAdded(Series<Number, String> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<Number, String> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override
    protected void seriesRemoved(Series<Number, String> series) {
        for (XYChart.Data<Number, String> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);
    }

    @Override
    protected void layoutPlotChildren() {
        // update bubble positions
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<Number, String> series = getData().get(seriesIndex);
//            for (Data<Number, String> item = series.begin; item != null; item = item.next) {
            Iterator<Data<Number, String>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<Number, String> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle rectangle;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            rectangle = new Rectangle(getLength(item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            rectangle = (Rectangle) region.getShape();
                        } else {
                            return;
                        }
                        rectangle.setWidth(getLength(item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        rectangle.setHeight(getBlockHeight() * ((getYAxis() instanceof CategoryAxis) ? Math.abs(((CategoryAxis)getYAxis()).getCategories().size()) : 1));

                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(rectangle);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);
                        // position the bubble
                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }


    public double getBlockHeight(){
        return blockHeight;
    }

    public void setBlockHeight(double blockHeight){
        this.blockHeight = blockHeight;
    }
}
