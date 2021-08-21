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

package net.lucypoulton.squirtgun.bungee;

import net.lucypoulton.squirtgun.bungee.task.BungeeTaskScheduler;
import net.lucypoulton.squirtgun.command.node.CommandNode;
import net.lucypoulton.squirtgun.format.FormatProvider;
import net.lucypoulton.squirtgun.platform.AuthMode;
import net.lucypoulton.squirtgun.platform.EventListener;
import net.lucypoulton.squirtgun.platform.Platform;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunPlayer;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunUser;
import net.lucypoulton.squirtgun.platform.scheduler.TaskScheduler;
import net.lucypoulton.squirtgun.plugin.SquirtgunPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A Platform implementation for BungeeCord.
 */
public class BungeePlatform implements Platform {

    private final Plugin plugin;

    private final BungeeTaskScheduler scheduler = new BungeeTaskScheduler(this);

    private final BungeeListenerAdapter listenerAdapter = new BungeeListenerAdapter();

    private final BungeeAudiences audiences;

    public BungeePlatform(final Plugin plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, listenerAdapter);
        audiences = BungeeAudiences.create(plugin);
    }

    public Plugin getBungeePlugin() {
        return plugin;
    }

    @Override
    public String name() {
        return "BungeeCord";
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public void log(Component component) {
        audiences.console().sendMessage(component);
    }


    @Override
    public AuthMode getAuthMode() {
        return getBungeePlugin().getProxy().getConfig().isOnlineMode() ? AuthMode.ONLINE : AuthMode.OFFLINE;
    }

    @Override
    public TaskScheduler getTaskScheduler() {
        return scheduler;
    }

    @Override
    public void registerEventListener(EventListener listener) {
        listenerAdapter.addListener(listener);
    }

    @Override
    public void unregisterEventListener(EventListener listener) {
        listenerAdapter.removeListener(listener);
    }

    @Override
    public SquirtgunUser getConsole() {
        return new BungeeConsoleWrapper(audiences.console());
    }

    @Override
    public SquirtgunPlayer getPlayer(UUID uuid) {
        return new BungeePlayer(getBungeePlugin().getProxy().getPlayer(uuid), audiences.player(uuid));
    }

    @Override
    public SquirtgunPlayer getPlayer(String name) {
        ProxiedPlayer player = getBungeePlugin().getProxy().getPlayer(name);
        if (player == null) {
            return null;
        }
        Audience audience = audiences.sender(player);
        return new BungeePlayer(player, audience);
    }

    @Override
    public List<SquirtgunPlayer> getOnlinePlayers() {
        return getBungeePlugin().getProxy().getPlayers().stream()
                .map(p -> new BungeePlayer(p, audiences.player(p)))
                .collect(Collectors.toList());
    }

    /**
     * Gets a SquirtgunUser from a command sender.
     *
     * @param sender the command sender
     */
    public SquirtgunUser getUser(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return getPlayer(((ProxiedPlayer) sender).getUniqueId());
        }
        return getConsole();
    }

    @Override
    public Path getConfigPath(SquirtgunPlugin<?> plugin) {
        return Paths.get(this.plugin.getDataFolder().toURI());
    }

    @Override
    public void registerCommand(CommandNode<?> node, FormatProvider provider) {
        plugin.getProxy().getPluginManager().registerCommand(plugin,
                new BungeeNodeExecutor(node, provider, this));
    }
}
