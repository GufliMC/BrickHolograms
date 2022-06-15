package com.guflimc.brick.holograms.api.domain;

import com.guflimc.brick.holograms.api.meta.Position;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface Hologram {

    UUID id();

    String name();

    Position position();

    void setPosition(Position position);

    List<Component> lines();

    void setLines(Collection<Component> lines);

    void addLine(Component component);

    void setLine(int index, Component component);

    void removeLine(int index);

}
