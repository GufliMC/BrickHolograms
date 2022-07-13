package com.guflimc.brick.holograms.minestom.entity;

import com.guflimc.brick.placeholders.minestom.api.MinestomPlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;

import java.util.ArrayList;
import java.util.List;

public class HologramTextEntity {

    public final Player player;
    private final List<Hologram> armorStands = new ArrayList<>();

    public HologramTextEntity(Player player, Pos origin, List<Component> lines) {
        this.player = player;

        for (int i = lines.size() - 1; i >= 0; i--) {
            armorStands.add(new Hologram(player.getInstance(), origin, lines.get(i)));
            origin = origin.add(0, 0.3, 0);
        }

        for (Hologram holo : armorStands) {
            holo.getEntity().setNoGravity(true);
            holo.getEntity().setAutoViewable(false);
            holo.getEntity().addViewer(player);
        }

        update();
    }

    void remove() {
        armorStands.forEach(Hologram::remove);
    }

    public void update() {
        if (!MinecraftServer.getExtensionManager().hasExtension("brickplaceholders")) {
            return;
        }

        for (Hologram holo : armorStands) {
            holo.setText(MinestomPlaceholderAPI.get().replace(player, holo.getText()));
        }
    }

}
