package com.guflimc.brick.holograms.minestom;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.api.domain.MultiLineTextHologram;
import com.guflimc.brick.holograms.common.BrickDatabaseContext;
import com.guflimc.brick.holograms.common.domain.DMultiLineTextHologram;
import com.guflimc.brick.holograms.minestom.api.MinestomHologramManager;
import com.guflimc.brick.holograms.minestom.domain.EMultiLineTextHologram;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomBrickHologramManager implements MinestomHologramManager {

    private final BrickDatabaseContext databaseContext;

    private final Set<Hologram> holograms = new CopyOnWriteArraySet<>();

    public MinestomBrickHologramManager(BrickDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        databaseContext.queryBuilder((session, cb) -> {
            // creatures
            CriteriaQuery<DMultiLineTextHologram> creatureQuery = cb.createQuery(DMultiLineTextHologram.class);
            Root<DMultiLineTextHologram> creatureRoot = creatureQuery.from(DMultiLineTextHologram.class);
            creatureQuery = creatureQuery.select(creatureRoot);

            TypedQuery<DMultiLineTextHologram> creatureAllQuery = session.createQuery(creatureQuery);
            holograms.addAll(creatureAllQuery.getResultList());
        });
    }


    @Override
    public Collection<Hologram> holograms() {
        return Collections.unmodifiableSet(holograms);
    }

    @Override
    public Optional<Hologram> find(String name) {
        return holograms.stream().filter(holo -> holo.name().equals(name)).findFirst();
    }

    @Override
    public MultiLineTextHologram create() {
        EMultiLineTextHologram hologram = new EMultiLineTextHologram(null);
        holograms.add(hologram);
        return hologram;
    }

    @Override
    public MultiLineTextHologram create(@NotNull String name) {
        EMultiLineTextHologram hologram = new EMultiLineTextHologram(name);
        holograms.add(hologram);
        return hologram;
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Hologram hologram) {
        return databaseContext.persistAsync(hologram);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Hologram hologram) {
        this.holograms.remove(hologram);
        return databaseContext.removeAsync(hologram);
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull Hologram hologram) {
        return databaseContext.mergeAsync(hologram);
    }

}
