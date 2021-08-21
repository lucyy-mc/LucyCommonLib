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

package net.lucypoulton.squirtgun.command.argument;

import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.platform.Platform;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * An argument that gets a player. This will tabcomplete for online players, but will
 * provide any player, regardless of if they are online or not.
 */
public class OnlinePlayerArgument extends AbstractArgument<SquirtgunPlayer> {
    private final Platform platform;

    public OnlinePlayerArgument(String name, String description, boolean isOptional, Platform platform) {
        super(name, description, isOptional);
        this.platform = platform;
    }

    @Override
    public SquirtgunPlayer getValue(Queue<String> args, CommandContext ctx) {
        String name = args.poll();
        return name == null || "".equals(name) ? null : platform.getPlayer(name);
    }

    @Override
    public @Nullable List<String> tabComplete(Queue<String> args, CommandContext ctx) {
        String name = args.poll();
        return name == null ? null : platform.getOnlinePlayers()
                .stream()
                .map(SquirtgunPlayer::getUsername)
                .filter(username -> username.startsWith(name))
                .collect(Collectors.toList());
    }
}
