package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import towson.cosc519.group6.Main;
import towson.cosc519.group6.model.Job;
import towson.cosc519.group6.model.SchedulerOutput;
import towson.cosc519.group6.schedulers.Scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptyList;
import static javafx.scene.control.SelectionMode.MULTIPLE;
import static towson.cosc519.group6.ui.Utils.createTab;
import static towson.cosc519.group6.ui.Utils.iconify;


/**
 * Handles all UI interactions
 */
public class Controller implements Initializable {
    private static final List<Job> DEMO_JOBS = loadDemoJobs();
    private static final String AVG_WAIT_TIME_MSG = "Average Wait Time: ";
    private static final int MIN_PRIORITY = 1;
    private static final int MAX_PRIORITY = 5;
    private static final int DEFAULT_PRIORITY = 3;

    @FXML private ToolBar toolBar;
    @FXML private Spinner<Integer> burstField;
    @FXML private Spinner<Integer> startTimeField;
    @FXML private Spinner<Integer> priorityField;
    @FXML private TableColumn<Job, String> processNumCol;
    @FXML private TableColumn<Job, Integer> burstTimeCol;
    @FXML private TableColumn<Job, Integer> startTimeCol;
    @FXML private TableColumn<Job, Integer> priorityCol;
    @FXML private TableView<Job> procsTable;
    @FXML private TabPane schedTabs;
    @FXML private Button addBtn;
    @FXML private Button btnDemo;
    private final Map<Tab, Scheduler> tabSchedulerMap = new HashMap<>();
    private final ObservableList<Job> jobs = FXCollections.observableArrayList();
    @FXML private Label waitTime;
    @FXML private Label lblRunning;
    @FXML private Label lblWaiting;
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
        burstTimeCol.setCellValueFactory(new PropertyValueFactory<>("burst"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));

        burstField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        startTimeField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        priorityField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_PRIORITY, MAX_PRIORITY, DEFAULT_PRIORITY));

        burstField.getEditor().setOnKeyPressed(this::spinnerKeyPressed);
        startTimeField.getEditor().setOnKeyPressed(this::spinnerKeyPressed);

        procsTable.getSelectionModel().setSelectionMode(MULTIPLE);
        procsTable.setItems(jobs);

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

        // Load FontAwesome
        Font.loadFont(Main.class.getResource("fontawesome.ttf").toExternalForm(), 12);

        schedTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            graphOnTab(newTab);
        });

        // Add icons to buttons
        iconify(addBtn, FontAwesome.PLUS);
        iconify(btnDemo, FontAwesome.TRUCK);
        iconify(lblRunning, FontAwesome.SQUARE, "label-running");
        iconify(lblWaiting, FontAwesome.SQUARE, "label-waiting");
    }

    /**
     * Runs through all the spinners in the toolbar and commits the current values of each to the spinner
     */
    @SuppressWarnings("unchecked")
    private void commitSpinners() {
        List<Spinner<Integer>> spinners = new LinkedList<>();

        for (Node node : toolBar.getChildrenUnmodifiable()) {
            if (Spinner.class.isAssignableFrom(node.getClass())) {
                spinners.add((Spinner<Integer>) node);
            }
        }

        for (Spinner<Integer> spinner : spinners) {
            if (!spinner.isEditable()) return;
            String text = spinner.getEditor().getText();
            SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
            if (valueFactory != null) {
                StringConverter<Integer> converter = valueFactory.getConverter();
                if (converter != null) {
                    Integer value = converter.fromString(text);

                    if (value != null) {
                        valueFactory.setValue(value);
                    }
                }
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
        commitSpinners();

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
        // Sync the text with the actual value
        commitSpinners();

        // Parse input
        int burstTime = burstField.getValue();
        int startTime = startTimeField.getValue();
        int priority = priorityField.getValue();
        String processName = "P" + Integer.toString(jobs.size());

        // Add the job which will automatically update the UI
        Job job = new Job(processName, burstTime, startTime, priority);
        jobs.add(job);

        // Update graphs with new process
        graphOnTab(getCurrentTab());
    }

    /**
     * Gets the currently selected tab
     *
     * @return Selected tab
     */
    public Tab getCurrentTab(){
        for (Tab tab : schedTabs.getTabs()) {
            if (tab.isSelected()) {
                return tab;
            }
        }

        return null;
    }

    private void graphOnTab(Tab tab) {
        // Get the graph
        GanttChart chart = Utils.findFirstSuccessor((Parent) tab.getContent(), GanttChart.class);

        // Get the scheduler for that tab
        Scheduler scheduler = tabSchedulerMap.get(tab);
        if (scheduler == null) {
            throw new UnsupportedOperationException("No scheduler for this tab");
        }

        // Run the scheduler
        SchedulerOutput output = scheduler.runJobs(jobs);

        // Update the chart
        Utils.updateChart(chart, output);

        //Display Average wait time
        waitTime.setText(AVG_WAIT_TIME_MSG + output.getAverageWaitTime());
    }

    /**
     * Load demo button click
     *
     * @param e Event object
     */
    @FXML public void loadDemoClick(ActionEvent e) {
        jobs.clear();
        jobs.addAll(DEMO_JOBS);
        graphOnTab(getCurrentTab());
    }

    /**
     * Load a set of demo jobs, useful for showing an interesting sample
     * without having to enter all the processes in over and over again.
     *
     * @return List of demo jobs
     */
    private static List<Job> loadDemoJobs() {
        List<Job> jobs = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("demo.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] parts = line.split(",");
                    jobs.add(new Job(parts[0], parseInt(parts[1]), parseInt(parts[2]), parseInt(parts[3])));
                }
            }

            return jobs;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emptyList();
    }
}
