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

package net.lucypoulton.squirtgun.bukkit;

import net.lucypoulton.squirtgun.bukkit.task.BukkitTaskScheduler;
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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A Platform implementation for Bukkit.
 */
public class BukkitPlatform implements Platform {

    private final JavaPlugin plugin;

    private final BukkitTaskScheduler scheduler = new BukkitTaskScheduler(this);

    private final BukkitListenerAdapter listenerAdapter = new BukkitListenerAdapter();

    private final BukkitAudiences audiences;

    public BukkitPlatform(final JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(listenerAdapter, plugin);
        audiences = BukkitAudiences.create(plugin);
    }

    public JavaPlugin getBukkitPlugin() {
        return plugin;
    }

    @Override
    public String name() {
        return "Bukkit";
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
        boolean onlineMode = Bukkit.getOnlineMode();
        try {
            if (Class.forName("org.spigotmc.SpigotConfig").getField("bungee").getBoolean(null)) {
                return AuthMode.BUNGEE;
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            // probably running craftbukkit, we can safely ignore these
        }
        return onlineMode ? AuthMode.ONLINE : AuthMode.OFFLINE;
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
        return new BukkitConsoleWrapper(audiences.console());
    }

    @Override
    public SquirtgunPlayer getPlayer(UUID uuid) {
        return new BukkitPlayer(Bukkit.getOfflinePlayer(uuid), audiences.player(uuid));
    }

    @Override
    public SquirtgunPlayer getPlayer(String name) {
        OfflinePlayer player = Bukkit.getPlayer(name);
        if (player == null) {
            player = Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(p -> name.equals(p.getName()))
                    .findFirst().orElse(null);
        }
        if (player == null) {
            return null;
        }
        Audience audience = player instanceof Player ? audiences.player((Player) player) : Audience.empty();
        return new BukkitPlayer(player, audience);
    }

    @Override
    public List<SquirtgunPlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .map(p -> new BukkitPlayer(p, audiences.player(p)))
                .collect(Collectors.toList());
    }

    /**
     * Gets a SquirtgunUser from a command sender.
     *
     * @param sender the command sender
     */
    public SquirtgunUser getUser(CommandSender sender) {
        if (sender instanceof OfflinePlayer) {
            return getPlayer(((OfflinePlayer) sender).getUniqueId());
        }
        return getConsole();
    }

    @Override
    public Path getConfigPath(SquirtgunPlugin<?> plugin) {
        return Paths.get(this.plugin.getDataFolder().toURI());
    }

    @Override
    public void registerCommand(CommandNode<?> node, FormatProvider provider) {
        PluginCommand command = plugin.getCommand(node.getName());
        Objects.requireNonNull(command);
        TabExecutor executor = new BukkitNodeExecutor(node, provider, this);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }
}
