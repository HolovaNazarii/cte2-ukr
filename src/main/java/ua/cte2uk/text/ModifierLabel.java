package ua.cte2uk.text;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Підпис модифікатора стату Mine and Slash.
 *
 * <p>Мод складає рядок в англійському порядку: «+5% Збільшено Шкода». Українською читається краще
 * «Шкода: +5% (збільшення)». Без числа: «Збільшено Шкода» → «Шкода (збільшення)».
 * Кольорові коди зберігаються.
 */
public final class ModifierLabel {
    private static final String CODE = "§[0-9a-fk-or]";
    private static final String KIND = "Збільшено|Зменшено|Збільшення|Зменшення|Більше|Менше";

    private static final Pattern WITH_VALUE = Pattern.compile(
        "^(?<value>(?:" + CODE + ")*[+\\-−]?\\d[\\d.,]*[kmbt]?%)(?<gap>(?:" + CODE + "|\\s)+)"
            + "(?<kind>" + KIND + ")(?<gap2>(?:" + CODE + "|\\s)+)(?<name>.+)$",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern NAME_ONLY = Pattern.compile(
        "^(?<style>(?:" + CODE + "|\\s)*)(?<kind>" + KIND + ")(?<gap>(?:" + CODE + "|\\s)+)(?<name>.+)$",
        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final Pattern TRAILING_CODES = Pattern.compile("(?:" + CODE + ")+$", Pattern.CASE_INSENSITIVE);

    private ModifierLabel() {
    }

    /** @return переставлений підпис або {@code null}, якщо рядок не схожий на підпис модифікатора */
    public static String reorder(String label) {
        if (label == null) {
            return null;
        }
        Matcher m = WITH_VALUE.matcher(label);
        if (m.matches()) {
            String nameStyle = lastCodes(m.group("gap2"));
            return nameStyle + m.group("name").stripTrailing() + "§r: " + m.group("value") + " §7(" + kind(m.group("kind")) + ")";
        }
        m = NAME_ONLY.matcher(label);
        if (m.matches()) {
            return m.group("style") + lastCodes(m.group("gap")) + m.group("name").stripTrailing()
                + "§r §7(" + kind(m.group("kind")) + ")";
        }
        return null;
    }

    private static String lastCodes(String gap) {
        Matcher codes = TRAILING_CODES.matcher(gap.stripTrailing());
        return codes.find() ? codes.group() : "";
    }

    private static String kind(String word) {
        return switch (word.toLowerCase(Locale.ROOT)) {
            case "збільшено", "збільшення" -> "збільшення";
            case "зменшено", "зменшення" -> "зменшення";
            case "більше" -> "більше, множник";
            default -> "менше, множник";
        };
    }
}
