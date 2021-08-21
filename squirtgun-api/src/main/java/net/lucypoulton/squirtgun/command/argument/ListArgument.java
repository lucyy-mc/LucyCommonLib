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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * An argument that accepts a single string from a list.
 */
public class ListArgument extends AbstractArgument<String> {
    private final List<String> values;

    /**
     * @param name        this argument's name
     * @param description this argument's description
     * @param isOptional  whether this argument will be displayed as optional
     * @param values      a set of acceptable values for this argument
     */
    public ListArgument(String name, String description, boolean isOptional, List<String> values) {
        super(name, description, isOptional);
        this.values = values;
    }

    @Override
    public String getValue(Queue<String> args, CommandContext ctx) {
        String val = args.poll();
        return val != null && values.contains(val) ? val : null;
    }

    @Override
    public @Nullable List<String> tabComplete(Queue<String> value, CommandContext ctx) {
        String top = value.poll();
        if (top == null) return null;
        return values.stream()
                .filter(x -> x.startsWith(top))
                .collect(Collectors.toList());
    }
}
