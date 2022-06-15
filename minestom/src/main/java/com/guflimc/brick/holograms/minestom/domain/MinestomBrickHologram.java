package com.guflimc.brick.holograms.minestom.domain;

import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import com.guflimc.brick.worlds.api.world.World;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.SharedInstance;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MinestomBrickHologram implements MinestomHologram {

    private final DHologram domainHologram;

    // entity code

    private Instance instance;
    private final Map<Player, PlayerMultiLineHologram> playerHolograms = new ConcurrentHashMap<>();
    private final EventNode<PlayerEvent> eventNode = EventNode.type(RandomStringUtils.randomAlphanumeric(16), EventFilter.PLAYER);

    public MinestomBrickHologram(DHologram domainHologram) {
        this.domainHologram = domainHologram;

        // show hologram to players when they spawn in the set instance
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            if (event.getSpawnInstance() != instance) {
                return;
            }

            refresh(event.getPlayer());
        });
    }

    //

    private void refresh(Player player) {
        if (playerHolograms.containsKey(player)) {
            playerHolograms.get(player).remove();
        }

        playerHolograms.put(player, new PlayerMultiLineHologram(player, this));
    }

    private void refresh() {
        if (instance == null) {
            return;
        }

        playerHolograms.keySet().forEach(this::refresh);

    }

    //

    public void tick() {
        playerHolograms.values().forEach(PlayerMultiLineHologram::update);
    }

    public DHologram domainHologram() {
        return domainHologram;
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        remove();

        this.instance = instance;

        MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .filter(p -> instance == p.getInstance())
                .forEach(this::refresh);

        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            if (instance instanceof World w) {
                domainHologram.setWorldName(w.info().name());
            } else if (instance instanceof SharedInstance si && si.getInstanceContainer() instanceof World w) {
                domainHologram.setWorldName(w.info().name());
            }
        }

        System.out.println("a");
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
        System.out.println("b");
    }

    @Override
    public Instance instance() {
        return instance;
    }

    @Override
    public void remove() {
        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);

        playerHolograms.values().forEach(PlayerMultiLineHologram::remove);
        playerHolograms.clear();
    }

    // PROXY

    @Override
    public UUID id() {
        return domainHologram.id();
    }

    @Override
    public String name() {
        return domainHologram.name();
    }

    @Override
    public Position position() {
        return domainHologram.position();
    }

    @Override
    public void setPosition(Position position) {
        domainHologram.setPosition(position);
        refresh();
    }

    @Override
    public List<Component> lines() {
        return domainHologram.lines();
    }

    @Override
    public void setLines(Collection<Component> lines) {
        domainHologram.setLines(lines);
        refresh();
    }

    @Override
    public void addLine(Component component) {
        domainHologram.addLine(component);
        refresh();
    }

    @Override
    public void setLine(int index, Component component) {
        domainHologram.setLine(index, component);
        refresh();
    }

    @Override
    public void removeLine(int index) {
        domainHologram.removeLine(index);
        refresh();
    }

    //

    public static class PlayerMultiLineHologram {

        private final Player player;
        private final List<Hologram> armorStands = new ArrayList<>();

        public PlayerMultiLineHologram(Player player, MinestomBrickHologram parent) {
            this.player = player;

            // spawn holograms
            Pos pos = new Pos(parent.position().x(), parent.position().y(), parent.position().z());
            List<Component> lines = parent.lines();
            for (int i = lines.size() - 1; i >= 0; i--) {
                armorStands.add(new Hologram(parent.instance, pos, lines.get(i)));
                pos = pos.add(0, 0.3, 0);
            }

            for (Hologram holo : armorStands) {
                holo.getEntity().setAutoViewable(false);
                holo.getEntity().addViewer(player);
            }

            // update with placeholders
            update();
        }

        private void remove() {
            armorStands.forEach(Hologram::remove);
        }

        private void update() {
            if (!MinecraftServer.getExtensionManager().hasExtension("brickplaceholders")) {
                return;
            }

            for (Hologram holo : armorStands) {
                holo.setText(MinestomPlaceholderAPI.get().replace(player, holo.getText()));
            }
        }

    }

}
