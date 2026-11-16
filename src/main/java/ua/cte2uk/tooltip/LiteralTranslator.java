package ua.cte2uk.tooltip;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;

/**
 * Заміна англійських рядків, які моди пишуть у підказках без lang-ключів.
 *
 * <p>Переклад шукається за ключем {@code cte2_uk.literal.<мод>.<текст>}, а потім {@code cte2_uk.literal.*.<текст>},
 * де текст — точний англійський рядок без кольорових кодів і крайніх пробілів.
 * Спершу пробуємо замінити рядок цілком; якщо не вийшло, то окремі літерали, з яких він складений
 * (наприклад «Inverted: » + «Yes»). Кольорові коди й пробіли на краях та стиль рядка зберігаються.
 */
public final class LiteralTranslator {
    public static final String PREFIX = "cte2_uk.literal.";
    private static final Pattern CODES = Pattern.compile("§[0-9a-fk-or]", Pattern.CASE_INSENSITIVE);
    private static final Pattern EDGES = Pattern.compile("^((?:§[0-9a-fk-or]|\\s)*)(.*?)(\\s*)$",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final Function<String, String> lookup;

    /** @param lookup повертає переклад за ключем або {@code null} */
    public LiteralTranslator(Function<String, String> lookup) {
        this.lookup = lookup;
    }

    /** Перекладає всі рядки списку на місці. */
    public <T extends Component> void translateAll(List<T> lines, String namespace,
                                                   java.util.function.Function<MutableComponent, T> cast) {
        for (int i = 0; i < lines.size(); i++) {
            MutableComponent translated = translate(lines.get(i), namespace);
            if (translated != null) {
                lines.set(i, cast.apply(translated));
            }
        }
    }

    /** @return перекладений рядок або {@code null}, якщо нічого не змінилось */
    public MutableComponent translate(Component line, String namespace) {
        String whole = translateText(line.getString(), namespace);
        if (whole != null) {
            return Component.literal(whole).setStyle(line.getStyle());
        }
        return translateTree(line, namespace);
    }

    private MutableComponent translateTree(Component node, String namespace) {
        String text = node.getContents() instanceof LiteralContents literal
            ? translateText(literal.text(), namespace)
            : null;

        List<Component> siblings = node.getSiblings();
        Component[] newSiblings = new Component[siblings.size()];
        boolean changed = text != null;
        for (int i = 0; i < siblings.size(); i++) {
            MutableComponent sibling = translateTree(siblings.get(i), namespace);
            changed |= sibling != null;
            newSiblings[i] = sibling != null ? sibling : siblings.get(i);
        }
        if (!changed) {
            return null;
        }

        MutableComponent copy = text != null
            ? Component.literal(text)
            : MutableComponent.create(node.getContents());
        copy.setStyle(node.getStyle());
        for (Component sibling : newSiblings) {
            copy.append(sibling);
        }
        return copy;
    }

    /** @return текст із перекладом або {@code null} */
    String translateText(String raw, String namespace) {
        Matcher edges = EDGES.matcher(raw);
        if (!edges.matches()) {
            return null;
        }
        String key = CODES.matcher(edges.group(2)).replaceAll("").trim();
        if (key.isEmpty()) {
            return null;
        }
        String translated = lookup.apply(PREFIX + namespace + "." + key);
        if (translated == null) {
            translated = lookup.apply(PREFIX + "*." + key);
        }
        return translated == null ? null : edges.group(1) + translated + edges.group(3);
    }
}
