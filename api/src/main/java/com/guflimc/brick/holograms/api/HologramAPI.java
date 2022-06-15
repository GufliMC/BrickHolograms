package com.guflimc.brick.holograms.api;

import org.jetbrains.annotations.ApiStatus;

public class HologramAPI {

    private HologramAPI() {}

    private static HologramManager<?> hologramManager;

    @ApiStatus.Internal
    public static void setHologramManager(HologramManager<?> manager) {
        hologramManager = manager;
    }

    //

    public static HologramManager<?> get() {
        return hologramManager;
    }
    
}
