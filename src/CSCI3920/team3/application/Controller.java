package CSCI3920.team3.application;

import CSCI3920.team3.client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import CSCI3920.team3.objects.Item;
import CSCI3920.team3.objects.User;


import java.io.IOException;
import java.util.ArrayList;

public class Controller {


    public TextField txtUsername;
    public TextField txtPassword;
    public Button btnLogin;
    public Button btnExit;
    public TableView<Item> inventoryList;
    public TableColumn ItemName;
    public TableColumn PricePerDay;
    public Tab tabListInventory;

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
                adminStage.toBack();


                adminStage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });
            } else {
                Stage userStage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("application-user.fxml"));
                userStage.setTitle("University Application");
                userStage.setScene(new Scene(root, 1080, 720)); //1080x720 for main
                userStage.show();
                userStage.toBack();

                userStage.setOnCloseRequest(e -> {
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


    public void listInventoryUpdate() throws InterruptedException {
        Alert alert;

        if (this.tabListInventory.isSelected()) {
            try {
                String response = client.sendRequest("I|");
                String[] responses = response.split("\\|");

                ItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
                PricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

                ArrayList<Item> items = new ArrayList<>();

                for (int i = 0; i < responses.length ; i += 2) {
                    items.add(new Item(responses[i], responses[i+1]));
                }

                inventoryList.setItems(FXCollections.observableArrayList(items));
                inventoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            } catch (Exception e) {
                alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                e.printStackTrace();
                alert.show();
            }
        }
    }

    public void exitApplication(ActionEvent actionEvent) {
        Stage stage = (Stage) this.btnExit.getScene().getWindow();
        stage.close();
    }


}
