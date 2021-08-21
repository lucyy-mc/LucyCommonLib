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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.command.node.AbstractNode;
import net.lucypoulton.squirtgun.command.node.CommandNode;
import net.lucypoulton.squirtgun.command.argument.CommandArgument;
import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A node that tab completes and acts as a "splitter" for other nodes.
 * As per the name, it can be used to create subcommands.
 *
 * @since 2.0.0
 */
public class SubcommandNode extends AbstractNode<PermissionHolder> {

    private final Set<CommandNode<?>> childNodes;
    private final CommandArgument<CommandNode<?>> argument;
    private CommandNode<?> fallbackNode;

    /**
     * @param name       this node's name
     * @param condition  the condition needed to execute this node
     * @param childNodes the child nodes
     */
    protected SubcommandNode(@NotNull String name, @NotNull String description,
                             Condition<PermissionHolder, PermissionHolder> condition, @NotNull CommandNode<?>... childNodes) {
        super(name, description, condition);
        Preconditions.checkNotNull(childNodes, "Child nodes must not be null");

        this.childNodes = new HashSet<>(Arrays.asList(childNodes));

        argument = new SubcommandNodeArgument(this, "subcommand", "The subcommand to execute");
    }

    /**
     * Creates a node with an advanced help node.
     *
     * @param name        this node's name
     * @param description this node's description
     * @param condition   the condition needed to execute this node
     * @param childNodes  the child nodes
     * @return a new SubcommandNode
     */
    public static SubcommandNode withHelp(
            String name,
            String description,
            Condition<PermissionHolder, PermissionHolder> condition,
            @NotNull CommandNode<?>... childNodes) {
        SubcommandNode node = new SubcommandNode(name, description, condition, childNodes);
        node.setFallbackNode(new SubcommandHelpNode(node));
        return node;
    }

    /**
     * Creates a node with a custom default node.
     *
     * @param name        this node's name
     * @param description this node's description
     * @param condition   the condition needed to execute this node
     * @param fallback    the node to use if no valid subcommand is specified
     * @param childNodes  the child nodes
     * @return a new SubcommandNode
     */
    public static SubcommandNode withFallback(
            String name,
            String description,
            Condition<PermissionHolder, PermissionHolder> condition,
            @NotNull CommandNode<?> fallback,
            @NotNull CommandNode<?>... childNodes) {
        SubcommandNode node = new SubcommandNode(name, description, condition, childNodes);
        node.setFallbackNode(fallback);
        return node;
    }

    /**
     * Creates a node with a basic help node.
     *
     * @param name        this node's name
     * @param description this node's description
     * @param condition   the condition needed to execute this node
     * @param childNodes  the child nodes
     * @return a new SubcommandNode
     */
    public static SubcommandNode withBasicHelp(
            String name,
            String description,
            Condition<PermissionHolder, PermissionHolder> condition,
            @NotNull CommandNode<?>... childNodes) {
        return new SubcommandNode(name, description, condition, childNodes);
    }

    /**
     * Gets the child nodes that this node holds.
     */
    public Set<CommandNode<?>> getNodes() {
        return childNodes;
    }

    /**
     * Gets the fallback node.
     */
    public @Nullable CommandNode<?> getFallbackNode() {
        return fallbackNode;
    }

    /**
     * Sets the fallback node.
     */
    private void setFallbackNode(CommandNode<?> fallback) {
        fallbackNode = fallback;
        childNodes.add(fallback);
    }

    @Override
    public @NotNull List<CommandArgument<?>> getArguments() {
        return ImmutableList.of(argument);
    }

    @Override
    public @Nullable CommandNode<? extends PermissionHolder> next(CommandContext context) {
        CommandNode<?> name = context.getArgumentValue(argument);
        return name == null ? fallbackNode : name;
    }

    @Override
    public @Nullable Component execute(CommandContext context) {
        return null;
    }
}
