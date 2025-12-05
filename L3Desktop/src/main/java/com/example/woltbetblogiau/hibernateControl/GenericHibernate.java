package com.example.woltbetblogiau.hibernateControl;

import com.example.woltbetblogiau.fxControllers.FxUtils;
import com.example.woltbetblogiau.model.Cuisine;
import com.example.woltbetblogiau.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class GenericHibernate {
    protected final EntityManagerFactory entityManagerFactory;
    protected EntityManager entityManager;

    public GenericHibernate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public <T> void create(T entity) {
        entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity); //INSERT
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if(entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "During CREATE", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    public <T> void update(T entity) {
        entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity); //UPDATE
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if(entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "During UPDATE", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    public <T> T getEntityById(Class<T> entityClass, int id) {
        entityManager = null;
        T entity = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            entity = entityManager.find(entityClass, id);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "During READ", e);
            return null;
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return entity;
    }

    //Gali buti blogai su detached entity
    public <T> void delete(Class<T> entityClass, int id) {
        entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            T entity = entityManager.find(entityClass, id);
            entityManager.remove(entity); //DELETE
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if(entityManager != null && entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "During DELETE", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
    }

    public <T> List<T> getAllRecords(Class<T> entityClass) {
        entityManager = null;
        List<T> list = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(entityClass);
            query.select(query.from(entityClass));
            Query q = entityManager.createQuery(query);
            list = q.getResultList();
        } catch (Exception e) {
            FxUtils.generateDialogAlert(Alert.AlertType.ERROR, "During READ ALL", e);
        } finally {
            if (entityManager != null && entityManager.isOpen()) entityManager.close();
        }
        return list;
    }


}
