package com.example.woltbetblogiau.fxControllers;

import com.example.woltbetblogiau.HelloApplication;
import com.example.woltbetblogiau.hibernateControl.GenericHibernate;
import com.example.woltbetblogiau.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class UserForm implements Initializable {

    @FXML
    public RadioButton userRadio;
    @FXML
    public RadioButton restaurantRadio;
    @FXML
    public RadioButton clientRadio;
    @FXML
    public RadioButton driverRadio;
    @FXML
    public ToggleGroup Select;
    @FXML
    public TextField addressField;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField phoneField;
    public Button updateButton;
    public ComboBox<VehicleType> comboTest;
    public Button backToLoginFormButton;

    private EntityManagerFactory entityManagerFactory;
    private GenericHibernate genericHibernate;
    private User userForUpdate;
    private boolean isForUpdate;

    public void setData(EntityManagerFactory entityManagerFactory, User user, boolean isForUpdate) {
        this.entityManagerFactory = entityManagerFactory;
        this.genericHibernate = new GenericHibernate(entityManagerFactory);
        this.userForUpdate = user;
        this.isForUpdate = isForUpdate;
        fillUserDataForUpdate();
    }

    private void fillUserDataForUpdate() {
        if(userForUpdate != null && isForUpdate){
            if(userForUpdate instanceof User){
                usernameField.setText(userForUpdate.getLogin() != null ? userForUpdate.getLogin() : "");
                passwordField.setText(userForUpdate.getPassword() != null ? userForUpdate.getPassword() : "");
                nameField.setText(userForUpdate.getName() != null ? userForUpdate.getName() : "");
                phoneField.setText(userForUpdate.getPhoneNumber() != null ? userForUpdate.getPhoneNumber() : "");
                if (userForUpdate instanceof Restaurant) {
                    restaurantRadio.setSelected(true);
                    Restaurant restaurant = (Restaurant) userForUpdate;
                    addressField.setText(restaurant.getAddress() != null ? restaurant.getAddress() : "");
                } else if (userForUpdate instanceof BasicUser) {
                    clientRadio.setSelected(true);
                    BasicUser basicUser = (BasicUser) userForUpdate;
                    addressField.setText(basicUser.getAddress() != null ? basicUser.getAddress() : "");
                    surnameField.setText(userForUpdate.getSurname() != null ? userForUpdate.getSurname() : "");
                } else if(userForUpdate instanceof Driver){
                    driverRadio.setSelected(true);
                    Driver driver = (Driver) userForUpdate;
                    addressField.setText(driver.getAddress() != null ? driver.getAddress() : "");
                    surnameField.setText(userForUpdate.getSurname() != null ? userForUpdate.getSurname() : "");
                } else {
                    userRadio.setSelected(true);
                    surnameField.setText(userForUpdate.getSurname() != null ? userForUpdate.getSurname() : "");
                }

                updateButton.setVisible(true);
                disableFields();
            }
        }else{
            updateButton.setVisible(false);
        }
    }

    public void disableFields() {
        if (userRadio.isSelected()) {
            addressField.setDisable(true);
            surnameField.setDisable(false);
        } else if (restaurantRadio.isSelected()) {
            restaurantRadio.setSelected(true);
            addressField.setDisable(false);
            surnameField.setDisable(true);
        } else if (clientRadio.isSelected()) {
            clientRadio.setSelected(true);
            addressField.setDisable(false);
            surnameField.setDisable(false);
        } else if (driverRadio.isSelected()) {
            driverRadio.setSelected(true);
            addressField.setDisable(false);
            surnameField.setDisable(false);
        } else {
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        comboTest.getItems().addAll(VehicleType.values());
    }

    public void createNewUser() {
        try {
            if (userRadio.isSelected()) {
                User user = new User(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText());
                genericHibernate.create(user);
            } else if (restaurantRadio.isSelected()) {
                Restaurant restaurant = new Restaurant(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText());
                genericHibernate.create(restaurant);
            } else if (clientRadio.isSelected()) {
                BasicUser basicUser = new BasicUser(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText());
                genericHibernate.create(basicUser);
            } else if (driverRadio.isSelected()) {
                Driver driver = new Driver(usernameField.getText(), passwordField.getText(), nameField.getText(), surnameField.getText(), phoneField.getText(), addressField.getText(), "SampleLicence", LocalDate.of(1969, 4, 20), VehicleType.SCOOTER);
                //REMOVE THE PLACEHOLDERS
                genericHibernate.create(driver);
            }
            new Alert(Alert.AlertType.INFORMATION, "User successfully created!").showAndWait();
            backToLoginFormButton.fire();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error creating user: " + e.getMessage()).showAndWait();
        }
    }

    public void updateUser(ActionEvent actionEvent) {
        if(userForUpdate == null) return;
            userForUpdate.setLogin(usernameField.getText());
            userForUpdate.setPassword(passwordField.getText());
            userForUpdate.setName(nameField.getText());
            userForUpdate.setPhoneNumber(phoneField.getText());
        try{
            if(userForUpdate instanceof User){
                if (userForUpdate instanceof Restaurant) {
                    Restaurant restaurant = (Restaurant) userForUpdate;
                    restaurant.setAddress(addressField.getText());
                } else {
                    userForUpdate.setSurname(surnameField.getText());
            }
                genericHibernate.update(userForUpdate);
                new Alert(Alert.AlertType.INFORMATION, "User successfully updated!").showAndWait();
                ((Button) actionEvent.getSource()).getScene().getWindow().hide();
        }
    } catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Error updating user: " + e.getMessage()).showAndWait();
        }
    }

    public void backToLoginForm(ActionEvent actionEvent) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-form.fxml"));
            Parent parent = fxmlLoader.load();
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e){
            new Alert(Alert.AlertType.ERROR, "Error loading login form: " + e.getMessage()).showAndWait();
        }
    }
}