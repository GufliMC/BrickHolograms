package com.guflimc.brick.holograms.minestom.entity;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class HologramEntity extends Entity {

    private final ItemEntity itemEntity;
    private final Set<HologramTextEntity> textEntities = new CopyOnWriteArraySet<>();

    private final List<Component> text;

    public HologramEntity(Pos pos, ItemStack itemStack, List<Component> text) {
        super(EntityType.ARMOR_STAND);
        setInvisible(true);
        setNoGravity(true);

        this.position = pos;
        this.text = text;

        if (itemStack != null) {
            itemEntity = new ItemEntity(itemStack);
            itemEntity.setNoGravity(true);
            itemEntity.setPickable(false);
        } else {
            itemEntity = null;
        }
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        if ( itemEntity != null )
            itemEntity.setInstance(instance, spawnPosition);
        return super.setInstance(instance, spawnPosition);
    }

    @Override
    public void remove() {
        super.remove();

        if ( itemEntity != null )
            itemEntity.remove();

        textEntities.forEach(HologramTextEntity::remove);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        textEntities.forEach(HologramTextEntity::update);
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        Pos origin = getPosition();
        if (itemEntity != null) {
            origin = origin.add(0, 0.5, 0);
        }

        textEntities.add(new HologramTextEntity(player, origin, text));
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        textEntities.stream().filter(te -> te.player == player).toList()
                .forEach(te -> {
                    te.remove();
                    textEntities.remove(te);
                });
    }
}
