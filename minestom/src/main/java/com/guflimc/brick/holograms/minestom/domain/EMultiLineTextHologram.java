package com.guflimc.brick.holograms.minestom.domain;

import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.common.domain.DMultiLineTextHologram;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomMultiLineTextHologram;
import jakarta.persistence.Entity;
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
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Entity
public class EMultiLineTextHologram extends DMultiLineTextHologram implements MinestomMultiLineTextHologram {

    private Instance instance;
    private final Map<Player, PlayerMultiLineHologram> playerHolograms = new ConcurrentHashMap<>();

    private final EventNode<PlayerEvent> eventNode = EventNode.type(RandomStringUtils.randomAlphanumeric(16), EventFilter.PLAYER);

    public EMultiLineTextHologram(String name) {
        super(name);

        // show hologram to players when they spawn in the set instance
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            if ( event.getSpawnInstance() != instance ) {
                return;
            }

            refresh(event.getPlayer());
        });
    }

    private void refresh(Player player) {
        if ( playerHolograms.containsKey(player) ) {
            playerHolograms.get(player).remove();
        }

        playerHolograms.put(player, new PlayerMultiLineHologram(player, this));
    }

    public void tick() {
        playerHolograms.values().forEach(PlayerMultiLineHologram::update);
    }

    private void refresh() {
        if ( instance == null ) {
            return;
        }

        playerHolograms.keySet().forEach(p -> {
            playerHolograms.get(p).remove();
            playerHolograms.put(p, new PlayerMultiLineHologram(p, this));
        });


        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(this::refresh);

        // TODO it is stuck here:
//        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }

    @Override
    public void setInstance(Instance instance) {
        this.instance = instance;
        refresh();
    }

    @Override
    public void remove() {
        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);

        playerHolograms.values().forEach(PlayerMultiLineHologram::remove);
        playerHolograms.clear();
    }

    @Override
    public void setPosition(Position position) {
        super.setPosition(position);
        refresh();
    }

    @Override
    public void setLines(Collection<Component> lines) {
        super.setLines(lines);
        refresh();
    }

    @Override
    public void addLine(Component component) {
        super.addLine(component);
        refresh();
    }

    @Override
    public void setLine(int index, Component component) {
        super.setLine(index, component);
        refresh();
    }

    @Override
    public void removeLine(int index) {
        super.removeLine(index);
        refresh();
    }

    //

    public static class PlayerMultiLineHologram {

        private final Player player;
        private final List<Hologram> armorStands = new ArrayList<>();

        public PlayerMultiLineHologram(Player player, EMultiLineTextHologram parent) {
            this.player = player;

            // spawn holograms
            Pos pos = new Pos(parent.position().x(), parent.position().y(), parent.position().z());
            List<Component> lines = parent.lines();
            for ( int i = lines.size() - 1; i >= 0; i-- ) {
                armorStands.add(new Hologram(parent.instance, pos, lines.get(i)));
                pos = pos.add(0, 0.3, 0);
            }

            for ( Hologram holo : armorStands ) {
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
            for ( Hologram holo : armorStands ) {
                // TODO replace
            }
        }

    }

}
