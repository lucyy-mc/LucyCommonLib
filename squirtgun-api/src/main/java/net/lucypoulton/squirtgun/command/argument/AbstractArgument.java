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

import org.jetbrains.annotations.NotNull;

/**
 * An abstract argument, encapsulating the name and description fields.
 */
public abstract class AbstractArgument<T> implements CommandArgument<T> {
    private final String name;
    private final String description;
    private final boolean optional;

    /**
     * @param name        the argument's name
     * @param description the argument's description
     */
    protected AbstractArgument(String name, String description, boolean isOptional) {
        this.name = name;
        this.description = description;
        this.optional = isOptional;
    }

    @Override
    public final @NotNull String getName() {
        return name;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return isOptional() ? "[" + getName() + "]" : "<" + getName() + ">";
    }
}
