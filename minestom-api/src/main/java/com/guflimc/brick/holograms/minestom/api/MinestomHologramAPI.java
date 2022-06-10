package com.guflimc.brick.holograms.minestom.api;

import org.jetbrains.annotations.ApiStatus;

public class MinestomHologramAPI {

    private static MinestomHologramManager creatureManager;

    @ApiStatus.Internal
    public static void registerManager(MinestomHologramManager manager) {
        creatureManager = manager;
    }

    //

    public static MinestomHologramManager get() {
        return creatureManager;
    }

}
