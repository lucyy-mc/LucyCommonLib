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
package net.lucypoulton.squirtgun.command.condition;

import net.lucypoulton.squirtgun.command.context.CommandContext;
import net.lucypoulton.squirtgun.platform.audience.PermissionHolder;
import net.lucypoulton.squirtgun.platform.audience.SquirtgunPlayer;

/**
 * TODO javadoc
 *
 * @param <T> the input holder type
 * @param <U> the outputted holder type
 */
@FunctionalInterface
public interface Condition<T extends PermissionHolder, U extends PermissionHolder> {

    static Condition<PermissionHolder, SquirtgunPlayer> isPlayer() {
        return (target, context) ->
                target instanceof SquirtgunPlayer
                        ? new Result<>(true, (SquirtgunPlayer) target, null)
                        : new Result<>(false, null, "This command can only be run by a player.");
    }

    static Condition<PermissionHolder, PermissionHolder> isPlayerCastless() {
        return (target, context) ->
                target instanceof SquirtgunPlayer
                        ? new Result<>(true, target, null)
                        : new Result<>(false, null, "This command can only be run by a player.");
    }

    static Condition<PermissionHolder, PermissionHolder> isConsole() {
        return (target, context) ->
                target instanceof SquirtgunPlayer
                        ? new Result<>(false, null, "This command can only be run from the console.")
                        : new Result<>(true, target, null);
    }

    static <V extends PermissionHolder> Condition<V, V> alwaysTrue() {
        return (target, context) -> new Result<>(true, target, null);
    }

    static <V extends PermissionHolder> Condition<V, V> hasPermission(String permission) {
        return (target, context) -> target.hasPermission(permission)
                ? new Result<>(true, target, null)
                : new Result<>(false, null, "No permission!");
    }

    Result<U> test(T target, CommandContext context);

    default <V extends PermissionHolder> Condition<T, V> and(Condition<? super U, V> other) {
        return ((target, context) -> {
            Result<U> first = this.test(target, context);
            if (!first.isSuccessful()) return new Result<>(false, null, first.getError());

            return other.test(first.getResult(), context);
        });
    }

    default Condition<T, U> or(Condition<? super T, U> other) {
        return ((target, context) -> {
            Result<U> first = this.test(target, context);
            if (first.isSuccessful()) return first;

            return other.test(target, context);
        });
    }

    class Result<U> {
        private final boolean successful;
        private final U result;
        private final String error;

        public Result(boolean successful, U result, String error) {
            this.successful = successful;
            this.result = result;
            this.error = error;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public final boolean isFailure() {
            return !isSuccessful();
        }

        public U getResult() {
            return result;
        }

        public String getError() {
            return error;
        }
    }
}
