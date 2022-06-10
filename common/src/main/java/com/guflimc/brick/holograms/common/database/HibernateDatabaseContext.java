package com.guflimc.brick.holograms.common.database;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class HibernateDatabaseContext {

    private final SessionFactory sessionFactory;

    public HibernateDatabaseContext(HibernateConfig config) {
        this(config, 5);
    }

    public HibernateDatabaseContext(HibernateConfig config, int poolSize) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.url", config.dsn);
        properties.setProperty("hibernate.connection.username", config.username);
        properties.setProperty("hibernate.connection.password", config.password);

        if ( config.driver != null ) {
            properties.setProperty("hibernate.connection.driver_class", config.driver);
        }

        properties.setProperty("hibernate.connection.pool_size", poolSize + "");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);

        // register classes
        Arrays.stream(entityClasses()).forEach(configuration::addAnnotatedClass);

        this.sessionFactory = configuration.buildSessionFactory();
    }

    protected abstract Class<?>[] entityClasses();

    public final SessionFactory sessionFactory() {
        return sessionFactory;
    }

    public final CompletableFuture<Void> async(Consumer<Session> consumer) {
        return CompletableFuture.runAsync(() -> {
            try (
                    Session session = sessionFactory.openSession();
            ) {
                Transaction tx = session.beginTransaction();
                consumer.accept(session);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public final <T> CompletableFuture<T> findAsync(Class<T> entityType, Object id) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    Session session = sessionFactory.getCurrentSession();
            ) {
                return session.find(entityType, id);
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public final CompletableFuture<Void> persistAsync(Object object) {
        return async((session -> session.persist(object)));
    }

    public final CompletableFuture<Void> mergeAsync(Object object) {
        return async((session -> session.merge(object)));
    }

    public final CompletableFuture<Void> removeAsync(Object object) {
        return async((session -> session.remove(object)));
    }

    public final void queryBuilder(BiConsumer<Session, CriteriaBuilder> consumer) {
        try (
                Session session = sessionFactory.openSession();
        ) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            consumer.accept(session, cb);
        }
    }

}
