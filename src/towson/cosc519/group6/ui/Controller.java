package towson.cosc519.group6.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import towson.cosc519.group6.Job;
import towson.cosc519.group6.schedulers.FirstComeFirstServe;
import towson.cosc519.group6.schedulers.Scheduler;


public class Controller {
      @FXML private TextField burstField;
      @FXML private TextField startTimeField;
      @FXML private ChoiceBox algorithmList;
      @FXML private ListView queueList;

      private static final ObservableList<String> queueItems = FXCollections.observableArrayList();

       @FXML public void addtoQueueClick(ActionEvent actionEvent) {

           System.out.println("Added to Queue!");
           String algorithmChoice = algorithmList.getValue().toString();
           System.out.println(algorithmChoice);
           String burstTime = burstField.getText();
           String startTime = startTimeField.getText();


           queueItems.add("Burst: " + burstTime + " " + "Start: "+ startTime);

           queueList.setItems(queueItems);


       }




       @FXML public void runQueueProcesses(){


    }


}
