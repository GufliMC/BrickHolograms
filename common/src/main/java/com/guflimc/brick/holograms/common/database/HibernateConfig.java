package com.guflimc.brick.holograms.common.database;

public class HibernateConfig {

    public final String dsn;
    public final String driver;
    public final String username;
    public final String password;

    public HibernateConfig(String dsn, String driver, String username, String password) {
        this.dsn = dsn;
        this.driver = driver;
        this.username = username;
        this.password = password;
    }
}
