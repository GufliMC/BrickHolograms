package com.guflimc.brick.holograms.minestom;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.common.BrickHologramsDatabaseContext;
import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.minestom.api.MinestomHologramManager;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.holograms.minestom.domain.MinestomBrickHologram;
import com.guflimc.brick.worlds.api.world.World;
import com.guflimc.brick.worlds.minestom.api.MinestomWorldAPI;
import com.guflimc.brick.worlds.minestom.api.event.WorldLoadEvent;
import com.guflimc.brick.worlds.minestom.api.world.MinestomWorld;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomBrickHologramManager implements MinestomHologramManager {

    private final Logger logger = LoggerFactory.getLogger(MinestomBrickHologramManager.class);

    private final BrickHologramsDatabaseContext databaseContext;

    private final Set<MinestomBrickHologram> holograms = new CopyOnWriteArraySet<>();

    public MinestomBrickHologramManager(BrickHologramsDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        // hologram tick lifecycle
        MinecraftServer.getSchedulerManager().scheduleTask(() -> holograms.forEach(MinestomBrickHologram::tick),
                TaskSchedule.tick(1), TaskSchedule.tick(1));

        // spawn holograms that are assigned to a world when a world is loaded
        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            MinecraftServer.getGlobalEventHandler().addListener(WorldLoadEvent.class, e -> load(e.world()));
        }

        reload();
    }

    private void load(World world) {
        logger.info("Loading holograms for world '{}'.", world.info().name());
        holograms.stream().filter(h -> h.instance() == null)
                .filter(h -> h.domainHologram().worldName() != null)
                .filter(h -> h.domainHologram().worldName().equals(world.info().name()))
                .forEach(h -> {
                    h.setInstance(((MinestomWorld) world).asInstance());
                });
    }

    @Override
    public void reload() {
        // load holograms from database
        databaseContext.findAllAsync(DHologram.class).join().stream()
                .map(MinestomBrickHologram::new)
                .forEach(holo -> {
                    // remove old one with same id
                    holograms.stream().filter(h -> h.id().equals(holo.id())).toList().forEach(h -> {
                        h.remove();
                        holograms.remove(h);
                    });

                    holograms.add(holo);
                });

        // set instance of holograms that are assigned to a world
        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            MinestomWorldAPI.get().loadedWorlds().forEach(this::load);
        }
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
        holo.remove();

        this.holograms.remove(holo);
        return databaseContext.removeAsync(holo.domainHologram());
    }

    @Override
    public CompletableFuture<Void> merge(@NotNull Hologram hologram) {
        MinestomBrickHologram bholo = (MinestomBrickHologram) hologram;
        return databaseContext.mergeAsync(bholo.domainHologram()).thenAccept(bholo::setDomainHologram);
    }

}
