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
        if (userForUpdate == null) return;

        String login    = usernameField.getText();
        String password = passwordField.getText();
        String name     = nameField.getText();
        String surname  = surnameField.getText();
        String phone    = phoneField.getText();
        String address  = addressField.getText();

        try {
            boolean typeChanged = false;

            // Check if selected type matches current entity type
            if (userRadio.isSelected() && !(userForUpdate instanceof BasicUser)) {
                userForUpdate.setLogin(login);
                userForUpdate.setPassword(password);
                userForUpdate.setName(name);
                userForUpdate.setSurname(surname);
                userForUpdate.setPhoneNumber(phone);

                genericHibernate.update(userForUpdate);

            } else if (clientRadio.isSelected()
                    && (userForUpdate instanceof BasicUser)
                    && !(userForUpdate instanceof Driver)
                    && !(userForUpdate instanceof Restaurant)) {

                BasicUser basicUser = (BasicUser) userForUpdate;
                basicUser.setLogin(login);
                basicUser.setPassword(password);
                basicUser.setName(name);
                basicUser.setSurname(surname);
                basicUser.setPhoneNumber(phone);
                basicUser.setAddress(address);

                genericHibernate.update(basicUser);

            } else if (restaurantRadio.isSelected()
                    && (userForUpdate instanceof Restaurant)) {

                Restaurant restaurant = (Restaurant) userForUpdate;
                restaurant.setLogin(login);
                restaurant.setPassword(password);
                restaurant.setName(name);
                restaurant.setPhoneNumber(phone);
                restaurant.setAddress(address);

                genericHibernate.update(restaurant);

            } else if (driverRadio.isSelected()
                    && (userForUpdate instanceof Driver)) {

                Driver driver = (Driver) userForUpdate;
                driver.setLogin(login);
                driver.setPassword(password);
                driver.setName(name);
                driver.setSurname(surname);
                driver.setPhoneNumber(phone);
                driver.setAddress(address);

                genericHibernate.update(driver);

            } else {
                // Type actually changed – delete old entity and create a new one
                typeChanged = true;
                int oldId = userForUpdate.getId();

                // delete old
                genericHibernate.delete(User.class, oldId);

                // create new according to selected radio
                if (userRadio.isSelected()) {
                    User newUser = new User(login, password, name, surname, phone);
                    genericHibernate.create(newUser);

                } else if (clientRadio.isSelected()) {
                    BasicUser newBasic = new BasicUser(login, password, name, surname, phone, address);
                    genericHibernate.create(newBasic);

                } else if (restaurantRadio.isSelected()) {
                    Restaurant newRest = new Restaurant(login, password, name, surname, phone, address);
                    genericHibernate.create(newRest);

                } else if (driverRadio.isSelected()) {
                    // still using placeholder licence/bDate like in createNewUser()
                    Driver newDriver = new Driver(
                            login,
                            password,
                            name,
                            surname,
                            phone,
                            address,
                            "SampleLicence",
                            LocalDate.of(1969, 4, 20),
                            VehicleType.SCOOTER
                    );
                    genericHibernate.create(newDriver);
                }
            }

            new Alert(Alert.AlertType.INFORMATION,
                    typeChanged ? "User type changed and user updated!"
                            : "User successfully updated!")
                    .showAndWait();

            ((Button) actionEvent.getSource()).getScene().getWindow().hide();

        } catch (Exception e) {
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