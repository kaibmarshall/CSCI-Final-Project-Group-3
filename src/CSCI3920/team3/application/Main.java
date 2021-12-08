package CSCI3920.team3.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("application.fxml"));
        primaryStage.setTitle("University Application");
        primaryStage.setScene(new Scene(root, 810, 241)); //1080x720 for main
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });




    }


    public static void main(String[] args) {
        launch(args);
    }
}
