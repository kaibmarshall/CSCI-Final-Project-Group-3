package CSCI3920.team3.client;

import CSCI3920.team3.client.TestClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;


public class Controller {

    public TextField txtUsername;
    public TextField txtPassword;
    public Button btnLogin;
    public Button btnExit;
    TestClient client;



    public Controller(){
        client = new TestClient();
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

        try {
            String response = client.sendRequest("login|" + username + "|" + password);
            alert = new Alert(Alert.AlertType.CONFIRMATION, response, ButtonType.OK);
            alert.show();
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
