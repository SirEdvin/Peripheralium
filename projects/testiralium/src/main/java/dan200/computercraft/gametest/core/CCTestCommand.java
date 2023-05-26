// SPDX-FileCopyrightText: 2021 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package dan200.computercraft.gametest.core;

import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * Helper commands for importing/exporting the computer directory.
 */
public class CCTestCommand {
    public static final LevelResource LOCATION = new LevelResource(ComputerCraftAPI.MOD_ID);

    public static void importFiles(MinecraftServer server) {
        try {
            Copier.replicate(getSourceComputerPath(), getWorldComputerPath(server));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void exportFiles(MinecraftServer server) {
        try {
            Copier.replicate(getWorldComputerPath(server), getSourceComputerPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path getWorldComputerPath(MinecraftServer server) {
        return server.getWorldPath(LOCATION).resolve("computer").resolve("0");
    }

    private static Path getSourceComputerPath() {
        return TestHooks.getSourceDir().resolve("computer");
    }

    public static int error(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message).withStyle(ChatFormatting.RED));
        return 0;
    }
}
