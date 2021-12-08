package CSCI3920.team3.application;

import CSCI3920.team3.client.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {


    public TextField txtUsername;
    public TextField txtPassword;
    public Button btnLogin;
    public Button btnExit;
    Client client;



    public Controller(){
        client = new Client();
        client.connect();


    }

    public void initialize() {


//        this.lstEnrolledCourses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
//            @Override
//            public void changed(ObservableValue<? extends Course> observable, Course oldvalue, Course newValue) {
//                lstEnrolledStudents.setItems(FXCollections.observableArrayList(newValue.getEnrolledStudents()));
//            }
//        });

    }

    public void attemptLogin(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        Alert alert;
        boolean userIsAdmin = false;

        try {
            String response = client.sendRequest("L|" + username + "|" + password);
            String[] responses = response.split("\\|");
            alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
            alert.show();


            if (responses[1].equals("True")) {
                userIsAdmin = true;
            } else {
                userIsAdmin = false;
            }

            this.exitApplication(actionEvent);

            if (userIsAdmin) {
                Stage adminStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("application-admin.fxml"));
                adminStage.setTitle("University Application");
                adminStage.setScene(new Scene(root, 1080, 720)); //1080x720 for main
                adminStage.show();

                adminStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });
            } else {
                Stage adminStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("application-user.fxml"));
                adminStage.setTitle("University Application");
                adminStage.setScene(new Scene(root, 1080, 720)); //1080x720 for main
                adminStage.show();

                adminStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });

            }



        }
        catch (IOException ioe) {
            alert = new Alert(Alert.AlertType.CONFIRMATION, "Username or password incorrect", ButtonType.OK);
            alert.show();
        }

    }


    public void listInventoryUpdate() {

    }

    public void exitApplication(ActionEvent actionEvent) {
        Stage stage = (Stage) this.btnExit.getScene().getWindow();
        stage.close();
    }


}
