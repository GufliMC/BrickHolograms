package com.guflimc.brick.holograms.api.domain;

import com.guflimc.brick.holograms.api.meta.Position;

import java.util.UUID;

public interface Hologram {

    UUID id();

    String name();

    Position position();

    void setPosition(Position position);

}
