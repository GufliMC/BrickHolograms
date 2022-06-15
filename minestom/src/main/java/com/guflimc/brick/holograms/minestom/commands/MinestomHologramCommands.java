package com.guflimc.brick.holograms.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.minestom.MinestomBrickHologramManager;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.minestom.server.entity.Player;

@CommandContainer
public class MinestomHologramCommands {

    private final MinestomBrickHologramManager manager;

    public MinestomHologramCommands(MinestomBrickHologramManager manager) {
        this.manager = manager;
    }

    @CommandMethod("bh create <name>")
    public void create(Player sender, @Argument(value = "name") String name) {
        MinestomHologram hologram = manager.create(name);

        hologram.setPosition(new Position(sender.getPosition().x(), sender.getPosition().y(), sender.getPosition().z()));
        hologram.setInstance(sender.getInstance());

        manager.persist(hologram);

        I18nAPI.get(this).send(sender, "cmd.hologram.create", name);
    }

    @CommandMethod("bh tphere <hologram>")
    public void tphere(Player sender, @Argument(value = "hologram") Hologram hologram) {
        hologram.setPosition(new Position(sender.getPosition().x(), sender.getPosition().y(), sender.getPosition().z()));
        manager.merge(hologram);

        I18nAPI.get(this).send(sender, "cmd.hologram.tphere", hologram.name());
    }

}
