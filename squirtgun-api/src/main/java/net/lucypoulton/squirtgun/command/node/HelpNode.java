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

import net.lucypoulton.squirtgun.command.argument.CommandArgument;
import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.format.FormatProvider;
import net.lucypoulton.squirtgun.format.TextFormatter;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command node that shows a help screen for another node
 * that shows its description, usage, and argument information.
 *
 * @since 2.0.0
 */
public class HelpNode implements CommandNode<PermissionHolder> {

    private final CommandNode<?> parentNode;

    public HelpNode(CommandNode<?> parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public @Nullable Component execute(CommandContext context) {
        FormatProvider format = context.getFormat();
        Component out = Component.empty()
                .append(TextFormatter.formatTitle("Command Help", format))
                .append(Component.newline());

        String fullCommand = parentNode.getName() + " " +
                parentNode.getArguments().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(" "));

        out = out.append(TextFormatter.centreText(parentNode.getDescription(), format, " "))
                .append(Component.newline())
                .append(format.formatAccent("Usage"))
                .append(format.formatMain(": " + fullCommand))
                .append(Component.newline());

        if (parentNode.getArguments().size() != 0) {
            out = out.append(Component.newline())
                    .append(TextFormatter.centreText("Arguments", format, " "))
                    .append(Component.newline());
            for (CommandArgument<?> argument : parentNode.getArguments()) {
                out = out.append(format.formatAccent(argument.getName()))
                        .append(format.formatMain(" - " + argument.getDescription()))
                        .append(Component.newline());
            }
        }

        out = out.append(Component.newline())
                .append(TextFormatter.formatTitle("*", context.getFormat()));

        return out;
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows help on using this command.";
    }

    @Override
    public Condition<PermissionHolder, PermissionHolder> getCondition() {
        return Condition.alwaysTrue();
    }

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return Collections.emptyList();
    }
}
