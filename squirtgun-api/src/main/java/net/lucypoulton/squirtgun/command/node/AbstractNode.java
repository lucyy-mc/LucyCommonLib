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
import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNode<T extends PermissionHolder> implements CommandNode<T> {

    private final String name;
    private final String description;
    private final Condition<PermissionHolder, T> condition;

    protected AbstractNode(@NotNull String name, @NotNull String description, Condition<PermissionHolder, T> condition) {
        Preconditions.checkNotNull(name, "Name must not be null");
        Preconditions.checkNotNull(description, "Description must not be null");
        this.name = name;
        this.description = description;
        this.condition = condition;
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
}
