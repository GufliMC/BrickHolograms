package com.guflimc.brick.holograms.minestom.domain;

import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import com.guflimc.brick.worlds.api.world.World;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.item.ItemStack;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MinestomBrickHologram implements MinestomHologram {

    private final Logger logger = LoggerFactory.getLogger(MinestomBrickHologram.class);

    private DHologram domainHologram;

    // entity code

    private final EventNode<Event> eventNode = EventNode.type(RandomStringUtils.randomAlphanumeric(16), EventFilter.ALL);

    private Instance instance;

    private final Map<Player, PlayerMultiLineHologram> playerHolograms = new ConcurrentHashMap<>();
    private Entity itemEntity;

    public MinestomBrickHologram(@NotNull DHologram domainHologram) {
        this.domainHologram = domainHologram;

        // show hologram to players when they spawn in the set instance
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            if (event.getSpawnInstance() != instance) {
                return;
            }

            playerHolograms.put(event.getPlayer(), new PlayerMultiLineHologram(event.getPlayer(), this));
        });

        // cleanup on player quit
        eventNode.addListener(PlayerDisconnectEvent.class, event -> {
            if ( playerHolograms.containsKey(event.getPlayer()) ) {
                playerHolograms.get(event.getPlayer()).remove();
                playerHolograms.remove(event.getPlayer());
            }
        });

        // do not allow item pickup
        eventNode.addListener(PickupItemEvent.class, e -> {
            if ( e.getItemEntity().equals(itemEntity) ) {
                e.setCancelled(true);
            }
        });
    }

    //

    private void refresh() {
        // remove old
        clear();

        // spawn item entity
        if ( domainHologram.itemSerialized() != null ) {
            try {
                ItemStack itemStack = ItemStack.fromItemNBT((NBTCompound) new SNBTParser(new StringReader(domainHologram.itemSerialized())).parse());
                itemEntity = new ItemEntity(itemStack);
                itemEntity.setNoGravity(true);
                itemEntity.setInstance(instance, new Pos(domainHologram.position().x(), domainHologram.position().y(), domainHologram.position().z()));
            } catch (NBTException e) {
                logger.warn("Failed to parse item nbt for hologram '{}'", domainHologram.name() == null ? domainHologram.id().toString() : domainHologram.name());
            }
        }

        // update for all players in this instance
        MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .filter(p -> p.getInstance() == instance)
                .forEach(p -> playerHolograms.put(p, new PlayerMultiLineHologram(p, this)));
    }

    private void clear() {
        playerHolograms.values().forEach(PlayerMultiLineHologram::remove);
        playerHolograms.clear();

        if ( itemEntity != null ) {
            itemEntity.remove();
        }
    }

    //

    public void tick() {
        playerHolograms.values().forEach(PlayerMultiLineHologram::update);
    }

    public DHologram domainHologram() {
        return domainHologram;
    }

    public void setDomainHologram(@NotNull DHologram domainHologram) {
        this.domainHologram = domainHologram;
    }

    @Override
    public void setInstance(@NotNull Instance instance) {
        if ( instance == this.instance ) {
            return;
        }

        if (MinecraftServer.getExtensionManager().hasExtension("brickworlds")) {
            if (instance instanceof World w) {
                domainHologram.setWorldName(w.info().name());
            } else if (instance instanceof SharedInstance si && si.getInstanceContainer() instanceof World w) {
                domainHologram.setWorldName(w.info().name());
            }
        }

        this.instance = instance;
        refresh();

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }

    @Override
    public Instance instance() {
        return instance;
    }

    @Override
    public void remove() {
        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);
        clear();
    }

    @Override
    public void setItem(@NotNull ItemStack itemStack) {
        domainHologram.setItemSerialized(itemStack.toItemNBT().toSNBT());
        refresh();
    }

    @Override
    public void unsetItem() {
        domainHologram.setItemSerialized(null);
        refresh();
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
            if ( parent.itemEntity != null ) {
                pos = pos.add(0, 0.5, 0); // item height
            }

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
