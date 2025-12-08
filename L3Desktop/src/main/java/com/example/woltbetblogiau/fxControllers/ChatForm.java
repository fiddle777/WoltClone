package com.example.woltbetblogiau.fxControllers;

import com.example.woltbetblogiau.hibernateControl.CustomHibernate;
import com.example.woltbetblogiau.model.BasicUser;
import com.example.woltbetblogiau.model.Chat;
import com.example.woltbetblogiau.model.FoodOrder;
import com.example.woltbetblogiau.model.Review;
import com.example.woltbetblogiau.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ChatForm implements Initializable {

    public ListView<Review> messageList;
    public TextArea messageBody;

    private EntityManagerFactory entityManagerFactory;
    private CustomHibernate customHibernate;
    private User currentUser;
    private FoodOrder currentFoodOrder;
    private Chat currentChat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Review item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    StringBuilder sb = new StringBuilder();

                    String author = item.getAuthorLabel();
                    String time   = item.getTimeLabel();

                    if (author != null && !author.isBlank()) {
                        sb.append("[")
                                .append(author);
                        if (time != null && !time.isBlank()) {
                            sb.append(" @ ").append(time);
                        }
                        sb.append("] ");
                    }

                    if (item.getReviewText() != null) {
                        sb.append(item.getReviewText());
                    }

                    if (item.getDateCreated() != null) {
                        sb.append(" (")
                                .append(item.getDateCreated().format(DateTimeFormatter.ISO_DATE))
                                .append(")");
                    }

                    setText(sb.toString());
                }
            }
        });
    }


    public void setData(EntityManagerFactory emf, User currentUser, FoodOrder order) {
        this.entityManagerFactory = emf;
        this.customHibernate = new CustomHibernate(entityManagerFactory);
        this.currentUser = currentUser;
        this.currentFoodOrder = order;

        FoodOrder managedOrder =
                customHibernate.getEntityById(FoodOrder.class, currentFoodOrder.getId());
        this.currentFoodOrder = managedOrder;
        this.currentChat = managedOrder.getChat();

        if (this.currentChat == null) {
            messageList.setItems(FXCollections.observableArrayList());
        } else {
            loadMessages();
        }
    }

    private void loadMessages() {
        if (currentChat == null) {
            messageList.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Review> messages = customHibernate.getChatMessages(currentChat);
        messageList.setItems(FXCollections.observableArrayList(messages));
    }

    public void sendMessage(ActionEvent event) {
        String text = messageBody.getText();
        if (text == null || text.isBlank()) {
            return;
        }

        if (currentFoodOrder.getChat() == null) {
            Chat chat = new Chat("Chat for order " + currentFoodOrder.getId(), currentFoodOrder);
            customHibernate.create(chat);
            currentFoodOrder =
                    customHibernate.getEntityById(FoodOrder.class, currentFoodOrder.getId());
        }

        currentChat = currentFoodOrder.getChat();

        BasicUser sender = null;
        if (currentUser instanceof BasicUser) {
            sender = (BasicUser) currentUser;
        }

        Review message = new Review(text, sender, currentChat);
        customHibernate.create(message);

        messageBody.clear();
        loadMessages();
    }
}
