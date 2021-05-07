package me.lucyy.squirtgun.command.argument;

import me.lucyy.squirtgun.command.context.CommandContext;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Queue;

/**
 * An argument.
 */
public interface CommandArgument<T> {

	/**
	 * Gets this argument's name.
	 */
	String getName();

	/**
	 * Gets this argument's description for use in help commands.
	 */
	String getDescription();

	/**
	 * Reads from a queue to get the value for this argument.
	 *
	 * @param args a queue of strings containing the raw arguments. Pop as many as needed and no more.
	 * @return the parsed string value of this argument.
	 */
	String getValue(Queue<String> args);

	/**
	 * Tab-completes this node.
	 *
	 * @param context the command context built so far
	 * @param value the partial value to tabcomplete
	 * @return a string list of tabcomplete entries, or null if no entries are needed
	 */
	@Nullable List<String> tabComplete(CommandContext<?> context, String value);
}
