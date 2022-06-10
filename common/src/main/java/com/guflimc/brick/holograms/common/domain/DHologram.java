package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.common.converters.PositionConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@MappedSuperclass
public abstract class DHologram implements Hologram {

    @Id
    @Basic
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Convert(converter = PositionConverter.class)
    @Column(nullable = false)
    private Position position;

    //

    public DHologram() {}

    public DHologram(String name) {
        this.name = name;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

}
