package com.guflimc.brick.holograms.common;

import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.common.domain.DHologramLine;
import com.guflimc.brick.orm.database.HibernateConfig;
import com.guflimc.brick.orm.database.HibernateDatabaseContext;

public class BrickHologramsDatabaseContext extends HibernateDatabaseContext {

    public BrickHologramsDatabaseContext(HibernateConfig config) {
        super(config);
    }

    public BrickHologramsDatabaseContext(HibernateConfig config, int poolSize) {
        super(config, poolSize);
    }

    @Override
    protected Class<?>[] entityClasses() {
        return new Class[]{
                DHologram.class,
                DHologramLine.class
        };
    }

}
