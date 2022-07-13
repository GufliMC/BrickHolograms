package com.guflimc.brick.holograms.common.domain;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.maths.api.geo.Location;
import com.guflimc.brick.maths.database.api.LocationConverter;
import jakarta.persistence.*;
import net.kyori.adventure.text.Component;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "holograms")
public class DHologram implements Hologram {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Convert(converter = LocationConverter.class)
    @Column(nullable = false)
    private Location location = new Location(null, 0, 0, 0, 0, 0);

    @OneToMany(targetEntity = DHologramLine.class, mappedBy = "hologram",
            orphanRemoval = true, fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OrderColumn(name = "line_index")
    private List<DHologramLine> lines = new ArrayList<>();

    @Column(name = "item_serialized")
    private String itemSerialized;

    //

    public DHologram() {
    }

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
    public Location location() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public List<Component> lines() {
        return lines.stream().map(DHologramLine::text)
                .collect(Collectors.toList());
    }

    @Override
    public void setLines(Collection<Component> lines) {
        this.lines.clear();
        this.lines.addAll(lines.stream().map(line -> new DHologramLine(this, line)).toList());
    }

    @Override
    public void addLine(Component component) {
        lines.add(new DHologramLine(this, component));
    }

    @Override
    public void setLine(int index, Component component) {
        lines.set(index, new DHologramLine(this, component));
    }

    @Override
    public void removeLine(int index) {
        lines.remove(index);
    }

    public void setItemSerialized(String itemSerialized) {
        this.itemSerialized = itemSerialized;
    }

    public String itemSerialized() {
        return itemSerialized;
    }
}
