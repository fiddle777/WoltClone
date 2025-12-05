package com.example.woltbetblogiau.fxControllers;
import com.example.woltbetblogiau.HelloApplication;
import com.example.woltbetblogiau.hibernateControl.CustomHibernate;
import com.example.woltbetblogiau.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class MainForm implements Initializable {
    @FXML
    public Tab userTab;
    @FXML
    public Tab managementTab;
    @FXML
    public Tab foodTab;
    @FXML
    public TabPane tabsPane;
    //<editor-fold desc="User Tab Elements">
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public TableColumn<UserTableParameters, Integer> idCol;
    @FXML
    public TableColumn<UserTableParameters, String> userTypeCol;
    @FXML
    public TableColumn<UserTableParameters, String> loginCol;
    @FXML
    public TableColumn<UserTableParameters, String> passCol;
    @FXML
    public TableColumn<UserTableParameters, String> nameCol;
    @FXML
    public TableColumn<UserTableParameters, String> surnameCol;
    @FXML
    public TableColumn<UserTableParameters, String> addrCol;
    @FXML
    public TableColumn<UserTableParameters, Void> dummyCol;
    private ObservableList<UserTableParameters> data = FXCollections.observableArrayList();
    //</editor-fold>
    //<editor-fold desc="Order Tab Elements">
    public ListView<FoodOrder> ordersList;
    public TextField titleField;
    public ComboBox<BasicUser> clientList;
    public TextField priceField;
    public ComboBox<Restaurant> restaurantField;
    public ListView<BasicUser> basicUserList;
    public ComboBox<OrderStatus> orderStatusField;
    public ComboBox<OrderStatus> filterStatus;
    public ComboBox<BasicUser> filterClients;
    public DatePicker filterFrom;
    public DatePicker filterTo;
    public ListView<Cuisine> foodList;
    //</editor-fold>
    //<editor-fold desc="Cuisine Tab Elements">
    public TextField titleCuisineField;
    public TextArea ingredientsField;
    public ListView<Restaurant> restaurantList;
    public TextField cuisinePriceField;
    public CheckBox isDeadly;
    public CheckBox isVegan;
    public ListView<Cuisine> cuisineList;
    //</editor-fold>
    //<editor-fold desc="Admin chat Elements">
    public Tab chatTab;
    public ListView<Chat> allChats;
    public ListView<Review> chatMessages;
    //</editor-fold>
    private EntityManagerFactory entityManagerFactory;
    private CustomHibernate customHibernate;
    private User currentUser;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userTable.setItems(data);
        userTable.setEditable(true);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));

        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        loginCol.setCellFactory(TextFieldTableCell.forTableColumn());
        loginCol.setOnEditCommit(event -> {
            UserTableParameters row = event.getTableView().getItems().get(event.getTablePosition().getRow());
            row.setLogin(event.getNewValue());
            if (customHibernate != null) {
                User user = customHibernate.getEntityById(User.class, row.getId());
                user.setLogin(event.getNewValue());
                customHibernate.update(user);
            }
        });

        passCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        passCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passCol.setOnEditCommit(event -> {
            UserTableParameters row = event.getTableView().getItems().get(event.getTablePosition().getRow());
            row.setPassword(event.getNewValue());
            if (customHibernate != null) {
                User user = customHibernate.getEntityById(User.class, row.getId());
                user.setPassword(event.getNewValue());
                customHibernate.update(user);
            }
        });

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> {
            UserTableParameters row = event.getTableView().getItems().get(event.getTablePosition().getRow());
            row.setName(event.getNewValue());
            if (customHibernate != null) {
                User user = customHibernate.getEntityById(User.class, row.getId());
                user.setName(event.getNewValue());
                customHibernate.update(user);
            }
        });

        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        surnameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        surnameCol.setOnEditCommit(event -> {
            UserTableParameters row = event.getTableView().getItems().get(event.getTablePosition().getRow());
            row.setSurname(event.getNewValue());
            if (customHibernate != null) {
                User user = customHibernate.getEntityById(User.class, row.getId());
                user.setSurname(event.getNewValue());
                customHibernate.update(user);
            }
        });

        addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addrCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addrCol.setOnEditCommit(event -> {
            UserTableParameters row = event.getTableView().getItems().get(event.getTablePosition().getRow());
            row.setAddress(event.getNewValue());
            if (customHibernate != null) {
                User user = customHibernate.getEntityById(User.class, row.getId());
                if (user instanceof BasicUser) {
                    ((BasicUser) user).setAddress(event.getNewValue());
                    customHibernate.update(user);
                }
            }
        });
        ordersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        foodList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (restaurantField != null) {
            restaurantField.setOnAction(event -> {
                loadRestaurantMenuForOrder();
            });
        }

        if (tabsPane != null) {
            tabsPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                reloadTableData();
            });
        }
        ordersList.setCellFactory(lv -> new ListCell<FoodOrder>() {
            @Override
            protected void updateItem(FoodOrder it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                String buyer = (it.getBuyer() != null) ? (it.getBuyer().getName() + " " + it.getBuyer().getSurname()) : "—";
                String rest = (it.getRestaurant() != null) ? it.getRestaurant().getName() : "—";
                setText("#" + it.getId() + " • " + it.getName() + " • " + buyer + " @ " + rest + " • " + it.getPrice());
            }
        });

        clientList.setCellFactory(cb -> new ListCell<BasicUser>() {
            @Override
            protected void updateItem(BasicUser it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " " + it.getSurname() + " (" + it.getLogin() + ")");
            }
        });
        clientList.setButtonCell(new ListCell<BasicUser>() {
            @Override
            protected void updateItem(BasicUser it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " " + it.getSurname() + " (" + it.getLogin() + ")");
            }
        });

        restaurantField.setCellFactory(cb -> new ListCell<Restaurant>() {
            @Override
            protected void updateItem(Restaurant it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " — " + (it.getAddress() != null ? it.getAddress() : ""));
            }
        });
        restaurantField.setButtonCell(new ListCell<Restaurant>() {
            @Override
            protected void updateItem(Restaurant it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " — " + (it.getAddress() != null ? it.getAddress() : ""));
            }
        });

        foodList.setCellFactory(lv -> new ListCell<Cuisine>() {
            @Override
            protected void updateItem(Cuisine it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " • " + it.getPrice());
            }
        });

        cuisineList.setCellFactory(lv -> new ListCell<Cuisine>() {
            @Override
            protected void updateItem(Cuisine it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                setText(it.getName() + " • " + it.getPrice());
            }
        });

        restaurantList.getSelectionModel().selectedItemProperty().addListener((obs, o, r) -> loadRestaurantMenu());
    }

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = user;
        this.customHibernate = new CustomHibernate(entityManagerFactory);
        setUserFormVisibility();
        reloadTableData();
    }
    private void setUserFormVisibility() {
        if (currentUser instanceof User) {
            //turbut nieko nedarom, gal kazka custom

        } else if (currentUser instanceof Restaurant) {
//            altTab.setDisable(true);
        }

    }
    //<editor-fold desc="User Tab functionality">
    public void reloadTableData() {
        if(customHibernate == null) {
            return;
        }
        try {
            if (userTab.isSelected()) {
                List<UserTableParameters> rows = new ArrayList<>();
                List<User> users = customHibernate.getAllRecords(User.class);
                for (User u : users) {
                    UserTableParameters utp = new UserTableParameters();
                    utp.setId(u.getId());
                    utp.setUserType(u.getClass().getSimpleName());
                    utp.setLogin(u.getLogin());
                    utp.setPassword(u.getPassword());
                    utp.setName(u.getName());
                    utp.setSurname(u.getSurname());
                    utp.setPhoneNum(u.getPhoneNumber());
                    if (u instanceof BasicUser) {
                        utp.setAddress(((BasicUser) u).getAddress());
                    }
                    if (u instanceof Restaurant) {
                    }
                    if (u instanceof Driver) {
                    }
                    rows.add(utp);
                }
                data.setAll(rows);
            } else if (managementTab.isSelected()) {
                helperPopulateManagementTab();
            } else if (foodTab.isSelected()) {
                clearAllCuisineFields();
                restaurantList.getItems().setAll(customHibernate.getAllRecords(Restaurant.class));
            } else if (chatTab.isSelected()) {
                allChats.getItems().setAll(customHibernate.getAllRecords(Chat.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading data: " + e.getMessage()).showAndWait();
        }
        //pabaigt
    }
    private void clearAllOrderFields() {
        //turbut reik salygos sakiniu
        ordersList.getItems().clear();
        basicUserList.getItems().clear();
        clientList.getItems().clear();
        restaurantField.getItems().clear();
        titleField.clear();
        priceField.clear();
    }
    private void clearAllCuisineFields() {
        foodList.getItems().clear();
        cuisinePriceField.clear();
        titleCuisineField.clear();
        ingredientsField.clear();
        isDeadly.setSelected(false);
        isVegan.setSelected(false);
        restaurantList.getItems().clear();
    }
    public void addUser(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        UserForm userForm = fxmlLoader.getController();
        userForm.setData(entityManagerFactory, null, false);


        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        reloadTableData();
    }
    public void loadUser(ActionEvent actionEvent) throws IOException {
        UserTableParameters row = userTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.ERROR, "Select a user", ButtonType.OK).showAndWait();
            return;
        }
        User entity = customHibernate.getEntityById(User.class, row.getId());
        if(entity == null) {
            new Alert(Alert.AlertType.ERROR, "User not found", ButtonType.OK).showAndWait();
            reloadTableData();
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();
        UserForm userForm = fxmlLoader.getController();
        userForm.setData(entityManagerFactory, entity, true);
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        reloadTableData();
    }
    public void deleteUser() {
        UserTableParameters row = userTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.ERROR, "Select a user", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the user?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.YES) {
            return;
        }
        customHibernate.delete(User.class, row.getId());
        reloadTableData();
    }

    //</editor-fold>

    //<editor-fold desc="Order Tab functionality">
    private List<FoodOrder> getFoodOrders() {
        if (currentUser instanceof Restaurant) {
            return customHibernate.getRestaurantOrders((Restaurant) currentUser);
        } else {
            return customHibernate.getAllRecords(FoodOrder.class);
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void createOrder() {
        String title = titleField.getText();
        String priceText = priceField.getText();
        BasicUser client = clientList.getValue();
        Restaurant restaurant = restaurantField.getValue();
        List<Cuisine> selectedFoods = foodList.getSelectionModel().getSelectedItems();
        if(title == null || title.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Title cannot be empty").showAndWait();
            return;
        }
        if(!isNumeric(priceText)) {
            new Alert(Alert.AlertType.ERROR, "Price must be a valid number").showAndWait();
            return;
        }
        if(client == null) {
            new Alert(Alert.AlertType.ERROR, "Select a client").showAndWait();
            return;
        }
        if(restaurant == null) {
            new Alert(Alert.AlertType.ERROR, "Select a restaurant").showAndWait();
            return;
        }
        if(selectedFoods == null || selectedFoods.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Select at least one food item").showAndWait();
            return;
        }
        double price = Double.parseDouble(priceText);
        FoodOrder foodOrder = new FoodOrder(
                title,
                price,
                client,
                selectedFoods,
                restaurant
        );
        customHibernate.create(foodOrder);
        fillOrderLists();
        new Alert(Alert.AlertType.INFORMATION, "Order created successfully").showAndWait();
    }

    public void updateOrder() {
        FoodOrder foodOrder = ordersList.getSelectionModel().getSelectedItem();
        if (foodOrder == null) {
            new Alert(Alert.AlertType.WARNING, "Select an order to update.").showAndWait();
            return;
        }
        if (!isNumeric(priceField.getText())) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid price.").showAndWait();
            return;
        }
        if (clientList.getSelectionModel().getSelectedItem() == null ||
                restaurantField.getSelectionModel().getSelectedItem() == null) {
            new Alert(Alert.AlertType.WARNING, "Select client and restaurant.").showAndWait();
            return;
        }
        foodOrder.setName(titleField.getText());
        foodOrder.setPrice(Double.valueOf(priceField.getText()));
        foodOrder.setBuyer(clientList.getSelectionModel().getSelectedItem());
        foodOrder.setRestaurant(restaurantField.getSelectionModel().getSelectedItem());
        if (orderStatusField.getValue() != null) {
            foodOrder.setOrderStatus(orderStatusField.getValue());
        }
        List<Cuisine> selectedFood = new ArrayList<>(foodList.getSelectionModel().getSelectedItems());
        foodOrder.setCuisineList(selectedFood);
        customHibernate.update(foodOrder);
        fillOrderLists();
        new Alert(Alert.AlertType.INFORMATION, "Order updated.").showAndWait();
    }

    public void deleteOrder() {
        FoodOrder selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.WARNING, "Select an order to delete.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this order?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.OK) return;

        customHibernate.delete(FoodOrder.class, selectedOrder.getId());
        fillOrderLists();
    }

    private void fillOrderLists() {
        ordersList.getItems().clear();
        ordersList.getItems().addAll(customHibernate.getAllRecords(FoodOrder.class));
    }

    public void loadOrderInfo() {
        FoodOrder selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) return;
        titleField.setText(selectedOrder.getName());
        priceField.setText(selectedOrder.getPrice() != null ? selectedOrder.getPrice().toString() : "");

        if (selectedOrder.getRestaurant() != null) {
            restaurantField.getItems().stream()
                    .filter(r -> r.getId() == selectedOrder.getRestaurant().getId())
                    .findFirst()
                    .ifPresent(r -> restaurantField.getSelectionModel().select(r));
            loadRestaurantMenuForOrder();
        }

        if (selectedOrder.getBuyer() != null) {
            clientList.getItems().stream()
                    .filter(c -> c.getId() == selectedOrder.getBuyer().getId())
                    .findFirst()
                    .ifPresent(c -> clientList.getSelectionModel().select(c));
        }

        foodList.getSelectionModel().clearSelection();
        if (selectedOrder.getCuisineList() != null) {
            for (Cuisine c : selectedOrder.getCuisineList()) {
                for (int i = 0; i < foodList.getItems().size(); i++) {
                    if (foodList.getItems().get(i).getId() == c.getId()) {
                        foodList.getSelectionModel().select(i);
                    }
                }
            }
        }

        if (selectedOrder.getOrderStatus() != null) {
            orderStatusField.getSelectionModel().select(selectedOrder.getOrderStatus());
        }

        disableFoodOrderFields();
    }

    private void disableFoodOrderFields() {
        if (orderStatusField.getSelectionModel().getSelectedItem() == OrderStatus.COMPLETED) {
            clientList.setDisable(true);
            priceField.setDisable(true);
        }
    }

    public void filterOrders() {
        OrderStatus status = filterStatus.getSelectionModel().getSelectedItem();
        BasicUser client = filterClients.getSelectionModel().getSelectedItem();
        LocalDate start = filterFrom.getValue();
        LocalDate end = filterTo.getValue();
        Restaurant restaurant = null;
        if (currentUser instanceof Restaurant) {
            restaurant = (Restaurant) currentUser;
        }
        List<FoodOrder> filteredOrders = customHibernate.getFilteredRestaurantOrders(status, client, start, end, restaurant);
        ordersList.getItems().setAll(filteredOrders);
    }

    public void loadRestaurantMenuForOrder() {
        Restaurant restaurant = restaurantField.getSelectionModel().getSelectedItem();
        foodList.getItems().clear();
        if (restaurant != null) {
            foodList.getItems().addAll(customHibernate.getRestaurantCuisine(restaurant));
        }
    }
    //</editor-fold>

    //<editor-fold desc="Cuisine Tab Functionality">
    public void createNewMenuItem() {
        Restaurant restaurant = restaurantList.getSelectionModel().getSelectedItem();
        String title = titleCuisineField.getText();
        String ingredients = ingredientsField.getText();
        String priceText = cuisinePriceField.getText();
        boolean spicy = isDeadly.isSelected();
        boolean vegan = isVegan.isSelected();
        if(restaurant == null) {
            new Alert(Alert.AlertType.ERROR, "Select a restaurant").showAndWait();
            return;
        }
        if(title == null || title.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Title cannot be empty").showAndWait();
            return;
        }
        if(ingredients == null || ingredients.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Ingredients cannot be empty").showAndWait();
            return;
        }
        double price;
        try{
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR, "Price must be a valid number").showAndWait();
            return;
        }
        Cuisine cuisine = new Cuisine(
                title,
                ingredients,
                price,
                spicy,
                vegan,
                restaurant
        );
        customHibernate.create(cuisine);
        loadRestaurantMenu();
        titleCuisineField.clear();
        ingredientsField.clear();
        cuisinePriceField.clear();
        isDeadly.setSelected(false);
        isVegan.setSelected(false);
        new Alert(Alert.AlertType.INFORMATION, "Menu item created successfully").showAndWait();
    }

    public void updateMenuItem(ActionEvent actionEvent) {
        Cuisine cuisine = cuisineList.getSelectionModel().getSelectedItem();
        if (cuisine == null) {
            new Alert(Alert.AlertType.WARNING, "Select a menu item to update.").showAndWait();
            return;
        }
        String title = titleCuisineField.getText();
        String ingredients = ingredientsField.getText();
        String priceText = cuisinePriceField.getText();
        boolean spicy = isDeadly.isSelected();
        boolean vegan = isVegan.isSelected();
        if(title == null || title.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Title cannot be empty").showAndWait();
            return;
        }
        if(ingredients == null || ingredients.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Ingredients cannot be empty").showAndWait();
            return;
        }
        double price;
        try{
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.ERROR, "Price must be a valid number").showAndWait();
            return;
        }
        cuisine.setName(title);
        cuisine.setIngredients(ingredients);
        cuisine.setPrice(price);
        cuisine.setSpicy(spicy);
        cuisine.setVegan(vegan);
        customHibernate.update(cuisine);
        loadRestaurantMenu();
        new Alert(Alert.AlertType.INFORMATION, "Menu item updated successfully").showAndWait();
    }

    public void loadRestaurantMenu() {
        Restaurant restaurant = restaurantList.getSelectionModel().getSelectedItem();
        cuisineList.getItems().clear();
        if (restaurant != null) {
            cuisineList.getItems().addAll(customHibernate.getRestaurantCuisine(restaurant));
        } else{
            return;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Admin Chat Functionality">
    public void loadChatMessages() {
//        chatMessages.getItems().addAll(customHibernate.getChatMessages(allChats.getSelectionModel().getSelectedItem()));
    }

    public void deleteChat() {
    }

    public void deleteMessage() {
    }

    public void loadChatForm(ActionEvent actionEvent) {

    }
    //</editor-fold>
    private void helperPopulateManagementTab() {
        clearAllOrderFields();
        List<FoodOrder> foodOrders = getFoodOrders();
        ordersList.getItems().setAll(foodOrders);
        List<BasicUser> clients = customHibernate.getAllBasicUsers();
        clientList.getItems().setAll(clients);
        basicUserList.getItems().setAll(clients);
        restaurantField.getItems().setAll(customHibernate.getAllRecords(Restaurant.class));
        orderStatusField.getItems().setAll(OrderStatus.values());
        filterStatus.getItems().setAll(OrderStatus.values());

        // Filter clients
        List<BasicUser> distinctBuyers = foodOrders.stream()
                .map(FoodOrder::getBuyer)
                .filter(b -> b != null)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toMap(BasicUser::getId, b -> b, (a,b) -> a),
                        m -> new ArrayList<>(m.values())
                ));
        filterClients.getItems().setAll(distinctBuyers);
    }

    public void deleteCuisine(ActionEvent actionEvent) {
        Cuisine cuisine = cuisineList.getSelectionModel().getSelectedItem();
        if (cuisine == null) {
            new Alert(Alert.AlertType.WARNING, "Select a menu item to delete.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this menu item?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait();
        if (confirm.getResult() != ButtonType.OK) return;

        customHibernate.delete(Cuisine.class, cuisine.getId());
        loadRestaurantMenu();
    }
}