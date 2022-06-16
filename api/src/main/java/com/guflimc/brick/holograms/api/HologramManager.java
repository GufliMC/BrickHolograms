package com.guflimc.brick.holograms.api;

import com.guflimc.brick.holograms.api.domain.Hologram;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface HologramManager<T extends Hologram> {

    void reload();

    Collection<T> holograms();

    Optional<T> find(String name);

    Hologram create();

    Hologram create(@NotNull String name);

    CompletableFuture<Void> persist(@NotNull Hologram hologram);

    CompletableFuture<Void> remove(@NotNull Hologram hologram);

    CompletableFuture<Void> merge(@NotNull Hologram hologram);

}
