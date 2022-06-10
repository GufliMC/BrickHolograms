package com.guflimc.brick.holograms.minestom.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.guflimc.brick.holograms.api.domain.Hologram;
import com.guflimc.brick.holograms.api.domain.MultiLineTextHologram;
import com.guflimc.brick.holograms.api.meta.Position;
import com.guflimc.brick.holograms.minestom.MinestomBrickHologramManager;
import com.guflimc.brick.holograms.minestom.api.MinestomHologramManager;
import com.guflimc.brick.holograms.minestom.api.domain.MinestomHologram;
import com.guflimc.brick.i18n.api.I18nAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MinestomHologramCommands {

    private final MinestomBrickHologramManager manager;

    public MinestomHologramCommands(MinestomBrickHologramManager manager) {
        this.manager = manager;
    }

    @Suggestions("hologram")
    public List<String> creatureSuggestion(CommandContext<Audience> sender, String input) {
        return manager.holograms().stream()
                .map(Hologram::name)
//                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }

    @CommandMethod("bh create text <name>")
    public void createHologram(Player sender, @Argument(value = "name") String name) {
        MinestomHologram hologram = (MinestomHologram) manager.create(name);
        hologram.setPosition(new Position(sender.getPosition().x(), sender.getPosition().y(), sender.getPosition().z()));
        hologram.setInstance(sender.getInstance());

        System.out.println(hologram.name());
        manager.persist(hologram);

        I18nAPI.get(this).send(sender, "cmd.hologram.create", name);
    }

    @CommandMethod("bh edit text add <name> <line>")
    public void editTextAdd(Audience sender,
                                 @Argument(value = "name", suggestions = "hologram") String name,
                                 @Argument(value = "line") String line) {
        Optional<Hologram> hologram = manager.find(name);
        if ( hologram.isEmpty() || !(hologram.get() instanceof MultiLineTextHologram mlth) ) {
            I18nAPI.get(this).send(sender, "cmd.error.args.hologram", name);
            return;
        }

        Component text = MiniMessage.miniMessage().deserialize(line);
        mlth.addLine(text);
        manager.merge(mlth);

        I18nAPI.get(this).send(sender, "cmd.hologram.edit.text.add", text, mlth.name());
    }
}
