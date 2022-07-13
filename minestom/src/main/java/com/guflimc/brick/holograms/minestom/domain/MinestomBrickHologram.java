package com.guflimc.brick.holograms.minestom.domain;

import com.guflimc.brick.holograms.common.domain.DHologram;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.holograms.minestom.entity.HologramEntity;
import com.guflimc.brick.maths.api.geo.Location;
import com.guflimc.brick.maths.minestom.api.MinestomMaths;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTException;
import org.jglrxavpok.hephaistos.parser.SNBTParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class MinestomBrickHologram implements MinestomHologram {

    private final Logger logger = LoggerFactory.getLogger(MinestomBrickHologram.class);

    private DHologram domainHologram;
    private HologramEntity entity;

    public MinestomBrickHologram(@NotNull DHologram domainHologram) {
        this.domainHologram = domainHologram;
        refresh();
    }

    //

    public void refresh() {
        try {
            if (entity != null) {
                entity.remove();
            }

            ItemStack itemStack = null;
            if (domainHologram.itemSerialized() != null) {
                try {
                    itemStack = ItemStack.fromItemNBT((NBTCompound) new SNBTParser(new StringReader(domainHologram.itemSerialized())).parse());
                } catch (NBTException e) {
                    logger.warn("Failed to parse item nbt for hologram '{}'", domainHologram.name() == null ? domainHologram.id().toString() : domainHologram.name());
                }
            }

            entity = new HologramEntity(MinestomMaths.toPos(domainHologram.location()), itemStack, domainHologram.lines());

            String worldId = domainHologram.location().worldId();
            if (worldId == null) {
                return;
            }

            Instance instance = MinecraftServer.getInstanceManager().getInstance(UUID.fromString(worldId));
            if (instance != null) {
                entity.setInstance(instance);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //

    public HologramEntity entity() {
        return entity;
    }

    public DHologram domainHologram() {
        return domainHologram;
    }

    public void setDomainHologram(@NotNull DHologram domainHologram) {
        this.domainHologram = domainHologram;
    }

    @Override
    public void despawn() {
        if (entity != null)
            entity.remove();
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
    public Location location() {
        return domainHologram.location();
    }

    @Override
    public void setLocation(Location position) {
        domainHologram.setLocation(position);
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

}
