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

import com.google.common.collect.ImmutableList;
import net.lucypoulton.squirtgun.command.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;

/**
 * An argument that consumes all the available arguments. Note this argument
 * must be the last in a command chain.
 */
public final class GreedyStringArgument extends AbstractArgument<String> {

    /**
     * @param name        the argument's name
     * @param description the argument's description
     * @param isOptional  whether the argument is optional
     */
    public GreedyStringArgument(String name, String description, boolean isOptional) {
        super(name, description, isOptional);
    }

    @Override
    public String getValue(Queue<String> args, CommandContext ctx) {
        return String.join(" ", args);
    }

    @Override
    public @NotNull List<String> tabComplete(Queue<String> value, CommandContext ctx) {
        value.poll();
        return ImmutableList.of(toString());
    }
}