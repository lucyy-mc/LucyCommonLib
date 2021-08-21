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

import com.google.common.base.Preconditions;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.command.argument.CommandArgument;
import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A builder to build a node. The minimum required fields are name and an execute function.
 *
 * @param <T> the target type to build the node for
 * @since 2.0.0
 */
public class NodeBuilder<T extends PermissionHolder> {

    private final List<CommandArgument<?>> arguments = new ArrayList<>();
    private String name;
    private String description;
    private Condition<PermissionHolder, T> condition;
    private Function<CommandContext, @Nullable Component> executes;
    private CommandNode<T> next;

    /**
     * Sets this node's name.
     *
     * @param name the new name to set
     * @return this
     */
    public NodeBuilder<T> name(@NotNull String name) {
        Preconditions.checkNotNull(name, "Name must not be null");
        this.name = name;
        return this;
    }

    /**
     * Sets this node's description.
     *
     * @param description the new description to set - this should be a short, 1-line sentence
     * @return this
     */
    public NodeBuilder<T> description(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets this node's required condition.
     *
     * @param condition the required condition
     * @return this
     */
    public NodeBuilder<T> condition(Condition<PermissionHolder, T> condition) {
        Preconditions.checkNotNull(condition, "Condition must not be null");
        this.condition = condition;
        return this;
    }

    /**
     * Sets the function to execute this node.
     *
     * @param executes the function. It should return a component to display to the sender. This may be null, in which
     *                 case nothing will be sent.
     * @return this
     */
    public NodeBuilder<T> executes(@NotNull Function<CommandContext, @Nullable Component> executes) {
        Preconditions.checkNotNull(executes, "Executes function must not be null");
        this.executes = executes;
        return this;
    }

    /**
     * Sets the next node in the chain. Typically you don't need this, the default value of null will suffice.
     *
     * @param next a possibly null next node
     * @return this
     */
    public NodeBuilder<T> next(@Nullable CommandNode<T> next) {
        this.next = next;
        return this;
    }

    /**
     * Adds arguments to this node.
     *
     * @param arguments arguments to add, in order, to the list
     * @return this
     */
    public NodeBuilder<T> arguments(@NotNull CommandArgument<?>... arguments) {
        this.arguments.addAll(Arrays.asList(arguments));
        return this;
    }

    /**
     * Builds this node, throwing a {@link NullPointerException} if an argument is incorrect or missing.
     *
     * @return a node built from the specified parameters.
     */
    public CommandNode<T> build() {
        return new BuiltCommandNode<>(
                Objects.requireNonNull(name),
                description,
                Objects.requireNonNull(condition),
                Objects.requireNonNull(executes),
                next, arguments);
    }

    private static class BuiltCommandNode<T extends PermissionHolder> implements CommandNode<T> {

        private final String name;
        private final String description;
        private final Condition<PermissionHolder, T> condition;
        private final Function<CommandContext, @Nullable Component> executes;
        private final @Nullable CommandNode<T> next;
        private final List<CommandArgument<?>> arguments;

        private BuiltCommandNode(String name,
                                 String description,
                                 Condition<PermissionHolder, T> condition,
                                 Function<CommandContext, @Nullable Component> executes,
                                 @Nullable CommandNode<T> next,
                                 List<CommandArgument<?>> arguments) {
            this.name = name;
            this.description = description;
            this.condition = condition;
            this.executes = executes;
            this.next = next;
            this.arguments = arguments;
        }

        @Override
        public @Nullable Component execute(CommandContext context) {
            return executes.apply(context);
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Condition<PermissionHolder, T> getCondition() {
            return condition;
        }

        @Override
        public @NotNull List<CommandArgument<?>> getArguments() {
            return arguments;
        }

        @Override
        public @Nullable CommandNode<T> next(CommandContext context) {
            return next;
        }
    }
}
