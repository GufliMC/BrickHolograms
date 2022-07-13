package com.guflimc.brick.holograms.api.domain;

import com.guflimc.brick.maths.api.geo.Location;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface Hologram {

    UUID id();

    String name();

    Location location();

    void setLocation(Location position);

    List<Component> lines();

    void setLines(Collection<Component> lines);

    void addLine(Component component);

    void setLine(int index, Component component);

    void removeLine(int index);

}
