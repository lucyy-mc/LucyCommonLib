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

package net.lucypoulton.squirtgun.command.node;

import net.lucypoulton.squirtgun.Squirtgun;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.format.FormatProvider;
import net.lucypoulton.squirtgun.format.TextFormatter;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunUser;
import net.lucypoulton.squirtgun.plugin.SquirtgunPlugin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class PluginInfoNode extends AbstractNode<SquirtgunUser> {

    private final SquirtgunPlugin<?> plugin;

    public PluginInfoNode(String name, SquirtgunPlugin<?> plugin) {
        super(name, "Shows information about this plugin.", null);
        this.plugin = plugin;
    }

    @Override
    public @Nullable Component execute(CommandContext context) {
        final FormatProvider fmt = context.getFormat();
        final Component nl = Component.newline();
        return Component.empty()
                .append(TextFormatter.formatTitle(plugin.getPluginName(), fmt))
                .append(nl).append(nl)
                .append(fmt.formatMain(plugin.getPluginName() + " version "))
                .append(fmt.formatAccent(plugin.getPluginVersion())).append(nl)
                .append(fmt.formatMain("Written by "))
                .append(fmt.formatAccent(String.join(", ", plugin.getAuthors())))
                .append(nl)
                .append(fmt.formatMain("Built with Squirtgun version "))
                .append(fmt.formatAccent(Squirtgun.VERSION))
                .append(nl)
                .append(fmt.formatMain("Using platform "))
                .append(fmt.formatAccent(plugin.getPlatform().name()))
                .append(nl).append(nl)
                .append(TextFormatter.formatTitle("*", fmt));
    }
}
