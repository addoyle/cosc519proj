package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import towson.cosc519.group6.Job;
import towson.cosc519.group6.schedulers.Scheduler;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.BorderPane.setAlignment;

/**
 * Handles all UI interactions
 */
public class Controller implements Initializable {
    @FXML private Scene scene;
    @FXML private Spinner<Integer> burstField;
    @FXML private Spinner<Integer> startTimeField;
    @FXML private TableColumn<Job, String> processNumCol;
    @FXML private TableColumn<Job, Integer> burstTimeCol;
    @FXML private TableColumn<Job, Integer> startTimeCol;
    @FXML private TableView<Job> procsTable;;
    @FXML private TabPane schedTabs;
    private final Map<Tab, Scheduler> tabSchedulerMap = new HashMap<>();
    private final ObservableList<Job> jobs = FXCollections.observableArrayList();

    /**
     * Initialize the UI
     *
     * @param location     Ignored, not used
     * @param resources    Ignored, not used
     */
    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        processNumCol.setCellValueFactory(new PropertyValueFactory<>("label"));
        burstTimeCol.setCellValueFactory(new PropertyValueFactory<>("totalBurst"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));

        burstField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        startTimeField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));

        burstField.getEditor().setOnKeyPressed(this::spinnerKeyPressed);
        startTimeField.getEditor().setOnKeyPressed(this::spinnerKeyPressed);

        procsTable.getSelectionModel().setSelectionMode(MULTIPLE);

        // Load tabs
        for (Class<? extends Scheduler> sClass : Scheduler.SCHEDULERS) {
            try {
                Scheduler sched = sClass.newInstance();
                Tab tab = createTab(sched);
                tabSchedulerMap.put(tab, sched);
                schedTabs.getTabs().add(tab);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a new tab
     *
     * @param scheduler    Scheduler for that particular tab
     * @return New tab
     */
    private Tab createTab(Scheduler scheduler) {
        AnchorPane anchor = new AnchorPane();
        BorderPane tabContent = new BorderPane();

        Label title = new Label(scheduler.getLabel());
        title.setFont(new Font(24.0));
        setAlignment(title, Pos.CENTER);
        tabContent.setTop(title);

        anchor.getChildren().add(tabContent);

        setRightAnchor(tabContent, 0.0);
        setBottomAnchor(tabContent, 0.0);
        setLeftAnchor(tabContent, 0.0);
        setTopAnchor(tabContent, 0.0);

        return new Tab(scheduler.getShortLabel(), anchor);
    }


    /*
     * Copied and pasted from Spinner
     */
    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    /**
     * Key listener, used to delete jobs when DELETE/BACKSPACE is pressed
     *
     * @param e
     */
    @FXML public void keyListener(KeyEvent e) {
        // Delete selected row
        if ((e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) && procsTable.isFocused()) {
            ObservableList<Job> jobsToDelete = procsTable.getSelectionModel().getSelectedItems();
            jobs.removeAll(jobsToDelete);
        }
    }

    /**
     * Handles when enter is pressed for both spinners
     *
     * @param e
     */
    @FXML public void spinnerKeyPressed(KeyEvent e) {
        // Sync the text with the actual value
        commitEditorText(burstField);
        commitEditorText(startTimeField);

        // Submit process
        if (e.getCode() == KeyCode.ENTER) {
            addtoQueueClick(null);
        }
    }

    /**
     * Add job to the queue button action
     *
     * @param e
     */
    @FXML public void addtoQueueClick(ActionEvent e) {
        // Parse input
        int burstTime = burstField.getValue();
        int startTime = startTimeField.getValue();
        String processName = "P" + Integer.toString(jobs.size());

        // Add the job
        Job job = new Job(processName, burstTime, startTime);
        jobs.add(job);

        // Update UI
        procsTable.setItems(jobs);
    }

    /**
     * Runs the CPU scheduler, and generates a graphic of the results
     */
    @FXML public void runProcesses(){
        // TODO: implement this
    }
}
