package com.guflimc.brick.holograms.minestom;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.common.BrickHologramsDatabaseContext;
import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.minestom.api.MinestomHologramManager;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.holograms.minestom.domain.MinestomBrickHologram;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomBrickHologramManager implements MinestomHologramManager {

    private final Logger logger = LoggerFactory.getLogger(MinestomBrickHologramManager.class);

    private final BrickHologramsDatabaseContext databaseContext;

    private final Set<MinestomBrickHologram> holograms = new CopyOnWriteArraySet<>();
    private final Set<UUID> loadedInstances = new CopyOnWriteArraySet<>();

    public MinestomBrickHologramManager(BrickHologramsDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        // instance load
        MinecraftServer.getGlobalEventHandler().addListener(InstanceChunkLoadEvent.class, e -> load(e.getInstance()));

        reload();
    }

    private void load(Instance instance) {
        if ( loadedInstances.contains(instance.getUniqueId()) ) {
            return;
        }

        logger.info("Loading holograms for instance '{}'.", instance.getUniqueId());
        holograms.stream()
                .filter(h -> h.entity().getInstance() != null)
                .filter(h -> instance.getUniqueId().toString().equals(h.location().worldId()))
                .forEach(MinestomBrickHologram::refresh);
        loadedInstances.add(instance.getUniqueId());
    }

    @Override
    public void reload() {
        // load holograms from database
        databaseContext.findAllAsync(DHologram.class).join().stream()
                .map(MinestomBrickHologram::new)
                .forEach(holo -> {
                    // remove old one with same id
                    holograms.stream().filter(h -> h.id().equals(holo.id())).toList().forEach(h -> {
                        h.despawn();
                        holograms.remove(h);
                    });

                    holograms.add(holo);
                });

        // spawn instances
        MinecraftServer.getInstanceManager().getInstances().forEach(this::load);
    }

    @Override
    public Collection<MinestomHologram> holograms() {
        return Collections.unmodifiableSet(holograms);
    }

    @Override
    public Optional<MinestomHologram> find(String name) {
        return holograms.stream()
                .filter(holo -> holo.name().equals(name))
                .map(e -> (MinestomHologram) e)
                .findFirst();
    }

    @Override
    public MinestomHologram create() {
        MinestomBrickHologram hologram = new MinestomBrickHologram(new DHologram());
        holograms.add(hologram);
        return hologram;
    }

    @Override
    public MinestomHologram create(@NotNull String name) {
        MinestomBrickHologram hologram = new MinestomBrickHologram(new DHologram(name));
        holograms.add(hologram);
        return hologram;
    }

    @Override
    public CompletableFuture<Void> persist(@NotNull Hologram hologram) {
        return databaseContext.persistAsync(((MinestomBrickHologram) hologram).domainHologram());
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull Hologram hologram) {
        MinestomBrickHologram holo = (MinestomBrickHologram) hologram;
        holo.despawn();

        this.holograms.remove(holo);
        return databaseContext.removeAsync(holo.domainHologram());
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull Hologram hologram) {
        MinestomBrickHologram bholo = (MinestomBrickHologram) hologram;
        return databaseContext.mergeAsync(bholo.domainHologram()).thenAccept(bholo::setDomainHologram);
    }

}
