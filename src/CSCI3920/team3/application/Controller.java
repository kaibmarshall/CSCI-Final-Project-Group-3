package CSCI3920.team3.application;

import CSCI3920.team3.client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import CSCI3920.team3.objects.Item;

import CSCI3920.team3.objects.User;

import javax.swing.*;
import java.awt.event.MouseEvent;
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
    public TableView<Item> adminInventoryList;
    public TableColumn adminItemName;
    public TableColumn adminUserRentingItem;
    public TableColumn adminPricePerDay;
    public Tab tabListInventory;
    public TableView<Item> userRentedItems;
    public TableColumn UserRentedItem;
    public TableColumn UserPricePerDay;
    public Tab tabUserInfo;
    public Button returnItem;
    public Button checkOutItem;
    public Button btnAdminRemoveItem;
    public TextField txtAddItemName;
    public TextField txtAddItemPrice;
    public Button btnAddItem;
    public Tab tabUsers;
    public TableView<User> userList;
    public TableColumn userUsername;
    public TableColumn userPassword;
    public TableColumn userIsAdmin;
    public Button RemoveUser;
    public TextField txtAddUserUsername;
    public TextField txtAddUserPassword;
    public Button btnAddUser;

    public Item itemToReturn;
    public Item itemToRent;
    public Item itemToRemove;
    public User userToRemove;

    Client client;

    public Controller(){
        client = new Client();
        client.connect();
    }

    public void initialize() {



    }

    public void attemptLogin(ActionEvent actionEvent) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        Alert alert;
        boolean userIsAdmin = false;

        try {
            String response = client.sendRequest("L|" + username + "|" + password);
            String[] responses = response.split("\\|");


            if (responses[2].equals("success")) {

                if (responses[1].equals("True"))
                    userIsAdmin = true;
                else
                    userIsAdmin = false;

                this.closeWindow(actionEvent);

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
                    Stage userStage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("application-user.fxml"));
                    userStage.setTitle("University Application");
                    userStage.setScene(new Scene(root, 1080, 720)); //1080x720 for main
                    userStage.show();

                    userStage.setOnCloseRequest(e -> {
                        Platform.exit();
                        System.exit(0);
                    });

                }
            } else if(responses[2].equals("fail")) {
                alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
                alert.show();
                cleanLoginPage();
            }
        }
        catch (IOException ioe) {
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }

    }

    public void cleanLoginPage() {
        txtUsername.setText("");
        txtPassword.setText("");
    }

    public void listInventoryUpdate() {
        Alert alert;

        if (this.tabListInventory.isSelected()) {
            try {
                String response = client.sendRequest("I|");
                if (response.equals("none")){
                    ArrayList<Item> items = new ArrayList<>();
                    items.add(new Item("No items in inventory", ""));

                    ItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
                    PricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

                    inventoryList.setItems(FXCollections.observableArrayList(items));
                } else {
                    String[] responses = response.split("\\|");

                    ItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
                    PricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

                    ArrayList<Item> items = new ArrayList<>();

                    for (int i = 0; i < responses.length; i += 2) {
                        items.add(new Item(responses[i], responses[i + 1]));
                    }

                    inventoryList.setItems(FXCollections.observableArrayList(items));
                    inventoryList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                    inventoryList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                        //Check whether item is selected and set value of selected item to Label
                        if (inventoryList.getSelectionModel().getSelectedItem() != null) {
                            Item item = newValue;
                            this.itemToRent = item;
                        }
                    });
                }
            } catch (Exception e) {
                alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.show();
            }
        }
    }

    public void adminListInventoryUpdate() {
        Alert alert;

        if (this.tabListInventory.isSelected()) {
            try {
                String response = client.sendRequest("adminI|");
                if (response.equals("none")){
                    ArrayList<Item> items = new ArrayList<>();
                    items.add(new Item("No items in inventory","", ""));

                    adminItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
                    adminUserRentingItem.setCellValueFactory(new PropertyValueFactory<>("userRentedTo"));
                    adminPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

                    adminInventoryList.setItems(FXCollections.observableArrayList(items));
                } else {
                    String[] responses = response.split("\\|");

                    adminItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
                    adminUserRentingItem.setCellValueFactory(new PropertyValueFactory<>("userRentedTo"));
                    adminPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

                    ArrayList<Item> items = new ArrayList<>();

                    for (int i = 0; i < responses.length; i += 3) {
                        items.add(new Item(responses[i], responses[i + 1], responses[i + 2]));
                    }

                    adminInventoryList.setItems(FXCollections.observableArrayList(items));
                    adminInventoryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                    adminInventoryList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                        if (adminInventoryList.getSelectionModel().getSelectedItem() != null) {
                            Item item = newValue;
                            this.itemToRemove = item;
                        }
                    });
                }
            } catch (Exception e) {
                alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                e.printStackTrace();
                alert.show();
            }
        }
    }

    public void userRentedItemsUpdate() {
        Alert alert;

        if (this.tabUserInfo.isSelected()) {
            try {
                String response = client.sendRequest("GRI|");
                if (response.equals("none")){
                    ArrayList<Item> items = new ArrayList<>();
                    items.add(new Item("No items for user", ""));
                    userRentedItems.setItems(FXCollections.observableArrayList(items));
                }
                else {
                    String[] responses = response.split("\\|");

                    UserRentedItem.setCellValueFactory(new PropertyValueFactory<>("name"));
                    UserPricePerDay.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));


                    ArrayList<Item> items = new ArrayList<>();

                    for (int i = 0; i < responses.length; i += 2) {
                        items.add(new Item(responses[i], responses[i + 1]));
                    }


                    userRentedItems.setItems(FXCollections.observableArrayList(items));
                    userRentedItems.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                    userRentedItems.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                        //Check whether item is selected and set value of selected item to Label
                        if (userRentedItems.getSelectionModel().getSelectedItem() != null) {
                            Item item = newValue;
                            this.itemToReturn = item;
                        }
                    });
                }


            }
            catch (IOException ioe){
                alert = new Alert(Alert.AlertType.ERROR, ioe.getMessage(), ButtonType.OK);
                alert.show();
            }
        }
    }

    public void returnSelectedItem(ActionEvent actionEvent){
        Alert alert;
        try {
            if (this.itemToReturn == null) {
                alert = new Alert(Alert.AlertType.ERROR, "No item selected.", ButtonType.OK);
                alert.show();
                userRentedItemsUpdate();
            }
            else {
                String response = client.sendRequest("R|" + this.itemToReturn.getName() + "|");
                String[] responses = response.split("\\|");
                alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
                alert.show();
                userRentedItemsUpdate();
            }
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    public void checkOutSelectedItem(ActionEvent actionEvent){
        Alert alert;
        try {
            if (this.itemToRent == null) {
                alert = new Alert(Alert.AlertType.ERROR, "No item selected.", ButtonType.OK);
                alert.show();
                userRentedItemsUpdate();
            }
            else {
                String response = client.sendRequest("C|" + this.itemToRent.getName() + "|");
                String[] responses = response.split("\\|");
                alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
                alert.show();
                listInventoryUpdate();
            }
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }

    }

    public void adminRemoveItem(ActionEvent actionEvent) {
        Alert alert;
        try {
            if (this.itemToRemove == null) {
                alert = new Alert(Alert.AlertType.ERROR, "No item selected.", ButtonType.OK);
                alert.show();
                userRentedItemsUpdate();
            }
            else {
                String response = client.sendRequest("adminR|" + this.itemToRemove.getName() + "|");
                String[] responses = response.split("\\|");
                alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
                alert.show();
                adminListInventoryUpdate();
            }
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    public void adminAddItem(ActionEvent actionEvent) {
        String itemName = txtAddItemName.getText();
        String itemPrice = txtAddItemPrice.getText();
        Alert alert;

        try {
            String response = client.sendRequest("adminA|" + itemName + "|" + itemPrice + "|");
            String[] responses = response.split("\\|");
            alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
            alert.show();
            adminListInventoryUpdate();
            cleanAddItem();
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    public void cleanAddItem() {
        this.txtAddItemPrice.setText("$");
        this.txtAddItemName.setText("");
    }

    public void adminTabUsersUpdate() {
        Alert alert;

        if (this.tabUsers.isSelected()) {
            try {
                String response = client.sendRequest("adminUL|");
                if (response.equals("none")){
                    ArrayList<User> users = new ArrayList<>();
                    users.add(new User("No items in inventory","", ""));

                    userUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
                    userPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
                    userIsAdmin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

                    userList.setItems(FXCollections.observableArrayList(users));
                } else {
                    String[] responses = response.split("\\|");

                    userUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
                    userPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
                    userIsAdmin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

                    ArrayList<User> users = new ArrayList<>();

                    for (int i = 0; i < responses.length; i += 3) {
                        users.add(new User(responses[i], responses[i + 1], responses[i + 2]));
                    }

                    userList.setItems(FXCollections.observableArrayList(users));
                    userList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

                    userList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                        if (userList.getSelectionModel().getSelectedItem() != null) {
                            User user = newValue;
                            this.userToRemove = user;
                        }
                    });
                }
            } catch (Exception e) {
                alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                e.printStackTrace();
                alert.show();
            }
        }
    }

    public void adminRemoveUser(ActionEvent actionEvent) {
        Alert alert;
        try {
            if (this.userToRemove == null) {
                alert = new Alert(Alert.AlertType.ERROR, "No user selected.", ButtonType.OK);
                alert.show();
                userRentedItemsUpdate();
            }
            else {
                String response = client.sendRequest("adminRU|" + this.userToRemove.getUsername() + "|");
                String[] responses = response.split("\\|");
                alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
                alert.show();
                adminTabUsersUpdate();
            }
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    public void adminAddUser(ActionEvent actionEvent) {
        String userUsername = txtAddUserUsername.getText();
        String userPassword = txtAddUserPassword.getText();
        Alert alert;

        try {
            String response = client.sendRequest("adminAU|" + userUsername + "|" + userPassword + "|");
            String[] responses = response.split("\\|");
            alert = new Alert(Alert.AlertType.CONFIRMATION, responses[0], ButtonType.OK);
            alert.show();
            adminTabUsersUpdate();
            cleanAddUser();
        }
        catch (IOException ioe){
            alert = new Alert(Alert.AlertType.CONFIRMATION, ioe.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    public void cleanAddUser() {
        this.txtAddUserUsername.setText("");
        this.txtAddUserPassword.setText("");
    }

    public void closeWindow(ActionEvent actionEvent){
        Stage stage = (Stage) this.btnExit.getScene().getWindow();
        stage.close();
    }

    public void exitApplication(ActionEvent actionEvent) {
        try {
            client.sendRequest("T|");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Stage stage = (Stage) this.btnExit.getScene().getWindow();
        stage.close();
    }


}
