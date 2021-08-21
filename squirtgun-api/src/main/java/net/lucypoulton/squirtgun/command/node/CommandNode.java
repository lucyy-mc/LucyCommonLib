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
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A node as part of a command chain.
 *
 * @param <T> the type of object to pass in command context
 * @since 2.0.0
 */
public interface CommandNode<T extends PermissionHolder> {

    /**
     * Execute this node. This will only be executed if there are no further nodes
     * in the chain.
     *
     * @param context the context that this command was executed in
     * @return a component to optionally
     */
    @Nullable Component execute(CommandContext context);

    /**
     * Gets this node's name, which will be used as a literal if needed.
     */
    @NotNull String getName();

    /**
     * Gets this node's description for use in help commands. This should be a simple,
     * one-line sentence.
     */
    String getDescription();

    /**
     * Gets the condition needed to execute this node or any children. If not needed, use PermissionHolder as T and
     * return {@link Condition#alwaysTrue()}
     *
     * @return the condition
     */
    Condition<PermissionHolder, T> getCondition();

    /**
     * Gets this command's arguments. By default, returns an empty list.
     */
    default @NotNull List<CommandArgument<?>> getArguments() {
        return Collections.emptyList();
    }

    /**
     * Returns the node following this node.
     *
     * @return the next node in the chain, or if this is the end of the chain, null
     */
    default @Nullable CommandNode<? extends T> next(CommandContext context) {
        return null;
    }
}
