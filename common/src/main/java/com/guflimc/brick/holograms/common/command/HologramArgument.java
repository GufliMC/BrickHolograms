package com.guflimc.brick.holograms.common.command;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.guflimc.brick.holograms.api.HologramAPI;
import com.guflimc.brick.holograms.api.domain.Hologram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

public final class HologramArgument<C> extends CommandArgument<C, Hologram> {

    private HologramArgument(
            final boolean required,
            final @NotNull String name,
            final @NotNull String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String,
                    List<String>> suggestionsProvider
    ) {
        super(required, name, new HologramParser<>(), defaultValue, Hologram.class, suggestionsProvider);
    }

    public static final class HologramParser<C> implements ArgumentParser<C, Hologram> {

        @Override
        public @NotNull ArgumentParseResult<Hologram> parse(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull Queue<String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        HologramParser.class,
                        commandContext
                ));
            }
            inputQueue.remove();

            Hologram hologram = HologramAPI.get().find(input).orElse(null);

            if (hologram == null) {
                return ArgumentParseResult.failure(new HologramParseException(input, commandContext));
            }

            return ArgumentParseResult.success(hologram);
        }

        @Override
        public @NotNull List<String> suggestions(
                final @NotNull CommandContext<C> commandContext,
                final @NotNull String input
        ) {
            List<String> output = new ArrayList<>();

            HologramAPI.get().holograms().stream().filter(holo -> holo.name() != null)
                    .forEach(holo -> output.add(holo.name()));

            return output;
        }

    }

    public static final class HologramParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2563079642852029296L;

        private final String input;

        public HologramParseException(
                final @NotNull String input,
                final @NotNull CommandContext<?> context
        ) {
            super(
                    HologramArgument.HologramParser.class,
                    context,
                    Caption.of("cmd.args.error.hologram"),
                    CaptionVariable.of("0", input)
            );
            this.input = input;
        }

        public @NotNull String getInput() {
            return input;
        }

    }

}