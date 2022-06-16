package com.guflimc.brick.holograms.minestom.api.domain;

import com.guflimc.brick.holograms.api.domain.Hologram;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public interface MinestomHologram extends Hologram {

    void setInstance(Instance instance);

    Instance instance();

    void remove();

    void setItem(ItemStack itemStack);

    void unsetItem();

}
