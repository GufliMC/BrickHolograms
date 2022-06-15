package com.guflimc.brick.holograms.minestom.api;

import com.guflimc.brick.holograms.api.HologramAPI;
import org.jetbrains.annotations.ApiStatus;

public class MinestomHologramAPI {

    private MinestomHologramAPI() {}

    private static MinestomHologramManager creatureManager;

    @ApiStatus.Internal
    public static void setHologramManager(MinestomHologramManager manager) {
        HologramAPI.setHologramManager(manager);
        creatureManager = manager;
    }

    //

    public static MinestomHologramManager get() {
        return creatureManager;
    }

}
