package com.guflimc.brick.holograms.minestom.api.domain;

import com.guflimc.brick.holograms.api.domain.Hologram;
import net.minestom.server.item.ItemStack;

public interface MinestomHologram extends Hologram {

    void despawn();

    void setItem(ItemStack itemStack);

    void unsetItem();

}
