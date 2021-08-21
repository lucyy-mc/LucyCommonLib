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

package net.lucypoulton.squirtgun.command.node.subcommand;

import com.google.common.collect.ImmutableList;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.command.node.CommandNode;
import net.lucypoulton.squirtgun.command.node.HelpNode;
import net.lucypoulton.squirtgun.command.argument.CommandArgument;
import net.lucypoulton.squirtgun.command.argument.ListArgument;
import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.format.FormatProvider;
import net.lucypoulton.squirtgun.format.TextFormatter;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A help node for a subcommand.
 * One optional argument is exposed, which determines a specific child node to show help for.
 * If specified, a new {@link HelpNode} will be created for it and the output shown, otherwise
 * a general overview of the command and its subnodes will be shown.
 *
 * @since 2.0.0
 */
public class SubcommandHelpNode implements CommandNode<PermissionHolder> {

    private final SubcommandNode parentNode;
    private final CommandArgument<String> childArgument;

    public SubcommandHelpNode(SubcommandNode parentNode) {
        this.parentNode = parentNode;
        childArgument = new ListArgument("command",
                "The command to get help for.",
                true,
                parentNode.getNodes().stream()
                        .map(CommandNode::getName)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return ImmutableList.of(childArgument);
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows this screen.";
    }

    @Override
    public Condition<PermissionHolder, PermissionHolder> getCondition() {
        return Condition.alwaysTrue();
    }

    @Override
    public @Nullable CommandNode<?> next(CommandContext context) {
        String child = context.getArgumentValue(childArgument);
        if (child == null) return null;

        Optional<? extends CommandNode<?>> nodeWithGivenName = parentNode.getNodes().stream()
                .filter(node -> node.getName().equals(child))
                .findFirst();

        return nodeWithGivenName.<CommandNode<?>>map(HelpNode::new).orElse(null);

    }

    @Override
    public @Nullable Component execute(CommandContext context) {
        final FormatProvider fmt = context.getFormat();
        Component out = Component.empty()
                .append(TextFormatter.formatTitle("Commands:", fmt))
                .append(Component.newline());

        for (CommandNode<?> node : parentNode.getNodes()) {
            if (node.getCondition().test(context.getTarget(), context).isSuccessful()) {
                Component innerComp =
                        fmt.formatMain(parentNode.getName() + " ")
                                .append(fmt.formatAccent(node.getName()))
                                .append(fmt.formatMain(" - " + node.getDescription()))
                                .append(Component.text("\n"));
                out = out.append(innerComp);
            }
        }

        out = out.append(Component.newline())
                .append(TextFormatter.formatTitle("*", fmt));

        return out;
    }
}
