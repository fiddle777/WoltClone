package com.example.woltbetblogiau.hibernateControl;

import com.example.woltbetblogiau.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomHibernate extends GenericHibernate {

    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String login, String psw) {
        User user = null;
        entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.and(cb.equal(root.get("login"), login),
                    cb.equal(root.get("password"), psw)));
            Query q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();
        } catch (Exception e) {

        } finally {
            if(entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return user;
    }

    public List<FoodOrder> getRestaurantOrders(Restaurant restaurant) {
        entityManager = null;
        List<FoodOrder> orders = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));
            Query q = entityManager.createQuery(query);
            orders = q.getResultList();
        } catch (Exception e) {

        } finally {
            if(entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return orders;
    }

    public List<Cuisine> getRestaurantCuisine(Restaurant restaurant) {
        entityManager = null;
        List<Cuisine> menu = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Cuisine> query = cb.createQuery(Cuisine.class);
            Root<Cuisine> root = query.from(Cuisine.class);

            query.select(root).where(cb.equal(root.get("restaurant"), restaurant));
            Query q = entityManager.createQuery(query);
            menu = q.getResultList();
        } catch (Exception e) {

        } finally {
            if(entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return menu;
    }

    public List<FoodOrder> getFilteredRestaurantOrders(OrderStatus orderStatus, BasicUser client, LocalDate start, LocalDate end, Restaurant restaurant) {
        entityManager = null;
        List<FoodOrder> orders = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<FoodOrder> query = cb.createQuery(FoodOrder.class);
            Root<FoodOrder> root = query.from(FoodOrder.class);

            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            //Cia aiskinsiu Predicates
            if(restaurant != null) {
                predicates.add(cb.equal(root.get("restaurant"), restaurant));
            }
            if(orderStatus != null){
                predicates.add(cb.equal(root.get("status"), orderStatus));
            }
            if(client != null){
                predicates.add(cb.equal(root.get("buyer"), client));
            }
            if (start != null && end != null) {
                predicates.add(cb.between(root.get("dateCreated"), start, end));
            } else if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreated"), start));
            } else if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateCreated"), end));
            }
            query.select(root);
            if(!predicates.isEmpty()){
                query.where(cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
            }

            Query q = entityManager.createQuery(query);
            orders = q.getResultList();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if(entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return orders;
    }
    public List<BasicUser> getAllBasicUsers() {
        List<BasicUser> list = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            var cb = entityManager.getCriteriaBuilder();
            var cq = cb.createQuery(BasicUser.class);
            var root = cq.from(BasicUser.class);
            cq.select(root).where(cb.equal(root.type(), BasicUser.class));
            list = entityManager.createQuery(cq).getResultList();
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return list;
    }
    public List<Review> getChatMessages(Chat chat) {
        entityManager = null;
        List<Review> messages = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            var query = entityManager.createQuery(
                    "SELECT r FROM Review r WHERE r.chat = :chat ORDER BY r.dateCreated",
                    Review.class
            );
            query.setParameter("chat", chat);
            messages = query.getResultList();
        } catch (Exception e) {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return messages;
    }
    public List<Chat> getRestaurantChats(Restaurant restaurant) {
        entityManager = null;
        List<Chat> chats = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            var query = entityManager.createQuery(
                    "SELECT c FROM Chat c WHERE c.foodOrder.restaurant = :restaurant",
                    Chat.class
            );
            query.setParameter("restaurant", restaurant);
            chats = query.getResultList();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while fetching chats");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return chats;
    }


}
