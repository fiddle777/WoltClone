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
import javafx.util.StringConverter;
import javafx.scene.control.Alert;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class MainForm implements Initializable {
    @FXML public DatePicker DateFilterFrom;
    @FXML public DatePicker DateFilterTo;
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
    public ComboBox<Restaurant> filterRestaurants;
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
    public TextArea chatMessageBody;
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

        if (clientList != null) {
            setupClientList();
        }

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

        foodList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cuisine it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                    return;
                }
                if (it.getPrice() != null) {
                    setText(it.getName() + " • " + it.getPrice());
                } else {
                    setText(it.getName());
                }
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

        cuisineList.getSelectionModel().selectedItemProperty().addListener((obs, oldCuisine, newCuisine) -> {
            loadCuisineInfo();
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
        if (currentUser == null) {
            return;
        }

        boolean isRestaurant = currentUser instanceof Restaurant;
        boolean isAdmin = currentUser.isAdmin();

        if (isAdmin) {
            // admin fill access
            if (userTab != null) userTab.setDisable(false);
            if (managementTab != null) managementTab.setDisable(false);
            if (foodTab != null) foodTab.setDisable(false);
            if (chatTab != null) chatTab.setDisable(false);
            return;
        }

        if (isRestaurant) {
            // Restaurant
            if (userTab != null) userTab.setDisable(true);
            if (managementTab != null) managementTab.setDisable(false);
            if (foodTab != null) foodTab.setDisable(false);
            if (chatTab != null) chatTab.setDisable(false);
            return;
        }

        // Safety net
        new Alert(Alert.AlertType.ERROR,
                "Only administrators and restaurant accounts can use the desktop application.")
                .showAndWait();
        if (tabsPane != null && tabsPane.getScene() != null) {
            Stage stage = (Stage) tabsPane.getScene().getWindow();
            stage.close();
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
                if (currentUser instanceof Restaurant restaurant) {
                    // Only this restaurant in the list, and lock it.
                    restaurantList.getItems().setAll(FXCollections.observableArrayList(restaurant));
                    restaurantList.getSelectionModel().select(restaurant);
                    restaurantList.setDisable(true);
                } else {
                    restaurantList.setDisable(false);
                    restaurantList.getItems().setAll(customHibernate.getAllRecords(Restaurant.class));
                }
            } else if (chatTab.isSelected()) {
                if (currentUser instanceof Restaurant restaurant) {
                    allChats.getItems().setAll(customHibernate.getRestaurantChats(restaurant));
                } else {
                    allChats.getItems().setAll(customHibernate.getAllRecords(Chat.class));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading data: " + e.getMessage()).showAndWait();
        }
        //pabaigt
    }
    private void clearAllOrderFields() {
        if (ordersList != null) {
            ordersList.getItems().clear();
            ordersList.getSelectionModel().clearSelection();
        }
        if (basicUserList != null) {
            basicUserList.getItems().clear();
            basicUserList.getSelectionModel().clearSelection();
        }
        if (clientList != null) {
            clientList.getItems().clear();
            clientList.getSelectionModel().clearSelection();
            clientList.setValue(null);
        }
        if (restaurantField != null) {
            restaurantField.getItems().clear();
            restaurantField.getSelectionModel().clearSelection();
            restaurantField.setValue(null);
        }
        if (foodList != null) {
            foodList.getSelectionModel().clearSelection();
            // keep menu population separate (loadRestaurantMenu / loadRestaurantMenuForOrder)
        }
        if (orderStatusField != null) orderStatusField.getSelectionModel().clearSelection();
        if (filterStatus != null) filterStatus.getSelectionModel().clearSelection();
        if (filterClients != null) filterClients.getSelectionModel().clearSelection();
        if (filterFrom != null) filterFrom.setValue(null);
        if (filterTo != null) filterTo.setValue(null);

        if (titleField != null) titleField.clear();
        if (priceField != null) priceField.clear();
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
        FoodOrder selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.WARNING, "Select an order to update.").showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(HelloApplication.class.getResource("order-form.fxml"));
            Parent parent = fxmlLoader.load();

            OrderForm controller = fxmlLoader.getController();
            controller.setData(customHibernate, selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Edit order #" + selectedOrder.getId());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // refresher
            helperPopulateManagementTab();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open order editor: " + e.getMessage())
                    .showAndWait();
        }
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

        foodList.getItems().clear();

        // Preferred path – backend orders with itemsSummary string
        if (selectedOrder.getItemsSummary() != null &&
                !selectedOrder.getItemsSummary().isBlank()) {

            String[] parts = selectedOrder.getItemsSummary().split(",");
            for (String part : parts) {
                String itemText = part.trim();
                // light Cuisine just for display
                Cuisine pseudo = new Cuisine(itemText, "", null, false, false, null);
                foodList.getItems().add(pseudo);
            }

            //Fallback – old desktop-created orders that still use cuisineList
        } else if (selectedOrder.getCuisineList() != null &&
                !selectedOrder.getCuisineList().isEmpty()) {

            foodList.getItems().setAll(selectedOrder.getCuisineList());
        }


        if (selectedOrder.getStatus() != null) {
            orderStatusField.getSelectionModel().select(selectedOrder.getStatus());
        }

        disableFoodOrderFields();
    }

    private void disableFoodOrderFields() {
        if (orderStatusField.getSelectionModel().getSelectedItem() == OrderStatus.DELIVERED) {
            clientList.setDisable(true);
            priceField.setDisable(true);
        }
    }

    public void filterOrders() {
        OrderStatus status = (filterStatus != null)
                ? filterStatus.getSelectionModel().getSelectedItem()
                : null;

        BasicUser client = (filterClients != null)
                ? filterClients.getSelectionModel().getSelectedItem()
                : null;

        LocalDate start = (filterFrom != null) ? filterFrom.getValue() : null;
        LocalDate end   = (filterTo   != null) ? filterTo.getValue()   : null;

        Restaurant restaurant = null;

        if (filterRestaurants != null &&
                filterRestaurants.getSelectionModel().getSelectedItem() != null) {
            restaurant = filterRestaurants.getSelectionModel().getSelectedItem();
        } else if (currentUser instanceof Restaurant) {
            restaurant = (Restaurant) currentUser;
        }

        List<FoodOrder> filteredOrders =
                customHibernate.getFilteredRestaurantOrders(status, client, start, end, restaurant);
        ordersList.getItems().setAll(filteredOrders);
    }



    public void loadRestaurantMenuForOrder() {
        Restaurant restaurant = restaurantField.getSelectionModel().getSelectedItem();
        foodList.getItems().clear();
        if (restaurant != null) {
            foodList.getItems().addAll(customHibernate.getRestaurantCuisine(restaurant));
        }
    }
    private void setupClientList() {
        if (clientList == null) return;

        clientList.setPromptText("Select client");

        clientList.setCellFactory(cb -> new ListCell<BasicUser>() {
            @Override
            protected void updateItem(BasicUser it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                } else {
                    setText(it.getName() + " " + it.getSurname() + " (" + it.getLogin() + ")");
                }
            }
        });

        clientList.setButtonCell(new ListCell<BasicUser>() {
            @Override
            protected void updateItem(BasicUser it, boolean empty) {
                super.updateItem(it, empty);
                if (empty || it == null) {
                    setText(null);
                } else {
                    setText(it.getName() + " " + it.getSurname() + " (" + it.getLogin() + ")");
                }
            }
        });

        clientList.setConverter(new StringConverter<BasicUser>() {
            @Override
            public String toString(BasicUser u) {
                return u == null ? "" : u.getName() + " " + u.getSurname() + " (" + u.getLogin() + ")";
            }
            @Override
            public BasicUser fromString(String string) {
                // Not used for non-editable combo; return null
                return null;
            }
        });
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
    @FXML
    public void loadChatMessages(MouseEvent event) {
        Chat selectedChat = allChats.getSelectionModel().getSelectedItem();
        if (selectedChat == null) {
            chatMessages.getItems().clear();
            return;
        }

        List<Review> messages = customHibernate.getChatMessages(selectedChat);
        chatMessages.getItems().setAll(messages);
    }

    @FXML
    public void deleteChat(ActionEvent event) {
        Chat selectedChat = allChats.getSelectionModel().getSelectedItem();

        if (selectedChat == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a chat to delete.")
                    .showAndWait();
            return;
        }

        customHibernate.delete(Chat.class, selectedChat.getId());
        allChats.getItems().setAll(customHibernate.getAllRecords(Chat.class));
        chatMessages.getItems().clear();
    }



    @FXML
    public void deleteMessage(ActionEvent event) {
        Review selectedMessage = chatMessages.getSelectionModel().getSelectedItem();
        Chat selectedChat = allChats.getSelectionModel().getSelectedItem();

        if (selectedMessage == null || selectedChat == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a message to delete.")
                    .showAndWait();
            return;
        }

        customHibernate.delete(Review.class, selectedMessage.getId());
        chatMessages.getItems().setAll(customHibernate.getChatMessages(selectedChat));
    }


    //</editor-fold>
    private void helperPopulateManagementTab() {
        clearAllOrderFields();

        List<FoodOrder> foodOrders = getFoodOrders();
        ordersList.getItems().setAll(foodOrders);

        List<BasicUser> allClients = customHibernate.getAllBasicUsers();
        clientList.getItems().setAll(allClients);

        // Restaurant combobox in order editor
        restaurantField.getItems()
                .setAll(customHibernate.getAllRecords(Restaurant.class));

        // lock restaurantField if rest login
        if (currentUser instanceof Restaurant restaurant) {
            restaurantField.getItems().setAll(FXCollections.observableArrayList(restaurant));
            restaurantField.getSelectionModel().select(restaurant);
            restaurantField.setDisable(true);
        } else {
            restaurantField.setDisable(false);
        }

        orderStatusField.getItems().setAll(OrderStatus.values());
        filterStatus.getItems().setAll(OrderStatus.values());

        List<BasicUser> distinctBuyers = foodOrders.stream()
                .map(FoodOrder::getBuyer)
                .filter(b -> b != null)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toMap(BasicUser::getId, b -> b, (a, b) -> a),
                        m -> new java.util.ArrayList<>(m.values())
                ));

        filterClients.getItems().setAll(distinctBuyers);
        basicUserList.getItems().setAll(distinctBuyers);

        if (filterRestaurants != null) {
            List<Restaurant> distinctRestaurants = foodOrders.stream()
                    .map(FoodOrder::getRestaurant)
                    .filter(r -> r != null)
                    .collect(java.util.stream.Collectors.collectingAndThen(
                            java.util.stream.Collectors.toMap(Restaurant::getId, r -> r, (a, b) -> a),
                            m -> new java.util.ArrayList<>(m.values())
                    ));

            if (currentUser instanceof Restaurant restaurant) {
                filterRestaurants.getItems().setAll(FXCollections.observableArrayList(restaurant));
                filterRestaurants.getSelectionModel().select(restaurant);
                filterRestaurants.setDisable(true);
            } else {
                filterRestaurants.getItems().setAll(distinctRestaurants);
                filterRestaurants.setDisable(false);
            }
        }
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

    public void loadCuisineInfo() {
        Cuisine selected = cuisineList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            titleCuisineField.clear();
            ingredientsField.clear();
            cuisinePriceField.clear();
            isDeadly.setSelected(false);
            isVegan.setSelected(false);
            return;
        }

        titleCuisineField.setText(selected.getName());
        ingredientsField.setText(selected.getIngredients());
        cuisinePriceField.setText(
                selected.getPrice() != null ? selected.getPrice().toString() : ""
        );
        isDeadly.setSelected(selected.isSpicy());
        isVegan.setSelected(selected.isVegan());
    }

    public void loadChatForm(ActionEvent actionEvent) {
        FoodOrder selectedOrder = ordersList.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            new Alert(Alert.AlertType.WARNING, "Select an order to open chat for.").showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    HelloApplication.class.getResource("chat-form.fxml"));
            Parent parent = fxmlLoader.load();

            ChatForm controller = fxmlLoader.getController();
            controller.setData(entityManagerFactory, currentUser, selectedOrder);

            Stage stage = new Stage();
            stage.setTitle("Chat for order #" + selectedOrder.getId());
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Failed to open chat window:\n" + e.getMessage())
                    .showAndWait();
            e.printStackTrace();
        }

    }

    @FXML
    public void sendChatMessage(ActionEvent event) {
        Chat selectedChat = allChats.getSelectionModel().getSelectedItem();
        if (selectedChat == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Select a chat to send a message.")
                    .showAndWait();
            return;
        }

        String text = chatMessageBody.getText();
        if (text == null || text.isBlank()) {
            new Alert(Alert.AlertType.WARNING,
                    "Message is empty.")
                    .showAndWait();
            return;
        }

        BasicUser sender = null;
        if (currentUser instanceof BasicUser) {
            sender = (BasicUser) currentUser;
        }

        Review message = new Review(text, sender, selectedChat);
        customHibernate.create(message);

        chatMessageBody.clear();
        chatMessages.getItems().setAll(customHibernate.getChatMessages(selectedChat));
    }



}