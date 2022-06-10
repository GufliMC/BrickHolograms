package com.guflimc.brick.holograms.api.domain;

import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;

public interface MultiLineTextHologram extends Hologram {

    List<Component> lines();

    void setLines(Collection<Component> lines);

    void addLine(Component component);

    void setLine(int index, Component component);

    void removeLine(int index);

}
