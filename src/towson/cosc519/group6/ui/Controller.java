package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import towson.cosc519.group6.Job;
import towson.cosc519.group6.schedulers.FirstComeFirstServe;
import towson.cosc519.group6.schedulers.Scheduler;


public class Controller {
    @FXML private TextField burstField;
    @FXML private TextField startTimeField;
    @FXML private TableColumn<Job, String> processNumCol;
    @FXML private TableColumn burstTimeCol;
    @FXML private TableColumn startTimeCol;
    @FXML private ChoiceBox algorithmList;
    @FXML private TableView queueList;
    private Scheduler FCFSScheduler = new FirstComeFirstServe();
    private Job job;
    private final ObservableList<Job> jobs = FXCollections.observableArrayList();

    @FXML public void addtoQueueClick(ActionEvent actionEvent) {

           //Add to UI Queue
           System.out.println("Added to Queue!");
           String algorithmChoice = algorithmList.getValue().toString();
           System.out.println(algorithmChoice); //testing choices
           String burstTime = burstField.getText();
           String startTime = startTimeField.getText();
           String processName = "P" + Integer.toString(jobs.size());

           job = new Job(processName,Integer.parseInt(startTime),Integer.parseInt(burstTime));
           jobs.add(job);
           System.out.println(jobs);
           queueList.setItems(jobs); //Cant get the table to update with the appropriate values. They are showing up blank for every insert.




           //Add to real Queue
        switch(algorithmChoice){
            case "FCFS":
                FCFSScheduler.addJob(job);
                break;
            case "SJF":

                break;
            case "Round Robin":

                break;
            default:
                System.out.println("");

        }


       }
    

       @FXML public void runProcesses(){



       }


}
