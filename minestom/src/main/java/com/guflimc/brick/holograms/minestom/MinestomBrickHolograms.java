package com.guflimc.brick.holograms.minestom;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.gson.Gson;
import com.guflimc.brick.holograms.api.domain.MultiLineTextHologram;
import com.guflimc.brick.holograms.common.BrickDatabaseContext;
import com.guflimc.brick.holograms.common.BrickHologramsConfig;
import com.guflimc.brick.holograms.minestom.api.MinestomHologramAPI;
import com.guflimc.brick.holograms.minestom.commands.MinestomHologramCommands;
import com.guflimc.brick.i18n.minestom.api.MinestomI18nAPI;
import com.guflimc.brick.i18n.minestom.api.namespace.MinestomNamespace;
import com.guflimc.cloud.minestom.MinestomCommandManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.function.Function;

public class MinestomBrickHolograms extends Extension {

    private static final Gson gson = new Gson();

    private BrickDatabaseContext databaseContext;
    private MinestomCommandManager<CommandSender> commandManager;

    @Override
    public void initialize() {
        getLogger().info("Enabling " + nameAndVersion() + ".");

        // load config
        BrickHologramsConfig config;
        try (
                InputStream is = getResource("config.json");
                InputStreamReader isr = new InputStreamReader(is)
        ) {
            config = gson.fromJson(isr, BrickHologramsConfig.class);
        } catch (IOException e) {
            getLogger().error("Cannot load configuration.", e);
            return;
        }

        // DATABASE
        databaseContext = new BrickDatabaseContext(config.database);

        MinestomBrickHologramManager manager = new MinestomBrickHologramManager(databaseContext);
        MinestomHologramAPI.registerManager(manager);

        // TRANSLATIONS
        MinestomNamespace namespace = new MinestomNamespace(this, Locale.ENGLISH);
        namespace.loadValues(this, "languages");
        MinestomI18nAPI.get().register(namespace);

        // COMMAND MANAGER
        commandManager = new MinestomCommandManager<>(
                CommandExecutionCoordinator.simpleCoordinator(),
                Function.identity(),
                Function.identity()
        );

//        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(PersistentCreature.class), parserParameters ->
//                new PersistentCreatureArgument.CreatureParser<>());

        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                parameters -> SimpleCommandMeta.empty()
        );

        annotationParser.parse(new MinestomHologramCommands(manager));

        getLogger().info("Enabled " + nameAndVersion() + ".");
    }

    @Override
    public void terminate() {

        getLogger().info("Disabled " + nameAndVersion() + ".");
    }

    private String nameAndVersion() {
        return getOrigin().getName() + " v" + getOrigin().getVersion();
    }


    public static void main(String[] args) {
        MultiLineTextHologram hologram = MinestomHologramAPI.get().create();
        hologram.addLine(Component.text("hey"));
    }
}
