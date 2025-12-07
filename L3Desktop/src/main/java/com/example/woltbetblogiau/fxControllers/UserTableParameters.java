package com.example.woltbetblogiau.fxControllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserTableParameters {
    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty userType = new SimpleStringProperty();
    private SimpleStringProperty login = new SimpleStringProperty();
    private SimpleStringProperty password = new SimpleStringProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty surname = new SimpleStringProperty();
    private SimpleStringProperty phoneNum = new SimpleStringProperty();
    private SimpleStringProperty address = new SimpleStringProperty();

    public UserTableParameters(int id,
                               String typeLabel,
                               String login,
                               String password,
                               String name,
                               String surname,
                               String phone,
                               String address) {
        this.id.set(id);
        this.userType.set(typeLabel);
        this.login.set(login);
        this.password.set(password);
        this.name.set(name);
        this.surname.set(surname);
        this.phoneNum.set(phone);
        this.address.set(address);
    }


    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getUserType() {
        return userType.get();
    }

    public SimpleStringProperty userTypeProperty() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType.set(userType);
    }

    public String getLogin() {
        return login.get();
    }

    public SimpleStringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSurname() {
        return surname.get();
    }

    public SimpleStringProperty surnameProperty() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname.set(surname);
    }

    public String getPhoneNum() {
        return phoneNum.get();
    }

    public SimpleStringProperty phoneNumProperty() {
        return phoneNum;
    }

    public void setPhoneNumber(String phoneNum) {
        this.phoneNum.set(phoneNum);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getPhoneNumber() {
        return phoneNum.get();
    }
}
