package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.MULTIPLE;
import static towson.cosc519.group6.ui.Utils.createTab;
import static towson.cosc519.group6.ui.Utils.iconify;


/**
 * Handles all UI interactions
 */
public class Controller implements Initializable {
    @FXML private Spinner<Integer> burstField;
    @FXML private Spinner<Integer> startTimeField;
    @FXML private TableColumn<Job, String> processNumCol;
    @FXML private TableColumn<Job, Integer> burstTimeCol;
    @FXML private TableColumn<Job, Integer> startTimeCol;
    @FXML private TableView<Job> procsTable;
    @FXML private TabPane schedTabs;
    @FXML private Button addBtn;
    @FXML private Button runBtn;
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
        burstTimeCol.setCellValueFactory(new PropertyValueFactory<>("burst"));
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

        // Load FontAwesome
        Font.loadFont(Main.class.getResource("fontawesome.ttf").toExternalForm(), 12);

        // Add icons to buttons
        iconify(addBtn, FontAwesome.PLUS);
        iconify(runBtn, FontAwesome.ROCKET);
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

                if (value != null) {
                    valueFactory.setValue(value);
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
        // Sync the text with the actual value
        commitEditorText(burstField);
        commitEditorText(startTimeField);

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
        // Get current tab
        Tab curTab = null;
        for (Tab tab : schedTabs.getTabs()) {
            if (tab.isSelected()) {
                curTab = tab;
                break;
            }
        }

        // Get the graph
        GanttChart chart = Utils.findFirst((Parent) curTab.getContent(), GanttChart.class);

        // Get the scheduler for that tab
        Scheduler scheduler = tabSchedulerMap.get(curTab);
        if (scheduler == null) {
            throw new UnsupportedOperationException("No scheduler for this tab");
        }

        // Run the scheduler
        SchedulerOutput output = scheduler.runJobs(jobs);

        // Update the chart
        Utils.updateChart(chart, output);
    }

}
