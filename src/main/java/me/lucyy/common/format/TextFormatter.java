package me.lucyy.common.format;

import me.lucyy.common.command.FormatProvider;
import me.lucyy.common.format.blocked.BlockedGradient;
import me.lucyy.common.format.blocked.BlockedGradientPattern;
import me.lucyy.common.format.pattern.FormatPattern;
import me.lucyy.common.format.pattern.HexPattern;
import me.lucyy.common.format.pattern.HsvGradientPattern;
import me.lucyy.common.format.pattern.RgbGradientPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats text, supporting gradients and hex codes.
 * This was inspired by IridiumColorAPI and CMI
 * https://github.com/Iridium-Development/IridiumColorAPI
 */
@SuppressWarnings("unused")
public class TextFormatter {

    private static final List<FormatPattern> patterns = new ArrayList<>();
    private static final Pattern formatterPattern = Pattern.compile("\\{[^{}]+>}.*\\{[^{}]+<}|(\\{[^{}]+})?[^{}]+");

    static {
        patterns.add(new RgbGradientPattern());
        patterns.add(new HsvGradientPattern());
        patterns.add(new HexPattern());

        patterns.add(new BlockedGradientPattern("flag",
                        new BlockedGradient(
                                new String[]{"transgender", "trans"},
                                TextColor.color(0x55cdfc),
                                TextColor.color(0xf7a8b8),
                                NamedTextColor.WHITE,
                                TextColor.color(0xf7a8b8),
                                TextColor.color(0x55cdfc)
                        ),
                        new BlockedGradient(
                                new String[]{"bisexual", "bi"},
                                TextColor.color(0xd60270),
                                TextColor.color(0xd60270),
                                TextColor.color(0x9b4f96),
                                TextColor.color(0x0038a8),
                                TextColor.color(0x0038a8)
                        ),
                        new BlockedGradient(
                                "lesbian",
                                TextColor.color(0xD62900),
                                TextColor.color(0xFF9B55),
                                NamedTextColor.WHITE,
                                TextColor.color(0xD461A6),
                                TextColor.color(0xA50062)
                        ),
                        new BlockedGradient(
                                new String[]{"nonbinary", "non-binary", "enby"},
                                TextColor.color(0xfff430),
                                NamedTextColor.WHITE,
                                TextColor.color(0x9c59d1),
                                NamedTextColor.BLACK
                        ),
                        new BlockedGradient(
                                new String[]{"pansexual", "pan"},
                                TextColor.color(0xff1b8d),
                                TextColor.color(0xffda00),
                                TextColor.color(0x1bb3ff)

                        ),
                        new BlockedGradient(
                                new String[]{"asexual", "ace"},
                                NamedTextColor.BLACK,
                                TextColor.color(0xa3a3a3),
                                NamedTextColor.WHITE,
                                TextColor.color(0x810082)
                        ),
                        new BlockedGradient(
                                "genderqueer",
                                TextColor.color(0xb77fdd),
                                NamedTextColor.WHITE,
                                TextColor.color(0x48821e)
                        ),
                        new BlockedGradient(
                                new String[]{"genderfluid", "fluid"},
                                TextColor.color(0xff76a3),
                                NamedTextColor.WHITE,
                                TextColor.color(0xbf11d7),
                                NamedTextColor.BLACK,
                                TextColor.color(0x303cbe)
                        )
                )
        );
    }

