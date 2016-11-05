package towson.cosc519.group6;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("CPU Scheduler Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("New height: " + newValue);
        });
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("New width: " + newValue);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
