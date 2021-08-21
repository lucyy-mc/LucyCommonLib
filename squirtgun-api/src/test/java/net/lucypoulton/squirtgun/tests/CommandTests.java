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

package net.lucypoulton.squirtgun.tests;

import net.lucypoulton.squirtgun.command.condition.Condition;
import net.lucypoulton.squirtgun.command.context.StringContext;
import net.lucypoulton.squirtgun.command.node.CommandNode;
import net.lucypoulton.squirtgun.command.node.NodeBuilder;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommandTests {

    @Test
    @DisplayName("Check simple built command nodes work")
    public void testSimpleBuiltNodes() {
        Component component = Component.text("hello");
        CommandNode<PermissionHolder> node = new NodeBuilder<>()
                .name("test")
                .executes(ctx -> component)
                .condition(Condition.alwaysTrue())
                .build();

        Component returned = new StringContext(new TestFormatter(),
                x -> true, node, "test").execute();
        Assertions.assertEquals(returned, component);
    }

    @Test
    @DisplayName("Check that commands fail when a required permission is not present")
    public void testCommandNoPermission() {
        Component component = Component.text("hello");
        CommandNode<PermissionHolder> node = new NodeBuilder<>()
                .name("test")
                .executes(ctx -> component)
                .condition(Condition.hasPermission("test.permission"))
                .build();
        Component returned = new StringContext(new TestFormatter(),
                x -> false, node, "test").execute();
        Assertions.assertNotEquals(component, returned);
    }

    @Test
    @DisplayName("Check that commands succeed when a required permission is present")
    public void testCommandWithPermission() {
        Component component = Component.text("hello");
        CommandNode<PermissionHolder> node = new NodeBuilder<>()
                .name("test")
                .executes(ctx -> component)
                .condition(Condition.hasPermission("test.permission"))
                .build();
        Component returned = new StringContext(new TestFormatter(),
                x -> true, node, "test").execute();
        Assertions.assertEquals(component, returned);
    }

    @Test
    @DisplayName("Check that a missing permission blocks a next node")
    public void textMissingPermissionForNextNode() {
        Component component = Component.text("hello");
        CommandNode<PermissionHolder> node = new NodeBuilder<>()
                .name("test")
                .executes(x -> component)
                .condition(Condition.alwaysTrue())
                .next(new NodeBuilder<>()
                        .name("test2")
                        .executes(ctx -> component)
                        .condition(Condition.hasPermission("you.dont.have.this.permission"))
                        .build()
                )
                .build();
        Component returned = new StringContext(new TestFormatter(),
                x -> false, node, "test2").execute();
        Assertions.assertEquals(component, returned);
    }
}
