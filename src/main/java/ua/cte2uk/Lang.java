package ua.cte2uk;

import net.minecraft.locale.Language;
import ua.cte2uk.tooltip.LiteralTranslator;

/**
 * Доступ до перекладів. Повертає текст лише тоді, коли вибрана українська і ключ існує.
 */
public final class Lang {
    /** Спільний перекладач англійських літералів (ключі {@code cte2_uk.literal.*}). */
    public static final LiteralTranslator LITERALS = new LiteralTranslator(Lang::raw);

    private Lang() {
    }

    /** Текст за ключем або {@code null}, якщо мова не українська чи ключа немає. */
    public static String get(String key) {
        return Ukrainian.active() ? raw(key) : null;
    }

    /** Чи є ключ (і вибрана українська). */
    public static boolean has(String key) {
        return get(key) != null;
    }

    private static String raw(String key) {
        Language language = Language.getInstance();
        return language.has(key) ? language.getOrDefault(key) : null;
    }
}
