package com.guflimc.brick.holograms.minestom.api.domain;

import com.guflimc.brick.holograms.api.domain.Hologram;
import net.minestom.server.instance.Instance;

public interface MinestomHologram extends Hologram {

    void setInstance(Instance instance);

    void remove();

}