    /**
     * Repeats a character to create a string.
     *
     * @param charToRepeat the character to repeat
     * @param count        the amount of times to repeat it
     */
    public static String repeat(String charToRepeat, int count) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < count; x++) builder.append(charToRepeat);
        return builder.toString();
    }

    //private static final String TITLE_SEPARATOR = "᠄"; // U+1804 Mongolian Colon - works perfectly for MC strikethrough
    private static final String TITLE_SEPARATOR = " ";
    private static final int CHAT_WIDTH = 320;

    /**
     * Inverts a component, flipping the extra components and adding the original component on the end.
     */
    public static Component invert(Component component) {
        List<Component> comps = new ArrayList<>();
        for (int i = component.children().size() - 1; i >= 0; i--) {
            comps.add(component.children().get(i));
        }
        comps.add(component.children(new ArrayList<>()));

        return comps.get(0).children(comps.subList(1, comps.size()));
    }

    /**
     * Creates a centre-aligned title bar for use in commands. Input is formatted using the main colour, the bars either
     * side are formatted using the accent colour.
     *
     * @param in     the text to use as a title
     * @param format the format to apply to the string
     * @return a formatted string, ready to send to the player
     */
    public static Component formatTitle(String in, FormatProvider format) {
        return centreText(in, format, TITLE_SEPARATOR, new TextDecoration[]{TextDecoration.STRIKETHROUGH});
    }

    /**
     * Inserts characters either side of an input string to centre-align it in chat.
     *
     * @param in        the string to centre
     * @param format    what to format the input with. Centred text is main-formatted, edges are accent-formatted
     * @param character the character to repeat to centre-align the text
     * @return a formatted string containing the centred text
     */
    public static Component centreText(String in, FormatProvider format, String character) {
        return centreText(in, format, character, new TextDecoration[0]);
    }

    /**
     * Inserts characters either side of an input string to centre-align it in chat.
     *
     * @param in         the string to centre
     * @param format     what to format the input with. Centred text is main-formatted, edges are accent-fornatted
     * @param character  the character to repeat to centre-align the text
     * @param formatters a list of vanilla formatter codes to apply to the edges
     * @return a formatted string containing the centred text
     */
    public static Component centreText(String in, FormatProvider format, String character, TextDecoration[] formatters) {
        int spaceLength = (CHAT_WIDTH - TextWidthFinder.findWidth(in) - 6) / 2; // take off 6 for two spaces
        Component line = format.formatAccent(repeat(character, spaceLength / TextWidthFinder.findWidth(character)), formatters);

        Component centre = format.formatMain(" " + in + " ");
        for (TextDecoration deco : formatters) centre = centre.decoration(deco, false);
        return line.append(centre).append(invert(line));
    }

    /**
     * Parse a string colour representation to a {@link TextColor}.
     *
     * @param in a string representation, either as:
     *           <ul>
     *           <li>a single character as in a standard formatter code</li>
     *           <li>a 6-digit HTML hex code, prepended with # ie #ff00ff</li>
     *           </ul>
     * @return the ChatColor representation, or null if it could not be parsed
     */
    public static TextColor colourFromText(String in) {
        if (in.length() == 1) return Objects.requireNonNull(LegacyComponentSerializer.parseChar(in.charAt(0))).color();
        else if (in.length() == 7 && in.startsWith("#")) {
            try {
                return TextColor.fromCSSHexString(in);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Parses a string to a set of coloured components, calculating gradients.
     * By default, any colours already in the string will be overridden
     *
     * @param input the string to parse. The following formats are supported:
     *              <ul>
     *              <li>{@literal &c}text - vanilla format codes, using &amp; as per bukkit conventions</li>
     *              <li>{@literal {#FFFFFF}}text - formatted as a static hex code, similar to vanilla codes</li>
     *              <li>{@literal {#FFFFFF>}text{#000000<}} - RGB gradient between the two specified hex codes</li>
     *              <li>{@literal {hsv:FF0000>}text{FF<}} - HSV gradient. The hex value in the start tag is not an HTML
     *              code - the first two characters are hue, next two are saturation, last two are value. In the closing
     *              tag the value is the finishing hue. All values are in the range 0-255 inclusive.</li>
     *              </ul>
     *              Gradient formats support extra format tags, as a list of vanilla characters following a colon. For
     *              example, a gradient from #FFFFFF, in bold and italic, would start {@literal {#FFFFFF:lo}>}.
     * @return the formatted text
     */
    public static Component format(String input) {
        return format(input, null, false);
    }

    /**
     * Parses a string to a set of coloured components, calculating gradients.
     *
     * @param input                   as for {@link #format(String)}
     * @param overrides               a string of vanilla formatters to add to the text
     * @param usePredefinedFormatters whether to use §-prefixed codes. if true then they will take priority over LCL
     *                                formatters, if false then they will be removed prior to formatting
     * @return the formatted text
     */
    public static Component format(String input, String overrides, boolean usePredefinedFormatters) {
        if ((usePredefinedFormatters && input.contains("\u00a7")) || input.contains("&")) {
            return Component.text(input.replaceAll("&", "\u00a7")
                    .replaceAll("(?<!\\\\)\\{[^}]*}", ""));
        }
        input = input.replaceAll("\u00a7.", "");
        Component output = null;
        Matcher matcher = formatterPattern.matcher(input);
        while (matcher.find()) {
            boolean hasBeenParsed = false;
            for (FormatPattern pattern : patterns) {
                Component component = pattern.process(matcher.group(), overrides);
                if (component != null) {
                    hasBeenParsed = true;
                    if (output == null) output = component;
                    else output = output.append(component);
                    break;
                }
            }
            if (!hasBeenParsed) {
                Component component = Component.text(matcher.group());
                if (output == null) output = component;
                else output = output.append(component);
            }
        }
        return output;
    }

    /**
     * Create an array of evenly-spaced integers between two values. Values are calculated as floats and are then
     * rounded.
     *
     * @param count the length of the returned array
     * @param val1  the value to start from
     * @param val2  the value to end at
     * @return an integer array of length count
     */
    public static int[] fade(int count, int val1, int val2) {
        float step = (val2 - val1) / (float) (count - 1);

        int[] output = new int[count];

        for (int x = 0; x < count; x++) {
            float result = (val1) + step * x;
            while (result < 0) result += 360;
            while (result > 360) result -= 360;
            output[x] = Math.round(result);
        }
        return output;
    }
}