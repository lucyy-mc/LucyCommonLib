/*
 * Copyright © 2021 Lucy Poulton
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.lucypoulton.squirtgun.fabric;

import net.lucypoulton.squirtgun.command.node.CommandNode;
import net.lucypoulton.squirtgun.fabric.task.FabricTaskScheduler;
import net.lucypoulton.squirtgun.format.FormatProvider;
import net.lucypoulton.squirtgun.platform.AuthMode;
import net.lucypoulton.squirtgun.platform.EventListener;
import net.lucypoulton.squirtgun.platform.Platform;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunPlayer;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunUser;
import net.lucypoulton.squirtgun.plugin.SquirtgunPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Squirtgun Platform implementation for the Fabric mod loader.
 */
public final class FabricPlatform implements Platform {

    private static final Logger LOGGER = Logger.getLogger(FabricPlatform.class.getSimpleName());
    private static final List<String> PROXY_BRIDGING_MODS =
            List.of("fabricproxy",  // fabricproxy has been superseded by fabricproxy-lite, TODO drop off the list?
                    "fabricproxy-lite");
    private final FabricTaskScheduler taskScheduler;
    private final FabricConsoleWrapper consoleWrapper;
    private final FabricListenerAdapter listenerAdapter = new FabricListenerAdapter();
    private MinecraftServer server;
    private FabricServerAudiences audiences;
    private List<SquirtgunPlayer> onlinePlayers = List.of();  // default to empty list until server has started

    /**
     * Create the Squirtgun's Fabric "platform" for the server.
     *
     * @param server server instance to work with
     */
    public FabricPlatform(final @NotNull MinecraftServer server) {
        this.server = requireNonNull(server, "server");
        this.audiences = FabricServerAudiences.of(server);
        this.taskScheduler = new FabricTaskScheduler(this);
        this.consoleWrapper = new FabricConsoleWrapper(this);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::serverStopping);
        ServerLifecycleEvents.SERVER_STARTED.register(this::serverStarted);
    }

    private void serverStarted(final MinecraftServer server) {
        this.onlinePlayers = getServer().getPlayerManager().getPlayerList()
                .stream()
                .map(this::asFabricPlayerOrDummy)
                .collect(Collectors.toList());
    }

    private void serverStopping(final MinecraftServer server) {
        this.taskScheduler.shutdown();
        this.server = null;
        this.audiences = null;
    }

    /**
     * Get the server instance this platform is working with.
     *
     * @return the server instance
     */
    public MinecraftServer getServer() {  // TODO mark as @Nullable instead of forcefully failing?
        return requireNonNull(this.server, "Cannot access the server without a server running");
    }

    /**
     * Get Adventure's audience provider for this server.
     *
     * @return the server audience provider
     */
    public FabricServerAudiences getAudienceProvider() {  // TODO mark as @Nullable instead of forcefully failing?
        return requireNonNull(this.audiences, "Cannot access the audience provider without a server running");
    }

    @Override
    public String name() {
        return "Fabric";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void log(final Component component) {
        getAudienceProvider().console().sendMessage(component);
    }

    @Override
    public AuthMode getAuthMode() {
        final MinecraftServer server = getServer();
        if (server instanceof DedicatedServer) {
            if (PROXY_BRIDGING_MODS.stream().anyMatch(FabricLoader.getInstance()::isModLoaded)) {
                return AuthMode.BUNGEE;
            } else {
                return ((DedicatedServer) server).getProperties().onlineMode ? AuthMode.ONLINE : AuthMode.OFFLINE;
            }
        } else {
            return AuthMode.ONLINE;  // https://github.com/lucyy-mc/Squirtgun/pull/29#discussion_r658576670
        }
    }

    @Override
    public FabricTaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    @Override
    public void registerEventListener(final EventListener listener) {
        this.listenerAdapter.addListener(listener);
    }

    @Override
    public void unregisterEventListener(final EventListener listener) {
        this.listenerAdapter.removeListener(listener);
    }

    /**
     * Wraps a {@link ServerCommandSource} in a {@link SquirtgunUser}.
     *
     * <p>If the command source entity is a player entity, this method calls and returns
     * {@link #getPlayer(ServerPlayerEntity)}, elsewise it returns {@link #getConsole()}.</p>
     *
     * @param commandSource {@link ServerCommandSource} to adapt
     * @return corresponding {@link SquirtgunUser}
     */
    public @NotNull SquirtgunUser fromCommandSource(final @NotNull ServerCommandSource commandSource) {
        final Entity entity = requireNonNull(commandSource, "commandSource").getEntity();
        if (entity instanceof ServerPlayerEntity) {
            return getPlayer((ServerPlayerEntity) entity);
        } else {
            return getConsole();
        }
    }

    @Override
    public FabricConsoleWrapper getConsole() {
        return this.consoleWrapper;
    }

    @Override
    public FabricPlayer getPlayer(final UUID uuid) {
        return asFabricPlayerOrDummy(getServer().getPlayerManager().getPlayer(requireNonNull(uuid, "uuid")));
    }

    @Override
    public FabricPlayer getPlayer(final String name) {
        return asFabricPlayerOrDummy(getServer().getPlayerManager().getPlayer(requireNonNull(name, "name")));
    }

    /**
     * Wraps a {@link ServerPlayerEntity} in a {@link FabricPlayer}.
     *
     * @param player player entity to wrap
     * @return Squirtgun's Fabric player wrapper
     */
    public @NotNull FabricPlayer getPlayer(final @Nullable ServerPlayerEntity player) {
        return asFabricPlayerOrDummy(player);
    }

    @Override
    public List<SquirtgunPlayer> getOnlinePlayers() {
        return this.onlinePlayers;
    }

    @Override
    public Path getConfigPath(final SquirtgunPlugin<?> plugin) {
        return FabricLoader.getInstance().getConfigDir().resolve(plugin.getPluginName());
    }

    @Override
    public void registerCommand(CommandNode<?> node, FormatProvider provider) {
        // TODO
        throw new NotImplementedException("TODO");
    }

    private FabricPlayer asFabricPlayerOrDummy(final ServerPlayerEntity player) {
        return player == null ? DummyFabricPlayer.INSTANCE : new FabricPlayerImpl(player, getAudienceProvider().audience(player));
    }
}
