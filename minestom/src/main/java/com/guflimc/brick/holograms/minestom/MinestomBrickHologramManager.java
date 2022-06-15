package com.guflimc.brick.holograms.minestom;

import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.common.BrickDatabaseContext;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class MinestomBrickHologramManager implements MinestomHologramManager {

    private final BrickDatabaseContext databaseContext;

    private final Set<MinestomBrickHologram> holograms = new CopyOnWriteArraySet<>();

    public MinestomBrickHologramManager(BrickDatabaseContext databaseContext) {
        this.databaseContext = databaseContext;

        databaseContext.queryBuilder((session, cb) -> {

            CriteriaQuery<DHologram> creatureQuery = cb.createQuery(DHologram.class);
            Root<DHologram> creatureRoot = creatureQuery.from(DHologram.class);
            creatureQuery = creatureQuery.select(creatureRoot);

            TypedQuery<DHologram> creatureAllQuery = session.createQuery(creatureQuery);
            creatureAllQuery.getResultList().stream()
                    .map(MinestomBrickHologram::new)
                    .forEach(holograms::add);
        });

        MinecraftServer.getSchedulerManager().scheduleTask(() -> holograms.forEach(MinestomBrickHologram::tick),
                TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            MinestomWorldAPI.get().loadedWorlds().forEach(this::load);
            MinecraftServer.getGlobalEventHandler().addListener(WorldLoadEvent.class, e -> load(e.world()));
        }
    }

    private void load(World world) {
        System.out.println("Loading holograms for world '" + world.info().name() + "'");
        holograms.stream().filter(h -> h.instance() == null)
                .filter(h -> h.domainHologram().worldName() != null)
                .filter(h -> h.domainHologram().worldName().equals(world.info().name()))
                .forEach(h -> {
                    h.setInstance(((MinestomWorld) world).asInstance());
                    System.out.println("Loaded hologram '" + h.name() + "'");
                });
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
        return databaseContext.mergeAsync(((MinestomBrickHologram) hologram).domainHologram());
    }

}
