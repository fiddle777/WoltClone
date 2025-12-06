package com.example.l3web.controllers;

import com.example.l3web.model.Chat;
import com.example.l3web.model.Cuisine;
import com.example.l3web.model.FoodOrder;
import com.example.l3web.model.Review;
import com.example.l3web.repos.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Properties;

@RestController
public class OrdersController {
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private CuisineRepo cuisineRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;

    @GetMapping(value = "getMenuRestaurant/{id}")
    public Iterable<Cuisine> getRestaurantMenu(@PathVariable int id){
        return cuisineRepo.getCuisineByRestaurantId(id);
    }

    @GetMapping(value = "getOrderByUser/{id}")
    public @ResponseBody Iterable<FoodOrder> getOrdersForUser(@PathVariable int id) {
        return ordersRepo.getFoodOrdersByBuyer_Id(id);
    }

    @GetMapping(value = "getMessagesForOrder/{id}")
    public @ResponseBody Iterable<Review> getMessagesForOrder(@PathVariable int id) {
        Chat chat = chatRepo.getChatByFoodOrder_Id(id);
        if(chat == null){
            FoodOrder order = ordersRepo.getReferenceById(id);
            Chat chat1 = new Chat("User " + order.getBuyer().getLogin(), "Chat order" + id, order);
            order.setChat(chat1);
            chatRepo.save(chat1);
        }
        return chatRepo.getChatByFoodOrder_Id(id).getMessages();
    }

    @PostMapping(value = "sendMessage")
    public @ResponseBody String sendMessage(@RequestBody String info) {
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var messageText = properties.getProperty("messageText");
        var commentOwner = basicUserRepo.getReferenceById(Integer.valueOf(properties.getProperty("userId")));
        var order = ordersRepo.getReferenceById(Integer.valueOf(properties.getProperty("orderId")));

        Review review = new Review(messageText, commentOwner, order.getChat());
        reviewRepo.save(review);

        return "test";

    }
    @PostMapping(value = "createOrder")
    public @ResponseBody FoodOrder createOrder(@RequestBody String info) {
        Gson gson = new Gson();

        com.google.gson.JsonObject root = gson.fromJson(info, com.google.gson.JsonObject.class);

        int userId = root.get("userId").getAsInt();
        int restaurantId = root.get("restaurantId").getAsInt();

        var buyer = basicUserRepo.getReferenceById(userId);
        var restaurant = restaurantRepo.getReferenceById(restaurantId);

        java.util.List<Cuisine> cuisines = new java.util.ArrayList<>();
        double totalPrice = 0.0;

        com.google.gson.JsonArray itemsArray = root.getAsJsonArray("items");
        if (itemsArray != null) {
            for (int i = 0; i < itemsArray.size(); i++) {
                com.google.gson.JsonObject itemObj = itemsArray.get(i).getAsJsonObject();
                int cuisineId = itemObj.get("cuisineId").getAsInt();
                int quantity = itemObj.has("quantity") ? itemObj.get("quantity").getAsInt() : 1;

                Cuisine cuisine = cuisineRepo.getReferenceById(cuisineId);
                if (cuisine != null) {
                    for (int q = 0; q < quantity; q++) {
                        cuisines.add(cuisine);
                    }
                    if (cuisine.getPrice() != null) {
                        totalPrice += cuisine.getPrice() * quantity;
                    }
                }
            }
        }
        //items summary like "Burger x2, Fries x1"
        java.util.Map<String, Integer> itemCounts = new java.util.LinkedHashMap<>();
        for (Cuisine c : cuisines) {
            itemCounts.merge(c.getName(), 1, Integer::sum);
        }

        StringBuilder summaryBuilder = new StringBuilder();
        for (var entry : itemCounts.entrySet()) {
            if (summaryBuilder.length() > 0) summaryBuilder.append(", ");
            summaryBuilder.append(entry.getKey());
            if (entry.getValue() > 1) {
                summaryBuilder.append(" x").append(entry.getValue());
            }
        }

        FoodOrder order = new FoodOrder();
        order.setName("Order for " + buyer.getLogin());
        order.setPrice(totalPrice);
        order.setBuyer(buyer);
        order.setRestaurant(restaurant);
        order.setCuisineList(cuisines);

        order.setRestaurantName(restaurant.getName());
        order.setItemsSummary(summaryBuilder.toString());

        ordersRepo.save(order);

        Chat chat = new Chat("User " + buyer.getLogin(), "Chat order " + order.getId(), order);
        order.setChat(chat);
        chatRepo.save(chat);
        ordersRepo.save(order);

        return order;
    }

}