package ua.cte2uk.text;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exile Overlay: анімований підпис лічильника днів «— D —», «— DA —», «— DAY —», «— DAY 12 —».
 */
public final class DayCaption {
    private static final Pattern CAPTION = Pattern.compile("^(\\P{L}*?)(DAY|DA|D)((?: \\d+)?)(\\P{L}*)$");

    private DayCaption() {
    }

    /**
     * @param lookup переклад за ключем {@code cte2_uk.day_counter.<d|da|day>} або {@code null}
     * @return перекладений підпис або {@code null}, якщо це не підпис дня
     */
    public static String translate(String text, Function<String, String> lookup) {
        if (text == null) {
            return null;
        }
        Matcher m = CAPTION.matcher(text);
        if (!m.matches()) {
            return null;
        }
        String word = lookup.apply("cte2_uk.day_counter." + m.group(2).toLowerCase(java.util.Locale.ROOT));
        return word == null ? null : m.group(1) + word + m.group(3) + m.group(4);
    }
}
