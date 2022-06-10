package com.guflimc.brick.holograms.api;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.api.domain.MultiLineTextHologram;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface HologramManager {

    Collection<Hologram> holograms();

    Optional<Hologram> find(String name);

    MultiLineTextHologram create();

    MultiLineTextHologram create(@NotNull String name);


    CompletableFuture<Void> persist(@NotNull Hologram hologram);

    CompletableFuture<Void> remove(@NotNull Hologram hologram);

    CompletableFuture<Void> merge(@NotNull Hologram hologram);

}
