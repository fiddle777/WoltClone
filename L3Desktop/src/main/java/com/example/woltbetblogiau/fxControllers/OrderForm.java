package com.example.woltbetblogiau.fxControllers;

import com.example.woltbetblogiau.hibernateControl.CustomHibernate;
import com.example.woltbetblogiau.model.Driver;
import com.example.woltbetblogiau.model.FoodOrder;
import com.example.woltbetblogiau.model.OrderStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OrderForm implements Initializable {

    @FXML private Label orderIdLabel;
    @FXML private TextField orderNameField;
    @FXML private TextField orderPriceField;
    @FXML private TextArea itemsSummaryArea;
    @FXML private ComboBox<OrderStatus> statusField;
    @FXML private ComboBox<Driver> driverField;

    private CustomHibernate customHibernate;
    private FoodOrder order;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusField.getItems().setAll(OrderStatus.values());
    }

    public void setData(CustomHibernate customHibernate, FoodOrder order) {
        this.customHibernate = customHibernate;
        this.order = order;

        orderIdLabel.setText("Order #" + order.getId());
        orderNameField.setText(order.getName());
        orderPriceField.setText(order.getPrice() != null ? order.getPrice().toString() : "");
        itemsSummaryArea.setText(order.getItemsSummary() != null ? order.getItemsSummary() : "");

        statusField.getSelectionModel().select(order.getStatus());

        List<Driver> drivers = customHibernate.getAllRecords(Driver.class);
        driverField.getItems().setAll(drivers);
        if (order.getDriver() != null) {
            driverField.getSelectionModel().select(order.getDriver());
        }
    }

    @FXML
    public void save(ActionEvent event) {
        if (order == null) return;

        String priceText = orderPriceField.getText();
        Double price = null;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Enter a valid price.").showAndWait();
            return;
        }

        order.setName(orderNameField.getText());
        order.setPrice(price);
        order.setItemsSummary(itemsSummaryArea.getText());

        OrderStatus status = statusField.getSelectionModel().getSelectedItem();
        if (status != null) {
            order.setStatus(status);
        }

        Driver driver = driverField.getSelectionModel().getSelectedItem();
        order.setDriver(driver);

        customHibernate.update(order);

        close(event);
    }

    @FXML
    public void cancel(ActionEvent event) {
        close(event);
    }

    private void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
