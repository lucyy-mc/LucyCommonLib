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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

/**
 * An argument.
 */
public interface CommandArgument<T> {

    /**
     * Gets this argument's name.
     *
     * @return the name
     */
    @NotNull String getName();

    /**
     * Gets this argument's description for use in help commands. This should be a simple, one-line sentence.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Reads from a queue to get the value for this argument.
     *
     * @param args    a queue of strings containing the raw arguments. Pop as many as needed and no more.
     * @param context the context that this command has been executed in.
     *                Be aware that arguments may not yet be populated.
     * @return the parsed string value of this argument.
     */
    T getValue(Queue<String> args, CommandContext context);

    /**
     * Tab-completes this node.
     *
     * @param args    a queue of strings containing the raw arguments. Pop as many as needed and no more.
     * @param context the context that this command has been executed in.
     *                Be aware that arguments may not yet be populated.
     * @return the tabcompleted value of this node, or if not applicable, null
     */
    @Nullable List<String> tabComplete(Queue<String> args, CommandContext context);

    /**
     * Gets whether this argument is optional or not. This should not change how the argument behaves.
     */
    boolean isOptional();
}
