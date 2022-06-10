package com.guflimc.brick.holograms.common;

import com.guflimc.brick.holograms.common.database.HibernateConfig;
import com.guflimc.brick.holograms.common.database.HibernateDatabaseContext;
import com.guflimc.brick.holograms.common.domain.DMultiLineTextHologram;
import com.guflimc.brick.holograms.common.domain.DMultiLineTextHologramLine;

public class BrickDatabaseContext extends HibernateDatabaseContext {

    public BrickDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public BrickDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[] {
                DMultiLineTextHologram.class,
                DMultiLineTextHologramLine.class
        };
    }

}
